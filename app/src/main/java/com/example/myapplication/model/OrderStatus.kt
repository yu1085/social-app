package com.example.myapplication.model

/**
 * 订单状态枚举
 */
enum class OrderStatus(val code: String, val displayName: String) {
    PENDING("pending", "待支付"),
    PROCESSING("processing", "处理中"),
    SUCCESS("success", "成功"),
    PAID("paid", "已支付"),
    EXPIRED("expired", "已过期"),
    CANCELLED("cancelled", "已取消"),
    FAILED("failed", "支付失败");
    
    companion object {
        /**
         * 根据代码获取订单状态
         */
        fun fromCode(code: String): OrderStatus? {
            return values().find { it.code == code }
        }
        
        /**
         * 根据代码获取订单状态，如果找不到则返回默认值
         */
        fun fromCodeOrDefault(code: String, default: OrderStatus = PENDING): OrderStatus {
            return fromCode(code) ?: default
        }
    }
}

