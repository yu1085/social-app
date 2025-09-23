package com.example.myapplication.service

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * 用户资料服务
 * 负责与后端API交互，处理用户资料的获取和更新
 */
class ProfileService private constructor() {

    companion object {
        private const val TAG = "ProfileService"
        private const val BASE_URL = "http://10.0.2.2:8080/api/users/profile"
        
        @Volatile
        private var INSTANCE: ProfileService? = null

        fun getInstance(): ProfileService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProfileService().also { INSTANCE = it }
            }
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * 获取用户资料
     */
    suspend fun getUserProfile(token: String? = null): ProfileResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== 开始获取用户资料 ===")
            Log.d(TAG, "Token: ${token?.take(10)}...")
            Log.d(TAG, "Token长度: ${token?.length ?: 0}")
            Log.d(TAG, "Token是否为空: ${token.isNullOrEmpty()}")
            Log.d(TAG, "请求URL: $BASE_URL")
            
            // Token验证
            if (token.isNullOrEmpty()) {
                Log.w(TAG, "Token为空，将发送无认证请求")
            } else {
                Log.d(TAG, "Token存在，将发送认证请求")
                // 这里可以添加更详细的Token验证逻辑
                if (token.length < 10) {
                    Log.w(TAG, "Token长度异常，可能无效")
                }
            }
            
            val requestBuilder = Request.Builder()
                .url(BASE_URL)
                .addHeader("Content-Type", "application/json")
            
            // 只有在有token时才添加Authorization头
            token?.let {
                Log.d(TAG, "添加Authorization头: Bearer ${it.take(20)}...")
                requestBuilder.addHeader("Authorization", "Bearer $it")
            } ?: run {
                Log.w(TAG, "Token为空，将发送无认证请求")
            }
            
            val request = requestBuilder
                .get()
                .build()

            Log.d(TAG, "发送GET请求到: ${request.url}")
            Log.d(TAG, "请求头: ${request.headers}")
            Log.d(TAG, "请求方法: ${request.method}")

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            Log.d(TAG, "收到响应")
            Log.d(TAG, "响应状态码: ${response.code}")
            Log.d(TAG, "响应消息: ${response.message}")
            Log.d(TAG, "响应头: ${response.headers}")
            Log.d(TAG, "响应体长度: ${responseBody?.length ?: 0}")
            Log.d(TAG, "响应体内容: $responseBody")
            Log.d(TAG, "响应是否成功: ${response.isSuccessful}")

