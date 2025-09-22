package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 动态评论实体
 */
@Entity
@Table(name = "dynamic_comments")
@Data
@EntityListeners(AuditingEntityListener.class)
public class DynamicComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "dynamic_id", nullable = false)
    private Long dynamicId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "parent_id")
    private Long parentId; // 父评论ID，支持回复
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 关联用户信息（不存储，通过查询获取）
    @Transient
    private User user;
}
