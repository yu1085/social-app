package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 虚拟货币实体类
 */
@Entity
@Table(name = "virtual_currencies")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class VirtualCurrency {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "currency_type", length = 20, nullable = false)
    private String currencyType; // COINS, DIAMONDS, POINTS, GOLD
    
    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "frozen_amount", precision = 15, scale = 2)
    private BigDecimal frozenAmount = BigDecimal.ZERO;
    
    @Column(name = "total_earned", precision = 15, scale = 2)
    private BigDecimal totalEarned = BigDecimal.ZERO;
    
    @Column(name = "total_spent", precision = 15, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 货币类型枚举
    public enum CurrencyType {
        COINS("金币", "用于购买礼物、道具等"),
        DIAMONDS("钻石", "高级货币，用于购买VIP、特殊道具"),
        POINTS("积分", "通过活动获得，可兑换奖励"),
        GOLD("黄金", "最高级货币，用于购买稀有道具");
        
        private final String displayName;
        private final String description;
        
        CurrencyType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 构造函数
    public VirtualCurrency() {}
    
    public VirtualCurrency(Long userId, String currencyType) {
        this.userId = userId;
        this.currencyType = currencyType;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // 业务方法
    public BigDecimal getAvailableBalance() {
        return balance.subtract(frozenAmount);
    }
    
    public boolean hasEnoughBalance(BigDecimal amount) {
        return getAvailableBalance().compareTo(amount) >= 0;
    }
    
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.totalEarned = this.totalEarned.add(amount);
        this.lastUpdated = LocalDateTime.now();
    }
    
    public boolean deductBalance(BigDecimal amount) {
        if (hasEnoughBalance(amount)) {
            this.balance = this.balance.subtract(amount);
            this.totalSpent = this.totalSpent.add(amount);
            this.lastUpdated = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    public void freezeAmount(BigDecimal amount) {
        this.frozenAmount = this.frozenAmount.add(amount);
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void unfreezeAmount(BigDecimal amount) {
        this.frozenAmount = this.frozenAmount.subtract(amount);
        if (this.frozenAmount.compareTo(BigDecimal.ZERO) < 0) {
            this.frozenAmount = BigDecimal.ZERO;
        }
        this.lastUpdated = LocalDateTime.now();
    }
}
