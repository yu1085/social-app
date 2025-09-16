package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {
    
    // 页面状态
    var isLoading by mutableStateOf(false)
        private set
    
    var currentTab by mutableStateOf(0) // 0: 消息, 1: 通话, 2: 关系
        private set
    
    var notificationEnabled by mutableStateOf(false)
        private set
    
    // 推荐用户列表
    var recommendedUsers by mutableStateOf(
        listOf(
            RecommendedUser("空闲", UserStatus.IDLE),
            RecommendedUser("忙碌", UserStatus.BUSY),
            RecommendedUser("空闲", UserStatus.IDLE),
            RecommendedUser("忙碌", UserStatus.BUSY)
        )
    )
        private set
    
    // 消息列表
    var messages by mutableStateOf(
        listOf(
            Message(
                id = "1",
                name = "你的小可爱512",
                content = "[视频通话]",
                time = "刚刚",
                avatarColor = 0xFFD9D9D9,
                unreadCount = 0,
                isOnline = true,
                type = MessageType.VIDEO_CALL
            ),
            Message(
                id = "2",
                name = "漫步的美人鱼",
                content = "晚上好呀，在干嘛?",
                time = "12小时前",
                avatarColor = 0xFFD9D9D9,
                unreadCount = 1,
                isOnline = false,
                type = MessageType.CHAT
            ),
            Message(
                id = "3",
                name = "谁来看过我",
                content = "有个小姐姐看了你，对你很感兴趣，点击看...",
                time = "12小时前",
                avatarColor = 0xFFD9D9D9,
                unreadCount = 1,
                isOnline = false,
                type = MessageType.VISITOR
            ),
            Message(
                id = "4",
                name = "提醒",
                content = "给您赠送的免费私信马上要过期了哦，还...",
                time = "12小时前",
                avatarColor = 0xFF37D5EE,
                unreadCount = 4,
                isOnline = false,
                type = MessageType.SYSTEM
            )
        )
    )
        private set
    
    // 切换标签页
    fun switchTab(tabIndex: Int) {
        currentTab = tabIndex
    }
    
    // 刷新推荐用户
    fun refreshRecommendedUsers() {
        viewModelScope.launch {
            isLoading = true
            delay(1000) // 模拟网络请求
            
            // 随机打乱用户状态
            recommendedUsers = recommendedUsers.shuffled()
            isLoading = false
        }
    }
    
    // 开启通知
    fun enableNotification() {
        notificationEnabled = true
        // 这里可以添加实际的通知权限请求逻辑
    }
    
    // 标记消息为已读
    fun markMessageAsRead(messageId: String) {
        messages = messages.map { message ->
            if (message.id == messageId) {
                message.copy(unreadCount = 0)
            } else {
                message
            }
        }
    }
    
    // 删除消息
    fun deleteMessage(messageId: String) {
        messages = messages.filter { it.id != messageId }
    }
    
    // 获取未读消息总数
    fun getTotalUnreadCount(): Int {
        return messages.sumOf { it.unreadCount }
    }
}

// 数据模型
enum class UserStatus {
    IDLE, BUSY
}

enum class MessageType {
    CHAT, VIDEO_CALL, VISITOR, SYSTEM
}

data class RecommendedUser(
    val name: String,
    val status: UserStatus
)

data class Message(
    val id: String,
    val name: String,
    val content: String,
    val time: String,
    val avatarColor: Long,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val type: MessageType
)
