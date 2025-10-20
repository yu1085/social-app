package com.example.myapplication.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.Observer

/**
 * 个人中心页面ViewModel - 完全独立实现
 * 管理个人中心页面的数据状态和业务逻辑
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    
    // 钱包ViewModel
    private val walletViewModel = WalletViewModel(application)
    
    // 直接管理余额状态
    var balance by mutableStateOf(0.0)
        private set
    
    var totalRecharge by mutableStateOf(0.0)
        private set
    
    var totalConsume by mutableStateOf(0.0)
        private set
    
    var isBalanceLoading by mutableStateOf(true)
        private set
    
    init {
        // 立即加载钱包数据，不延迟
        android.util.Log.d("ProfileViewModel", "=== ProfileViewModel初始化开始 ===")
        loadBalanceDirectly()
        
        // 加载用户设置数据
        loadUserSettings()
    }
    
    /**
     * 加载用户设置数据
     */
    private fun loadUserSettings() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // 从后端API获取用户设置 - 使用enqueue异步调用
                val authHeader = com.example.myapplication.auth.AuthManager.getInstance(getApplication()).getAuthHeader()
                
                // 使用suspend函数进行异步调用
                val settings = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    com.example.myapplication.network.NetworkConfig.getApiService()
                        .getUserSettings(authHeader)
                        .execute()
                }
                
                // 切换回主线程更新UI状态
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    if (settings.isSuccessful && settings.body() != null) {
                        val settingsData = settings.body()!!.data
                        updateSettingsInfo(
                            voiceCallEnabled = settingsData.voiceCallEnabled ?: false,
                            videoCallEnabled = settingsData.videoCallEnabled ?: false,
                            messageChargeEnabled = settingsData.messageChargeEnabled ?: false
                        )
                        
                        updatePriceInfo(
                            voiceCallPrice = settingsData.voiceCallPrice ?: 0.0,
                            videoCallPrice = settingsData.videoCallPrice ?: 0.0,
                            messagePrice = settingsData.messagePrice ?: 0.0
                        )
                        
                        android.util.Log.d("ProfileViewModel", "用户设置加载成功")
                    } else {
                        android.util.Log.e("ProfileViewModel", "用户设置加载失败: ${settings.code()}")
                        // 设置默认值
                        setDefaultSettings()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "加载用户设置异常", e)
                // 切换回主线程设置默认值
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    setDefaultSettings()
                }
            }
        }
    }
    
    /**
     * 设置默认值
     */
    private fun setDefaultSettings() {
        updateSettingsInfo(
            voiceCallEnabled = true,
            videoCallEnabled = true,
            messageChargeEnabled = false
        )
        
        updatePriceInfo(
            voiceCallPrice = 0.0,
            videoCallPrice = 0.0,
            messagePrice = 0.0
        )
    }
    
    // 用户信息 - 从后端API获取
    var userName by mutableStateOf("")
        private set
    
    var userId by mutableStateOf("")
        private set
    
    var userAvatar by mutableStateOf("")
        private set
    
    // 加载状态
    var isLoading by mutableStateOf(true)
        private set
    
    // 会员信息 - 从后端API获取
    var isVip by mutableStateOf(false)
        private set
    
    var vipLevel by mutableStateOf(0)
        private set
    
    // 财富等级 - 从后端API获取
    var wealthLevel by mutableStateOf(0)
        private set
    
    var itemShopLevel by mutableStateOf(0)
        private set
    
    // 功能设置状态 - 从后端API获取
    var voiceCallEnabled by mutableStateOf(false)
        private set
    
    var videoCallEnabled by mutableStateOf(false)
        private set
    
    var messageChargeEnabled by mutableStateOf(false)
        private set
    
    // 通话价格设置 - 从后端API获取
    var voiceCallPrice by mutableStateOf(0.0)
        private set
    
    var videoCallPrice by mutableStateOf(0.0)
        private set
    
    var messagePrice by mutableStateOf(0.0)
        private set
    
    // 通知数量 - 从后端API获取
    var notificationCount by mutableStateOf(0)
        private set
    
    /**
     * 更新用户信息
     */
    fun updateUserInfo(name: String, id: String, avatar: String = "") {
        userName = name
        userId = id
        userAvatar = avatar
        isLoading = false
    }
    
    // 所有用户操作都通过后端API处理，不再有本地硬编码逻辑
    
    /**
     * 获取用户显示ID
     */
    fun getUserDisplayId(): String {
        return "ID:$userId"
    }
    
    /**
     * 直接加载余额 - 不通过嵌套ViewModel
     */
    private fun loadBalanceDirectly() {
        viewModelScope.launch {
            try {
                // 在Main线程设置加载状态
                isBalanceLoading = true
                android.util.Log.d("ProfileViewModel", "开始加载余额...")
                
                val authManager = com.example.myapplication.auth.AuthManager.getInstance(getApplication())
                val token = authManager.getToken()
                android.util.Log.d("ProfileViewModel", "Token: $token")
                
                if (token == null) {
                    android.util.Log.e("ProfileViewModel", "Token为空，无法加载余额")
                    balance = 0.0
                    isBalanceLoading = false
                    return@launch
                }
                
                // 调用异步网络请求
                android.util.Log.d("ProfileViewModel", "调用API获取余额...")
                val response = com.example.myapplication.network.NetworkService.getWalletBalance(token)
                
                android.util.Log.d("ProfileViewModel", "API响应码: ${response.code()}")
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    android.util.Log.d("ProfileViewModel", "API响应体: $apiResponse")
                    
                    if (apiResponse?.isSuccess() == true) {
                        val newBalance = apiResponse.data?.balance?.toDouble() ?: 0.0
                        balance = newBalance
                        android.util.Log.d("ProfileViewModel", "余额加载成功: $balance")
                    } else {
                        android.util.Log.e("ProfileViewModel", "API返回失败: ${apiResponse?.message}")
                        balance = 0.0
                    }
                } else {
                    android.util.Log.e("ProfileViewModel", "API调用失败: ${response.code()}, ${response.message()}")
                    balance = 0.0
                }
                
            } catch (e: Exception) {
                balance = 0.0
                android.util.Log.e("ProfileViewModel", "余额加载异常: ${e.message}", e)
            } finally {
                isBalanceLoading = false
                android.util.Log.d("ProfileViewModel", "余额加载完成，最终余额: $balance")
            }
        }
    }
    
    /**
     * 获取余额显示文本 - 直接使用状态变量
     */
    fun getBalanceDisplayText(): String {
        val result = when {
            isBalanceLoading -> "加载中..."
            balance == 0.0 -> "0"
            else -> {
                if (balance.toInt().toDouble() == balance) {
                    balance.toInt().toString()
                } else {
                    String.format("%.2f", balance)
                }
            }
        }
        android.util.Log.d("ProfileViewModel", "getBalanceDisplayText() 被调用 - isBalanceLoading: $isBalanceLoading, balance: $balance, 返回: $result")
        return result
    }
    
    /**
     * 获取钱包数据 - 用于Compose中监听数据变化
     */
    fun getWalletData() = walletViewModel.walletData
    
    /**
     * 获取VIP状态文本
     */
    fun getVipStatusText(): String {
        return if (isVip) "VIP会员" else "普通用户"
    }
    
    /**
     * 获取财富等级显示文本
     */
    fun getWealthLevelText(): String {
        return if (wealthLevel > 0) "Lv.$wealthLevel" else "加载中..."
    }
    
    /**
     * 获取道具商城等级显示文本
     */
    fun getItemShopLevelText(): String {
        return if (itemShopLevel > 0) "Lv.$itemShopLevel" else "加载中..."
    }
    
    // 删除所有随机生成函数，数据完全从后端API获取
    
    /**
     * 刷新用户信息 - 从后端API获取真实数据
     */
    fun refreshUserInfo() {
        isLoading = true
        viewModelScope.launch {
            try {
                // TODO: 调用NetworkService获取用户信息
                // 这里需要实现从后端API获取用户信息的逻辑
            } catch (e: Exception) {
                // 处理错误
            } finally {
                isLoading = false
            }
        }
    }
    
    /**
     * 更新用户信息 - 从API响应中更新
     */
    fun updateUserInfo(nickname: String, id: Long, avatarUrl: String = "") {
        userName = nickname
        userId = id.toString()
        userAvatar = avatarUrl
        isLoading = false
    }
    
    /**
     * 设置加载状态
     */
    fun setLoadingState(loading: Boolean) {
        isLoading = loading
    }
    
    /**
     * 更新用户昵称
     */
    fun updateNickname(newNickname: String) {
        userName = newNickname
        // TODO: 调用后端API更新昵称
    }
    
    /**
     * 更新用户头像
     */
    fun updateAvatar(newAvatarUrl: String) {
        userAvatar = newAvatarUrl
        // TODO: 调用后端API更新头像
    }
    
    /**
     * 刷新钱包数据
     */
    fun refreshWalletData() {
        walletViewModel.refreshWalletBalance()
    }
    
    /**
     * 立即刷新钱包数据（不延迟）
     */
    fun refreshWalletDataImmediately() {
        android.util.Log.d("ProfileViewModel", "=== 刷新按钮被点击 ===")
        loadBalanceDirectly()
    }
    
    /**
     * 更新钱包信息
     */
    fun updateWalletInfo(balance: Double, totalRecharge: Double, totalConsume: Double) {
        this.balance = balance
        this.totalRecharge = totalRecharge
        this.totalConsume = totalConsume
        android.util.Log.d("ProfileViewModel", "钱包信息更新 - 余额: $balance, 总充值: $totalRecharge, 总消费: $totalConsume")
    }
    
    /**
     * 更新VIP信息
     */
    fun updateVipInfo(isVip: Boolean, vipLevel: Int, remainingDays: Long) {
        this.isVip = isVip
        this.vipLevel = vipLevel
        android.util.Log.d("ProfileViewModel", "VIP信息更新 - 是否VIP: $isVip, 等级: $vipLevel, 剩余天数: $remainingDays")
    }
    
    /**
     * 更新设置信息
     */
    fun updateSettingsInfo(voiceCallEnabled: Boolean, videoCallEnabled: Boolean, messageChargeEnabled: Boolean) {
        this.voiceCallEnabled = voiceCallEnabled
        this.videoCallEnabled = videoCallEnabled
        this.messageChargeEnabled = messageChargeEnabled
        android.util.Log.d("ProfileViewModel", "设置信息更新 - 语音: $voiceCallEnabled, 视频: $videoCallEnabled, 私信收费: $messageChargeEnabled")
    }
    
    /**
     * 更新设置到后端
     */
    fun updateSettingsToBackend(voiceCallEnabled: Boolean, videoCallEnabled: Boolean, messageChargeEnabled: Boolean) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val settingsDTO = com.example.myapplication.dto.UserSettingsDTO().apply {
                    this.voiceCallEnabled = voiceCallEnabled
                    this.videoCallEnabled = videoCallEnabled
                    this.messageChargeEnabled = messageChargeEnabled
                    this.voiceCallPrice = this@ProfileViewModel.voiceCallPrice
                    this.videoCallPrice = this@ProfileViewModel.videoCallPrice
                    this.messagePrice = this@ProfileViewModel.messagePrice
                }
                
                val authHeader = com.example.myapplication.auth.AuthManager.getInstance(getApplication()).getAuthHeader()
                
                val response = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    com.example.myapplication.network.NetworkConfig.getApiService()
                        .updateUserSettings(authHeader, settingsDTO)
                        .execute()
                }
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val updatedSettings = response.body()!!.data
                        updateSettingsInfo(
                            voiceCallEnabled = updatedSettings.voiceCallEnabled ?: false,
                            videoCallEnabled = updatedSettings.videoCallEnabled ?: false,
                            messageChargeEnabled = updatedSettings.messageChargeEnabled ?: false
                        )
                        
                        updatePriceInfo(
                            voiceCallPrice = updatedSettings.voiceCallPrice ?: 0.0,
                            videoCallPrice = updatedSettings.videoCallPrice ?: 0.0,
                            messagePrice = updatedSettings.messagePrice ?: 0.0
                        )
                        
                        android.util.Log.d("ProfileViewModel", "设置更新到后端成功")
                    } else {
                        android.util.Log.e("ProfileViewModel", "设置更新到后端失败: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "更新设置到后端异常", e)
            }
        }
    }
    
    /**
     * 更新价格到后端
     */
    fun updatePriceToBackend(voiceCallPrice: Double, videoCallPrice: Double, messagePrice: Double) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val settingsDTO = com.example.myapplication.dto.UserSettingsDTO().apply {
                    this.voiceCallEnabled = this@ProfileViewModel.voiceCallEnabled
                    this.videoCallEnabled = this@ProfileViewModel.videoCallEnabled
                    this.messageChargeEnabled = this@ProfileViewModel.messageChargeEnabled
                    this.voiceCallPrice = voiceCallPrice
                    this.videoCallPrice = videoCallPrice
                    this.messagePrice = messagePrice
                }
                
                val authHeader = com.example.myapplication.auth.AuthManager.getInstance(getApplication()).getAuthHeader()
                
                val response = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    com.example.myapplication.network.NetworkConfig.getApiService()
                        .updateUserSettings(authHeader, settingsDTO)
                        .execute()
                }
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val updatedSettings = response.body()!!.data
                        updateSettingsInfo(
                            voiceCallEnabled = updatedSettings.voiceCallEnabled ?: false,
                            videoCallEnabled = updatedSettings.videoCallEnabled ?: false,
                            messageChargeEnabled = updatedSettings.messageChargeEnabled ?: false
                        )
                        
                        updatePriceInfo(
                            voiceCallPrice = updatedSettings.voiceCallPrice ?: 0.0,
                            videoCallPrice = updatedSettings.videoCallPrice ?: 0.0,
                            messagePrice = updatedSettings.messagePrice ?: 0.0
                        )
                        
                        android.util.Log.d("ProfileViewModel", "价格更新到后端成功")
                    } else {
                        android.util.Log.e("ProfileViewModel", "价格更新到后端失败: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileViewModel", "更新价格到后端异常", e)
            }
        }
    }
    
    /**
     * 更新价格信息
     */
    fun updatePriceInfo(voiceCallPrice: Double, videoCallPrice: Double, messagePrice: Double) {
        this.voiceCallPrice = voiceCallPrice
        this.videoCallPrice = videoCallPrice
        this.messagePrice = messagePrice
        android.util.Log.d("ProfileViewModel", "价格信息更新 - 语音: $voiceCallPrice, 视频: $videoCallPrice, 私信: $messagePrice")
    }
    
    /**
     * 获取语音通话显示文本
     */
    fun getVoiceCallDisplayText(): String {
        return if (voiceCallEnabled) {
            "${voiceCallPrice.toInt()}/分钟"
        } else {
            "关闭语音"
        }
    }

    /**
     * 获取视频通话显示文本
     */
    fun getVideoCallDisplayText(): String {
        return if (videoCallEnabled) {
            "${videoCallPrice.toInt()}/分钟"
        } else {
            "关闭视频"
        }
    }
    
    /**
     * 获取私信收费显示文本
     */
    fun getMessageChargeDisplayText(): String {
        return if (messageChargeEnabled) "收费" else "免费"
    }
}
