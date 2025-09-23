package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * VIP特权数据类
 */
data class VipPrivilege(
    val title: String,
    val icon: ImageVector,
    val unlockLevel: String,
    val description: String = ""
)

/**
 * VIP套餐数据类
 */
data class VipPlan(
    val duration: String,
    val currentPrice: String,
    val originalPrice: String,
    val growthValue: String = "",
    val isBest: Boolean = false
)

/**
 * VIP会员中心页面
 */
@Composable
fun VipCenterScreen(
    onBackClick: () -> Unit,
    onUpgradeSvipClick: () -> Unit = {},
    onActivateVipClick: (Long) -> Unit = {},
    onPaymentConfirm: (String, String, String, Long) -> Unit = { _, _, _, _ -> },
    onAgreementClick: () -> Unit = {},
    vipLevels: List<com.example.myapplication.model.VipLevel> = emptyList(),
    currentSubscription: com.example.myapplication.model.VipSubscription? = null,
    isVip: Boolean = false,
    isLoading: Boolean = false,
    isSubscribing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedPlan by remember { mutableStateOf(2) } // 默认选择12个月套餐
    var selectedTab by remember { mutableStateOf(1) } // 0=VIP, 1=SVIP，默认选择SVIP
    var showPaymentDialog by remember { mutableStateOf(false) } // 支付对话框显示状态
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(scrollState)
    ) {
        // 顶部导航栏
        VipTopBar(onBackClick = onBackClick)
        
        // 用户信息区域
        UserVipInfoSection(
            onUpgradeSvipClick = onUpgradeSvipClick
        )
        
        // VIP/SVIP标签
        VipTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        // 套餐选择
        VipPlanSelection(
            selectedPlan = selectedPlan,
            onPlanSelected = { selectedPlan = it },
            isSvip = selectedTab == 1
        )
        
        // 优惠信息
        SavingsInfo(isSvip = selectedTab == 1)
        
        // 开通按钮
        ActivateVipButton(
            onActivateVipClick = {
                // 显示支付对话框
                showPaymentDialog = true
            },
            isSvip = selectedTab == 1,
            isLoading = isSubscribing
        )
        
        // 协议文本
        AgreementText(onAgreementClick = onAgreementClick)
        
        // VIP特权展示
        VipPrivilegesSection(isSvip = selectedTab == 1)
        
        Spacer(modifier = Modifier.height(20.dp))
    }
    
    // 支付对话框
    PaymentDialog(
        isVisible = showPaymentDialog,
        onDismiss = { showPaymentDialog = false },
        onConfirmPayment = { paymentMethod, membershipType, price ->
            // 处理支付确认
            val vipLevelId = when {
                selectedTab == 0 -> 1L // VIP会员
                selectedTab == 1 -> 2L // SVIP会员
                else -> 2L
            }
            onPaymentConfirm(paymentMethod.name, membershipType, price, vipLevelId)
            showPaymentDialog = false
        },
        membershipType = if (selectedTab == 1) "SVIP会员${getPlanDuration(selectedPlan)}" else "VIP会员${getPlanDuration(selectedPlan)}",
        price = getPlanPrice(selectedPlan, selectedTab == 1)
    )
}

/**
 * 获取套餐时长
 */
private fun getPlanDuration(selectedPlan: Int): String {
    return when (selectedPlan) {
        0 -> "1个月"
        1 -> "3个月"
        2 -> "12个月"
        else -> "12个月"
    }
}

/**
 * 获取套餐价格
 */
private fun getPlanPrice(selectedPlan: Int, isSvip: Boolean): String {
    return if (isSvip) {
        when (selectedPlan) {
            0 -> "¥40"
            1 -> "¥102"
            2 -> "¥334"
            else -> "¥334"
        }
    } else {
        when (selectedPlan) {
            0 -> "¥20"
            1 -> "¥54"
            2 -> "¥166"
            else -> "¥166"
        }
    }
}

/**
 * 顶部导航栏
 */
