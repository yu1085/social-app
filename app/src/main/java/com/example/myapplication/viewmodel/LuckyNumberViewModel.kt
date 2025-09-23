package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.example.myapplication.service.LuckyNumberService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 靓号ViewModel
 * 管理靓号相关的UI状态和业务逻辑
 */
class LuckyNumberViewModel : ViewModel() {
    
    private val luckyNumberService = LuckyNumberService.getInstance()
    
    // UI状态
    private val _uiState = MutableStateFlow(LuckyNumberUiState())
    val uiState: StateFlow<LuckyNumberUiState> = _uiState.asStateFlow()
    
    /**
     * 加载靓号列表
     */
    fun loadLuckyNumbers(
        filter: LuckyNumberFilter? = null,
        sortBy: LuckyNumberSortBy = LuckyNumberSortBy.TIER
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = luckyNumberService.getLuckyNumbers()
                
                when (result) {
                    is LuckyNumberService.LuckyNumberResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            luckyNumbers = result.data,
                            isLoading = false
                        )
                    }
                    is LuckyNumberService.LuckyNumberResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 购买靓号
     */
    fun purchaseLuckyNumber(token: String, luckyNumberId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPurchasing = true, purchaseError = null)
            
            try {
                val result = luckyNumberService.purchaseLuckyNumber(token, luckyNumberId)
                
                when (result) {
                    is LuckyNumberPurchaseResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isPurchasing = false,
                            purchaseSuccess = "购买成功！获得靓号: ${result.luckyNumber.number}"
                        )
                        // 刷新列表
                        loadLuckyNumbers()
                    }
                    is LuckyNumberPurchaseResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isPurchasing = false,
                            purchaseError = result.message
                        )
                    }
                    is LuckyNumberPurchaseResult.InsufficientBalance -> {
                        _uiState.value = _uiState.value.copy(
                            isPurchasing = false,
                            purchaseError = "余额不足，需要 ${result.required} 金币，当前余额 ${result.current} 金币"
                        )
                    }
                    is LuckyNumberPurchaseResult.AlreadyOwned -> {
                        _uiState.value = _uiState.value.copy(
                            isPurchasing = false,
                            purchaseError = "您已拥有此靓号: ${result.luckyNumber.number}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isPurchasing = false,
                    purchaseError = "购买失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 筛选靓号
     */
    fun filterLuckyNumbers(filter: LuckyNumberFilter) {
        loadLuckyNumbers(filter = filter)
    }
    
    /**
     * 排序靓号
     */
    fun sortLuckyNumbers(sortBy: LuckyNumberSortBy) {
        loadLuckyNumbers(sortBy = sortBy)
    }
    
    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null,
            purchaseError = null,
            purchaseSuccess = null
        )
    }
    
    /**
     * 刷新数据
     */
    fun refresh() {
        loadLuckyNumbers()
    }
}

/**
 * 靓号UI状态
 */
data class LuckyNumberUiState(
    val luckyNumbers: List<LuckyNumber> = emptyList(),
    val isLoading: Boolean = false,
    val isPurchasing: Boolean = false,
    val error: String? = null,
    val purchaseError: String? = null,
    val purchaseSuccess: String? = null
)
