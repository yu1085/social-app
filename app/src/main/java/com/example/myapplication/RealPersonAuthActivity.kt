package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.camera.FaceCameraPreview
import com.example.myapplication.service.AliyunFaceAuthService
// import com.google.mlkit.vision.face.Face  // 已移除Google ML Kit
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class RealPersonAuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                RealPersonAuthScreen(
                    onClose = { finish() },
                    onStartAuth = { 
                        Toast.makeText(this, "开始真人认证", Toast.LENGTH_SHORT).show()
                        // TODO: 实现真实的认证逻辑
                    }
                )
            }
        }
    }
}

@Composable
fun RealPersonAuthScreen(
    onClose: () -> Unit,
    onStartAuth: () -> Unit,
    viewModel: RealPersonAuthViewModel = viewModel()
) {
    var isAgreementChecked by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var detectedFaces by remember { mutableStateOf<List<Any>>(emptyList()) }
    
    if (showCamera) {
        // 相机认证界面
        CameraAuthScreen(
            onBack = { showCamera = false },
            onAuthSuccess = {
                showCamera = false
                onStartAuth()
            },
            viewModel = viewModel
        )
    } else {
        // 说明界面
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 顶部状态栏区域
            TopStatusBar()
            
            // 主要内容区域
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // 顶部导航栏
                    TopAppBar(onClose = onClose)
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 正确示范区域
                    CorrectExampleSection()
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 认证要求
                    AuthRequirementSection()
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 错误示例区域
                    IncorrectExamplesSection()
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // 去认证按钮
                    AuthButton(
                        enabled = isAgreementChecked,
                        onClick = { showCamera = true }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 协议同意区域
                    AgreementSection(
                        isChecked = isAgreementChecked,
                        onCheckedChange = { isAgreementChecked = it }
                    )
                }
            }
        }
    }
}

@Composable
fun CameraAuthScreen(
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit,
    viewModel: RealPersonAuthViewModel
) {
    val context = LocalContext.current
    var isProcessing by remember { mutableStateOf(false) }
    var authResult by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 顶部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    text = "返回",
                    color = Color.White
                )
            }
            
            Text(
                text = "真人认证",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.width(60.dp))
        }
        
        // 相机预览区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            FaceCameraPreview(
                onFaceDetected = { hasFace ->
                    // 处理检测到的人脸
                },
                onPhotoTaken = { bitmap ->
                    isProcessing = true
                    viewModel.performFaceAuth(bitmap) { result ->
                        isProcessing = false
                        authResult = result
                        if (result.contains("成功")) {
                            onAuthSuccess()
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // 处理状态显示
            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "正在验证身份...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            
            // 认证结果显示
            authResult?.let { result ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (result.contains("成功")) "认证成功" else "认证失败",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (result.contains("成功")) Color.Green else Color.Red
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = result,
                                fontSize = 14.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = {
                                    if (result.contains("成功")) {
                                        onAuthSuccess()
                                    } else {
                                        authResult = null
                                    }
                                }
                            ) {
                                Text(
                                    text = if (result.contains("成功")) "完成" else "重试",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopStatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "23:22",
            fontSize = 16.sp,
            color = Color.Black
        )
        
        Icon(
            painter = painterResource(id = android.R.drawable.ic_input_add),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Gray
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_dialog_info),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Black
            )
            Icon(
                painter = painterResource(id = android.R.drawable.ic_dialog_info),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Black
            )
            Text(
                text = "324",
                fontSize = 12.sp,
                color = Color.Green
            )
        }
    }
}

@Composable
private fun TopAppBar(onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧空白占位
        Spacer(modifier = Modifier.width(24.dp))
        
        Text(
            text = "真人认证",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        IconButton(
            onClick = onClose,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "关闭",
                tint = Color.Gray
            )
        }
    }
}

@Composable
private fun CorrectExampleSection() {
    Column {
        Text(
            text = "正确示范",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 示例图片区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
        ) {
            // 背景图片（使用占位符）
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F8F8))
            )
            
            // 标签覆盖层
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // 正面全脸标签
                ExampleLabel(
                    text = "正面全脸",
                    color = Color(0xFF2196F3),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )
                
                // 化淡妆标签
                ExampleLabel(
                    text = "化淡妆",
                    color = Color(0xFFFF9800),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
                
                // 光线明亮标签
                ExampleLabel(
                    text = "光线明亮",
                    color = Color(0xFFF44336),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(12.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "本人五官清晰的认证照将会获得更多的",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 推荐标签
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFFF4444),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "推荐",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ExampleLabel(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color.White, CircleShape)
            )
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AuthRequirementSection() {
    Column {
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "认证要求: 每人仅可认证一个账号",
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
    }
}

@Composable
private fun IncorrectExamplesSection() {
    Column {
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "以下不能通过认证",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 错误示例网格
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IncorrectExampleItem(
                label = "躺卧拍摄",
                modifier = Modifier.weight(1f)
            )
            
            IncorrectExampleItem(
                label = "面部遮挡",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IncorrectExampleItem(
                label = "光线昏暗",
                modifier = Modifier.weight(1f)
            )
            
            IncorrectExampleItem(
                label = "多人认证",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun IncorrectExampleItem(
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color(0xFFF0F0F0),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // 红色X图标
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFFFF4444), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✕",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AuthButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3),
            disabledContainerColor = Color(0xFFE0E0E0)
        )
    ) {
        Text(
            text = "去认证",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (enabled) Color.White else Color(0xFF999999)
        )
    }
}

@Composable
private fun AgreementSection(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF2196F3),
                uncheckedColor = Color(0xFFE0E0E0)
            )
        )
        
        Text(
            text = "我已阅读《身份信息认证协议》并同意授权采集我的面部信息, 仅用于系统对比, 不会对外展示",
            fontSize = 12.sp,
            color = Color(0xFF666666),
            lineHeight = 16.sp
        )
    }
}

/**
 * 真人认证ViewModel
 */
class RealPersonAuthViewModel : ViewModel() {
    
    private val faceAuthService = AliyunFaceAuthService.getInstance()
    
    /**
     * 执行人脸认证
     */
    fun performFaceAuth(
        bitmap: android.graphics.Bitmap,
        onResult: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = faceAuthService.performRealPersonAuth(bitmap)
                
                if (result.success) {
                    onResult("真人认证成功！您的身份已验证。")
                } else {
                    onResult("认证失败：${result.message}")
                }
            } catch (e: Exception) {
                onResult("认证失败：${e.message}")
            }
        }
    }
}
