package com.example.myapplication.utils

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * 今日头条屏幕适配方案
 * 核心思想：以屏幕宽度为基准，等比例缩放所有尺寸
 */
object ScreenAdaptUtil {
    
    // 设计稿基准宽度（dp）
    private const val DESIGN_WIDTH_DP = 360f
    
    // 设计稿基准密度
    private var sNoncompatDensity = 0f
    private var sNoncompatScaledDensity = 0f
    
    /**
     * 初始化屏幕适配
     * 在Application的onCreate中调用
     */
    fun setCustomDensity(activity: Activity, application: Application) {
        val appDisplayMetrics = application.resources.displayMetrics
        
        if (sNoncompatDensity == 0f) {
            sNoncompatDensity = appDisplayMetrics.density
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity
            
            // 监听字体大小变化
            application.registerComponentCallbacks(object : ComponentCallbacks {
                override fun onConfigurationChanged(newConfig: Configuration) {
                    if (newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.resources.displayMetrics.scaledDensity
                    }
                }
                
                override fun onLowMemory() {}
            })
        }
        
        // 计算目标密度
        val targetDensity = appDisplayMetrics.widthPixels / DESIGN_WIDTH_DP
        val targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity)
        val targetDensityDpi = (targetDensity * 160).toInt()
        
        // 设置Activity的密度
        val activityDisplayMetrics = activity.resources.displayMetrics
        activityDisplayMetrics.density = targetDensity
        activityDisplayMetrics.scaledDensity = targetScaledDensity
        activityDisplayMetrics.densityDpi = targetDensityDpi
        
        // 设置Application的密度
        appDisplayMetrics.density = targetDensity
        appDisplayMetrics.scaledDensity = targetScaledDensity
        appDisplayMetrics.densityDpi = targetDensityDpi
    }
    
    /**
     * 获取适配后的尺寸
     * @param designSize 设计稿尺寸（dp）
     * @return 适配后的尺寸（dp）
     */
    fun getAdaptSize(designSize: Float): Float {
        return designSize
    }
    
    /**
     * 获取屏幕宽度（dp）
     */
    fun getScreenWidthDp(activity: Activity): Float {
        val displayMetrics = DisplayMetrics()
        val windowManager = activity.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels / displayMetrics.density
    }
    
    /**
     * 获取屏幕高度（dp）
     */
    fun getScreenHeightDp(activity: Activity): Float {
        val displayMetrics = DisplayMetrics()
        val windowManager = activity.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels / displayMetrics.density
    }
    
    /**
     * 获取状态栏高度（px）
     */
    fun getStatusBarHeight(activity: Activity): Int {
        val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) activity.resources.getDimensionPixelSize(resourceId) else 0
    }
    
    /**
     * 获取导航栏高度（px）
     */
    fun getNavigationBarHeight(activity: Activity): Int {
        val resourceId = activity.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) activity.resources.getDimensionPixelSize(resourceId) else 0
    }
}
