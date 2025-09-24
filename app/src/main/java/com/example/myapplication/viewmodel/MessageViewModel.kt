package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

/**
 * 消息页面ViewModel
 * 管理消息列表状态、搜索、排序等功能
 */
class MessageViewModel : ViewModel() {
    
    // 消息列表状态
    private val _messages = MutableStateFlow(emptyList<MessageItem>())
    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()
    
    // 搜索状态
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // 排序方式
    private val _sortType = MutableStateFlow(SortType.TIME_DESC)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()
    
    // 是否正在加载
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // 过滤后的消息列表
    val filteredMessages: StateFlow<List<MessageItem>> = combine(
        _messages,
        _searchQuery,
        _sortType
    ) { messages, searchQuery, sortType ->
        var result = messages
        
        // 搜索过滤
        if (searchQuery.isNotEmpty()) {
            result = result.filter { message ->
                message.name.contains(searchQuery, ignoreCase = true) ||
                message.content.contains(searchQuery, ignoreCase = true)
            }
        }
        
        // 排序
        result = when (sortType) {
            SortType.TIME_DESC -> result.sortedByDescending { it.timestamp }
            SortType.TIME_ASC -> result.sortedBy { it.timestamp }
            SortType.UNREAD_FIRST -> result.sortedWith(
                compareByDescending<MessageItem> { it.unreadCount > 0 }
                    .thenByDescending { it.timestamp }
            )
            SortType.NAME_ASC -> result.sortedBy { it.name }
        }
        
        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    init {
        loadMessages()
    }
    
    /**
     * 加载消息列表
     */
    fun loadMessages() {
        _isLoading.value = true
        viewModelScope.launch {
            // 模拟网络请求延迟
            delay(1000)
            
            // 模拟数据
            _messages.value = getMockMessages()
            _isLoading.value = false
        }
    }
    
    /**
     * 设置搜索查询
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * 设置排序方式
     */
    fun setSortType(sortType: SortType) {
        _sortType.value = sortType
    }
    
    /**
     * 标记消息为已读
     */
    fun markAsRead(messageId: String) {
        _messages.value = _messages.value.map { message ->
            if (message.id == messageId) {
                message.copy(unreadCount = 0)
            } else {
                message
            }
        }
    }
    
    /**
     * 删除消息
     */
    fun deleteMessage(messageId: String) {
        _messages.value = _messages.value.filter { it.id != messageId }
    }
    
    /**
     * 刷新消息列表
     */
    fun refreshMessages() {
        loadMessages()
    }
    
    /**
     * 获取模拟消息数据
     */
    private fun getMockMessages(): List<MessageItem> {
        return listOf(
            MessageItem(
                id = "1",
                name = "你的小可爱512",
                content = "[视频通话]",
                time = "刚刚",
                avatarImage = "group_27",
                unreadCount = 0,
                isOnline = true,
                timestamp = System.currentTimeMillis()
            ),
            MessageItem(
                id = "2",
                name = "漫步的美人鱼",
                content = "晚上好呀，在干嘛?",
                time = "12小时前",
                avatarImage = "group_28",
                unreadCount = 1,
                isOnline = false,
                timestamp = System.currentTimeMillis() - 12 * 60 * 60 * 1000
            ),
            MessageItem(
                id = "3",
                name = "谁来看过我",
                content = "有个小姐姐看了你，对你很感兴趣，点击看...",
                time = "12小时前",
                avatarImage = "group_29",
                unreadCount = 1,
                isOnline = false,
                timestamp = System.currentTimeMillis() - 12 * 60 * 60 * 1000
            ),
            MessageItem(
                id = "4",
                name = "提醒",
                content = "给您赠送的免费私信马上要过期了哦，还...",
                time = "12小时前",
                avatarImage = "group_30",
                unreadCount = 4,
                isOnline = false,
                timestamp = System.currentTimeMillis() - 12 * 60 * 60 * 1000
            ),
            MessageItem(
                id = "5",
                name = "小雅",
                content = "今天天气真好，要不要一起出去走走？",
                time = "1小时前",
                avatarImage = "group_27",
                unreadCount = 2,
                isOnline = false,
                timestamp = System.currentTimeMillis() - 1 * 60 * 60 * 1000
            ),
            MessageItem(
                id = "6",
                name = "小雨",
                content = "好的，那我们明天见！",
                time = "2小时前",
                avatarImage = "group_28",
                unreadCount = 0,
                isOnline = false,
                timestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000
            ),
            MessageItem(
                id = "7",
                name = "小美",
                content = "[语音通话]",
                time = "3小时前",
                avatarImage = "group_29",
                unreadCount = 0,
                isOnline = false,
                timestamp = System.currentTimeMillis() - 3 * 60 * 60 * 1000
            ),
            MessageItem(
                id = "8",
                name = "小琳",
                content = "谢谢你的礼物，我很喜欢！",
                time = "5小时前",
                avatarImage = "group_30",
                unreadCount = 1,
                isOnline = false,
                timestamp = System.currentTimeMillis() - 5 * 60 * 60 * 1000
            ),
            MessageItem(
                id = "9",
                name = "甜心宝贝",
                content = "最近工作忙吗？",
                time = "1天前",
                avatarImage = "group_27",
                unreadCount = 0,
                isOnline = false,
                timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            ),
            MessageItem(
                id = "10",
                name = "不吃香菜",
                content = "周末有空一起看电影吗？",
                time = "2天前",
                avatarImage = "group_28",
                unreadCount = 3,
                isOnline = false,
                timestamp = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000
            )
        )
    }
}

/**
 * 消息数据类
 */
data class MessageItem(
    val id: String,
    val name: String,
    val content: String,
    val time: String,
    val avatarImage: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 排序类型枚举
 */
enum class SortType {
    TIME_DESC,      // 按时间降序（最新在前）
    TIME_ASC,       // 按时间升序（最旧在前）
    UNREAD_FIRST,   // 未读消息在前
    NAME_ASC        // 按姓名升序
}