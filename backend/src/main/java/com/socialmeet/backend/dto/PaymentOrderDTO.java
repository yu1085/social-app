package com.socialmeet.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderDTO {
    
    private Long id;
    private String orderId;
    private Long userId;
    private String packageId;
    private Long coins;
    private BigDecimal amount;
    private String paymentMethod;
    private String alipayTradeNo;
    private String alipayOutTradeNo;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paidAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;
    
    private String description;
    private String transactionId;
    private String failureReason;
}
