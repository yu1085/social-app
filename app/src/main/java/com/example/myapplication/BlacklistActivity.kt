package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.auth.AuthManager
import com.example.myapplication.dto.UserDTO
import com.example.myapplication.network.NetworkConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlacklistActivity : ComponentActivity() {
    private val TAG = "BlacklistActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlacklistScreen(
                onBackClick = { finish() }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BlacklistScreen(
        onBackClick: () -> Unit
    ) {
        var blacklistUsers by remember { mutableStateOf<List<UserDTO>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isSelectionMode by remember { mutableStateOf(false) }
        var selectedUsers by remember { mutableStateOf<Set<Long>>(emptySet()) }
        var showDeleteConfirmDialog by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()

        // 加载黑名单数据
        LaunchedEffect(Unit) {
            loadBlacklistUsers { users, error ->
                isLoading = false
                if (error != null) {
                    errorMessage = error
                } else {
                    blacklistUsers = users
                }
            }
        }

        // 删除确认对话框
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("确认移出黑名单") },
                text = { Text("确定要将${selectedUsers.size}个用户从黑名单中移出吗?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteConfirmDialog = false
                            scope.launch {
                                batchRemoveFromBlacklist(selectedUsers.toList()) { success, error ->
                                    if (success) {
                                        // 重新加载列表
                                        selectedUsers = emptySet()
                                        isSelectionMode = false
                                        scope.launch {
                                            loadBlacklistUsers { users, _ ->
                                                blacklistUsers = users
                                            }
                                        }
                                    } else {
                                        errorMessage = error
                                    }
                                }
                            }
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 顶部导航栏
                TopAppBar(
                    title = {
                        Text(
                            text = if (isSelectionMode) "已选${selectedUsers.size}个" else "黑名单",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isSelectionMode) {
                                isSelectionMode = false
                                selectedUsers = emptySet()
                            } else {
                                onBackClick()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "返回",
                                tint = Color.Black
                            )
                        }
                    },
                    actions = {
                        if (!isSelectionMode && blacklistUsers.isNotEmpty()) {
                            TextButton(onClick = { isSelectionMode = true }) {
                                Text("管理", color = Color(0xFF007AFF))
                            }
                        }
                        if (isSelectionMode) {
                            IconButton(
                                onClick = { showDeleteConfirmDialog = true },
                                enabled = selectedUsers.isNotEmpty()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "删除",
                                    tint = if (selectedUsers.isNotEmpty()) Color.Red else Color.Gray
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )

                // 内容区域
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    errorMessage != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = errorMessage ?: "加载失败",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = {
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch {
                                        loadBlacklistUsers { users, error ->
                                            isLoading = false
                                            if (error != null) {
                                                errorMessage = error
                                            } else {
                                                blacklistUsers = users
                                            }
                                        }
                                    }
                                }) {
                                    Text("重试")
                                }
                            }
                        }
                    }
                    blacklistUsers.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            EmptyStateIcon()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "当前无黑名单用户",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 统计信息卡片
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "当前黑名单人数: ${blacklistUsers.size}",
                                        fontSize = 16.sp,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // 用户列表
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                            ) {
                                items(blacklistUsers) { user ->
                                    BlacklistUserItem(
                                        user = user,
                                        isSelectionMode = isSelectionMode,
                                        isSelected = selectedUsers.contains(user.id),
                                        onSelectionToggle = {
                                            selectedUsers = if (selectedUsers.contains(user.id)) {
                                                selectedUsers - user.id
                                            } else {
                                                selectedUsers + user.id
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun BlacklistUserItem(
        user: UserDTO,
        isSelectionMode: Boolean,
        isSelected: Boolean,
        onSelectionToggle: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(enabled = isSelectionMode) { onSelectionToggle() },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 头像
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.nickname?.firstOrNull()?.toString() ?: "👤",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 用户信息
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = user.nickname ?: "未知用户",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ID: ${user.id}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // 选择框
                if (isSelectionMode) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "已选择",
                            tint = Color(0xFF007AFF),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun EmptyStateIcon() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 第一个头像
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFFE0E0E0),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 20.sp
                )
            }

            // 连接线
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(
                        Color(0xFFE0E0E0),
                        RoundedCornerShape(1.dp)
                    )
            )

            // 第二个头像
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFFE0E0E0),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 20.sp
                )
            }
        }
    }

    private suspend fun loadBlacklistUsers(callback: (List<UserDTO>, String?) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val authManager = AuthManager.getInstance(this@BlacklistActivity)
                val authHeader = authManager.getAuthHeader()

                if (authHeader == null) {
                    withContext(Dispatchers.Main) {
                        callback(emptyList(), "未登录")
                    }
                    return@withContext
                }

                val apiService = NetworkConfig.getApiService()
                val response = apiService.getBlacklistUsers(authHeader).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()
                        if (apiResponse?.isSuccess() == true) {
                            Log.d(TAG, "成功加载黑名单，共${apiResponse.data?.size ?: 0}个用户")
                            callback(apiResponse.data ?: emptyList(), null)
                        } else {
                            Log.e(TAG, "API返回失败: ${apiResponse?.message}")
                            callback(emptyList(), apiResponse?.message ?: "加载失败")
                        }
                    } else {
                        Log.e(TAG, "网络请求失败: ${response.code()}")
                        callback(emptyList(), "网络请求失败")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "加载黑名单失败", e)
                withContext(Dispatchers.Main) {
                    callback(emptyList(), "加载失败: ${e.message}")
                }
            }
        }
    }

    private suspend fun batchRemoveFromBlacklist(userIds: List<Long>, callback: (Boolean, String?) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val authManager = AuthManager.getInstance(this@BlacklistActivity)
                val authHeader = authManager.getAuthHeader()

                if (authHeader == null) {
                    withContext(Dispatchers.Main) {
                        callback(false, "未登录")
                    }
                    return@withContext
                }

                val apiService = NetworkConfig.getApiService()
                val response = apiService.batchRemoveFromBlacklist(authHeader, userIds).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()
                        if (apiResponse?.isSuccess() == true) {
                            Log.d(TAG, "批量移出黑名单成功: ${apiResponse.message}")
                            callback(true, null)
                        } else {
                            Log.e(TAG, "API返回失败: ${apiResponse?.message}")
                            callback(false, apiResponse?.message ?: "操作失败")
                        }
                    } else {
                        Log.e(TAG, "网络请求失败: ${response.code()}")
                        callback(false, "网络请求失败")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "批量移出黑名单失败", e)
                withContext(Dispatchers.Main) {
                    callback(false, "操作失败: ${e.message}")
                }
            }
        }
    }
}
