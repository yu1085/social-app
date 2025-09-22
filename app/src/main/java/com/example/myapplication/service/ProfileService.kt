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
    suspend fun getUserProfile(token: String): ProfileResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "获取用户资料")
            
            val request = Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

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
                ProfileResult(success = false, message = "获取用户资料失败")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "获取用户资料异常", e)
            ProfileResult(success = false, message = "网络错误: ${e.message}")
        }
    }

    /**
     * 更新用户资料
     */
    suspend fun updateUserProfile(
        userId: Long,
        token: String,
        profileData: Map<String, Any>
    ): ProfileResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "更新用户资料: $profileData")
            
            val jsonObject = JSONObject()
            profileData.forEach { (key, value) ->
                jsonObject.put(key, value)
            }
            
            val requestBody = jsonObject.toString()
                .toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url("$BASE_URL/$userId")
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .put(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

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
                ProfileResult(success = true, profile = profile)
            } else {
                Log.e(TAG, "更新用户资料失败: ${response.code} - $responseBody")
                ProfileResult(success = false, message = "更新用户资料失败")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "更新用户资料异常", e)
            ProfileResult(success = false, message = "网络错误: ${e.message}")
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
    val profile: UserProfile? = null
)
