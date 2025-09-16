package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.ui.screens.SettingsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * 设置页面Activity
 */
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                SettingsActivityContent(
                    onBackClick = {
                        // 关闭当前Activity，返回上一页
                        finish()
                    },
                    onSettingItemClick = { settingItem ->
                        // 处理设置项点击事件
                        handleSettingItemClick(settingItem)
                    }
                )
            }
        }
    }
    
    /**
     * 处理设置项点击事件
     */
    private fun handleSettingItemClick(settingItem: String) {
        when (settingItem) {
            "账号相关" -> {
                // 跳转到账号相关页面
                val intent = android.content.Intent(this, AccountManagementActivity::class.java)
                startActivity(intent)
            }
            "黑名单" -> {
                // 跳转到黑名单页面
                val intent = android.content.Intent(this, BlacklistActivity::class.java)
                startActivity(intent)
            }
            "隐私设置" -> {
                // 跳转到隐私设置页面
                val intent = android.content.Intent(this, PrivacySettingsActivity::class.java)
                startActivity(intent)
            }
            "通知设置" -> {
                // 跳转到通知设置页面
                val intent = android.content.Intent(this, NotificationSettingsActivity::class.java)
                startActivity(intent)
            }
            "系统权限设置" -> {
                // 跳转到系统权限设置页面
                val intent = android.content.Intent(this, SystemPermissionActivity::class.java)
                startActivity(intent)
            }
            "视频帧率" -> {
                // 跳转到视频帧率设置页面
                val intent = android.content.Intent(this, VideoFramerateActivity::class.java)
                startActivity(intent)
            }
            "美颜设置" -> {
                // 处理美颜设置
                showToast("点击了美颜设置")
            }
            "清空聊天记录" -> {
                // 处理清空聊天记录
                showToast("点击了清空聊天记录")
            }
            "清除缓存" -> {
                // 处理清除缓存
                showToast("点击了清除缓存")
            }
            "个人信息浏览与导出" -> {
                // 跳转到个人信息浏览导出页面
                val intent = android.content.Intent(this, PersonalInfoBrowseActivity::class.java)
                startActivity(intent)
            }
            "收集个人信息清单" -> {
                // 跳转到收集个人信息清单页面
                val intent = android.content.Intent(this, PersonalInfoCollectionActivity::class.java)
                startActivity(intent)
            }
            "与第三方共享信息清单" -> {
                // 跳转到与第三方共享信息清单页面
                val intent = android.content.Intent(this, ThirdPartySharingActivity::class.java)
                startActivity(intent)
            }
            "青少年模式" -> {
                // 跳转到青少年模式页面
                val intent = android.content.Intent(this, YouthModeActivity::class.java)
                startActivity(intent)
            }
            "关于知聊" -> {
                // 跳转到关于知聊页面
                val intent = android.content.Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
            "建议" -> {
                // 跳转到建议反馈页面
                val intent = android.content.Intent(this, SuggestionActivity::class.java)
                startActivity(intent)
            }
            "举报" -> {
                // 跳转到举报页面
                val intent = android.content.Intent(this, ReportActivity::class.java)
                startActivity(intent)
            }
            "退出当前账号" -> {
                // 处理退出当前账号
                showToast("点击了退出当前账号")
            }
        }
    }
    
    /**
     * 显示Toast消息
     */
    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}

/**
 * 设置页面内容
 */
@Composable
fun SettingsActivityContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSettingItemClick: (String) -> Unit = {}
) {
    var showClearChatDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SettingsScreen(
            onBackClick = onBackClick,
            onSettingItemClick = { settingItem ->
                when (settingItem) {
                    "清空聊天记录" -> {
                        showClearChatDialog = true
                    }
                    "清除缓存" -> {
                        showClearCacheDialog = true
                    }
                    else -> {
                        onSettingItemClick(settingItem)
                    }
                }
            }
        )
        
        // 清空聊天记录确认弹窗
        if (showClearChatDialog) {
            ClearChatDialog(
                onDismiss = { showClearChatDialog = false },
                onConfirm = {
                    showClearChatDialog = false
                    onSettingItemClick("清空聊天记录")
                }
            )
        }
        
        // 清除缓存确认弹窗
        if (showClearCacheDialog) {
            ClearCacheDialog(
                onDismiss = { showClearCacheDialog = false },
                onConfirm = {
                    showClearCacheDialog = false
                    onSettingItemClick("清除缓存")
                }
            )
        }
    }
}



/**
 * 清空聊天记录确认弹窗
 */
@Composable
fun ClearChatDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                Text(
                    text = "确定清除所有聊天记录?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // 按钮区域
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 取消按钮
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Text(
                            text = "取消",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                    
                    // 清除记录按钮
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF)
                        )
                    ) {
                        Text(
                            text = "清除记录",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * 清除缓存确认弹窗
 */
@Composable
fun ClearCacheDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                Text(
                    text = "确定清除本地缓存?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // 按钮区域
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 取消按钮
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Text(
                            text = "取消",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    }
                    
                    // 确定按钮
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF)
                        )
                    ) {
                        Text(
                            text = "确定",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * 预览
 */
@Preview(showBackground = true)
@Composable
fun SettingsActivityContentPreview() {
    MyApplicationTheme {
        SettingsActivityContent()
    }
}
