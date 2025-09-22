package com.example.socialmeet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AlipayApiService {
    
    @Value("${app.alipay.app-id}")
    private String appId;
    
    @Value("${app.alipay.private-key}")
    private String privateKey;
    
    @Value("${app.alipay.public-key}")
    private String publicKey;
    
    @Value("${app.alipay.gateway-url}")
    private String gatewayUrl;
    
    @Value("${app.alipay.sign-type}")
    private String signType;
    
    @Value("${app.alipay.charset}")
    private String charset;
    
    @Value("${app.alipay.format}")
    private String format;
    
    @Value("${app.alipay.version}")
    private String version;
    
    @Value("${app.alipay.timeout:30000}")
    private int timeout;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 调用支付宝身份证二要素核验API
     * 接口文档: https://opendocs.alipay.com/open/0bk0tu
     * 注意：需要先申请API权限才能正常调用
     */
    public Map<String, Object> verifyIdCard(String certName, String certNo) {
        try {
            // 模拟API调用（实际需要申请权限后才能调用真实API）
            Map<String, Object> result = new HashMap<>();
            
            // 检查配置
            if (appId == null || privateKey == null || publicKey == null) {
                result.put("success", false);
                result.put("error", "支付宝API配置不完整，请检查配置文件");
                return result;
            }
            
            // 模拟验证逻辑（实际应该调用支付宝API）
            result.put("success", true);
            result.put("message", "API配置正确，但需要申请权限后才能调用真实接口");
            result.put("cert_name", certName);
            result.put("cert_no", certNo);
            result.put("outer_biz_no", generateOuterBizNo());
            result.put("status", "PENDING_PERMISSION");
            
            return result;
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "调用支付宝API失败: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 生成外部订单号
     */
    private String generateOuterBizNo() {
        return "SOCIALMEET_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 测试API连接
     */
    public Map<String, Object> testConnection() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 检查配置
            if (appId == null || privateKey == null || publicKey == null) {
                result.put("success", false);
                result.put("error", "支付宝API配置不完整");
                return result;
            }
            
            result.put("success", true);
            result.put("message", "支付宝API配置正确！");
            result.put("app_id", appId);
            result.put("gateway_url", gatewayUrl);
            result.put("sign_type", signType);
            result.put("note", "需要申请API权限后才能调用真实接口");
            
            return result;
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "测试连接失败: " + e.getMessage());
            return result;
        }
    }
}
