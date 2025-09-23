package com.example.myapplication.model

import java.math.BigDecimal

import java.io.Serializable

/**
 * 靓号数据模型
 */
data class LuckyNumber(
    val id: Long,
    val number: String,
    val tier: LuckyNumberTier,
    val price: Long,
    val isLimited: Boolean = false,
    val isAvailable: Boolean = true,
    val description: String? = null,
    val icon: String = "靓",
    val iconColor: String = "#FFD700"
) : Serializable

/**
 * 靓号等级枚举
 */
enum class LuckyNumberTier(
    val displayName: String,
    val sortOrder: Int,
    val color: String,
    val iconColor: String
) : Serializable {
    LIMITED("限量", 1, "#FF6B6B", "#FFD700"),      // 限量 - 最高优先级
    TOP("顶级", 2, "#9C27B0", "#E1BEE7"),         // 顶级
    SUPER("超级", 3, "#673AB7", "#C5CAE9"),       // 超级
    NORMAL("普通", 4, "#FF9800", "#FFE0B2")       // 普通 - 最低优先级
}

/**
 * 靓号分类
 */
enum class LuckyNumberCategory(
    val displayName: String,
    val icon: String
) {
    LUCKY_NUMBERS("靓号", "靓"),
    ENTRANCE_EFFECTS("进场特效", "✨"),
    JEWELRY("首饰", "💎")
}

/**
 * 靓号购买结果
 */
sealed class LuckyNumberPurchaseResult {
    data class Success(val luckyNumber: LuckyNumber) : LuckyNumberPurchaseResult()
    data class Error(val message: String) : LuckyNumberPurchaseResult()
    data class InsufficientBalance(val required: Long, val current: Long) : LuckyNumberPurchaseResult()
    data class AlreadyOwned(val luckyNumber: LuckyNumber) : LuckyNumberPurchaseResult()
}

/**
 * 靓号筛选条件
 */
data class LuckyNumberFilter(
    val tier: LuckyNumberTier? = null,
    val maxPrice: Long? = null,
    val minPrice: Long? = null,
    val isLimited: Boolean? = null,
    val isAvailable: Boolean? = true
)

/**
 * 靓号排序方式
 */
enum class LuckyNumberSortBy {
    TIER,           // 按等级排序
    PRICE_ASC,      // 按价格升序
    PRICE_DESC,     // 按价格降序
    NUMBER_ASC,     // 按号码升序
    NUMBER_DESC     // 按号码降序
}
