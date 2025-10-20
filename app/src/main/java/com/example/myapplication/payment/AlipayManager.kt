package com.example.myapplication.payment

import android.app.Activity
import android.content.Context
import android.util.Log
import com.alipay.sdk.app.PayTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * 支付宝支付管理器
 */
object AlipayManager {
    
    private const val TAG = "AlipayManager"
    
    // 支付宝应用ID - 使用您的实际应用ID
    private const val APP_ID = "2021005195696348"
    
    // 支付结果监听器
    interface PayResultListener {
        fun onPaySuccess(result: PayResult)
        fun onPayFailed(result: PayResult)
        fun onPayCancel(result: PayResult)
    }
    
    /**
     * 支付结果数据类
     */
    data class PayResult(
        val resultStatus: String,
        val result: String,
        val memo: String
    ) {
        val isSuccess: Boolean
            get() = resultStatus == "9000"
        
        val isCancel: Boolean
            get() = resultStatus == "6001"
        
        val isFailed: Boolean
            get() = !isSuccess && !isCancel
    }
    
    /**
     * 发起支付
     */
    suspend fun pay(
        activity: Activity,
        payInfo: String,
        listener: PayResultListener
    ) {
        try {
            Log.d(TAG, "开始支付宝支付")
            Log.d(TAG, "支付信息: $payInfo")
            
            val result = withContext(Dispatchers.IO) {
                val payTask = PayTask(activity)
                val result = payTask.payV2(payInfo, true)
                
                // 解析支付结果
                val resultStatus = result["resultStatus"] ?: ""
                val resultStr = result["result"] ?: ""
                val memo = result["memo"] ?: ""
                
                PayResult(resultStatus, resultStr, memo)
            }
            
            // 在主线程回调结果
            withContext(Dispatchers.Main) {
                when {
                    result.isSuccess -> {
                        Log.d(TAG, "支付成功")
                        listener.onPaySuccess(result)
                    }
                    result.isCancel -> {
                        Log.d(TAG, "支付取消")
                        listener.onPayCancel(result)
                    }
                    else -> {
                        Log.d(TAG, "支付失败: ${result.resultStatus}")
                        listener.onPayFailed(result)
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "支付异常", e)
            withContext(Dispatchers.Main) {
                val errorResult = PayResult("4000", "", "支付异常: ${e.message}")
                listener.onPayFailed(errorResult)
            }
        }
    }
    
    /**
     * 检查支付宝是否已安装
     */
    fun isAlipayInstalled(context: Context): Boolean {
        return try {
            val packageManager = context.packageManager
            packageManager.getPackageInfo("com.eg.android.AlipayGphone", 0)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取支付宝版本
     */
    fun getAlipayVersion(context: Context): String {
        return try {
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo("com.eg.android.AlipayGphone", 0)
            packageInfo.versionName ?: "未知版本"
        } catch (e: Exception) {
            "未安装"
        }
    }
}
