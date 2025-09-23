package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.AlbumViewModel
import com.example.myapplication.auth.AuthManager
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 个人资料详情页面
 * 根据设计稿实现，包含身份认证、动态日常、关于我、我的礼物等功能
 */
class ProfileDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                ProfileDetailScreen(
                    onBackClick = { finish() },
                    onMenuClick = { showMenuOptions() },
                    onEditProfileClick = { 
                        val intent = Intent(this, MyProfileEditActivity::class.java)
                        startActivity(intent)
                    },
                    onPublishDynamicClick = {
                        Toast.makeText(this, "跳转到发布动态页面", Toast.LENGTH_SHORT).show()
                    },
                    onEditAlbumClick = {
                        val intent = Intent(this, EditAlbumActivity::class.java)
                        startActivity(intent)
                    },
                    onRealPersonAuthClick = {
                        val intent = Intent(this, RealPersonAuthActivity::class.java)
                        startActivity(intent)
                    },
                    onIdCardVerifyClick = {
                        val intent = Intent(this, IdCardVerifyActivity::class.java)
                        startActivity(intent)
                    },
                    onPhoneAuthClick = {
                        val intent = Intent(this, PhoneIdentityAuthActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
    
    private fun showMenuOptions() {
        Toast.makeText(this, "显示更多选项", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ProfileDetailScreen(
    onBackClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onPublishDynamicClick: () -> Unit = {},
    onEditAlbumClick: () -> Unit = {},
    onRealPersonAuthClick: () -> Unit = {},
    onIdCardVerifyClick: () -> Unit = {},
    onPhoneAuthClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val albumViewModel = remember { AlbumViewModel(context) }
    val authManager = remember { AuthManager.getInstance(context) }
    
    // 获取用户信息
    val userId = authManager.getUserId()
    val userToken = authManager.getToken()
    val isLoggedIn = authManager.isLoggedIn()
    
    // 加载相册数据
    LaunchedEffect(Unit) {
        if (isLoggedIn) {
            albumViewModel.loadUserPhotos()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部状态栏和导航栏
        TopNavigationBar(
            onBackClick = onBackClick,
            onMenuClick = onMenuClick
        )
        
        // 主要内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 个人资料图片区域 - 相册功能
            ProfileImageSection(
                onEditAlbumClick = onEditAlbumClick,
                albumViewModel = albumViewModel
            )
            
            // 用户状态
            UserStatusSection()
            
            // 用户名/ID
            UserNameSection(userId = userId)
            
            // 用户属性标签
            UserAttributesSection()
            
            // 身份认证区域
            IdentityVerificationSection(
                onRealPersonAuthClick = onRealPersonAuthClick,
                onIdCardVerifyClick = onIdCardVerifyClick,
                onPhoneAuthClick = onPhoneAuthClick
            )
            
            // 动态日常区域
            DynamicDailySection(
                onPublishDynamicClick = onPublishDynamicClick
            )
            
            // 关于我区域
            AboutMeSection()
            
            // 我的礼物区域
            MyGiftsSection()
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 编辑资料按钮
            EditProfileButton(
                onEditProfileClick = onEditProfileClick
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun TopNavigationBar(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
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
                tint = Color.Black
            )
        }
        
        // 页面标题
        Text(
            text = "相约未来 605",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        // 更多选项按钮
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多选项",
                tint = Color.Black
            )
        }
    }
}

@Composable
private fun ProfileImageSection(
    onEditAlbumClick: () -> Unit,
    albumViewModel: AlbumViewModel
) {
    val photos by albumViewModel.photos.collectAsState()
    val avatarPhoto = photos.firstOrNull { it.isAvatar }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFF5F5F5))
            .clickable { onEditAlbumClick() }
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        if (avatarPhoto != null) {
            // 显示头像
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White, CircleShape)
                    .border(3.dp, Color.White, CircleShape)
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_avatar_custom),
                    contentDescription = "头像",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // 编辑相册按钮
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(Color(0xFF2196F3), CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑相册",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }
        } else {
            // 默认添加照片按钮
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, Color(0xFFE0E0E0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加照片",
                    tint = Color(0xFF999999),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun UserStatusSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // 状态标签
        Box(
            modifier = Modifier
                .background(
                    Color(0xFFF0F0F0),
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 绿色状态指示器
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "空闲",
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}

@Composable
private fun UserNameSection(
    userId: Long = -1L
) {
    val displayName = if (userId != -1L) {
        "用户 $userId"
    } else {
        "相约未来605" // 默认显示
    }
    
    Text(
        text = displayName,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}

@Composable
private fun UserAttributesSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 黄金会员标签
        AttributeTag(
            text = "黄金",
            icon = R.drawable.ic_star,
            backgroundColor = Color(0xFFFFD700)
        )
        
        // 年龄标签
        AttributeTag(
            text = "35",
            icon = R.drawable.ic_male,
            backgroundColor = Color(0xFFE3F2FD)
        )
        
        // 位置标签
        AttributeTag(
            text = "南京",
            icon = R.drawable.ic_location,
            backgroundColor = Color(0xFFE8F5E8)
        )
    }
}

@Composable
private fun AttributeTag(
    text: String,
    icon: Int,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF666666)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
private fun IdentityVerificationSection(
    onRealPersonAuthClick: () -> Unit = {},
    onIdCardVerifyClick: () -> Unit = {},
    onPhoneAuthClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "身份认证",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            VerificationItem(
                title = "真人认证",
                icon = R.drawable.ic_person,
                isVerified = true,
                onItemClick = onRealPersonAuthClick
            )
            
            VerificationItem(
                title = "实名认证",
                icon = R.drawable.ic_verified_user,
                isVerified = true,
                onItemClick = onIdCardVerifyClick
            )
            
            VerificationItem(
                title = "手机认证",
                icon = R.drawable.ic_phone,
                isVerified = true,
                onItemClick = onPhoneAuthClick
            )
        }
    }
}

@Composable
private fun VerificationItem(
    title: String,
    icon: Int,
    isVerified: Boolean,
    onItemClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(0xFFF5F5F5), CircleShape)
                .border(2.dp, Color(0xFFE0E0E0), CircleShape)
                .clickable { onItemClick?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF666666)
            )
            
            // 认证对勾
            if (isVerified) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                        .align(Alignment.BottomEnd)
                        .offset(x = 8.dp, y = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "已认证",
                        modifier = Modifier.size(12.dp),
                        tint = Color.White
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun DynamicDailySection(
    onPublishDynamicClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "动态日常",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 动态内容卡片
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE8EAF6),
                            Color(0xFFF3E5F5)
                        )
                    ),
                    RoundedCornerShape(12.dp)
                )
                .border(2.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Today",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "记录你的日常",
                    fontSize = 14.sp,
                    color = Color(0xFF999999)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onPublishDynamicClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C27B0)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "发布动态",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("发布动态")
                }
            }
        }
    }
}

@Composable
private fun AboutMeSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "关于我",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 个性签名
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Ta说:我很懒没想好个性签名",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 星座标签
        Box(
            modifier = Modifier
                .background(Color(0xFFF0F0F0), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "摩羯座",
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun MyGiftsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "我的礼物",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 礼物卡片
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 礼物图标
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFE1BEE7),
                                    Color(0xFFF8BBD9)
                                )
                            ),
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Nightlight,
                        contentDescription = "晚安礼物",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF9C27B0)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "晚安",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "x1",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

@Composable
private fun EditProfileButton(
    onEditProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = onEditProfileClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                Color(0xFFE0E0E0)
            )
        ) {
            Text(
                text = "编辑资料",
                fontSize = 16.sp,
                color = Color(0xFF666666)
            )
        }
    }
}
