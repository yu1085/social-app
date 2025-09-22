package com.example.socialmeet.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 监控配置
 */
@Configuration
public class MonitoringConfig {
    
    /**
     * API请求计数器
     */
    @Bean
    public Counter apiRequestCounter(MeterRegistry meterRegistry) {
        return Counter.builder("api.requests.total")
                .description("Total API requests")
                .register(meterRegistry);
    }
    
    /**
     * API响应时间计时器
     */
    @Bean
    public Timer apiResponseTimer(MeterRegistry meterRegistry) {
        return Timer.builder("api.response.time")
                .description("API response time")
                .register(meterRegistry);
    }
    
    /**
     * 数据库查询计时器
     */
    @Bean
    public Timer databaseQueryTimer(MeterRegistry meterRegistry) {
        return Timer.builder("database.query.time")
                .description("Database query time")
                .register(meterRegistry);
    }
    
    /**
     * 缓存命中计数器
     */
    @Bean
    public Counter cacheHitCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cache.hits.total")
                .description("Cache hits")
                .register(meterRegistry);
    }
    
    /**
     * 缓存未命中计数器
     */
    @Bean
    public Counter cacheMissCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cache.misses.total")
                .description("Cache misses")
                .register(meterRegistry);
    }
    
    /**
     * 错误计数器
     */
    @Bean
    public Counter errorCounter(MeterRegistry meterRegistry) {
        return Counter.builder("errors.total")
                .description("Total errors")
                .register(meterRegistry);
    }
    
    /**
     * 用户活跃度计数器
     */
    @Bean
    public Counter activeUserCounter(MeterRegistry meterRegistry) {
        return Counter.builder("users.active.total")
                .description("Active users")
                .register(meterRegistry);
    }
    
    /**
     * 动态发布计数器
     */
    @Bean
    public Counter dynamicPublishCounter(MeterRegistry meterRegistry) {
        return Counter.builder("dynamics.published.total")
                .description("Published dynamics")
                .register(meterRegistry);
    }
    
    /**
     * 点赞操作计数器
     */
    @Bean
    public Counter likeCounter(MeterRegistry meterRegistry) {
        return Counter.builder("likes.total")
                .description("Total likes")
                .register(meterRegistry);
    }
}
