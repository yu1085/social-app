package com.example.myapplication.service

import android.util.Log
import com.example.myapplication.dto.ApiResponse
import com.example.myapplication.model.VipLevel
import com.example.myapplication.model.VipSubscription
import com.example.myapplication.network.NetworkConfig
import com.example.myapplication.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * VIP会员API服务
 */
class VipService private constructor() {

    private val apiService: ApiService = NetworkConfig.getApiService()
    private val TAG = "VipService"

    /**
     * 获取VIP等级列表
     */
    suspend fun getVipLevels(): List<VipLevel> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始获取VIP等级列表")
            
            val response = apiService.getVipLevels().execute()
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    Log.d(TAG, "获取VIP等级列表成功: ${apiResponse.data?.size} 个等级")
                    apiResponse.data ?: emptyList()
                } else {
                    Log.e(TAG, "API返回错误: ${apiResponse?.message}")
                    throw Exception(apiResponse?.message ?: "获取VIP等级失败")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "HTTP错误: ${response.code()}, 错误体: $errorBody")
                throw Exception("HTTP错误: ${response.code()} - ${errorBody ?: response.message()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            throw Exception("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            throw Exception("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            throw e
        }
    }

    /**
     * 获取当前VIP订阅状态
     */
    suspend fun getCurrentVipSubscription(token: String): VipSubscription? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始获取当前VIP订阅状态")
            
            val response = apiService.getCurrentVipSubscription("Bearer $token").execute()
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    Log.d(TAG, "获取VIP订阅状态成功")
                    apiResponse.data
                } else {
                    Log.e(TAG, "API返回错误: ${apiResponse?.message}")
                    null
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "HTTP错误: ${response.code()}, 错误体: $errorBody")
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            null
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            null
        }
    }

    /**
     * 订阅VIP
     */
    suspend fun subscribeVip(token: String, vipLevelId: Long): VipSubscription = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始订阅VIP: 等级ID=$vipLevelId")
            
            val response = apiService.subscribeVip("Bearer $token", vipLevelId).execute()
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    Log.d(TAG, "VIP订阅成功")
                    apiResponse.data!!
                } else {
                    Log.e(TAG, "API返回错误: ${apiResponse?.message}")
                    throw Exception(apiResponse?.message ?: "订阅VIP失败")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "HTTP错误: ${response.code()}, 错误体: $errorBody")
                throw Exception("HTTP错误: ${response.code()} - ${errorBody ?: response.message()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            throw Exception("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            throw Exception("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            throw e
        }
    }

    /**
     * 检查VIP状态
     */
    suspend fun checkVipStatus(token: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始检查VIP状态")
            
            val response = apiService.checkVipStatus("Bearer $token").execute()
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    Log.d(TAG, "VIP状态检查成功: ${apiResponse.data}")
                    apiResponse.data ?: false
                } else {
                    Log.e(TAG, "API返回错误: ${apiResponse?.message}")
                    false
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "HTTP错误: ${response.code()}, 错误体: $errorBody")
                false
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            false
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            false
        }
    }

    companion object {
        @Volatile
        private var instance: VipService? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: VipService().also { instance = it }
            }
    }
}
