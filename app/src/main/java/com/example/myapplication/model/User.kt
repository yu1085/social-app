package com.example.myapplication.model

import androidx.annotation.DrawableRes

/**
 * 用户数据模型
 */
data class User(
    val id: String,
    val name: String,
    val avatar: Any? = null,
    val age: Int,
    val distance: Double,
    val status: UserStatus,
    val isOnline: Boolean = false,
    val isVerified: Boolean = false
)

/**
 * 用户状态枚举
 */
enum class UserStatus(val displayName: String, val color: String) {
    FREE("空闲", "#9CE8AF"),
    BUSY("忙碌", "#DC7455"),
    OFFLINE("离线", "#E0E0E0")
}

/**
 * 动态数据模型
 */
data class Dynamic(
    val id: String,
    val user: User,
    val content: String,
    val images: List<Any> = emptyList(),
    val publishTime: String,
    val location: String,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false,
    val isFreeMinute: Boolean = false
)

/**
 * 底部导航项数据模型
 */
data class BottomNavItem(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int,
    val isSelected: Boolean = false
)
