package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.auth.AuthManager
import com.example.myapplication.dto.WalletDTO
import com.example.myapplication.network.NetworkService
import kotlinx.coroutines.launch

class WalletViewModel(application: Application) : AndroidViewModel(application) {
    
    private val authManager = AuthManager.getInstance(application)
    private val networkService = NetworkService
    
    private val _walletData = MutableLiveData<WalletDTO?>()
    val walletData: LiveData<WalletDTO?> = _walletData
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    init {
        loadWalletBalance()
    }
    
    fun loadWalletBalance() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val token = authManager.getToken()
                if (token == null) {
                    _errorMessage.value = "用户未登录"
                    return@launch
                }
                
                val userId = authManager.getUserId()
                if (userId == -1L) {
                    _errorMessage.value = "用户ID无效"
                    return@launch
                }
                
                val response = networkService.getWalletBalance(token)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess() == true) {
                        _walletData.value = apiResponse.data
                    } else {
                        throw Exception(apiResponse?.message ?: "API调用失败")
                    }
                } else {
                    throw Exception("HTTP错误: ${response.code()}")
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "加载钱包余额失败: ${e.message}"
                // API调用失败时不设置模拟数据，保持为null
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshWalletBalance() {
        loadWalletBalance()
    }
}
