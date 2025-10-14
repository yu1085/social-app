package com.example.myapplication.signaling

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * 信令消息数据结构
 * 在信令服务器和客户端之间传递的标准消息格式
 */
data class SignalMessage(
    /**
     * 信令类型
     */
    @SerializedName("type")
    val type: SignalType,

    /**
     * 呼叫ID（唯一标识一次呼叫）
     * 格式: "call_${timestamp}_${fromUserId}"
     */
    @SerializedName("callId")
    val callId: String,

    /**
     * 发送方用户ID
     */
    @SerializedName("fromUserId")
    val fromUserId: String,

    /**
     * 接收方用户ID
     */
    @SerializedName("toUserId")
    val toUserId: String,

    /**
     * RTC房间ID（通话建立后使用）
     * 通常使用 callId 作为房间ID
     */
    @SerializedName("roomId")
    val roomId: String? = null,

    /**
     * 时间戳（毫秒）
     */
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),

    /**
     * 扩展字段（可选）
     * 用于传递额外信息，如：
     * - callerName: 呼叫者姓名
     * - callerAvatar: 呼叫者头像URL
     * - reason: 拒绝/取消原因
     */
    @SerializedName("extra")
    val extra: Map<String, String>? = null
) {
    companion object {
        private val gson = Gson()

        /**
         * 从JSON字符串解析信令消息
         */
        fun fromJson(json: String): SignalMessage? {
            return try {
                gson.fromJson(json, SignalMessage::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * 转换为JSON字符串
     */
    fun toJson(): String {
        return gson.toJson(this)
    }

    /**
     * 生成呼叫ID
     */
    fun generateCallId(fromUserId: String): String {
        return "call_${System.currentTimeMillis()}_$fromUserId"
    }
}

/**
 * 信令消息构建器
 * 提供便捷的方法创建各种类型的信令消息
 */
object SignalMessageBuilder {
    /**
     * 创建呼叫请求消息
     */
    fun createCallRequest(
        fromUserId: String,
        toUserId: String,
        extra: Map<String, String>? = null
    ): SignalMessage {
        val callId = "call_${System.currentTimeMillis()}_$fromUserId"
        return SignalMessage(
            type = SignalType.CALL_REQUEST,
            callId = callId,
            fromUserId = fromUserId,
            toUserId = toUserId,
            roomId = callId, // 使用callId作为房间ID
            extra = extra
        )
    }

    /**
     * 创建接听消息
     */
    fun createCallAccept(
        callId: String,
        fromUserId: String,
        toUserId: String,
        roomId: String
    ): SignalMessage {
        return SignalMessage(
            type = SignalType.CALL_ACCEPT,
            callId = callId,
            fromUserId = fromUserId,
            toUserId = toUserId,
            roomId = roomId
        )
    }

    /**
     * 创建拒绝消息
     */
    fun createCallReject(
        callId: String,
        fromUserId: String,
        toUserId: String,
        reason: String? = null
    ): SignalMessage {
        val extra = reason?.let { mapOf("reason" to it) }
        return SignalMessage(
            type = SignalType.CALL_REJECT,
            callId = callId,
            fromUserId = fromUserId,
            toUserId = toUserId,
            extra = extra
        )
    }

    /**
     * 创建取消消息
     */
    fun createCallCancel(
        callId: String,
        fromUserId: String,
        toUserId: String
    ): SignalMessage {
        return SignalMessage(
            type = SignalType.CALL_CANCEL,
            callId = callId,
            fromUserId = fromUserId,
            toUserId = toUserId
        )
    }

    /**
     * 创建挂断消息
     */
    fun createCallEnd(
        callId: String,
        fromUserId: String,
        toUserId: String
    ): SignalMessage {
        return SignalMessage(
            type = SignalType.CALL_END,
            callId = callId,
            fromUserId = fromUserId,
            toUserId = toUserId
        )
    }

    /**
     * 创建忙线消息
     */
    fun createCallBusy(
        callId: String,
        fromUserId: String,
        toUserId: String
    ): SignalMessage {
        return SignalMessage(
            type = SignalType.CALL_BUSY,
            callId = callId,
            fromUserId = fromUserId,
            toUserId = toUserId
        )
    }

    /**
     * 创建用户上线消息
     */
    fun createUserOnline(userId: String): SignalMessage {
        return SignalMessage(
            type = SignalType.USER_ONLINE,
            callId = "",
            fromUserId = userId,
            toUserId = ""
        )
    }

    /**
     * 创建心跳消息
     */
    fun createHeartbeat(userId: String): SignalMessage {
        return SignalMessage(
            type = SignalType.HEARTBEAT,
            callId = "",
            fromUserId = userId,
            toUserId = ""
        )
    }
}
