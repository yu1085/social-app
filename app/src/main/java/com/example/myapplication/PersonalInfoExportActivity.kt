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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PersonalInfoExportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalInfoExportScreen(
                onBackClick = { finish() },
                onNextStepClick = { email ->
                    android.widget.Toast.makeText(this, "个人信息已发送到: $email", android.widget.Toast.LENGTH_SHORT).show()
                    finish()
                },
                context = this
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoExportScreen(
    onBackClick: () -> Unit,
    onNextStepClick: (String) -> Unit,
    context: android.content.Context
) {
    var email by remember { mutableStateOf("") }

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
                        text = "个人信息浏览与导出",
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
                // 说明信息
                InfoBox()
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 邮箱输入区域
                EmailInputSection(
                    email = email,
                    onEmailChange = { email = it }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 下一步按钮
                Button(
                    onClick = {
                        if (email.isNotEmpty()) {
                            onNextStepClick(email)
                        } else {
                            android.widget.Toast.makeText(
                                context,
                                "请输入邮箱地址",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    enabled = email.isNotEmpty()
                ) {
                    Text(
                        text = "下一步",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (email.isNotEmpty()) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun InfoBox() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "个人信息导出头像、昵称、微信号等个人信息将整理成文件并发送到你的邮箱",
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun EmailInputSection(
    email: String,
    onEmailChange: (String) -> Unit
) {
    Column {
        // 邮箱标签和图标
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "邮箱",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "邮箱地址",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        
        // 邮箱输入框
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = {
                Text(
                    text = "请输入邮箱地址",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
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
