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
 * 充值套餐实体类
 */
@Entity
@Table(name = "recharge_packages")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class RechargePackage {
    
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "coins", nullable = false)
    private Long coins;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    @Column(name = "bonus_coins")
    private Long bonusCoins = 0L;
    
    @Column(name = "first_time_bonus")
    private Long firstTimeBonus = 0L;
    
    @Column(name = "is_recommended")
    private Boolean isRecommended = false;
    
    @Column(name = "is_popular")
    private Boolean isPopular = false;
    
    @Column(name = "discount_label", length = 50)
    private String discountLabel;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public RechargePackage() {}
    
    public RechargePackage(String id, String name, Long coins, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.coins = coins;
        this.price = price;
    }
}
