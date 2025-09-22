package com.example.socialmeet.controller;

import com.example.socialmeet.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 指标控制器
 */
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    
    @Autowired
    private MetricsService metricsService;
    
    /**
     * 获取所有指标
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMetrics() {
        Map<String, Object> metrics = metricsService.getAllMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * 获取业务指标
     */
    @GetMapping("/business")
    public ResponseEntity<Map<String, Object>> getBusinessMetrics() {
        Map<String, Object> metrics = metricsService.getBusinessMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * 获取系统指标
     */
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        Map<String, Object> metrics = metricsService.getSystemMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * 获取性能指标
     */
    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        Map<String, Object> metrics = metricsService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * 重置指标
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetMetrics() {
        metricsService.resetMetrics();
        return ResponseEntity.ok("指标已重置");
    }
}
