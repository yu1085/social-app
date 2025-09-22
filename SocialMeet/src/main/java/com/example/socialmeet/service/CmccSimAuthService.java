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
 * 中国移动基于SIM的一键登录服务
 * 基于超级SIM卡内SIM快捷卡应用实现的快捷登录产品
 */
@Service
public class CmccSimAuthService {
    
    @Value("${cmcc.sim-auth.app-id:}")
    private String appId;
    
    @Value("${cmcc.sim-auth.app-secret:}")
    private String appSecret;
    
    @Value("${cmcc.sim-auth.api-url:https://api.10086.cn}")
    private String apiUrl;
    
    @Value("${cmcc.sim-auth.service-type:SIM_ONE_CLICK}")
    private String serviceType;
    
    @Value("${cmcc.sim-auth.auth-mode:NO_PASSWORD}")
    private String authMode;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * 执行SIM一键登录认证
     * @param phoneNumber 手机号
     * @param authCode 验证码（验证码模式需要）
     * @param simPassword SIM卡密码（密码模式需要）
     * @return 认证结果
     */
    public SimAuthResult performSimAuth(String phoneNumber, String authCode, String simPassword) {
        try {
            // 构建请求参数
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("appId", appId);
            requestData.put("appSecret", appSecret);
            requestData.put("phoneNumber", phoneNumber);
            requestData.put("serviceType", serviceType);
            requestData.put("authMode", authMode);
            requestData.put("timestamp", System.currentTimeMillis());
            
            // 根据认证模式添加相应参数
            if ("VERIFICATION_CODE".equals(authMode) && authCode != null) {
                requestData.put("authCode", authCode);
            } else if ("PASSWORD".equals(authMode) && simPassword != null) {
                requestData.put("simPassword", simPassword);
            }
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "SocialMeet/1.0");
            headers.set("X-CMCC-Service", "SIM_AUTH");
            
            // 创建请求实体
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);
            
            // 发送请求
            String url = apiUrl + "/sim/auth/oneclick";
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, String.class);
            
            // 解析响应
            return parseAuthResponse(response.getBody());
            
        } catch (Exception e) {
            System.err.println("SIM一键登录认证失败: " + e.getMessage());
            return SimAuthResult.failure("网络错误: " + e.getMessage());
        }
    }
    
    /**
     * 获取SIM卡信息
     * @param phoneNumber 手机号
     * @return SIM卡信息
     */
    public SimCardInfo getSimCardInfo(String phoneNumber) {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("appId", appId);
            requestData.put("phoneNumber", phoneNumber);
            requestData.put("timestamp", System.currentTimeMillis());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-CMCC-Service", "SIM_INFO");
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);
            
            String url = apiUrl + "/sim/info";
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, String.class);
            
            return parseSimCardInfo(response.getBody());
            
        } catch (Exception e) {
            System.err.println("获取SIM卡信息失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 检查SIM卡是否支持一键登录
     * @param phoneNumber 手机号
     * @return 是否支持
     */
    public boolean isSimSupported(String phoneNumber) {
        try {
            SimCardInfo simInfo = getSimCardInfo(phoneNumber);
            return simInfo != null && simInfo.isSupported();
        } catch (Exception e) {
            System.err.println("检查SIM卡支持失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 解析认证响应
     */
    private SimAuthResult parseAuthResponse(String responseBody) {
        try {
            JSONObject json = JSON.parseObject(responseBody);
            int code = json.getIntValue("code");
            String message = json.getString("message");
            JSONObject data = json.getJSONObject("data");
            
            if (code == 200 && data != null) {
                boolean success = data.getBooleanValue("success");
                String phoneNumber = data.getString("phoneNumber");
                String simCardId = data.getString("simCardId");
                String authToken = data.getString("authToken");
                long expireTime = data.getLongValue("expireTime");
                String authMode = data.getString("authMode");
                
                return SimAuthResult.success(phoneNumber, simCardId, authToken, expireTime, authMode);
            } else {
                return SimAuthResult.failure(message);
            }
        } catch (Exception e) {
            return SimAuthResult.failure("响应解析失败");
        }
    }
    
    /**
     * 解析SIM卡信息
     */
    private SimCardInfo parseSimCardInfo(String responseBody) {
        try {
            JSONObject json = JSON.parseObject(responseBody);
            int code = json.getIntValue("code");
            JSONObject data = json.getJSONObject("data");
            
            if (code == 200 && data != null) {
                String phoneNumber = data.getString("phoneNumber");
                String simCardId = data.getString("simCardId");
                boolean supported = data.getBooleanValue("supported");
                String carrier = data.getString("carrier");
                String region = data.getString("region");
                
                return new SimCardInfo(phoneNumber, simCardId, supported, carrier, region);
            }
        } catch (Exception e) {
            System.err.println("解析SIM卡信息失败: " + e.getMessage());
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
     * SIM认证结果
     */
    public static class SimAuthResult {
        private boolean success;
        private String message;
        private String phoneNumber;
        private String simCardId;
        private String authToken;
        private long expireTime;
        private String authMode;
        
        private SimAuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        private SimAuthResult(boolean success, String message, String phoneNumber, 
                            String simCardId, String authToken, long expireTime, String authMode) {
            this.success = success;
            this.message = message;
            this.phoneNumber = phoneNumber;
            this.simCardId = simCardId;
            this.authToken = authToken;
            this.expireTime = expireTime;
            this.authMode = authMode;
        }
        
        public static SimAuthResult success(String phoneNumber, String simCardId, 
                                          String authToken, long expireTime, String authMode) {
            return new SimAuthResult(true, "SIM认证成功", phoneNumber, simCardId, authToken, expireTime, authMode);
        }
        
        public static SimAuthResult failure(String message) {
            return new SimAuthResult(false, message);
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getSimCardId() { return simCardId; }
        public String getAuthToken() { return authToken; }
        public long getExpireTime() { return expireTime; }
        public String getAuthMode() { return authMode; }
    }
    
    /**
     * SIM卡信息
     */
    public static class SimCardInfo {
        private String phoneNumber;
        private String simCardId;
        private boolean supported;
        private String carrier;
        private String region;
        
        public SimCardInfo(String phoneNumber, String simCardId, boolean supported, String carrier, String region) {
            this.phoneNumber = phoneNumber;
            this.simCardId = simCardId;
            this.supported = supported;
            this.carrier = carrier;
            this.region = region;
        }
        
        // Getters
        public String getPhoneNumber() { return phoneNumber; }
        public String getSimCardId() { return simCardId; }
        public boolean isSupported() { return supported; }
        public String getCarrier() { return carrier; }
        public String getRegion() { return region; }
    }
}
