package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.util.Log
import com.example.myapplication.ui.theme.*
import com.example.myapplication.UserDetailActivity
import com.example.myapplication.ChatActivity
import com.example.myapplication.AcquaintancesActivity
import com.example.myapplication.LikesActivity
import com.example.myapplication.IntimacyActivity
import com.example.myapplication.network.RetrofitClient
import com.example.myapplication.network.ApiService
import com.example.myapplication.dto.UserDTO
import com.example.myapplication.dto.ApiResponse
import com.example.myapplication.dto.MessageDTO
import com.example.myapplication.dto.ConversationDTO
import com.example.myapplication.auth.AuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun MessageScreen(
    onSearchClick: () -> Unit = {}
) {
    // 添加状态管理来跟踪当前选中的标签
    var selectedTab by remember { mutableStateOf(0) } // 0: 消息, 1: 通话, 2: 关系

    // 消息搜索状态
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // 推荐用户状态管理
    var recommendedUsers by remember { mutableStateOf<List<UserDTO>>(emptyList()) }
    var isLoadingUsers by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // 真实会话状态管理
    var realConversations by remember { mutableStateOf<List<ConversationDTO>>(emptyList()) }
    var isLoadingMessages by remember { mutableStateOf(false) }
    var messageRefreshTrigger by remember { mutableStateOf(0) }

    val scope = rememberCoroutineScope()
    val apiService = remember { RetrofitClient.create(ApiService::class.java) }

    // ✅ 注册广播接收器监听新消息，收到后自动刷新会话列表
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(context, lifecycleOwner) {
        val newMessageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (intent?.action == "com.example.myapplication.NEW_MESSAGE") {
                    Log.d("MessageScreen", "收到新消息广播，准备刷新会话列表")
                    // 使用scope.launch在Compose协程作用域中更新状态
                    scope.launch {
                        messageRefreshTrigger++
                    }
                }
            }
        }

        val filter = IntentFilter("com.example.myapplication.NEW_MESSAGE")
        context.registerReceiver(newMessageReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        Log.d("MessageScreen", "已注册新消息广播接收器")

        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // 当页面恢复时也刷新会话列表
                Log.d("MessageScreen", "页面恢复，刷新会话列表")
                // 使用scope.launch在Compose协程作用域中更新状态
                scope.launch {
                    messageRefreshTrigger++
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            context.unregisterReceiver(newMessageReceiver)
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            Log.d("MessageScreen", "已注销新消息广播接收器")
        }
    }

    // 加载推荐用户
    LaunchedEffect(refreshTrigger) {
        isLoadingUsers = true
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.getRecommendedUsers(4).execute()
            }
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                recommendedUsers = response.body()?.data ?: emptyList()
                Log.d("MessageScreen", "成功加载 ${recommendedUsers.size} 个推荐用户")
            } else {
                Log.e("MessageScreen", "加载推荐用户失败: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("MessageScreen", "加载推荐用户异常", e)
        } finally {
            isLoadingUsers = false
        }
    }

    // 加载真实消息
    LaunchedEffect(messageRefreshTrigger) {
        isLoadingMessages = true
        try {
            val authManager = AuthManager.getInstance(context)
            val token = authManager.getToken()

            if (token != null) {
                // ✅ 使用真实的当前登录用户ID
                val currentUserId = authManager.getUserId()
                if (currentUserId != null) {
                    val response = withContext(Dispatchers.IO) {
                        apiService.getConversations(currentUserId).execute()
                    }
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        realConversations = response.body()?.data ?: emptyList()
                        Log.d("MessageScreen", "成功加载 ${realConversations.size} 个会话")
                    } else {
                        Log.e("MessageScreen", "加载会话列表失败: ${response.message()}")
                    }
                } else {
                    Log.e("MessageScreen", "当前用户ID为null，无法加载会话")
                }
            } else {
                Log.e("MessageScreen", "用户未登录，无法加载消息")
            }
        } catch (e: Exception) {
            // 协程取消异常是正常的，表示页面已离开
            if (e.javaClass.simpleName.contains("Cancellation")) {
                Log.d("MessageScreen", "页面已离开，取消消息加载")
            } else {
                Log.e("MessageScreen", "加载真实消息异常", e)
            }
        } finally {
            isLoadingMessages = false
        }
    }

    // 格式化时间（需要先定义，因为convertConversationToMessage会使用它）
    fun formatTime(timeString: String): String {
        return try {
            // 简单的时间格式化，可以根据需要调整
            if (timeString.contains("刚刚") || timeString.contains("Just now")) "刚刚"
            else if (timeString.contains("小时前") || timeString.contains("hours ago")) "1小时前"
            else if (timeString.contains("天前") || timeString.contains("days ago")) "1天前"
            else "刚刚"
        } catch (e: Exception) {
            "刚刚"
        }
    }

    // 将ConversationDTO转换为Message的函数
    fun convertConversationToMessage(conversation: ConversationDTO): Message {
        return Message(
            name = conversation.nickname ?: "Unknown",
            content = conversation.lastMessage ?: "",
            time = formatTime(conversation.lastMessageTime?.toString() ?: ""),
            avatarImage = "group_27", // 使用默认头像
            unreadCount = conversation.unreadCount?.toInt() ?: 0,
            isOnline = conversation.isOnline ?: false,
            userId = conversation.userId  // ✅ 传递对方用户ID
        )
    }

    // 过滤后的消息列表 - 使用真实会话
    val filteredMessages = remember(searchQuery, realConversations) {
        val convertedMessages = realConversations.map { convertConversationToMessage(it) }
        if (searchQuery.isEmpty()) {
            convertedMessages
        } else {
            convertedMessages.filter { message ->
                message.name.contains(searchQuery, ignoreCase = true) ||
                message.content.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部区域
        TopSection(
            onRefreshClick = {
                refreshTrigger++
                messageRefreshTrigger++
            }
        )

        // 推荐用户区域
        RecommendedUsersSection(
            users = recommendedUsers,
            isLoading = isLoadingUsers
        )

        // 标签栏
        TabBarSection(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onSearchClick = onSearchClick
        )

        // 根据选中的标签显示不同的内容
        when (selectedTab) {
            0 -> MessageListSection(filteredMessages = filteredMessages) // 消息页面
            1 -> CallListSection()    // 通话页面
            2 -> RelationshipSection() // 关系页面
        }


    }
}

@Composable
private fun TopSection(onRefreshClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // 标题区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "潜力女神",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFE62AC)
                )
            }

            Text(
                text = "换一批",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5289ED),
                modifier = Modifier.clickable { onRefreshClick() }
            )
        }


    }
}

