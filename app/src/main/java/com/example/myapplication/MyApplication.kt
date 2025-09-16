package com.example.myapplication

import android.app.Application
import android.content.Context
import com.example.myapplication.utils.ScreenAdaptUtil

/**
 * 应用程序入口类
 * 负责初始化屏幕适配等全局配置
 */
class MyApplication : Application() {
    
    companion object {
        private lateinit var instance: MyApplication
        
        fun getInstance(): MyApplication = instance
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 初始化屏幕适配
        initScreenAdapt()
    }
    
    /**
     * 初始化屏幕适配
     * 使用今日头条适配方案
     */
    private fun initScreenAdapt() {
        // 这里可以添加其他初始化逻辑
        // 屏幕适配会在Activity创建时调用
    }
    
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // 如果需要在这里进行屏幕适配，可以添加相关代码
    }
}
