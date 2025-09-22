package com.example.socialmeet.service;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.DynamicDTO;
import com.example.socialmeet.dto.PublishDynamicRequest;
import com.example.socialmeet.entity.Dynamic;
import com.example.socialmeet.entity.DynamicLike;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.exception.DynamicBusinessException;
import com.example.socialmeet.repository.DynamicRepository;
import com.example.socialmeet.repository.DynamicLikeRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 优化后的动态服务
 */
@Service
public class OptimizedDynamicService {
    
    private static final Logger logger = LoggerFactory.getLogger(OptimizedDynamicService.class);
    
    @Autowired
    private DynamicRepository dynamicRepository;
    
    @Autowired
    private DynamicLikeRepository dynamicLikeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 发布动态 - 带缓存失效
     */
    @Transactional
    @CacheEvict(value = {"dynamics", "dynamicLists"}, allEntries = true)
    public ApiResponse<DynamicDTO> publishDynamic(PublishDynamicRequest request, String token) {
        logger.info("开始发布动态，用户token: {}", token != null ? "已提供" : "未提供");
        
        try {
            // 验证请求参数
            validatePublishRequest(request);
            
            // 验证token并获取用户ID
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                logger.warn("用户未登录，无法发布动态");
                throw new DynamicBusinessException("USER_NOT_LOGGED_IN", "用户未登录");
            }
            
            // 检查用户是否存在
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                logger.warn("用户不存在，用户ID: {}", userId);
                throw new DynamicBusinessException("USER_NOT_FOUND", "用户不存在");
            }
            
            // 创建动态实体
            Dynamic dynamic = new Dynamic();
            dynamic.setUserId(userId);
            dynamic.setContent(request.getContent().trim());
            dynamic.setLocation(request.getLocation());
            dynamic.setPublishTime(LocalDateTime.now());
            dynamic.setStatus("PUBLISHED");
            dynamic.setLikeCount(0);
            dynamic.setCommentCount(0);
            dynamic.setViewCount(0);
            dynamic.setIsDeleted(false);
            
            // 处理图片列表
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                // 验证图片数量限制
                if (request.getImages().size() > 9) {
                    throw new DynamicBusinessException("TOO_MANY_IMAGES", "最多只能上传9张图片");
                }
                
