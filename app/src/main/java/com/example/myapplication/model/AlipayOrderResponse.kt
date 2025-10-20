package com.example.myapplication.model

/**
 * 支付宝订单响应数据类
 */
data class AlipayOrderResponse(
    val orderId: String,
    val alipayOrderInfo: String,
    val alipayOutTradeNo: String,
    val qrCode: String? = null,
    val payUrl: String? = null,
    val expireTime: Long
)
