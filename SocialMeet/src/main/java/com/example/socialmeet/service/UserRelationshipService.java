package com.example.socialmeet.service;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.UserRelationshipDTO;
import com.example.socialmeet.dto.CreateRelationshipRequest;
import com.example.socialmeet.entity.UserRelationshipEntity;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.UserRelationshipRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户关系服务层
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class UserRelationshipService {
    
    @Autowired
    private UserRelationshipRepository userRelationshipRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 创建关系
     */
    @Transactional
    public ApiResponse<UserRelationshipDTO> createRelationship(CreateRelationshipRequest request, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            // 验证目标用户是否存在
            Optional<User> targetUserOpt = userRepository.findById(request.getTargetUserId());
            if (!targetUserOpt.isPresent()) {
                return ApiResponse.error("目标用户不存在");
            }
            
            // 检查是否已存在关系
            Optional<UserRelationshipEntity> existingRelationship = userRelationshipRepository
                    .findRelationshipBetweenUsers(userId, request.getTargetUserId(), request.getRelationshipType());
            
            if (existingRelationship.isPresent()) {
                return ApiResponse.error("关系已存在");
            }
            
            // 创建关系
            UserRelationshipEntity relationship = new UserRelationshipEntity();
            relationship.setUser1Id(userId);
            relationship.setUser2Id(request.getTargetUserId());
            relationship.setRelationshipType(request.getRelationshipType());
            relationship.setInitiatedBy(userId);
            relationship.setStatus("ACTIVE");
            relationship.setNotes(request.getNotes());
            relationship.setTags(request.getTags());
            
            // 检查是否相互关注
            if (request.getRelationshipType() == UserRelationshipEntity.RelationshipType.FOLLOW) {
                Optional<UserRelationshipEntity> mutualRelationship = userRelationshipRepository
                        .findRelationshipBetweenUsers(request.getTargetUserId(), userId, UserRelationshipEntity.RelationshipType.FOLLOW);
                if (mutualRelationship.isPresent()) {
                    relationship.setIsMutual(true);
                    mutualRelationship.get().setIsMutual(true);
                    userRelationshipRepository.save(mutualRelationship.get());
                }
            }
            
            relationship = userRelationshipRepository.save(relationship);
            
            return ApiResponse.success(convertToDTO(relationship));
            
        } catch (Exception e) {
            return ApiResponse.error("创建关系失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户关系列表
     */
    public ApiResponse<Page<UserRelationshipDTO>> getRelationships(int page, int size, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<UserRelationshipEntity> relationships = userRelationshipRepository.findRelationshipsByUserId(userId, pageable);
            
            Page<UserRelationshipDTO> relationshipDTOs = relationships.map(this::convertToDTO);
            
            return ApiResponse.success(relationshipDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取关系列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取知友列表
     */
    public ApiResponse<List<UserRelationshipDTO>> getFriends(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            List<UserRelationshipEntity> friends = userRelationshipRepository.findFriendsByUserId(userId);
            List<UserRelationshipDTO> friendDTOs = friends.stream()
                    .map(this::convertToDTO)
                    .collect(java.util.stream.Collectors.toList());
            
            return ApiResponse.success(friendDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取知友列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取喜欢列表
     */
    public ApiResponse<List<UserRelationshipDTO>> getLikes(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            List<UserRelationshipEntity> likes = userRelationshipRepository.findLikesByUserId(userId);
            List<UserRelationshipDTO> likeDTOs = likes.stream()
                    .map(this::convertToDTO)
                    .collect(java.util.stream.Collectors.toList());
            
            return ApiResponse.success(likeDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取喜欢列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取亲密关系列表
     */
    public ApiResponse<List<UserRelationshipDTO>> getIntimateRelationships(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            List<UserRelationshipEntity> intimateRelationships = userRelationshipRepository.findIntimateRelationshipsByUserId(userId);
            List<UserRelationshipDTO> intimateDTOs = intimateRelationships.stream()
                    .map(this::convertToDTO)
                    .collect(java.util.stream.Collectors.toList());
            
            return ApiResponse.success(intimateDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取亲密关系列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取高亲密度关系
     */
    public ApiResponse<List<UserRelationshipDTO>> getHighIntimacyRelationships(Integer minIntimacyScore, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            List<UserRelationshipEntity> highIntimacyRelationships = userRelationshipRepository
                    .findHighIntimacyRelationships(userId, minIntimacyScore);
            List<UserRelationshipDTO> highIntimacyDTOs = highIntimacyRelationships.stream()
                    .map(this::convertToDTO)
                    .collect(java.util.stream.Collectors.toList());
            
            return ApiResponse.success(highIntimacyDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取高亲密度关系失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最近聊天的关系
     */
    public ApiResponse<Page<UserRelationshipDTO>> getRecentChatRelationships(int page, int size, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<UserRelationshipEntity> recentChatRelationships = userRelationshipRepository
                    .findRecentChatRelationships(userId, pageable);
            
            Page<UserRelationshipDTO> recentChatDTOs = recentChatRelationships.map(this::convertToDTO);
            
            return ApiResponse.success(recentChatDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取最近聊天关系失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新关系备注
     */
    @Transactional
    public ApiResponse<String> updateRelationshipNotes(Long relationshipId, String notes, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Optional<UserRelationshipEntity> relationshipOpt = userRelationshipRepository.findById(relationshipId);
            if (!relationshipOpt.isPresent()) {
                return ApiResponse.error("关系不存在");
            }
            
            UserRelationshipEntity relationship = relationshipOpt.get();
            if (!relationship.getUser1Id().equals(userId) && !relationship.getUser2Id().equals(userId)) {
                return ApiResponse.error("无权限操作此关系");
            }
            
            relationship.setNotes(notes);
            userRelationshipRepository.save(relationship);
            
            return ApiResponse.success("备注已更新");
            
        } catch (Exception e) {
            return ApiResponse.error("更新备注失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新关系标签
     */
    @Transactional
    public ApiResponse<String> updateRelationshipTags(Long relationshipId, String tags, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Optional<UserRelationshipEntity> relationshipOpt = userRelationshipRepository.findById(relationshipId);
            if (!relationshipOpt.isPresent()) {
                return ApiResponse.error("关系不存在");
            }
            
            UserRelationshipEntity relationship = relationshipOpt.get();
            if (!relationship.getUser1Id().equals(userId) && !relationship.getUser2Id().equals(userId)) {
                return ApiResponse.error("无权限操作此关系");
            }
            
            relationship.setTags(tags);
            userRelationshipRepository.save(relationship);
            
            return ApiResponse.success("标签已更新");
            
        } catch (Exception e) {
            return ApiResponse.error("更新标签失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除关系
     */
    @Transactional
    public ApiResponse<String> deleteRelationship(Long relationshipId, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Optional<UserRelationshipEntity> relationshipOpt = userRelationshipRepository.findById(relationshipId);
            if (!relationshipOpt.isPresent()) {
                return ApiResponse.error("关系不存在");
            }
            
            UserRelationshipEntity relationship = relationshipOpt.get();
            if (!relationship.getUser1Id().equals(userId) && !relationship.getUser2Id().equals(userId)) {
                return ApiResponse.error("无权限操作此关系");
            }
            
            userRelationshipRepository.deleteRelationship(relationshipId);
            
            return ApiResponse.success("关系已删除");
            
        } catch (Exception e) {
            return ApiResponse.error("删除关系失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取关系统计信息
     */
    public ApiResponse<Object> getRelationshipStatistics(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Object[] statistics = userRelationshipRepository.getRelationshipStatistics(userId);
            
            return ApiResponse.success(statistics);
            
        } catch (Exception e) {
            return ApiResponse.error("获取关系统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查两个用户是否有指定类型的关系
     */
    public ApiResponse<Boolean> hasRelationship(Long targetUserId, UserRelationshipEntity.RelationshipType relationshipType, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            boolean hasRelationship = userRelationshipRepository.hasRelationship(userId, targetUserId, relationshipType);
            
            return ApiResponse.success(hasRelationship);
            
        } catch (Exception e) {
            return ApiResponse.error("检查关系失败: " + e.getMessage());
        }
    }
    
    /**
     * 转换为DTO
     */
    private UserRelationshipDTO convertToDTO(UserRelationshipEntity relationship) {
        UserRelationshipDTO dto = new UserRelationshipDTO();
        dto.setId(relationship.getId());
        dto.setUser1Id(relationship.getUser1Id());
        dto.setUser2Id(relationship.getUser2Id());
        dto.setRelationshipType(relationship.getRelationshipType());
        dto.setIsMutual(relationship.getIsMutual());
        dto.setInitiatedBy(relationship.getInitiatedBy());
        dto.setStatus(relationship.getStatus());
        dto.setIntimacyScore(relationship.getIntimacyScore());
        dto.setChatFrequency(relationship.getChatFrequency());
        dto.setLastChatTime(relationship.getLastChatTime());
        dto.setLastInteractionTime(relationship.getLastInteractionTime());
        dto.setInteractionCount(relationship.getInteractionCount());
        dto.setLikeCount(relationship.getLikeCount());
        dto.setCommentCount(relationship.getCommentCount());
        dto.setCallCount(relationship.getCallCount());
        dto.setCallDuration(relationship.getCallDuration());
        dto.setGiftCount(relationship.getGiftCount());
        dto.setGiftValue(relationship.getGiftValue());
        dto.setNotes(relationship.getNotes());
        dto.setTags(relationship.getTags());
        dto.setCreatedAt(relationship.getCreatedAt());
        dto.setUpdatedAt(relationship.getUpdatedAt());
        
        return dto;
    }
}
