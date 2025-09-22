package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.service.OperatorOneClickAuthService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/oneclick")
@CrossOrigin(originPatterns = "*")
public class OneClickAuthController {

    @Autowired
    private OperatorOneClickAuthService oneClickAuthService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 一键登录验证
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyOneClickAuth(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String accessToken = request.get("accessToken");
            String operator = request.get("operator");
            
            if (accessToken == null || operator == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("访问令牌和运营商类型不能为空"));
            }
            
            // 执行一键登录验证
            OperatorOneClickAuthService.OneClickAuthResult result = 
                oneClickAuthService.performOneClickAuth(accessToken, operator);
            
            if (result.isSuccess()) {
                Map<String, Object> responseData = Map.of(
                    "success", true,
                    "message", "一键登录验证成功",
                    "phoneNumber", result.getPhoneNumber(),
                    "operator", result.getOperator(),
                    "expireTime", result.getExpireTime()
                );
                return ResponseEntity.ok(ApiResponse.success(responseData));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("验证失败: " + result.getMessage()));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("验证失败: " + e.getMessage()));
        }
    }

    /**
     * 获取手机号信息
     */
    @PostMapping("/phone-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPhoneInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String accessToken = request.get("accessToken");
            
            if (accessToken == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("访问令牌不能为空"));
            }
            
            // 获取手机号信息
            OperatorOneClickAuthService.PhoneInfo phoneInfo = 
                oneClickAuthService.getPhoneInfo(accessToken);
            
            if (phoneInfo != null) {
                Map<String, Object> responseData = Map.of(
                    "phoneNumber", phoneInfo.getPhoneNumber(),
                    "operator", phoneInfo.getOperator(),
                    "province", phoneInfo.getProvince(),
                    "city", phoneInfo.getCity()
                );
                return ResponseEntity.ok(ApiResponse.success(responseData));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("获取手机号信息失败"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取手机号信息失败: " + e.getMessage()));
        }
    }

    /**
     * 检查一键登录配置状态
     */
    @GetMapping("/config-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConfigStatus() {
        try {
            boolean isConfigured = oneClickAuthService.isConfigured();
            Map<String, Object> responseData = Map.of(
                "configured", isConfigured,
                "message", isConfigured ? "配置正常" : "请配置运营商认证参数"
            );
            return ResponseEntity.ok(ApiResponse.success(responseData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查配置失败: " + e.getMessage()));
        }
    }
}
