package com.example.socialmeet.controller;

import com.example.socialmeet.service.HealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    @Autowired
    private HealthCheckService healthCheckService;
    
    /**
     * 基础健康检查
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = healthCheckService.getHealthStatus();
        
        String status = (String) health.get("status");
        if ("UP".equals(status)) {
            return ResponseEntity.ok(health);
        } else {
            return ResponseEntity.status(503).body(health);
        }
    }
    
    /**
     * 详细健康检查
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = healthCheckService.getHealthStatus();
        
        // 添加更多详细信息
        health.put("version", "1.0.0");
        health.put("environment", "production");
        health.put("uptime", System.currentTimeMillis() - getStartTime());
        
        String status = (String) health.get("status");
        if ("UP".equals(status)) {
            return ResponseEntity.ok(health);
        } else {
            return ResponseEntity.status(503).body(health);
        }
    }
    
    /**
     * 就绪检查
     */
    @GetMapping("/ready")
    public ResponseEntity<String> ready() {
        Map<String, Object> health = healthCheckService.getHealthStatus();
        String status = (String) health.get("status");
        
        if ("UP".equals(status)) {
            return ResponseEntity.ok("Ready");
        } else {
            return ResponseEntity.status(503).body("Not Ready");
        }
    }
    
    /**
     * 存活检查
     */
    @GetMapping("/live")
    public ResponseEntity<String> live() {
        return ResponseEntity.ok("Alive");
    }
    
    private long getStartTime() {
        return System.currentTimeMillis() - java.lang.management.ManagementFactory
                .getRuntimeMXBean().getUptime();
    }
}