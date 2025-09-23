package com.example.myapplication.util

import android.util.Log
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong

/**
 * EGL渲染性能监控工具
 * 用于监控和优化GPU渲染性能
 */
object EGLPerformanceMonitor {
    
    private const val TAG = "EGLPerformanceMonitor"
    private const val MAX_SAMPLES = 100 // 最大样本数
    
    // 性能统计数据
    private val renderTimes = ConcurrentLinkedQueue<Long>()
    private val totalFrames = AtomicLong(0)
    private val droppedFrames = AtomicLong(0)
    private val lastFrameTime = AtomicLong(0)
    
    // 性能阈值
    private const val TARGET_FRAME_TIME_MS = 16L // 60fps目标帧时间
    private const val DROPPED_FRAME_THRESHOLD_MS = 33L // 掉帧阈值
    
    /**
     * 记录渲染时间
     */
    fun recordRenderTime(renderTimeMs: Long) {
        // 过滤异常值（超过500ms的渲染时间通常是异常）
        if (renderTimeMs > 500) {
            Log.w(TAG, "检测到高渲染时间: ${renderTimeMs}ms")
            // 仍然记录，但标记为异常
        }
        
        totalFrames.incrementAndGet()
        
        // 添加到样本队列
        renderTimes.offer(renderTimeMs)
        
        // 保持样本数量在限制范围内
        while (renderTimes.size > MAX_SAMPLES) {
            renderTimes.poll()
        }
        
        // 检查是否掉帧
        if (renderTimeMs > DROPPED_FRAME_THRESHOLD_MS) {
            droppedFrames.incrementAndGet()
            Log.w(TAG, "检测到掉帧: ${renderTimeMs}ms")
        }
        
        // 记录当前帧时间
        lastFrameTime.set(renderTimeMs)
        
        // 每100帧输出一次统计信息
        if (totalFrames.get() % 100 == 0L) {
            logPerformanceStats()
        }
    }
    
    /**
     * 获取平均渲染时间
     */
    fun getAverageRenderTime(): Double {
        if (renderTimes.isEmpty()) return 0.0
        
        val sum = renderTimes.sum()
        return sum.toDouble() / renderTimes.size
    }
    
    /**
     * 获取最大渲染时间
     */
    fun getMaxRenderTime(): Long {
        return renderTimes.maxOrNull() ?: 0L
    }
    
    /**
     * 获取最小渲染时间
     */
    fun getMinRenderTime(): Long {
        return renderTimes.minOrNull() ?: 0L
    }
    
    /**
     * 获取掉帧率
     */
    fun getDroppedFrameRate(): Double {
        val total = totalFrames.get()
        if (total == 0L) return 0.0
        return droppedFrames.get().toDouble() / total * 100.0
    }
    
    /**
     * 获取当前FPS
     */
    fun getCurrentFPS(): Double {
        val currentFrameTime = lastFrameTime.get()
        if (currentFrameTime == 0L) return 0.0
        return 1000.0 / currentFrameTime
    }
    
    /**
     * 检查性能是否正常
     */
    fun isPerformanceGood(): Boolean {
        val avgTime = getAverageRenderTime()
        val droppedRate = getDroppedFrameRate()
        
        return avgTime <= TARGET_FRAME_TIME_MS && droppedRate < 5.0
    }
    
    /**
     * 输出性能统计信息
     */
    private fun logPerformanceStats() {
        val avgTime = getAverageRenderTime()
        val maxTime = getMaxRenderTime()
        val minTime = getMinRenderTime()
        val droppedRate = getDroppedFrameRate()
        val currentFPS = getCurrentFPS()
        
        // 计算性能等级
        val performanceGrade = when {
            avgTime <= 16 -> "优秀"
            avgTime <= 33 -> "良好"
            avgTime <= 50 -> "一般"
            else -> "需要优化"
        }
        
        Log.i(TAG, """
            EGL性能统计:
            - 平均渲染时间: ${String.format("%.2f", avgTime)}ms
            - 最大渲染时间: ${maxTime}ms
            - 最小渲染时间: ${minTime}ms
            - 掉帧率: ${String.format("%.2f", droppedRate)}%
            - 当前FPS: ${String.format("%.1f", currentFPS)}
            - 总帧数: ${totalFrames.get()}
            - 掉帧数: ${droppedFrames.get()}
            - 性能等级: $performanceGrade
            - 性能状态: ${if (isPerformanceGood()) "良好" else "需要优化"}
        """.trimIndent())
    }
    
    /**
     * 重置统计数据
     */
    fun resetStats() {
        renderTimes.clear()
        totalFrames.set(0)
        droppedFrames.set(0)
        lastFrameTime.set(0)
        Log.i(TAG, "性能统计数据已重置")
    }
    
    /**
     * 获取性能建议
     */
    fun getPerformanceRecommendations(): List<String> {
        val recommendations = mutableListOf<String>()
        val avgTime = getAverageRenderTime()
        val droppedRate = getDroppedFrameRate()
        
        if (avgTime > TARGET_FRAME_TIME_MS) {
            recommendations.add("平均渲染时间过高，建议降低渲染分辨率或优化着色器")
        }
        
        if (droppedRate > 5.0) {
            recommendations.add("掉帧率过高，建议减少同时渲染的对象数量")
        }
        
        if (getMaxRenderTime() > 100) {
            recommendations.add("存在极端渲染时间，建议检查是否有阻塞操作")
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("性能表现良好，无需优化")
        }
        
        return recommendations
    }
}
