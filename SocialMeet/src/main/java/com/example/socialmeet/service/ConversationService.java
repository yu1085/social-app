package com.example.socialmeet.service;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.ConversationDTO;
import com.example.socialmeet.entity.ConversationEntity;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.ConversationRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 会话服务层
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class ConversationService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取会话列表
     */
    public ApiResponse<Page<ConversationDTO>> getConversations(int page, int size, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<ConversationEntity> conversations = conversationRepository.findConversationsByUserId(userId, pageable);
            
            Page<ConversationDTO> conversationDTOs = conversations.map(this::convertToDTO);
            
            return ApiResponse.success(conversationDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取会话列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取置顶会话
     */
    public ApiResponse<Page<ConversationDTO>> getPinnedConversations(int page, int size, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<ConversationEntity> conversations = conversationRepository.findConversationsByUserId(userId, pageable);
            
            // 过滤置顶会话
            Page<ConversationEntity> pinnedConversations = conversations.map(conv -> {
                if (conv.isPinnedForUser(userId)) {
                    return conv;
                }
                return null;
            });
            
            Page<ConversationDTO> conversationDTOs = pinnedConversations.map(this::convertToDTO);
            
            return ApiResponse.success(conversationDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取置顶会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取有未读消息的会话
     */
    public ApiResponse<Page<ConversationDTO>> getUnreadConversations(int page, int size, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<ConversationEntity> conversations = conversationRepository.findConversationsByUserId(userId, pageable);
            
            // 过滤有未读消息的会话
            Page<ConversationEntity> unreadConversations = conversations.map(conv -> {
                if (conv.getUnreadCountForUser(userId) > 0) {
                    return conv;
                }
                return null;
            });
            
            Page<ConversationDTO> conversationDTOs = unreadConversations.map(this::convertToDTO);
            
            return ApiResponse.success(conversationDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取未读会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索会话
     */
    public ApiResponse<Page<ConversationDTO>> searchConversations(String keyword, int page, int size, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<ConversationEntity> conversations = conversationRepository.searchConversations(userId, keyword, pageable);
            
            Page<ConversationDTO> conversationDTOs = conversations.map(this::convertToDTO);
            
            return ApiResponse.success(conversationDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("搜索会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置会话置顶状态
     */
    @Transactional
    public ApiResponse<String> setPinnedStatus(Long conversationId, Boolean pinned, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            // 验证会话权限
            Optional<ConversationEntity> conversationOpt = conversationRepository.findById(conversationId);
            if (!conversationOpt.isPresent()) {
                return ApiResponse.error("会话不存在");
            }
            
            ConversationEntity conversation = conversationOpt.get();
            if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
                return ApiResponse.error("无权限操作此会话");
            }
            
            int updatedCount = conversationRepository.setPinnedStatus(conversationId, userId, pinned);
            
            if (updatedCount > 0) {
                return ApiResponse.success(pinned ? "会话已置顶" : "会话已取消置顶");
            } else {
                return ApiResponse.error("操作失败");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("设置置顶状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置会话静音状态
     */
    @Transactional
    public ApiResponse<String> setMutedStatus(Long conversationId, Boolean muted, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            // 验证会话权限
            Optional<ConversationEntity> conversationOpt = conversationRepository.findById(conversationId);
            if (!conversationOpt.isPresent()) {
                return ApiResponse.error("会话不存在");
            }
            
            ConversationEntity conversation = conversationOpt.get();
            if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
                return ApiResponse.error("无权限操作此会话");
            }
            
            int updatedCount = conversationRepository.setMutedStatus(conversationId, userId, muted);
            
            if (updatedCount > 0) {
                return ApiResponse.success(muted ? "会话已静音" : "会话已取消静音");
            } else {
                return ApiResponse.error("操作失败");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("设置静音状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除会话
     */
    @Transactional
    public ApiResponse<String> deleteConversation(Long conversationId, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            // 验证会话权限
            Optional<ConversationEntity> conversationOpt = conversationRepository.findById(conversationId);
            if (!conversationOpt.isPresent()) {
                return ApiResponse.error("会话不存在");
            }
            
            ConversationEntity conversation = conversationOpt.get();
            if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
                return ApiResponse.error("无权限操作此会话");
            }
            
            int updatedCount = conversationRepository.deleteConversationForUser(conversationId, userId);
            
            if (updatedCount > 0) {
                return ApiResponse.success("会话已删除");
            } else {
                return ApiResponse.error("删除失败");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("删除会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取总未读消息数
     */
    public ApiResponse<Long> getTotalUnreadCount(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Long totalUnreadCount = conversationRepository.getTotalUnreadCountByUserId(userId);
            
            return ApiResponse.success(totalUnreadCount);
            
        } catch (Exception e) {
            return ApiResponse.error("获取未读消息数失败: " + e.getMessage());
        }
    }
    
    /**
     * 转换为DTO
     */
    private ConversationDTO convertToDTO(ConversationEntity conversation) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setUser1Id(conversation.getUser1Id());
        dto.setUser2Id(conversation.getUser2Id());
        dto.setLastMessageId(conversation.getLastMessageId());
        dto.setLastMessageContent(conversation.getLastMessageContent());
        dto.setLastMessageTime(conversation.getLastMessageTime());
        dto.setUnreadCountUser1(conversation.getUnreadCountUser1());
        dto.setUnreadCountUser2(conversation.getUnreadCountUser2());
        dto.setIsPinnedUser1(conversation.getIsPinnedUser1());
        dto.setIsPinnedUser2(conversation.getIsPinnedUser2());
        dto.setIsMutedUser1(conversation.getIsMutedUser1());
        dto.setIsMutedUser2(conversation.getIsMutedUser2());
        dto.setIsDeletedUser1(conversation.getIsDeletedUser1());
        dto.setIsDeletedUser2(conversation.getIsDeletedUser2());
        dto.setConversationType(conversation.getConversationType());
        dto.setConversationName(conversation.getConversationName());
        dto.setConversationAvatar(conversation.getConversationAvatar());
        dto.setIsActive(conversation.getIsActive());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        
        return dto;
    }
}
