




package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.PriceSettingsViewModel

/**
 * 价格设置页面
 */
class PriceSettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val viewModel: PriceSettingsViewModel = viewModel(
                    factory = viewModelFactory {
                        addInitializer(PriceSettingsViewModel::class) {
                            PriceSettingsViewModel(this@PriceSettingsActivity)
                        }
                    }
                )

                PriceSettingsScreen(
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    onSaveSuccess = {
                        Toast.makeText(this, "价格设置已保存", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriceSettingsScreen(
    viewModel: PriceSettingsViewModel,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "设置来电及价格",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF333333),
                    navigationIconContentColor = Color(0xFF333333)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F8FA))
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // 说明文字
                Text(
                    text = "设置你的通话接听权限和收费标准",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 语音通话设置
                CallSettingCard(
                    title = "语音通话",
                    enabled = uiState.voiceCallEnabled,
                    price = uiState.voiceCallPrice,
                    onEnabledChange = { viewModel.updateVoiceCallEnabled(it) },
                    onPriceChange = { viewModel.updateVoiceCallPrice(it) },
                    iconRes = R.drawable.ic_call_settings
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 视频通话设置
                CallSettingCard(
                    title = "视频通话",
                    enabled = uiState.videoCallEnabled,
                    price = uiState.videoCallPrice,
                    onEnabledChange = { viewModel.updateVideoCallEnabled(it) },
                    onPriceChange = { viewModel.updateVideoCallPrice(it) },
                    iconRes = R.drawable.ic_call_settings
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 价格说明
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_info),
                            contentDescription = null,
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "设置价格后,他人给你打电话将按时长收费,费用从对方账户扣除并转入你的账户",
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 保存按钮
                Button(
                    onClick = {
                        viewModel.saveSettings(
                            onSuccess = onSaveSuccess,
                            onError = { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF498AFE)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "保存设置",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // 加载状态
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            // 错误提示
            uiState.error?.let { error ->
                LaunchedEffect(error) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }
    }
}

@Composable
private fun CallSettingCard(
    title: String,
    enabled: Boolean,
    price: Double,
    onEnabledChange: (Boolean) -> Unit,
    onPriceChange: (Double) -> Unit,
    iconRes: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和开关
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Switch组件 - 使用更大的点击区域
                Switch(
                    checked = enabled,
                    onCheckedChange = { newValue ->
                        android.util.Log.d("PriceSettings", "$title 开关状态变更: $enabled -> $newValue")
                        android.util.Log.d("PriceSettings", "调用onEnabledChange: $newValue")
                        onEnabledChange(newValue)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF498AFE),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE0E0E0)
                    )
                )
            }

            // 价格设置区域(仅在开启时显示)
            if (enabled) {
                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color(0xFFE0E0E0))

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "价格:",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // 价格输入
                    OutlinedTextField(
                        value = price.toInt().toString(),
                        onValueChange = {
                            val newPrice = it.toDoubleOrNull() ?: 0.0
                            if (newPrice >= 0 && newPrice <= 1000) {
                                onPriceChange(newPrice)
                            }
                        },
                        label = { Text("元/分钟") },
                        modifier = Modifier.width(120.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF498AFE),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            disabledBorderColor = Color(0xFFE0E0E0)
                        ),
                        enabled = enabled
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 金币图标
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.ic_coin),
                        contentDescription = "金币",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 价格范围提示
                Text(
                    text = "价格范围: 0-1000元/分钟(0表示免费)",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            } else {
                // 关闭时的提示
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "关闭后将拒绝所有${title}请求",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }
        }
    }
}
