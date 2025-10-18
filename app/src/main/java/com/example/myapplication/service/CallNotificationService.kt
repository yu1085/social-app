package com.example.myapplication.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.IncomingCallActivity
import com.example.myapplication.R
import com.example.myapplication.auth.AuthManager
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * 通话通知服务
 * 用于监听来电并显示通知
 */
class CallNotificationService : Service() {

    private val TAG = "CallNotificationService"
    private val CHANNEL_ID = "call_notification_channel"
    private val NOTIFICATION_ID = 1001

    private var isMonitoring = false
    private val monitorHandler = Handler(Looper.getMainLooper())
    private val monitorInterval = 5000L // 5秒轮询一次

    private lateinit var authManager: AuthManager
    private var userToken: String? = null

    companion object {
        private var serviceInstance: CallNotificationService? = null

        /**
         * 启动通话监听服务
         */
        fun startService(context: Context) {
            val intent = Intent(context, CallNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        /**
         * 停止通话监听服务
         */
        fun stopService(context: Context) {
            val intent = Intent(context, CallNotificationService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        serviceInstance = this
        authManager = AuthManager.getInstance(this)
        userToken = authManager.token

        Log.d(TAG, "通话通知服务已创建")

        // 创建通知渠道
        createNotificationChannel()

        // 启动前台服务
        startForeground(NOTIFICATION_ID, createForegroundNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "通话通知服务已启动")

        // 开始监听来电
        startMonitoring()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "通话通知服务已停止")
        stopMonitoring()
        serviceInstance = null
    }

    /**
     * 创建通知渠道（Android 8.0+）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "通话通知",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "用于显示来电通知"
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 创建前台服务通知
     */
    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("通话服务")
            .setContentText("正在监听来电...")
            .setSmallIcon(R.drawable.ic_call)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /**
     * 开始监听来电
     */
    private fun startMonitoring() {
        if (isMonitoring) {
            Log.d(TAG, "已经在监听中，不重复启动")
            return
        }

        if (userToken == null) {
            Log.w(TAG, "用户未登录，无法监听来电")
            return
        }

        isMonitoring = true
        Log.d(TAG, "开始监听来电")

        // 定期检查来电
        monitorHandler.post(monitorRunnable)
    }

    /**
     * 停止监听来电
     */
    private fun stopMonitoring() {
        isMonitoring = false
        monitorHandler.removeCallbacks(monitorRunnable)
        Log.d(TAG, "停止监听来电")
    }

    /**
     * 监听来电的Runnable
     */
    private val monitorRunnable = object : Runnable {
        override fun run() {
            if (!isMonitoring) return

            // 异步检查来电
            CoroutineScope(Dispatchers.IO).launch {
                checkIncomingCalls()
            }

            // 继续下一次检查
            monitorHandler.postDelayed(this, monitorInterval)
        }
    }

    /**
     * 检查是否有来电
     */
    private suspend fun checkIncomingCalls() {
        withContext(Dispatchers.IO) {
            try {
                if (userToken == null) {
                    Log.w(TAG, "Token为空，跳过检查")
                    return@withContext
                }

                val url = URL("http://10.0.2.2:8080/api/call/pending")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", "Bearer $userToken")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d(TAG, "检查来电响应: $response")

                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        val data = jsonResponse.optJSONObject("data")

                        if (data != null) {
                            // 有来电
                            val callSessionId = data.getString("callSessionId")
                            val callerId = data.getLong("callerId")
                            val callerName = data.optString("callerName", "未知用户")
                            val callerAvatar = data.optString("callerAvatar", "")
                            val callType = data.getString("callType")

                            Log.d(TAG, "收到来电: $callSessionId, 来电人: $callerName, 类型: $callType")

                            // 显示来电通知界面
                            withContext(Dispatchers.Main) {
                                showIncomingCallNotification(
                                    callSessionId,
                                    callerId,
                                    callerName,
                                    callerAvatar,
                                    callType
                                )
                            }
                        } else {
                            Log.d(TAG, "暂无来电")
                        }
                    }
                } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                    // 404说明后端还没实现这个API，暂时忽略
                    Log.d(TAG, "后端API未实现: /api/call/pending")
                } else {
                    Log.w(TAG, "检查来电失败: $responseCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "检查来电异常", e)
            }
        }
    }

    /**
     * 显示来电通知
     */
    private fun showIncomingCallNotification(
        callSessionId: String,
        callerId: Long,
        callerName: String,
        callerAvatar: String,
        callType: String
    ) {
        // 启动全屏来电Activity
        val intent = Intent(this, IncomingCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("CALL_SESSION_ID", callSessionId)
            putExtra("CALLER_ID", callerId)
            putExtra("CALLER_NAME", callerName)
            putExtra("CALLER_AVATAR", callerAvatar)
            putExtra("CALL_TYPE", callType)
        }

        startActivity(intent)
    }
}
