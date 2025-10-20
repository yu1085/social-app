package com.example.myapplication.service

import android.util.Log
import com.example.myapplication.dto.ApiResponse
import com.example.myapplication.dto.LuckyNumberDTO
import com.example.myapplication.model.LuckyNumber
import com.example.myapplication.model.LuckyNumberPurchaseResult
import com.example.myapplication.network.NetworkConfig
import com.example.myapplication.network.ApiService
import java.math.BigDecimal
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
    suspend fun getLuckyNumbers(page: Int = 0, size: Int = 20): LuckyNumberResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "获取靓号列表 - page: $page, size: $size")
            val response = apiService.getLuckyNumbers(page, size).execute()
            Log.d(TAG, "API响应 - 状态码: ${response.code()}, 成功: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                Log.d(TAG, "API响应体: $apiResponse")
                if (apiResponse?.isSuccess() == true) {
                    val pageData = apiResponse.data
                    if (pageData != null) {
                        val luckyNumberDTOs = pageData.content ?: emptyList()
                        Log.d(TAG, "获取到靓号数量: ${luckyNumberDTOs.size}")
                        // 转换DTO到模型
                        val luckyNumbers = luckyNumberDTOs.map { dto -> convertDtoToModel(dto) }
                        // Sort by tier: LIMITED, TOP, SUPER, NORMAL
                        val sortedNumbers = luckyNumbers.sortedWith(compareBy { it.tier.sortOrder })
                        LuckyNumberResult.Success(sortedNumbers)
                    } else {
                        LuckyNumberResult.Error("响应数据为空")
                    }
                } else {
                    LuckyNumberResult.Error(apiResponse?.message ?: "获取靓号失败")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "HTTP错误: ${response.code()}, 错误体: $errorBody")
                LuckyNumberResult.Error("HTTP错误: ${response.code()} - $errorBody")
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            LuckyNumberResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            LuckyNumberResult.Error("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
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

    /**
     * 转换DTO到模型
     */
    private fun convertDtoToModel(dto: LuckyNumberDTO): LuckyNumber {
        return LuckyNumber(
            id = dto.id ?: 0,
            number = dto.number ?: "",
            price = dto.price ?: BigDecimal.ZERO,
            tier = LuckyNumber.tierFromString(dto.tier ?: "NORMAL"),
            status = LuckyNumber.statusFromString(dto.status ?: "AVAILABLE"),
            ownerId = dto.ownerId,
            purchaseTime = dto.purchaseTime?.toString(),
            validityDays = dto.validityDays,
            expireTime = dto.expireTime?.toString(),
            description = dto.description ?: "",
            isSpecial = dto.isSpecial ?: false,
            createdAt = dto.createdAt?.toString(),
            updatedAt = dto.updatedAt?.toString()
        )
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