@Composable
private fun VipTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4A90E2))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White
            )
        }
        
        Text(
            text = "会员中心",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.width(48.dp)) // 平衡左侧返回按钮
    }
}

/**
 * 用户VIP信息区域
 */
@Composable
private fun UserVipInfoSection(
    onUpgradeSvipClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 用户头像
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "用户头像",
                        modifier = Modifier.size(30.dp),
                        tint = Color(0xFF4A90E2)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "相约未来605",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Text(
                        text = "开通会员,尽享20大专属特权",
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    // 进度条
                    LinearProgressIndicator(
                        progress = 0.8f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFFFF6B35),
                        trackColor = Color(0xFFE0E0E0)
                    )
                    
                    Text(
                        text = "未开通,还需1成长值升至V1 升级规则 >",
                        fontSize = 10.sp,
                        color = Color(0xFF999999),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                // 升级SVIP按钮
                Button(
                    onClick = onUpgradeSvipClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "升级SVIP",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * VIP/SVIP标签
 */
@Composable
private fun VipTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // VIP标签
        Box(
            modifier = Modifier
                .background(
                    if (selectedTab == 0) Color(0xFFFF6B35) else Color(0xFFE0E0E0),
                    RoundedCornerShape(20.dp)
                )
                .clickable { onTabSelected(0) }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "VIP",
                    modifier = Modifier.size(16.dp),
                    tint = if (selectedTab == 0) Color.White else Color(0xFF666666)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "VIP",
                    color = if (selectedTab == 0) Color.White else Color(0xFF666666),
                    fontSize = 14.sp,
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // SVIP标签
        Box(
            modifier = Modifier
                .background(
                    if (selectedTab == 1) Color(0xFFFFD700) else Color(0xFFE0E0E0),
                    RoundedCornerShape(20.dp)
                )
                .clickable { onTabSelected(1) }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "SVIP",
                    modifier = Modifier.size(16.dp),
                    tint = if (selectedTab == 1) Color.White else Color(0xFF666666)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SVIP",
                    color = if (selectedTab == 1) Color.White else Color(0xFF666666),
                    fontSize = 14.sp,
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

/**
 * VIP套餐选择
 */
@Composable
private fun VipPlanSelection(
    selectedPlan: Int,
    onPlanSelected: (Int) -> Unit,
    isSvip: Boolean = false
) {
    val plans = if (isSvip) {
        listOf(
            VipPlan("1个月", "¥40", "¥50"),
            VipPlan("3个月", "¥102", "¥150"),
            VipPlan("12个月", "¥334", "¥600", "成长值+1080", true)
        )
    } else {
        listOf(
            VipPlan("1个月", "¥20", "¥25"),
            VipPlan("3个月", "¥54", "¥75"),
            VipPlan("12个月", "¥166", "¥300", "成长值+720", true)
        )
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        plans.forEachIndexed { index, plan ->
            Box(
                modifier = Modifier.weight(1f)
            ) {
                VipPlanCard(
                    plan = plan,
                    isSelected = selectedPlan == index,
                    onClick = { onPlanSelected(index) }
                )
            }
        }
    }
}

/**
 * VIP套餐卡片
 */
@Composable
private fun VipPlanCard(
    plan: VipPlan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF3E0) else Color.White
        ),
        border = if (isSelected) BorderStroke(2.dp, Color(0xFFFF6B35)) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (plan.isBest) {
                Box(
                    modifier = Modifier
                        .background(
                            Color(0xFFFF6B35),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "最佳",
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Text(
                text = plan.duration,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = plan.currentPrice,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35)
            )
            
            Text(
                text = plan.originalPrice,
                fontSize = 12.sp,
                color = Color(0xFF999999),
                textDecoration = TextDecoration.LineThrough
            )
            
            if (plan.growthValue.isNotEmpty()) {
                Text(
                    text = plan.growthValue,
                    fontSize = 10.sp,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

/**
 * 优惠信息
 */
@Composable
private fun SavingsInfo(isSvip: Boolean = false) {
    val savingsText = if (isSvip) {
        "已省¥266 官方直减¥182 + 财富等级优惠¥84"
    } else {
        "已省¥134 官方直减¥92 + 财富等级优惠¥42"
    }
    
    Text(
        text = savingsText,
        fontSize = 12.sp,
        color = Color(0xFFFF4444),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

/**
 * 开通VIP按钮
 */
@Composable
private fun ActivateVipButton(
    onActivateVipClick: () -> Unit,
    isSvip: Boolean = false,
    isLoading: Boolean = false
) {
    Button(
        onClick = onActivateVipClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSvip) Color(0xFFFFD700) else Color(0xFFFF6B35)
        ),
        shape = RoundedCornerShape(24.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = if (isSvip) "立即开通SVIP" else "立即开通VIP",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 协议文本
 */
@Composable
private fun AgreementText(onAgreementClick: () -> Unit = {}) {
    Text(
        text = "成为会员即表示同意《会员服务协议》",
        fontSize = 10.sp,
        color = Color(0xFF1677FF),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAgreementClick() }
            .padding(horizontal = 16.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

/**
 * VIP特权展示区域
 */
@Composable
private fun VipPrivilegesSection(isSvip: Boolean = false) {
    val privileges = if (isSvip) {
        listOf(
            VipPrivilege("专属SVIP标识", Icons.Default.Diamond, "v1解锁"),
            VipPrivilege("查看访客记录", Icons.Default.Visibility, "v1解锁"),
            VipPrivilege("专属礼物", Icons.Default.CardGiftcard, "v1解锁"),
            VipPrivilege("高级美颜", Icons.Default.Face, "v1解锁"),
            VipPrivilege("私信收费", Icons.Default.Message, "v1解锁"),
            VipPrivilege("通话收费", Icons.Default.VideoCall, "v1解锁"),
            VipPrivilege("联系人数上限", Icons.Default.People, "v1解锁500"),
            VipPrivilege("隐藏在线状态", Icons.Default.VisibilityOff, "v1解锁")
        )
    } else {
        listOf(
            VipPrivilege("专属VIP标识", Icons.Default.Star, "v1解锁"),
            VipPrivilege("查看访客记录", Icons.Default.Visibility, "v1解锁"),
            VipPrivilege("专属礼物", Icons.Default.CardGiftcard, "v1解锁"),
            VipPrivilege("高级美颜", Icons.Default.Face, "v1解锁"),
            VipPrivilege("私信收费", Icons.Default.Message, "v4解锁"),
            VipPrivilege("通话收费", Icons.Default.VideoCall, "v4解锁"),
            VipPrivilege("联系人数上限", Icons.Default.People, "v4解锁300"),
            VipPrivilege("隐藏在线状态", Icons.Default.VisibilityOff, "v4解锁")
        )
    }
    
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = if (isSvip) "SVIP尊享20大特权" else "VIP尊享20大特权",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // 使用Row和Column替代LazyVerticalGrid来避免滚动冲突
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 第一行
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                privileges.take(4).forEach { privilege ->
                    Box(modifier = Modifier.weight(1f)) {
                        VipPrivilegeItem(privilege = privilege)
                    }
                }
            }
            
            // 第二行
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                privileges.drop(4).forEach { privilege ->
                    Box(modifier = Modifier.weight(1f)) {
                        VipPrivilegeItem(privilege = privilege)
                    }
                }
            }
        }
    }
}

/**
 * VIP特权项
 */
@Composable
private fun VipPrivilegeItem(
    privilege: VipPrivilege
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    Color(0xFFF5F5F5),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = privilege.icon,
                contentDescription = privilege.title,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF666666)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = privilege.title,
            fontSize = 10.sp,
            color = Color.Black,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.width(60.dp)
        )
        
        Text(
            text = privilege.unlockLevel,
            fontSize = 8.sp,
            color = Color(0xFF999999),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/**
 * 预览
 */
@Preview(showBackground = true)
@Composable
fun VipCenterScreenPreview() {
    VipCenterScreen(
        onBackClick = {},
        onUpgradeSvipClick = {},
        onActivateVipClick = {}
    )
}
