package com.example.myapplication.model

import java.math.BigDecimal

/**
 * 创建订单请求数据类
 */
data class CreateOrderRequest(
    val packageId: String,
    val coins: Long,
    val amount: BigDecimal,
    val paymentMethod: String,
    val description: String? = null
)
