package com.socialmeet.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 支付宝订单响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlipayOrderResponse {
    
    private String orderId;
    private String alipayOrderInfo;
    private String alipayOutTradeNo;
    private String qrCode;
    private String payUrl;
    private Long expireTime;
}