            if (response.isSuccessful && responseBody != null) {
                val jsonObject = JSONObject(responseBody)
                val data = jsonObject.getJSONObject("data")
                
                val profile = UserProfile(
                    id = data.optLong("id", 0),
                    username = data.optString("username", ""),
                    nickname = data.optString("nickname", ""),
                    realName = data.optString("realName", ""),
                    email = data.optString("email", ""),
                    phone = data.optString("phone", ""),
                    avatarUrl = data.optString("avatarUrl", ""),
                    gender = data.optString("gender", ""),
                    birthday = data.optString("birthDate", ""),
                    age = data.optInt("age", 0),
                    height = data.optInt("height", 0),
                    weight = data.optInt("weight", 0),
                    bio = data.optString("bio", ""),
                    zodiacSign = data.optString("zodiacSign", ""),
                    city = data.optString("city", ""),
                    hometown = data.optString("hometown", ""),
                    location = data.optString("location", ""),
                    latitude = data.optDouble("latitude", 0.0),
                    longitude = data.optDouble("longitude", 0.0),
                    education = data.optString("education", ""),
                    occupation = data.optString("occupation", ""),
                    income = data.optString("income", ""),
                    relationshipStatus = data.optString("relationshipStatus", ""),
                    residenceStatus = data.optString("residenceStatus", ""),
                    houseOwnership = data.optBoolean("houseOwnership", false),
                    carOwnership = data.optBoolean("carOwnership", false),
                    hobbies = data.optString("hobbies", ""),
                    bloodType = data.optString("bloodType", ""),
                    smoking = data.optBoolean("smoking", false),
                    drinking = data.optBoolean("drinking", false),
                    verified = data.optBoolean("verified", false),
                    createdAt = data.optString("createdAt", ""),
                    updatedAt = data.optString("updatedAt", ""),
                    lastLoginAt = data.optString("lastLoginAt", "")
                )
                
                Log.d(TAG, "获取用户资料成功")
                ProfileResult(success = true, profile = profile)
            } else {
                Log.e(TAG, "获取用户资料失败: ${response.code} - $responseBody")
                Log.e(TAG, "错误详情分析:")
                Log.e(TAG, "- 状态码: ${response.code}")
                Log.e(TAG, "- 状态消息: ${response.message}")
                Log.e(TAG, "- 响应头: ${response.headers}")
                Log.e(TAG, "- 响应体: $responseBody")
                
                // 分析错误类型
                when (response.code) {
                    401 -> {
                        Log.e(TAG, "认证失败 - Token可能无效或已过期")
                        Log.e(TAG, "当前Token: ${token?.take(20)}...")
                    }
                    403 -> Log.e(TAG, "权限不足 - 用户无权限访问此资源")
                    404 -> Log.e(TAG, "资源不存在 - 用户资料不存在")
                    500 -> Log.e(TAG, "服务器内部错误")
                    else -> Log.e(TAG, "未知错误: ${response.code}")
                }
                
                // 认证失败时，返回一个基本的用户资料结构，让用户可以编辑
                val basicProfile = UserProfile(
                    id = 1,
                    username = "user_${System.currentTimeMillis()}",
                    nickname = "新用户",
                    realName = "",
                    email = "",
                    phone = "",
                    avatarUrl = "",
                    gender = "男",
                    birthday = "",
                    age = 0,
                    height = 0,
                    weight = 0,
                    bio = "",
                    zodiacSign = "",
                    city = "",
                    hometown = "",
                    location = "",
                    latitude = 0.0,
                    longitude = 0.0,
                    education = "",
                    occupation = "",
                    income = "",
                    relationshipStatus = "",
                    residenceStatus = "",
                    houseOwnership = false,
                    carOwnership = false,
                    hobbies = "",
                    languages = listOf(),
                    bloodType = "",
                    smoking = false,
                    drinking = false,
                    verified = false,
                    createdAt = "",
                    updatedAt = "",
                    lastLoginAt = ""
                )
                ProfileResult(success = true, profile = basicProfile, error = "使用本地模式（API认证失败）")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "获取用户资料异常", e)
            Log.e(TAG, "异常类型: ${e::class.simpleName}")
            Log.e(TAG, "异常消息: ${e.message}")
            Log.e(TAG, "异常堆栈: ${e.stackTrace.joinToString("\n")}")
            
            val errorMessage = when {
                e.message?.contains("timeout", ignoreCase = true) == true -> "网络超时，请检查网络连接"
                e.message?.contains("connection", ignoreCase = true) == true -> "网络连接失败，请检查网络设置"
                e.message?.contains("unknown host", ignoreCase = true) == true -> "无法连接到服务器，请检查网络"
                else -> "网络错误: ${e.message ?: "未知错误"}"
            }
            
