package com.example.myapplication.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.MessageViewModel
import com.example.myapplication.viewmodel.MessageItem
import com.example.myapplication.viewmodel.SortType

/**
 * 消息列表组件
 * 使用ViewModel管理状态，支持搜索、排序、刷新等功能
 */
@Composable
fun MessageListComponent(
    onMessageClick: (MessageItem) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MessageViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredMessages by viewModel.filteredMessages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sortType by viewModel.sortType.collectAsState()
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 搜索栏
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::setSearchQuery,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        
        // 排序选项
        SortOptions(
            currentSort = sortType,
            onSortChange = viewModel::setSortType,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        
        // 消息列表
        if (isLoading) {
            // 加载状态
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFFFE62AC)
                )
            }
        } else if (filteredMessages.isEmpty()) {
            // 空状态
            EmptyState(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // 消息列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(filteredMessages) { message ->
                    MessageItemCard(
                        message = message,
                        onClick = { onMessageClick(message) }
                    )
                    
                    // 分割线
                    if (message != filteredMessages.last()) {
                        Divider(
                            modifier = Modifier.padding(start = 60.dp, end = 0.dp),
                            color = Color(0xFFF0F0F0),
                            thickness = 0.5.dp
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

/**
 * 搜索栏组件
 */
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "搜索消息...",
                color = Color(0xFF999999),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Text(
                text = "🔍",
                fontSize = 16.sp
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Text(
                    text = "✕",
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onQueryChange("") }
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFFE62AC),
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedTextColor = Color(0xFF333333),
            unfocusedTextColor = Color(0xFF333333)
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

/**
 * 排序选项组件
 */
@Composable
private fun SortOptions(
    currentSort: SortType,
    onSortChange: (SortType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SortChip(
            label = "时间",
            isSelected = currentSort == SortType.TIME_DESC,
            onClick = { onSortChange(SortType.TIME_DESC) }
        )
        SortChip(
            label = "未读",
            isSelected = currentSort == SortType.UNREAD_FIRST,
            onClick = { onSortChange(SortType.UNREAD_FIRST) }
        )
        SortChip(
            label = "姓名",
            isSelected = currentSort == SortType.NAME_ASC,
            onClick = { onSortChange(SortType.NAME_ASC) }
        )
    }
}

/**
 * 排序芯片组件
 */
@Composable
private fun SortChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        selected = isSelected,
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFFE62AC),
            selectedLabelColor = Color.White,
            containerColor = Color(0xFFF5F5F5),
            labelColor = Color(0xFF666666)
        )
    )
}

/**
 * 消息项卡片组件
 */
@Composable
private fun MessageItemCard(
    message: MessageItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像容器
        Box(
            modifier = Modifier.size(48.dp)
        ) {
            // 用户头像
            Image(
                painter = painterResource(id = getImageResourceId(message.avatarImage)),
                contentDescription = "用户头像",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            // 未读消息数徽章
            if (message.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(20.dp)
                        .background(Color(0xFFFE4E4E), CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (message.unreadCount > 99) "99+" else message.unreadCount.toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            // 在线状态指示器
            if (message.isOnline) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(14.dp)
                        .background(Color(0xFF64E684), CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 消息内容区域
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 用户名和时间行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    maxLines = 1
                )
                
                Text(
                    text = message.time,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF999999)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 消息内容
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.content,
                    fontSize = 14.sp,
                    fontWeight = if (message.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                    color = if (message.unreadCount > 0) Color(0xFF333333) else Color(0xFF999999),
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                
                // 消息类型图标
                if (message.content.contains("[视频通话]") || message.content.contains("[语音通话]")) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFFFE62AC), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (message.content.contains("视频")) "📹" else "📞",
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * 空状态组件
 */
@Composable
private fun EmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📭",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "暂无消息",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF999999)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "开始和朋友们聊天吧",
            fontSize = 14.sp,
            color = Color(0xFFCCCCCC)
        )
    }
}

/**
 * 辅助函数：根据图片名称获取资源ID
 */
private fun getImageResourceId(imageName: String): Int {
    return when (imageName) {
        "group_27" -> com.example.myapplication.R.drawable.group_27
        "group_28" -> com.example.myapplication.R.drawable.group_28
        "group_29" -> com.example.myapplication.R.drawable.group_29
        "group_30" -> com.example.myapplication.R.drawable.group_30
        else -> com.example.myapplication.R.drawable.group_27
    }
}
