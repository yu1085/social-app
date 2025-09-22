package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.service.JPushService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 推送通知控制器
 */
@RestController
@RequestMapping("/api/push")
@CrossOrigin(originPatterns = "*")
public class PushController {
    
    @Autowired
    private JPushService jpushService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 注册设备Token
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> registerToken(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String deviceToken = (String) request.get("token");
            String platform = (String) request.get("platform");
            String appVersion = (String) request.get("appVersion");
            String deviceModel = (String) request.get("deviceModel");
            
            System.out.println("=== 注册设备Token ===");
            System.out.println("用户ID: " + userId);
            System.out.println("设备Token: " + deviceToken);
            System.out.println("平台: " + platform);
            
            // 验证必要参数
            if (deviceToken == null || deviceToken.trim().isEmpty()) {
                System.err.println("设备Token不能为空");
                return ResponseEntity.badRequest().body(ApiResponse.error("设备Token不能为空"));
            }
            
            if (platform == null || platform.trim().isEmpty()) {
                System.err.println("平台不能为空");
                return ResponseEntity.badRequest().body(ApiResponse.error("平台不能为空"));
            }
            
            // 设置默认值
            if (appVersion == null) appVersion = "1.0.0";
            if (deviceModel == null) deviceModel = "Unknown";
            
            boolean success = jpushService.registerDeviceToken(userId, deviceToken, platform, appVersion, deviceModel);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("设备Token注册成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("设备Token注册失败"));
            }
            
        } catch (Exception e) {
            System.out.println("注册设备Token异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("注册设备Token失败: " + e.getMessage()));
        }
    }
    
    /**
     * 注销设备Token
     */
    @DeleteMapping("/unregister")
    public ResponseEntity<ApiResponse<Object>> unregisterToken(
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            System.out.println("=== 注销设备Token ===");
            System.out.println("用户ID: " + userId);
            
            // 注销用户所有设备Token
            boolean success = jpushService.unregisterAllUserTokens(userId);
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("设备Token注销成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("设备Token注销失败"));
            }
            
        } catch (Exception e) {
            System.out.println("注销设备Token异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("注销设备Token失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取推送历史
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Object>> getPushHistory(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            System.out.println("=== 获取推送历史 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("页码: " + page + ", 大小: " + size);
            
            // TODO: 实现获取推送历史的逻辑
            return ResponseEntity.ok(ApiResponse.success("推送历史获取成功"));
            
        } catch (Exception e) {
            System.out.println("获取推送历史异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("获取推送历史失败: " + e.getMessage()));
        }
    }
    
    /**
     * 测试推送
     */
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Object>> testPush(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> request) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String title = (String) request.get("title");
            String content = (String) request.get("content");
            
            System.out.println("=== 测试推送 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("标题: " + title);
            System.out.println("内容: " + content);
            
            jpushService.sendSystemNotification(userId, title, content);
            
            return ResponseEntity.ok(ApiResponse.success("测试推送发送成功"));
            
        } catch (Exception e) {
            System.out.println("测试推送异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("测试推送失败: " + e.getMessage()));
        }
    }
}
