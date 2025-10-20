package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

/**
 * 靓号模型类
 */
@Parcelize
data class LuckyNumber(
    val id: Long = 0,
    val number: String = "",
    val price: BigDecimal = BigDecimal.ZERO,
    val tier: LuckyNumberTier = LuckyNumberTier.NORMAL,
    val status: LuckyNumberStatus = LuckyNumberStatus.AVAILABLE,
    val ownerId: Long? = null,
    val purchaseTime: String? = null,
    val validityDays: Int? = null,
    val expireTime: String? = null,
    val description: String = "",
    val isSpecial: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
) : Parcelable {
    // 靓号等级枚举
    enum class LuckyNumberTier(val displayName: String, val sortOrder: Int, val iconColor: String, val icon: String) {
        NORMAL("普通", 4, "#9E9E9E", "🔢"),
        LIMITED("限量", 1, "#FF9800", "⭐"),
        SUPER("超级", 2, "#E91E63", "💎"),
        TOP_TIER("顶级", 3, "#9C27B0", "👑")
    }
    
    // 靓号状态枚举
    enum class LuckyNumberStatus(val displayName: String) {
        AVAILABLE("可购买"),
        SOLD("已售出"),
        RESERVED("已预订"),
        EXPIRED("已过期")
    }
    
    // 从字符串创建等级
    companion object {
        fun tierFromString(tierString: String): LuckyNumberTier {
            return when (tierString.uppercase()) {
                "LIMITED" -> LuckyNumberTier.LIMITED
                "SUPER" -> LuckyNumberTier.SUPER
                "TOP_TIER" -> LuckyNumberTier.TOP_TIER
                else -> LuckyNumberTier.NORMAL
            }
        }
        
        fun statusFromString(statusString: String): LuckyNumberStatus {
            return when (statusString.uppercase()) {
                "SOLD" -> LuckyNumberStatus.SOLD
                "RESERVED" -> LuckyNumberStatus.RESERVED
                "EXPIRED" -> LuckyNumberStatus.EXPIRED
                else -> LuckyNumberStatus.AVAILABLE
            }
        }
    }
}
