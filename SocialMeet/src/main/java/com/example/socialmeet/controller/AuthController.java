package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.LoginRequest;
import com.example.socialmeet.dto.LoginResponse;
import com.example.socialmeet.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@RequestParam String phone) {
        try {
            System.out.println("=== 发送验证码请求 ===");
            System.out.println("手机号: " + phone);
            
            String code = authService.sendVerificationCode(phone);
            
            return ResponseEntity.ok(ApiResponse.success("验证码发送成功", code));
        } catch (Exception e) {
            System.out.println("发送验证码失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("发送验证码失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/login-with-code")
    public ResponseEntity<ApiResponse<LoginResponse>> loginWithCode(
            @RequestParam String phone,
            @RequestParam String code,
            @RequestParam(required = false) String gender) {
        try {
            System.out.println("=== 登录请求 ===");
            System.out.println("手机号: " + phone);
            System.out.println("验证码: " + code);
            System.out.println("性别: " + gender);
            
            LoginResponse response = authService.loginWithVerificationCode(phone, code, gender);
            System.out.println("登录成功，用户ID: " + response.getUser().getId());
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            System.out.println("登录失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("登录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("认证服务正常"));
    }
}
