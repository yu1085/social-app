package com.example.socialmeet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查服务
 */
@Service
public class HealthCheckService {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 检查数据库连接
     */
    public boolean checkDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5秒超时
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * 检查Redis连接
     */
    public boolean checkRedis() {
        try {
            redisTemplate.opsForValue().get("health_check");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 检查系统资源
     */
    public Map<String, Object> checkSystemResources() {
        Map<String, Object> resources = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        resources.put("maxMemory", maxMemory);
        resources.put("totalMemory", totalMemory);
        resources.put("freeMemory", freeMemory);
        resources.put("usedMemory", usedMemory);
        resources.put("memoryUsagePercent", (double) usedMemory / maxMemory * 100);
        
        // CPU使用率（简化计算）
        resources.put("availableProcessors", runtime.availableProcessors());
        
        return resources;
    }
    
    /**
     * 综合健康检查
     */
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        
        boolean dbHealthy = checkDatabase();
        boolean redisHealthy = checkRedis();
        Map<String, Object> resources = checkSystemResources();
        
        health.put("status", dbHealthy && redisHealthy ? "UP" : "DOWN");
        health.put("database", dbHealthy ? "UP" : "DOWN");
        health.put("redis", redisHealthy ? "UP" : "DOWN");
        health.put("resources", resources);
        health.put("timestamp", System.currentTimeMillis());
        
        return health;
    }
}
