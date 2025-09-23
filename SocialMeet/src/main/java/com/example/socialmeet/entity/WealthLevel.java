package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wealth_levels")
public class WealthLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "wealth_value", nullable = false)
    private Integer wealthValue = 0;
    
    @Column(name = "level_name", nullable = false)
    private String levelName;
    
    @Column(name = "level_icon")
    private String levelIcon;
    
    @Column(name = "level_color")
    private String levelColor;
    
    @Column(name = "min_wealth_value", nullable = false)
    private Integer minWealthValue;
    
    @Column(name = "max_wealth_value")
    private Integer maxWealthValue;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public WealthLevel() {}
    
    public WealthLevel(Long userId, Integer wealthValue) {
        this.userId = userId;
        this.wealthValue = wealthValue;
        // 等级信息将在服务层根据规则更新
    }
    
    // 根据财富等级规则更新等级信息
    public void updateLevelInfo(WealthLevelRule rule) {
        if (rule != null) {
            this.levelName = rule.getLevelName();
            this.levelIcon = rule.getLevelIcon();
            this.levelColor = rule.getLevelColor();
            this.minWealthValue = rule.getMinWealthValue();
            this.maxWealthValue = rule.getMaxWealthValue();
        }
    }
    
    // 检查是否有特定特权
    public boolean hasPrivilege(PrivilegeType privilege) {
        switch (privilege) {
            case LUCKY_NUMBER_DISCOUNT:
                return wealthValue >= 1000; // 青铜及以上
            case WEEKLY_PROMOTION:
                return wealthValue >= 1000; // 青铜及以上
            case VIP_DISCOUNT:
                return wealthValue >= 2000; // 白银及以上
            case EFFECT_DISCOUNT:
                return wealthValue >= 2000; // 白银及以上
            case FREE_VIP:
                return wealthValue >= 500000; // 红钻及以上
            case FREE_EFFECT:
                return wealthValue >= 500000; // 红钻及以上
            case EXCLUSIVE_EFFECT:
                return wealthValue >= 300000; // 橙钻及以上
            case LUCKY_NUMBER_CUSTOM:
                return wealthValue >= 300000; // 橙钻及以上
            case EXCLUSIVE_GIFT:
                return wealthValue >= 300000; // 橙钻及以上
            case EXCLUSIVE_SERVICE:
                return wealthValue >= 30000; // 青钻及以上
            default:
                return false;
        }
    }
    
    // 获取升级到下一等级需要的财富值（需要从服务层传入下一等级规则）
    public Integer getNextLevelRequirement(WealthLevelRule nextRule) {
        if (nextRule == null) {
            return null; // 已经是最高等级
        }
        return nextRule.getMinWealthValue();
    }
    
    // 获取当前等级进度百分比（需要从服务层传入下一等级规则）
    public Double getLevelProgress(WealthLevelRule nextRule) {
        if (nextRule == null) {
            return 100.0; // 最高等级
        }
        
        Integer currentMin = minWealthValue;
        Integer currentMax = nextRule.getMinWealthValue();
        
        if (currentMax <= currentMin) {
            return 100.0;
        }
        
        return ((double)(wealthValue - currentMin) / (currentMax - currentMin)) * 100.0;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Integer getWealthValue() {
        return wealthValue;
    }
    
    public void setWealthValue(Integer wealthValue) {
        this.wealthValue = wealthValue;
        // 等级信息将在服务层根据规则更新
    }
    
    public String getLevelName() {
        return levelName;
    }
    
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
    
    public String getLevelIcon() {
        return levelIcon;
    }
    
    public void setLevelIcon(String levelIcon) {
        this.levelIcon = levelIcon;
    }
    
    public String getLevelColor() {
        return levelColor;
    }
    
    public void setLevelColor(String levelColor) {
        this.levelColor = levelColor;
    }
    
    public Integer getMinWealthValue() {
        return minWealthValue;
    }
    
    public void setMinWealthValue(Integer minWealthValue) {
        this.minWealthValue = minWealthValue;
    }
    
    public Integer getMaxWealthValue() {
        return maxWealthValue;
    }
    
    public void setMaxWealthValue(Integer maxWealthValue) {
        this.maxWealthValue = maxWealthValue;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    
    // 特权类型枚举
    public enum PrivilegeType {
        LUCKY_NUMBER_DISCOUNT("靓号折扣"),
        WEEKLY_PROMOTION("每周促销"),
        VIP_DISCOUNT("VIP折扣"),
        EFFECT_DISCOUNT("特效折扣"),
        FREE_VIP("免费VIP"),
        FREE_EFFECT("免费特效"),
        EXCLUSIVE_EFFECT("专属特效"),
        LUCKY_NUMBER_CUSTOM("靓号定制"),
        EXCLUSIVE_GIFT("专属礼物"),
        EXCLUSIVE_SERVICE("专属客服服务");
        
        private final String displayName;
        
        PrivilegeType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return displayName;
        }
    }
}