package com.example.myapplication.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Compose屏幕适配扩展函数
 * 基于今日头条适配方案
 */

/**
 * 设计稿基准宽度（dp）
 */
const val DESIGN_WIDTH_DP = 360f

/**
 * 获取适配后的尺寸
 * @param designSize 设计稿尺寸（dp）
 * @return 适配后的尺寸（dp）
 */
fun Float.adapt(): Dp {
    return (this * getAdaptRatio()).dp
}

/**
 * 获取适配后的尺寸
 * @param designSize 设计稿尺寸（dp）
 * @return 适配后的尺寸（dp）
 */
fun Int.adapt(): Dp {
    return (this * getAdaptRatio()).dp
}

/**
 * 获取适配比例
 */
private fun getAdaptRatio(): Float {
    // 这里可以通过全局变量或者系统API获取当前屏幕宽度
    // 暂时返回1.0，实际使用时需要动态计算
    // 实际项目中，这里应该从ScreenAdaptUtil获取适配比例
    return 1.0f
}

/**
 * 设计稿尺寸转换为适配尺寸
 * 使用方式：360.dp.adapt()
 */
fun Dp.adapt(): Dp {
    return (this.value * getAdaptRatio()).dp
}

/**
 * 常用尺寸的适配扩展
 */
object AdaptDimens {
    // 间距
    val spacing_4 = 4f.adapt()
    val spacing_8 = 8f.adapt()
    val spacing_12 = 12f.adapt()
    val spacing_16 = 16f.adapt()
    val spacing_20 = 20f.adapt()
    val spacing_24 = 24f.adapt()
    val spacing_32 = 32f.adapt()
    
    // 字体大小
    val text_12 = 12f.adapt()
    val text_14 = 14f.adapt()
    val text_16 = 16f.adapt()
    val text_18 = 18f.adapt()
    val text_20 = 20f.adapt()
    val text_24 = 24f.adapt()
    
    // 圆角
    val radius_8 = 8f.adapt()
    val radius_12 = 12f.adapt()
    val radius_16 = 16f.adapt()
    val radius_20 = 20f.adapt()
    
    // 图标尺寸
    val icon_16 = 16f.adapt()
    val icon_20 = 20f.adapt()
    val icon_24 = 24f.adapt()
    val icon_32 = 32f.adapt()
    val icon_48 = 48f.adapt()
}
