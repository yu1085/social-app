package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 虚拟货币交易记录实体类
 */
@Entity
@Table(name = "currency_transactions")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class CurrencyTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "currency_type", length = 20, nullable = false)
    private String currencyType;
    
    @Column(name = "transaction_type", length = 20, nullable = false)
    private String transactionType; // EARN, SPEND, FREEZE, UNFREEZE, TRANSFER
    
    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "balance_before", precision = 15, scale = 2)
    private BigDecimal balanceBefore;
    
    @Column(name = "balance_after", precision = 15, scale = 2)
    private BigDecimal balanceAfter;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "related_id")
    private Long relatedId; // 关联的业务ID (如礼物ID、订单ID等)
    
    @Column(name = "related_type", length = 50)
    private String relatedType; // 关联的业务类型
    
    @Column(name = "status", length = 20)
    private String status = "SUCCESS"; // SUCCESS, FAILED, PENDING
    
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 交易类型枚举
    public enum TransactionType {
        EARN("获得", "通过充值、活动等方式获得货币"),
        SPEND("消费", "购买礼物、道具等消费货币"),
        FREEZE("冻结", "冻结部分货币"),
        UNFREEZE("解冻", "解冻冻结的货币"),
        TRANSFER("转账", "向其他用户转账"),
        REFUND("退款", "退款到账户"),
        BONUS("奖励", "系统奖励"),
        PENALTY("扣除", "系统扣除");
        
        private final String displayName;
        private final String description;
        
        TransactionType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 构造函数
    public CurrencyTransaction() {}
    
    public CurrencyTransaction(Long userId, String currencyType, String transactionType, 
                             BigDecimal amount, String description) {
        this.userId = userId;
        this.currencyType = currencyType;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
    }
    
    public CurrencyTransaction(Long userId, String currencyType, String transactionType, 
                             BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, 
                             String description, Long relatedId, String relatedType) {
        this.userId = userId;
        this.currencyType = currencyType;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.description = description;
        this.relatedId = relatedId;
        this.relatedType = relatedType;
    }
}
