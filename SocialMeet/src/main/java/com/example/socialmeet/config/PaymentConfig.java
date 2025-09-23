package com.example.socialmeet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 支付配置类
 */
@Configuration
@ConfigurationProperties(prefix = "payment")
@Data
public class PaymentConfig {
    
    /**
     * 支付宝配置
     */
    private AlipayConfig alipay = new AlipayConfig();
    
    /**
     * 微信支付配置
     */
    private WechatPayConfig wechat = new WechatPayConfig();
    
    @Data
    public static class AlipayConfig {
        private String appId;
        private String privateKey;
        private String publicKey;
        private String alipayPublicKey;
        private String serverUrl = "https://openapi.alipay.com/gateway.do";
        private String format = "json";
        private String charset = "UTF-8";
        private String signType = "RSA2";
        private String notifyUrl;
        private String returnUrl;
    }
    
    @Data
    public static class WechatPayConfig {
        private String appId;
        private String mchId;
        private String apiV3Key;
        private String privateKeyPath;
        private String certificateSerialNumber;
        private String notifyUrl;
        private String returnUrl;
        private String serverUrl = "https://api.mch.weixin.qq.com";
    }
}
