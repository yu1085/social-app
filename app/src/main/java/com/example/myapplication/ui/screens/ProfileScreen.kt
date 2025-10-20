package com.example.myapplication.ui.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
 
import com.example.myapplication.R
import com.example.myapplication.viewmodel.ProfileViewModel
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

/**
 * 个人中心页面 - 优化版实现
 * 按照Figma设计稿1:1复刻，增强用户体验和功能
 * 使用响应式布局，适配各种屏幕尺寸
 * 支持状态管理和数据绑定
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onSettingClick: () -> Unit = {},
    onVipClick: () -> Unit = {},
    onRechargeClick: () -> Unit = {},
    onWealthLevelClick: () -> Unit = {},
    onPropMallClick: () -> Unit = {},
    onMenuClick: (String) -> Unit = {},
    onUserInfoClick: () -> Unit = {},
    profileViewModel: ProfileViewModel? = null
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val viewModel = profileViewModel ?: remember { ProfileViewModel(context.applicationContext as Application) }
    
    // 下拉刷新状态
    var isRefreshing by remember { mutableStateOf(false) }
    var pullOffset by remember { mutableStateOf(0f) }
    
    // 页面加载时立即刷新钱包数据
    LaunchedEffect(Unit) {
        android.util.Log.d("ProfileScreen", "=== ProfileScreen LaunchedEffect 开始 ===")
        viewModel.refreshWalletDataImmediately()
    }
    
    // 监听ViewModel状态变化
    val userName = viewModel.userName
    val userId = viewModel.userId
    val isLoading = viewModel.isLoading
    val isVip = viewModel.isVip
    val voiceCallEnabled = viewModel.voiceCallEnabled
    val videoCallEnabled = viewModel.videoCallEnabled
    val messageChargeEnabled = viewModel.messageChargeEnabled
    
    // 直接使用ViewModel的方法获取余额显示文本
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 背景色
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 主要内容区域
            MainContentArea(
                onSettingClick = onSettingClick,
                onVipClick = onVipClick,
                onRechargeClick = onRechargeClick,
                onWealthLevelClick = onWealthLevelClick,
                onPropMallClick = onPropMallClick,
                onMenuClick = onMenuClick,
                onUserInfoClick = onUserInfoClick,
                viewModel = viewModel
            )
        }
        
        // 刷新状态指示器
        if (isRefreshing) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 20.dp)
                    .wrapContentSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFF498AFE),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

/**
 * 通用设置项 + 滑动开关
 */
