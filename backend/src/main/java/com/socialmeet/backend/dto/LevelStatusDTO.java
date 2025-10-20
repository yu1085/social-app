package com.socialmeet.backend.dto;

/**
 * 等级状态DTO
 */
public class LevelStatusDTO {
    
    private String levelName;
    private Integer levelId;
    private Boolean isCurrent;
    private Boolean isLocked;
    private String iconUrl;
    private String levelColor;
    private Integer sortOrder;
    
    // 构造函数
    public LevelStatusDTO() {}
    
    public LevelStatusDTO(String levelName, Integer levelId, Boolean isCurrent, 
                         Boolean isLocked, String iconUrl, String levelColor, Integer sortOrder) {
        this.levelName = levelName;
        this.levelId = levelId;
        this.isCurrent = isCurrent;
        this.isLocked = isLocked;
        this.iconUrl = iconUrl;
        this.levelColor = levelColor;
        this.sortOrder = sortOrder;
    }
    
    // Getters and Setters
    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }
    
    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }
    
    public Boolean getIsCurrent() { return isCurrent; }
    public void setIsCurrent(Boolean isCurrent) { this.isCurrent = isCurrent; }
    
    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }
    
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    
    public String getLevelColor() { return levelColor; }
    public void setLevelColor(String levelColor) { this.levelColor = levelColor; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
