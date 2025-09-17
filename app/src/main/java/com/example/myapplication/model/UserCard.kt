package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

/**
 * 用户卡片数据模型
 * 用于首页用户卡片显示
 */
data class UserCard(
    @SerializedName("id")
    val id: Long = 0,
    
    @SerializedName("nickname")
    val nickname: String = "",
    
    @SerializedName("avatar")
    val avatar: String = "",
    
    @SerializedName("age")
    val age: Int = 0,
    
    @SerializedName("location")
    val location: String = "",
    
    @SerializedName("bio")
    val bio: String = "",
    
    @SerializedName("isOnline")
    val isOnline: Boolean = false,
    
    @SerializedName("status")
    val status: String = "",
    
    @SerializedName("statusColor")
    val statusColor: String = "",
    
    @SerializedName("callPrice")
    val callPrice: Int = 0,
    
    @SerializedName("messagePrice")
    val messagePrice: Int = 0,
    
    @SerializedName("gender")
    val gender: String = ""
) {
    /**
     * 获取状态显示文本
     */
    fun getStatusText(): String {
        return when (status) {
            "空闲" -> "空闲"
            "忙碌" -> "忙碌"
            "在线" -> "在线"
            "离线" -> "离线"
            else -> if (isOnline) "在线" else "离线"
        }
    }
    
    /**
     * 获取状态颜色
     */
    fun getStatusColor(): String {
        return when (statusColor) {
            "green" -> "#4CAF50"
            "orange" -> "#FF9800"
            "red" -> "#F44336"
            "gray" -> "#9E9E9E"
            else -> if (isOnline) "#4CAF50" else "#9E9E9E"
        }
    }
    
    /**
     * 获取价格显示文本
     */
    fun getPriceText(): String {
        return "${callPrice}/分钟"
    }
    
    /**
     * 获取年龄显示文本
     */
    fun getAgeText(): String {
        return "${age}岁"
    }
    
    /**
     * 获取用户显示名称
     */
    fun getDisplayName(): String {
        return nickname.ifEmpty { "用户${id}" }
    }
}
