package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * 动态详情页面Activity
 * 1:1复刻Figma设计: 心聊临摹-广场动态详情
 */
class DynamicDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                DynamicDetailScreen()
            }
        }
    }
}

@Composable
fun DynamicDetailScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. 状态栏
        StatusBar()
        
        // 2. 主内容区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column {
                // 动态详情标题
                DynamicDetailHeader()
                
                // 用户信息区域
                UserInfoSection()
                
                // 动态内容区域
                DynamicContentSection()
                
                // 互动信息区域
                InteractionSection()
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 底部输入框
                BottomInputSection()
            }
        }
        
        // 3. Home键指示器
        HomeIndicator()
    }
}

@Composable
fun StatusBar() {
    // 状态栏 - 透明背景，高度160px (约53.3dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(53.dp)
            .background(Color.Transparent)
    ) {
        // 这里可以添加状态栏内容，如电池、WiFi等图标
        // 根据设计，状态栏保持简洁
    }
}

@Composable
fun DynamicDetailHeader() {
    // 动态详情标题区域 - 调整到更靠近顶部
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp), // 减少垂直padding，让标题更靠近顶部
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "动态详情",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
        
        // 右上角三个点
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                )
            }
        }
    }
}

@Composable
fun UserInfoSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 用户头像
        Image(
            painter = painterResource(id = R.drawable.profile_avatar),
            contentDescription = "用户头像",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            // 用户名和年龄标签行
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "小倩",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 年龄标签 - 放在名字右侧
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            Color(0xFFFBADBA), // 粉色背景
                            RoundedCornerShape(11.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    // 年龄图标
                    Image(
                        painter = painterResource(id = R.drawable.age_icon),
                        contentDescription = "年龄",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "27",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 距离信息
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 地点图标
                Image(
                    painter = painterResource(id = R.drawable.location_icon),
                    contentDescription = "地点",
                    modifier = Modifier.size(12.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFDADADA))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "6.34km",
                    fontSize = 12.sp,
                    color = Color(0xFFDADADA)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 喜欢按钮
        Box(
            modifier = Modifier
                .border(
                    0.5.dp,
                    Color(0xFFA2C3FF),
                    RoundedCornerShape(15.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 喜欢图标 - 使用Material Design心形图标
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "喜欢",
                    tint = Color(0xFFA2C3FF),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "喜欢",
                    fontSize = 13.sp, // 40px -> 约13sp
                    color = Color(0xFFA2C3FF)
                )
            }
        }
    }
}

@Composable
fun DynamicContentSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // 动态标题
        Text(
            text = "减肥餐",
            fontSize = 16.sp, // 48px -> 约16sp
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 动态图片
        Image(
            painter = painterResource(id = R.drawable.dynamic_image), // 使用现有资源
            contentDescription = "动态图片",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(456f / 459f) // 根据设计稿比例
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun InteractionSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 时间和地点信息
        Text(
            text = "08-12 11:57 安徽省",
            fontSize = 12.sp,
            color = Color(0xFFDADADA)
        )
    }
}

@Composable
fun BottomInputSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .background(
                Color(0xFFF5F5F5),
                RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Text(
            text = "请输入内容",
            fontSize = 17.sp, // 52px -> 约17sp
            color = Color(0xFFB6B7B9)
        )
    }
}

@Composable
fun HomeIndicator() {
    // Home键指示器
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp) // 136px -> 约45dp
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(180.dp) // 540px -> 约180dp
                .height(7.dp) // 20px -> 约7dp
                .background(
                    Color.Black,
                    RoundedCornerShape(3.5.dp)
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DynamicDetailScreenPreview() {
    MyApplicationTheme {
        DynamicDetailScreen()
    }
}
