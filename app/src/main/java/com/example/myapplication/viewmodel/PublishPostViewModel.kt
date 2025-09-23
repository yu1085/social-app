package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.PostApiService
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

/**
 * 发布动态ViewModel
 */
class PublishPostViewModel : ViewModel() {

    private val postApiService = RetrofitClient.create(PostApiService::class.java)

    private val _uiState = MutableStateFlow(PublishPostUiState())
    val uiState: StateFlow<PublishPostUiState> = _uiState.asStateFlow()

    /**
     * 更新内容
     */
    fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
    }

    /**
     * 选择图片
     */
    fun selectImage() {
        // TODO: 实现图片选择功能
        // 这里可以集成图片选择库，如 Coil 或 Glide
        Log.d("PublishPostViewModel", "选择图片")
    }

    /**
     * 移除图片
     */
    fun removeImage(index: Int) {
        val currentImages = _uiState.value.selectedImages.toMutableList()
        if (index in currentImages.indices) {
            currentImages.removeAt(index)
            _uiState.value = _uiState.value.copy(selectedImages = currentImages)
        }
    }

    /**
     * 选择位置
     */
    fun selectLocation() {
        // TODO: 实现位置选择功能
        // 这里可以集成地图选择或位置搜索
        _uiState.value = _uiState.value.copy(location = "北京市朝阳区")
        Log.d("PublishPostViewModel", "选择位置")
    }

    /**
     * 发布动态
     */
    fun publishPost() {
        if (_uiState.value.content.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "请输入动态内容")
            return
        }

        if (_uiState.value.content.length > 140) {
            _uiState.value = _uiState.value.copy(error = "内容不能超过140个字符")
            return
        }

        _uiState.value = _uiState.value.copy(isPublishing = true, error = null, publishSuccess = false)

        viewModelScope.launch {
            try {
                val token = getAuthToken()
                if (token.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isPublishing = false,
                        error = "用户未登录"
                    )
                    return@launch
                }

                // 构建发布数据
                val postData = mapOf(
                    "content" to _uiState.value.content,
                    "location" to (_uiState.value.location ?: ""),
                    "imageUrl" to (_uiState.value.selectedImages.firstOrNull() ?: "")
                )

                val response = postApiService.createPost(
                    token = "Bearer $token",
                    postData = postData
                )

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true) {
                        _uiState.value = _uiState.value.copy(
                            isPublishing = false,
                            publishSuccess = true
                        )
                        Log.d("PublishPostViewModel", "动态发布成功")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isPublishing = false,
                            error = "发布失败: ${apiResponse?.message ?: "未知错误"}"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isPublishing = false,
                        error = "网络请求失败: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("PublishPostViewModel", "发布动态失败", e)
                _uiState.value = _uiState.value.copy(
                    isPublishing = false,
                    error = "发布失败: ${e.message}"
                )
            }
        }
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * 重置状态（发布成功后调用）
     */
    fun resetState() {
        _uiState.value = PublishPostUiState()
    }

    /**
     * 获取认证Token
     */
    private fun getAuthToken(): String {
        return AuthManager.getInstance(null).getToken() ?: ""
    }
}

/**
 * 发布动态UI状态
 */
data class PublishPostUiState(
    val content: String = "",
    val selectedImages: List<String> = emptyList(),
    val location: String? = null,
    val isPublishing: Boolean = false,
    val publishSuccess: Boolean = false,
    val error: String? = null
)
