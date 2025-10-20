package com.socialmeet.backend.entity;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    PENDING("pending", "待支付"),
    PROCESSING("processing", "处理中"),
    SUCCESS("success", "成功"),
    PAID("paid", "已支付"),
    EXPIRED("expired", "已过期"),
    CANCELLED("cancelled", "已取消"),
    FAILED("failed", "支付失败");

    private final String code;
    private final String displayName;

    OrderStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status code: " + code);
    }
}
