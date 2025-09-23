package com.example.myapplication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.auth.AuthManager
import com.example.myapplication.service.WealthLevelService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal

/**
 * 充值页面ViewModel
 */
class RechargeViewModel(private val context: Context) : ViewModel() {
    
    private val authManager = AuthManager.getInstance(context)
    private val _uiState = MutableStateFlow(RechargeUiState())
    val uiState: StateFlow<RechargeUiState> = _uiState.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // 加载钱包余额
                loadWalletBalance()
                
                // 加载充值套餐
                loadRechargePackages()
                
                // 加载充值等级信息
                loadRechargeLevel()
                
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private suspend fun loadWalletBalance() {
        try {
            val token = authManager.getToken()
            if (token == null) {
                _uiState.value = _uiState.value.copy(
                    error = "用户未登录"
                )
                return
            }
            
            val userId = authManager.getUserId()
            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    error = "用户ID无效"
                )
                return
            }
            
            val response = com.example.myapplication.network.NetworkService.getWalletBalance(token)
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.isSuccess() == true) {
                    val walletData = apiResponse.data
                    val balance = walletData?.balance?.toLong() ?: 0L
                    
                    _uiState.value = _uiState.value.copy(
                        currentBalance = balance,
                        rechargeBalance = balance, // 暂时使用总余额作为充值余额
                        giftBalance = 0,
                        exchangeBalance = 0,
                        earnBalance = 0
                    )
                } else {
                    throw Exception(apiResponse?.message ?: "API调用失败")
                }
            } else {
                throw Exception("HTTP错误: ${response.code()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("RechargeViewModel", "加载钱包余额失败", e)
            // 如果API调用失败，使用默认值而不是硬编码的模拟数据
            _uiState.value = _uiState.value.copy(
                currentBalance = 0,
                rechargeBalance = 0,
                giftBalance = 0,
                exchangeBalance = 0,
                earnBalance = 0,
                error = "加载钱包余额失败: ${e.message}"
            )
        }
    }
    
    private suspend fun loadRechargePackages() {
        val packages = listOf(
            // 主要套餐（大卡片）
            RechargePackage(
                id = "package_1200",
                coins = 1200,
                price = BigDecimal("12"),
                isRecommended = true
            ),
            RechargePackage(
                id = "package_5800",
                coins = 5800,
                price = BigDecimal("58")
            ),
            RechargePackage(
                id = "package_9800",
                coins = 9800,
                price = BigDecimal("98")
            ),
            // 小套餐（小卡片）
            RechargePackage(
                id = "package_800",
                coins = 800,
                price = BigDecimal("8")
            ),
            RechargePackage(
                id = "package_2800",
                coins = 2800,
                price = BigDecimal("28")
            ),
            RechargePackage(
                id = "package_3800",
                coins = 3800,
                price = BigDecimal("38")
            ),
            RechargePackage(
                id = "package_19800",
                coins = 19800,
                price = BigDecimal("198")
            ),
            RechargePackage(
                id = "package_23800",
                coins = 23800,
                price = BigDecimal("238")
            ),
            RechargePackage(
                id = "package_51800",
                coins = 51800,
                price = BigDecimal("518")
            )
        )
        
        _uiState.value = _uiState.value.copy(rechargePackages = packages)
    }
    
    private suspend fun loadRechargeLevel() {
        try {
            val token = authManager.getToken()
            if (token == null) {
                _uiState.value = _uiState.value.copy(
                    rechargeLevel = "普通",
                    levelProgress = 0,
                    maxLevelProgress = 1000
                )
                return
            }
            
            // 调用财富等级API
            val wealthLevelService = WealthLevelService.getInstance()
            val result = wealthLevelService.getMyWealthLevel(token)
            
            when (result) {
                is WealthLevelService.WealthLevelResult.Success -> {
                    val wealthLevel = result.data
                    _uiState.value = _uiState.value.copy(
                        rechargeLevel = wealthLevel.levelName,
                        levelProgress = wealthLevel.wealthValue,
                        maxLevelProgress = wealthLevel.nextLevelRequirement ?: wealthLevel.wealthValue
                    )
                }
                is WealthLevelService.WealthLevelResult.Error -> {
                    android.util.Log.e("RechargeViewModel", "获取财富等级失败: ${result.message}")
                    _uiState.value = _uiState.value.copy(
                        rechargeLevel = "普通",
                        levelProgress = 0,
                        maxLevelProgress = 1000
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("RechargeViewModel", "加载充值等级失败", e)
            _uiState.value = _uiState.value.copy(
                rechargeLevel = "普通",
                levelProgress = 0,
                maxLevelProgress = 1000
            )
        }
    }
    
    /**
     * 处理支付
     */
    fun processPayment(rechargePackage: RechargePackage, paymentMethod: PaymentMethod) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                when (paymentMethod) {
                    PaymentMethod.ALIPAY -> processAlipayPayment(rechargePackage)
                    PaymentMethod.WECHAT -> processWechatPayment(rechargePackage)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "支付失败：${e.message}"
                )
            }
        }
    }
    
    private suspend fun processAlipayPayment(rechargePackage: RechargePackage) {
        try {
            // 1. 调用后端API创建订单
            val orderResponse = createBackendOrder(rechargePackage, PaymentMethod.ALIPAY)
            
            // 2. 获取支付宝订单信息
            val alipayOrderInfo = orderResponse["alipayOrderInfo"] as? String
            if (alipayOrderInfo == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "获取支付宝订单信息失败"
                )
                return
            }
            
            // 3. 调用支付宝SDK
            // TODO: 集成真实支付宝SDK
            // val payResult = AlipayManager.pay(alipayOrderInfo)
            
            // 模拟支付成功
            android.util.Log.d("RechargeViewModel", "支付宝订单信息: $alipayOrderInfo")
            handlePaymentResult(createOrder(rechargePackage, PaymentMethod.ALIPAY), true)
            
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "支付宝支付失败: ${e.message}"
            )
        }
    }

    private suspend fun processWechatPayment(rechargePackage: RechargePackage) {
        try {
            // 1. 调用后端API创建订单
            val orderResponse = createBackendOrder(rechargePackage, PaymentMethod.WECHAT)
            
            // 2. 获取微信支付信息
            val wechatPayInfo = orderResponse["wechatPayInfo"] as? Map<String, String>
            if (wechatPayInfo == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "获取微信支付信息失败"
                )
                return
            }
            
            // 3. 调用微信支付SDK
            // TODO: 集成真实微信支付SDK
            // val payResult = WechatPayManager.pay(wechatPayInfo)
            
            // 模拟支付成功
            android.util.Log.d("RechargeViewModel", "微信支付信息: $wechatPayInfo")
            handlePaymentResult(createOrder(rechargePackage, PaymentMethod.WECHAT), true)
            
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "微信支付失败: ${e.message}"
            )
        }
    }
    
    
    private fun createOrder(rechargePackage: RechargePackage, paymentMethod: PaymentMethod): RechargeOrder {
        return RechargeOrder(
            orderId = generateOrderId(),
            userId = getCurrentUserId(),
            packageId = rechargePackage.id,
            coins = rechargePackage.coins,
            amount = rechargePackage.price,
            paymentMethod = paymentMethod,
            status = OrderStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )
    }
    
    private fun handlePaymentResult(order: RechargeOrder, success: Boolean) {
        if (success) {
            // 支付成功，更新余额
            _uiState.value = _uiState.value.copy(
                currentBalance = _uiState.value.currentBalance + order.coins,
                isLoading = false,
                paymentSuccess = true
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "支付失败"
            )
        }
    }
    
    private fun generateOrderId(): String {
        return "ORDER_${System.currentTimeMillis()}"
    }
    
    private fun getCurrentUserId(): Long {
        return authManager.getUserId() ?: 0L
    }
    
    /**
     * 调用后端API创建订单
     */
    private suspend fun createBackendOrder(rechargePackage: RechargePackage, paymentMethod: PaymentMethod): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            try {
                val baseUrl = "http://10.0.2.2:8080" // Android模拟器访问本机地址
                val url = "$baseUrl/api/recharge/create-order"
                
                val requestBody = mapOf(
                    "packageId" to rechargePackage.id,
                    "coins" to rechargePackage.coins,
                    "amount" to rechargePackage.price.toDouble(),
                    "paymentMethod" to paymentMethod.code.uppercase(),
                    "description" to "充值${rechargePackage.coins}金币"
                )
                
                val json = com.google.gson.Gson().toJson(requestBody)
                val requestBodyBytes = json.toByteArray()
                
                val connection = java.net.URL(url).openConnection() as java.net.HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer ${authManager.getToken()}")
                connection.doOutput = true
                
                connection.outputStream.use { it.write(requestBodyBytes) }
                
                val responseCode = connection.responseCode
                val responseBody = if (responseCode == 200) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream.bufferedReader().use { it.readText() }
                }
                
                if (responseCode == 200) {
                    val response = com.google.gson.Gson().fromJson(responseBody, Map::class.java) as Map<String, Any>
                    val data = response["data"] as? Map<String, Any>
                    if (data != null) {
                        data
                    } else {
                        throw Exception("后端返回数据格式错误")
                    }
                } else {
                    throw Exception("创建订单失败: HTTP $responseCode")
                }
                
            } catch (e: Exception) {
                android.util.Log.e("RechargeViewModel", "创建后端订单失败", e)
                throw e
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearPaymentSuccess() {
        _uiState.value = _uiState.value.copy(paymentSuccess = false)
    }
}

/**
 * 充值页面UI状态
 */
data class RechargeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val paymentSuccess: Boolean = false,
    
    // 钱包余额
    val currentBalance: Long = 0,
    val rechargeBalance: Long = 0,
    val giftBalance: Long = 0,
    val exchangeBalance: Long = 0,
    val earnBalance: Long = 0,
    
    // 充值等级
    val rechargeLevel: String = "普通",
    val levelProgress: Int = 0,
    val maxLevelProgress: Int = 1000,
    
    // 充值套餐
    val rechargePackages: List<RechargePackage> = emptyList()
)
