package com.example.socialmeet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VipPaymentOrderDTO {
    private String orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String orderInfo; // 支付宝支付字符串
    private Long vipLevelId;
    private String vipLevelName;
    private LocalDateTime createdAt;
    
    // Constructors
    public VipPaymentOrderDTO() {}
    
    public VipPaymentOrderDTO(String orderId, BigDecimal amount, String paymentMethod, 
                             String orderInfo, Long vipLevelId, String vipLevelName) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.orderInfo = orderInfo;
        this.vipLevelId = vipLevelId;
        this.vipLevelName = vipLevelName;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getOrderInfo() {
        return orderInfo;
    }
    
    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