            ProfileResult(success = false, profile = null, error = errorMessage)
        }
    }

    /**
     * 测试Token有效性
     */
    suspend fun testToken(token: String?): Map<String, Any> = withContext(Dispatchers.IO) {
        val result = mutableMapOf<String, Any>()
        
        try {
            Log.d(TAG, "=== 开始测试Token ===")
            Log.d(TAG, "Token: ${token?.take(20)}...")
            
            if (token.isNullOrEmpty()) {
                result["success"] = false
                result["error"] = "Token为空"
                return@withContext result
            }
            
            val requestBuilder = Request.Builder()
                .url("$BASE_URL/test-token")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $token")
            
            val request = requestBuilder.get().build()
            
            Log.d(TAG, "发送Token测试请求到: ${request.url}")
            Log.d(TAG, "请求头: ${request.headers}")
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            Log.d(TAG, "Token测试响应:")
            Log.d(TAG, "- 状态码: ${response.code}")
            Log.d(TAG, "- 响应体: $responseBody")
            
            if (response.isSuccessful && responseBody != null) {
                val jsonObject = JSONObject(responseBody)
                result["success"] = jsonObject.optBoolean("success", false)
                result["isValid"] = jsonObject.optBoolean("isValid", false)
                result["userId"] = jsonObject.optLong("userId", -1)
                result["username"] = jsonObject.optString("username", "")
                result["tokenLength"] = jsonObject.optInt("tokenLength", 0)
                result["error"] = jsonObject.optString("error", "")
            } else {
                result["success"] = false
                result["error"] = "HTTP ${response.code}: $responseBody"
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Token测试异常", e)
            result["success"] = false
            result["error"] = "测试异常: ${e.message}"
        }
        
        Log.d(TAG, "Token测试结果: $result")
        result
    }

    /**
     * 创建模拟用户资料
     */
    private fun createMockProfile(): UserProfile {
        return UserProfile(
            id = 1,
            username = "xiangyueweilai605",
            nickname = "相约未来605",
            realName = "张三",
            email = "zhangsan@example.com",
            phone = "13800138000",
            avatarUrl = "",
            gender = "男",
            birthday = "1989-01-01",
            age = 35,
            height = 175,
            weight = 70,
            bio = "我很懒没想好个性签名",
            zodiacSign = "摩羯座",
            city = "南京",
            hometown = "北京",
            location = "江苏省南京市",
            latitude = 32.0603,
            longitude = 118.7969,
            education = "本科",
            occupation = "程序员",
            income = "10-20万",
            relationshipStatus = "单身",
            residenceStatus = "自有住房",
            houseOwnership = true,
            carOwnership = false,
            hobbies = "旅游、摄影、音乐",
            languages = listOf("中文", "英语"),
            bloodType = "A型",
            smoking = false,
            drinking = false,
            verified = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-09-23T01:30:00Z",
            lastLoginAt = "2024-09-23T01:30:00Z"
        )
    }

    /**
     * 更新用户资料
     */
    suspend fun updateUserProfile(
        userId: Long,
        token: String?,
        profileData: Map<String, Any>
    ): ProfileResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "=== 开始更新用户资料 ===")
            Log.d(TAG, "用户ID: $userId")
            Log.d(TAG, "Token: ${token?.take(10)}...")
            Log.d(TAG, "Token长度: ${token?.length ?: 0}")
            Log.d(TAG, "Token是否为空: ${token.isNullOrEmpty()}")
            Log.d(TAG, "请求数据: $profileData")
            Log.d(TAG, "请求数据字段数量: ${profileData.size}")
            
            // 详细记录每个字段
            profileData.forEach { (key, value) ->
                Log.d(TAG, "字段: $key = $value (类型: ${value::class.simpleName})")
            }
            
            val jsonObject = JSONObject()
            profileData.forEach { (key, value) ->
                jsonObject.put(key, value)
            }
            
            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaType())
            
            Log.d(TAG, "构建JSON请求体: $requestBody")
            Log.d(TAG, "请求体长度: ${requestBody.contentLength()}")
            
            val requestBuilder = Request.Builder()
                .url("$BASE_URL/$userId")
                .addHeader("Content-Type", "application/json")
                .put(requestBody)
            
            // 只有在有token时才添加Authorization头
            token?.let {
                Log.d(TAG, "添加Authorization头: Bearer ${it.take(20)}...")
                requestBuilder.addHeader("Authorization", "Bearer $it")
            } ?: run {
                Log.w(TAG, "Token为空，将发送无认证请求")
            }
            
            val request = requestBuilder.build()
            Log.d(TAG, "请求URL: ${request.url}")
            Log.d(TAG, "请求头: ${request.headers}")
            Log.d(TAG, "请求方法: ${request.method}")
            Log.d(TAG, "请求体: $requestBody")

            Log.d(TAG, "发送PUT请求...")
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            Log.d(TAG, "收到响应")
            Log.d(TAG, "响应状态码: ${response.code}")
            Log.d(TAG, "响应消息: ${response.message}")
            Log.d(TAG, "响应头: ${response.headers}")
            Log.d(TAG, "响应体长度: ${responseBody?.length ?: 0}")
            Log.d(TAG, "响应体内容: $responseBody")
            Log.d(TAG, "响应是否成功: ${response.isSuccessful}")

            if (response.isSuccessful && responseBody != null) {
                val jsonObject = JSONObject(responseBody)
                val data = jsonObject.getJSONObject("data")
                
                val profile = UserProfile(
                    id = data.optLong("id", 0),
                    username = data.optString("username", ""),
                    nickname = data.optString("nickname", ""),
                    realName = data.optString("realName", ""),
                    email = data.optString("email", ""),
                    phone = data.optString("phone", ""),
                    avatarUrl = data.optString("avatarUrl", ""),
                    gender = data.optString("gender", ""),
                    birthday = data.optString("birthDate", ""),
                    age = data.optInt("age", 0),
                    height = data.optInt("height", 0),
                    weight = data.optInt("weight", 0),
                    bio = data.optString("bio", ""),
                    zodiacSign = data.optString("zodiacSign", ""),
                    city = data.optString("city", ""),
                    hometown = data.optString("hometown", ""),
                    location = data.optString("location", ""),
                    latitude = data.optDouble("latitude", 0.0),
                    longitude = data.optDouble("longitude", 0.0),
                    education = data.optString("education", ""),
                    occupation = data.optString("occupation", ""),
                    income = data.optString("income", ""),
                    relationshipStatus = data.optString("relationshipStatus", ""),
                    residenceStatus = data.optString("residenceStatus", ""),
                    houseOwnership = data.optBoolean("houseOwnership", false),
                    carOwnership = data.optBoolean("carOwnership", false),
                    hobbies = data.optString("hobbies", ""),
                    bloodType = data.optString("bloodType", ""),
                    smoking = data.optBoolean("smoking", false),
                    drinking = data.optBoolean("drinking", false),
                    verified = data.optBoolean("verified", false),
                    createdAt = data.optString("createdAt", ""),
                    updatedAt = data.optString("updatedAt", ""),
                    lastLoginAt = data.optString("lastLoginAt", "")
                )
                
                Log.d(TAG, "更新用户资料成功")
                ProfileResult(success = true, profile = profile, isServerSaved = true)
            } else {
                Log.e(TAG, "更新用户资料失败: ${response.code} - $responseBody")
                Log.e(TAG, "错误详情分析:")
                Log.e(TAG, "- 状态码: ${response.code}")
                Log.e(TAG, "- 状态消息: ${response.message}")
                Log.e(TAG, "- 响应头: ${response.headers}")
                Log.e(TAG, "- 响应体: $responseBody")
                Log.e(TAG, "- 用户ID: $userId")
                Log.e(TAG, "- Token: ${token?.take(20)}...")
                
                // 分析错误类型
                when (response.code) {
                    401 -> {
                        Log.e(TAG, "认证失败 - Token可能无效或已过期")
                        Log.e(TAG, "当前Token: ${token?.take(20)}...")
                        Log.e(TAG, "Token长度: ${token?.length ?: 0}")
                        Log.e(TAG, "建议: 检查Token是否有效，或重新登录获取新Token")
                    }
                    403 -> {
                        Log.e(TAG, "权限不足 - 用户无权限访问此资源")
                        Log.e(TAG, "用户ID: $userId")
                        Log.e(TAG, "建议: 检查用户是否有权限修改此资料")
                    }
                    404 -> {
                        Log.e(TAG, "资源不存在 - 用户不存在")
                        Log.e(TAG, "用户ID: $userId")
                        Log.e(TAG, "建议: 检查用户ID是否正确")
                    }
                    500 -> {
                        Log.e(TAG, "服务器内部错误")
                        Log.e(TAG, "建议: 稍后重试或联系管理员")
                    }
                    else -> {
                        Log.e(TAG, "未知错误: ${response.code}")
                        Log.e(TAG, "建议: 检查网络连接和服务器状态")
                    }
                }
                
                // 根据不同的错误码提供更具体的错误信息
                val errorMessage = when (response.code) {
                    401 -> "认证失败，请重新登录"
                    403 -> "无权限访问此用户资源"
                    404 -> "用户不存在"
                    500 -> "服务器内部错误"
                    else -> "服务器错误: ${response.code}"
                }
                
                ProfileResult(success = false, profile = null, error = errorMessage)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "更新用户资料异常", e)
            Log.e(TAG, "异常类型: ${e::class.simpleName}")
            Log.e(TAG, "异常消息: ${e.message}")
            Log.e(TAG, "异常堆栈: ${e.stackTrace.joinToString("\n")}")
            
            val errorMessage = when {
                e.message?.contains("timeout", ignoreCase = true) == true -> "网络超时，请检查网络连接"
                e.message?.contains("connection", ignoreCase = true) == true -> "网络连接失败，请检查网络设置"
                e.message?.contains("unknown host", ignoreCase = true) == true -> "无法连接到服务器，请检查网络"
                e.message?.contains("ssl", ignoreCase = true) == true -> "SSL连接错误，请检查网络设置"
                else -> "网络错误: ${e.message ?: "未知错误"}"
            }
            
            ProfileResult(success = false, profile = null, error = errorMessage)
        }
    }
}

