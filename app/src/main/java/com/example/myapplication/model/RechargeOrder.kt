package com.example.myapplication.model

import java.math.BigDecimal

/**
 * 充值订单数据类
 */
data class RechargeOrder(
    val orderId: String,
    val userId: Long,
    val packageId: String,
    val coins: Long,
    val amount: BigDecimal,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val createdAt: Long,
    val updatedAt: Long? = null,
    val paidAt: Long? = null,
    val expiredAt: Long? = null,
    val description: String? = null,
    val transactionId: String? = null,
    val failureReason: String? = null
) {
    /**
     * 获取状态显示文本
     */
    fun getStatusText(): String {
        return when (status) {
            OrderStatus.PENDING -> "待支付"
            OrderStatus.PROCESSING -> "处理中"
            OrderStatus.SUCCESS -> "成功"
            OrderStatus.PAID -> "已支付"
            OrderStatus.EXPIRED -> "已过期"
            OrderStatus.CANCELLED -> "已取消"
            OrderStatus.FAILED -> "支付失败"
        }
    }
    
    /**
     * 获取支付方式显示文本
     */
    fun getPaymentMethodText(): String {
        return when (paymentMethod) {
            PaymentMethod.ALIPAY -> "支付宝"
            PaymentMethod.WECHAT -> "微信支付"
        }
    }
    
    /**
     * 是否已支付
     */
    fun isPaid(): Boolean {
        return status == OrderStatus.PAID
    }
    
    /**
     * 是否已过期
     */
    fun isExpired(): Boolean {
        return status == OrderStatus.EXPIRED
    }
    
    /**
     * 是否可以支付
     */
    fun canPay(): Boolean {
        return status == OrderStatus.PENDING
    }
}

