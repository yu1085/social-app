package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 财富等级规则实体类
 * 存储财富等级的规则定义，不包含用户数据
 */
@Entity
@Table(name = "wealth_level_rules")
public class WealthLevelRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
    public WealthLevelRule() {}
    
    public WealthLevelRule(String levelName, String levelIcon, String levelColor, 
                          Integer minWealthValue, Integer maxWealthValue) {
        this.levelName = levelName;
        this.levelIcon = levelIcon;
        this.levelColor = levelColor;
        this.minWealthValue = minWealthValue;
        this.maxWealthValue = maxWealthValue;
    }
    
    /**
     * 根据财富值判断是否匹配此规则
     */
    public boolean matches(Integer wealthValue) {
        if (wealthValue == null) {
            return false;
        }
        
        if (maxWealthValue == null) {
            // 最高等级，无上限
            return wealthValue >= minWealthValue;
        } else {
            return wealthValue >= minWealthValue && wealthValue <= maxWealthValue;
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    @Override
    public String toString() {
        return "WealthLevelRule{" +
                "id=" + id +
                ", levelName='" + levelName + '\'' +
                ", levelIcon='" + levelIcon + '\'' +
                ", levelColor='" + levelColor + '\'' +
                ", minWealthValue=" + minWealthValue +
                ", maxWealthValue=" + maxWealthValue +
                '}';
    }
}