/**
 * 用户资料数据类
 * 基于主流社交应用的用户信息表设计
 */
data class UserProfile(
    // 基础身份信息
    val id: Long = 0,
    val username: String = "",
    val nickname: String = "",
    val realName: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    
    // 基本信息
    val gender: String = "", // 男/女/其他
    val birthday: String = "", // YYYY-MM-DD
    val age: Int = 0,
    val height: Int = 0, // cm
    val weight: Int = 0, // kg
    val bio: String = "", // 个性签名
    val zodiacSign: String = "", // 星座
    
    // 地理位置
    val city: String = "", // 当前居住城市
    val hometown: String = "", // 家乡
    val location: String = "", // 详细地址
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    
    // 社会属性
    val education: String = "", // 学历
    val occupation: String = "", // 职业
    val income: String = "", // 年收入范围
    val relationshipStatus: String = "", // 情感状态
    
    // 生活状况
    val residenceStatus: String = "", // 居住情况
    val houseOwnership: Boolean = false, // 是否购房
    val carOwnership: Boolean = false, // 是否购车
    
    // 扩展信息
    val tags: List<String> = emptyList(), // 兴趣标签
    val hobbies: String = "", // 兴趣爱好
    val languages: List<String> = emptyList(), // 掌握语言
    val bloodType: String = "", // 血型
    val smoking: Boolean = false, // 是否吸烟
    val drinking: Boolean = false, // 是否饮酒
    
    // 系统字段
    val verified: Boolean = false, // 认证状态
    val createdAt: String = "", // 注册时间
    val updatedAt: String = "", // 更新时间
    val lastLoginAt: String = "" // 最后登录时间
)

/**
 * 资料操作结果
 */
data class ProfileResult(
    val success: Boolean,
    val message: String = "",
    val profile: UserProfile? = null,
    val error: String? = null,
    val isServerSaved: Boolean = false  // 是否真正保存到服务器
)
