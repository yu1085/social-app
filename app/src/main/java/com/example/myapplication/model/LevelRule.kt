package com.example.myapplication.model

/**
 * 等级规则
 */
data class LevelRule(
    val levelName: String,
    val levelIcon: String,
    val levelColor: String,
    val minWealthValue: Int,
    val maxWealthValue: Int?,
    val description: String
)
