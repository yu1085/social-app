package com.example.socialmeet.service;

// 暂时注释阿里云SMS SDK导入，使用模拟实现
// import com.aliyun.dysmsapi20170525.Client;
// import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
// import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
// import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AliyunSmsService {
    
    @Value("${aliyun.sms.access-key-id:}")
    private String accessKeyId;
    
    @Value("${aliyun.sms.access-key-secret:}")
    private String accessKeySecret;
    
    @Value("${aliyun.sms.sign-name:}")
    private String signName;
    
    @Value("${aliyun.sms.template-code:}")
    private String templateCode;
    
    @Value("${aliyun.sms.endpoint:dysmsapi.aliyuncs.com}")
    private String endpoint;
    
    /**
     * 发送验证码短信
     * @param phoneNumber 手机号
     * @param code 验证码
     * @return 发送结果
     */
    public boolean sendVerificationCode(String phoneNumber, String code) {
        // 暂时使用模拟实现，不调用真实的阿里云SMS API
        System.out.println("模拟发送短信: " + phoneNumber + ", 验证码: " + code);
        return true;
        
        /*
        try {
            // 创建客户端
            Client client = createClient();
            
            // 构建短信内容
            Map<String, String> templateParam = new HashMap<>();
            templateParam.put("code", code);
            
            // 创建发送请求
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumber)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam(com.aliyun.teautil.Common.toJsonString(templateParam));
            
            // 发送短信
            SendSmsResponse response = client.sendSms(request);
            
            // 检查发送结果
            if ("OK".equals(response.getBody().getCode())) {
                System.out.println("短信发送成功: " + phoneNumber + ", 验证码: " + code);
                return true;
            } else {
                System.err.println("短信发送失败: " + response.getBody().getMessage());
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("发送短信异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        */
    }
    
    /**
     * 创建阿里云SMS客户端
     */
    // 暂时注释，使用模拟实现
    /*
    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint(endpoint);
        
        return new Client(config);
    }
    */
    
    /**
     * 验证配置是否完整
     */
    public boolean isConfigured() {
        return accessKeyId != null && !accessKeyId.isEmpty() &&
               accessKeySecret != null && !accessKeySecret.isEmpty() &&
               signName != null && !signName.isEmpty() &&
               templateCode != null && !templateCode.isEmpty();
    }
}