@Composable
private fun SettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    trailingText: String,
    showPrice: Boolean = false,
    onPriceClick: (() -> Unit)? = null,
    context: android.content.Context? = null,
    alwaysShowText: Boolean = false  // 新增参数：是否总是显示文本（用于私信收费）
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.weight(1f))

        // 使用Material3自带的Switch，带滑动动画
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF498AFE),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFECEDF2)
            )
        )

        // 价格显示区域 - 根据alwaysShowText参数决定是否总是显示
        if (checked || alwaysShowText) {
            Spacer(modifier = Modifier.width(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = trailingText,
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )

                // 金币图标 - 只在有具体价格时显示
                if (trailingText.contains("/分钟")) {
                    Spacer(modifier = Modifier.width(2.dp))
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.ic_coin),
                        contentDescription = "金币",
                        modifier = Modifier.size(14.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}



/**
 * 主要内容区域
 */
@Composable
private fun MainContentArea(
    onSettingClick: () -> Unit,
    onVipClick: () -> Unit,
    onRechargeClick: () -> Unit,
    onWealthLevelClick: () -> Unit,
    onPropMallClick: () -> Unit,
    onMenuClick: (String) -> Unit,
    onUserInfoClick: () -> Unit = {},
    viewModel: ProfileViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        // 上半部分：用户信息、会员中心、财富等级、钱包
        Column(
        ) {
            // 用户信息区域 - 置顶显示
            UserInfoSection(
                onSettingClick = onSettingClick,
                onUserInfoClick = onUserInfoClick,
                viewModel = viewModel
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 会员中心区域
            MembershipSection(onVipClick = onVipClick)
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // 财富等级区域
            WealthLevelSection(
                onWealthLevelClick = onWealthLevelClick,
                onPropMallClick = onPropMallClick
            )
            
            Spacer(modifier = Modifier.height(36.dp))
            
            // 钱包区域
            WalletSection(
                onRechargeClick = onRechargeClick,
                viewModel = viewModel
            )
        }
        
        // 下半部分：功能设置区域 + 功能菜单区域（整体下移 20dp）
        Spacer(modifier = Modifier.height(36.dp))
        
        Column(
        ) {
            // 功能设置区域
            FunctionSettingsSection(viewModel = viewModel)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 功能菜单区域
            FunctionMenuSection(
                onMenuClick = onMenuClick
            )
        }
    }
}

/**
 * 用户信息区域 - 优化版
 */
@Composable
private fun UserInfoSection(
    onSettingClick: () -> Unit,
    onUserInfoClick: () -> Unit = {},
    viewModel: ProfileViewModel
) {
    // 直接使用viewModel的属性
    val userName = viewModel.userName
    val userId = viewModel.userId
    
    // 添加调试日志
    LaunchedEffect(userName, userId) {
        android.util.Log.d("ProfileScreen", "UserInfoSection - userName: $userName, userId: $userId")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUserInfoClick() }
    ) {
        // 第一行：设置按钮和刷新按钮
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 刷新按钮 - 修复版
            IconButton(
                onClick = {
                    android.util.Log.d("ProfileScreen", "=== 刷新按钮被点击 ===")
                    viewModel.refreshWalletDataImmediately()
                },
                modifier = Modifier
                    .size(30.dp, 30.dp)
                    .align(Alignment.TopStart)
                    .background(
                        Color(0xFFF8F9FA),
                        RoundedCornerShape(10.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "刷新",
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF666666)
                )
            }
            
            // 设置按钮 - 优化后的设计
            IconButton(
                onClick = {
                    // 添加调试日志
                    android.util.Log.d("ProfileScreen", "设置按钮被点击了！")
                    onSettingClick()
                },
                modifier = Modifier
                    .size(30.dp, 30.dp)  // 更小的设置按钮（30）
                    .align(Alignment.TopEnd)
                    .background(
                        Color(0xFFF8F9FA),
                        RoundedCornerShape(10.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置",
                    modifier = Modifier.size(14.dp),  // 更小的图标
                    tint = Color(0xFF666666)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(2.dp))  // 设置与用户信息间距更小
        
        // 第二行：用户信息区域（头像+昵称+ID）
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 用户头像 - 优化后的设计
            Box(
                modifier = Modifier
                    .size(88.dp, 88.dp)  // 保持原来尺寸
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFF8F9FA),
                                Color(0xFFE9ECEF)
                            )
                        )
                    )
                    .border(
                        width = 3.dp,  // 保持原来边框宽度
                        color = Color.White,
                        shape = RoundedCornerShape(22.dp)
                    )
                    // 头像点击功能 - 可以用于头像编辑
                    // .clickable { /* 头像编辑功能 */ }
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ic_avatar_custom),
                    contentDescription = "用户头像",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),  // 保持原来内边距
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(24.dp))  // 保持原来间距
            
            // 用户信息
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 0.dp) // 进一步上移
            ) {
                // 昵称和验证图标
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = userName,
                        fontSize = 22.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    var showTooltip by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { showTooltip = true },
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.ic_verified_star),
                            contentDescription = "认证",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.vector_5),
                            contentDescription = "认证标记",
                            modifier = Modifier.size(6.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    if (showTooltip) {
                        Box(
                            modifier = Modifier
                                .offset(y = (-26).dp)
                                .zIndex(1f)
                                .background(Color(0xE61A1A1A), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "已真人验证",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    LaunchedEffect(showTooltip) {
                        if (showTooltip) {
                            delay(1200)
                            showTooltip = false
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // 用户ID - 优化后的设计
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = viewModel.getUserDisplayId(),
                        fontSize = 16.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )
                    
                    Spacer(modifier = Modifier.width(10.dp))
                    
                    // 复制图标 - 优化后的设计
                    IconButton(
                        onClick = {
                            // 复制ID到剪贴板
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                Color(0xFFF8F9FA),
                                RoundedCornerShape(6.dp)
                            )
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.ic_copy),
                            contentDescription = "复制ID",
                            modifier = Modifier.size(14.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}

/**
 * 会员中心区域 - 优化版
 */
@Composable
private fun MembershipSection(onVipClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)  // 更高，容纳完整图标
            .clip(RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)  // 减小内边距以变宽
    ) {
        // 渐变背景（替代图片）
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFF6E9), // 浅金黄
                            Color(0xFFFFE8C8)  // 柔和金色
                        )
                    )
                )
        )
        
        // 内容覆盖在背景上
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 6.dp),  // 整体上移一点
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 会员中心标题
                Text(
                    text = "会员中心",
                    fontSize = 14.sp,  // 增加字体大小
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .height(23.dp)
                        .defaultMinSize(minWidth = 60.dp),
                    color = Color(0xFFB97A2E)
                )
                
                Spacer(modifier = Modifier.height(2.dp))  // 增加间距
                
                // 开通提示
                Text(
                    text = "开通会员，享专属特权",
                    fontSize = 10.sp,  // 增加字体大小
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFB97A2E)
                )
            }
            
            // VIP按钮
            Button(
                onClick = onVipClick,
                modifier = Modifier
                    .height(25.dp)
                    .defaultMinSize(minWidth = 70.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFDAA9)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "开通VIP",
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB97A2E),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * 财富等级区域 - 优化版
 */
