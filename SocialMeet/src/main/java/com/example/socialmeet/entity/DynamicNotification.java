package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 动态通知实体
 */
@Entity
@Table(name = "dynamic_notifications")
@Data
@EntityListeners(AuditingEntityListener.class)
public class DynamicNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "dynamic_id")
    private Long dynamicId;
    
    @Column(name = "from_user_id")
    private Long fromUserId;
    
    @Column(name = "type", length = 20, nullable = false)
    private String type; // LIKE, COMMENT, SHARE, FOLLOW
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
