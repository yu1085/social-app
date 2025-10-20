package com.socialmeet.backend.dto;

import com.socialmeet.backend.validation.ValidPaymentAmount;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 创建订单请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotBlank(message = "套餐ID不能为空")
    @Size(max = 32, message = "套餐ID长度不能超过32个字符")
    private String packageId;
    
    @NotNull(message = "金币数量不能为空")
    @Min(value = 1, message = "金币数量必须大于0")
    @Max(value = 1000000, message = "金币数量不能超过1000000")
    private Long coins;
    
    @NotNull(message = "支付金额不能为空")
    @ValidPaymentAmount(min = 0.01, max = 10000.00)
    private BigDecimal amount;
    
    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "^(ALIPAY|WECHAT|alipay|wechat)$", message = "支付方式只能是ALIPAY或WECHAT")
    private String paymentMethod;
    
    @Size(max = 255, message = "订单描述长度不能超过255个字符")
    private String description;
    
    /**
     * 客户端IP（用于防重放攻击）
     */
    private String clientIp;
    
    /**
     * 客户端时间戳（用于防重放攻击）
     */
    private Long timestamp;
    
    /**
     * 客户端签名（用于防重放攻击）
     */
    private String signature;
}
