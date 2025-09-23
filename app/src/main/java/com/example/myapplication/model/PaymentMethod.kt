package com.example.myapplication.model

/**
 * 支付方式枚举
 */
enum class PaymentMethod(val code: String, val displayName: String, val iconRes: Int) {
    ALIPAY("alipay", "支付宝", com.example.myapplication.R.drawable.ic_alipay),
    WECHAT("wechat", "微信支付", com.example.myapplication.R.drawable.ic_wechat);
    
    companion object {
        /**
         * 根据代码获取支付方式
         */
        fun fromCode(code: String): PaymentMethod? {
            return values().find { it.code == code }
        }
        
        /**
         * 根据代码获取支付方式，如果找不到则返回默认值
         */
        fun fromCodeOrDefault(code: String, default: PaymentMethod = ALIPAY): PaymentMethod {
            return fromCode(code) ?: default
        }
    }
}

