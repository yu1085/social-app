package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.model.Dynamic
import com.example.myapplication.model.User
import com.example.myapplication.model.UserStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 广场界面ViewModel
 */
class SquareViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SquareUiState())
    val uiState: StateFlow<SquareUiState> = _uiState.asStateFlow()
    
    private val _selectedTab = MutableStateFlow(SquareTab.NEARBY)
    val selectedTab: StateFlow<SquareTab> = _selectedTab.asStateFlow()
    
    init {
        loadMockData()
    }
    
    /**
     * 切换标签页
     */
    fun selectTab(tab: SquareTab) {
        _selectedTab.value = tab
        // 根据标签页加载不同数据
        when (tab) {
            SquareTab.NEARBY -> loadNearbyDynamics()
            SquareTab.LATEST -> loadLatestDynamics()
            SquareTab.FRIENDS -> loadFriendsDynamics()
            SquareTab.LIKE -> loadLikedDynamics()
        }
    }
    
    /**
     * 点赞动态
     */
    fun likeDynamic(dynamicId: String) {
        val currentDynamics = _uiState.value.dynamics.toMutableList()
        val index = currentDynamics.indexOfFirst { it.id == dynamicId }
        if (index != -1) {
            val dynamic = currentDynamics[index]
            currentDynamics[index] = dynamic.copy(
                isLiked = !dynamic.isLiked,
                likeCount = if (dynamic.isLiked) dynamic.likeCount - 1 else dynamic.likeCount + 1
            )
            _uiState.value = _uiState.value.copy(dynamics = currentDynamics)
        }
    }
    
    /**
     * 加载模拟数据
     */
    private fun loadMockData() {
        viewModelScope.launch {
            val mockDynamics = createMockDynamics()
            _uiState.value = _uiState.value.copy(
                dynamics = mockDynamics,
                isLoading = false
            )
        }
    }
    
    /**
     * 加载附近动态
     */
    private fun loadNearbyDynamics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // 模拟网络延迟
            kotlinx.coroutines.delay(500)
            val nearbyDynamics = createMockDynamics().filter { it.user.distance <= 10.0 }
            _uiState.value = _uiState.value.copy(
                dynamics = nearbyDynamics,
                isLoading = false
            )
        }
    }
    
    /**
     * 加载最新动态
     */
    private fun loadLatestDynamics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            kotlinx.coroutines.delay(500)
            val latestDynamics = createMockDynamics().sortedByDescending { it.publishTime }
            _uiState.value = _uiState.value.copy(
                dynamics = latestDynamics,
                isLoading = false
            )
        }
    }
    
    /**
     * 加载知友动态
     */
    private fun loadFriendsDynamics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            kotlinx.coroutines.delay(500)
            val latestDynamics = createMockDynamics().sortedByDescending { it.publishTime }
            _uiState.value = _uiState.value.copy(
                dynamics = latestDynamics,
                isLoading = false
            )
        }
    }
    
    /**
     * 加载喜欢的动态
     */
    private fun loadLikedDynamics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            kotlinx.coroutines.delay(500)
            val likedDynamics = createMockDynamics().filter { it.isLiked }
            _uiState.value = _uiState.value.copy(
                dynamics = likedDynamics,
                isLoading = false
            )
        }
    }
    
    /**
     * 创建模拟动态数据
     */
    private fun createMockDynamics(): List<Dynamic> {
        return listOf(
            Dynamic(
                id = "1",
                user = User(
                    id = "user1",
                    name = "小倩",
                    avatar = R.drawable.profile_avatar,
                    age = 27,
                    distance = 6.34,
                    status = UserStatus.FREE,
                    isOnline = true
                ),
                content = "有没有人交朋友啊?睡不着在一起聊聊呗。",
                images = listOf(R.drawable.dynamic_image),
                publishTime = "昨天 20:40",
                location = "安徽省",
                likeCount = 12,
                commentCount = 5,
                isFreeMinute = true
            ),
            Dynamic(
                id = "2",
                user = User(
                    id = "user2",
                    name = "小雨",
                    age = 24,
                    distance = 3.2,
                    status = UserStatus.BUSY,
                    isOnline = true
                ),
                content = "今天天气真好，有人一起出去走走吗？",
                images = listOf(R.drawable.dynamic_image),
                publishTime = "今天 10:30",
                location = "北京市",
                likeCount = 8,
                commentCount = 3
            ),
            Dynamic(
                id = "3",
                user = User(
                    id = "user3",
                    name = "小明",
                    age = 26,
                    distance = 8.7,
                    status = UserStatus.FREE,
                    isOnline = false
                ),
                content = "工作累了，想找人聊聊天",
                images = emptyList(),
                publishTime = "今天 15:20",
                location = "上海市",
                likeCount = 15,
                commentCount = 7
            )
        )
    }
}

/**
 * 广场界面UI状态
 */
data class SquareUiState(
    val dynamics: List<Dynamic> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * 广场标签页枚举
 */
enum class SquareTab(val title: String) {
    NEARBY("附近"),
    LATEST("最新"),
    FRIENDS("知友"),
    LIKE("喜欢")
}
