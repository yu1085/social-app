package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.VipLevel
import com.example.myapplication.model.VipSubscription
import com.example.myapplication.service.VipService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * VIP会员ViewModel
 */
class VipViewModel : ViewModel() {
    
    private val vipService = VipService.getInstance()
    
    // UI状态
    private val _uiState = MutableStateFlow(VipUiState())
    val uiState: StateFlow<VipUiState> = _uiState.asStateFlow()
    
    /**
     * 加载VIP等级列表
     */
    fun loadVipLevels() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val levels = vipService.getVipLevels()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    vipLevels = levels,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载VIP等级失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 获取当前VIP订阅状态
     */
    fun loadCurrentVipSubscription(token: String?) {
        if (token == null) {
            _uiState.value = _uiState.value.copy(
                error = "请先登录"
            )
            return
        }
        
        viewModelScope.launch {
            try {
                val subscription = vipService.getCurrentVipSubscription(token)
                _uiState.value = _uiState.value.copy(
                    currentSubscription = subscription,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "获取VIP状态失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 订阅VIP
     */
    fun subscribeVip(token: String?, vipLevelId: Long) {
        if (token == null) {
            _uiState.value = _uiState.value.copy(
                error = "请先登录"
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubscribing = true, error = null)
            
            try {
                val subscription = vipService.subscribeVip(token, vipLevelId)
                _uiState.value = _uiState.value.copy(
                    isSubscribing = false,
                    currentSubscription = subscription,
                    subscriptionResult = "订阅成功！"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubscribing = false,
                    error = "订阅失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 检查VIP状态
     */
    fun checkVipStatus(token: String?) {
        if (token == null) {
            _uiState.value = _uiState.value.copy(
                error = "请先登录"
            )
            return
        }
        
        viewModelScope.launch {
            try {
                val isVip = vipService.checkVipStatus(token)
                _uiState.value = _uiState.value.copy(
                    isVip = isVip,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "检查VIP状态失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * 清除订阅结果
     */
    fun clearSubscriptionResult() {
        _uiState.value = _uiState.value.copy(subscriptionResult = null)
    }
}

/**
 * VIP UI状态
 */
data class VipUiState(
    val isLoading: Boolean = false,
    val isSubscribing: Boolean = false,
    val vipLevels: List<VipLevel> = emptyList(),
    val currentSubscription: VipSubscription? = null,
    val isVip: Boolean = false,
    val error: String? = null,
    val subscriptionResult: String? = null
)
