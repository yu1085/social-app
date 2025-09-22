package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.service.IdCardVerifyService;
import com.example.socialmeet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/id-card")
@CrossOrigin(originPatterns = "*")
public class IdCardVerifyController {

    @Autowired
    private IdCardVerifyService idCardVerifyService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 身份证二要素核验（支付宝官方推荐）
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyIdCard(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String certName = request.get("certName");
            String certNo = request.get("certNo");
            
            if (certName == null || certNo == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("姓名和身份证号不能为空"));
            }
            
            Map<String, Object> result = idCardVerifyService.verifyIdCard(userId, certName, certNo);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("验证失败: " + e.getMessage()));
        }
    }

    /**
     * 获取实名认证状态
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVerificationStatus(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Map<String, Object> result = idCardVerifyService.getVerificationStatus(userId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取状态失败: " + e.getMessage()));
        }
    }

    /**
     * 提交实名认证
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitVerification(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            String certName = request.get("certName");
            String certNo = request.get("certNo");
            
            if (certName == null || certNo == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("姓名和身份证号不能为空"));
            }
            
            Map<String, Object> result = idCardVerifyService.submitVerification(userId, certName, certNo);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("提交失败: " + e.getMessage()));
        }
    }

    /**
     * 查询认证结果
     */
    @GetMapping("/result")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVerificationResult(
            @RequestHeader("Authorization") String authHeader) {
        try {
            String jwt = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            Map<String, Object> result = idCardVerifyService.getVerificationResult(userId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }
}
