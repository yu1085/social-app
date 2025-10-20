package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.LuckyNumber
import com.example.myapplication.service.PropPurchaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 道具购买ViewModel
 */
class PropPurchaseViewModel : ViewModel() {
    
    private val propPurchaseService = PropPurchaseService.getInstance()
    
    // UI状态
    private val _uiState = MutableStateFlow(PropPurchaseUiState())
    val uiState: StateFlow<PropPurchaseUiState> = _uiState.asStateFlow()
    
    /**
     * 初始化数据
     */
    fun initializeData(luckyNumber: LuckyNumber, token: String?) {
        _uiState.value = _uiState.value.copy(
            luckyNumber = luckyNumber,
            token = token,
            originalPrice = luckyNumber.price.toLong(),
            discountAmount = calculateDiscount(luckyNumber.price.toLong()),
            finalPrice = calculateFinalPrice(luckyNumber.price.toLong())
        )
    }
    
    /**
     * 计算折扣金额
     */
    private fun calculateDiscount(originalPrice: Long): Long {
        // 根据财富等级计算折扣，这里模拟20%的折扣
        return (originalPrice * 0.2).toLong()
    }
    
    /**
     * 计算最终价格
     */
    private fun calculateFinalPrice(originalPrice: Long): Long {
        val discount = calculateDiscount(originalPrice)
        return originalPrice - discount
    }
    
    /**
     * 购买靓号
     */
    fun purchaseLuckyNumber() {
        val currentState = _uiState.value
        if (currentState.token == null) {
            _uiState.value = currentState.copy(
                purchaseResult = PurchaseResult.Error("请先登录")
            )
            return
        }
        
        _uiState.value = currentState.copy(showPaymentDialog = true)
    }
    
    /**
     * 确认购买
     */
    fun confirmPurchase() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            isPurchasing = true,
            showPaymentDialog = false
        )
        
        viewModelScope.launch {
            try {
                val result = propPurchaseService.purchaseLuckyNumber(
                    token = currentState.token!!,
                    luckyNumberId = currentState.luckyNumber!!.id,
                    finalPrice = currentState.finalPrice
                )
                
                when (result) {
                    is PropPurchaseService.PurchaseResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isPurchasing = false,
                            purchaseResult = PurchaseResult.Success("购买成功！获得靓号: ${currentState.luckyNumber!!.number}")
                        )
                    }
                    is PropPurchaseService.PurchaseResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isPurchasing = false,
                            purchaseResult = PurchaseResult.Error(result.message)
                        )
                    }
                    is PropPurchaseService.PurchaseResult.InsufficientBalance -> {
                        _uiState.value = _uiState.value.copy(
                            isPurchasing = false,
                            purchaseResult = PurchaseResult.Error("余额不足，需要 ${result.required} 金币，当前余额 ${result.current} 金币")
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isPurchasing = false,
                    purchaseResult = PurchaseResult.Error("购买失败: ${e.message}")
                )
            }
        }
    }
    
    /**
     * 取消购买
     */
    fun cancelPurchase() {
        _uiState.value = _uiState.value.copy(showPaymentDialog = false)
    }
    
    /**
     * 显示支付对话框
     */
    fun showPaymentDialog() {
        _uiState.value = _uiState.value.copy(showPaymentDialog = true)
    }
    
    /**
     * 隐藏支付对话框
     */
    fun hidePaymentDialog() {
        _uiState.value = _uiState.value.copy(showPaymentDialog = false)
    }
    
    /**
     * 清除购买结果
     */
    fun clearPurchaseResult() {
        _uiState.value = _uiState.value.copy(purchaseResult = null)
    }
}

/**
 * 道具购买UI状态
 */
data class PropPurchaseUiState(
    val luckyNumber: LuckyNumber? = null,
    val token: String? = null,
    val originalPrice: Long = 0,
    val discountAmount: Long = 0,
    val finalPrice: Long = 0,
    val isPurchasing: Boolean = false,
    val showPaymentDialog: Boolean = false,
    val purchaseResult: PurchaseResult? = null
)

/**
 * 购买结果
 */
sealed class PurchaseResult {
    data class Success(val message: String) : PurchaseResult()
    data class Error(val message: String) : PurchaseResult()
}
