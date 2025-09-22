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
 * 中国移动号卡认证服务
 * 基于运营商网关认证、SIM快捷认证的分层分级安全能力
 * 支持PC端、手机端、H5、短链等多种场景
 */
@Service
public class CmccCardAuthService {
    
    @Value("${cmcc.card-auth.app-id:}")
    private String appId;
    
    @Value("${cmcc.card-auth.app-secret:}")
    private String appSecret;
    
    @Value("${cmcc.card-auth.api-url:https://api.10086.cn}")
    private String apiUrl;
    
    @Value("${cmcc.card-auth.service-type:CARD_AUTH}")
    private String serviceType;
    
    @Value("${cmcc.card-auth.auth-mode:SDK}")
    private String authMode;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    /**
     * 执行号卡认证
     * @param phoneNumber 手机号
     * @param scenario 使用场景 (PC/MOBILE/H5/SHORT_LINK)
     * @param networkType 网络类型 (MOBILE/WIFI)
     * @return 认证结果
     */
    public CardAuthResult performCardAuth(String phoneNumber, String scenario, String networkType) {
        try {
            // 检查是否配置了真实SDK
            if (isRealSdkConfigured()) {
                return performRealCardAuth(phoneNumber, scenario, networkType);
            } else {
                return performMockCardAuth(phoneNumber, scenario, networkType);
            }
            
        } catch (Exception e) {
            System.err.println("号卡认证失败: " + e.getMessage());
            return CardAuthResult.failure("认证错误: " + e.getMessage());
        }
    }
    
