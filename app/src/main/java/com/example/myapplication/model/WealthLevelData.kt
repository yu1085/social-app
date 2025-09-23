package com.example.myapplication.model

/**
 * 财富等级数据
 */
data class WealthLevelData(
    val id: Long? = null,
    val userId: Long,
    val wealthValue: Int,
    val levelName: String,
    val levelIcon: String,
    val levelColor: String,
    val minWealthValue: Int? = null,
    val maxWealthValue: Int? = null,
    val nextLevelRequirement: Int?,
    val progressPercentage: Double,
    val nextLevelName: String?,
    val nextLevelIcon: String? = null,
    val nextLevelColor: String? = null,
    val privileges: List<PrivilegeType> = emptyList(),
    val userRank: Long? = null
)

/**
 * 特权类型
 */
enum class PrivilegeType(val displayName: String) {
    LUCKY_NUMBER_DISCOUNT("靓号折扣"),
    WEEKLY_PROMOTION("每周促销"),
    VIP_DISCOUNT("VIP折扣"),
    EFFECT_DISCOUNT("特效折扣"),
    FREE_VIP("免费VIP"),
    FREE_EFFECT("免费特效"),
    EXCLUSIVE_EFFECT("专属特效"),
    LUCKY_NUMBER_CUSTOM("靓号定制"),
    EXCLUSIVE_GIFT("专属礼物"),
    EXCLUSIVE_SERVICE("专属客服服务")
}
