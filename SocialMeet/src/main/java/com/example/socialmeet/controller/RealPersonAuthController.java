package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 实人认证控制器
 */
@RestController
@RequestMapping("/api/auth/real-person")
@CrossOrigin(originPatterns = "*")
public class RealPersonAuthController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 实人认证验证接口
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyRealPerson(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> requestData) {
        
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("=== 实人认证验证接口被调用 ===");
        System.out.println("Authorization Header: " + authHeader);
        System.out.println("请求数据: " + requestData);
        
        try {
            // 验证JWT token（如果提供）
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                System.out.println("提取的Token: " + token);
                System.out.println("Token长度: " + token.length());
                
                boolean isValid = jwtUtil.validateToken(token);
                System.out.println("Token验证结果: " + isValid);
                
                if (!isValid) {
                    result.put("success", false);
                    result.put("error", "Token无效或已过期");
                    result.put("code", 401);
                    return ResponseEntity.status(401).body(result);
                }
                
                // 从token中获取用户信息
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                result.put("userId", userId);
                result.put("username", username);
            }
            
            // 模拟实人认证验证过程
            String faceImage = (String) requestData.get("faceImage");
            String faceId = (String) requestData.get("faceId");
            
            System.out.println("人脸图片数据长度: " + (faceImage != null ? faceImage.length() : 0));
            System.out.println("人脸ID: " + faceId);
            
            // 模拟验证逻辑
            boolean verificationSuccess = simulateRealPersonVerification(faceImage, faceId);
            
            if (verificationSuccess) {
                result.put("success", true);
                result.put("verified", true);
                result.put("message", "实人认证成功");
                result.put("verificationId", "VERIFY_" + System.currentTimeMillis());
                result.put("timestamp", System.currentTimeMillis());
            } else {
                result.put("success", false);
                result.put("verified", false);
                result.put("error", "实人认证失败");
                result.put("message", "人脸识别验证未通过");
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("实人认证验证异常: " + e.getMessage());
            e.printStackTrace();
            
            result.put("success", false);
            result.put("error", "实人认证验证异常: " + e.getMessage());
            result.put("code", 500);
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 模拟实人认证验证过程
     */
    private boolean simulateRealPersonVerification(String faceImage, String faceId) {
        try {
            // 模拟验证延迟
            Thread.sleep(1000);
            
            // 简单的验证逻辑
            if (faceImage == null || faceImage.isEmpty()) {
                System.out.println("人脸图片为空");
                return false;
            }
            
            if (faceId == null || faceId.isEmpty()) {
                System.out.println("人脸ID为空");
                return false;
            }
            
            // 模拟验证成功（在实际应用中，这里会调用阿里云等第三方服务）
            System.out.println("模拟实人认证验证成功");
            return true;
            
        } catch (Exception e) {
            System.err.println("模拟验证过程异常: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取实人认证状态
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Object>> getVerificationStatus(
            @PathVariable Long userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("=== 获取实人认证状态 ===");
        System.out.println("用户ID: " + userId);
        System.out.println("Authorization Header: " + authHeader);
        
        try {
            // 验证JWT token
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                boolean isValid = jwtUtil.validateToken(token);
                
                if (!isValid) {
                    result.put("success", false);
                    result.put("error", "Token无效或已过期");
                    result.put("code", 401);
                    return ResponseEntity.status(401).body(result);
                }
                
                // 验证用户ID是否匹配
                Long tokenUserId = jwtUtil.getUserIdFromToken(token);
                if (!tokenUserId.equals(userId)) {
                    result.put("success", false);
                    result.put("error", "无权限访问此用户资源");
                    result.put("code", 403);
                    return ResponseEntity.status(403).body(result);
                }
            }
            
            // 模拟获取认证状态
            result.put("success", true);
            result.put("userId", userId);
            result.put("verified", true);
            result.put("verificationDate", System.currentTimeMillis());
            result.put("verificationLevel", "金融级");
            result.put("status", "已认证");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("获取认证状态异常: " + e.getMessage());
            e.printStackTrace();
            
            result.put("success", false);
            result.put("error", "获取认证状态异常: " + e.getMessage());
            result.put("code", 500);
            return ResponseEntity.status(500).body(result);
        }
    }
}
