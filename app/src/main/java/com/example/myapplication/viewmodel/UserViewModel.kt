package com.example.myapplication.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.UserCard
import com.example.myapplication.network.NetworkService
import kotlinx.coroutines.launch
import android.util.Log

/**
 * 用户数据ViewModel
 * 管理首页用户卡片数据
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {
    
    // 用户卡片列表 - 使用LiveData
    private val _userCards = MutableLiveData<List<UserCard>>()
    val userCards: LiveData<List<UserCard>> = _userCards
    
    // 加载状态 - 使用LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 错误信息 - 使用LiveData
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 当前页码
    private var currentPage = 0
    private val pageSize = 10
    
    init {
        loadUserCards()
    }
    
    /**
     * 加载用户卡片数据
     */
    fun loadUserCards() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                Log.d("UserViewModel", "开始加载用户卡片...")
                
                val response = NetworkService.getHomeUserCards(currentPage, pageSize)
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        _userCards.value = apiResponse.data ?: emptyList()
                        Log.d("UserViewModel", "用户卡片加载成功: ${_userCards.value?.size} 个")
                    } else {
                        _errorMessage.value = apiResponse?.message ?: "加载失败"
                        Log.e("UserViewModel", "API返回失败: ${apiResponse?.message}")
                    }
                } else {
                    _errorMessage.value = "网络请求失败: ${response.code()}"
                    Log.e("UserViewModel", "网络请求失败: ${response.code()}")
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "加载用户卡片失败: ${e.message}"
                Log.e("UserViewModel", "加载用户卡片异常", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 刷新用户卡片数据
     */
    fun refreshUserCards() {
        currentPage = 0
        loadUserCards()
    }
    
    /**
     * 加载更多用户卡片
     */
    fun loadMoreUserCards() {
        if (_isLoading.value == true) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                currentPage++
                Log.d("UserViewModel", "加载更多用户卡片，页码: $currentPage")
                
                val response = NetworkService.getHomeUserCards(currentPage, pageSize)
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        val newCards = apiResponse.data ?: emptyList()
                        val currentCards = _userCards.value ?: emptyList()
                        _userCards.value = currentCards + newCards
                        Log.d("UserViewModel", "加载更多成功: ${newCards.size} 个，总计: ${_userCards.value?.size} 个")
                    } else {
                        currentPage-- // 回退页码
                        _errorMessage.value = apiResponse?.message ?: "加载更多失败"
                    }
                } else {
                    currentPage-- // 回退页码
                    _errorMessage.value = "网络请求失败: ${response.code()}"
                }
                
            } catch (e: Exception) {
                currentPage-- // 回退页码
                _errorMessage.value = "加载更多失败: ${e.message}"
                Log.e("UserViewModel", "加载更多异常", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 获取用户详情
     */
    fun getUserDetail(userId: Long, onSuccess: (UserCard) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("UserViewModel", "获取用户详情: $userId")
                
                val response = NetworkService.getUserDetail(userId)
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        val userDetail = apiResponse.data
                        if (userDetail != null) {
                            onSuccess(userDetail)
                            Log.d("UserViewModel", "用户详情获取成功: ${userDetail.nickname}")
                        } else {
                            onError("用户详情为空")
                        }
                    } else {
                        onError(apiResponse?.message ?: "获取用户详情失败")
                    }
                } else {
                    onError("网络请求失败: ${response.code()}")
                }
                
            } catch (e: Exception) {
                onError("获取用户详情异常: ${e.message}")
                Log.e("UserViewModel", "获取用户详情异常", e)
            }
        }
    }
    
    /**
     * 搜索用户
     */
    fun searchUsers(keyword: String? = null, location: String? = null, gender: String? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                Log.d("UserViewModel", "搜索用户: keyword=$keyword, location=$location, gender=$gender")
                
                val response = NetworkService.searchUsers(keyword, location, gender, 0, pageSize)
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        _userCards.value = apiResponse.data ?: emptyList()
                        currentPage = 0
                        Log.d("UserViewModel", "搜索成功: ${_userCards.value?.size} 个用户")
                    } else {
                        _errorMessage.value = apiResponse?.message ?: "搜索失败"
                    }
                } else {
                    _errorMessage.value = "网络请求失败: ${response.code()}"
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "搜索用户异常: ${e.message}"
                Log.e("UserViewModel", "搜索用户异常", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * 获取在线用户数量
     */
    fun getOnlineUserCount(): Int {
        return _userCards.value?.count { it.isOnline } ?: 0
    }
    
    /**
     * 获取离线用户数量
     */
    fun getOfflineUserCount(): Int {
        return _userCards.value?.count { !it.isOnline } ?: 0
    }
}
