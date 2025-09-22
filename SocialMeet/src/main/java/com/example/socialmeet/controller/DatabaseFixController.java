package com.example.socialmeet.controller;

import com.example.socialmeet.service.DatabaseFixService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库修复控制器 - 提供数据库修复相关的API
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/admin/database")
@Tag(name = "数据库修复", description = "数据库修复相关接口")
@Slf4j
public class DatabaseFixController {
    
    @Autowired
    private DatabaseFixService databaseFixService;
    
    /**
     * 修复 device_tokens 表的日期时间值问题（无需认证）
     */
    @PostMapping("/fix/device-tokens-datetime")
    @Operation(summary = "修复 device_tokens 表日期时间值", 
               description = "修复 device_tokens 表中无效的日期时间值 '0000-00-00 00:00:00'")
    public ResponseEntity<Map<String, Object>> fixDeviceTokensDateTime() {
        try {
            log.info("收到修复 device_tokens 表日期时间值的请求");
            
            databaseFixService.fixDeviceTokensDateTime();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "device_tokens 表日期时间值修复成功");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("修复 device_tokens 表日期时间值时发生错误", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "修复失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * 修复所有表的日期时间值问题
     */
    @PostMapping("/fix/all-datetime")
    @Operation(summary = "修复所有表日期时间值", 
               description = "检查并修复所有表中无效的日期时间值")
    public ResponseEntity<Map<String, Object>> fixAllDateTimeIssues() {
        try {
            log.info("收到修复所有表日期时间值的请求");
            
            databaseFixService.fixAllDateTimeIssues();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "所有表日期时间值修复成功");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("修复所有表日期时间值时发生错误", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "修复失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
