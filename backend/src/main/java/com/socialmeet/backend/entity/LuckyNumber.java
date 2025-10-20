package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 靓号实体
 */
@Entity
@Table(name = "lucky_numbers")
@Data
@EntityListeners(AuditingEntityListener.class)
public class LuckyNumber {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "number", nullable = false, unique = true)
    private String number; // 靓号数字
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // 价格（聊币）
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private LuckyNumberTier tier; // 等级：LIMITED, SUPER, TOP_TIER
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LuckyNumberStatus status; // 状态：AVAILABLE, SOLD, RESERVED
    
    @Column(name = "owner_id")
    private Long ownerId; // 拥有者ID
    
    @Column(name = "purchase_time")
    private LocalDateTime purchaseTime; // 购买时间
    
    @Column(name = "validity_days")
    private Integer validityDays; // 有效期（天）
    
    @Column(name = "expire_time")
    private LocalDateTime expireTime; // 过期时间
    
    @Column(name = "description")
    private String description; // 描述
    
    @Column(name = "is_special", nullable = false)
    private Boolean isSpecial = false; // 是否特殊靓号
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 靓号等级枚举
     */
    public enum LuckyNumberTier {
        LIMITED("限量"),
        SUPER("超级"),
        TOP_TIER("顶级");
        
        private final String displayName;
        
        LuckyNumberTier(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 靓号状态枚举
     */
    public enum LuckyNumberStatus {
        AVAILABLE("可购买"),
        SOLD("已售出"),
        RESERVED("已预订");
        
        private final String displayName;
        
        LuckyNumberStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
