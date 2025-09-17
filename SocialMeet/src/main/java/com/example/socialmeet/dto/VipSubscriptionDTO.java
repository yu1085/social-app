package com.example.socialmeet.dto;

import com.example.socialmeet.entity.VipSubscription;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VipSubscriptionDTO {
    private Long id;
    private Long userId;
    private Long vipLevelId;
    private String vipLevelName;
    private Integer vipLevel;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public VipSubscriptionDTO() {}
    
    public VipSubscriptionDTO(VipSubscription subscription) {
        this.id = subscription.getId();
        this.userId = subscription.getUserId();
        this.vipLevelId = subscription.getVipLevelId();
        this.startDate = subscription.getStartDate();
        this.endDate = subscription.getEndDate();
        this.status = subscription.getStatus();
        this.amount = subscription.getAmount();
        this.createdAt = subscription.getCreatedAt();
        this.updatedAt = subscription.getUpdatedAt();
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
    
    public Long getVipLevelId() {
        return vipLevelId;
    }
    
    public void setVipLevelId(Long vipLevelId) {
        this.vipLevelId = vipLevelId;
    }
    
    public String getVipLevelName() {
        return vipLevelName;
    }
    
    public void setVipLevelName(String vipLevelName) {
        this.vipLevelName = vipLevelName;
    }
    
    public Integer getVipLevel() {
        return vipLevel;
    }
    
    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
