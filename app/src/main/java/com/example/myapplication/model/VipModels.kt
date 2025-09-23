package com.example.myapplication.model

import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime
import com.google.gson.annotations.SerializedName

/**
 * VIP等级数据模型
 */
data class VipLevel(
    val id: Long? = null,
    val name: String,
    val level: Int,
    val price: BigDecimal,
    val duration: Int, // 有效期（天数）
    val benefits: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) : Serializable

/**
 * VIP订阅数据模型
 */
data class VipSubscription(
    val id: Long? = null,
    val userId: Long,
    val vipLevelId: Long,
    @SerializedName("startDate")
    val startDate: String, // 改为String类型以匹配后端返回的格式
    @SerializedName("endDate")
    val endDate: String, // 改为String类型以匹配后端返回的格式
    val amount: BigDecimal,
    val isActive: Boolean = true,
    @SerializedName("createdAt")
    val createdAt: String? = null, // 改为String类型
    @SerializedName("updatedAt")
    val updatedAt: String? = null, // 改为String类型
    val vipLevelName: String? = null,
    val vipLevel: Int? = null
) : Serializable

/**
 * VIP特权数据模型
 */
data class VipPrivilege(
    val id: Long? = null,
    val vipLevelId: Long,
    val privilegeType: String,
    val privilegeName: String,
    val privilegeDescription: String,
    val privilegeValue: String,
    val sortOrder: Int = 0,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) : Serializable

/**
 * VIP等级DTO
 */
data class VipLevelDTO(
    val id: Long,
    val name: String,
    val level: Int,
    val price: BigDecimal,
    val duration: Int,
    val benefits: String?,
    val isActive: Boolean
) : Serializable

/**
 * VIP订阅DTO
 */
data class VipSubscriptionDTO(
    val id: Long,
    val userId: Long,
    val vipLevelId: Long,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val amount: BigDecimal,
    val isActive: Boolean,
    val vipLevelName: String? = null,
    val vipLevel: Int? = null
) : Serializable
