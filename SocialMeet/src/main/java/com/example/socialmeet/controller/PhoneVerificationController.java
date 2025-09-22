package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.entity.PhoneVerification;
import com.example.socialmeet.repository.PhoneVerificationRepository;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/phone")
@CrossOrigin(originPatterns = "*")
public class PhoneVerificationController {
    
    @Autowired
    private PhoneVerificationRepository phoneVerificationRepository;
    
    // JWT工具类，用于后续认证功能扩展
    // @Autowired
    // private JwtUtil jwtUtil;
    
    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(
            @RequestParam String phoneNumber) {
        try {
            // 生成6位验证码
            String verificationCode = String.format("%06d", new Random().nextInt(1000000));
            
            // 保存验证码到数据库
            PhoneVerification verification = new PhoneVerification();
            verification.setPhoneNumber(phoneNumber);
            verification.setVerificationCode(verificationCode);
            verification.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // 5分钟有效期
            verification.setStatus("PENDING");
            verification.setCreatedAt(LocalDateTime.now());
            
            phoneVerificationRepository.save(verification);
            
            // TODO: 这里应该调用短信服务发送验证码
            // 目前只是模拟，实际项目中需要集成短信服务商API
            
            return ResponseEntity.ok(ApiResponse.success("验证码已发送"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("发送验证码失败: " + e.getMessage()));
        }
    }
    
    /**
     * 验证手机号
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyPhoneNumber(
            @RequestParam String phoneNumber,
            @RequestParam String verificationCode) {
        try {
            // 查找有效的验证码
            PhoneVerification verification = phoneVerificationRepository
                    .findByPhoneNumberAndVerificationCodeAndStatus(phoneNumber, verificationCode, "PENDING")
                    .stream()
                    .filter(v -> v.getExpiresAt().isAfter(LocalDateTime.now()))
                    .findFirst()
                    .orElse(null);
            
            if (verification == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("验证码无效或已过期"));
            }
            
            // 更新验证状态
            verification.setStatus("VERIFIED");
            verification.setVerifiedAt(LocalDateTime.now());
            phoneVerificationRepository.save(verification);
            
            Map<String, Object> result = Map.of(
                "phoneNumber", phoneNumber,
                "verified", true,
                "verifiedAt", verification.getVerifiedAt()
            );
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("验证失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取验证状态
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVerificationStatus(
            @RequestParam String phoneNumber) {
        try {
            PhoneVerification verification = phoneVerificationRepository
                    .findByPhoneNumberAndStatus(phoneNumber, "VERIFIED")
                    .stream()
                    .findFirst()
                    .orElse(null);
            
            Map<String, Object> result = Map.of(
                "phoneNumber", phoneNumber,
                "verified", verification != null,
                "verifiedAt", verification != null ? verification.getVerifiedAt() : null
            );
            
            return ResponseEntity.ok(ApiResponse.success(result));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取验证状态失败: " + e.getMessage()));
        }
    }
}
