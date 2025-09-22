package com.example.socialmeet.dto;

import com.example.socialmeet.entity.MessageEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息DTO
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class MessageDTO {
    
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private MessageEntity.MessageType messageType;
    private String mediaUrl;
    private String mediaThumbnail;
    private Integer mediaDuration;
    private Long mediaSize;
    private MessageEntity.MessageStatus messageStatus;
    private Boolean isRead;
    private Boolean isDeleted;
    private Boolean isRecalled;
    private LocalDateTime sendTime;
    private LocalDateTime readTime;
    private LocalDateTime recallTime;
    private Long replyToMessageId;
    private Long forwardFromMessageId;
    private Long conversationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 扩展字段
    private String senderNickname;
    private String senderAvatar;
    private String receiverNickname;
    private String receiverAvatar;
    private MessageDTO replyToMessage;
    private MessageDTO forwardFromMessage;
}