                String imagesJson = objectMapper.writeValueAsString(request.getImages());
                dynamic.setImages(imagesJson);
            }
            
            // 保存动态
            Dynamic savedDynamic = dynamicRepository.save(dynamic);
            logger.info("动态发布成功，动态ID: {}, 用户ID: {}", savedDynamic.getId(), userId);
            
            // 异步更新用户统计
            updateUserStatsAsync(userId);
            
            // 转换为DTO
            DynamicDTO dynamicDTO = convertToDTO(savedDynamic);
            
            return ApiResponse.success(dynamicDTO);
            
        } catch (DynamicBusinessException e) {
            logger.warn("动态发布业务异常: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            logger.error("动态发布系统异常: {}", e.getMessage(), e);
            return ApiResponse.error("发布动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证发布动态请求参数
     */
    private void validatePublishRequest(PublishDynamicRequest request) {
        if (request == null) {
            throw new DynamicBusinessException("INVALID_REQUEST", "请求参数不能为空");
        }
        
        if (!StringUtils.hasText(request.getContent())) {
            throw new DynamicBusinessException("EMPTY_CONTENT", "动态内容不能为空");
        }
        
        if (request.getContent().length() > 2000) {
            throw new DynamicBusinessException("CONTENT_TOO_LONG", "动态内容不能超过2000个字符");
        }
        
        if (StringUtils.hasText(request.getLocation()) && request.getLocation().length() > 100) {
            throw new DynamicBusinessException("LOCATION_TOO_LONG", "位置信息不能超过100个字符");
        }
    }
    
    /**
     * 获取动态列表 - 带缓存
     */
    @Cacheable(value = "dynamicLists", key = "#type + '_' + #page + '_' + #size")
    public ApiResponse<Page<DynamicDTO>> getDynamics(String type, int page, int size, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 创建分页对象
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishTime"));
            
            // 根据类型获取动态
            Page<Dynamic> dynamics;
            switch (type.toLowerCase()) {
                case "latest":
                    dynamics = dynamicRepository.findByStatusAndIsDeletedOrderByPublishTimeDesc("PUBLISHED", false, pageable);
                    break;
                case "hot":
                    dynamics = dynamicRepository.findByStatusAndIsDeletedOrderByLikeCountDescPublishTimeDesc("PUBLISHED", false, pageable);
                    break;
                case "nearby":
                    // 这里可以根据用户位置筛选附近的动态
                    dynamics = dynamicRepository.findByStatusAndIsDeletedOrderByPublishTimeDesc("PUBLISHED", false, pageable);
                    break;
                default:
                    dynamics = dynamicRepository.findByStatusAndIsDeletedOrderByPublishTimeDesc("PUBLISHED", false, pageable);
            }
            
            // 转换为DTO
            Page<DynamicDTO> dynamicDTOs = dynamics.map(this::convertToDTO);
            
            return ApiResponse.success(dynamicDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取动态列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 点赞/取消点赞动态 - 带缓存失效
     */
    @Transactional
    @CacheEvict(value = {"dynamics", "dynamicLists"}, allEntries = true)
    public ApiResponse<String> likeDynamic(Long dynamicId, String token) {
        try {
            // 验证token并获取用户ID
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 检查动态是否存在
            Optional<Dynamic> dynamicOpt = dynamicRepository.findById(dynamicId);
            if (!dynamicOpt.isPresent()) {
                return ApiResponse.error("动态不存在");
            }
            
            Dynamic dynamic = dynamicOpt.get();
            
            // 检查是否已经点赞
            Optional<DynamicLike> existingLike = dynamicLikeRepository.findByDynamicIdAndUserId(dynamicId, userId);
            
            if (existingLike.isPresent()) {
                // 取消点赞
                dynamicLikeRepository.delete(existingLike.get());
                dynamic.setLikeCount(Math.max(0, dynamic.getLikeCount() - 1));
                dynamicRepository.save(dynamic);
                return ApiResponse.success("取消点赞成功");
            } else {
                // 添加点赞
                DynamicLike like = new DynamicLike();
                like.setDynamicId(dynamicId);
                like.setUserId(userId);
                like.setCreatedAt(LocalDateTime.now());
                dynamicLikeRepository.save(like);
                
                dynamic.setLikeCount(dynamic.getLikeCount() + 1);
                dynamicRepository.save(dynamic);
                return ApiResponse.success("点赞成功");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取动态详情 - 带缓存
     */
    @Cacheable(value = "dynamics", key = "#dynamicId")
    public ApiResponse<DynamicDTO> getDynamicById(Long dynamicId) {
        try {
            Optional<Dynamic> dynamicOpt = dynamicRepository.findById(dynamicId);
            if (!dynamicOpt.isPresent()) {
                return ApiResponse.error("动态不存在");
            }
            
            Dynamic dynamic = dynamicOpt.get();
            
            // 增加浏览次数
            dynamic.setViewCount(dynamic.getViewCount() + 1);
            dynamicRepository.save(dynamic);
            
            DynamicDTO dynamicDTO = convertToDTO(dynamic);
            return ApiResponse.success(dynamicDTO);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取动态详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除动态 - 带缓存失效
     */
    @Transactional
    @CacheEvict(value = {"dynamics", "dynamicLists"}, allEntries = true)
    public ApiResponse<String> deleteDynamic(Long dynamicId, String token) {
        try {
            // 验证token并获取用户ID
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 检查动态是否存在且属于当前用户
            Optional<Dynamic> dynamicOpt = dynamicRepository.findById(dynamicId);
            if (!dynamicOpt.isPresent()) {
                return ApiResponse.error("动态不存在");
            }
            
            Dynamic dynamic = dynamicOpt.get();
            if (!dynamic.getUserId().equals(userId)) {
                return ApiResponse.error("无权限删除此动态");
            }
            
            // 软删除
            dynamic.setIsDeleted(true);
            dynamic.setStatus("DELETED");
            dynamicRepository.save(dynamic);
            
            return ApiResponse.success("删除成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索动态
     */
    @Cacheable(value = "dynamicSearch", key = "#keyword + '_' + #page + '_' + #size")
    public ApiResponse<Page<DynamicDTO>> searchDynamics(String keyword, int page, int size, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 创建分页对象
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishTime"));
            
            // 搜索动态
            Page<Dynamic> dynamics = dynamicRepository.findByContentContainingAndStatusAndIsDeleted(
                keyword, "PUBLISHED", false, pageable);
            
            // 转换为DTO
            Page<DynamicDTO> dynamicDTOs = dynamics.map(this::convertToDTO);
            
            return ApiResponse.success(dynamicDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("搜索动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的动态列表
     */
    @Cacheable(value = "userDynamics", key = "#targetUserId + '_' + #page + '_' + #size")
    public ApiResponse<Page<DynamicDTO>> getUserDynamics(Long targetUserId, int page, int size, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 创建分页对象
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishTime"));
            
            // 获取用户动态
            Page<Dynamic> dynamics = dynamicRepository.findByUserIdAndStatusAndIsDeleted(
                targetUserId, "PUBLISHED", false, pageable);
            
            // 转换为DTO
            Page<DynamicDTO> dynamicDTOs = dynamics.map(this::convertToDTO);
            
            return ApiResponse.success(dynamicDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取用户动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门动态
     */
    @Cacheable(value = "hotDynamics", key = "#page + '_' + #size")
    public ApiResponse<Page<DynamicDTO>> getHotDynamics(int page, int size) {
        try {
            // 创建分页对象，按点赞数排序
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "likeCount", "publishTime"));
            
            // 获取热门动态
            Page<Dynamic> dynamics = dynamicRepository.findByStatusAndIsDeletedOrderByLikeCountDescPublishTimeDesc(
                "PUBLISHED", false, pageable);
            
            // 转换为DTO
            Page<DynamicDTO> dynamicDTOs = dynamics.map(this::convertToDTO);
            
            return ApiResponse.success(dynamicDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取热门动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取附近动态
     */
    @Cacheable(value = "nearbyDynamics", key = "#latitude + '_' + #longitude + '_' + #page + '_' + #size")
    public ApiResponse<Page<DynamicDTO>> getNearbyDynamics(Double latitude, Double longitude, int page, int size, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 创建分页对象
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishTime"));
            
            // 这里可以根据地理位置筛选附近的动态
            // 目前先返回所有动态，后续可以集成地理位置服务
            Page<Dynamic> dynamics = dynamicRepository.findByStatusAndIsDeletedOrderByPublishTimeDesc(
                "PUBLISHED", false, pageable);
            
            // 转换为DTO
            Page<DynamicDTO> dynamicDTOs = dynamics.map(this::convertToDTO);
            
            return ApiResponse.success(dynamicDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取附近动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 异步更新用户统计
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> updateUserStatsAsync(Long userId) {
        try {
            // 更新用户动态数量等统计信息
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                // User user = userOpt.get(); // 暂时不使用user变量
                
                // 统计用户动态数量
                long dynamicCount = dynamicRepository.countByUserIdAndIsDeleted(userId, false);
                
                // 这里可以更新用户的动态统计信息
                // user.setDynamicCount((int) dynamicCount);
                // userRepository.save(user);
                
                logger.debug("用户 {} 的动态数量: {}", userId, dynamicCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * 从token获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 转换为DTO
     */
    private DynamicDTO convertToDTO(Dynamic dynamic) {
        DynamicDTO dto = new DynamicDTO();
        dto.setId(dynamic.getId());
        dto.setUserId(dynamic.getUserId());
        dto.setContent(dynamic.getContent());
        dto.setLocation(dynamic.getLocation());
        dto.setLikeCount(dynamic.getLikeCount());
        dto.setCommentCount(dynamic.getCommentCount());
        dto.setViewCount(dynamic.getViewCount());
        dto.setPublishTime(dynamic.getPublishTime());
        dto.setStatus(dynamic.getStatus());
        
        // 处理图片
        if (dynamic.getImages() != null && !dynamic.getImages().isEmpty()) {
            try {
                List<String> images = objectMapper.readValue(dynamic.getImages(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
                dto.setImages(images);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // 获取用户信息
        Optional<User> userOpt = userRepository.findById(dynamic.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            dto.setUserNickname(user.getNickname());
            // dto.setUserAvatar(user.getAvatar()); // 如果User实体没有avatar字段，注释掉
        }
        
        return dto;
    }
}
