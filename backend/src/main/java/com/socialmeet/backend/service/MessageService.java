package com.socialmeet.backend.service;

import com.socialmeet.backend.dto.ApiResponse;
import com.socialmeet.backend.dto.MessageDTO;
import com.socialmeet.backend.entity.Message;
import com.socialmeet.backend.entity.User;
import com.socialmeet.backend.repository.MessageRepository;
import com.socialmeet.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
            
            // 发送推送
            boolean sent = jPushService.sendNotification(
                    receiver.getId(),
                    receiver.getJpushRegistrationId(),
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