@Composable
private fun WealthLevelSection(
    onWealthLevelClick: () -> Unit = {},
    onPropMallClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 财富等级
        Box(
            modifier = Modifier
                .weight(1f)
                .height(64.dp)  // 再加高度，完整展示
                .clip(RoundedCornerShape(20.dp))  // 增加圆角
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF5F8FD),
                            Color(0xFFE8EEFA)
                        )
                    )
                )
                .clickable { onWealthLevelClick() }
                .padding(12.dp)  // 增加内边距，避免裁剪
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "财富等级",
                    fontSize = 15.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                // 右侧徽章：Union 背板 + 星星 + 重影
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(28.dp)
                ) {
                    // 重影：union1_shadow 放在左后方
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.union1_shadow),
                        contentDescription = "徽章重影",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart)
                            .offset(y = 4.dp)
                            .offset(x = 1.dp)
                            .graphicsLayer(
                                scaleX = 1.1f,
                                scaleY = 1.1f
                            ),
                        contentScale = ContentScale.Fit
                    )
                    // 正面徽章 + 星星
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.TopStart)
                            .offset(x = (-6).dp, y = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.union_copy),
                            contentDescription = "徽章",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.star_6),
                            contentDescription = "星星",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(y = 2.dp)
                                .size(12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.width(10.dp))  // 增加间距
        
        // 道具商城
        Box(
            modifier = Modifier
                .weight(1f)
                .height(64.dp)  // 再加高度，完整展示
                .clip(RoundedCornerShape(20.dp))  // 增加圆角
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFDF8F5),
                            Color(0xFFFAF1EC)
                        )
                    )
                )
                .clickable { onPropMallClick() }  // 添加点击事件
                .padding(12.dp)  // 增加内边距
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "道具商城",
                    fontSize = 15.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                // 右侧徽章：Union1 背板 + 魔法棒 + 重影
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(28.dp)
                ) {
                    // 重影：union3 放在左后方
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.union3),
                        contentDescription = "徽章重影",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterStart)
                            .graphicsLayer(
                                scaleX = 1.18f,
                                scaleY = 1.18f
                            ),
                        contentScale = ContentScale.Fit
                    )
                    // 正面徽章 + 魔法棒
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.TopStart)
                            .offset(x = (-5.5).dp, y = -1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.union1),
                            contentDescription = "徽章",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = 1.0f,
                                    scaleY = 1.0f
                                ),
                            contentScale = ContentScale.Fit
                        )
                        androidx.compose.foundation.Image(
                            painter = painterResource(id = R.drawable.union_magic_wand),
                            contentDescription = "魔法棒",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(y = 2.dp)
                                .size(12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}

/**
 * 钱包区域
 */
@Composable
private fun WalletSection(
    onRechargeClick: () -> Unit,
    viewModel: ProfileViewModel
) {
    // 直接使用getBalanceDisplayText()方法
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)  // 整体高度+20dp
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(10.dp)
    ) {
        Column {
            // 钱包标题（左侧图标 + 文本）
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ic_wallet), // 若需要使用@钱包.png，请放入res/drawable并改为对应资源ID
                    contentDescription = "钱包",
                    modifier = Modifier.size(16.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "我的钱包",
                    fontSize = 14.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }
            
            Divider(
                color = Color(0xFFEAEBF0),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 6.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            // 余额信息
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "余额",
                    fontSize = 12.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = viewModel.getBalanceDisplayText(),
                    fontSize = 20.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.offset(y = (-2).dp)
                )
                
                // 金币图标
                Spacer(modifier = Modifier.width(4.dp))
                
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ic_coin),
                    contentDescription = "金币",
                    modifier = Modifier.size(20.dp),
                    contentScale = ContentScale.Fit
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 充值按钮
                Button(
                    onClick = onRechargeClick,
                    modifier = Modifier
                        .size(60.dp, 32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF498AFE)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "充值",
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF498AFE)
                    )
                }
            }
        }
    }
}

