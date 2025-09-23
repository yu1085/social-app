package com.example.myapplication.service

import android.util.Log
import com.example.myapplication.model.LuckyNumber
import com.example.myapplication.network.NetworkConfig
import com.example.myapplication.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * 道具购买服务
 */
class PropPurchaseService private constructor() {

    private val apiService: ApiService = NetworkConfig.getApiService()
    private val TAG = "PropPurchaseService"

    /**
     * 购买靓号
     */
    suspend fun purchaseLuckyNumber(
        token: String,
        luckyNumberId: Long,
        finalPrice: Long
    ): PurchaseResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始购买靓号: ID=$luckyNumberId, 价格=$finalPrice")
            
            val requestBody = mutableMapOf<String, Any>(
                "itemId" to luckyNumberId,
                "price" to finalPrice,
                "paymentMethod" to "COINS" // 使用积分支付
            )
            
            val response = apiService.purchaseLuckyNumber("Bearer $token", requestBody).execute()
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    Log.d(TAG, "购买成功")
                    PurchaseResult.Success(apiResponse.data!!)
                } else {
                    Log.e(TAG, "购买失败: ${apiResponse?.message}")
                    PurchaseResult.Error(apiResponse?.message ?: "购买失败")
                }
            } else {
                when (response.code()) {
                    400 -> {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "请求错误: $errorBody")
                        PurchaseResult.Error("请求参数错误")
                    }
                    401 -> {
                        Log.e(TAG, "认证失败")
                        PurchaseResult.Error("认证失败，请重新登录")
                    }
                    403 -> {
                        Log.e(TAG, "权限不足")
                        PurchaseResult.Error("权限不足")
                    }
                    404 -> {
                        Log.e(TAG, "商品不存在")
                        PurchaseResult.Error("商品不存在")
                    }
                    409 -> {
                        Log.e(TAG, "商品已售出")
                        PurchaseResult.Error("商品已售出")
                    }
                    422 -> {
                        Log.e(TAG, "余额不足")
                        // 尝试解析错误信息获取具体余额信息
                        val errorBody = response.errorBody()?.string()
                        val currentBalance = extractBalanceFromError(errorBody)
                        val requiredAmount = finalPrice
                        PurchaseResult.InsufficientBalance(
                            current = currentBalance,
                            required = requiredAmount
                        )
                    }
                    else -> {
                        Log.e(TAG, "服务器错误: ${response.code()}")
                        PurchaseResult.Error("服务器错误: ${response.code()}")
                    }
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            PurchaseResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP错误: ${e.message}", e)
            PurchaseResult.Error("网络请求失败: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            PurchaseResult.Error("购买失败: ${e.message}")
        }
    }

    /**
     * 从错误信息中提取余额信息
     */
    private fun extractBalanceFromError(errorBody: String?): Long {
        return try {
            // 简单的余额提取逻辑，实际项目中可能需要更复杂的JSON解析
            if (errorBody?.contains("current") == true) {
                val regex = "\"current\"\\s*:\\s*(\\d+)".toRegex()
                val matchResult = regex.find(errorBody)
                matchResult?.groupValues?.get(1)?.toLong() ?: 0L
            } else {
                0L
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析余额信息失败: ${e.message}")
            0L
        }
    }

    sealed class PurchaseResult {
        data class Success(val luckyNumber: LuckyNumber) : PurchaseResult()
        data class Error(val message: String) : PurchaseResult()
        data class InsufficientBalance(val current: Long, val required: Long) : PurchaseResult()
    }

    companion object {
        @Volatile
        private var instance: PropPurchaseService? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: PropPurchaseService().also { instance = it }
            }
    }
}
