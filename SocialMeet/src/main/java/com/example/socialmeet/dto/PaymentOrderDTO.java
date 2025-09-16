package com.example.socialmeet.dto;

import com.example.socialmeet.entity.PaymentOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentOrderDTO {
    private Long id;
    private Long userId;
    private String orderNo;
    private PaymentOrder.OrderType type;
    private BigDecimal amount;
    private PaymentOrder.OrderStatus status;
    private PaymentOrder.PaymentMethod paymentMethod;
    private String paymentNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public PaymentOrderDTO() {}
    
    public PaymentOrderDTO(PaymentOrder order) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.orderNo = order.getOrderNo();
        this.type = order.getType();
        this.amount = order.getAmount();
        this.status = order.getStatus();
        this.paymentMethod = order.getPaymentMethod();
        this.paymentNo = order.getPaymentNo();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
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
    
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public PaymentOrder.OrderType getType() {
        return type;
    }
    
    public void setType(PaymentOrder.OrderType type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public PaymentOrder.OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentOrder.OrderStatus status) {
        this.status = status;
    }
    
    public PaymentOrder.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentOrder.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentNo() {
        return paymentNo;
    }
    
    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
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
