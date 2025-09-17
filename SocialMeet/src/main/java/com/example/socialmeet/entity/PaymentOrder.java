package com.example.socialmeet.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_orders")
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "order_no", nullable = false, unique = true, length = 64)
    private String orderNo;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "status")
    private String status = "PENDING";
    
    @Column(name = "paymentMethod")
    private String paymentMethod;
    
    @Column(name = "payment_no", length = 100)
    private String paymentNo;
    
    @Column(name = "callback_data", columnDefinition = "TEXT")
    private String callbackData;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderNo == null) {
            orderNo = generateOrderNo();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public PaymentOrder() {}
    
    public PaymentOrder(Long userId, String type, BigDecimal amount, String paymentMethod) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
    
    public enum PaymentMethod {
        ALIPAY, WECHAT
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentNo() {
        return paymentNo;
    }
    
    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }
    
    public String getCallbackData() {
        return callbackData;
    }
    
    public void setCallbackData(String callbackData) {
        this.callbackData = callbackData;
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
    
    // Business methods
    private String generateOrderNo() {
        return "ORDER" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }
    
    public boolean isSuccess() {
        return status == "SUCCESS";
    }
    
    public boolean isPending() {
        return status == "PENDING";
    }
}
