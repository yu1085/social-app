package com.example.socialmeet.service;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.DynamicDTO;
import com.example.socialmeet.dto.UserDTO;
import com.example.socialmeet.dto.PublishDynamicRequest;
import com.example.socialmeet.entity.Dynamic;
import com.example.socialmeet.entity.DynamicLike;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.DynamicLikeRepository;
import com.example.socialmeet.repository.DynamicRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 动态服务
 */
@Service
public class DynamicService {
    
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
     * 发布动态
     */
    @Transactional
    public ApiResponse<DynamicDTO> publishDynamic(PublishDynamicRequest request, String token) {
        try {
            // 验证token并获取用户ID
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            // 创建动态实体
            Dynamic dynamic = new Dynamic();
            dynamic.setUserId(userId);
            dynamic.setContent(request.getContent());
            dynamic.setLocation(request.getLocation());
            dynamic.setPublishTime(LocalDateTime.now());
            dynamic.setStatus("PUBLISHED");
            
            // 处理图片列表
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                String imagesJson = objectMapper.writeValueAsString(request.getImages());
                dynamic.setImages(imagesJson);
            }
            
            // 保存动态
            Dynamic savedDynamic = dynamicRepository.save(dynamic);
            
            // 转换为DTO
            DynamicDTO dynamicDTO = convertToDTO(savedDynamic);
            
            return ApiResponse.success(dynamicDTO);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("发布动态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取动态列表
     */
    public ApiResponse<Page<DynamicDTO>> getDynamics(String type, int page, int size, String token) {
        try {
            Long userId = getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("用户未登录");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<Dynamic> dynamics;
            
            // 根据类型获取不同的动态列表
            switch (type.toLowerCase()) {
                case "latest":
                    dynamics = dynamicRepository.findByIsDeletedFalseOrderByPublishTimeDesc(pageable);
                    break;
                case "hot":
                    dynamics = dynamicRepository.findHotDynamics(pageable);
                    break;
                case "following":
                    dynamics = dynamicRepository.findFollowingDynamics(userId, pageable);
                    break;
                case "liked":
                    dynamics = dynamicRepository.findLikedDynamics(userId, pageable);
                    break;
                default:
                    dynamics = dynamicRepository.findByIsDeletedFalseOrderByPublishTimeDesc(pageable);
            }
            
            // 转换为DTO并填充关联数据
            Page<DynamicDTO> dynamicDTOs = dynamics.map(this::convertToDTO);
            
            return ApiResponse.success(dynamicDTOs);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("获取动态列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 点赞/取消点赞动态
     */
    @Transactional
    public ApiResponse<String> likeDynamic(Long dynamicId, String token) {
        try {
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
            
            // 检查是否已点赞
            Optional<DynamicLike> existingLike = dynamicLikeRepository.findByDynamicIdAndUserId(dynamicId, userId);
            
            if (existingLike.isPresent()) {
                // 取消点赞
                dynamicLikeRepository.delete(existingLike.get());
                dynamic.setLikeCount(Math.max(0, dynamic.getLikeCount() - 1));
                dynamicRepository.save(dynamic);
                return ApiResponse.success("取消点赞成功");
            } else {
                // 点赞
                DynamicLike like = new DynamicLike();
                like.setDynamicId(dynamicId);
                like.setUserId(userId);
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
     * 删除动态
     */
    @Transactional
    public ApiResponse<String> deleteDynamic(Long dynamicId, String token) {
        try {
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
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 从token中获取用户ID
     */
    private Long getUserIdFromToken(String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return null;
            }
            
            String actualToken = token.substring(7);
            Long userId = jwtUtil.getUserIdFromToken(actualToken);
            return userId;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 将Dynamic实体转换为DTO
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
        dto.setStatus(dynamic.getStatus());
        dto.setPublishTime(dynamic.getPublishTime());
        dto.setCreatedAt(dynamic.getCreatedAt());
        dto.setUpdatedAt(dynamic.getUpdatedAt());
        
        // 处理图片列表
        if (dynamic.getImages() != null && !dynamic.getImages().isEmpty()) {
            try {
                List<String> images = objectMapper.readValue(dynamic.getImages(), new TypeReference<List<String>>() {});
                dto.setImages(images);
            } catch (Exception e) {
                dto.setImages(List.of());
            }
        } else {
            dto.setImages(List.of());
        }
        
        // 获取用户信息
        Optional<User> userOpt = userRepository.findById(dynamic.getUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            dto.setUser(new UserDTO(user));
        }
        
        // 设置是否已点赞（这里需要根据当前用户判断）
        dto.setIsLiked(false); // 默认值，实际使用时需要根据当前用户判断
        dto.setIsFreeMinute(false); // 默认值，实际使用时需要根据业务逻辑判断
        
        return dto;
    }
}