@Composable
private fun RecommendedUsersSection(
    users: List<UserDTO>,
    isLoading: Boolean
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 推荐用户卡片
        if (isLoading) {
            // 加载中显示占位符
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "...",
                            color = Color(0xFF999999),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else if (users.isEmpty()) {
            // 空状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无",
                            color = Color(0xFF999999),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            // 显示用户卡片（最多4个）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                users.take(4).forEachIndexed { index, user ->
                    RecommendedUserCard(
                        name = user.nickname ?: user.username ?: "用户${user.id}",
                        status = UserStatus.IDLE,
                        iconRes = getAvatarResourceForUser(index),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val intent = Intent(context, UserDetailActivity::class.java)
                            // ✅ 使用API返回的真实user_id和属性（不再硬编码）
                            intent.putExtra("user_id", user.id ?: 0L)
                            intent.putExtra("user_name", user.nickname ?: user.username)
                            intent.putExtra("user_status", user.status ?: "OFFLINE")
                            intent.putExtra("user_age", user.age?.toString() ?: "")
                            intent.putExtra("user_location", user.location ?: "未知")
                            intent.putExtra("user_description", user.signature ?: "这是一个有趣的用户")
                            intent.putExtra("user_avatar", getImageResourceId(getAvatarResourceForUser(index))) // TODO: 后续使用user.avatarUrl
                            context.startActivity(intent)
                        }
                    )
                }
                // 如果用户少于4个，填充空白占位符
                if (users.size < 4) {
                    repeat(4 - users.size) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5F5F5))
                        )
                    }
                }
            }
        }
    }
}

// 辅助函数：为用户分配头像资源
private fun getAvatarResourceForUser(index: Int): String {
    val avatars = listOf("group_27", "group_28", "group_29", "group_30")
    return avatars[index % avatars.size]
}

@Composable
private fun RecommendedUserCard(
    name: String,
    status: UserStatus,
    iconRes: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD9D9D9))
            .clickable { onClick() }
    ) {
        // 显示真实的用户头像图片作为背景
        Image(
            painter = painterResource(id = getImageResourceId(iconRes)),
            contentDescription = "用户头像背景",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        

    }
}

