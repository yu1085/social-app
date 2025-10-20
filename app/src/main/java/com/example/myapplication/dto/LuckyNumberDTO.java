package com.example.myapplication.dto;

import java.math.BigDecimal;

/**
 * 靓号DTO
 */
public class LuckyNumberDTO {
    
    private Long id;
    private String number;
    private BigDecimal price;
    private String tier;
    private String tierDisplayName;
    private String status;
    private String statusDisplayName;
    private Long ownerId;
    private String purchaseTime;
    private Integer validityDays;
    private String expireTime;
    private String description;
    private Boolean isSpecial;
    private String createdAt;
    private String updatedAt;
    
    // 构造函数
    public LuckyNumberDTO() {}
    
    public LuckyNumberDTO(Long id, String number, BigDecimal price, String tier, 
                         String tierDisplayName, String status, String statusDisplayName,
                         Long ownerId, String purchaseTime, Integer validityDays,
                         String expireTime, String description, Boolean isSpecial,
                         String createdAt, String updatedAt) {
        this.id = id;
        this.number = number;
        this.price = price;
        this.tier = tier;
        this.tierDisplayName = tierDisplayName;
        this.status = status;
        this.statusDisplayName = statusDisplayName;
        this.ownerId = ownerId;
        this.purchaseTime = purchaseTime;
        this.validityDays = validityDays;
        this.expireTime = expireTime;
        this.description = description;
        this.isSpecial = isSpecial;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
    
    public String getTierDisplayName() { return tierDisplayName; }
    public void setTierDisplayName(String tierDisplayName) { this.tierDisplayName = tierDisplayName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getStatusDisplayName() { return statusDisplayName; }
    public void setStatusDisplayName(String statusDisplayName) { this.statusDisplayName = statusDisplayName; }
    
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    public String getPurchaseTime() { return purchaseTime; }
    public void setPurchaseTime(String purchaseTime) { this.purchaseTime = purchaseTime; }
    
    public Integer getValidityDays() { return validityDays; }
    public void setValidityDays(Integer validityDays) { this.validityDays = validityDays; }
    
    public String getExpireTime() { return expireTime; }
    public void setExpireTime(String expireTime) { this.expireTime = expireTime; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsSpecial() { return isSpecial; }
    public void setIsSpecial(Boolean isSpecial) { this.isSpecial = isSpecial; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
