package com.example.socialmeet.dto;

import com.example.socialmeet.entity.WealthLevel;

import java.util.List;

public class WealthLevelDTO {
    private Long id;
    private Long userId;
    private Integer wealthValue;
    private String levelName;
    private String levelIcon;
    private String levelColor;
    private Integer minWealthValue;
    private Integer maxWealthValue;
    private Double progressPercentage;
    private Integer nextLevelRequirement;
    private String nextLevelName;
    private String nextLevelIcon;
    private String nextLevelColor;
    private List<WealthLevel.PrivilegeType> privileges;
    private Long userRank;
    
    public WealthLevelDTO() {}
    
    public WealthLevelDTO(WealthLevel wealthLevel) {
        this.id = wealthLevel.getId();
        this.userId = wealthLevel.getUserId();
        this.wealthValue = wealthLevel.getWealthValue();
        this.levelName = wealthLevel.getLevelName();
        this.levelIcon = wealthLevel.getLevelIcon();
        this.levelColor = wealthLevel.getLevelColor();
        this.minWealthValue = wealthLevel.getMinWealthValue();
        this.maxWealthValue = wealthLevel.getMaxWealthValue();
        // 进度和下一等级信息需要在服务层计算，这里先设为null
        this.progressPercentage = null;
        this.nextLevelRequirement = null;
        this.nextLevelName = null;
        this.nextLevelIcon = null;
        this.nextLevelColor = null;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Integer getWealthValue() { return wealthValue; }
    public void setWealthValue(Integer wealthValue) { this.wealthValue = wealthValue; }
    
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    
    public String getLevelIcon() { return levelIcon; }
    public void setLevelIcon(String levelIcon) { this.levelIcon = levelIcon; }
    
    public String getLevelColor() { return levelColor; }
    public void setLevelColor(String levelColor) { this.levelColor = levelColor; }
    
    public Integer getMinWealthValue() { return minWealthValue; }
    public void setMinWealthValue(Integer minWealthValue) { this.minWealthValue = minWealthValue; }
    
    public Integer getMaxWealthValue() { return maxWealthValue; }
    public void setMaxWealthValue(Integer maxWealthValue) { this.maxWealthValue = maxWealthValue; }
    
    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }
    
    public Integer getNextLevelRequirement() { return nextLevelRequirement; }
    public void setNextLevelRequirement(Integer nextLevelRequirement) { this.nextLevelRequirement = nextLevelRequirement; }
    
    public String getNextLevelName() { return nextLevelName; }
    public void setNextLevelName(String nextLevelName) { this.nextLevelName = nextLevelName; }
    
    public String getNextLevelIcon() { return nextLevelIcon; }
    public void setNextLevelIcon(String nextLevelIcon) { this.nextLevelIcon = nextLevelIcon; }
    
    public String getNextLevelColor() { return nextLevelColor; }
    public void setNextLevelColor(String nextLevelColor) { this.nextLevelColor = nextLevelColor; }
    
    public List<WealthLevel.PrivilegeType> getPrivileges() { return privileges; }
    public void setPrivileges(List<WealthLevel.PrivilegeType> privileges) { this.privileges = privileges; }
    
    public Long getUserRank() { return userRank; }
    public void setUserRank(Long userRank) { this.userRank = userRank; }
}