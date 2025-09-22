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
import com.example.myapplication.ui.theme.MyApplicationTheme

class TermsOfServiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                TermsOfServiceScreen(
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun TermsOfServiceScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部导航栏
        TopAppBar(
            title = {
                Text(
                    text = "中国移动认证服务条款",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
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
            // 服务条款内容
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "中国移动认证服务条款",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "生效日期：2024年1月1日",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // 条款内容
                    val termsContent = """
                        1. 服务说明
                        中国移动认证服务（以下简称"本服务"）是由中国移动通信集团有限公司提供的身份认证服务，用于验证用户手机号码的真实性和有效性。

                        2. 服务内容
                        - 手机号码一键登录认证
                        - 用户身份信息验证
                        - 安全认证服务
                        - 相关技术支持

                        3. 用户权利和义务
                        3.1 用户权利
                        - 享受安全、便捷的认证服务
                        - 保护个人隐私信息
                        - 获得必要的技术支持

                        3.2 用户义务
                        - 提供真实、准确的个人信息
                        - 妥善保管个人账户信息
                        - 遵守相关法律法规
                        - 不得将服务用于违法用途

                        4. 隐私保护
                        4.1 信息收集
                        我们仅收集认证所必需的最少信息，包括：
                        - 手机号码
                        - 设备信息
                        - 网络信息

                        4.2 信息使用
                        收集的信息仅用于：
                        - 身份认证服务
                        - 安全防护
                        - 服务改进

                        4.3 信息保护
                        - 采用加密技术保护数据安全
                        - 严格限制信息访问权限
                        - 定期进行安全审计

                        5. 服务限制
                        5.1 服务可用性
                        - 服务可能因维护、升级等原因暂时中断
                        - 不保证服务100%可用性
                        - 因不可抗力导致的服务中断不承担责任

                        5.2 使用限制
                        - 禁止恶意使用服务
                        - 禁止破解、逆向工程
                        - 禁止用于违法活动

                        6. 免责声明
                        6.1 服务中断
                        因以下原因导致的服务中断，我们不承担责任：
                        - 网络故障
                        - 设备故障
                        - 不可抗力事件
                        - 第三方原因

                        6.2 信息准确性
                        - 用户提供的信息由用户负责
                        - 我们仅提供认证服务，不保证信息真实性
                        - 因信息不实导致的后果由用户承担

                        7. 服务变更
                        7.1 条款修改
                        - 可能随时修改本条款
                        - 修改后的条款将在平台公布
                        - 继续使用服务视为同意修改

                        7.2 服务终止
                        - 可能随时终止服务
                        - 提前30天通知用户
                        - 用户可随时停止使用服务

                        8. 争议解决
                        8.1 适用法律
                        本条款适用中华人民共和国法律。

                        8.2 争议处理
                        - 首先通过协商解决
                        - 协商不成的，提交有管辖权的人民法院解决

                        9. 联系方式
                        如有疑问，请联系我们：
                        - 客服电话：10086
                        - 官方网站：www.10086.cn
                        - 邮箱：service@10086.cn

                        10. 其他条款
                        10.1 条款效力
                        本条款自用户同意之日起生效。

                        10.2 条款解释
                        本条款的解释权归中国移动通信集团有限公司所有。

                        感谢您选择中国移动认证服务！
                    """.trimIndent()
                    
                    Text(
                        text = termsContent,
                        fontSize = 14.sp,
                        color = Color.Black,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // 同意按钮
                    Button(
                        onClick = {
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF007AFF)
                        )
                    ) {
                        Text(
                            text = "我已阅读并同意",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}