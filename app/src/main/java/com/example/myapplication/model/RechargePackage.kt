package com.example.myapplication.model

import java.math.BigDecimal

/**
 * 充值套餐数据类
 */
data class RechargePackage(
    val id: String,
    val coins: Long,
    val price: BigDecimal,
    val isRecommended: Boolean = false,
    val name: String? = null,
    val description: String? = null,
    val originalPrice: BigDecimal? = null,
    val discount: String? = null
) {
    /**
     * 获取显示名称
     */
    fun getDisplayName(): String {
        return name ?: "${coins}金币"
    }
    
    /**
     * 获取价格显示文本
     */
    fun getPriceText(): String {
        return "¥${price}"
    }
    
    /**
     * 获取金币显示文本
     */
    fun getCoinsText(): String {
        return "${coins}金币"
    }
    
    /**
     * 是否有折扣
     */
    fun hasDiscount(): Boolean {
        return originalPrice != null && originalPrice > price
    }
    
    /**
     * 获取折扣文本
     */
    fun getDiscountText(): String? {
        return if (hasDiscount()) {
            val discountPercent = ((originalPrice!! - price) / originalPrice * BigDecimal(100)).toInt()
            "省${discountPercent}%"
        } else {
            discount
        }
    }
}

