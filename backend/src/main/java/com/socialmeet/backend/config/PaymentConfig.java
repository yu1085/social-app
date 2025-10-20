package com.socialmeet.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 支付配置类
 */
@Configuration
@ConfigurationProperties(prefix = "payment")
@Data
@Validated
public class PaymentConfig {
    
    /**
     * 订单过期时间（分钟）
     */
    @Min(value = 1, message = "订单过期时间不能小于1分钟")
    @Max(value = 1440, message = "订单过期时间不能超过1440分钟（24小时）")
    private int orderExpireMinutes = 30;
    
    /**
     * 最大重试次数
     */
    @Min(value = 1, message = "最大重试次数不能小于1")
    @Max(value = 10, message = "最大重试次数不能超过10")
    private int maxRetryCount = 3;
    
    /**
     * 回调超时时间（秒）
     */
    @Min(value = 5, message = "回调超时时间不能小于5秒")
    @Max(value = 300, message = "回调超时时间不能超过300秒")
    private int callbackTimeoutSeconds = 30;
    
    /**
     * 最小支付金额
     */
    @DecimalMin(value = "0.01", message = "最小支付金额不能小于0.01元")
    @DecimalMax(value = "1000.00", message = "最小支付金额不能超过1000元")
    private double minAmount = 0.01;
    
    /**
     * 最大支付金额
     */
    @DecimalMin(value = "1.00", message = "最大支付金额不能小于1元")
    @DecimalMax(value = "10000.00", message = "最大支付金额不能超过10000元")
    private double maxAmount = 10000.00;
    
    /**
     * 是否启用防重放攻击
     */
    private boolean enableReplayAttackPrevention = true;
    
    /**
     * 是否启用频率限制
     */
    private boolean enableRateLimit = true;
    
    /**
     * 频率限制窗口时间（秒）
     */
    @Min(value = 1, message = "频率限制窗口时间不能小于1秒")
    @Max(value = 3600, message = "频率限制窗口时间不能超过3600秒")
    private int rateLimitWindowSeconds = 60;
    
    /**
     * 订单创建频率限制（每分钟最大次数）
     */
    @Min(value = 1, message = "订单创建频率限制不能小于1")
    @Max(value = 100, message = "订单创建频率限制不能超过100")
    private int orderCreateRateLimit = 5;
    
    /**
     * 支付频率限制（每分钟最大次数）
     */
    @Min(value = 1, message = "支付频率限制不能小于1")
    @Max(value = 100, message = "支付频率限制不能超过100")
    private int paymentRateLimit = 10;
}
