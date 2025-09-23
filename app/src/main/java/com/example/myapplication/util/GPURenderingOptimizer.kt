package com.example.myapplication.util

import android.app.Activity
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.WindowManager

/**
 * GPU渲染优化工具
 * 提供各种GPU渲染优化策略
 */
object GPURenderingOptimizer {
    
    private const val TAG = "GPURenderingOptimizer"
    
    /**
     * 优化Activity的GPU渲染设置
     */
    fun optimizeActivityRendering(activity: Activity) {
        try {
            // 启用硬件加速
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            )
            
            // 优化渲染性能
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            
            Log.d(TAG, "Activity GPU渲染优化已启用")
        } catch (e: Exception) {
            Log.e(TAG, "Activity GPU渲染优化失败", e)
        }
    }
    
    /**
     * 优化GLSurfaceView的渲染设置
     */
    fun optimizeGLSurfaceView(glSurfaceView: GLSurfaceView) {
        try {
            // 设置OpenGL ES版本
            glSurfaceView.setEGLContextClientVersion(2)
            
            // 启用深度测试
            glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
            
            // 设置渲染模式为连续渲染
            glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            
            Log.d(TAG, "GLSurfaceView渲染优化已启用")
        } catch (e: Exception) {
            Log.e(TAG, "GLSurfaceView渲染优化失败", e)
        }
    }
    
    /**
     * 获取GPU渲染性能建议
     */
    fun getRenderingRecommendations(): List<String> {
        val recommendations = mutableListOf<String>()
        
        // 检查系统GPU信息
        val gpuInfo = getGPUInfo()
        if (gpuInfo.isNotEmpty()) {
            recommendations.add("GPU信息: $gpuInfo")
        }
        
        // 通用优化建议
        recommendations.addAll(listOf(
            "启用硬件加速",
            "使用合适的纹理格式",
            "减少绘制调用次数",
            "使用对象池避免频繁GC",
            "优化着色器代码",
            "使用LOD系统减少远距离对象细节",
            "启用视锥剔除",
            "使用批处理减少状态切换"
        ))
        
        return recommendations
    }
    
    /**
     * 获取GPU信息
     */
    private fun getGPUInfo(): String {
        return try {
            val renderer = android.opengl.GLES20.glGetString(android.opengl.GLES20.GL_RENDERER)
            val vendor = android.opengl.GLES20.glGetString(android.opengl.GLES20.GL_VENDOR)
            val version = android.opengl.GLES20.glGetString(android.opengl.GLES20.GL_VERSION)
            
            "Renderer: $renderer, Vendor: $vendor, Version: $version"
        } catch (e: Exception) {
            "无法获取GPU信息: ${e.message}"
        }
    }
    
    /**
     * 优化内存使用
     */
    fun optimizeMemoryUsage() {
        try {
            // 建议系统进行垃圾回收
            System.gc()
            
            // 获取当前内存使用情况
            val runtime = Runtime.getRuntime()
            val totalMemory = runtime.totalMemory()
            val freeMemory = runtime.freeMemory()
            val usedMemory = totalMemory - freeMemory
            
            Log.d(TAG, "内存使用情况: 已使用 ${usedMemory / 1024 / 1024}MB, 总计 ${totalMemory / 1024 / 1024}MB")
            
            // 如果内存使用率过高，建议清理
            val memoryUsagePercent = (usedMemory.toFloat() / totalMemory * 100).toInt()
            if (memoryUsagePercent > 80) {
                Log.w(TAG, "内存使用率过高: ${memoryUsagePercent}%, 建议清理缓存")
            }
        } catch (e: Exception) {
            Log.e(TAG, "内存优化失败", e)
        }
    }
    
    /**
     * 检查渲染性能
     */
    fun checkRenderingPerformance(): RenderingPerformanceInfo {
        val avgRenderTime = EGLPerformanceMonitor.getAverageRenderTime()
        val maxRenderTime = EGLPerformanceMonitor.getMaxRenderTime()
        val droppedFrameRate = EGLPerformanceMonitor.getDroppedFrameRate()
        val currentFPS = EGLPerformanceMonitor.getCurrentFPS()
        
        return RenderingPerformanceInfo(
            averageRenderTime = avgRenderTime,
            maxRenderTime = maxRenderTime,
            droppedFrameRate = droppedFrameRate,
            currentFPS = currentFPS,
            isPerformanceGood = EGLPerformanceMonitor.isPerformanceGood()
        )
    }
    
    /**
     * 渲染性能信息数据类
     */
    data class RenderingPerformanceInfo(
        val averageRenderTime: Double,
        val maxRenderTime: Long,
        val droppedFrameRate: Double,
        val currentFPS: Double,
        val isPerformanceGood: Boolean
    )
}
