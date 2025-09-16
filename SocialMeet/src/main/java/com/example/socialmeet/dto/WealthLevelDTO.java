package com.example.socialmeet.dto;

import com.example.socialmeet.entity.WealthLevel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WealthLevelDTO {
    private Long id;
    private String name;
    private Integer level;
    private BigDecimal minContribution;
    private BigDecimal maxContribution;
    private String benefits;
    private String iconUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public WealthLevelDTO() {}
    
    public WealthLevelDTO(WealthLevel wealthLevel) {
        this.id = wealthLevel.getId();
        this.name = wealthLevel.getName();
        this.level = wealthLevel.getLevel();
        this.minContribution = wealthLevel.getMinContribution();
        this.maxContribution = wealthLevel.getMaxContribution();
        this.benefits = wealthLevel.getBenefits();
        this.iconUrl = wealthLevel.getIconUrl();
        this.isActive = wealthLevel.getIsActive();
        this.createdAt = wealthLevel.getCreatedAt();
        this.updatedAt = wealthLevel.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getLevel() {
        return level;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public BigDecimal getMinContribution() {
        return minContribution;
    }
    
    public void setMinContribution(BigDecimal minContribution) {
        this.minContribution = minContribution;
    }
    
    public BigDecimal getMaxContribution() {
        return maxContribution;
    }
    
    public void setMaxContribution(BigDecimal maxContribution) {
        this.maxContribution = maxContribution;
    }
    
    public String getBenefits() {
        return benefits;
    }
    
    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
}
