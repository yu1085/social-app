package com.example.myapplication.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * 身份证验证API服务
 * 调用后端身份证二要素核验接口
 */
class IdCardApiService {
    
    companion object {
        private const val TAG = "IdCardApiService"
        // 模拟器环境使用10.0.2.2访问宿主机localhost
        private const val BASE_URL = "http://10.0.2.2:8080" // 后端服务地址
        private const val VERIFY_ENDPOINT = "/api/auth/id-card/verify"
    }
    
    /**
     * 验证身份证信息
     * @param name 真实姓名
     * @param idCard 身份证号码
     * @param token JWT认证token
     * @return 验证结果
     */
    suspend fun verifyIdCard(name: String, idCard: String, token: String? = null): IdCardVerifyResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始调用身份证验证API: name=$name, idCard=$idCard")
            
            val url = URL("$BASE_URL$VERIFY_ENDPOINT")
            val connection = url.openConnection() as HttpURLConnection
            
            // 设置请求方法和头部
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            
            // 添加认证头（如果有token）
            token?.let {
                connection.setRequestProperty("Authorization", "Bearer $it")
            }
            
            // 设置请求体
            val requestBody = JSONObject().apply {
                put("certName", name)
                put("certNo", idCard)
            }
            
            connection.doOutput = true
            val outputStream = connection.outputStream
            val writer = OutputStreamWriter(outputStream)
            writer.write(requestBody.toString())
            writer.flush()
            writer.close()
            
            // 读取响应
            val responseCode = connection.responseCode
            Log.d(TAG, "API响应码: $responseCode")
            
            val inputStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            
            val response = BufferedReader(inputStream.reader()).use { it.readText() }
            Log.d(TAG, "API响应: $response")
            
            // 解析响应
            val jsonResponse = JSONObject(response)
            val success = jsonResponse.optBoolean("success", false)
            val data = jsonResponse.optJSONObject("data")
            
            if (success && data != null) {
                val match = data.optBoolean("match", false)
                val message = data.optString("message", "")
                val verifyId = data.optString("verifyId", "")
                val certifyId = data.optString("certifyId", "")
                
                IdCardVerifyResult(
                    success = true,
                    match = match,
                    message = message,
                    verifyId = verifyId,
                    certifyId = certifyId
                )
            } else {
                val errorMessage = jsonResponse.optString("message", "验证失败")
                IdCardVerifyResult(
                    success = false,
                    match = false,
                    message = errorMessage,
                    verifyId = "",
                    certifyId = ""
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "身份证验证API调用失败", e)
            IdCardVerifyResult(
                success = false,
                match = false,
                message = "网络错误: ${e.message}",
                verifyId = "",
                certifyId = ""
            )
        }
    }
    
    /**
     * 获取验证状态
     */
    suspend fun getVerificationStatus(token: String? = null): IdCardStatusResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "获取身份证验证状态")
            
            val url = URL("$BASE_URL/api/auth/id-card/status")
            val connection = url.openConnection() as HttpURLConnection
            
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")
            
            token?.let {
                connection.setRequestProperty("Authorization", "Bearer $it")
            }
            
            val responseCode = connection.responseCode
            val response = BufferedReader(connection.inputStream.reader()).use { it.readText() }
            
            Log.d(TAG, "状态查询响应: $response")
            
            val jsonResponse = JSONObject(response)
            val success = jsonResponse.optBoolean("success", false)
            val data = jsonResponse.optJSONObject("data")
            
            if (success && data != null) {
                val status = data.optString("status", "UNKNOWN")
                val message = data.optString("message", "")
                
                IdCardStatusResult(
                    success = true,
                    status = status,
                    message = message
                )
            } else {
                IdCardStatusResult(
                    success = false,
                    status = "ERROR",
                    message = jsonResponse.optString("message", "查询失败")
                )
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "获取验证状态失败", e)
            IdCardStatusResult(
                success = false,
                status = "ERROR",
                message = "网络错误: ${e.message}"
            )
        }
    }
}

/**
 * 身份证验证结果
 */
data class IdCardVerifyResult(
    val success: Boolean,
    val match: Boolean,
    val message: String,
    val verifyId: String,
    val certifyId: String
)

/**
 * 身份证验证状态结果
 */
data class IdCardStatusResult(
    val success: Boolean,
    val status: String,
    val message: String
)
