package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ChangePhoneActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChangePhoneScreen(
                onBackClick = { finish() },
                onSendCodeClick = { phoneNumber ->
                    // 处理发送验证码
                    android.widget.Toast.makeText(this, "已发送验证码到: $phoneNumber", android.widget.Toast.LENGTH_SHORT).show()
                },
                onConfirmClick = { phoneNumber, code ->
                    // 处理确认更换
                    android.widget.Toast.makeText(this, "更换手机号成功: $phoneNumber", android.widget.Toast.LENGTH_SHORT).show()
                    finish()
                },
                context = this
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePhoneScreen(
    onBackClick: () -> Unit,
    onSendCodeClick: (String) -> Unit,
    onConfirmClick: (String, String) -> Unit,
    context: android.content.Context
) {
    var phoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(0) }

    // 倒计时逻辑
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            kotlinx.coroutines.delay(1000)
            countdown--
        }
    }

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
                        text = "更换新手机号",
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
                // 说明文字
                Text(
                    text = "请输入新的手机号码，我们将发送验证码到该号码",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Start
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 手机号输入
                PhoneNumberInput(
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 验证码输入
                VerificationCodeInput(
                    verificationCode = verificationCode,
                    onVerificationCodeChange = { verificationCode = it },
                    isCodeSent = isCodeSent,
                    countdown = countdown,
                    onSendCodeClick = {
                        if (phoneNumber.isNotEmpty() && phoneNumber.length == 11) {
                            onSendCodeClick(phoneNumber)
                            isCodeSent = true
                            countdown = 60
                        } else {
                            android.widget.Toast.makeText(
                                context,
                                "请输入正确的手机号",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 确认按钮
                Button(
                    onClick = {
                        if (phoneNumber.isNotEmpty() && verificationCode.isNotEmpty()) {
                            onConfirmClick(phoneNumber, verificationCode)
                        } else {
                            android.widget.Toast.makeText(
                                context,
                                "请填写完整信息",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
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
                        text = "确认更换",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 温馨提示
                Text(
                    text = "温馨提示：更换手机号后，原手机号将无法登录此账号",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun PhoneNumberInput(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit
) {
    Column {
        Text(
            text = "新手机号",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            placeholder = {
                Text(
                    text = "请输入11位手机号",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF007AFF),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
    }
}

@Composable
fun VerificationCodeInput(
    verificationCode: String,
    onVerificationCodeChange: (String) -> Unit,
    isCodeSent: Boolean,
    countdown: Int,
    onSendCodeClick: () -> Unit
) {
    Column {
        Text(
            text = "验证码",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = verificationCode,
                onValueChange = onVerificationCodeChange,
                placeholder = {
                    Text(
                        text = "请输入验证码",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            
            Button(
                onClick = onSendCodeClick,
                enabled = isCodeSent.not() && countdown == 0,
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCodeSent && countdown > 0) Color.Gray else Color(0xFF007AFF),
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(
                    text = if (countdown > 0) "${countdown}s" else "发送验证码",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}
