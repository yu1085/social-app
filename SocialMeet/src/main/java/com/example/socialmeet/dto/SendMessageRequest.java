package com.example.socialmeet.dto;

import com.example.socialmeet.entity.MessageEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发送消息请求DTO
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
public class SendMessageRequest {
    
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;
    
    private String content;
    
    @NotNull(message = "消息类型不能为空")
    private MessageEntity.MessageType messageType = MessageEntity.MessageType.TEXT;
    
    private String mediaUrl;
    private String mediaThumbnail;
    private Integer mediaDuration;
    private Long mediaSize;
    private Long replyToMessageId;
    private Long forwardFromMessageId;
    private String extraData;
}
