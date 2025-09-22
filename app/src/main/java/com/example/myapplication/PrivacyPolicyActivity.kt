package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PrivacyPolicyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrivacyPolicyScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen() {
    val context = LocalContext.current
    var isAgreed by remember { mutableStateOf(false) }
    var showThirdPartySDK by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        Text(
            text = "隐私政策",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // 隐私政策内容
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "个人信息收集和使用说明",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "我们非常重视您的隐私保护。在使用我们的服务时，我们可能会收集和使用您的个人信息。",
                    fontSize = 14.sp
                )
                
                Text(
                    text = "收集的个人信息类型：",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "• 基本信息：姓名、手机号、身份证号\n• 设备信息：设备型号、操作系统版本\n• 网络信息：IP地址、网络类型\n• 位置信息：用于提供附近用户功能\n• 相机权限：用于拍照和身份认证",
                    fontSize = 14.sp
                )
                
                Text(
                    text = "使用目的：",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "• 提供社交服务功能\n• 用户身份验证\n• 保障服务安全稳定运行\n• 提升用户体验",
                    fontSize = 14.sp
                )
            }
        }

        // 第三方SDK信息
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "第三方SDK信息",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Button(
                        onClick = { showThirdPartySDK = !showThirdPartySDK },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text(if (showThirdPartySDK) "收起" else "查看详情")
                    }
                }
                
                if (showThirdPartySDK) {
                    // 支付宝身份验证SDK信息
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "身份验证 SDK",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = "第三方名称：支付宝（中国）网络技术有限公司",
                                fontSize = 12.sp
                            )
                            
                            Text(
                                text = "收集的个人信息：",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Text(
                                text = "人脸信息、IMEI、IMSI、MAC地址、设备序列号、硬件序列号、SIM卡序列号、ICCID；Android ID、OAID、SSID、BSSID；系统设置、系统属性、设备型号、设备品牌、操作系统；IP地址、网络类型、运营商信息、Wi-Fi状态、Wi-Fi参数、Wi-Fi列表；软件安装列表。",
                                fontSize = 11.sp
                            )
                            
                            Text(
                                text = "使用目的：进行用户身份识别验证，保障产品功能的实现与安全稳定运行，实现网络链路的选择和优化，以提升用户体验。",
                                fontSize = 11.sp
                            )
                            
                            Button(
                                onClick = {
                                    // 打开支付宝隐私政策链接
                                    Toast.makeText(context, "打开支付宝隐私政策", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1677FF))
                            ) {
                                Text("查看身份验证 SDK 隐私说明", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 用户协议
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "用户协议",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "1. 我们承诺按照相关法律法规要求收集、使用您的个人信息\n2. 我们不会向第三方出售、出租或以其他方式披露您的个人信息\n3. 您有权随时撤回对个人信息处理的同意\n4. 如有疑问，请联系我们的客服",
                    fontSize = 14.sp
                )
            }
        }

        // 同意按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Checkbox(
                checked = isAgreed,
                onCheckedChange = { isAgreed = it }
            )
            
            Text(
                text = "我已阅读并同意《隐私政策》和《用户协议》",
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }

        // 操作按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "您已拒绝隐私政策", Toast.LENGTH_SHORT).show()
                    // 退出应用或返回
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF757575))
            ) {
                Text("拒绝")
            }
            
            Button(
                onClick = {
                    if (isAgreed) {
                        Toast.makeText(context, "您已同意隐私政策", Toast.LENGTH_SHORT).show()
                        // 继续使用应用
                    } else {
                        Toast.makeText(context, "请先同意隐私政策", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isAgreed,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("同意")
            }
        }
    }
}
