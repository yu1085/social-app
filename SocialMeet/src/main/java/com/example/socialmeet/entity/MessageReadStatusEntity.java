package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 消息已读状态实体类 - 用于群聊等复杂场景的消息已读状态管理
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "message_read_status")
@Data
@EntityListeners(AuditingEntityListener.class)
public class MessageReadStatusEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "message_id", nullable = false)
    private Long messageId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "read_time")
    private LocalDateTime readTime;
    
    @Column(name = "device_type", length = 50)
    private String deviceType; // 阅读设备类型
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress; // 阅读时的IP地址
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 关联用户信息（不存储，通过查询获取）
    @Transient
    private User user;
    
    @Transient
    private MessageEntity message;
    
    /**
     * 标记为已读
     */
    public void markAsRead() {
        this.isRead = true;
        this.readTime = LocalDateTime.now();
    }
    
    /**
     * 标记为未读
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readTime = null;
    }
}
