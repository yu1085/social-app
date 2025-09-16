package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 设置页面 - 完全按照设计稿实现
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSettingItemClick: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部标题栏
        SettingsTopBar(
            onBackClick = onBackClick
        )
        
        // 设置内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 账号相关设置
            SettingsSection(
                title = null,
                items = listOf(
                    SettingsItem(
                        title = "账号相关",
                        icon = Icons.Default.AccountCircle,
                        showArrow = true
                    ),
                    SettingsItem(
                        title = "黑名单",
                        icon = Icons.Default.Block,
                        showArrow = true
                    )
                ),
                onItemClick = onSettingItemClick
            )
            
            // 隐私和通知设置
            SettingsSection(
                title = null,
                items = listOf(
                    SettingsItem(
                        title = "隐私设置",
                        icon = Icons.Default.PrivacyTip,
                        showArrow = true
                    ),
                    SettingsItem(
                        title = "通知设置",
                        icon = Icons.Default.Notifications,
                        showArrow = true
                    ),
                    SettingsItem(
                        title = "系统权限设置",
                        icon = Icons.Default.Security,
                        subtitle = "授权使用的手机权限",
                        showArrow = true
                    )
                ),
                onItemClick = onSettingItemClick
            )
            
            // 通用设置
            SettingsSection(
                title = null,
                items = listOf(
                    SettingsItem(
                        title = "视频帧率",
                        icon = Icons.Default.VideoSettings,
                        showArrow = true
                    ),
                    SettingsItem(
                        title = "美颜设置",
                        icon = Icons.Default.Face,
                        showArrow = true
                    )
                ),
                onItemClick = onSettingItemClick
            )
            
            // 数据管理
            SettingsSection(
                title = null,
                items = listOf(
                    SettingsItem(
                        title = "清空聊天记录",
                        icon = Icons.Default.Delete,
                        showArrow = true
                    ),
                    SettingsItem(
                        title = "清除缓存",
                        icon = Icons.Default.Storage,
                        subtitle = "362.69MB",
                        showArrow = true
                    )
                ),
                onItemClick = onSettingItemClick
            )
            
            // 隐私和信息
            SettingsSection(
                title = null,
                items = listOf(
                    SettingsItem(
                        title = "个人信息浏览与导出",
                        icon = Icons.Default.Person,
                        showArrow = true
                    ),
                    SettingsItem(
                        title = "收集个人信息清单",
                        icon = Icons.Default.List,
                        showArrow = true
                    ),
                    SettingsItem(
                        title = "与第三方共享信息清单",
                        icon = Icons.Default.Share,
                        showArrow = true
                    )
                ),
                onItemClick = onSettingItemClick
            )
            
            // 应用相关设置
            SettingsSection(
                title = null,
                items = listOf(
                    SettingsItem(
                        title = "青少年模式",
                        icon = Icons.Default.ChildCare,
                        showArrow = true
                    ),
                    SettingsItem(
                        title = "关于知聊",
                        icon = Icons.Default.Info,
                        showArrow = true
                    ),
                    SettingsItem(
                        title = "建议",
                        icon = Icons.Default.Feedback,
                        showArrow = true
                    ),
                    SettingsItem(
                        title = "举报",
                        icon = Icons.Default.Report,
                        showArrow = true
                    )
                ),
                onItemClick = onSettingItemClick
            )
            
            // 账号管理
            SettingsSection(
                title = null,
                items = listOf(
                    SettingsItem(
                        title = "退出当前账号",
                        icon = Icons.Default.ExitToApp,
                        showArrow = true,
                        textColor = Color(0xFFFF4444) // 红色文字
                    )
                ),
                onItemClick = onSettingItemClick
            )
            
            // 底部间距
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * 设置页面顶部标题栏
 */
@Composable
private fun SettingsTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回按钮
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color(0xFF333333),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 标题
        Text(
            text = "设置",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )
    }
}

/**
 * 设置项数据类
 */
data class SettingsItem(
    val title: String,
    val icon: ImageVector,
    val subtitle: String? = null,
    val showArrow: Boolean = false,
    val textColor: Color = Color(0xFF333333)
)

/**
 * 设置分组组件
 */
@Composable
private fun SettingsSection(
    title: String?,
    items: List<SettingsItem>,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 分组标题（如果有）
        title?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // 设置项列表
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {
            items.forEachIndexed { index, item ->
                SettingsItemRow(
                    item = item,
                    onClick = { onItemClick(item.title) },
                    showDivider = index < items.lastIndex
                )
            }
        }
        
        // 分组间距
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * 单个设置项行
 */
@Composable
private fun SettingsItemRow(
    item: SettingsItem,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color(0xFF666666),
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 标题和副标题
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = item.textColor
                )
                
                item.subtitle?.let { subtitle ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
            
            // 箭头
            if (item.showArrow) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "更多",
                    tint = Color(0xFFCCCCCC),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // 分割线
        if (showDivider) {
            Divider(
                color = Color(0xFFF0F0F0),
                thickness = 0.5.dp,
                modifier = Modifier.padding(start = 48.dp)
            )
        }
    }
}
