package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 通话扣费记录实体
 */
@Entity
@Table(name = "call_charges")
public class CallCharge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "call_session_id", nullable = false, length = 50)
    private String callSessionId; // 通话会话ID
    
    @Column(name = "user_id", nullable = false)
    private Long userId; // 用户ID
    
    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds; // 通话时长（秒）
    
    @Column(name = "rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal rate; // 每分钟费率（元）
    
    @Column(name = "charged_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal chargedAmount; // 扣费金额（元）
    
    @Column(name = "balance_before", precision = 10, scale = 2, nullable = false)
    private BigDecimal balanceBefore; // 扣费前余额
    
    @Column(name = "balance_after", precision = 10, scale = 2, nullable = false)
    private BigDecimal balanceAfter; // 扣费后余额
    
    @Column(name = "created_at")
    private LocalDateTime createdAt; // 创建时间
    
    // 构造函数
    public CallCharge() {}
    
    public CallCharge(String callSessionId, Long userId, Integer durationSeconds, 
                     BigDecimal rate, BigDecimal chargedAmount, 
                     BigDecimal balanceBefore, BigDecimal balanceAfter) {
        this.callSessionId = callSessionId;
        this.userId = userId;
        this.durationSeconds = durationSeconds;
        this.rate = rate;
        this.chargedAmount = chargedAmount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCallSessionId() {
        return callSessionId;
    }
    
    public void setCallSessionId(String callSessionId) {
        this.callSessionId = callSessionId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Integer getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    public BigDecimal getRate() {
        return rate;
    }
    
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
    
    public BigDecimal getChargedAmount() {
        return chargedAmount;
    }
    
    public void setChargedAmount(BigDecimal chargedAmount) {
        this.chargedAmount = chargedAmount;
    }
    
    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }
    
    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }
    
    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
