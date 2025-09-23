package com.example.myapplication.model

/**
 * 财富等级规则
 */
data class WealthLevelRule(
    val levelName: String,
    val minWealthValue: Int,
    val maxWealthValue: Int?,
    val privileges: List<String>,
    val description: String
)
