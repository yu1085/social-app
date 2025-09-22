package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "id_card_verify")
public class IdCardVerify {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "verify_id", unique = true, nullable = false)
    private String verifyId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "cert_name", nullable = false)
    private String certName;
    
    @Column(name = "cert_no", nullable = false)
    private String certNo;
    
    @Column(name = "status", nullable = false)
    private String status; // PENDING, SUCCESS, FAILED
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "reject_reason")
    private String rejectReason;
    
    @Column(name = "certify_id")
    private String certifyId; // 支付宝认证单据号
    
    @Column(name = "alipay_response", columnDefinition = "TEXT")
    private String alipayResponse;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    // 构造函数
    public IdCardVerify() {}
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getVerifyId() {
        return verifyId;
    }
    
    public void setVerifyId(String verifyId) {
        this.verifyId = verifyId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getCertName() {
        return certName;
    }
    
    public void setCertName(String certName) {
        this.certName = certName;
    }
    
    public String getCertNo() {
        return certNo;
    }
    
    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getRejectReason() {
        return rejectReason;
    }
    
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    
    public String getCertifyId() {
        return certifyId;
    }
    
    public void setCertifyId(String certifyId) {
        this.certifyId = certifyId;
    }
    
    public String getAlipayResponse() {
        return alipayResponse;
    }
    
    public void setAlipayResponse(String alipayResponse) {
        this.alipayResponse = alipayResponse;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
