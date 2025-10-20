package com.example.myapplication.dto;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包信息DTO
 */
public class WalletDTO {
    
    @SerializedName("userId")
    private Long userId;
    
    @SerializedName("balance")
    private BigDecimal balance;
    
    @SerializedName("totalRecharge")
    private BigDecimal totalRecharge;
    
    @SerializedName("totalConsume")
    private BigDecimal totalConsume;
    
    @SerializedName("transactionCount")
    private Integer transactionCount;
    
    @SerializedName("lastTransactionAt")
    private String lastTransactionAt;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;
    
    // 构造函数
    public WalletDTO() {}
    
    public WalletDTO(Long userId, BigDecimal balance, BigDecimal totalRecharge, BigDecimal totalConsume, 
                    Integer transactionCount, String lastTransactionAt, 
                    String createdAt, String updatedAt) {
        this.userId = userId;
        this.balance = balance;
        this.totalRecharge = totalRecharge;
        this.totalConsume = totalConsume;
        this.transactionCount = transactionCount;
        this.lastTransactionAt = lastTransactionAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter和Setter方法
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public BigDecimal getTotalRecharge() { return totalRecharge; }
    public void setTotalRecharge(BigDecimal totalRecharge) { this.totalRecharge = totalRecharge; }
    
    public BigDecimal getTotalConsume() { return totalConsume; }
    public void setTotalConsume(BigDecimal totalConsume) { this.totalConsume = totalConsume; }
    
    public Integer getTransactionCount() { return transactionCount; }
    public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }
    
    public String getLastTransactionAt() { return lastTransactionAt; }
    public void setLastTransactionAt(String lastTransactionAt) { this.lastTransactionAt = lastTransactionAt; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
