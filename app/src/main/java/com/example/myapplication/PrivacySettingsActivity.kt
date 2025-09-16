package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PrivacySettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrivacySettingsScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    onBackClick: () -> Unit
) {
    // 隐私设置状态
    var shareCityChanges by remember { mutableStateOf(false) }
    var hideVipBadge by remember { mutableStateOf(false) }
    var hideWealthLevel by remember { mutableStateOf(false) }
    var hideOnlineStatus by remember { mutableStateOf(false) }
    var hideLocation by remember { mutableStateOf(false) }

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
                        text = "隐私设置",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
            
            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 分享所在城市变化
                PrivacySettingItem(
                    title = "分享所在城市变化",
                    description = "开启后,当您更换所在城市,系统将自动通知您的知友/喜欢的人",
                    isChecked = shareCityChanges,
                    onCheckedChange = { shareCityChanges = it }
                )
                
                // 隐藏 VIP标识
                PrivacySettingItem(
                    title = "隐藏 VIP标识",
                    description = "开启后,您的会员标识将不对其他人展示",
                    isChecked = hideVipBadge,
                    onCheckedChange = { hideVipBadge = it }
                )
                
                // 隐藏财富等级标识
                PrivacySettingItem(
                    title = "隐藏财富等级标识",
                    description = "开启后,您的财富等级将不对其他人显示",
                    isChecked = hideWealthLevel,
                    onCheckedChange = { hideWealthLevel = it }
                )
                
                // 分割线
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFF0F0F0)
                )
                
                // 隐藏在线状态 (会员专享)
                PrivacySettingItem(
                    title = "隐藏在线状态",
                    description = "开启后,即可隐藏自己的在线状态",
                    isChecked = hideOnlineStatus,
                    onCheckedChange = { hideOnlineStatus = it },
                    isVipExclusive = true
                )
                
                // 隐藏定位 (会员专享)
                PrivacySettingItem(
                    title = "隐藏定位",
                    description = "开启后,不对外显示定位信息,不在附近同城推荐中展示",
                    isChecked = hideLocation,
                    onCheckedChange = { hideLocation = it },
                    isVipExclusive = true
                )
            }
        }
    }
}

@Composable
fun PrivacySettingItem(
    title: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isVipExclusive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 标题和VIP标识
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                if (isVipExclusive) {
                    Spacer(modifier = Modifier.width(8.dp))
                    VipExclusiveBadge()
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 描述文字
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 开关
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF007AFF),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }
}

@Composable
fun VipExclusiveBadge() {
    Box(
        modifier = Modifier
            .background(
                Color(0xFFFF9500),
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "V",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "会员专享",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}
