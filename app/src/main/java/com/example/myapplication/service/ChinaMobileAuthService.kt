package com.example.myapplication.service

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * 中国移动认证服务
 * 模拟中国移动一键登录API调用
 */
class ChinaMobileAuthService(private val context: Context) {
    
    companion object {
        private const val TAG = "ChinaMobileAuth"
        private const val API_BASE_URL = "https://api.chinamobile.com"
        private const val APP_ID = "your_app_id" // 需要替换为实际的App ID
        private const val APP_SECRET = "your_app_secret" // 需要替换为实际的App Secret
    }
    
    /**
     * 执行手机号一键认证
     * @param phoneNumber 手机号
     * @param onSuccess 成功回调
     * @param onError 失败回调
     */
    suspend fun performOneClickAuth(
        phoneNumber: String,
        onSuccess: (AuthResult) -> Unit,
        onError: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始执行手机号认证: $phoneNumber")
            
            // 验证手机号格式
            if (!isValidPhoneNumber(phoneNumber)) {
                onError("手机号格式不正确")
                return@withContext
            }
            
            // 构建请求参数
            val requestData = buildRequestData(phoneNumber)
            
            // 发送认证请求
            val response = sendAuthRequest(requestData)
            
            // 解析响应
            val authResult = parseAuthResponse(response)
            
            if (authResult.isSuccess) {
                Log.d(TAG, "手机号认证成功")
                onSuccess(authResult)
            } else {
                Log.e(TAG, "手机号认证失败: ${authResult.message}")
                onError(authResult.message)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "认证过程中发生异常", e)
            onError("网络错误，请检查网络连接后重试")
        }
    }
    
    /**
     * 构建请求数据
     */
    private fun buildRequestData(phoneNumber: String): Map<String, String> {
        return mapOf(
            "appId" to APP_ID,
            "appSecret" to APP_SECRET,
            "phoneNumber" to phoneNumber,
            "timestamp" to System.currentTimeMillis().toString(),
            "version" to "1.0"
        )
    }
    
    /**
     * 发送认证请求
     */
    private fun sendAuthRequest(requestData: Map<String, String>): String {
        val url = URL("$API_BASE_URL/auth/oneclick")
        val connection = url.openConnection() as HttpURLConnection
        
        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.setRequestProperty("User-Agent", "SocialMeet/1.0")
            connection.doOutput = true
            connection.doInput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            // 构建POST数据
            val postData = requestData.entries.joinToString("&") { 
                "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
            }
            
            // 发送请求
            connection.outputStream.use { outputStream ->
                outputStream.write(postData.toByteArray())
            }
            
            // 读取响应
            val responseCode = connection.responseCode
            Log.d(TAG, "HTTP响应码: $responseCode")
            
            val inputStream = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            
            val response = BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readText()
            }
            
            Log.d(TAG, "API响应: $response")
            return response
            
        } finally {
            connection.disconnect()
        }
    }
    
    /**
     * 解析认证响应
     */
    private fun parseAuthResponse(response: String): AuthResult {
        return try {
            val json = JSONObject(response)
            val code = json.optInt("code", -1)
            val message = json.optString("message", "未知错误")
            val data = json.optJSONObject("data")
            
            if (code == 200 && data != null) {
                val isVerified = data.optBoolean("verified", false)
                val authToken = data.optString("authToken", "")
                val expireTime = data.optLong("expireTime", 0)
                
                AuthResult(
                    isSuccess = isVerified,
                    message = if (isVerified) "认证成功" else "认证失败",
                    authToken = authToken,
                    expireTime = expireTime
                )
            } else {
                AuthResult(
                    isSuccess = false,
                    message = message,
                    authToken = "",
                    expireTime = 0
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析响应失败", e)
            AuthResult(
                isSuccess = false,
                message = "响应解析失败",
                authToken = "",
                expireTime = 0
            )
        }
    }
    
    /**
     * 验证手机号格式
     */
    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("^1[3-9]\\d{9}$"))
    }
    
    /**
     * 认证结果数据类
     */
    data class AuthResult(
        val isSuccess: Boolean,
        val message: String,
        val authToken: String,
        val expireTime: Long
    )
}
