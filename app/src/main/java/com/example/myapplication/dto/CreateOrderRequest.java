package com.example.myapplication.dto;

import java.math.BigDecimal;

/**
 * 创建订单请求DTO
 */
public class CreateOrderRequest {
    private String packageId;
    private Integer coins;
    private BigDecimal amount;
    private String paymentMethod;
    private String description;
    
    public CreateOrderRequest() {}
    
    public CreateOrderRequest(String packageId, Integer coins, BigDecimal amount, String paymentMethod, String description) {
        this.packageId = packageId;
        this.coins = coins;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.description = description;
    }
    
    public String getPackageId() {
        return packageId;
    }
    
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
    
    public Integer getCoins() {
        return coins;
    }
    
    public void setCoins(Integer coins) {
        this.coins = coins;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
