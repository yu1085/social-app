package com.example.socialmeet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值订单实体类
 */
@Entity
@Table(name = "recharge_orders")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class RechargeOrder {
    
    @Id
    @Column(length = 50)
    private String orderId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "package_id", length = 50)
    private String packageId;
    
    @Column(name = "coins", nullable = false)
    private Long coins;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_method", length = 20, nullable = false)
    private String paymentMethod; // ALIPAY, WECHAT
    
    @Column(name = "status", length = 20, nullable = false)
    private String status; // PENDING, PROCESSING, SUCCESS, FAILED, CANCELLED
    
    @Column(name = "third_party_order_id", length = 100)
    private String thirdPartyOrderId; // 第三方支付平台订单号
    
    @Column(name = "third_party_transaction_id", length = 100)
    private String thirdPartyTransactionId; // 第三方支付平台交易号
    
    @Column(name = "notify_url", length = 500)
    private String notifyUrl; // 支付回调地址
    
    @Column(name = "return_url", length = 500)
    private String returnUrl; // 支付返回地址
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt; // 支付完成时间
    
    @Column(name = "expired_at")
    private LocalDateTime expiredAt; // 订单过期时间
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public RechargeOrder() {}
    
    public RechargeOrder(String orderId, Long userId, String packageId, Long coins, 
                        BigDecimal amount, String paymentMethod, String description) {
        this.orderId = orderId;
        this.userId = userId;
        this.packageId = packageId;
        this.coins = coins;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = "PENDING";
        this.description = description;
        this.expiredAt = LocalDateTime.now().plusMinutes(30); // 30分钟过期
    }
}