/**
 * 功能设置区域
 */
@Composable
private fun FunctionSettingsSection(viewModel: ProfileViewModel) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    val voiceCallEnabled = viewModel.voiceCallEnabled
    val videoCallEnabled = viewModel.videoCallEnabled
    val messageChargeEnabled = viewModel.messageChargeEnabled

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp) // 外边距更窄
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(6.dp) // 调大一点：卡片内所有内容距离边框四周6dp
    ) {
        Column {
            // 设置标题 - 添加通话设置图标、设置按钮和展开/收起按钮
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 28.dp) // 调大一点：标题行最小高度28dp
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.ic_call_settings),
                    contentDescription = "通话设置",
                    modifier = Modifier.size(18.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "设置来电及价格",
                    fontSize = 14.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.weight(1f))

                // 设置按钮 - 跳转到详细设置页面
                TextButton(
                    onClick = {
                        android.util.Log.d("ProfileScreen", "点击设置按钮，跳转到PriceSettingsActivity")
                        val intent = android.content.Intent(context, com.example.myapplication.PriceSettingsActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "设置",
                        fontSize = 12.sp,
                        color = Color(0xFF498AFE)
                    )
                }

                // 展开/收起按钮
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "收起" else "展开",
                        tint = Color(0xFF999999),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // 展开时显示详细设置
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // 语音接听设置
                SettingSwitchRow(
                    title = "语音接听",
                    checked = voiceCallEnabled,
                    onCheckedChange = {
                        // 调用后端API更新语音接听状态
                        viewModel.updateSettingsToBackend(it, videoCallEnabled, messageChargeEnabled)
                    },
                    trailingText = viewModel.getVoiceCallDisplayText(),
                    showPrice = voiceCallEnabled,
                    onPriceClick = {},
                    context = context
                )

                // 语音价格选择器 - 仅在开启时显示
                if (voiceCallEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceSelector(
                        selectedPrice = viewModel.voiceCallPrice.toInt(),
                        onPriceSelected = { price ->
                            viewModel.updatePriceToBackend(price.toDouble(), viewModel.videoCallPrice, viewModel.messagePrice)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 视频接听设置
                SettingSwitchRow(
                    title = "视频接听",
                    checked = videoCallEnabled,
                    onCheckedChange = {
                        // 调用后端API更新视频接听状态
                        viewModel.updateSettingsToBackend(voiceCallEnabled, it, messageChargeEnabled)
                    },
                    trailingText = viewModel.getVideoCallDisplayText(),
                    showPrice = videoCallEnabled,
                    onPriceClick = {},
                    context = context
                )

                // 视频价格选择器 - 仅在开启时显示
                if (videoCallEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceSelector(
                        selectedPrice = viewModel.videoCallPrice.toInt(),
                        onPriceSelected = { price ->
                            viewModel.updatePriceToBackend(viewModel.voiceCallPrice, price.toDouble(), viewModel.messagePrice)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 私信收费设置
                SettingSwitchRow(
                    title = "私信收费",
                    checked = messageChargeEnabled,
                    onCheckedChange = {
                        // 调用后端API更新私信收费状态
                        viewModel.updateSettingsToBackend(voiceCallEnabled, videoCallEnabled, it)
                    },
                    trailingText = viewModel.getMessageChargeDisplayText(),
                    showPrice = false,
                    onPriceClick = null,
                    context = context,
                    alwaysShowText = true  // 私信收费固定显示"免费"
                )
                
                // 免费接听时长信息
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "免费接听时长: 0分钟",
                        fontSize = 11.sp,
                        color = Color(0xFF666666)
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = "规则",
                        fontSize = 11.sp,
                        color = Color(0xFF498AFE),
                        modifier = Modifier.clickable {
                            // TODO: 跳转到规则说明页面
                            android.util.Log.d("ProfileScreen", "点击查看规则")
                        }
                    )
                }
            }
        }
    }
}

/**
 * 单个菜单项组件
 */
@Composable
private fun MenuItem(
    title: String,
    iconRes: Int,
    onMenuClick: (String) -> Unit,
    showDivider: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMenuClick(title) }
            .padding(vertical = 0.dp)
    ) {
        // 图标 - 使用自定义图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 标题
        Text(
            text = title,
            fontSize = 16.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 箭头
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "更多",
            tint = Color(0xFFCCCCCC)
        )
    }
    
    if (showDivider) {
        Divider(
            color = Color(0xFFF0F0F0),
            thickness = 0.6.dp,
            modifier = Modifier.padding(start = 56.dp)
        )
    }
}

