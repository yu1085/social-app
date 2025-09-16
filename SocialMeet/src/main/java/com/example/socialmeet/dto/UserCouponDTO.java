package com.example.socialmeet.dto;

import com.example.socialmeet.entity.UserCoupon;
import java.time.LocalDateTime;

public class UserCouponDTO {
    private Long id;
    private Long userId;
    private Long couponId;
    private String couponName;
    private String couponDescription;
    private UserCoupon.CouponStatus status;
    private LocalDateTime usedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    
    // Constructors
    public UserCouponDTO() {}
    
    public UserCouponDTO(UserCoupon userCoupon) {
        this.id = userCoupon.getId();
        this.userId = userCoupon.getUserId();
        this.couponId = userCoupon.getCouponId();
        this.status = userCoupon.getStatus();
        this.usedAt = userCoupon.getUsedAt();
        this.expiresAt = userCoupon.getExpiresAt();
        this.createdAt = userCoupon.getCreatedAt();
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
    
    public Long getCouponId() {
        return couponId;
    }
    
    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
    
    public String getCouponName() {
        return couponName;
    }
    
    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }
    
    public String getCouponDescription() {
        return couponDescription;
    }
    
    public void setCouponDescription(String couponDescription) {
        this.couponDescription = couponDescription;
    }
    
    public UserCoupon.CouponStatus getStatus() {
        return status;
    }
    
    public void setStatus(UserCoupon.CouponStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getUsedAt() {
        return usedAt;
    }
    
    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
