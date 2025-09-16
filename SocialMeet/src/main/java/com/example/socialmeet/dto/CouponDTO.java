package com.example.socialmeet.dto;

import com.example.socialmeet.entity.Coupon;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponDTO {
    private Long id;
    private String name;
    private String description;
    private Coupon.CouponType type;
    private BigDecimal value;
    private BigDecimal minAmount;
    private BigDecimal maxDiscount;
    private Integer validDays;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public CouponDTO() {}
    
    public CouponDTO(Coupon coupon) {
        this.id = coupon.getId();
        this.name = coupon.getName();
        this.description = coupon.getDescription();
        this.type = coupon.getType();
        this.value = coupon.getValue();
        this.minAmount = coupon.getMinAmount();
        this.maxDiscount = coupon.getMaxDiscount();
        this.validDays = coupon.getValidDays();
        this.isActive = coupon.getIsActive();
        this.createdAt = coupon.getCreatedAt();
        this.updatedAt = coupon.getUpdatedAt();
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Coupon.CouponType getType() {
        return type;
    }
    
    public void setType(Coupon.CouponType type) {
        this.type = type;
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    public BigDecimal getMinAmount() {
        return minAmount;
    }
    
    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }
    
    public BigDecimal getMaxDiscount() {
        return maxDiscount;
    }
    
    public void setMaxDiscount(BigDecimal maxDiscount) {
        this.maxDiscount = maxDiscount;
    }
    
    public Integer getValidDays() {
        return validDays;
    }
    
    public void setValidDays(Integer validDays) {
        this.validDays = validDays;
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
