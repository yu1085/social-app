package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.dto.ConversationDTO;
import com.socialmeet.backend.dto.MessageDTO;
import com.socialmeet.backend.entity.Message;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.entity.UserSettings;
import com.socialmeet.backend.repository.MessageRepository;
import com.socialmeet.backend.repository.UserRepository;
import com.socialmeet.backend.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息服务层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;
    private final JPushService jPushService;
    
    /**
     * 发送消息
     */
    @Transactional
    public MessageDTO sendMessage(Long senderId, Long receiverId, String content, Message.MessageType messageType) {
        log.info("发送消息 - senderId: {}, receiverId: {}, content: {}, type: {}", 
                senderId, receiverId, content, messageType);
        
        // 验证发送者和接收者是否存在
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("发送者不存在"));
        
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("接收者不存在"));
        
        // 创建消息
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .messageType(messageType)
                .isRead(false)
                .build();
        
        message = messageRepository.save(message);
        log.info("消息保存成功 - messageId: {}", message.getId());
        
        // 发送推送通知给接收者
        try {
            sendMessagePushNotification(receiver, sender, message);
        } catch (Exception e) {
            log.error("发送消息推送失败", e);
            // 推送失败不影响消息发送
        }
        
        // 转换为DTO并返回
        MessageDTO messageDTO = MessageDTO.fromEntity(message);
        messageDTO.setSenderName(sender.getNickname());
        messageDTO.setSenderAvatar(sender.getAvatarUrl());
        messageDTO.setReceiverName(receiver.getNickname());
        messageDTO.setReceiverAvatar(receiver.getAvatarUrl());
        
        return messageDTO;
    }
    
    /**
     * 获取聊天记录
     */
    public List<MessageDTO> getChatHistory(Long userId1, Long userId2) {
        log.info("获取聊天记录 - userId1: {}, userId2: {}", userId1, userId2);
        
        List<Message> messages = messageRepository.findChatHistory(userId1, userId2);
        
        return messages.stream().map(message -> {
            MessageDTO dto = MessageDTO.fromEntity(message);
            
            // 设置发送者信息
            if (message.getSenderId().equals(userId1)) {
                User sender = userRepository.findById(userId1).orElse(null);
                if (sender != null) {
                    dto.setSenderName(sender.getNickname());
                    dto.setSenderAvatar(sender.getAvatarUrl());
                }
            } else {
                User sender = userRepository.findById(userId2).orElse(null);
                if (sender != null) {
                    dto.setSenderName(sender.getNickname());
                    dto.setSenderAvatar(sender.getAvatarUrl());
                }
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 标记消息为已读
     */
    @Transactional
    public void markMessagesAsRead(Long userId, Long otherUserId) {
        log.info("标记消息为已读 - userId: {}, otherUserId: {}", userId, otherUserId);
        
        List<Message> unreadMessages = messageRepository.findChatHistory(userId, otherUserId)
                .stream()
                .filter(message -> message.getReceiverId().equals(userId) && !message.getIsRead())
                .collect(Collectors.toList());
        
        unreadMessages.forEach(message -> message.setIsRead(true));
        messageRepository.saveAll(unreadMessages);
        
        log.info("已标记 {} 条消息为已读", unreadMessages.size());
    }
    
    /**
     * 获取未读消息数量
     */
    public Long getUnreadMessageCount(Long userId, Long otherUserId) {
        return messageRepository.countUnreadMessages(otherUserId, userId, userId);
    }
    
    /**
     * 获取用户的所有未读消息
     */
    public List<MessageDTO> getUnreadMessages(Long userId) {
        List<Message> messages = messageRepository.findUnreadMessages(userId);
        
        return messages.stream().map(message -> {
            MessageDTO dto = MessageDTO.fromEntity(message);
            
            // 设置发送者信息
            User sender = userRepository.findById(message.getSenderId()).orElse(null);
            if (sender != null) {
                dto.setSenderName(sender.getNickname());
                dto.setSenderAvatar(sender.getAvatarUrl());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 获取会话列表
     */
    public List<ConversationDTO> getConversations(Long userId) {
        log.info("获取用户{}的会话列表", userId);

        // 获取用户所有相关的消息
        List<Message> allMessages = messageRepository.findUserMessages(userId);

        // 按对话对方分组，找到每个对话的最后一条消息
        Map<Long, Message> latestMessages = new HashMap<>();
        for (Message message : allMessages) {
            Long otherUserId = message.getSenderId().equals(userId) ?
                    message.getReceiverId() : message.getSenderId();

            Message existing = latestMessages.get(otherUserId);
            if (existing == null || message.getCreatedAt().isAfter(existing.getCreatedAt())) {
                latestMessages.put(otherUserId, message);
            }
        }

        // 构建ConversationDTO列表
        List<ConversationDTO> conversations = new ArrayList<>();
        for (Map.Entry<Long, Message> entry : latestMessages.entrySet()) {
            Long otherUserId = entry.getKey();
            Message lastMessage = entry.getValue();

            User otherUser = userRepository.findById(otherUserId).orElse(null);
            if (otherUser == null) continue;

            // 获取未读消息数量
            Long unreadCount = messageRepository.countUnreadMessages(otherUserId, userId, userId);

            // 获取对方的通话价格设置
            BigDecimal currentPrice = userSettingsRepository.findByUserId(otherUserId)
                    .map(UserSettings::getVideoCallPrice)
                    .orElse(BigDecimal.ZERO);

            // 构建DTO
            ConversationDTO dto = ConversationDTO.builder()
                    .userId(otherUserId)
                    .nickname(otherUser.getNickname())
                    .avatar("")  // User实体没有avatar字段
                    .lastMessage(lastMessage.getContent())
                    .lastMessageTime(lastMessage.getCreatedAt())
                    .unreadCount(unreadCount)
                    .isOnline(false)  // 需要实现在线状态检测
                    .currentPrice(currentPrice.doubleValue())
                    .build();

            conversations.add(dto);
        }

        // 按最后消息时间排序
        conversations.sort((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()));

        return conversations;
    }

    /**
     * 发送消息推送通知
     */
    private void sendMessagePushNotification(User receiver, User sender, Message message) {
        if (receiver.getJpushRegistrationId() == null || receiver.getJpushRegistrationId().trim().isEmpty()) {
            log.warn("接收者未上传 Registration ID，跳过推送通知");
            return;
        }

        try {
            // 构建推送内容
            String title = "新消息";
            String content = sender.getNickname() + ": " + message.getContent();

            // 构建自定义数据
            java.util.Map<String, Object> extras = new java.util.HashMap<>();
            extras.put("type", "MESSAGE");
            extras.put("messageId", message.getId().toString());
            extras.put("senderId", sender.getId().toString());
            extras.put("senderName", sender.getNickname());
            extras.put("messageType", message.getMessageType().toString());
            extras.put("timestamp", String.valueOf(System.currentTimeMillis()));

            // 发送推送（多设备支持）
            boolean sent = jPushService.sendNotification(
                    receiver.getId(),
                    title,
                    content,
                    extras
            );

            if (sent) {
                log.info("消息推送发送成功 - receiverId: {}, messageId: {}", receiver.getId(), message.getId());
            } else {
                log.warn("消息推送发送失败 - receiverId: {}, messageId: {}", receiver.getId(), message.getId());
            }

        } catch (Exception e) {
            log.error("发送消息推送异常", e);
        }
    }
}
