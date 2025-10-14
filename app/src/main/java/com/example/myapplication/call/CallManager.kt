package com.example.myapplication.call

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.myapplication.signaling.SignalMessage
import com.example.myapplication.signaling.SignalMessageBuilder
import com.example.myapplication.signaling.SignalType
import com.example.myapplication.signaling.SignalingClient

/**
 * 呼叫管理器
 * 负责管理视频通话的整个生命周期：发起呼叫、接听、拒绝、挂断等
 */
class CallManager private constructor(private val context: Context) {

    companion object {
        private const val TAG = "CallManager"
        private var instance: CallManager? = null

        /**
         * 获取单例实例
         */
        fun getInstance(context: Context): CallManager {
            return instance ?: synchronized(this) {
                instance ?: CallManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    // 信令客户端
    private val signalingClient = SignalingClient.getInstance(context)

    // 当前通话状态
    private var callState: CallState = CallState.IDLE
    private var currentCallId: String? = null
    private var currentRoomId: String? = null
    private var remoteUserId: String? = null

    // 回调监听器
    private var onIncomingCall: ((callId: String, fromUserId: String, fromUserName: String?) -> Unit)? = null
    private var onCallAccepted: ((callId: String, roomId: String) -> Unit)? = null
    private var onCallRejected: ((callId: String, reason: String?) -> Unit)? = null
    private var onCallCancelled: ((callId: String) -> Unit)? = null
    private var onCallEnded: ((callId: String) -> Unit)? = null
    private var onCallTimeout: ((callId: String) -> Unit)? = null
    private var onUserBusy: ((callId: String) -> Unit)? = null

    init {
        // 监听信令消息
        signalingClient.setOnSignalReceived { signal ->
            handleSignalMessage(signal)
        }
    }

    /**
     * 发起呼叫
     * @param targetUserId 目标用户ID
     * @param targetUserName 目标用户姓名（可选）
     * @return 返回呼叫ID
     */
    fun makeCall(targetUserId: String, targetUserName: String? = null): String? {
        val currentUserId = signalingClient.getCurrentUserId()
        if (currentUserId == null) {
            Log.e(TAG, "Cannot make call: not logged in")
            return null
        }

        if (callState != CallState.IDLE) {
            Log.e(TAG, "Cannot make call: already in a call (state=$callState)")
            return null
        }

        // 生成呼叫ID和房间ID
        val callId = "call_${System.currentTimeMillis()}_$currentUserId"
        val roomId = callId

        // 构建呼叫请求消息
        val extra = targetUserName?.let { mapOf("callerName" to it) }
        val signal = SignalMessageBuilder.createCallRequest(
            fromUserId = currentUserId,
            toUserId = targetUserId,
            extra = extra
        )

        // 更新状态
        callState = CallState.CALLING
        currentCallId = callId
        currentRoomId = roomId
        remoteUserId = targetUserId

        // 发送信令
        signalingClient.sendSignal(signal)

        Log.d(TAG, "Making call to $targetUserId, callId=$callId")

        return callId
    }

    /**
     * 接听呼叫
     * @param callId 呼叫ID
     */
    fun acceptCall(callId: String) {
        if (callState != CallState.INCOMING) {
            Log.e(TAG, "Cannot accept call: no incoming call")
            return
        }

        if (currentCallId != callId) {
            Log.e(TAG, "Cannot accept call: callId mismatch")
            return
        }

        val currentUserId = signalingClient.getCurrentUserId()
        val remoteUser = remoteUserId

        if (currentUserId == null || remoteUser == null) {
            Log.e(TAG, "Cannot accept call: invalid state")
            return
        }

        // 构建接听消息
        val signal = SignalMessageBuilder.createCallAccept(
            callId = callId,
            fromUserId = currentUserId,
            toUserId = remoteUser,
            roomId = currentRoomId!!
        )

        // 更新状态
        callState = CallState.CONNECTED

        // 发送信令
        signalingClient.sendSignal(signal)

        Log.d(TAG, "Accepted call $callId")

        // 回调通知
        onCallAccepted?.invoke(callId, currentRoomId!!)
    }

    /**
     * 拒绝呼叫
     * @param callId 呼叫ID
     * @param reason 拒绝原因（可选）
     */
    fun rejectCall(callId: String, reason: String? = null) {
        if (callState != CallState.INCOMING) {
            Log.e(TAG, "Cannot reject call: no incoming call")
            return
        }

        if (currentCallId != callId) {
            Log.e(TAG, "Cannot reject call: callId mismatch")
            return
        }

        val currentUserId = signalingClient.getCurrentUserId()
        val remoteUser = remoteUserId

        if (currentUserId == null || remoteUser == null) {
            Log.e(TAG, "Cannot reject call: invalid state")
            return
        }

        // 构建拒绝消息
        val signal = SignalMessageBuilder.createCallReject(
            callId = callId,
            fromUserId = currentUserId,
            toUserId = remoteUser,
            reason = reason
        )

        // 发送信令
        signalingClient.sendSignal(signal)

        // 重置状态
        resetCallState()

        Log.d(TAG, "Rejected call $callId")
    }

    /**
     * 取消呼叫（呼叫方主动取消）
     */
    fun cancelCall() {
        if (callState != CallState.CALLING) {
            Log.e(TAG, "Cannot cancel call: not in calling state")
            return
        }

        val callId = currentCallId
        val currentUserId = signalingClient.getCurrentUserId()
        val remoteUser = remoteUserId

        if (callId == null || currentUserId == null || remoteUser == null) {
            Log.e(TAG, "Cannot cancel call: invalid state")
            return
        }

        // 构建取消消息
        val signal = SignalMessageBuilder.createCallCancel(
            callId = callId,
            fromUserId = currentUserId,
            toUserId = remoteUser
        )

        // 发送信令
        signalingClient.sendSignal(signal)

        // 重置状态
        resetCallState()

        Log.d(TAG, "Cancelled call $callId")
    }

    /**
     * 挂断通话
     */
    fun endCall() {
        if (callState != CallState.CONNECTED) {
            Log.e(TAG, "Cannot end call: not connected")
            return
        }

        val callId = currentCallId
        val currentUserId = signalingClient.getCurrentUserId()
        val remoteUser = remoteUserId

        if (callId == null || currentUserId == null || remoteUser == null) {
            Log.e(TAG, "Cannot end call: invalid state")
            return
        }

        // 构建挂断消息
        val signal = SignalMessageBuilder.createCallEnd(
            callId = callId,
            fromUserId = currentUserId,
            toUserId = remoteUser
        )

        // 发送信令
        signalingClient.sendSignal(signal)

        // 重置状态
        resetCallState()

        Log.d(TAG, "Ended call $callId")
    }

    /**
     * 处理接收到的信令消息
     */
    private fun handleSignalMessage(signal: SignalMessage) {
        Log.d(TAG, "Handling signal: ${signal.type}, callId=${signal.callId}")

        when (signal.type) {
            SignalType.CALL_REQUEST -> handleIncomingCall(signal)
            SignalType.CALL_ACCEPT -> handleCallAccepted(signal)
            SignalType.CALL_REJECT -> handleCallRejected(signal)
            SignalType.CALL_CANCEL -> handleCallCancelled(signal)
            SignalType.CALL_END -> handleCallEnded(signal)
            SignalType.CALL_TIMEOUT -> handleCallTimeout(signal)
            SignalType.CALL_BUSY -> handleUserBusy(signal)
            else -> Log.d(TAG, "Unhandled signal type: ${signal.type}")
        }
    }

    /**
     * 处理来电请求
     */
    private fun handleIncomingCall(signal: SignalMessage) {
        if (callState != CallState.IDLE) {
            // 如果正在通话中，回复忙线
            val busySignal = SignalMessageBuilder.createCallBusy(
                callId = signal.callId,
                fromUserId = signal.toUserId,
                toUserId = signal.fromUserId
            )
            signalingClient.sendSignal(busySignal)
            return
        }

        // 更新状态
        callState = CallState.INCOMING
        currentCallId = signal.callId
        currentRoomId = signal.roomId
        remoteUserId = signal.fromUserId

        // 提取呼叫者姓名
        val callerName = signal.extra?.get("callerName")

        Log.d(TAG, "Incoming call from ${signal.fromUserId}, callId=${signal.callId}")

        // 回调通知
        onIncomingCall?.invoke(signal.callId, signal.fromUserId, callerName)
    }

    /**
     * 处理对方接听
     */
    private fun handleCallAccepted(signal: SignalMessage) {
        if (callState != CallState.CALLING || currentCallId != signal.callId) {
            return
        }

        // 更新状态
        callState = CallState.CONNECTED

        Log.d(TAG, "Call accepted: ${signal.callId}")

        // 回调通知
        onCallAccepted?.invoke(signal.callId, signal.roomId!!)
    }

    /**
     * 处理对方拒绝
     */
    private fun handleCallRejected(signal: SignalMessage) {
        if (callState != CallState.CALLING || currentCallId != signal.callId) {
            return
        }

        val reason = signal.extra?.get("reason")

        // 重置状态
        resetCallState()

        Log.d(TAG, "Call rejected: ${signal.callId}, reason=$reason")

        // 回调通知
        onCallRejected?.invoke(signal.callId, reason)
    }

    /**
     * 处理对方取消
     */
    private fun handleCallCancelled(signal: SignalMessage) {
        if (callState != CallState.INCOMING || currentCallId != signal.callId) {
            return
        }

        // 重置状态
        resetCallState()

        Log.d(TAG, "Call cancelled: ${signal.callId}")

        // 回调通知
        onCallCancelled?.invoke(signal.callId)
    }

    /**
     * 处理对方挂断
     */
    private fun handleCallEnded(signal: SignalMessage) {
        if (callState != CallState.CONNECTED || currentCallId != signal.callId) {
            return
        }

        // 重置状态
        resetCallState()

        Log.d(TAG, "Call ended: ${signal.callId}")

        // 回调通知
        onCallEnded?.invoke(signal.callId)
    }

    /**
     * 处理呼叫超时
     */
    private fun handleCallTimeout(signal: SignalMessage) {
        if (currentCallId != signal.callId) {
            return
        }

        // 重置状态
        resetCallState()

        Log.d(TAG, "Call timeout: ${signal.callId}")

        // 回调通知
        onCallTimeout?.invoke(signal.callId)
    }

    /**
     * 处理对方忙线
     */
    private fun handleUserBusy(signal: SignalMessage) {
        if (callState != CallState.CALLING || currentCallId != signal.callId) {
            return
        }

        // 重置状态
        resetCallState()

        Log.d(TAG, "User busy: ${signal.callId}")

        // 回调通知
        onUserBusy?.invoke(signal.callId)
    }

    /**
     * 重置呼叫状态
     */
    private fun resetCallState() {
        callState = CallState.IDLE
        currentCallId = null
        currentRoomId = null
        remoteUserId = null
    }

    // ========== 状态查询方法 ==========

    fun getCallState(): CallState = callState
    fun getCurrentCallId(): String? = currentCallId
    fun getCurrentRoomId(): String? = currentRoomId
    fun getRemoteUserId(): String? = remoteUserId

    // ========== 回调设置方法 ==========

    fun setOnIncomingCall(listener: (callId: String, fromUserId: String, fromUserName: String?) -> Unit) {
        onIncomingCall = listener
    }

    fun setOnCallAccepted(listener: (callId: String, roomId: String) -> Unit) {
        onCallAccepted = listener
    }

    fun setOnCallRejected(listener: (callId: String, reason: String?) -> Unit) {
        onCallRejected = listener
    }

    fun setOnCallCancelled(listener: (callId: String) -> Unit) {
        onCallCancelled = listener
    }

    fun setOnCallEnded(listener: (callId: String) -> Unit) {
        onCallEnded = listener
    }

    fun setOnCallTimeout(listener: (callId: String) -> Unit) {
        onCallTimeout = listener
    }

    fun setOnUserBusy(listener: (callId: String) -> Unit) {
        onUserBusy = listener
    }
}

/**
 * 呼叫状态枚举
 */
enum class CallState {
    IDLE,        // 空闲
    CALLING,     // 呼叫中（主叫方）
    INCOMING,    // 来电中（被叫方）
    CONNECTED    // 通话中
}
