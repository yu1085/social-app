package com.example.myapplication.service

import android.util.Log
import com.example.myapplication.dto.ApiResponse
import com.example.myapplication.model.WealthLevelData
import com.example.myapplication.model.WealthLevelRule
import com.example.myapplication.network.NetworkConfig
import com.example.myapplication.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * 财富等级API服务
 */
class WealthLevelService private constructor() {

    private val apiService: ApiService = NetworkConfig.getApiService()
    private val TAG = "WealthLevelService"

    /**
     * 获取我的财富等级
     */
    suspend fun getMyWealthLevel(token: String): WealthLevelResult = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMyWealthLevel("Bearer $token").execute()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    WealthLevelResult.Success(apiResponse.data!!)
                } else {
                    WealthLevelResult.Error(apiResponse?.message ?: "获取财富等级失败")
                }
            } else {
                WealthLevelResult.Error("HTTP错误: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            WealthLevelResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            WealthLevelResult.Error("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            WealthLevelResult.Error("未知错误: ${e.message}")
        }
    }

    /**
     * 获取财富等级进度
     */
    suspend fun getWealthLevelProgress(token: String): WealthLevelResult = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getWealthLevelProgress("Bearer $token").execute()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    WealthLevelResult.Success(apiResponse.data!!)
                } else {
                    WealthLevelResult.Error(apiResponse?.message ?: "获取财富等级进度失败")
                }
            } else {
                WealthLevelResult.Error("HTTP错误: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            WealthLevelResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            WealthLevelResult.Error("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            WealthLevelResult.Error("未知错误: ${e.message}")
        }
    }

    /**
     * 获取用户特权
     */
    suspend fun getUserPrivileges(token: String): PrivilegeResult = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserPrivileges("Bearer $token").execute()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    PrivilegeResult.Success(apiResponse.data!!)
                } else {
                    PrivilegeResult.Error(apiResponse?.message ?: "获取用户特权失败")
                }
            } else {
                PrivilegeResult.Error("HTTP错误: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            PrivilegeResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            PrivilegeResult.Error("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            PrivilegeResult.Error("未知错误: ${e.message}")
        }
    }

    /**
     * 获取财富排行榜
     */
    suspend fun getWealthRanking(limit: Int = 10): RankingResult = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getWealthRanking(limit).execute()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    RankingResult.Success(apiResponse.data!!)
                } else {
                    RankingResult.Error(apiResponse?.message ?: "获取财富排行榜失败")
                }
            } else {
                RankingResult.Error("HTTP错误: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            RankingResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            RankingResult.Error("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            RankingResult.Error("未知错误: ${e.message}")
        }
    }

    /**
     * 获取财富等级规则
     */
    suspend fun getWealthLevelRules(): RulesResult = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getWealthLevelRules().execute()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    RulesResult.Success(apiResponse.data!!)
                } else {
                    RulesResult.Error(apiResponse?.message ?: "获取财富等级规则失败")
                }
            } else {
                RulesResult.Error("HTTP错误: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            RulesResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            RulesResult.Error("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            RulesResult.Error("未知错误: ${e.message}")
        }
    }

    sealed class WealthLevelResult {
        data class Success(val data: WealthLevelData) : WealthLevelResult()
        data class Error(val message: String) : WealthLevelResult()
    }

    sealed class PrivilegeResult {
        data class Success(val privileges: List<String>) : PrivilegeResult()
        data class Error(val message: String) : PrivilegeResult()
    }

    sealed class RankingResult {
        data class Success(val rankings: List<WealthLevelData>) : RankingResult()
        data class Error(val message: String) : RankingResult()
    }

    sealed class RulesResult {
        data class Success(val rules: List<WealthLevelRule>) : RulesResult()
        data class Error(val message: String) : RulesResult()
    }

    companion object {
        @Volatile
        private var instance: WealthLevelService? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: WealthLevelService().also { instance = it }
            }
    }
}