package com.example.myapplication.service

import android.util.Log
import com.example.myapplication.dto.ApiResponse
import com.example.myapplication.model.LuckyNumber
import com.example.myapplication.model.LuckyNumberPurchaseResult
import com.example.myapplication.network.NetworkConfig
import com.example.myapplication.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * 靓号API服务
 */
class LuckyNumberService private constructor() {

    private val apiService: ApiService = NetworkConfig.getApiService()
    private val TAG = "LuckyNumberService"

    /**
     * 获取靓号列表
     */
    suspend fun getLuckyNumbers(): LuckyNumberResult = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLuckyNumbers().execute()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    val luckyNumbers = apiResponse.data ?: emptyList()
                    // Sort by tier: LIMITED, TOP, SUPER, NORMAL
                    val sortedNumbers = luckyNumbers.sortedWith(compareBy { it.tier.sortOrder })
                    LuckyNumberResult.Success(sortedNumbers)
                } else {
                    LuckyNumberResult.Error(apiResponse?.message ?: "获取靓号失败")
                }
            } else {
                LuckyNumberResult.Error("HTTP错误: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e("LuckyNumberService", "网络错误: ${e.message}", e)
            LuckyNumberResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e("LuckyNumberService", "API错误: ${e.message}", e)
            LuckyNumberResult.Error("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e("LuckyNumberService", "未知错误: ${e.message}", e)
            LuckyNumberResult.Error("未知错误: ${e.message}")
        }
    }

    /**
     * 购买靓号
     */
    suspend fun purchaseLuckyNumber(token: String, luckyNumberId: Long): LuckyNumberPurchaseResult = withContext(Dispatchers.IO) {
        try {
            val requestBody = mapOf("itemId" to luckyNumberId)
            val response = apiService.purchaseLuckyNumber("Bearer $token", requestBody).execute()
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    LuckyNumberPurchaseResult.Success(apiResponse.data!!) // Assuming data is the purchased LuckyNumber
                } else {
                    LuckyNumberPurchaseResult.Error(apiResponse?.message ?: "购买失败")
                }
            } else {
                LuckyNumberPurchaseResult.Error("HTTP错误: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e("LuckyNumberService", "网络错误: ${e.message}", e)
            LuckyNumberPurchaseResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e("LuckyNumberService", "API错误: ${e.message}", e)
            LuckyNumberPurchaseResult.Error("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e("LuckyNumberService", "未知错误: ${e.message}", e)
            LuckyNumberPurchaseResult.Error("未知错误: ${e.message}")
        }
    }

    sealed class LuckyNumberResult {
        data class Success(val data: List<LuckyNumber>) : LuckyNumberResult()
        data class Error(val message: String) : LuckyNumberResult()
    }

    companion object {
        @Volatile
        private var instance: LuckyNumberService? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: LuckyNumberService().also { instance = it }
            }
    }
}