    /**
     * 使用真实SDK进行号卡认证
     */
    private CardAuthResult performRealCardAuth(String phoneNumber, String scenario, String networkType) {
        try {
            // 真实SDK集成代码（需要添加SDK后取消注释）
            /*
            // 创建中国移动认证客户端
            CmccAuthClient client = new CmccAuthClient("300013116387", "985E36132015F45031E9D653343C6DBD");
            
            // 构建认证请求
            CardAuthRequest request = new CardAuthRequest();
            request.setPhoneNumber(phoneNumber);
            request.setServiceType(serviceType);
            request.setAuthMode(authMode);
            request.setScenario(scenario);
            request.setNetworkType(networkType);
            request.setTimestamp(System.currentTimeMillis());
            
            // 调用认证接口
            CardAuthResponse response = client.performCardAuth(request);
            
            // 转换为内部结果格式
            if (response.isSuccess()) {
                return CardAuthResult.success(
                    response.getPhoneNumber(),
                    response.getAuthToken(),
                    response.getMessage()
                );
            } else {
                return CardAuthResult.failure(response.getMessage());
            }
            */
            
            // 临时返回模拟结果
            return performMockCardAuth(phoneNumber, scenario, networkType);
            
        } catch (Exception e) {
            System.err.println("真实SDK认证失败: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * 模拟号卡认证（用于测试）
     */
    private CardAuthResult performMockCardAuth(String phoneNumber, String scenario, String networkType) {
        try {
            // 模拟网络延迟
            Thread.sleep(1000);
            
            // 模拟认证成功
            String authToken = "mock_token_" + System.currentTimeMillis();
            long expireTime = System.currentTimeMillis() + 300000; // 5分钟后过期
            String authMethod = "SIM_CARD_AUTH";
            
            System.out.println("模拟号卡认证成功: " + phoneNumber);
            return CardAuthResult.success(phoneNumber, authToken, expireTime, authMethod, scenario, networkType);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("模拟号卡认证被中断: " + e.getMessage());
            return CardAuthResult.failure("认证被中断");
        } catch (Exception e) {
            System.err.println("模拟号卡认证失败: " + e.getMessage());
            return CardAuthResult.failure("认证失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查是否配置了真实SDK
     */
    private boolean isRealSdkConfigured() {
        // 检查是否有真实的App ID和Secret
        return appId != null && !appId.isEmpty() && 
               appSecret != null && !appSecret.isEmpty() &&
               !appId.startsWith("mock_") &&
               !apiUrl.contains("mock");
    }
    
    /**
     * 获取认证能力信息
     * @param phoneNumber 手机号
     * @return 认证能力信息
     */
    public AuthCapability getAuthCapability(String phoneNumber) {
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("appId", appId);
            requestData.put("phoneNumber", phoneNumber);
            requestData.put("timestamp", System.currentTimeMillis());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-CMCC-Service", "AUTH_CAPABILITY");
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestData, headers);
            
            String url = apiUrl + "/card/auth/capability";
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, String.class);
            
            return parseAuthCapability(response.getBody());
            
        } catch (Exception e) {
            System.err.println("获取认证能力失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 检查网络环境支持
     * @param networkType 网络类型
     * @return 是否支持
     */
    public boolean isNetworkSupported(String networkType) {
        // 号卡认证支持移动网络和WIFI网络
        return "MOBILE".equals(networkType) || "WIFI".equals(networkType);
    }
    
    /**
     * 检查场景支持
     * @param scenario 使用场景
     * @return 是否支持
     */
    public boolean isScenarioSupported(String scenario) {
        return "PC".equals(scenario) || "MOBILE".equals(scenario) || 
               "H5".equals(scenario) || "SHORT_LINK".equals(scenario);
    }
    
    /**
     * 解析认证响应
     */
    private CardAuthResult parseAuthResponse(String responseBody) {
        try {
            JSONObject json = JSON.parseObject(responseBody);
            int code = json.getIntValue("code");
            String message = json.getString("message");
            JSONObject data = json.getJSONObject("data");
            
            if (code == 200 && data != null) {
                boolean success = data.getBooleanValue("success");
                String phoneNumber = data.getString("phoneNumber");
                String authToken = data.getString("authToken");
                long expireTime = data.getLongValue("expireTime");
                String authMethod = data.getString("authMethod"); // MOBILE_AUTH/SIM_AUTH
                String scenario = data.getString("scenario");
                String networkType = data.getString("networkType");
                
                return CardAuthResult.success(phoneNumber, authToken, expireTime, 
                                            authMethod, scenario, networkType);
            } else {
                return CardAuthResult.failure(message);
            }
        } catch (Exception e) {
            return CardAuthResult.failure("响应解析失败");
        }
    }
    
    /**
     * 解析认证能力信息
     */
    private AuthCapability parseAuthCapability(String responseBody) {
        try {
            JSONObject json = JSON.parseObject(responseBody);
            int code = json.getIntValue("code");
            JSONObject data = json.getJSONObject("data");
            
            if (code == 200 && data != null) {
                String phoneNumber = data.getString("phoneNumber");
                boolean mobileAuthSupported = data.getBooleanValue("mobileAuthSupported");
                boolean simAuthSupported = data.getBooleanValue("simAuthSupported");
                String carrier = data.getString("carrier");
                String region = data.getString("region");
                String[] supportedScenarios = data.getObject("supportedScenarios", String[].class);
                
                return new AuthCapability(phoneNumber, mobileAuthSupported, simAuthSupported, 
                                        carrier, region, supportedScenarios);
            }
        } catch (Exception e) {
            System.err.println("解析认证能力信息失败: " + e.getMessage());
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
     * 号卡认证结果
     */
    public static class CardAuthResult {
        private boolean success;
        private String message;
        private String phoneNumber;
        private String authToken;
        private long expireTime;
        private String authMethod;
        private String scenario;
        private String networkType;
        
        private CardAuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        private CardAuthResult(boolean success, String message, String phoneNumber, 
                             String authToken, long expireTime, String authMethod, 
                             String scenario, String networkType) {
            this.success = success;
            this.message = message;
            this.phoneNumber = phoneNumber;
            this.authToken = authToken;
            this.expireTime = expireTime;
            this.authMethod = authMethod;
            this.scenario = scenario;
            this.networkType = networkType;
        }
        
        public static CardAuthResult success(String phoneNumber, String authToken, 
                                           long expireTime, String authMethod, 
                                           String scenario, String networkType) {
            return new CardAuthResult(true, "号卡认证成功", phoneNumber, authToken, 
                                    expireTime, authMethod, scenario, networkType);
        }
        
        public static CardAuthResult failure(String message) {
            return new CardAuthResult(false, message);
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getAuthToken() { return authToken; }
        public long getExpireTime() { return expireTime; }
        public String getAuthMethod() { return authMethod; }
        public String getScenario() { return scenario; }
        public String getNetworkType() { return networkType; }
    }
    
    /**
     * 认证能力信息
     */
    public static class AuthCapability {
        private String phoneNumber;
        private boolean mobileAuthSupported;
        private boolean simAuthSupported;
        private String carrier;
        private String region;
        private String[] supportedScenarios;
        
        public AuthCapability(String phoneNumber, boolean mobileAuthSupported, 
                            boolean simAuthSupported, String carrier, String region, 
                            String[] supportedScenarios) {
            this.phoneNumber = phoneNumber;
            this.mobileAuthSupported = mobileAuthSupported;
            this.simAuthSupported = simAuthSupported;
            this.carrier = carrier;
            this.region = region;
            this.supportedScenarios = supportedScenarios;
        }
        
        // Getters
        public String getPhoneNumber() { return phoneNumber; }
        public boolean isMobileAuthSupported() { return mobileAuthSupported; }
        public boolean isSimAuthSupported() { return simAuthSupported; }
        public String getCarrier() { return carrier; }
        public String getRegion() { return region; }
        public String[] getSupportedScenarios() { return supportedScenarios; }
    }
}
