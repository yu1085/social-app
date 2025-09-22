package com.example.myapplication.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.auth.AuthManager
import com.example.myapplication.dto.UserPhotoDTO
import com.example.myapplication.network.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * 相册ViewModel
 * 管理相册相关的数据和业务逻辑
 */
class AlbumViewModel(private val context: Context) : ViewModel() {
    
    private val _photos = MutableStateFlow<List<UserPhotoDTO>>(emptyList())
    val photos: StateFlow<List<UserPhotoDTO>> = _photos.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val authManager = AuthManager.getInstance(context)
    
    /**
     * 加载用户照片
     */
    fun loadUserPhotos() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val userId = authManager.getUserId() ?: 0L
                if (userId == 0L) {
                    android.util.Log.e("AlbumViewModel", "用户未登录")
                    return@launch
                }
                
                val response = NetworkService.getUserPhotos(userId, context)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        _photos.value = apiResponse.data ?: emptyList()
                        android.util.Log.d("AlbumViewModel", "加载照片成功: ${_photos.value.size}张")
                    } else {
                        android.util.Log.e("AlbumViewModel", "API返回失败: ${apiResponse?.message}")
                    }
                } else {
                    android.util.Log.e("AlbumViewModel", "网络请求失败: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("AlbumViewModel", "加载照片异常: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 上传照片
     */
    fun uploadPhoto(photoFile: File, isAvatar: Boolean = false) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                val userId = authManager.getUserId() ?: 0L
                if (userId == 0L) {
                    android.util.Log.e("AlbumViewModel", "用户未登录")
                    return@launch
                }
                
                val response = NetworkService.uploadPhoto(userId, photoFile, context, isAvatar)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        android.util.Log.d("AlbumViewModel", "上传照片成功")
                        // 重新加载照片列表
                        loadUserPhotos()
                    } else {
                        android.util.Log.e("AlbumViewModel", "上传失败: ${apiResponse?.message}")
                    }
                } else {
                    android.util.Log.e("AlbumViewModel", "上传请求失败: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("AlbumViewModel", "上传照片异常: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 删除照片
     */
    fun deletePhoto(photoId: Long) {
        viewModelScope.launch {
            try {
                val userId = authManager.getUserId() ?: 0L
                if (userId == 0L) {
                    android.util.Log.e("AlbumViewModel", "用户未登录")
                    return@launch
                }
                
                val response = NetworkService.deletePhoto(userId, photoId, context)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        android.util.Log.d("AlbumViewModel", "删除照片成功")
                        // 重新加载照片列表
                        loadUserPhotos()
                    } else {
                        android.util.Log.e("AlbumViewModel", "删除失败: ${apiResponse?.message}")
                    }
                } else {
                    android.util.Log.e("AlbumViewModel", "删除请求失败: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("AlbumViewModel", "删除照片异常: ${e.message}", e)
            }
        }
    }
    
    /**
     * 设置照片为头像
     */
    fun setAsAvatar(photoId: Long) {
        viewModelScope.launch {
            try {
                val userId = authManager.getUserId() ?: 0L
                if (userId == 0L) {
                    android.util.Log.e("AlbumViewModel", "用户未登录")
                    return@launch
                }
                
                val response = NetworkService.setAsAvatar(userId, photoId, context)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        android.util.Log.d("AlbumViewModel", "设置头像成功")
                        // 重新加载照片列表
                        loadUserPhotos()
                    } else {
                        android.util.Log.e("AlbumViewModel", "设置头像失败: ${apiResponse?.message}")
                    }
                } else {
                    android.util.Log.e("AlbumViewModel", "设置头像请求失败: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("AlbumViewModel", "设置头像异常: ${e.message}", e)
            }
        }
    }
}
