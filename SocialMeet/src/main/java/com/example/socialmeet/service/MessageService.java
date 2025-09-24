package com.example.socialmeet.service;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.MessageDTO;
import com.example.socialmeet.dto.SendMessageRequest;
import com.example.socialmeet.entity.MessageEntity;
import com.example.socialmeet.entity.ConversationEntity;
import com.example.socialmeet.entity.User;
import com.example.socialmeet.repository.MessageRepository;
import com.example.socialmeet.repository.ConversationRepository;
import com.example.socialmeet.repository.UserRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 消息服务层
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 发送消息
     */
    @Transactional
    public ApiResponse<MessageDTO> sendMessage(SendMessageRequest request, String token) {
        try {
            System.out.println("=== MessageService.sendMessage 开始执行 ===");
            System.out.println("接收到的token: " + token);
            
            // 验证token并获取用户ID
            System.out.println("开始从JWT中提取用户ID...");
            Long senderId = jwtUtil.getUserIdFromToken(token);
            System.out.println("提取到的发送者ID: " + senderId);
            
            if (senderId == null) {
                System.out.println("JWT解析失败，返回错误");
                return ApiResponse.error("无效的认证token");
            }
            
            // 验证接收者是否存在
            System.out.println("开始验证接收者ID: " + request.getReceiverId());
            Optional<User> receiverOpt = userRepository.findById(request.getReceiverId());
            if (!receiverOpt.isPresent()) {
                System.out.println("接收者不存在，ID: " + request.getReceiverId());
                return ApiResponse.error("接收者不存在");
            }
            System.out.println("接收者验证成功: " + receiverOpt.get().getUsername());
            
            // 创建消息实体
            System.out.println("开始创建消息实体...");
            MessageEntity message = new MessageEntity();
            message.setSenderId(senderId);
            message.setReceiverId(request.getReceiverId());
            message.setContent(request.getContent());
            message.setMessageType(request.getMessageType());
            message.setMediaUrl(request.getMediaUrl());
            message.setMediaThumbnail(request.getMediaThumbnail());
            message.setMediaDuration(request.getMediaDuration());
            message.setMediaSize(request.getMediaSize());
            message.setMessageStatus(MessageEntity.MessageStatus.SENDING);
            message.setSendTime(LocalDateTime.now());
            message.setReplyToMessageId(request.getReplyToMessageId());
            message.setForwardFromMessageId(request.getForwardFromMessageId());
            
            System.out.println("消息实体创建完成，开始保存到数据库...");
            // 保存消息
            message = messageRepository.save(message);
            System.out.println("消息保存成功，消息ID: " + message.getId());
            
            // 更新或创建会话
            System.out.println("开始更新或创建会话...");
            updateOrCreateConversation(senderId, request.getReceiverId(), message);
            System.out.println("会话更新完成");
            
            // 转换为DTO
            System.out.println("开始转换为DTO...");
            MessageDTO messageDTO = convertToDTO(message);
            System.out.println("DTO转换完成");
            
            System.out.println("=== MessageService.sendMessage 执行成功 ===");
            return ApiResponse.success(messageDTO);
            
        } catch (Exception e) {
            System.out.println("=== MessageService.sendMessage 执行失败 ===");
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            return ApiResponse.error("发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取消息列表
     */
    public ApiResponse<Page<MessageDTO>> getMessages(Long otherUserId, int page, int size, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<MessageEntity> messages = messageRepository.findMessagesBetweenUsers(userId, otherUserId, pageable);
            
            Page<MessageDTO> messageDTOs = messages.map(this::convertToDTO);
            
            return ApiResponse.success(messageDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取消息列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取会话消息
     */
    public ApiResponse<Page<MessageDTO>> getConversationMessages(Long conversationId, int page, int size, String token) {
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
                return ApiResponse.error("无权限访问此会话");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<MessageEntity> messages = messageRepository.findByConversationId(conversationId, pageable);
            
            Page<MessageDTO> messageDTOs = messages.map(this::convertToDTO);
            
            return ApiResponse.success(messageDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("获取会话消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     */
    @Transactional
    public ApiResponse<String> markMessagesAsRead(Long otherUserId, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            int updatedCount = messageRepository.markMessagesAsReadFromSender(otherUserId, userId, LocalDateTime.now());
            
            // 重置会话未读计数
            Optional<ConversationEntity> conversationOpt = conversationRepository.findConversationBetweenUsers(userId, otherUserId);
            if (conversationOpt.isPresent()) {
                conversationRepository.resetUnreadCount(conversationOpt.get().getId(), userId);
            }
            
            return ApiResponse.success("已标记" + updatedCount + "条消息为已读");
            
        } catch (Exception e) {
            return ApiResponse.error("标记已读失败: " + e.getMessage());
        }
    }
    
    /**
     * 撤回消息
     */
    @Transactional
    public ApiResponse<String> recallMessage(Long messageId, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            // 检查消息是否存在且属于当前用户
            Optional<MessageEntity> messageOpt = messageRepository.findById(messageId);
            if (!messageOpt.isPresent()) {
                return ApiResponse.error("消息不存在");
            }
            
            MessageEntity message = messageOpt.get();
            if (!message.getSenderId().equals(userId)) {
                return ApiResponse.error("只能撤回自己的消息");
            }
            
            // 检查撤回时间限制（2分钟内）
            if (message.getSendTime().isBefore(LocalDateTime.now().minusMinutes(2))) {
                return ApiResponse.error("消息发送超过2分钟，无法撤回");
            }
            
            int updatedCount = messageRepository.recallMessage(messageId, userId, LocalDateTime.now());
            
            if (updatedCount > 0) {
                return ApiResponse.success("消息已撤回");
            } else {
                return ApiResponse.error("撤回消息失败");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("撤回消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除消息
     */
    @Transactional
    public ApiResponse<String> deleteMessage(Long messageId, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            int updatedCount = messageRepository.deleteMessage(messageId, userId);
            
            if (updatedCount > 0) {
                return ApiResponse.success("消息已删除");
            } else {
                return ApiResponse.error("删除消息失败");
            }
            
        } catch (Exception e) {
            return ApiResponse.error("删除消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索消息
     */
    public ApiResponse<Page<MessageDTO>> searchMessages(String keyword, int page, int size, String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<MessageEntity> messages = messageRepository.searchMessagesByContent(userId, keyword, pageable);
            
            Page<MessageDTO> messageDTOs = messages.map(this::convertToDTO);
            
            return ApiResponse.success(messageDTOs);
            
        } catch (Exception e) {
            return ApiResponse.error("搜索消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未读消息数量
     */
    public ApiResponse<Long> getUnreadCount(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return ApiResponse.error("无效的认证token");
            }
            
            Long unreadCount = messageRepository.countUnreadMessagesByUserId(userId);
            
            return ApiResponse.success(unreadCount);
            
        } catch (Exception e) {
            return ApiResponse.error("获取未读消息数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新或创建会话
     */
    private void updateOrCreateConversation(Long user1Id, Long user2Id, MessageEntity message) {
        System.out.println("=== updateOrCreateConversation 开始执行 ===");
        System.out.println("用户1ID: " + user1Id + ", 用户2ID: " + user2Id);
        
        Optional<ConversationEntity> conversationOpt = conversationRepository.findConversationBetweenUsers(user1Id, user2Id);
        
        ConversationEntity conversation;
        if (conversationOpt.isPresent()) {
            System.out.println("找到现有会话，ID: " + conversationOpt.get().getId());
            conversation = conversationOpt.get();
            // 更新最后消息
            System.out.println("开始更新最后消息...");
            conversationRepository.updateLastMessage(
                conversation.getId(),
                message.getId(),
                message.getContent(),
                message.getSendTime()
            );
            System.out.println("最后消息更新完成");
        } else {
            System.out.println("未找到现有会话，创建新会话...");
            // 创建新会话
            conversation = new ConversationEntity();
            conversation.setUser1Id(user1Id);
            conversation.setUser2Id(user2Id);
            conversation.setLastMessageId(message.getId());
            conversation.setLastMessageContent(message.getContent());
            conversation.setLastMessageTime(message.getSendTime());
            conversation.setConversationType(ConversationEntity.ConversationType.PRIVATE);
            conversation = conversationRepository.save(conversation);
            System.out.println("新会话创建完成，ID: " + conversation.getId());
        }
        
        // 增加未读计数
        System.out.println("开始增加未读计数...");
        conversationRepository.incrementUnreadCount(conversation.getId(), user2Id);
        System.out.println("未读计数增加完成");
        
        // 设置消息的会话ID
        System.out.println("开始设置消息的会话ID...");
        message.setConversationId(conversation.getId());
        messageRepository.save(message);
        System.out.println("消息会话ID设置完成");
        
        System.out.println("=== updateOrCreateConversation 执行完成 ===");
    }
    
    /**
     * 转换为DTO
     */
    private MessageDTO convertToDTO(MessageEntity message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setMediaUrl(message.getMediaUrl());
        dto.setMediaThumbnail(message.getMediaThumbnail());
        dto.setMediaDuration(message.getMediaDuration());
        dto.setMediaSize(message.getMediaSize());
        dto.setMessageStatus(message.getMessageStatus());
        dto.setIsRead(message.getIsRead());
        dto.setIsDeleted(message.getIsDeleted());
        dto.setIsRecalled(message.getIsRecalled());
        dto.setSendTime(message.getSendTime());
        dto.setReadTime(message.getReadTime());
        dto.setRecallTime(message.getRecallTime());
        dto.setReplyToMessageId(message.getReplyToMessageId());
        dto.setForwardFromMessageId(message.getForwardFromMessageId());
        dto.setConversationId(message.getConversationId());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        
        return dto;
    }
}
