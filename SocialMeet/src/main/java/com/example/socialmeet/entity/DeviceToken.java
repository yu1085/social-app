package com.example.socialmeet.entity;

import com.example.socialmeet.validation.ValidDateTime;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 设备令牌实体类 - 管理推送设备令牌
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "device_tokens")
@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class DeviceToken extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "token", length = 500, nullable = false)
    private String token;
    
    @Column(name = "platform", length = 20, nullable = false)
    private String platform; // iOS, Android, Web
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "app_version", length = 20)
    private String appVersion;
    
    @Column(name = "device_model", length = 100)
    private String deviceModel;
    
    @Column(name = "os_version", length = 20)
    private String osVersion;
    
    @ValidDateTime(allowNull = true, allowFuture = false)
    @Column(name = "last_used")
    private LocalDateTime lastUsed;
    
    // 关联用户信息（不存储，通过查询获取）
    @Transient
    private User user;
}