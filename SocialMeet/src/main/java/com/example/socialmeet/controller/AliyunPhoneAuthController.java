package com.example.socialmeet.controller;

import com.example.socialmeet.service.AliyunPhoneAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云号码认证控制器
 * 支持三大运营商统一认证
 */
@RestController
@RequestMapping("/api/aliyun/phone-auth")
@CrossOrigin(origins = "*")
public class AliyunPhoneAuthController {
    
    @Autowired
    private AliyunPhoneAuthService phoneAuthService;
    
    /**
     * 一键登录认证
     */
    @PostMapping("/one-click-login")
    public Map<String, Object> oneClickLogin(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String phoneNumber = request.get("phoneNumber");
            String accessToken = request.get("accessToken");
            
            if (phoneNumber == null || accessToken == null) {
                response.put("success", false);
                response.put("message", "参数不完整");
                return response;
            }
            
            AliyunPhoneAuthService.PhoneAuthResult result = 
                phoneAuthService.performOneClickLogin(phoneNumber, accessToken);
            
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("phoneNumber", result.getPhoneNumber());
            response.put("authToken", result.getAuthToken());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系统错误: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 号码校验
     */
    @PostMapping("/verify-phone")
    public Map<String, Object> verifyPhone(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String phoneNumber = request.get("phoneNumber");
            String accessToken = request.get("accessToken");
            
            if (phoneNumber == null || accessToken == null) {
                response.put("success", false);
                response.put("message", "参数不完整");
                return response;
            }
            
            AliyunPhoneAuthService.PhoneAuthResult result = 
                phoneAuthService.verifyPhoneNumber(phoneNumber, accessToken);
            
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("phoneNumber", result.getPhoneNumber());
            response.put("authToken", result.getAuthToken());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系统错误: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 本机号码校验
     */
    @PostMapping("/verify-self-phone")
    public Map<String, Object> verifySelfPhone(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String accessToken = request.get("accessToken");
            
            if (accessToken == null) {
                response.put("success", false);
                response.put("message", "accessToken不能为空");
                return response;
            }
            
            AliyunPhoneAuthService.PhoneAuthResult result = 
                phoneAuthService.verifySelfPhoneNumber(accessToken);
            
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("phoneNumber", result.getPhoneNumber());
            response.put("authToken", result.getAuthToken());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系统错误: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 获取认证状态
     */
    @GetMapping("/status")
    public Map<String, Object> getAuthStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "阿里云号码认证服务运行正常");
        response.put("supportedOperators", new String[]{"中国移动", "中国联通", "中国电信"});
        response.put("supportedFeatures", new String[]{"一键登录", "号码校验", "本机号码校验"});
        return response;
    }
}
