package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "call_sessions")
public class CallSession {
    
    @Id
    @Column(name = "id")
    private String id;
    
    @Column(name = "caller_id", nullable = false)
    private Long callerId;
    
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;
    
    @Column(name = "status", nullable = false)
    private String status; // INITIATED, RINGING, ACTIVE, ENDED, REJECTED, CANCELLED, FAILED
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration")
    private Long duration; // 通话时长（秒）
    
    @Column(name = "rate", precision = 10, scale = 2)
    private BigDecimal rate; // 每分钟费率（元）
    
    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost; // 总费用（元）
    
    @Column(name = "caller_balance", precision = 10, scale = 2)
    private BigDecimal callerBalance; // 发起方余额
    
    @Column(name = "receiver_balance", precision = 10, scale = 2)
    private BigDecimal receiverBalance; // 接收方余额
    
    @Column(name = "is_online")
    private Boolean isOnline; // 接收方是否在线
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public CallSession() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getCallerId() {
        return callerId;
    }
    
    public void setCallerId(Long callerId) {
        this.callerId = callerId;
    }
    
    public Long getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Long getDuration() {
        return duration;
    }
    
    public void setDuration(Long duration) {
        this.duration = duration;
    }
    
    public BigDecimal getRate() {
        return rate;
    }
    
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
    
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    
    public BigDecimal getCallerBalance() {
        return callerBalance;
    }
    
    public void setCallerBalance(BigDecimal callerBalance) {
        this.callerBalance = callerBalance;
    }
    
    public void setCallerBalance(Double callerBalance) {
        this.callerBalance = BigDecimal.valueOf(callerBalance);
    }
    
    public BigDecimal getReceiverBalance() {
        return receiverBalance;
    }
    
    public void setReceiverBalance(BigDecimal receiverBalance) {
        this.receiverBalance = receiverBalance;
    }
    
    public void setReceiverBalance(Double receiverBalance) {
        this.receiverBalance = BigDecimal.valueOf(receiverBalance);
    }
    
    public Boolean getIsOnline() {
        return isOnline;
    }
    
    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
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
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
