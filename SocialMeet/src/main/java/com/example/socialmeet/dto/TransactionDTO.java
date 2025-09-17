package com.example.socialmeet.dto;

import com.example.socialmeet.entity.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {
    private Long id;
    private Long userId;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String description;
    private String status;
    private Long relatedId;
    private LocalDateTime createdAt;
    
    // Constructors
    public TransactionDTO() {}
    
    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.userId = transaction.getUserId();
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.balanceAfter = transaction.getBalanceAfter();
        this.description = transaction.getDescription();
        this.status = transaction.getStatus();
        this.relatedId = transaction.getRelatedId();
        this.createdAt = transaction.getCreatedAt();
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
    
    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Long getRelatedId() {
        return relatedId;
    }
    
    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
