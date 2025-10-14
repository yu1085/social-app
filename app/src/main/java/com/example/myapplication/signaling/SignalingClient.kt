package com.example.myapplication.signaling

import android.content.Context
import android.util.Log
import java.io.File

/**
 * 信令客户端（基于文件系统实现）
 * 使用文件系统实现跨模拟器通信
 */
class SignalingClient private constructor(private val context: Context) {

    companion object {
        private const val TAG = "SignalingClient"
        private var instance: SignalingClient? = null

        /**
         * 获取单例实例
         */
        fun getInstance(context: Context): SignalingClient {
            return instance ?: synchronized(this) {
                instance ?: SignalingClient(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    // 当前用户ID
    private var currentUserId: String? = null

    // 信令消息监听器
    private var onSignalReceived: ((SignalMessage) -> Unit)? = null

    // 连接状态监听器
    private var onConnected: (() -> Unit)? = null
    private var onDisconnected: (() -> Unit)? = null

    // 信令监控
    private val signalHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var isMonitoring = false

    /**
     * 连接到信令服务
     * @param userId 当前用户ID
     */
    fun connect(userId: String) {
        currentUserId = userId

        Log.d(TAG, "Connected with userId: $userId")

        // 开始监听信令文件
        startSignalMonitoring()

        // 触发连接成功回调
        onConnected?.invoke()
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        // 停止监听
        stopSignalMonitoring()

        currentUserId = null
        Log.d(TAG, "Disconnected")

        onDisconnected?.invoke()
    }

    /**
     * 发送信令消息
     * 使用共享内部存储实现跨模拟器通信
     */
    fun sendSignal(signal: SignalMessage) {
        Log.d(TAG, "Sending signal: ${signal.type} to ${signal.toUserId}")

        try {
            // 使用应用内部存储的 signals 目录
            val signalsDir = File(context.filesDir, "signals")
            signalsDir.mkdirs()

            // 文件名：目标用户ID_时间戳.json
            val fileName = "${signal.toUserId}_${System.currentTimeMillis()}.json"
            val file = File(signalsDir, fileName)

            // 写入JSON
            file.writeText(signalToJson(signal))

            Log.d(TAG, "✅ Signal saved: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to save signal", e)
        }
    }

    /**
     * 检查是否有发给当前用户的信令
     */
    private fun checkIncomingSignals() {
        try {
            val signalsDir = File(context.filesDir, "signals")
            if (!signalsDir.exists()) return

            val userId = currentUserId ?: return

            // 查找发给当前用户的文件
            val files = signalsDir.listFiles { file ->
                file.name.startsWith("${userId}_") && file.extension == "json"
            } ?: return

            if (files.isNotEmpty()) {
                Log.d(TAG, "📨 Found ${files.size} signal(s) for $userId")
            }

            files.forEach { file ->
                try {
                    val json = file.readText()
                    val signal = jsonToSignal(json)

                    Log.d(TAG, "📥 Received signal: ${signal.type} from ${signal.fromUserId}")

                    // 触发接收回调
                    onSignalReceived?.invoke(signal)

                    // 删除已处理的文件
                    file.delete()
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Failed to process signal file: ${file.name}", e)
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to check incoming signals", e)
        }
    }

    /**
     * 开始监听信令
     */
    private fun startSignalMonitoring() {
        if (isMonitoring) return
        isMonitoring = true

        val monitorRunnable = object : Runnable {
            override fun run() {
                if (isMonitoring) {
                    checkIncomingSignals()
                    signalHandler.postDelayed(this, 500) // 每500ms检查一次
                }
            }
        }
        signalHandler.post(monitorRunnable)
        Log.d(TAG, "🔍 Signal monitoring started for user: $currentUserId")
    }

    /**
     * 停止监听信令
     */
    private fun stopSignalMonitoring() {
        isMonitoring = false
        signalHandler.removeCallbacksAndMessages(null)
        Log.d(TAG, "⏹️ Signal monitoring stopped")
    }

    /**
     * 信令转JSON
     */
    private fun signalToJson(signal: SignalMessage): String {
        val extraJson = signal.extra?.entries?.joinToString(",") {
            """"${it.key}":"${it.value}""""
        } ?: ""

        return """
{
  "type": "${signal.type}",
  "callId": "${signal.callId}",
  "fromUserId": "${signal.fromUserId}",
  "toUserId": "${signal.toUserId}",
  "roomId": "${signal.roomId ?: ""}",
  "timestamp": ${signal.timestamp},
  "extra": {$extraJson}
}
        """.trimIndent()
    }

    /**
     * JSON转信令
     */
    private fun jsonToSignal(json: String): SignalMessage {
        val typeRegex = """"type":\s*"([^"]+)"""".toRegex()
        val callIdRegex = """"callId":\s*"([^"]+)"""".toRegex()
        val fromUserIdRegex = """"fromUserId":\s*"([^"]+)"""".toRegex()
        val toUserIdRegex = """"toUserId":\s*"([^"]+)"""".toRegex()
        val roomIdRegex = """"roomId":\s*"([^"]*?)"""".toRegex()
        val timestampRegex = """"timestamp":\s*(\d+)""".toRegex()

        return SignalMessage(
            type = SignalType.valueOf(typeRegex.find(json)?.groupValues?.get(1) ?: "HEARTBEAT"),
            callId = callIdRegex.find(json)?.groupValues?.get(1) ?: "",
            fromUserId = fromUserIdRegex.find(json)?.groupValues?.get(1) ?: "",
            toUserId = toUserIdRegex.find(json)?.groupValues?.get(1) ?: "",
            roomId = roomIdRegex.find(json)?.groupValues?.get(1)?.takeIf { it.isNotEmpty() },
            timestamp = timestampRegex.find(json)?.groupValues?.get(1)?.toLongOrNull() ?: System.currentTimeMillis(),
            extra = null // 简化处理，不解析extra字段
        )
    }

    /**
     * 设置信令接收监听器
     */
    fun setOnSignalReceived(listener: (SignalMessage) -> Unit) {
        onSignalReceived = listener
    }

    /**
     * 设置连接状态监听器
     */
    fun setOnConnected(listener: () -> Unit) {
        onConnected = listener
    }

    fun setOnDisconnected(listener: () -> Unit) {
        onDisconnected = listener
    }

    /**
     * 获取当前用户ID
     */
    fun getCurrentUserId(): String? {
        return currentUserId
    }

    /**
     * 获取Registration ID (文件系统模式不需要)
     */
    fun getRegistrationId(): String {
        return currentUserId ?: ""
    }

    /**
     * 检查推送服务是否已连接
     */
    fun isConnected(): Boolean {
        return currentUserId != null && isMonitoring
    }
}
