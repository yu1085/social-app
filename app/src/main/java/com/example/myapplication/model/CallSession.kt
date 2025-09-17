package com.example.myapplication.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * 通话会话数据模型
 */
data class CallSession(
    @SerializedName("id")
    val id: String = "",
    
    @SerializedName("callerId")
    val callerId: Long = 0,
    
    @SerializedName("receiverId")
    val receiverId: Long = 0,
    
    @SerializedName("status")
    val status: CallStatus = CallStatus.INITIATED,
    
    @SerializedName("startTime")
    val startTime: Date? = null,
    
    @SerializedName("endTime")
    val endTime: Date? = null,
    
    @SerializedName("duration")
    val duration: Long = 0, // 通话时长（秒）
    
    @SerializedName("rate")
    val rate: Double = 300.0, // 每分钟费率（元）
    
    @SerializedName("totalCost")
    val totalCost: Double = 0.0, // 总费用（元）
    
    @SerializedName("callerBalance")
    val callerBalance: Double = 0.0, // 发起方余额
    
    @SerializedName("receiverBalance")
    val receiverBalance: Double = 0.0, // 接收方余额
    
    @SerializedName("isOnline")
    val isOnline: Boolean = false, // 接收方是否在线
    
    @SerializedName("createdAt")
    val createdAt: Date = Date(),
    
    @SerializedName("updatedAt")
    val updatedAt: Date = Date()
) {
    /**
     * 获取通话状态文本
     */
    fun getStatusText(): String {
        return when (status) {
            CallStatus.INITIATED -> "发起中"
            CallStatus.RINGING -> "响铃中"
            CallStatus.ACTIVE -> "通话中"
            CallStatus.ENDED -> "已结束"
            CallStatus.REJECTED -> "已拒绝"
            CallStatus.CANCELLED -> "已取消"
            CallStatus.FAILED -> "失败"
        }
    }
    
    /**
     * 获取通话状态颜色
     */
    fun getStatusColor(): String {
        return when (status) {
            CallStatus.INITIATED -> "#FF9800"
            CallStatus.RINGING -> "#2196F3"
            CallStatus.ACTIVE -> "#4CAF50"
            CallStatus.ENDED -> "#9E9E9E"
            CallStatus.REJECTED -> "#F44336"
            CallStatus.CANCELLED -> "#FF5722"
            CallStatus.FAILED -> "#F44336"
        }
    }
    
    /**
     * 获取通话时长文本
     */
    fun getDurationText(): String {
        val minutes = duration / 60
        val seconds = duration % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    /**
     * 获取费用文本
     */
    fun getCostText(): String {
        return String.format("¥%.2f", totalCost)
    }
    
    /**
     * 获取费率文本
     */
    fun getRateText(): String {
        return String.format("¥%.0f/分钟", rate)
    }
    
    /**
     * 检查是否可以发起通话
     */
    fun canInitiateCall(): Boolean {
        return isOnline && callerBalance >= rate / 60.0 // 至少能支付1秒的费用
    }
    
    /**
     * 检查是否可以继续通话
     */
    fun canContinueCall(): Boolean {
        return status == CallStatus.ACTIVE && callerBalance >= rate / 60.0
    }
}

/**
 * 通话状态枚举
 */
enum class CallStatus {
    INITIATED,    // 发起中
    RINGING,      // 响铃中
    ACTIVE,       // 通话中
    ENDED,        // 已结束
    REJECTED,     // 已拒绝
    CANCELLED,    // 已取消
    FAILED        // 失败
}
