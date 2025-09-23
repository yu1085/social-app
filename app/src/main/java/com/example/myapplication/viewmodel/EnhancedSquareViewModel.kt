package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dto.EnhancedPostDTO
import com.example.myapplication.network.PostApiService
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.auth.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 增强的广场界面ViewModel
 */
class EnhancedSquareViewModel : ViewModel() {
    
    private val postApiService = RetrofitClient.create(PostApiService::class.java)
    
    private val _uiState = MutableStateFlow(EnhancedSquareUiState())
    val uiState: StateFlow<EnhancedSquareUiState> = _uiState.asStateFlow()
    
    private val _selectedTab = MutableStateFlow(SquareTab.NEARBY)
    val selectedTab: StateFlow<SquareTab> = _selectedTab.asStateFlow()
    
    private var currentPage = 0
    private var isLoadingMore = false
    private var hasMoreData = true
    
    init {
        loadPosts()
    }
    
    /**
     * 切换标签页
     */
    fun selectTab(tab: SquareTab) {
        _selectedTab.value = tab
        currentPage = 0
        hasMoreData = true
        loadPosts()
    }
    
    /**
     * 加载动态列表
     */
    fun loadPosts() {
        if (isLoadingMore) return
        
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val filter = when (_selectedTab.value) {
                    SquareTab.NEARBY -> "nearby"
                    SquareTab.LATEST -> "latest"
                    SquareTab.FRIENDS -> "friends"
                    SquareTab.LIKE -> "like"
                }
                
                val response = postApiService.getEnhancedPosts(
                    token = "Bearer ${getAuthToken()}",
                    filter = filter,
                    page = currentPage,
                    size = 10
                )
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    android.util.Log.d("EnhancedSquareViewModel", "API响应成功: ${apiResponse?.success}")
                    android.util.Log.d("EnhancedSquareViewModel", "API数据: ${apiResponse?.data}")
                    
                    if (apiResponse?.success == true && apiResponse.data != null) {
                        val pageData = apiResponse.data
                        android.util.Log.d("EnhancedSquareViewModel", "分页数据: ${pageData.content.size}条动态")
                        android.util.Log.d("EnhancedSquareViewModel", "第一条动态: ${pageData.content.firstOrNull()}")
                        
                        val newPosts = if (currentPage == 0) {
                            pageData.content
                        } else {
                            _uiState.value.posts + pageData.content
                        }
                        
                        android.util.Log.d("EnhancedSquareViewModel", "更新后的动态列表: ${newPosts.size}条")
                        
                        _uiState.value = _uiState.value.copy(
                            posts = newPosts,
                            isLoading = false,
                            hasMoreData = !pageData.last
                        )
                        
                        currentPage++
                        hasMoreData = !pageData.last
                    } else {
                        android.util.Log.e("EnhancedSquareViewModel", "API返回失败: ${apiResponse?.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "数据加载失败: ${apiResponse?.message ?: "未知错误"}"
                        )
                    }
                } else {
                    android.util.Log.e("EnhancedSquareViewModel", "HTTP请求失败: ${response.code()}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "网络请求失败: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("EnhancedSquareViewModel", "加载动态失败", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "加载失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 加载更多动态
     */
    fun loadMorePosts() {
        if (!hasMoreData || isLoadingMore) return
        
        isLoadingMore = true
        viewModelScope.launch {
            try {
                val filter = when (_selectedTab.value) {
                    SquareTab.NEARBY -> "nearby"
                    SquareTab.LATEST -> "latest"
                    SquareTab.FRIENDS -> "friends"
                    SquareTab.LIKE -> "like"
                }
                
                val response = postApiService.getEnhancedPosts(
                    token = "Bearer ${getAuthToken()}",
                    filter = filter,
                    page = currentPage,
                    size = 10
                )
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true && apiResponse.data != null) {
                        val pageData = apiResponse.data
                        val newPosts = _uiState.value.posts + pageData.content
                        _uiState.value = _uiState.value.copy(
                            posts = newPosts,
                            hasMoreData = !pageData.last
                        )
                        
                        currentPage++
                        hasMoreData = !pageData.last
                    }
                }
            } catch (e: Exception) {
                // 静默处理加载更多失败
            } finally {
                isLoadingMore = false
            }
        }
    }
    
    /**
     * 点赞/取消点赞动态
     */
    fun toggleLikePost(postId: Long) {
        viewModelScope.launch {
            try {
                val response = postApiService.toggleLikePost(
                    token = "Bearer ${getAuthToken()}",
                    postId = postId
                )
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true && apiResponse.data != null) {
                        val updatedPost = apiResponse.data
                        val currentPosts = _uiState.value.posts.toMutableList()
                        val index = currentPosts.indexOfFirst { it.id == postId }
                        if (index != -1) {
                            currentPosts[index] = updatedPost
                            _uiState.value = _uiState.value.copy(posts = currentPosts)
                        }
                    }
                }
            } catch (e: Exception) {
                // 静默处理点赞失败
            }
        }
    }
    
    /**
     * 添加评论
     */
    fun addComment(postId: Long, content: String, parentId: Long? = null) {
        viewModelScope.launch {
            try {
                val response = postApiService.addComment(
                    token = "Bearer ${getAuthToken()}",
                    postId = postId,
                    content = content,
                    parentId = parentId
                )
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.success == true && apiResponse.data != null) {
                        val updatedPost = apiResponse.data
                        val currentPosts = _uiState.value.posts.toMutableList()
                        val index = currentPosts.indexOfFirst { it.id == postId }
                        if (index != -1) {
                            currentPosts[index] = updatedPost
                            _uiState.value = _uiState.value.copy(posts = currentPosts)
                        }
                    }
                }
            } catch (e: Exception) {
                // 静默处理评论失败
            }
        }
    }
    
    /**
     * 刷新数据
     */
    fun refresh() {
        currentPage = 0
        hasMoreData = true
        loadPosts()
    }
    
    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * 获取认证Token
     */
    private fun getAuthToken(): String {
        return AuthManager.getInstance(null).getToken() ?: ""
    }
}

/**
 * 增强的广场界面UI状态
 */
data class EnhancedSquareUiState(
    val posts: List<EnhancedPostDTO> = emptyList(),
    val isLoading: Boolean = false,
    val hasMoreData: Boolean = true,
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
