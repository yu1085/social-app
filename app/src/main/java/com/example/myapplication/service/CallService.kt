package com.example.myapplication.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * 通话服务类
 * 处理视频通话、语音通话相关的API调用
 */
class CallService private constructor() {
    
    companion object {
        private const val TAG = "CallService"
        private const val BASE_URL = "http://192.168.1.100:8080/api"
        
        @Volatile
        private var INSTANCE: CallService? = null
        
        fun getInstance(): CallService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CallService().also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 获取用户通话价格信息
     */
    suspend fun getUserCallPrices(token: String): CallPricesResult {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/call-settings")
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", "Bearer $token")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                val responseCode = connection.responseCode
                Log.d(TAG, "获取通话价格响应码: $responseCode")
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d(TAG, "通话价格响应: $response")
                    
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        val data = jsonResponse.getJSONObject("data")
                        val prices = CallPrices(
                            videoCallPrice = data.getDouble("videoCallPrice"),
                            voiceCallPrice = data.getDouble("voiceCallPrice"),
                            messagePrice = data.getDouble("messagePrice"),
                            videoCallEnabled = data.getBoolean("videoCallEnabled"),
                            voiceCallEnabled = data.getBoolean("voiceCallEnabled"),
                            messageChargeEnabled = data.getBoolean("messageChargeEnabled")
                        )
                        CallPricesResult.Success(prices)
                    } else {
                        CallPricesResult.Error(jsonResponse.getString("message"))
                    }
                } else {
                    val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "未知错误"
                    Log.e(TAG, "获取通话价格失败: $errorResponse")
                    CallPricesResult.Error("获取通话价格失败: $errorResponse")
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取通话价格异常", e)
                CallPricesResult.Error("网络异常: ${e.message}")
            }
        }
    }
    
    /**
     * 发起视频通话
     */
    suspend fun initiateVideoCall(token: String, receiverId: Long): CallInitiateResult {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/call/initiate")
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Bearer $token")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                // 构建请求体
                val requestBody = JSONObject().apply {
                    put("receiverId", receiverId)
                    put("callType", "VIDEO")
                }
                
                connection.outputStream.use { outputStream ->
                    outputStream.write(requestBody.toString().toByteArray())
                }
                
                val responseCode = connection.responseCode
                Log.d(TAG, "发起视频通话响应码: $responseCode")
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d(TAG, "发起视频通话响应: $response")
                    
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        val data = jsonResponse.getJSONObject("data")
                        val callSession = CallSession(
                            callSessionId = data.getString("callSessionId"),
                            callerId = data.getLong("callerId"),
                            receiverId = data.getLong("receiverId"),
                            callType = data.getString("callType"),
                            status = data.getString("status")
                        )
                        CallInitiateResult.Success(callSession)
                    } else {
                        CallInitiateResult.Error(jsonResponse.getString("message"))
                    }
                } else {
                    val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "未知错误"
                    Log.e(TAG, "发起视频通话失败: $errorResponse")
                    CallInitiateResult.Error("发起视频通话失败: $errorResponse")
                }
            } catch (e: Exception) {
                Log.e(TAG, "发起视频通话异常", e)
                CallInitiateResult.Error("网络异常: ${e.message}")
            }
        }
    }
    
    /**
     * 发起语音通话
     */
    suspend fun initiateVoiceCall(token: String, receiverId: Long): CallInitiateResult {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/call/initiate")
                val connection = url.openConnection() as HttpURLConnection
                
                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Bearer $token")
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                
                // 构建请求体
                val requestBody = JSONObject().apply {
                    put("receiverId", receiverId)
                    put("callType", "VOICE")
                }
                
                connection.outputStream.use { outputStream ->
                    outputStream.write(requestBody.toString().toByteArray())
                }
                
                val responseCode = connection.responseCode
                Log.d(TAG, "发起语音通话响应码: $responseCode")
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d(TAG, "发起语音通话响应: $response")
                    
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        val data = jsonResponse.getJSONObject("data")
                        val callSession = CallSession(
                            callSessionId = data.getString("callSessionId"),
                            callerId = data.getLong("callerId"),
                            receiverId = data.getLong("receiverId"),
                            callType = data.getString("callType"),
                            status = data.getString("status")
                        )
                        CallInitiateResult.Success(callSession)
                    } else {
                        CallInitiateResult.Error(jsonResponse.getString("message"))
                    }
                } else {
                    val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "未知错误"
                    Log.e(TAG, "发起语音通话失败: $errorResponse")
                    CallInitiateResult.Error("发起语音通话失败: $errorResponse")
                }
            } catch (e: Exception) {
                Log.e(TAG, "发起语音通话异常", e)
                CallInitiateResult.Error("网络异常: ${e.message}")
            }
        }
    }
}

/**
 * 通话价格数据类
 */
data class CallPrices(
    val videoCallPrice: Double,
    val voiceCallPrice: Double,
    val messagePrice: Double,
    val videoCallEnabled: Boolean,
    val voiceCallEnabled: Boolean,
    val messageChargeEnabled: Boolean
)

/**
 * 通话会话数据类
 */
data class CallSession(
    val callSessionId: String,
    val callerId: Long,
    val receiverId: Long,
    val callType: String,
    val status: String
)

/**
 * 获取通话价格结果
 */
sealed class CallPricesResult {
    data class Success(val prices: CallPrices) : CallPricesResult()
    data class Error(val message: String) : CallPricesResult()
}

/**
 * 发起通话结果
 */
sealed class CallInitiateResult {
    data class Success(val callSession: CallSession) : CallInitiateResult()
    data class Error(val message: String) : CallInitiateResult()
}
