package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.auth.AuthManager
import com.example.myapplication.ui.screens.VipCenterScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.VipViewModel
import com.example.myapplication.service.AlipayService
import com.example.myapplication.service.PaymentOrderService
import com.example.myapplication.service.PayResult
import com.example.myapplication.service.PaymentOrderResult
import com.example.myapplication.service.PaymentVerifyResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * VIP会员中心Activity
 */
class VipCenterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VipCenterActivityContent(activity = this)
                }
            }
        }
    }
}

/**
 * VIP会员中心Activity内容
 */
@Composable
private fun VipCenterActivityContent(activity: ComponentActivity) {
    val vipViewModel: VipViewModel = viewModel()
    val uiState by vipViewModel.uiState.collectAsState()
    val authManager = AuthManager.getInstance(activity)
    val token = authManager.getToken()
    
    // 支付服务
    val alipayService = AlipayService.getInstance()
    val paymentOrderService = PaymentOrderService.getInstance()
    
    // 支付状态
    var isProcessingPayment by remember { mutableStateOf(false) }
    var currentOrderId by remember { mutableStateOf<String?>(null) }
    
    // 加载VIP数据
    LaunchedEffect(Unit) {
        vipViewModel.loadVipLevels()
        token?.let { vipViewModel.loadCurrentVipSubscription(it) }
    }
    
    // 监听错误和成功消息
    LaunchedEffect(uiState.error, uiState.subscriptionResult) {
        uiState.error?.let { error ->
            Toast.makeText(activity, error, Toast.LENGTH_LONG).show()
            vipViewModel.clearError()
        }
        uiState.subscriptionResult?.let { result ->
            Toast.makeText(activity, result, Toast.LENGTH_LONG).show()
            vipViewModel.clearSubscriptionResult()
        }
    }
    
    VipCenterScreen(
        onBackClick = {
            // 返回上一页
            activity.finish()
        },
        onUpgradeSvipClick = {
            // 处理升级SVIP点击
            handleUpgradeSvipClick(activity)
        },
        onActivateVipClick = { vipLevelId ->
            // 处理开通VIP点击
            handleActivateVipClick(activity, vipViewModel, token, vipLevelId)
        },
        onPaymentConfirm = { paymentMethod, membershipType, price, vipLevelId ->
            // 处理支付确认
            handlePaymentConfirm(activity, paymentMethod, membershipType, price, vipLevelId, alipayService, paymentOrderService, token, isProcessingPayment, currentOrderId)
        },
        onAgreementClick = {
            // 处理协议点击
            handleAgreementClick(activity)
        },
        vipLevels = uiState.vipLevels,
        currentSubscription = uiState.currentSubscription,
        isVip = uiState.isVip,
        isLoading = uiState.isLoading,
        isSubscribing = uiState.isSubscribing
    )
}

/**
 * 处理升级SVIP点击事件
 */
private fun handleUpgradeSvipClick(activity: ComponentActivity) {
    Toast.makeText(activity, "点击了升级SVIP", Toast.LENGTH_SHORT).show()
    // TODO: 实现升级SVIP逻辑
}

/**
 * 处理开通VIP点击事件
 */
private fun handleActivateVipClick(
    activity: ComponentActivity, 
    vipViewModel: VipViewModel, 
    token: String?, 
    vipLevelId: Long
) {
    if (token == null) {
        Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show()
        return
    }
    
    // 显示支付方式选择对话框
    showPaymentMethodDialog(activity, vipLevelId, token)
}

/**
 * 显示支付方式选择对话框
 */
private fun showPaymentMethodDialog(activity: ComponentActivity, vipLevelId: Long, token: String) {
    val paymentMethods = arrayOf("支付宝", "微信")
    
    androidx.appcompat.app.AlertDialog.Builder(activity)
        .setTitle("选择支付方式")
        .setItems(paymentMethods) { _, which ->
            val selectedMethod = paymentMethods[which]
            // 获取支付服务
            val alipayService = AlipayService.getInstance()
            val paymentOrderService = PaymentOrderService.getInstance()
            
            // 处理支付
            createOrderAndPay(activity, vipLevelId, selectedMethod, alipayService, paymentOrderService, token)
        }
        .setNegativeButton("取消", null)
        .show()
}

