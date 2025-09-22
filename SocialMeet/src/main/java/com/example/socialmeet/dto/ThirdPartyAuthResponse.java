package com.example.socialmeet.dto;

import java.time.LocalDateTime;

public class ThirdPartyAuthResponse {
    
    private String authId; // 认证ID
    private String authType; // 认证类型
    private String status; // 认证状态：PENDING, SUCCESS, FAILED, CANCELLED
    private String authUrl; // 认证URL（用于跳转）
    private String qrCode; // 二维码（如果需要）
    private String message; // 状态消息
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime expiresAt; // 过期时间
    private String realName; // 认证的真实姓名
    private String idCardNumber; // 身份证号（脱敏）
    private String phoneNumber; // 手机号（脱敏）
    private String rejectReason; // 拒绝原因
    private LocalDateTime completedAt; // 完成时间
    
    // Constructors
    public ThirdPartyAuthResponse() {}
    
    public ThirdPartyAuthResponse(String authId, String authType, String status) {
        this.authId = authId;
        this.authType = authType;
        this.status = status;
    }
    
    // Getters and Setters
    public String getAuthId() {
        return authId;
    }
    
    public void setAuthId(String authId) {
        this.authId = authId;
    }
    
    public String getAuthType() {
        return authType;
    }
    
    public void setAuthType(String authType) {
        this.authType = authType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAuthUrl() {
        return authUrl;
    }
    
    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }
    
    public String getQrCode() {
        return qrCode;
    }
    
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getIdCardNumber() {
        return idCardNumber;
    }
    
    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getRejectReason() {
        return rejectReason;
    }
    
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
