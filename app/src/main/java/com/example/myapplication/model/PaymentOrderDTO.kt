package com.example.myapplication.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 支付订单DTO
 */
data class PaymentOrderDTO(
    val id: Long,
    val orderId: String,
    val userId: Long,
    val packageId: String,
    val coins: Long,
    val amount: BigDecimal,
    val paymentMethod: String,
    val alipayTradeNo: String?,
    val alipayOutTradeNo: String?,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val paidAt: LocalDateTime?,
    val expiredAt: LocalDateTime?,
    val description: String?,
    val transactionId: String?,
    val failureReason: String?
)
