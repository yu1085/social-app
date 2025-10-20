package com.socialmeet.backend.dto;

import com.socialmeet.backend.enums.PaymentErrorCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 支付API统一响应格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApiResponse<T> {
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 请求ID（用于追踪）
     */
    private String requestId;
    
    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 成功响应
     */
    public static <T> PaymentApiResponse<T> success(T data) {
        return success(data, null);
    }
    
    /**
     * 成功响应（带请求ID）
     */
    public static <T> PaymentApiResponse<T> success(T data, String requestId) {
        PaymentApiResponse<T> response = new PaymentApiResponse<>();
        response.setCode(PaymentErrorCode.SUCCESS.getCode());
        response.setMessage(PaymentErrorCode.SUCCESS.getMessage());
        response.setData(data);
        response.setRequestId(requestId);
        response.setTimestamp(LocalDateTime.now());
        response.setSuccess(true);
        return response;
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> PaymentApiResponse<T> success() {
        return success(null, null);
    }
    
    /**
     * 错误响应
     */
    public static <T> PaymentApiResponse<T> error(PaymentErrorCode errorCode) {
        return error(errorCode, null);
    }
    
    /**
     * 错误响应（带请求ID）
     */
    public static <T> PaymentApiResponse<T> error(PaymentErrorCode errorCode, String requestId) {
        PaymentApiResponse<T> response = new PaymentApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());
        response.setData(null);
        response.setRequestId(requestId);
        response.setTimestamp(LocalDateTime.now());
        response.setSuccess(false);
        return response;
    }
    
    /**
     * 错误响应（自定义消息）
     */
    public static <T> PaymentApiResponse<T> error(PaymentErrorCode errorCode, String customMessage, String requestId) {
        PaymentApiResponse<T> response = new PaymentApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(customMessage);
        response.setData(null);
        response.setRequestId(requestId);
        response.setTimestamp(LocalDateTime.now());
        response.setSuccess(false);
        return response;
    }
    
    /**
     * 参数错误响应
     */
    public static <T> PaymentApiResponse<T> invalidParameter(String fieldName) {
        return error(PaymentErrorCode.INVALID_PARAMETER, 
                    String.format("参数 %s 无效", fieldName), null);
    }
    
    /**
     * 缺少参数响应
     */
    public static <T> PaymentApiResponse<T> missingParameter(String fieldName) {
        return error(PaymentErrorCode.MISSING_PARAMETER, 
                    String.format("缺少必要参数 %s", fieldName), null);
    }
}
