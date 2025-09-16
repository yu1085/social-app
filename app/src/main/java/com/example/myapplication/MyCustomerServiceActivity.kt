package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

class MyCustomerServiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCustomerServiceScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCustomerServiceScreen(
    onBackClick: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(getInitialMessages()) }
    
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
                        text = "我的客服",
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
            
            // 聊天内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // 日期和提示
                Text(
                    text = "今天 ${getCurrentTime()}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Text(
                    text = "机器人为您服务",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // 机器人欢迎消息
                RobotMessage(
                    message = "您好,请问有什么可以帮助您?",
                    avatar = "🤖"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 热门问题
                Text(
                    text = "热门问题",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                val hotQuestions = listOf(
                    "提现多久到账",
                    "为什么被禁止申请女神",
                    "为什么提示在其他账号认...",
                    "平台严禁的内容包括哪些"
                )
                
                hotQuestions.forEach { question ->
                    HotQuestionItem(
                        question = question,
                        onClick = {
                            // 处理热门问题点击
                            val newMessage = ChatMessage(
                                text = question,
                                isFromUser = true,
                                timestamp = getCurrentTime()
                            )
                            messages = messages + newMessage
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 机器人再次欢迎
                Text(
                    text = "机器人为您服务",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                RobotMessage(
                    message = "您好,请问有什么可以帮助您?",
                    avatar = "🤖"
                )
                
                // 显示聊天消息
                messages.forEach { message ->
                    if (message.isFromUser) {
                        UserMessage(message = message.text)
                    } else {
                        RobotMessage(message = message.text, avatar = "🤖")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 消息输入框
            MessageInputBox(
                messageText = messageText,
                onMessageTextChange = { messageText = it },
                onSendClick = {
                    if (messageText.isNotBlank()) {
                        val newMessage = ChatMessage(
                            text = messageText,
                            isFromUser = true,
                            timestamp = getCurrentTime()
                        )
                        messages = messages + newMessage
                        messageText = ""
                    }
                }
            )
        }
    }
}

@Composable
fun RobotMessage(
    message: String,
    avatar: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 机器人头像
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE3F2FD)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatar,
                fontSize = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 消息气泡
        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    Color(0xFFF5F5F5),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun UserMessage(
    message: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.End
    ) {
        // 消息气泡
        Box(
            modifier = Modifier
                .background(
                    Color(0xFF1976D2),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun HotQuestionItem(
    question: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = question,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "进入",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun MessageInputBox(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 语音输入按钮
        IconButton(
            onClick = { /* 处理语音输入 */ }
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "语音输入",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // 消息输入框
        OutlinedTextField(
            value = messageText,
            onValueChange = onMessageTextChange,
            placeholder = {
                Text(
                    text = "输入消息...",
                    color = Color.Gray
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1976D2),
                unfocusedBorderColor = Color(0xFFE0E0E0)
            ),
            shape = RoundedCornerShape(20.dp)
        )
        
        // 发送按钮
        Button(
            onClick = onSendClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1976D2)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "发送",
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String
)

fun getInitialMessages(): List<ChatMessage> {
    return emptyList()
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}