/**
 * 功能菜单区域
 */
@Composable
private fun FunctionMenuSection(
    onMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
    ) {
        FunctionMenuItems(onMenuClick = onMenuClick)
    }
}

/**
 * 功能菜单项
 */
@Composable
private fun FunctionMenuItems(onMenuClick: (String) -> Unit) {
    val allMenuItems = listOf(
        "邀请好友" to R.drawable.ic_invite,
        "我的认证" to R.drawable.ic_certification,
        "我的卡券" to R.drawable.ic_coupons,
        "谁看过我" to R.drawable.ic_who_viewed,
        "我的守护" to R.drawable.ic_guardian,
        "我的礼物" to R.drawable.ic_gift,
        "我的客服" to R.drawable.ic_customer_service
    )
    
    // 按顺序渲染所有菜单项
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        allMenuItems.forEachIndexed { index, pair ->
            val (title, iconRes) = pair
            MenuItem(
                title = title,
                iconRes = iconRes,
                onMenuClick = onMenuClick,
                showDivider = index != allMenuItems.lastIndex
            )
        }
    }
}

/**
 * 价格选择器组件
 */
@Composable
private fun PriceSelector(
    selectedPrice: Int,
    onPriceSelected: (Int) -> Unit
) {
    val priceOptions = listOf(0, 100, 200, 300, 400, 500)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        priceOptions.forEach { price ->
            val isSelected = price == selectedPrice

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) Color(0xFF498AFE) else Color(0xFFF5F5F5)
                    )
                    .clickable {
                        android.util.Log.d("PriceSelector", "选择价格: $price")
                        onPriceSelected(price)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$price",
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else Color(0xFF666666)
                )
            }
        }
    }
}

@Preview(
    name = "Profile Screen",
    showBackground = true,
    backgroundColor = 0xFFF7F8FAL,
    widthDp = 392,
    heightDp = 850
)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}