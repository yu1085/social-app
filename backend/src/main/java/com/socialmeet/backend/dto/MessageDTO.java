package com.socialmeet.backend.dto;

import com.socialmeet.backend.entity.Message;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 消息传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Message.MessageType messageType;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 发送者信息
    private String senderName;
    private String senderAvatar;
    
    // 接收者信息
    private String receiverName;
    private String receiverAvatar;
    
    /**
     * 从实体转换为DTO
     */
    public static MessageDTO fromEntity(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
    
    /**
     * 转换为实体
     */
    public Message toEntity() {
        return Message.builder()
                .id(this.id)
                .senderId(this.senderId)
                .receiverId(this.receiverId)
                .content(this.content)
                .messageType(this.messageType)
                .isRead(this.isRead)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}
