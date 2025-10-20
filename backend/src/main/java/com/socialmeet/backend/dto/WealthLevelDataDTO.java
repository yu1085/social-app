package com.socialmeet.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 财富等级数据DTO
 */
public class WealthLevelDataDTO {
    
    private String levelName;
    private Integer levelId;
    private Integer wealthValue;
    private Integer nextLevelRequirement;
    private String levelDescription;
    private String levelIcon;
    private String levelColor;
    private Boolean isMaxLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public WealthLevelDataDTO() {}
    
    public WealthLevelDataDTO(String levelName, Integer levelId, Integer wealthValue, 
                             Integer nextLevelRequirement, String levelDescription, 
                             String levelIcon, String levelColor, Boolean isMaxLevel) {
        this.levelName = levelName;
        this.levelId = levelId;
        this.wealthValue = wealthValue;
        this.nextLevelRequirement = nextLevelRequirement;
        this.levelDescription = levelDescription;
        this.levelIcon = levelIcon;
        this.levelColor = levelColor;
        this.isMaxLevel = isMaxLevel;
    }
    
    // Getters and Setters
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    
    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }
    
    public Integer getWealthValue() { return wealthValue; }
    public void setWealthValue(Integer wealthValue) { this.wealthValue = wealthValue; }
    
    public Integer getNextLevelRequirement() { return nextLevelRequirement; }
    public void setNextLevelRequirement(Integer nextLevelRequirement) { this.nextLevelRequirement = nextLevelRequirement; }
    
    public String getLevelDescription() { return levelDescription; }
    public void setLevelDescription(String levelDescription) { this.levelDescription = levelDescription; }
    
    public String getLevelIcon() { return levelIcon; }
    public void setLevelIcon(String levelIcon) { this.levelIcon = levelIcon; }
    
    public String getLevelColor() { return levelColor; }
    public void setLevelColor(String levelColor) { this.levelColor = levelColor; }
    
    public Boolean getIsMaxLevel() { return isMaxLevel; }
    public void setIsMaxLevel(Boolean isMaxLevel) { this.isMaxLevel = isMaxLevel; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
