package com.socialmeet.backend.enums;

import lombok.Getter;

/**
 * 支付错误码枚举
 */
@Getter
public enum PaymentErrorCode {
    
    // 通用错误码 (1000-1999)
    SUCCESS(1000, "操作成功"),
    INVALID_PARAMETER(1001, "参数无效"),
    MISSING_PARAMETER(1002, "缺少必要参数"),
    INVALID_TOKEN(1003, "无效的认证令牌"),
    USER_NOT_FOUND(1004, "用户不存在"),
    PERMISSION_DENIED(1005, "权限不足"),
    SYSTEM_ERROR(1099, "系统内部错误"),
    
    // 订单相关错误码 (2000-2999)
    ORDER_NOT_FOUND(2001, "订单不存在"),
    ORDER_ALREADY_EXISTS(2002, "订单已存在"),
    ORDER_STATUS_INVALID(2003, "订单状态无效"),
    ORDER_EXPIRED(2004, "订单已过期"),
    ORDER_AMOUNT_INVALID(2005, "订单金额无效"),
    ORDER_PACKAGE_INVALID(2006, "订单套餐无效"),
    ORDER_CREATE_FAILED(2007, "订单创建失败"),
    ORDER_UPDATE_FAILED(2008, "订单更新失败"),
    ORDER_CANCEL_FAILED(2009, "订单取消失败"),
    
    // 支付相关错误码 (3000-3999)
    PAYMENT_METHOD_NOT_SUPPORTED(3001, "不支持的支付方式"),
    PAYMENT_AMOUNT_INVALID(3002, "支付金额无效"),
    PAYMENT_SIGNATURE_INVALID(3003, "支付签名无效"),
    PAYMENT_CALLBACK_FAILED(3004, "支付回调处理失败"),
    PAYMENT_TIMEOUT(3005, "支付超时"),
    PAYMENT_DUPLICATE(3006, "重复支付"),
    PAYMENT_VERIFY_FAILED(3007, "支付验证失败"),
    PAYMENT_CREATE_FAILED(3008, "支付创建失败"),
    
    // 支付宝相关错误码 (4000-4999)
    ALIPAY_ORDER_CREATE_FAILED(4001, "支付宝订单创建失败"),
    ALIPAY_SIGNATURE_VERIFY_FAILED(4002, "支付宝签名验证失败"),
    ALIPAY_CALLBACK_INVALID(4003, "支付宝回调数据无效"),
    ALIPAY_QUERY_FAILED(4004, "支付宝订单查询失败"),
    ALIPAY_CONFIG_ERROR(4005, "支付宝配置错误"),
    
    // 微信支付相关错误码 (5000-5999)
    WECHAT_ORDER_CREATE_FAILED(5001, "微信支付订单创建失败"),
    WECHAT_SIGNATURE_VERIFY_FAILED(5002, "微信支付签名验证失败"),
    WECHAT_CALLBACK_INVALID(5003, "微信支付回调数据无效"),
    WECHAT_QUERY_FAILED(5004, "微信支付订单查询失败"),
    WECHAT_CONFIG_ERROR(5005, "微信支付配置错误"),
    
    // 钱包相关错误码 (6000-6999)
    WALLET_NOT_FOUND(6001, "钱包不存在"),
    WALLET_BALANCE_INSUFFICIENT(6002, "钱包余额不足"),
    WALLET_UPDATE_FAILED(6003, "钱包更新失败"),
    WALLET_CREATE_FAILED(6004, "钱包创建失败"),
    
    // 交易相关错误码 (7000-7999)
    TRANSACTION_CREATE_FAILED(7001, "交易记录创建失败"),
    TRANSACTION_NOT_FOUND(7002, "交易记录不存在"),
    TRANSACTION_UPDATE_FAILED(7003, "交易记录更新失败"),
    
    // 频率限制错误码 (8000-8999)
    RATE_LIMIT_EXCEEDED(8001, "请求频率过高"),
    DUPLICATE_REQUEST(8002, "重复请求"),
    REQUEST_TOO_FREQUENT(8003, "请求过于频繁");
    
    private final int code;
    private final String message;
    
    PaymentErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    /**
     * 根据错误码获取枚举
     */
    public static PaymentErrorCode fromCode(int code) {
        for (PaymentErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return SYSTEM_ERROR;
    }
}
