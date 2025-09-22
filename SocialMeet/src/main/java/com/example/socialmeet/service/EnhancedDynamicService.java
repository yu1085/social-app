package com.example.socialmeet.service;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.DynamicDTO;
import com.example.socialmeet.dto.DynamicFilterRequest;
import com.example.socialmeet.dto.PublishDynamicRequest;
import com.example.socialmeet.entity.Dynamic;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.EnhancedDynamicRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 增强版动态服务
 * 提供完整的广场动态功能
 */
@Service
public class EnhancedDynamicService {
    
    @Autowired
    private EnhancedDynamicRepository dynamicRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String UPLOAD_DIR = "uploads/dynamics/";
    
    /**
     * 发布动态
     */
    @Transactional
    @CacheEvict(value = {"dynamics", "dynamicLists", "trendingTopics"}, allEntries = true)
    public ApiResponse<DynamicDTO> publishDynamic(PublishDynamicRequest request, String token) {
        try {
            // 验证token并获取用户ID
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 检查用户是否存在
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            
            // 创建动态实体
            Dynamic dynamic = new Dynamic();
            dynamic.setUserId(userId);
            dynamic.setContent(request.getContent());
            dynamic.setLocation(request.getLocation());
            dynamic.setPublishTime(LocalDateTime.now());
            dynamic.setStatus("PUBLISHED");
            dynamic.setLikeCount(0);
            dynamic.setCommentCount(0);
            dynamic.setViewCount(0);
            dynamic.setIsDeleted(false);
            
            // 处理图片列表
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                String imagesJson = objectMapper.writeValueAsString(request.getImages());
                dynamic.setImages(imagesJson);
            }
            
            // 保存动态
            Dynamic savedDynamic = dynamicRepository.save(dynamic);
            
            // 异步更新用户统计
            updateUserStatsAsync(userId);
            
            // 转换为DTO
            DynamicDTO dynamicDTO = convertToDTO(savedDynamic, user);
            
            return ApiResponse.success(dynamicDTO);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("发布动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传动态图片
     */
    public ApiResponse<List<String>> uploadImages(List<MultipartFile> images, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 创建上传目录
            Path uploadPath = Paths.get(UPLOAD_DIR + userId);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            List<String> imageUrls = new ArrayList<>();
            
            for (MultipartFile image : images) {
                if (image.isEmpty()) {
                    continue;
                }
                
                // 生成唯一文件名
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                
                // 保存文件
                Files.copy(image.getInputStream(), filePath);
                
                // 生成访问URL
                String imageUrl = "/uploads/dynamics/" + userId + "/" + fileName;
                imageUrls.add(imageUrl);
            }
            
            return ApiResponse.success(imageUrls);
            
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.error("上传图片失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("上传图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取动态列表 - 支持多种筛选条件
     */
    @Cacheable(value = "dynamicLists", key = "#filterRequest.toString()")
    public ApiResponse<Page<DynamicDTO>> getDynamics(DynamicFilterRequest filterRequest, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 创建分页对象
            Sort sort = createSort(filterRequest);
            Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
            
            // 根据筛选条件获取动态
            Page<Dynamic> dynamics = dynamicRepository.findDynamicsWithFilters(filterRequest, pageable);
            
            // 转换为DTO
            Page<DynamicDTO> dynamicDTOs = dynamics.map(dynamic -> {
                User user = userRepository.findById(dynamic.getUserId()).orElse(null);
                return convertToDTO(dynamic, user);
            });
            
            return ApiResponse.success(dynamicDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取动态列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取动态详情
     */
    @Cacheable(value = "dynamics", key = "#id")
    public ApiResponse<DynamicDTO> getDynamicById(Long id, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 获取动态
            Dynamic dynamic = dynamicRepository.findById(id).orElse(null);
            if (dynamic == null || dynamic.getIsDeleted()) {
                return ApiResponse.error("动态不存在");
            }
            
            // 增加浏览量
            dynamic.setViewCount(dynamic.getViewCount() + 1);
            dynamicRepository.save(dynamic);
            
            // 获取用户信息
            User user = userRepository.findById(dynamic.getUserId()).orElse(null);
            
            // 转换为DTO
            DynamicDTO dynamicDTO = convertToDTO(dynamic, user);
            
            return ApiResponse.success(dynamicDTO);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取动态详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 点赞/取消点赞动态
     */
    @Transactional
    @CacheEvict(value = {"dynamics", "dynamicLists"}, allEntries = true)
    public ApiResponse<String> likeDynamic(Long id, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 获取动态
            Dynamic dynamic = dynamicRepository.findById(id).orElse(null);
            if (dynamic == null || dynamic.getIsDeleted()) {
                return ApiResponse.error("动态不存在");
            }
            
            // 检查是否已点赞
            boolean isLiked = dynamicRepository.isUserLikedDynamic(id, userId);
            
            if (isLiked) {
                // 取消点赞
                dynamicRepository.removeLike(id, userId);
                dynamic.setLikeCount(Math.max(0, dynamic.getLikeCount() - 1));
                dynamicRepository.save(dynamic);
                return ApiResponse.success("取消点赞成功");
            } else {
                // 添加点赞
                dynamicRepository.addLike(id, userId);
                dynamic.setLikeCount(dynamic.getLikeCount() + 1);
                dynamicRepository.save(dynamic);
                return ApiResponse.success("点赞成功");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("点赞操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 评论动态
     */
    @Transactional
    @CacheEvict(value = {"dynamics", "dynamicLists"}, allEntries = true)
    public ApiResponse<String> commentDynamic(Long id, String content, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 获取动态
            Dynamic dynamic = dynamicRepository.findById(id).orElse(null);
            if (dynamic == null || dynamic.getIsDeleted()) {
                return ApiResponse.error("动态不存在");
            }
            
            // 添加评论
            dynamicRepository.addComment(id, userId, content);
            
            // 更新评论数
            dynamic.setCommentCount(dynamic.getCommentCount() + 1);
            dynamicRepository.save(dynamic);
            
            return ApiResponse.success("评论成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("评论失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取动态评论列表
     */
    public ApiResponse<Page<Object>> getDynamicComments(Long id, int page, int size, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 获取评论列表
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Object> comments = dynamicRepository.getDynamicComments(id, pageable);
            
            return ApiResponse.success(comments);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取评论失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除动态
     */
    @Transactional
    @CacheEvict(value = {"dynamics", "dynamicLists"}, allEntries = true)
    public ApiResponse<String> deleteDynamic(Long id, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 获取动态
            Dynamic dynamic = dynamicRepository.findById(id).orElse(null);
            if (dynamic == null) {
                return ApiResponse.error("动态不存在");
            }
            
            // 检查权限
            if (!dynamic.getUserId().equals(userId)) {
                return ApiResponse.error("无权限删除此动态");
            }
            
            // 软删除
            dynamic.setIsDeleted(true);
            dynamicRepository.save(dynamic);
            
            return ApiResponse.success("删除成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("删除动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 举报动态
     */
    @Transactional
    public ApiResponse<String> reportDynamic(Long id, String reason, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 获取动态
            Dynamic dynamic = dynamicRepository.findById(id).orElse(null);
            if (dynamic == null || dynamic.getIsDeleted()) {
                return ApiResponse.error("动态不存在");
            }
            
            // 添加举报记录
            dynamicRepository.addReport(id, userId, reason);
            
            return ApiResponse.success("举报成功");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("举报失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户动态列表
     */
    public ApiResponse<Page<DynamicDTO>> getUserDynamics(Long userId, int page, int size, String token) {
        try {
            // 验证token
            Long currentUserId = getUserIdFromToken(token);
            if (currentUserId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 创建分页对象
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishTime"));
            
            // 获取用户动态
            Page<Dynamic> dynamics = dynamicRepository.findByUserIdAndIsDeletedOrderByPublishTimeDesc(userId, false, pageable);
            
            // 转换为DTO
            Page<DynamicDTO> dynamicDTOs = dynamics.map(dynamic -> {
                User user = userRepository.findById(dynamic.getUserId()).orElse(null);
                return convertToDTO(dynamic, user);
            });
            
            return ApiResponse.success(dynamicDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取用户动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门话题
     */
    @Cacheable(value = "trendingTopics", key = "#limit")
    public ApiResponse<List<String>> getTrendingTopics(int limit, String token) {
        try {
            // 验证token
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 获取热门话题
            List<String> topics = dynamicRepository.getTrendingTopics(limit);
            
            return ApiResponse.success(topics);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取热门话题失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索动态
     */
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
            Page<Dynamic> dynamics = dynamicRepository.searchDynamics(keyword, pageable);
            
            // 转换为DTO
            Page<DynamicDTO> dynamicDTOs = dynamics.map(dynamic -> {
                User user = userRepository.findById(dynamic.getUserId()).orElse(null);
                return convertToDTO(dynamic, user);
            });
            
            return ApiResponse.success(dynamicDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("搜索动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 异步更新用户统计
     */
    private void updateUserStatsAsync(Long userId) {
        CompletableFuture.runAsync(() -> {
            try {
                // 更新用户动态数
                long dynamicCount = dynamicRepository.countByUserIdAndIsDeleted(userId, false);
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    // 这里可以更新用户的动态统计字段
                    userRepository.save(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * 创建排序对象
     */
    private Sort createSort(DynamicFilterRequest filterRequest) {
        String sortBy = filterRequest.getSortBy() != null ? filterRequest.getSortBy() : "publishTime";
        String sortDirection = filterRequest.getSortDirection() != null ? filterRequest.getSortDirection() : "DESC";
        
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? 
            Sort.Direction.ASC : Sort.Direction.DESC;
        
        return Sort.by(direction, sortBy);
    }
    
    /**
     * 转换为DTO
     */
    private DynamicDTO convertToDTO(Dynamic dynamic, User user) {
        DynamicDTO dto = new DynamicDTO();
        dto.setId(dynamic.getId());
        dto.setUserId(dynamic.getUserId());
        dto.setContent(dynamic.getContent());
        dto.setLocation(dynamic.getLocation());
        dto.setPublishTime(dynamic.getPublishTime());
        dto.setLikeCount(dynamic.getLikeCount());
        dto.setCommentCount(dynamic.getCommentCount());
        dto.setViewCount(dynamic.getViewCount());
        dto.setStatus(dynamic.getStatus());
        
        // 设置用户信息
        if (user != null) {
            dto.setUserNickname(user.getNickname());
            dto.setUserAvatar(user.getAvatarUrl());
        }
        
        // 处理图片列表
        if (dynamic.getImages() != null && !dynamic.getImages().isEmpty()) {
            try {
                List<String> images = objectMapper.readValue(dynamic.getImages(), new TypeReference<List<String>>() {});
                dto.setImages(images);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return dto;
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
}
