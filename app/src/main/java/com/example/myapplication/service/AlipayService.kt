package com.example.myapplication.service

import android.app.Activity
import android.util.Log
import com.alipay.sdk.app.PayTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * 支付宝支付服务
 */
class AlipayService private constructor() {
    
    private val TAG = "AlipayService"
    
    /**
     * 发起支付宝支付
     * @param activity 当前Activity
     * @param orderInfo 订单信息（从服务器获取的支付字符串）
     * @param callback 支付结果回调
     */
    suspend fun pay(
        activity: Activity,
        orderInfo: String,
        callback: (PayResult) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "开始发起支付宝支付: $orderInfo")
            
            // 检查支付字符串是否为空
            if (orderInfo.isBlank()) {
                Log.e(TAG, "支付字符串为空")
                withContext(Dispatchers.Main) {
                    callback(PayResult.Error("支付字符串为空"))
                }
                return@withContext
            }
            
            // 在后台线程执行支付
            val payTask = PayTask(activity)
            val result = payTask.payV2(orderInfo, true)
            
            Log.d(TAG, "支付宝支付结果: $result")
            
            // 解析支付结果
            val payResult = parsePayResult(result)
            
            // 切换到主线程回调结果
            withContext(Dispatchers.Main) {
                callback(payResult)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "支付宝支付异常: ${e.message}", e)
            withContext(Dispatchers.Main) {
                callback(PayResult.Error("支付异常: ${e.message}"))
            }
        }
    }
    
    /**
     * 解析支付宝支付结果
     */
    private fun parsePayResult(result: Map<String, String>): PayResult {
        val resultStatus = result["resultStatus"]
        val resultInfo = result["result"]
        
        return when (resultStatus) {
            "9000" -> {
                // 支付成功
                Log.d(TAG, "支付成功")
                PayResult.Success("支付成功")
            }
            "8000" -> {
                // 正在处理中
                Log.d(TAG, "支付处理中")
                PayResult.Processing("支付处理中，请稍后查询")
            }
            "4000" -> {
                // 订单支付失败
                Log.d(TAG, "支付失败")
                PayResult.Error("支付失败")
            }
            "5000" -> {
                // 重复请求
                Log.d(TAG, "重复请求")
                PayResult.Error("重复请求")
            }
            "6001" -> {
                // 用户中途取消
                Log.d(TAG, "用户取消支付")
                PayResult.Cancelled("用户取消支付")
            }
            "6002" -> {
                // 网络连接出错
                Log.d(TAG, "网络连接出错")
                PayResult.Error("网络连接出错")
            }
            else -> {
                // 其他错误
                Log.d(TAG, "支付失败: $resultStatus")
                PayResult.Error("支付失败: $resultStatus")
            }
        }
    }
    
    companion object {
        @Volatile
        private var instance: AlipayService? = null
        
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: AlipayService().also { instance = it }
        }
    }
}

/**
 * 支付结果
 */
sealed class PayResult {
    data class Success(val message: String) : PayResult()
    data class Error(val message: String) : PayResult()
    data class Cancelled(val message: String) : PayResult()
    data class Processing(val message: String) : PayResult()
}
