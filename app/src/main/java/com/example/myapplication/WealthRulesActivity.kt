package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * 财富等级规则说明页面
 */
class WealthRulesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WealthRulesScreen(
                        onBackClick = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun WealthRulesScreen(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 顶部标题栏
            WealthRulesHeader(onBackClick = onBackClick)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 财富等级规则表格
            WealthLevelRulesTable()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 权益说明表格
            BenefitsDescriptionTable()
        }
    }
}

@Composable
fun WealthRulesHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Text(
            text = "规则说明",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.width(48.dp)) // 平衡布局
    }
}

@Composable
fun WealthLevelRulesTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 表格标题
            Text(
                text = "财富等级规则",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 表头
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "财富值",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "等级",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 财富等级数据
            val wealthLevels = listOf(
                WealthLevel("≥1000000", "黑钻", Color(0xFFFFD700)),
                WealthLevel("≥700000", "金钻", Color(0xFFFFD700)),
                WealthLevel("≥500000", "红钻", Color(0xFFFF69B4)),
                WealthLevel("≥300000", "橙钻", Color(0xFFFF8C00)),
                WealthLevel("≥100000", "紫钻", Color(0xFF9370DB)),
                WealthLevel("≥50000", "蓝钻", Color(0xFF4169E1)),
                WealthLevel("≥30000", "青钻", Color(0xFF20B2AA)),
                WealthLevel("≥10000", "铂金", Color(0xFFE5E4E2)),
                WealthLevel("≥5000", "黄金", Color(0xFFFFD700)),
                WealthLevel("≥2000", "白银", Color(0xFFC0C0C0)),
                WealthLevel("≥1000", "青铜", Color(0xFFCD7F32))
            )
            
            wealthLevels.forEach { level ->
                WealthLevelRow(
                    wealthValue = level.wealthValue,
                    levelName = level.levelName,
                    levelColor = level.levelColor
                )
                
                if (level != wealthLevels.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun WealthLevelRow(
    wealthValue: String,
    levelName: String,
    levelColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 财富值
        Text(
            text = wealthValue,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        
        // 等级图标和名称
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // 等级图标（圆形背景）
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = levelColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "◆",
                    fontSize = 12.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 等级名称
            Text(
                text = levelName,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun BenefitsDescriptionTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 表格标题
            Text(
                text = "权益说明",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 表头
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "权益项",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "说明",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
            
            // 权益说明数据
            val benefits = listOf(
                Benefit("专属标识", "在个人资料中显示财富等级标识"),
                Benefit("优先匹配", "优先匹配同等级或更高等级用户"),
                Benefit("专属客服", "享受专属客服服务"),
                Benefit("特殊权限", "解锁更多高级功能"),
                Benefit("等级奖励", "达到新等级可获得专属奖励"),
                Benefit("身份认证", "财富等级作为身份认证依据"),
                Benefit("社交特权", "享受更多社交互动特权"),
                Benefit("活动优先", "优先参与平台专属活动")
            )
            
            benefits.forEach { benefit ->
                BenefitRow(
                    benefitItem = benefit.benefitItem,
                    description = benefit.description
                )
                
                if (benefit != benefits.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun BenefitRow(
    benefitItem: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 权益项
        Text(
            text = benefitItem,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        
        // 说明
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
    }
}

data class WealthLevel(
    val wealthValue: String,
    val levelName: String,
    val levelColor: Color
)

data class Benefit(
    val benefitItem: String,
    val description: String
)
