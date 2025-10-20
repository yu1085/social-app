package com.example.myapplication.model

import java.math.BigDecimal

/**
 * 靓号筛选条件
 */
data class LuckyNumberFilter(
    val tier: LuckyNumber.LuckyNumberTier? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null,
    val isSpecial: Boolean? = null,
    val status: LuckyNumber.LuckyNumberStatus? = null
)

/**
 * 靓号排序方式
 */
enum class LuckyNumberSortBy {
    TIER,      // 按等级排序
    PRICE,     // 按价格排序
    NUMBER,    // 按号码排序
    CREATED_AT // 按创建时间排序
}
