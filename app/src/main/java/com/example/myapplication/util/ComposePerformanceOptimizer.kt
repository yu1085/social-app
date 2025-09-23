package com.example.myapplication.util

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.delay

/**
 * Jetpack Compose性能优化工具
 * 提供各种性能优化策略和修饰符
 */
object ComposePerformanceOptimizer {
    
    /**
     * 优化Canvas绘制的修饰符
     * 启用硬件加速和优化渲染
     */
    fun optimizedCanvasModifier(): Modifier {
        return Modifier.graphicsLayer {
            // 启用硬件加速
            compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
        }
    }
    
    /**
     * 防抖动的状态管理
     * 避免频繁的状态更新导致重组
     */
    @Composable
    fun <T> rememberDebounced(
        key: T,
        delayMs: Long = 300L
    ): T {
        var debouncedValue by remember { mutableStateOf(key) }
        
        LaunchedEffect(key) {
            delay(delayMs)
            debouncedValue = key
        }
        
        return debouncedValue
    }
    
    /**
     * 节流的状态更新
     * 限制状态更新频率
     */
    @Composable
    fun <T> rememberThrottled(
        key: T,
        throttleMs: Long = 100L
    ): T {
        var throttledValue by remember { mutableStateOf(key) }
        var lastUpdateTime by remember { mutableStateOf(0L) }
        
        LaunchedEffect(key) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastUpdateTime >= throttleMs) {
                throttledValue = key
                lastUpdateTime = currentTime
            }
        }
        
        return throttledValue
    }
    
    /**
     * 优化的图片加载修饰符
     * 减少重绘和内存使用
     */
    fun optimizedImageModifier(): Modifier {
        return Modifier.graphicsLayer {
            // 启用硬件加速
            compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
        }
    }
    
    /**
     * 内存优化的列表项修饰符
     * 用于长列表的性能优化
     */
    fun optimizedListItemModifier(): Modifier {
        return Modifier.graphicsLayer {
            // 启用硬件加速
            compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
        }
    }
}

/**
 * 性能优化的状态管理Hook
 */
@Composable
fun useOptimizedState(
    initialValue: Boolean,
    debounceMs: Long = 100L
): Pair<Boolean, (Boolean) -> Unit> {
    var state by remember { mutableStateOf(initialValue) }
    var pendingValue by remember { mutableStateOf<Boolean?>(null) }
    
    // 防抖更新
    LaunchedEffect(pendingValue) {
        pendingValue?.let { value ->
            delay(debounceMs)
            state = value
            pendingValue = null
        }
    }
    
    val setState = { newValue: Boolean ->
        pendingValue = newValue
    }
    
    return Pair(state, setState)
}

/**
 * 性能优化的动画Hook
 */
@Composable
fun useOptimizedAnimation(
    targetValue: Float,
    animationDuration: Int = 300
): Float {
    var animatedValue by remember { mutableStateOf(targetValue) }
    
    LaunchedEffect(targetValue) {
        // 使用协程实现平滑动画，避免阻塞UI线程
        val startValue = animatedValue
        val diff = targetValue - startValue
        val steps = animationDuration / 16 // 60fps
        val stepSize = diff / steps
        
        repeat(steps.toInt()) {
            animatedValue += stepSize
            delay(16) // 60fps
        }
        animatedValue = targetValue
    }
    
    return animatedValue
}

/**
 * 内存优化的图片缓存
 */
object ImageCacheManager {
    private val cache = mutableMapOf<String, Any>()
    private val maxCacheSize = 50
    
    fun <T> get(key: String): T? {
        return cache[key] as? T
    }
    
    fun put(key: String, value: Any) {
        if (cache.size >= maxCacheSize) {
            // 移除最旧的缓存项
            val firstKey = cache.keys.first()
            cache.remove(firstKey)
        }
        cache[key] = value
    }
    
    fun clear() {
        cache.clear()
    }
}

/**
 * 渲染性能监控
 */
object ComposeRenderMonitor {
    private var renderCount = 0
    private var lastRenderTime = 0L
    
    fun recordRender() {
        val currentTime = System.currentTimeMillis()
        if (lastRenderTime > 0) {
            val renderTime = currentTime - lastRenderTime
            EGLPerformanceMonitor.recordRenderTime(renderTime)
        }
        lastRenderTime = currentTime
        renderCount++
    }
    
    fun getRenderCount(): Int = renderCount
    
    fun reset() {
        renderCount = 0
        lastRenderTime = 0L
    }
}