@Composable
private fun TabBarItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) Color(0xFF333333) else Color(0xFF999999),
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
private fun TabBarSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // 标签栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧标签
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TabBarItem(
                    text = "消息",
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) }
                )
                TabBarItem(
                    text = "通话",
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) }
                )
                TabBarItem(
                    text = "关系",
                    isSelected = selectedTab == 2,
                    onClick = { onTabSelected(2) }
                )
            }
            
            // 右侧搜索图标
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF5F5F5))
                    .clickable { onSearchClick() },
                contentAlignment = Alignment.Center
            ) {
                // 搜索图标
                Text(
                    text = "🔍",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun MessageListSection(filteredMessages: List<Message>) {
    val context = LocalContext.current
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // 添加顶部间距
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 消息列表或空状态
        if (filteredMessages.isEmpty()) {
            item {
                // 空状态显示
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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
        } else {
            items(filteredMessages) { message ->
                MessageItem(
                    message = message,
                    onClick = {
                        // 跳转到聊天页面
                        val intent = Intent(context, ChatActivity::class.java).apply {
                            putExtra("user_id", message.userId ?: -1L)  // ✅ 传递对方用户ID
                            putExtra("user_name", message.name)
                            putExtra("user_avatar", message.avatarImage)
                            putExtra("user_status", if (message.isOnline) "在线" else "离线")
                            putExtra("last_message", message.content)
                            putExtra("unread_count", message.unreadCount)
                        }
                        context.startActivity(intent)
                    }
                )
                
                // 添加分割线
                if (message != filteredMessages.last()) {
                    Divider(
                        modifier = Modifier.padding(start = 60.dp, end = 0.dp),
                        color = Color(0xFFF0F0F0),
                        thickness = 0.5.dp
                    )
                }
            }
        }
        
        // 添加底部间距
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CallListSection() {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // 通话记录列表
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(callList) { call ->
                CallRecordCard(
                    call = call,
                    onClick = {
                        // 跳转到用户详情页面
                        val intent = Intent(context, UserDetailActivity::class.java).apply {
                            // ✅ 使用CallItem中的真实user_id（不再硬编码映射）
                            putExtra("user_id", call.userId)
                            putExtra("user_name", call.name)
                            // ✅ 移除硬编码的status、age、location，让UserDetailActivity从API加载
                            putExtra("user_avatar", getImageResourceId(call.avatarImage))
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun CallRecordCard(
    call: CallItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(48.dp)
        ) {
            Image(
                painter = painterResource(id = getImageResourceId(call.avatarImage)),
                contentDescription = "用户头像",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            // 右下角状态指示器
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 3.dp, y = (-1).dp)
                    .size(11.dp)
                    .background(
                        if (call.isMissed) Color(0xFFFE7664) else Color(0xFF64E684),
                        CircleShape
                    )
                    .border(1.5.dp, Color.White, CircleShape)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 通话信息
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Top
        ) {
            // 第一行：用户名
            Text(
                text = call.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // 第二行：通话状态
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Union.png 图标
                Image(
                    painter = painterResource(id = com.example.myapplication.R.drawable.union),
                    contentDescription = "通话类型图标",
                    modifier = Modifier.size(9.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = call.callStatus,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF999999),
                    maxLines = 1
                )
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // 第三行：Frame 14.png 价格图标 + 价格文字
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = com.example.myapplication.R.drawable.frame_14),
                    contentDescription = "价格图标",
                    modifier = Modifier.size(11.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = if (call.isMissed) "免费1分钟" else "100/分钟",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (call.isMissed) Color(0xFF71B989) else Color(0xFF999999)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 右侧信息
        Column(
            horizontalAlignment = Alignment.End
        ) {
            // 时间 + Frame 7.png 通话状态图标
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = call.time,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF999999),
                    textAlign = TextAlign.End
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Image(
                    painter = painterResource(id = com.example.myapplication.R.drawable.frame_7),
                    contentDescription = "通话状态图标",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun RelationshipSection() {
    val context = LocalContext.current
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        item {
            // 图标与上方标签区域的间距
            Spacer(modifier = Modifier.height(24.dp))
            
            // 标题图标和副标题 - 居中显示，完全挨着
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = com.example.myapplication.R.drawable.group_119),
                    contentDescription = "关系图标",
                    modifier = Modifier
                        .width(120.dp)
                        .height(40.dp)
                )
                
                Text(
                    text = "记录你在知聊时光里的温暖邂逅",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF999999),
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center
                )
            }
            
            // 图标与下方关系卡片区域之间的间距
            Spacer(modifier = Modifier.height(25.dp))
            
            // 关系卡片区域 - 左右布局
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 左侧：知友卡片 - 协调高度
                RelationshipCard(
                    title = "知友",
                    subtitle = "交友知心，畅聊互动",
                    backgroundColor = Color(0xFFF6EAFE),
                    onClick = {
                        // 跳转到知友界面
                        val intent = Intent(context, AcquaintancesActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    height = 410.dp
                )
                
                // 右侧：喜欢和亲密卡片 - 协调高度
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 喜欢卡片
                    RelationshipCard(
                        title = "喜欢",
                        subtitle = "你曾对ta一见钟情",
                        backgroundColor = Color(0xFFEAF2FF),
                        onClick = {
                            // 跳转到喜欢界面
                            val intent = Intent(context, LikesActivity::class.java)
                            context.startActivity(intent)
                        },
                        height = 197.dp
                    )
                    
                    // 亲密卡片
                    RelationshipCard(
                        title = "亲密",
                        subtitle = "看看谁聊的最频繁",
                        backgroundColor = Color(0xFFF6FFEA),
                        onClick = {
                            // 跳转到亲密界面
                            val intent = Intent(context, IntimacyActivity::class.java)
                            context.startActivity(intent)
                        },
                        height = 197.dp
                    )
                }
            }
            
            // 关系卡片区域与最底下片区域的间距
            Spacer(modifier = Modifier.height(10.dp))
        }
        
        // 添加额外的底部空间，让滑动更明显
        item {
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
private fun RelationshipCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 120.dp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clickable { onClick() }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(42.dp),
                spotColor = Color(0x1A000000)
            ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(42.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // 上方留白，让内容再往上一点
            Spacer(modifier = Modifier.weight(0.25f))
            
            // 标题 - 偏左上角
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000),
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 副标题 - 偏左上角
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF999999),
                modifier = Modifier.align(Alignment.Start)
            )
            
            // 弹性空间，使查看按钮位于卡片中间位置
            Spacer(modifier = Modifier.weight(0.6f))
            
            // 查看按钮 - 位于知友卡片的中间位置，左对齐
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .align(Alignment.Start)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color(0x1A000000)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "查看",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF000000)
                    )
                }
            }
            
            // 底部弹性空间，保持按钮在中间位置
            Spacer(modifier = Modifier.weight(1.4f))
        }
    }
}

@Composable
private fun MessageItem(
    message: Message,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像容器
        Box(
            modifier = Modifier
                .size(48.dp)
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

@Composable
private fun RelationshipItemRow(relationship: RelationshipItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            // 使用真实的用户头像图片
            Image(
                painter = painterResource(id = getImageResourceId(relationship.avatarImage)),
                contentDescription = "用户头像",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // 关系状态徽章
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .background(
                        when (relationship.status) {
                            RelationshipStatus.FRIEND -> Color(0xFF64E684)
                            RelationshipStatus.STRANGER -> Color(0xFF999999)
                            RelationshipStatus.REQUEST_SENT -> Color(0xFFFE62AC)
                            RelationshipStatus.REQUEST_RECEIVED -> Color(0xFFFE62AC)
                        },
                        CircleShape
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (relationship.status) {
                        RelationshipStatus.FRIEND -> "友"
                        RelationshipStatus.STRANGER -> "陌"
                        RelationshipStatus.REQUEST_SENT -> "发"
                        RelationshipStatus.REQUEST_RECEIVED -> "收"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 消息内容
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = relationship.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = when (relationship.status) {
                    RelationshipStatus.FRIEND -> "你们是好友"
                    RelationshipStatus.STRANGER -> "你们是陌生人"
                    RelationshipStatus.REQUEST_SENT -> "你已发送好友请求"
                    RelationshipStatus.REQUEST_RECEIVED -> "你收到好友请求"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF999999)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 时间
        Text(
            text = relationship.time,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF999999),
            textAlign = TextAlign.End
        )
    }
}


// 数据模型
enum class UserStatus {
    IDLE, BUSY
}

data class Message(
    val name: String,
    val content: String,
    val time: String,
    val avatarImage: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val userId: Long? = null  // 对方用户ID
)

data class CallItem(
    val name: String,
    val time: String,
    val avatarImage: String,
    val isMissed: Boolean = false,
    val callStatus: String = "已取消通话", // 通话状态：已取消通话、通话时长等
    val userId: Long = 0L // 用户ID，默认0表示无效
)

data class RelationshipItem(
    val name: String,
    val status: RelationshipStatus,
    val avatarImage: String,
    val time: String = "刚刚"
)

enum class RelationshipStatus {
    FRIEND, STRANGER, REQUEST_SENT, REQUEST_RECEIVED
}

// 模拟数据
private val messageList = listOf(
    Message(
        name = "你的小可爱512",
        content = "[视频通话]",
        time = "刚刚",
        avatarImage = "group_27",
        isOnline = true
    ),
    Message(
        name = "漫步的美人鱼",
        content = "晚上好呀，在干嘛?",
        time = "12小时前",
        avatarImage = "group_28",
        unreadCount = 1
    ),
    Message(
        name = "谁来看过我",
        content = "有个小姐姐看了你，对你很感兴趣，点击看...",
        time = "12小时前",
        avatarImage = "group_29",
        unreadCount = 1
    ),
    Message(
        name = "提醒",
        content = "给您赠送的免费私信马上要过期了哦，还...",
        time = "12小时前",
        avatarImage = "group_30",
        unreadCount = 4
    ),
    Message(
        name = "小雅",
        content = "今天天气真好，要不要一起出去走走？",
        time = "1小时前",
        avatarImage = "group_27",
        unreadCount = 2
    ),
    Message(
        name = "小雨",
        content = "好的，那我们明天见！",
        time = "2小时前",
        avatarImage = "group_28",
        unreadCount = 0
    ),
    Message(
        name = "小美",
        content = "[语音通话]",
        time = "3小时前",
        avatarImage = "group_29",
        unreadCount = 0
    ),
    Message(
        name = "小琳",
        content = "谢谢你的礼物，我很喜欢！",
        time = "5小时前",
        avatarImage = "group_30",
        unreadCount = 1
    ),
    Message(
        name = "甜心宝贝",
        content = "最近工作忙吗？",
        time = "1天前",
        avatarImage = "group_27",
        unreadCount = 0
    ),
    Message(
        name = "不吃香菜",
        content = "周末有空一起看电影吗？",
        time = "2天前",
        avatarImage = "group_28",
        unreadCount = 3
    ),
    Message(
        name = "你的菜",
        content = "今天心情不错，想和你分享",
        time = "3天前",
        avatarImage = "group_29",
        unreadCount = 0
    ),
    Message(
        name = "小仙女",
        content = "晚安，做个好梦",
        time = "3天前",
        avatarImage = "group_30",
        unreadCount = 0
    )
)

// 通话列表数据
private val callList = listOf(
    CallItem(
        name = "你的小可爱512",
        time = "刚刚",
        avatarImage = "group_27",
        isMissed = false,
        callStatus = "00:00:36",
        userId = 23820512L
    ),
    CallItem(
        name = "漫步的美人鱼",
        time = "12小时前",
        avatarImage = "group_28",
        isMissed = true,
        callStatus = "已取消通话",
        userId = 23820513L
    ),
    CallItem(
        name = "小雅",
        time = "1天前",
        avatarImage = "group_29",
        isMissed = false,
        callStatus = "00:01:24",
        userId = 23820516L
    ),
    CallItem(
        name = "小雨",
        time = "2天前",
        avatarImage = "group_30",
        isMissed = false,
        callStatus = "00:00:18",
        userId = 23820517L
    ),
    CallItem(
        name = "小美",
        time = "3天前",
        avatarImage = "group_27",
        isMissed = true,
        callStatus = "已取消通话",
        userId = 23820518L
    )
)

// 辅助函数：根据图片名称获取资源ID
private fun getImageResourceId(imageName: String): Int {
    return when (imageName) {
        "group_27" -> com.example.myapplication.R.drawable.group_27
        "group_28" -> com.example.myapplication.R.drawable.group_28
        "group_29" -> com.example.myapplication.R.drawable.group_29
        "group_30" -> com.example.myapplication.R.drawable.group_30
        else -> com.example.myapplication.R.drawable.group_27
    }
}
