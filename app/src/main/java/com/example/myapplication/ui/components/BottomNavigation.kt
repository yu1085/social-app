package com.example.myapplication.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

/**
 * 底部导航栏组件
 */
@Composable
fun BottomNavigation(
    currentRoute: String,
    onRouteSelected: (String) -> Unit,
    notificationCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 首页
            BottomNavItem(
                title = stringResource(R.string.nav_home),
                icon = Icons.Default.Home,
                isSelected = currentRoute == "home",
                onClick = { onRouteSelected("home") }
            )
            
            // 广场
            BottomNavItem(
                title = stringResource(R.string.nav_square),
                icon = Icons.Default.Explore,
                isSelected = currentRoute == "square",
                onClick = { onRouteSelected("square") }
            )
            
            // 私信（带通知数量）
            Box {
                BottomNavItem(
                    title = stringResource(R.string.nav_message),
                    icon = Icons.Default.Message,
                    isSelected = currentRoute == "message",
                    onClick = { onRouteSelected("message") }
                )
                
                // 通知徽章
                if (notificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFB6570)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (notificationCount > 99) "99+" else notificationCount.toString(),
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // 我的
            BottomNavItem(
                title = stringResource(R.string.nav_profile),
                icon = Icons.Default.Person,
                isSelected = currentRoute == "profile",
                onClick = { onRouteSelected("profile") }
            )
        }
    }
}

/**
 * 底部导航项组件
 */
@Composable
private fun BottomNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 使用动画颜色变化
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            Color(0xFF1976D2) // 选中时的蓝色
        } else {
            Color(0xFF757575) // 未选中时的灰色
        },
        animationSpec = tween(durationMillis = 200),
        label = "textColor"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            Color(0xFF1976D2) // 选中时的蓝色
        } else {
            Color(0xFF757575) // 未选中时的灰色
        },
        animationSpec = tween(durationMillis = 200),
        label = "iconColor"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            Color(0xFFE3F2FD) // 选中时的淡蓝色背景
        } else {
            Color.Transparent // 未选中时透明
        },
        animationSpec = tween(durationMillis = 200),
        label = "backgroundColor"
    )
    
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = backgroundColor)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = iconColor
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = title,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
