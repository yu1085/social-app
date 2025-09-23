package com.example.myapplication.model

import com.example.myapplication.R
import java.math.BigDecimal

// RechargePackage 定义在 RechargePackage.kt 文件中

// PaymentMethod 定义在 PaymentMethod.kt 文件中

// RechargeOrder 定义在 RechargeOrder.kt 文件中

// OrderStatus 定义在 OrderStatus.kt 文件中

// WalletBalance 定义在 WalletBalance.kt 文件中

/**
 * 充值等级信息
 */
data class RechargeLevel(
    val levelName: String,
    val currentProgress: Int,
    val maxProgress: Int,
    val benefits: List<String> = emptyList()
)

/**
 * 交易记录
 */
data class TransactionRecord(
    val id: String,
    val type: TransactionType,
    val amount: Long,
    val description: String,
    val timestamp: Long,
    val status: TransactionStatus
)

enum class TransactionType {
    RECHARGE,   // 充值
    CONSUME,    // 消费
    GIFT,       // 赠送
    EARN,       // 收益
    EXCHANGE    // 兑换
}

enum class TransactionStatus {
    SUCCESS,
    PENDING,
    FAILED
}
