package com.example.socialmeet.exception;

/**
 * 支付相关异常类
 */
public class PaymentException extends RuntimeException {
    
    private final String errorCode;
    private final String errorMessage;
    private final String orderId;
    
    public PaymentException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.orderId = null;
    }
    
    public PaymentException(String errorCode, String errorMessage, String orderId) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.orderId = orderId;
    }
    
    public PaymentException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.orderId = null;
    }
    
    public PaymentException(String errorCode, String errorMessage, String orderId, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.orderId = orderId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    // 常见错误代码
    public static class ErrorCodes {
        public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
        public static final String ORDER_ALREADY_PAID = "ORDER_ALREADY_PAID";
        public static final String ORDER_EXPIRED = "ORDER_EXPIRED";
        public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
        public static final String INVALID_AMOUNT = "INVALID_AMOUNT";
        public static final String INVALID_PAYMENT_METHOD = "INVALID_PAYMENT_METHOD";
        public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
        public static final String INSUFFICIENT_BALANCE = "INSUFFICIENT_BALANCE";
        public static final String NETWORK_ERROR = "NETWORK_ERROR";
        public static final String SIGNATURE_INVALID = "SIGNATURE_INVALID";
        public static final String CALLBACK_PROCESSING_FAILED = "CALLBACK_PROCESSING_FAILED";
        public static final String ORDER_CREATION_FAILED = "ORDER_CREATION_FAILED";
        public static final String WALLET_UPDATE_FAILED = "WALLET_UPDATE_FAILED";
    }
    
    // 常见错误消息
    public static class ErrorMessages {
        public static final String ORDER_NOT_FOUND = "订单不存在";
        public static final String ORDER_ALREADY_PAID = "订单已支付";
        public static final String ORDER_EXPIRED = "订单已过期";
        public static final String PAYMENT_FAILED = "支付失败";
        public static final String INVALID_AMOUNT = "金额无效";
        public static final String INVALID_PAYMENT_METHOD = "支付方式无效";
        public static final String USER_NOT_FOUND = "用户不存在";
        public static final String INSUFFICIENT_BALANCE = "余额不足";
        public static final String NETWORK_ERROR = "网络错误";
        public static final String SIGNATURE_INVALID = "签名验证失败";
        public static final String CALLBACK_PROCESSING_FAILED = "回调处理失败";
        public static final String ORDER_CREATION_FAILED = "创建订单失败";
        public static final String WALLET_UPDATE_FAILED = "更新钱包失败";
    }
}
