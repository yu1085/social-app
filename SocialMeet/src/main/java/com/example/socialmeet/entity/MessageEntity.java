package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 消息实体类 - 支持多种消息类型和状态管理
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "messages")
@Data
@EntityListeners(AuditingEntityListener.class)
public class MessageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT;
    
    @Column(name = "media_url", length = 500)
    private String mediaUrl;
    
    @Column(name = "media_thumbnail", length = 500)
    private String mediaThumbnail;
    
    @Column(name = "media_duration")
    private Integer mediaDuration; // 媒体文件时长（秒）
    
    @Column(name = "media_size")
    private Long mediaSize; // 媒体文件大小（字节）
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_status", nullable = false)
    private MessageStatus messageStatus = MessageStatus.SENDING;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "is_recalled", nullable = false)
    private Boolean isRecalled = false;
    
    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;
    
    @Column(name = "read_time")
    private LocalDateTime readTime;
    
    @Column(name = "recall_time")
    private LocalDateTime recallTime;
    
    @Column(name = "extra_data", columnDefinition = "JSON")
    private String extraData; // 扩展数据，JSON格式
    
    @Column(name = "reply_to_message_id")
    private Long replyToMessageId; // 回复的消息ID
    
    @Column(name = "forward_from_message_id")
    private Long forwardFromMessageId; // 转发的原消息ID
    
    @Column(name = "conversation_id")
    private Long conversationId; // 所属会话ID
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 关联用户信息（不存储，通过查询获取）
    @Transient
    private User sender;
    
    @Transient
    private User receiver;
    
    // 消息类型枚举
    public enum MessageType {
        TEXT("文本消息"),
        IMAGE("图片消息"),
        VIDEO("视频消息"),
        AUDIO("语音消息"),
        FILE("文件消息"),
        SYSTEM("系统消息"),
        NOTIFICATION("通知消息"),
        CALL("通话消息"),
        GIFT("礼物消息"),
        LOCATION("位置消息");
        
        private final String description;
        
        MessageType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 消息状态枚举
    public enum MessageStatus {
        SENDING("发送中"),
        SENT("已发送"),
        DELIVERED("已送达"),
        READ("已读"),
        FAILED("发送失败"),
        RECALLED("已撤回");
        
        private final String description;
        
        MessageStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
