package com.example.socialmeet.service;

import com.example.socialmeet.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 审计服务 - 统一处理实体的创建和更新操作
 * 确保日期时间字段的正确设置
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@Slf4j
public class AuditService {
    
    /**
     * 处理实体创建前的审计逻辑
     * @param entity 要创建的实体
     */
    public void beforeCreate(BaseEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
            log.debug("设置实体创建时间: {}", now);
        }
        
        if (entity.getUpdatedAt() == null) {
            entity.setUpdatedAt(now);
            log.debug("设置实体更新时间: {}", now);
        }
        
        // 验证日期时间字段
        if (!entity.isValidDateTime()) {
            log.warn("实体日期时间字段验证失败: {}", entity.getClass().getSimpleName());
            throw new IllegalArgumentException("实体日期时间字段无效");
        }
    }
    
    /**
     * 处理实体更新前的审计逻辑
     * @param entity 要更新的实体
     */
    public void beforeUpdate(BaseEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        
        // 更新时只设置updatedAt，不修改createdAt
        entity.setUpdatedAt(now);
        log.debug("更新实体修改时间: {}", now);
        
        // 验证日期时间字段
        if (!entity.isValidDateTime()) {
            log.warn("实体日期时间字段验证失败: {}", entity.getClass().getSimpleName());
            throw new IllegalArgumentException("实体日期时间字段无效");
        }
    }
    
    /**
     * 验证并修复实体的日期时间字段
     * @param entity 要验证的实体
     * @return 是否进行了修复
     */
    public boolean validateAndFixDateTime(BaseEntity entity) {
        boolean fixed = false;
        LocalDateTime now = LocalDateTime.now();
        
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
            fixed = true;
            log.info("修复实体创建时间: {}", entity.getClass().getSimpleName());
        }
        
        if (entity.getUpdatedAt() == null) {
            entity.setUpdatedAt(now);
            fixed = true;
            log.info("修复实体更新时间: {}", entity.getClass().getSimpleName());
        }
        
        return fixed;
    }
    
    /**
     * 检查实体是否需要日期时间修复
     * @param entity 要检查的实体
     * @return 是否需要修复
     */
    public boolean needsDateTimeFix(BaseEntity entity) {
        return entity.getCreatedAt() == null || entity.getUpdatedAt() == null;
    }
}