/**
 * 处理支付确认事件
 */
private fun handlePaymentConfirm(
    activity: ComponentActivity, 
    paymentMethod: String, 
    membershipType: String, 
    price: String,
    vipLevelId: Long,
    alipayService: AlipayService,
    paymentOrderService: PaymentOrderService,
    token: String?,
    isProcessingPayment: Boolean,
    currentOrderId: String?
) {
    if (token == null) {
        Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show()
        return
    }
    
    if (isProcessingPayment) {
        Toast.makeText(activity, "支付处理中，请稍候...", Toast.LENGTH_SHORT).show()
        return
    }
    
    // 根据支付方式处理
    when (paymentMethod) {
        "支付宝" -> {
            // 创建支付订单并调用支付宝
            createOrderAndPay(activity, vipLevelId, "ALIPAY", alipayService, paymentOrderService, token)
        }
        "微信" -> {
            // 创建支付订单并调用微信支付
            createOrderAndPay(activity, vipLevelId, "WECHAT", alipayService, paymentOrderService, token)
        }
        else -> {
            Toast.makeText(activity, "不支持的支付方式", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * 创建订单并支付
 */
private fun createOrderAndPay(
    activity: ComponentActivity,
    vipLevelId: Long,
    paymentMethod: String,
    alipayService: AlipayService,
    paymentOrderService: PaymentOrderService,
    token: String
) {
    // 在协程中执行
    CoroutineScope(Dispatchers.Main).launch {
        try {
            // 1. 创建支付订单
            val orderResult = paymentOrderService.createVipPaymentOrder(token, vipLevelId, paymentMethod)
            
            when (orderResult) {
                is PaymentOrderResult.Success -> {
                    val orderData = orderResult.orderData
                    
                    // 2. 调用支付宝支付
                    alipayService.pay(activity, orderData.orderInfo) { payResult ->
                        when (payResult) {
                            is PayResult.Success -> {
                                // 3. 验证支付结果
                                verifyPaymentResult(activity, token, orderData.orderId, paymentOrderService)
                            }
                            is PayResult.Error -> {
                                Toast.makeText(activity, "支付失败: ${payResult.message}", Toast.LENGTH_LONG).show()
                            }
                            is PayResult.Cancelled -> {
                                Toast.makeText(activity, "支付已取消", Toast.LENGTH_SHORT).show()
                            }
                            is PayResult.Processing -> {
                                Toast.makeText(activity, "支付处理中: ${payResult.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                is PaymentOrderResult.Error -> {
                    Toast.makeText(activity, "创建订单失败: ${orderResult.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(activity, "支付异常: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

/**
 * 验证支付结果
 */
private fun verifyPaymentResult(
    activity: ComponentActivity,
    token: String,
    orderId: String,
    paymentOrderService: PaymentOrderService
) {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            val verifyResult = paymentOrderService.verifyPaymentResult(token, orderId, "SUCCESS")
            
            when (verifyResult) {
                is PaymentVerifyResult.Success -> {
                    Toast.makeText(activity, "支付成功！VIP会员已开通", Toast.LENGTH_LONG).show()
                    // 刷新VIP状态
                    // 这里可以添加刷新逻辑
                }
                is PaymentVerifyResult.Error -> {
                    Toast.makeText(activity, "支付验证失败: ${verifyResult.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(activity, "验证支付结果异常: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

/**
 * 处理协议点击事件
 */
private fun handleAgreementClick(activity: ComponentActivity) {
    // TODO: 实现协议页面跳转
    Toast.makeText(activity, "跳转到会员协议页面", Toast.LENGTH_SHORT).show()
}
