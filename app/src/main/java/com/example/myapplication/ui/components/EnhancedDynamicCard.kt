package com.example.myapplication.ui.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.DynamicDetailActivity
import com.example.myapplication.R
import com.example.myapplication.dto.EnhancedPostDTO

/**
 * 增强的动态卡片组件
 */
@Composable
fun EnhancedDynamicCard(
    post: EnhancedPostDTO,
    onLikeClick: (Long) -> Unit,
    onUserClick: (Long) -> Unit,
    onCommentClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                // 点击卡片跳转到动态详情页面
                val intent = Intent(context, DynamicDetailActivity::class.java)
                intent.putExtra("post_id", post.id)
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 用户信息头部
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // 头像
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable { post.userId?.let { onUserClick(it) } }
                ) {
                    AsyncImage(
                        model = post.userAvatar ?: R.drawable.profile_avatar,
                        contentDescription = "用户头像",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.profile_avatar)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // 用户信息
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = post.userName ?: "未知用户",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // 状态标签
                            StatusTag(status = post.userStatus ?: "离线")
                        }
                        
                        // 右上角喜欢按钮
                        Box(
                            modifier = Modifier
                                .border(
                                    0.5.dp,
                                    Color(0xFFA2C3FF),
                                    RoundedCornerShape(15.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "喜欢",
                                    tint = Color(0xFFA2C3FF),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "喜欢",
                                    fontSize = 12.sp,
                                    color = Color(0xFFA2C3FF)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 年龄和距离信息
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 年龄
                        Text(
                            text = "${post.userAge ?: 0}岁",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // 距离
                        Text(
                            text = "${String.format("%.2f", post.distance ?: 0.0)}km",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 动态内容
            post.content?.let { content ->
                Text(
                    text = content,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                )
            }
            
            // 图片内容
            if (!post.imageUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "动态图片",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.dynamic_image)
                )
            }
            
            // 发布时间和位置信息行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：发布时间和位置
                Text(
                    text = "${post.publishTimeText ?: "刚刚"} 发布于${post.location ?: "未知位置"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // 右侧：免费1分钟文字
                if (post.isFreeMinute == true) {
                    Text(
                        text = "免费1分钟",
                        fontSize = 12.sp,
                        color = Color(0xFF71B989),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 点赞和留言按钮行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：点赞和留言按钮
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 点赞按钮
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { post.id?.let { onLikeClick(it) } }
                    ) {
                        Icon(
                            imageVector = if (post.isLiked == true) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "点赞",
                            tint = if (post.isLiked == true) Color(0xFFA2C3FF) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = post.likeCount.toString(),
                            fontSize = 14.sp,
                            color = if (post.isLiked == true) Color(0xFFA2C3FF) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(24.dp))
                    
                    // 留言按钮
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { post.id?.let { onCommentClick(it) } }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Comment,
                            contentDescription = "留言",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = post.commentCount.toString(),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // 右侧：视频图标
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "视频",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * 状态标签组件
 */
@Composable
fun StatusTag(
    status: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        "在线" -> Color(0xFF4CAF50)
        "忙碌" -> Color(0xFFFF9800)
        "离线" -> Color(0xFF9E9E9E)
        else -> Color(0xFF9E9E9E)
    }
    
    val textColor = Color.White
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(13.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}
