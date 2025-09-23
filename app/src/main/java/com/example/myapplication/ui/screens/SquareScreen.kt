package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.ui.components.*
import com.example.myapplication.viewmodel.SquareTab
import com.example.myapplication.viewmodel.SquareViewModel
import com.example.myapplication.viewmodel.EnhancedSquareViewModel
import com.example.myapplication.dto.EnhancedPostDTO

/**
 * 广场界面
 */
@Composable
fun SquareScreen(
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: EnhancedSquareViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 顶部标题栏
        TopTitleBar()
        
        // 标签页
        TabRow(
            selectedTab = selectedTab,
            onTabSelected = { viewModel.selectTab(it) }
        )
        
        // 动态列表
        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.posts.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.posts) { post ->
                    // 添加虚线分隔线
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(1.dp)
                            .background(
                                color = Color(0xFFE0E0E0),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(0.5.dp)
                            )
                    )
                    
                    EnhancedDynamicCard(
                        post = post,
                        onLikeClick = { viewModel.toggleLikePost(it) },
                        onUserClick = { onUserClick(it.toString()) },
                        onCommentClick = { /* TODO: 实现评论功能 */ }
                    )
                }
                
                // 加载更多
                if (uiState.hasMoreData) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 顶部标题栏
 */
@Composable
private fun TopTitleBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：动态标题（无图标）
            Text(
                text = stringResource(R.string.square_dynamic),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 小视频文字（无图标）
            Text(
                text = stringResource(R.string.square_video),
                fontSize = 18.sp,
                color = Color(0xFFABABAB)
            )
        }
    }
}

/**
 * 加载指示器
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * 空状态
 */
@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = "空状态",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.tip_no_data),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
