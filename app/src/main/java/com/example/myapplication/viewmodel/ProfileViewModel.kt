package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * 个人中心页面ViewModel - 完全独立实现
 * 管理个人中心页面的数据状态和业务逻辑
 */
class ProfileViewModel : ViewModel() {
    
    // 用户信息 - 从后端API获取
    var userName by mutableStateOf("加载中...")
        private set
    
    var userId by mutableStateOf("加载中...")
        private set
    
    var userAvatar by mutableStateOf("") // 使用默认头像
        private set
    
    // 加载状态
    var isLoading by mutableStateOf(true)
        private set
    
    // 会员信息
    var isVip by mutableStateOf(false)
        private set
    
    var vipLevel by mutableStateOf(0)
        private set
    
    // 钱包信息
    var balance by mutableStateOf(0.0)
        private set
    
    var wealthLevel by mutableStateOf(1)
        private set
    
    var itemShopLevel by mutableStateOf(1)
        private set
    
    // 功能设置状态
    var voiceCallEnabled by mutableStateOf(true)
        private set
    
    var videoCallEnabled by mutableStateOf(true)
        private set
    
    var messageChargeEnabled by mutableStateOf(false)
        private set
    
    // 通知数量
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
    
    /**
     * 开通VIP
     */
    fun enableVip() {
        isVip = true
        vipLevel = 1
    }
    
    /**
     * 取消VIP
     */
    fun disableVip() {
        isVip = false
        vipLevel = 0
    }
    
    /**
     * 充值
     */
    fun recharge(amount: Double) {
        balance += amount
    }
    
    /**
     * 消费
     */
    fun spend(amount: Double) {
        if (balance >= amount) {
            balance -= amount
        }
    }
    
    /**
     * 切换语音接听设置
     */
    fun toggleVoiceCall() {
        voiceCallEnabled = !voiceCallEnabled
    }
    
    /**
     * 切换视频接听设置
     */
    fun toggleVideoCall() {
        videoCallEnabled = !videoCallEnabled
    }
    
    /**
     * 切换私信收费设置
     */
    fun toggleMessageCharge() {
        messageChargeEnabled = !messageChargeEnabled
    }
    
    /**
     * 更新通知数量
     */
    fun updateNotificationCount(count: Int) {
        notificationCount = count
    }
    
    /**
     * 获取用户显示ID
     */
    fun getUserDisplayId(): String {
        return "ID:$userId"
    }
    
    /**
     * 获取余额显示文本
     */
    fun getBalanceDisplayText(): String {
        return if (balance.toInt().toDouble() == balance) {
            balance.toInt().toString()
        } else {
            String.format("%.2f", balance)
        }
    }
    
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
        return "Lv.$wealthLevel"
    }
    
    /**
     * 获取道具商城等级显示文本
     */
    fun getItemShopLevelText(): String {
        return "Lv.$itemShopLevel"
    }
    
    /**
     * 生成随机昵称
     */
    private fun generateRandomNickname(): String {
        val adjectives = listOf(
            "阳光", "温柔", "帅气", "可爱", "优雅", "迷人", "清新", "活力",
            "梦幻", "神秘", "浪漫", "甜美", "酷炫", "时尚", "知性", "文艺"
        )
        val nouns = listOf(
            "小仙女", "小王子", "小公主", "小天使", "小精灵", "小可爱", "小甜心", "小宝贝",
            "小星星", "小月亮", "小太阳", "小花朵", "小蝴蝶", "小猫咪", "小兔子", "小熊猫"
        )
        val numbers = (100..999).random()
        
        return adjectives.random() + nouns.random() + numbers
    }
    
    /**
     * 生成随机用户ID
     */
    private fun generateRandomUserId(): String {
        return (10000000..99999999).random().toString()
    }
    
    /**
     * 刷新用户信息 - 从后端API获取真实数据
     */
    fun refreshUserInfo() {
        isLoading = true
        // 这里会调用NetworkService获取用户信息
        // 在ProfileScreen中通过NetworkService调用
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
}
