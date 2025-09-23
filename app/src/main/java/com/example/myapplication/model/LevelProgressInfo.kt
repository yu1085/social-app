package com.example.myapplication.model

/**
 * 等级进度信息
 */
data class LevelProgressInfo(
    val currentLevel: String,
    val currentWealthValue: Int,
    val currentLevelIcon: String,
    val currentLevelColor: String,
    val progressPercentage: Double,
    val nextLevelRequirement: Int?,
    val nextLevelName: String?,
    val nextLevelIcon: String?,
    val nextLevelColor: String?
)
