package com.socialmeet.backend.entity;

/**
 * 支付方式枚举
 */
public enum PaymentMethod {
    ALIPAY("alipay", "支付宝"),
    WECHAT("wechat", "微信支付"),
    UNIONPAY("unionpay", "银联支付");

    private final String code;
    private final String displayName;

    PaymentMethod(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromCode(String code) {
        for (PaymentMethod method : values()) {
            if (method.code.equalsIgnoreCase(code)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown payment method code: " + code);
    }
}
