package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 动态举报实体
 */
@Entity
@Table(name = "dynamic_reports")
public class DynamicReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "dynamic_id", nullable = false)
    private Long dynamicId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "reason", nullable = false, length = 500)
    private String reason;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, PROCESSED, REJECTED
    
    @Column(name = "admin_id")
    private Long adminId;
    
    @Column(name = "admin_comment", length = 500)
    private String adminComment;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    // 构造函数
    public DynamicReport() {
        this.createdAt = LocalDateTime.now();
    }
    
    public DynamicReport(Long dynamicId, Long userId, String reason) {
        this();
        this.dynamicId = dynamicId;
        this.userId = userId;
        this.reason = reason;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDynamicId() {
        return dynamicId;
    }
    
    public void setDynamicId(Long dynamicId) {
        this.dynamicId = dynamicId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getAdminId() {
        return adminId;
    }
    
    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
    
    public String getAdminComment() {
        return adminComment;
    }
    
    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
