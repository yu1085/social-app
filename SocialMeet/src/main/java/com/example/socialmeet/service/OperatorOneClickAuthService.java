package com.example.socialmeet.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 运营商一键登录服务
 * 支持移动、联通、电信三大运营商的一键登录验证
 */
@Service
public class OperatorOneClickAuthService {
    
    @Value("${operator.auth.app-id:}")
    private String appId;
    
    @Value("${operator.auth.app-secret:}")
    private String appSecret;
    
    @Value("${operator.auth.api-url:https://api.operator.com}")
    private String apiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * 执行一键登录验证
     * @param accessToken 运营商返回的访问令牌
     * @param operator 运营商类型 (CMCC/UNICOM/TELECOM)
     * @return 验证结果
     */
    public OneClickAuthResult performOneClickAuth(String accessToken, String operator) {
        try {
            // 构建请求参数
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("appId", appId);
            requestData.put("appSecret", appSecret);
            requestData.put("accessToken", accessToken);
            requestData.put("operator", operator);
            requestData.put("timestamp", System.currentTimeMillis());
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "SocialMeet/1.0");
            
            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);
            
            // 发送请求
            String url = apiUrl + "/auth/oneclick/verify";
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, String.class);
            
            // 解析响应
            return parseAuthResponse(response.getBody());
            
        } catch (Exception e) {
            System.err.println("一键登录验证失败: " + e.getMessage());
            return OneClickAuthResult.failure("网络错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取手机号信息
     * @param accessToken 访问令牌
     * @return 手机号信息
     */
    public PhoneInfo getPhoneInfo(String accessToken) {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("appId", appId);
            requestData.put("accessToken", accessToken);
            requestData.put("timestamp", System.currentTimeMillis());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);
            
            String url = apiUrl + "/auth/oneclick/phone";
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, String.class);
            
            return parsePhoneInfo(response.getBody());
            
        } catch (Exception e) {
            System.err.println("获取手机号信息失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析认证响应
     */
    private OneClickAuthResult parseAuthResponse(String responseBody) {
        try {
            JSONObject json = JSON.parseObject(responseBody);
            int code = json.getIntValue("code");
            String message = json.getString("message");
            JSONObject data = json.getJSONObject("data");
            
            if (code == 200 && data != null) {
                boolean success = data.getBooleanValue("success");
                String phoneNumber = data.getString("phoneNumber");
                String operator = data.getString("operator");
                long expireTime = data.getLongValue("expireTime");
                
                return OneClickAuthResult.success(phoneNumber, operator, expireTime);
            } else {
                return OneClickAuthResult.failure(message);
            }
        } catch (Exception e) {
            return OneClickAuthResult.failure("响应解析失败");
        }
    }
    
    /**
     * 解析手机号信息
     */
    private PhoneInfo parsePhoneInfo(String responseBody) {
        try {
            JSONObject json = JSON.parseObject(responseBody);
            int code = json.getIntValue("code");
            JSONObject data = json.getJSONObject("data");
            
            if (code == 200 && data != null) {
                String phoneNumber = data.getString("phoneNumber");
                String operator = data.getString("operator");
                String province = data.getString("province");
                String city = data.getString("city");
                
                return new PhoneInfo(phoneNumber, operator, province, city);
            }
        } catch (Exception e) {
            System.err.println("解析手机号信息失败: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 检查配置是否完整
     */
    public boolean isConfigured() {
        return appId != null && !appId.isEmpty() &&
               appSecret != null && !appSecret.isEmpty();
    }
    
    /**
     * 一键登录认证结果
     */
    public static class OneClickAuthResult {
        private boolean success;
        private String message;
        private String phoneNumber;
        private String operator;
        private long expireTime;
        
        private OneClickAuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        private OneClickAuthResult(boolean success, String message, String phoneNumber, String operator, long expireTime) {
            this.success = success;
            this.message = message;
            this.phoneNumber = phoneNumber;
            this.operator = operator;
            this.expireTime = expireTime;
        }
        
        public static OneClickAuthResult success(String phoneNumber, String operator, long expireTime) {
            return new OneClickAuthResult(true, "认证成功", phoneNumber, operator, expireTime);
        }
        
        public static OneClickAuthResult failure(String message) {
            return new OneClickAuthResult(false, message);
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getOperator() { return operator; }
        public long getExpireTime() { return expireTime; }
    }
    
    /**
     * 手机号信息
     */
    public static class PhoneInfo {
        private String phoneNumber;
        private String operator;
        private String province;
        private String city;
        
        public PhoneInfo(String phoneNumber, String operator, String province, String city) {
            this.phoneNumber = phoneNumber;
            this.operator = operator;
            this.province = province;
            this.city = city;
        }
        
        // Getters
        public String getPhoneNumber() { return phoneNumber; }
        public String getOperator() { return operator; }
        public String getProvince() { return province; }
        public String getCity() { return city; }
    }
}
