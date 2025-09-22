package com.example.socialmeet.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 指标服务
 */
@Service
public class MetricsService {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    private Counter apiRequestCounter;
    private Timer apiResponseTimer;
    private Timer databaseQueryTimer;
    
    @PostConstruct
    public void init() {
        apiRequestCounter = Counter.builder("api.requests.total")
            .description("Total number of API requests")
            .register(meterRegistry);
            
        apiResponseTimer = Timer.builder("api.response.time")
            .description("API response time")
            .register(meterRegistry);
            
        databaseQueryTimer = Timer.builder("database.query.time")
            .description("Database query time")
            .register(meterRegistry);
            
        cacheHitCounter = Counter.builder("cache.hits.total")
            .description("Total cache hits")
            .register(meterRegistry);
            
        cacheMissCounter = Counter.builder("cache.misses.total")
            .description("Total cache misses")
            .register(meterRegistry);
            
        errorCounter = Counter.builder("errors.total")
            .description("Total errors")
            .register(meterRegistry);
            
        activeUserCounter = Counter.builder("users.active.total")
            .description("Total active users")
            .register(meterRegistry);
            
        dynamicPublishCounter = Counter.builder("dynamics.published.total")
            .description("Total published dynamics")
            .register(meterRegistry);
            
        likeCounter = Counter.builder("likes.total")
            .description("Total likes")
            .register(meterRegistry);
    }
    
    private Counter cacheHitCounter;
    private Counter cacheMissCounter;
    private Counter errorCounter;
    private Counter activeUserCounter;
    private Counter dynamicPublishCounter;
    private Counter likeCounter;
    
    // 自定义指标
    private final AtomicLong totalUsers = new AtomicLong(0);
    private final AtomicLong totalDynamics = new AtomicLong(0);
    private final AtomicLong totalLikes = new AtomicLong(0);
    private final AtomicLong totalComments = new AtomicLong(0);
    
    /**
     * 记录API请求
     */
    public void recordApiRequest(String endpoint, String method) {
        apiRequestCounter.increment();
    }
    
    /**
     * 记录API响应时间
     */
    public void recordApiResponseTime(String endpoint, long duration) {
        apiResponseTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    
    /**
     * 记录数据库查询时间
     */
    public void recordDatabaseQueryTime(String query, long duration) {
        databaseQueryTimer.record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    
    /**
     * 记录缓存命中
     */
    public void recordCacheHit(String cacheType) {
        cacheHitCounter.increment();
    }
    
    /**
     * 记录缓存未命中
     */
    public void recordCacheMiss(String cacheType) {
        cacheMissCounter.increment();
    }
    
    /**
     * 记录错误
     */
    public void recordError(String errorType, String errorMessage) {
        errorCounter.increment();
    }
    
    /**
     * 记录活跃用户
     */
    public void recordActiveUser() {
        activeUserCounter.increment();
    }
    
    /**
     * 记录动态发布
     */
    public void recordDynamicPublish() {
        dynamicPublishCounter.increment();
        totalDynamics.incrementAndGet();
    }
    
    /**
     * 记录点赞
     */
    public void recordLike() {
        likeCounter.increment();
        totalLikes.incrementAndGet();
    }
    
    /**
     * 记录评论
     */
    public void recordComment() {
        totalComments.incrementAndGet();
    }
    
    /**
     * 记录用户注册
     */
    public void recordUserRegistration() {
        totalUsers.incrementAndGet();
    }
    
    /**
     * 获取业务指标
     */
    public Map<String, Object> getBusinessMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("totalUsers", totalUsers.get());
        metrics.put("totalDynamics", totalDynamics.get());
        metrics.put("totalLikes", totalLikes.get());
        metrics.put("totalComments", totalComments.get());
        
        // 计算比率
        if (totalUsers.get() > 0) {
            metrics.put("dynamicsPerUser", (double) totalDynamics.get() / totalUsers.get());
            metrics.put("likesPerUser", (double) totalLikes.get() / totalUsers.get());
        }
        
        if (totalDynamics.get() > 0) {
            metrics.put("likesPerDynamic", (double) totalLikes.get() / totalDynamics.get());
            metrics.put("commentsPerDynamic", (double) totalComments.get() / totalDynamics.get());
        }
        
        return metrics;
    }
    
    /**
     * 获取系统指标
     */
    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // JVM内存信息
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        metrics.put("maxMemory", maxMemory);
        metrics.put("totalMemory", totalMemory);
        metrics.put("freeMemory", freeMemory);
        metrics.put("usedMemory", usedMemory);
        metrics.put("memoryUsagePercent", (double) usedMemory / maxMemory * 100);
        
        // 线程信息
        metrics.put("activeThreads", Thread.activeCount());
        metrics.put("availableProcessors", runtime.availableProcessors());
        
        return metrics;
    }
    
    /**
     * 获取性能指标
     */
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // 从MeterRegistry获取指标
        double apiResponseTime = apiResponseTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS);
        double databaseQueryTime = databaseQueryTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS);
        
        metrics.put("apiResponseTime", apiResponseTime);
        metrics.put("databaseQueryTime", databaseQueryTime);
        
        // 计算缓存命中率
        double cacheHits = cacheHitCounter.count();
        double cacheMisses = cacheMissCounter.count();
        double cacheHitRate = cacheHits + cacheMisses > 0 ? cacheHits / (cacheHits + cacheMisses) * 100 : 0;
        
        metrics.put("cacheHitRate", cacheHitRate);
        metrics.put("cacheHits", cacheHits);
        metrics.put("cacheMisses", cacheMisses);
        
        return metrics;
    }
    
    /**
     * 获取所有指标
     */
    public Map<String, Object> getAllMetrics() {
        Map<String, Object> allMetrics = new HashMap<>();
        
        allMetrics.put("business", getBusinessMetrics());
        allMetrics.put("system", getSystemMetrics());
        allMetrics.put("performance", getPerformanceMetrics());
        allMetrics.put("timestamp", System.currentTimeMillis());
        
        return allMetrics;
    }
    
    /**
     * 重置指标
     */
    public void resetMetrics() {
        totalUsers.set(0);
        totalDynamics.set(0);
        totalLikes.set(0);
        totalComments.set(0);
    }
}
