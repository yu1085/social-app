package com.example.myapplication.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.auth.AuthManager
import com.example.myapplication.dto.UserSettingsDTO
import com.example.myapplication.network.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 价格设置ViewModel
 */
class PriceSettingsViewModel(private val context: Context) : ViewModel() {

    private val authManager = AuthManager.getInstance(context)

    private val _uiState = MutableStateFlow(PriceSettingsUiState())
    val uiState: StateFlow<PriceSettingsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentSettings()
    }

    /**
     * 加载当前设置
     */
    fun loadCurrentSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val token = authManager.getToken()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "用户未登录"
                    )
                    return@launch
                }

                val response = NetworkService.getUserSettings(token)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        val settings = apiResponse.data
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            voiceCallEnabled = settings?.voiceCallEnabled ?: true,
                            videoCallEnabled = settings?.videoCallEnabled ?: true,
                            messageChargeEnabled = settings?.messageChargeEnabled ?: false,
                            voiceCallPrice = settings?.voiceCallPrice ?: 0.0,
                            videoCallPrice = settings?.videoCallPrice ?: 0.0,
                            messagePrice = settings?.messagePrice ?: 0.0
                        )
                    } else {
                        throw Exception(apiResponse?.message ?: "获取设置失败")
                    }
                } else {
                    throw Exception("HTTP错误: ${response.code()}")
                }

            } catch (e: Exception) {
                android.util.Log.e("PriceSettingsViewModel", "加载设置失败", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载设置失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 更新语音通话开关
     */
    fun updateVoiceCallEnabled(enabled: Boolean) {
        val currentState = _uiState.value.voiceCallEnabled
        android.util.Log.d("PriceSettingsViewModel", "=== 更新语音通话开关 ===")
        android.util.Log.d("PriceSettingsViewModel", "当前状态: $currentState")
        android.util.Log.d("PriceSettingsViewModel", "新状态: $enabled")

        _uiState.value = _uiState.value.copy(voiceCallEnabled = enabled)

        android.util.Log.d("PriceSettingsViewModel", "更新后的状态: ${_uiState.value.voiceCallEnabled}")
        android.util.Log.d("PriceSettingsViewModel", "状态更新${if (_uiState.value.voiceCallEnabled == enabled) "成功" else "失败"}")
    }

    /**
     * 更新视频通话开关
     */
    fun updateVideoCallEnabled(enabled: Boolean) {
        val currentState = _uiState.value.videoCallEnabled
        android.util.Log.d("PriceSettingsViewModel", "=== 更新视频通话开关 ===")
        android.util.Log.d("PriceSettingsViewModel", "当前状态: $currentState")
        android.util.Log.d("PriceSettingsViewModel", "新状态: $enabled")

        _uiState.value = _uiState.value.copy(videoCallEnabled = enabled)

        android.util.Log.d("PriceSettingsViewModel", "更新后的状态: ${_uiState.value.videoCallEnabled}")
        android.util.Log.d("PriceSettingsViewModel", "状态更新${if (_uiState.value.videoCallEnabled == enabled) "成功" else "失败"}")
    }

    /**
     * 更新消息收费开关
     */
    fun updateMessageChargeEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(messageChargeEnabled = enabled)
    }

    /**
     * 更新语音通话价格
     */
    fun updateVoiceCallPrice(price: Double) {
        if (price >= 0 && price <= 1000) {
            _uiState.value = _uiState.value.copy(voiceCallPrice = price)
        }
    }

    /**
     * 更新视频通话价格
     */
    fun updateVideoCallPrice(price: Double) {
        if (price >= 0 && price <= 1000) {
            _uiState.value = _uiState.value.copy(videoCallPrice = price)
        }
    }

    /**
     * 更新消息价格
     */
    fun updateMessagePrice(price: Double) {
        if (price >= 0 && price <= 1000) {
            _uiState.value = _uiState.value.copy(messagePrice = price)
        }
    }

    /**
     * 保存设置
     */
    fun saveSettings(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            try {
                val token = authManager.getToken()
                if (token == null) {
                    onError("用户未登录")
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    return@launch
                }

                val currentState = _uiState.value
                val settingsDTO = UserSettingsDTO().apply {
                    voiceCallEnabled = currentState.voiceCallEnabled
                    videoCallEnabled = currentState.videoCallEnabled
                    messageChargeEnabled = currentState.messageChargeEnabled
                    voiceCallPrice = currentState.voiceCallPrice
                    videoCallPrice = currentState.videoCallPrice
                    messagePrice = currentState.messagePrice
                }

                val response = NetworkService.updateUserSettings(token, settingsDTO)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        _uiState.value = _uiState.value.copy(isSaving = false)
                        onSuccess()
                    } else {
                        throw Exception(apiResponse?.message ?: "保存设置失败")
                    }
                } else {
                    throw Exception("HTTP错误: ${response.code()}")
                }

            } catch (e: Exception) {
                android.util.Log.e("PriceSettingsViewModel", "保存设置失败", e)
                _uiState.value = _uiState.value.copy(isSaving = false)
                onError("保存设置失败: ${e.message}")
            }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * 价格设置UI状态
 */
data class PriceSettingsUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,

    // 通话开关
    val voiceCallEnabled: Boolean = true,
    val videoCallEnabled: Boolean = true,
    val messageChargeEnabled: Boolean = false,

    // 价格设置
    val voiceCallPrice: Double = 0.0,
    val videoCallPrice: Double = 0.0,
    val messagePrice: Double = 0.0
)
