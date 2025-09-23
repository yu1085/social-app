package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.service.WealthLevelApiService
import com.example.myapplication.model.WealthLevelData
import com.example.myapplication.model.PrivilegeType
import com.example.myapplication.model.LevelProgressInfo
import com.example.myapplication.model.LevelRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 财富等级ViewModel
 * 管理财富等级相关的UI状态和业务逻辑
 */
class WealthLevelViewModel : ViewModel() {
    
    private val apiService = WealthLevelApiService()
    
    // UI状态
    private val _uiState = MutableStateFlow(WealthLevelUiState())
    val uiState: StateFlow<WealthLevelUiState> = _uiState.asStateFlow()
    
    /**
     * 加载财富等级信息
     */
    fun loadWealthLevel(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // 获取财富等级信息
                val wealthLevelResult = apiService.getMyWealthLevel(token)
                if (wealthLevelResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        wealthLevel = wealthLevelResult.getOrNull(),
                        isLoading = false
                    )
                    
                    // 获取用户特权
                    loadUserPrivileges(token)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = wealthLevelResult.exceptionOrNull()?.message ?: "加载失败"
                    )
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
     * 加载用户特权
     */
    private fun loadUserPrivileges(token: String) {
        viewModelScope.launch {
            try {
                val privilegeResult = apiService.getUserPrivileges(token)
                if (privilegeResult.isSuccess) {
                    val privileges = privilegeResult.getOrNull() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        privileges = privileges
                    )
                } else {
                    // 特权加载失败不影响主功能，只记录错误
                    _uiState.value = _uiState.value.copy(
                        error = "特权加载失败: ${privilegeResult.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                // 特权加载异常不影响主功能
                _uiState.value = _uiState.value.copy(
                    error = "特权加载异常: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 加载财富排行榜
     */
    fun loadWealthRanking(limit: Int = 10) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingRanking = true, rankingError = null)
            
            try {
                val rankingResult = apiService.getWealthRanking(limit)
                if (rankingResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        rankings = rankingResult.getOrNull() ?: emptyList(),
                        isLoadingRanking = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoadingRanking = false,
                        rankingError = rankingResult.exceptionOrNull()?.message ?: "排行榜加载失败"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingRanking = false,
                    rankingError = "排行榜加载失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 加载等级规则
     */
    fun loadLevelRules() {
        viewModelScope.launch {
            try {
                val rulesResult = apiService.getLevelRules()
                if (rulesResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        levelRules = rulesResult.getOrNull() ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                // 等级规则加载失败不影响主功能
            }
        }
    }
    
    /**
     * 刷新数据
     */
    fun refresh(token: String) {
        loadWealthLevel(token)
    }
    
    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, rankingError = null)
    }
}

/**
 * 财富等级UI状态
 */
data class WealthLevelUiState(
    val wealthLevel: WealthLevelData? = null,
    val privileges: List<PrivilegeType> = emptyList(),
    val rankings: List<WealthLevelData> = emptyList(),
    val levelRules: List<LevelRule> = emptyList(),
    val levelProgress: LevelProgressInfo? = null,
    val isLoading: Boolean = false,
    val isLoadingRanking: Boolean = false,
    val error: String? = null,
    val rankingError: String? = null
)
