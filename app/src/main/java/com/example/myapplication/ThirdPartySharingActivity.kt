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

class ThirdPartySharingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThirdPartySharingScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdPartySharingScreen(
    onBackClick: () -> Unit
) {
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
                        text = "与第三方共享信息清单",
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
                    .padding(16.dp)
            ) {
                // 接入第三方清单说明
                ThirdPartyAccessSection()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // App从第三方处获取的个人信息清单
                AppObtainedInfoTable()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // App与第三方共享信息清单
                AppSharingInfoTable()
            }
        }
    }
}

@Composable
fun ThirdPartyAccessSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "接入第三方清单",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "为了确保应用功能及稳定运行，我们应用中集成了第三方软件开发工具包（SDK）和应用程序编程接口（API）。不同版本的第三方SDK和API可能有所不同，但一般包括以下类别：一键登录、第三方账号登录、推送通知、手机厂商推送服务、第三方支付、地图导航、分享、统计、性能监控、云存储、点播、内容安全、唯一设备标识符、音视频通信、客服工具、开发工具等。",
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "我们对合作伙伴通过SDK和API获取的信息进行严格的安全检查，确保数据安全。您可以通过以下链接查看第三方的数据使用和保护规则。",
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun AppObtainedInfoTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 表格标题
            Text(
                text = "App从第三方处获取的个人信息清单",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp)
            )
            
            // 表格内容
            AppObtainedInfoContent()
        }
    }
}

@Composable
fun AppObtainedInfoContent() {
    val obtainedInfoData = listOf(
        ObtainedInfoItem(
            system = "安卓/iOS",
            product = "闪验SDK",
            company = "上海创蓝文化传播有限公司",
            infoType = "手机号",
            purpose = "实现手机号一键登录功能",
            method = "API接口",
            link = "查看链接"
        ),
        ObtainedInfoItem(
            system = "安卓/iOS",
            product = "中国移动认证服务SDK(含CMIC SSO)",
            company = "中国移动",
            infoType = "手机号",
            purpose = "实现手机号一键登录功能",
            method = "API接口",
            link = "查看链接"
        ),
        ObtainedInfoItem(
            system = "安卓/iOS",
            product = "中国电信天翼账号认证服务SDK",
            company = "中国电信",
            infoType = "手机号",
            purpose = "实现手机号一键登录功能",
            method = "API接口",
            link = "查看链接"
        ),
        ObtainedInfoItem(
            system = "安卓/iOS",
            product = "中国联通认证服务SDK",
            company = "中国联通",
            infoType = "手机号",
            purpose = "实现手机号一键登录功能",
            method = "API接口",
            link = "查看链接"
        )
    )
    
    obtainedInfoData.forEachIndexed { index, item ->
        ObtainedInfoRow(item = item)
        if (index < obtainedInfoData.size - 1) {
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0xFFE0E0E0)
            )
        }
    }
}

@Composable
fun ObtainedInfoRow(item: ObtainedInfoItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // 系统
        InfoRow(label = "所属系统", value = item.system)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 产品
        InfoRow(label = "产品", value = item.product)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 公司
        InfoRow(label = "公司", value = item.company)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 获取信息类型
        InfoRow(label = "获取信息类型", value = item.infoType)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 获取目的
        InfoRow(label = "获取目的", value = item.purpose)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 获取方式
        InfoRow(label = "获取方式", value = item.method)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 链接
        InfoRow(label = "链接", value = item.link)
        
    }
}

@Composable
fun AppSharingInfoTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 表格标题
            Text(
                text = "App与第三方共享信息清单",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp)
            )
            
            // 说明文字
            Text(
                text = "虽然我们采取了严格的安全措施，但有些产品/服务无法由我们独立完成，因此，我们会将部分个人信息委托给或共享给其他合作伙伴，以确保这些产品/服务的顺利完成。",
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp,
                modifier = Modifier.padding(12.dp)
            )
            
            // 表格内容
            AppSharingInfoContent()
        }
    }
}

@Composable
fun AppSharingInfoContent() {
    val sharingInfoData = listOf(
        SharingInfoItem(
            system = "安卓/iOS/鸿蒙",
            product = "数字联盟可信ID",
            company = "北京数字联盟网络科技有限公司",
            infoType = "设备制造商、设备型号、设备状态、设备系统版本、应用版本、传感器(光传感器、磁场传感器、重力传感器、压力传感器、方向传感器、旋转矢量传感器、陀螺仪传感器、加速度传感器)、应用列表、通信状态、信号强度、蓝牙信息、设备网络状态信息(网络的接入形式、IP地址、WIFI信息(BSSID、SSID)、运营商类型及网络基站信息、改变网络类型)、设备物理环境信息、设备识别码(根据风险等级可选)、设备广告标识(OAID)、IDFA(面向儿童的应用不收集IDFA)、IDFV、地理位置信息",
            purpose = "检测设备欺诈与作弊行为,识别反馈设备的真实性",
            method = "SDK获取",
            link = "查看链接"
        ),
        SharingInfoItem(
            system = "安卓/iOS",
            product = "闪验SDK",
            company = "上海创蓝文化传播有限公司",
            infoType = "IP地址、网卡(MAC)地址、国际移动设备识别码(IMEI)、OAID(替代)",
            purpose = "为了实现网关取号技术",
            method = "SDK获取",
            link = "查看链接"
        )
    )
    
    sharingInfoData.forEachIndexed { index, item ->
        SharingInfoRow(item = item)
        if (index < sharingInfoData.size - 1) {
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0xFFE0E0E0)
            )
        }
    }
}

@Composable
fun SharingInfoRow(item: SharingInfoItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // 系统
        InfoRow(label = "所属系统", value = item.system)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 产品
        InfoRow(label = "产品", value = item.product)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 公司
        InfoRow(label = "公司", value = item.company)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 获取信息类型
        InfoRow(label = "获取信息类型", value = item.infoType)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 获取目的
        InfoRow(label = "获取目的", value = item.purpose)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 获取方式
        InfoRow(label = "获取方式", value = item.method)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 链接
        InfoRow(label = "链接", value = item.link)
        
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.weight(0.3f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.weight(0.7f),
            textAlign = TextAlign.End
        )
    }
}

data class ObtainedInfoItem(
    val system: String,
    val product: String,
    val company: String,
    val infoType: String,
    val purpose: String,
    val method: String,
    val link: String
)

data class SharingInfoItem(
    val system: String,
    val product: String,
    val company: String,
    val infoType: String,
    val purpose: String,
    val method: String,
    val link: String
)
