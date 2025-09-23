package com.example.myapplication.service

import android.util.Log
import com.example.myapplication.dto.ApiResponse
import com.example.myapplication.network.NetworkConfig
import com.example.myapplication.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * 支付订单服务
 */
class PaymentOrderService private constructor() {
    
    private val apiService: ApiService = NetworkConfig.getApiService()
    private val TAG = "PaymentOrderService"
    
    /**
     * 创建VIP支付订单
     * @param token 用户认证token
     * @param vipLevelId VIP等级ID
     * @param paymentMethod 支付方式 (ALIPAY, WECHAT)
     * @return 支付订单信息
     */
    suspend fun createVipPaymentOrder(
        token: String,
        vipLevelId: Long,
        paymentMethod: String
    ): PaymentOrderResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "创建VIP支付订单: vipLevelId=$vipLevelId, paymentMethod=$paymentMethod")
            
            val requestBody = mapOf(
                "vipLevelId" to vipLevelId,
                "paymentMethod" to paymentMethod,
                "amount" to getVipPrice(vipLevelId)
            )
            
            Log.d(TAG, "请求体: $requestBody")
            Log.d(TAG, "Token: Bearer $token")
            
            val response = apiService.createPaymentOrder("Bearer $token", requestBody).execute()
            
            Log.d(TAG, "API响应码: ${response.code()}")
            Log.d(TAG, "API响应体: ${response.body()}")
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    val orderData = apiResponse.data
                    Log.d(TAG, "支付订单创建成功: ${orderData?.orderId}")
                    PaymentOrderResult.Success(orderData!!)
                } else {
                    Log.e(TAG, "API返回错误: ${apiResponse?.message}")
                    PaymentOrderResult.Error(apiResponse?.message ?: "创建订单失败")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "HTTP错误: ${response.code()}, 错误体: $errorBody")
                PaymentOrderResult.Error("HTTP错误: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            PaymentOrderResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            PaymentOrderResult.Error("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            PaymentOrderResult.Error("未知错误: ${e.message}")
        }
    }
    
    /**
     * 验证支付结果
     * @param token 用户认证token
     * @param orderId 订单ID
     * @param paymentResult 支付结果
     * @return 验证结果
     */
    suspend fun verifyPaymentResult(
        token: String,
        orderId: String,
        paymentResult: String
    ): PaymentVerifyResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "验证支付结果: orderId=$orderId")
            
            val requestBody = mapOf(
                "orderId" to orderId,
                "paymentResult" to paymentResult
            )
            
            val response = apiService.verifyPayment("Bearer $token", requestBody).execute()
            
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    Log.d(TAG, "支付验证成功")
                    PaymentVerifyResult.Success("支付验证成功")
                } else {
                    Log.e(TAG, "支付验证失败: ${apiResponse?.message}")
                    PaymentVerifyResult.Error(apiResponse?.message ?: "支付验证失败")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "HTTP错误: ${response.code()}, 错误体: $errorBody")
                PaymentVerifyResult.Error("HTTP错误: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.e(TAG, "网络错误: ${e.message}", e)
            PaymentVerifyResult.Error("网络错误: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "API错误: ${e.message}", e)
            PaymentVerifyResult.Error("API错误: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "未知错误: ${e.message}", e)
            PaymentVerifyResult.Error("未知错误: ${e.message}")
        }
    }
    
    /**
     * 根据VIP等级获取价格
     */
    private fun getVipPrice(vipLevelId: Long): Double {
        return when (vipLevelId) {
            1L -> 29.90 // VIP会员
            2L -> 59.90 // SVIP会员
            3L -> 99.90 // 钻石会员
            4L -> 199.90 // 至尊会员
            else -> 29.90
        }
    }
    
    companion object {
        @Volatile
        private var instance: PaymentOrderService? = null
        
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: PaymentOrderService().also { instance = it }
        }
    }
}

/**
 * 支付订单结果
 */
sealed class PaymentOrderResult {
    data class Success(val orderData: PaymentOrderData) : PaymentOrderResult()
    data class Error(val message: String) : PaymentOrderResult()
}

/**
 * 支付验证结果
 */
sealed class PaymentVerifyResult {
    data class Success(val message: String) : PaymentVerifyResult()
    data class Error(val message: String) : PaymentVerifyResult()
}

/**
 * 支付订单数据
 */
data class PaymentOrderData(
    val orderId: String,
    val amount: Double,
    val paymentMethod: String,
    val orderInfo: String, // 支付宝支付字符串
    val vipLevelId: Long,
    val vipLevelName: String
)
