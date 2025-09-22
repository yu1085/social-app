package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "third_party_auths")
public class ThirdPartyAuth {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "auth_id", unique = true, nullable = false)
    private String authId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "auth_type", nullable = false)
    private String authType; // ALIPAY, WECHAT
    
    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, SUCCESS, FAILED, CANCELLED
    
    @Column(name = "real_name")
    private String realName;
    
    @Column(name = "id_card_number")
    private String idCardNumber;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "auth_url")
    private String authUrl;
    
    @Column(name = "qr_code")
    private String qrCode;
    
    @Column(name = "redirect_url")
    private String redirectUrl;
    
    @Column(name = "extra_data", columnDefinition = "TEXT")
    private String extraData;
    
    @Column(name = "reject_reason")
    private String rejectReason;
    
    @Column(name = "message")
    private String message; // 状态消息
    
    @Column(name = "third_party_id")
    private String thirdPartyId; // 第三方平台的认证ID
    
    @Column(name = "third_party_response", columnDefinition = "TEXT")
    private String thirdPartyResponse; // 第三方平台的响应数据
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusMinutes(30); // 默认30分钟过期
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public ThirdPartyAuth() {}
    
    public ThirdPartyAuth(String authId, Long userId, String authType) {
        this.authId = authId;
        this.userId = userId;
        this.authType = authType;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAuthId() {
        return authId;
    }
    
    public void setAuthId(String authId) {
        this.authId = authId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public String getRedirectUrl() {
        return redirectUrl;
    }
    
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    
    public String getExtraData() {
        return extraData;
    }
    
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
    
    public String getRejectReason() {
        return rejectReason;
    }
    
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getThirdPartyId() {
        return thirdPartyId;
    }
    
    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }
    
    public String getThirdPartyResponse() {
        return thirdPartyResponse;
    }
    
    public void setThirdPartyResponse(String thirdPartyResponse) {
        this.thirdPartyResponse = thirdPartyResponse;
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
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
