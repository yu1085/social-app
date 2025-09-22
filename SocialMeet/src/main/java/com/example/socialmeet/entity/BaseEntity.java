package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 基础实体类 - 统一处理日期时间字段
 * 提供标准的创建时间、更新时间字段和自动填充逻辑
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    
    /**
     * 创建时间 - 自动填充，不可更新
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间 - 自动填充，每次更新时自动设置
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 实体创建前的回调 - 确保日期时间字段不为空
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }
    
    /**
     * 实体更新前的回调 - 确保更新时间字段正确设置
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 验证日期时间字段是否有效
     * @return 如果所有日期时间字段都有效则返回true
     */
    public boolean isValidDateTime() {
        return createdAt != null && updatedAt != null;
    }
    
    /**
     * 获取创建时间的字符串表示
     * @return 格式化的创建时间字符串
     */
    public String getCreatedAtString() {
        return createdAt != null ? createdAt.toString() : "未设置";
    }
    
    /**
     * 获取更新时间的字符串表示
     * @return 格式化的更新时间字符串
     */
    public String getUpdatedAtString() {
        return updatedAt != null ? updatedAt.toString() : "未设置";
    }
}
