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

class PersonalInfoCollectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalInfoCollectionScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoCollectionScreen(
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
                        text = "收集个人信息清单",
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
                // 收集个人信息清单表格
                PersonalInfoCollectionTable()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // iOS系统权限调用说明
                IosPermissionTable()
            }
        }
    }
}

@Composable
fun PersonalInfoCollectionTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 表格标题
            TableHeader()
            
            // 表格内容
            TableContent()
        }
    }
}

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(12.dp)
    ) {
        Text(
            text = "场景/业务功能",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "个人信息种类",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = "使用目的",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
fun TableContent() {
    val collectionData = listOf(
        CollectionItem(
            scenario = "账号注册、登录与验证",
            infoType = "手机号、短信验证码、密码",
            purpose = "用于注册创建账号和登录以及更换账号绑定的手机号"
        ),
        CollectionItem(
            scenario = "",
            infoType = "性别、头像、昵称",
            purpose = "用于完善网络身份标识,并基于性别提供对应的平台服务"
        ),
        CollectionItem(
            scenario = "第三方账号登录",
            infoType = "第三方平台账户相关信息",
            purpose = "用于使用第三方账号授权登录的情形"
        ),
        CollectionItem(
            scenario = "基于位置的展示功能",
            infoType = "位置",
            purpose = "用于应用中,使用相关同城、附近、地图、发送当前位置功能,用户可以自行选择开启位置权限"
        ),
        CollectionItem(
            scenario = "信息发布和评价功能",
            infoType = "头像、昵称、发布信息(包括动态、视频及相关评论)",
            purpose = "用于展示动态、视频中用户发布的信息以及相关评论信息"
        ),
        CollectionItem(
            scenario = "",
            infoType = "昵称、生日、职业、照片、星座、所在地、身高、体重、情感状态、年收入、家乡、学历、居住情况、是否购房、是否购车、个性签名、标签",
            purpose = "用户可以根据自身需要完善个人主页信息,若用户拒绝完善主页信息,不会影响正常使用"
        ),
        CollectionItem(
            scenario = "私信与音视频聊天",
            infoType = "面部信息、语音信息、文字、图片、表情信息",
            purpose = "用于平台违法违规信息监测"
        ),
        CollectionItem(
            scenario = "自拍认证、真人认证",
            infoType = "人脸信息",
            purpose = "用于平台中申请成为男神/女神,发起提现申请"
        ),
        CollectionItem(
            scenario = "营销活动",
            infoType = "手机号、第三方平台账户",
            purpose = "用于平台向用户发送商业性服务信息,用户可以直接在短信中退订,或在\"设置-通知设置\"中予以关闭,或联系平台客服处理"
        ),
        CollectionItem(
            scenario = "离线来电通知",
            infoType = "手机号",
            purpose = "用于当用户不在线时,系统向用户发送来电通知功能,用户可在\"设置-呼叫不通转手机通知\"中开关此功能"
        ),
        CollectionItem(
            scenario = "支付功能",
            infoType = "支付时间、支付金额、支付渠道",
            purpose = "用于完成在线支付功能"
        ),
        CollectionItem(
            scenario = "消费功能",
            infoType = "消费记录信息(包括充值、消费记录信息、订单信息)",
            purpose = "用于用户充值及消费的凭证"
        ),
        CollectionItem(
            scenario = "",
            infoType = "手机号码、短信验证码、应用平台账户信息",
            purpose = "用于核验用户身份"
        ),
        CollectionItem(
            scenario = "客服及争议处理",
            infoType = "通信/通话记录和内容(包括账户信息、用户为了证明相关事实提供的其他信息、与客服的联系记录)",
            purpose = "用于客服服务及争议处理"
        ),
        CollectionItem(
            scenario = "使用过程中",
            infoType = "设备信息、日志信息、网络信息",
            purpose = "用于安全保障及产品优化"
        ),
        CollectionItem(
            scenario = "附加功能",
            infoType = "相机拍摄、相册读取存储、麦克风录音、视频录制、读取设备状态、消息推送、读取应用安装列表",
            purpose = "上述附加功能仅在特定场景下用户使用相关功能时才会申请询问,用户拒绝不影响用户使用基本功能"
        )
    )
    
    collectionData.forEach { item ->
        TableRow(item = item)
    }
}

@Composable
fun TableRow(item: CollectionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = item.scenario,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            lineHeight = 16.sp
        )
        Text(
            text = item.infoType,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.weight(1.2f),
            lineHeight = 16.sp
        )
        Text(
            text = item.purpose,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.weight(1.5f),
            lineHeight = 16.sp
        )
    }
}

@Composable
fun IosPermissionTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // iOS权限表格标题
            Text(
                text = "iOS系统权限调用说明",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp)
            )
            
            // iOS权限表格内容
            IosPermissionContent()
        }
    }
}

@Composable
fun IosPermissionContent() {
    val iosPermissionData = listOf(
        IosPermissionItem(
            category = "敏感权限",
            permission = "Privacy - Location When In Use Usage Description",
            description = "始终定位权限",
            purpose = "首页同城、附近动态、发送动态、交友同城、设置资料中的所在地、聊天发送定位"
        ),
        IosPermissionItem(
            category = "敏感权限",
            permission = "Privacy - Location When In Use Usage Description",
            description = "使用时定位权限",
            purpose = "首页同城、附近动态、发送动态、交友同城、设置资料中的所在地、聊天发送定位"
        )
    )
    
    iosPermissionData.forEachIndexed { index, item ->
        IosPermissionRow(item = item)
        if (index < iosPermissionData.size - 1) {
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0xFFE0E0E0)
            )
        }
    }
}

@Composable
fun IosPermissionRow(item: IosPermissionItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "权限分类",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = item.category,
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "权限项",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = item.permission,
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "权限描述",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = item.description,
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "使用目的",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = item.purpose,
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }
}

data class CollectionItem(
    val scenario: String,
    val infoType: String,
    val purpose: String
)

data class IosPermissionItem(
    val category: String,
    val permission: String,
    val description: String,
    val purpose: String
)
