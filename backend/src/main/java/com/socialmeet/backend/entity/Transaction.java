package com.socialmeet.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录实体
 */
@Entity
@Table(name = "transactions")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    private TransactionType transactionType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "coin_source", nullable = false, length = 50)
    private CoinSource coinSource;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "coin_amount", nullable = false)
    private BigDecimal coinAmount;

    @Column(name = "balance_before", precision = 10, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 10, scale = 2)
    private BigDecimal balanceAfter;
    
    @Column(name = "wealth_value", nullable = false)
    private Integer wealthValue = 0;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "order_id")
    private String orderId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status = TransactionStatus.SUCCESS;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 交易类型枚举
    public enum TransactionType {
        RECHARGE,       // 充值
        CONSUME,        // 消费
        PURCHASE,       // 购买
        REFUND,         // 退款
        BONUS,          // 赠送
        VIP_PURCHASE,   // VIP购买
        ACTIVITY,       // 活动
        CALL_CHARGE,    // 通话扣费
        CALL_INCOME     // 通话收入
    }

    // 聊币来源枚举
    public enum CoinSource {
        PURCHASED,      // 购买（计算财富值）
        BONUS_RECHARGE, // 充值赠送（不计算财富值）
        BONUS_ACTIVITY, // 活动赠送（不计算财富值）
        VIP_PURCHASE,   // VIP购买（不计算财富值）
        REFUND,         // 退款（不计算财富值）
        CONSUMED,       // 消费（通话扣费）
        EARNED          // 收入（通话收入）
    }

    // 交易状态枚举
    public enum TransactionStatus {
        SUCCESS,        // 成功
        PENDING,        // 待处理
        FAILED,         // 失败
        CANCELLED       // 已取消
    }
}
