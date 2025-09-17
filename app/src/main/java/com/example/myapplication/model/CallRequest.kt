package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

/**
 * 通话请求数据模型
 */
data class CallRequest(
    @SerializedName("receiverId")
    val receiverId: Long,
    
    @SerializedName("callType")
    val callType: CallType = CallType.VIDEO,
    
    @SerializedName("rate")
    val rate: Double = 300.0
)

/**
 * 通话类型枚举
 */
enum class CallType {
    VIDEO,    // 视频通话
    AUDIO    // 语音通话
}

/**
 * 通话响应数据模型
 */
data class CallResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("callSession")
    val callSession: CallSession? = null,
    
    @SerializedName("errorCode")
    val errorCode: String? = null
) {
    companion object {
        fun success(callSession: CallSession): CallResponse {
            return CallResponse(true, "通话发起成功", callSession)
        }
        
        fun failure(message: String, errorCode: String? = null): CallResponse {
            return CallResponse(false, message, null, errorCode)
        }
    }
}

/**
 * 通话结束请求
 */
data class EndCallRequest(
    @SerializedName("callSessionId")
    val callSessionId: String,
    
    @SerializedName("reason")
    val reason: EndCallReason = EndCallReason.NORMAL
)

/**
 * 结束通话原因枚举
 */
enum class EndCallReason {
    NORMAL,      // 正常结束
    TIMEOUT,     // 超时
    REJECTED,    // 被拒绝
    CANCELLED,   // 取消
    FAILED,      // 失败
    INSUFFICIENT_BALANCE  // 余额不足
}
