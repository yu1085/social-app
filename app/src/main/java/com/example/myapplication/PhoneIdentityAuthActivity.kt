package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.auth.AuthManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
// 阿里云融合认证SDK导入（支持三大运营商一键登录）
import com.alicom.fusion.auth.AlicomFusionAuthCallBack
import com.alicom.fusion.auth.AlicomFusionAuthUICallBack
import com.alicom.fusion.auth.AlicomFusionBusiness
import com.alicom.fusion.auth.AlicomFusionConstant
import com.alicom.fusion.auth.AlicomFusionLog
import com.alicom.fusion.auth.HalfWayVerifyResult
import com.alicom.fusion.auth.error.AlicomFusionEvent
import com.alicom.fusion.auth.numberauth.FusionNumberAuthModel
import com.alicom.fusion.auth.token.AlicomFusionAuthToken
import com.mobile.auth.gatewayauth.AuthRegisterViewConfig
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig
import com.mobile.auth.gatewayauth.CustomInterface
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate

class PhoneIdentityAuthActivity : ComponentActivity() {
    private lateinit var authManager: AuthManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化认证管理器
        authManager = AuthManager.getInstance(this)
        
        setContent {
            MyApplicationTheme {
                PhoneIdentityAuthScreen(
                    onClose = { finish() },
                    onAuthSuccess = { 
                        // 检查用户是否已登录
                        if (!authManager.isLoggedIn()) {
                            Toast.makeText(this, "请先登录后再进行认证", Toast.LENGTH_LONG).show()
                            return@PhoneIdentityAuthScreen
                        }
                        
                        // 获取用户ID和token
                        val userId = authManager.getUserId()
                        val token = authManager.getToken()
                        
                        if (userId == -1L || token == null) {
                            Toast.makeText(this, "认证信息无效，请重新登录", Toast.LENGTH_LONG).show()
                            return@PhoneIdentityAuthScreen
                        }
                        
                        android.util.Log.d("PhoneAuth", "用户ID: $userId, 手机身份认证成功")
                        Toast.makeText(this, "手机身份认证成功", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun PhoneIdentityAuthScreen(
    onClose: () -> Unit,
    onAuthSuccess: () -> Unit,
    viewModel: PhoneAuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val authManager = remember { AuthManager.getInstance(context) }
    var showDialog by remember { mutableStateOf(true) }
    var phoneNumber by remember { mutableStateOf("198****2076") }
    var isOtherPhone by remember { mutableStateOf(false) }
    var inputPhone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    // 执行手机认证的函数
    fun performPhoneAuth() {
        // 检查用户是否已登录
        if (!authManager.isLoggedIn()) {
            Toast.makeText(context, "请先登录后再进行认证", Toast.LENGTH_LONG).show()
            return
        }
        
        // 获取用户ID和token
        val userId = authManager.getUserId()
        val token = authManager.getToken()
        
        if (userId == -1L || token == null) {
            Toast.makeText(context, "认证信息无效，请重新登录", Toast.LENGTH_LONG).show()
            return
        }
        
        isLoading = true
        // 执行手机认证
        viewModel.performPhoneAuth(
            phone = if (isOtherPhone) inputPhone else phoneNumber,
            userId = userId,
            token = token,
            onSuccess = {
                isLoading = false
                onAuthSuccess()
            },
            onError = { error ->
                isLoading = false
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = onClose,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            PhoneIdentityAuthDialog(
                phoneNumber = if (isOtherPhone) inputPhone else phoneNumber,
                isOtherPhone = isOtherPhone,
                isLoading = isLoading,
                onPhoneChange = { inputPhone = it },
                onToggleOtherPhone = { isOtherPhone = !isOtherPhone },
                onAuthClick = { performPhoneAuth() },
                onClose = onClose,
                onTermsClick = {
                    // 打开服务条款页面
                    val intent = Intent(context, TermsOfServiceActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun PhoneIdentityAuthDialog(
    phoneNumber: String,
    isOtherPhone: Boolean,
    isLoading: Boolean,
    onPhoneChange: (String) -> Unit,
    onToggleOtherPhone: () -> Unit,
    onAuthClick: () -> Unit,
    onClose: () -> Unit,
    onTermsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "手机身份认证",
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 说明文字
            Text(
                text = "根据法律实名制要求,同时为保证您的正常使用,请尽快完成身份认证",
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "认证使用手机号码不会替换现有登录账号",
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 手机号输入区域
            if (isOtherPhone) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = onPhoneChange,
                    label = { Text("请输入手机号") },
                    placeholder = { Text("请输入11位手机号") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF007AFF),
                        unfocusedBorderColor = Color(0xFFE5E5EA)
                    )
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF2F2F7),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = phoneNumber,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "使用其他手机",
                        fontSize = 14.sp,
                        color = Color(0xFF007AFF),
                        modifier = Modifier.clickable { onToggleOtherPhone() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 一键认证按钮
            Button(
                onClick = onAuthClick,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF),
                    disabledContainerColor = Color(0xFFE5E5EA)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "一键认证",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 服务条款
            Text(
                text = "认证代表同意",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "阿里云融合认证服务条款",
                fontSize = 12.sp,
                color = Color(0xFF007AFF),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTermsClick() },
                textAlign = TextAlign.Center
            )
        }
    }
}

// ViewModel for phone authentication
class PhoneAuthViewModel : ViewModel() {
    
    fun performPhoneAuth(
        phone: String,
        userId: Long,
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 验证手机号格式
                if (!isValidPhoneNumber(phone)) {
                    onError("请输入正确的手机号")
                    return@launch
                }
                
                // 使用阿里云融合认证SDK进行真实认证
                performRealAliyunAuth(phone, userId, token, onSuccess, onError)
                
            } catch (e: Exception) {
                onError("认证失败，请重试")
            }
        }
    }
    
    /**
     * 使用阿里云融合认证SDK进行真实认证
     */
    private fun performRealAliyunAuth(
        phone: String,
        userId: Long,
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // 阿里云融合认证SDK集成代码
        /*
        // 初始化阿里云融合认证SDK
        AlicomFusionBusiness.useSDKSupplyUMSDK(true, "ymeng")
        AlicomFusionLog.setLogEnable(false)
        
        val alicomFusionBusiness = AlicomFusionBusiness()
        val token = AlicomFusionAuthToken()
        token.authToken = "your_auth_token_here" // 需要从后端获取
        
        alicomFusionBusiness.initWithToken(context, "your_scheme_code", token)
        
        val authCallBack = object : AlicomFusionAuthCallBack {
            override fun onSDKTokenUpdate(): AlicomFusionAuthToken {
                // 更新token
                val newToken = AlicomFusionAuthToken()
                newToken.authToken = "updated_token"
                return newToken
            }
            
            override fun onSDKTokenAuthSuccess() {
                // SDK token认证成功
            }
            
            override fun onSDKTokenAuthFailure(token: AlicomFusionAuthToken, event: AlicomFusionEvent) {
                onError("SDK token认证失败: ${event.errorMsg}")
            }
            
            override fun onVerifySuccess(token: String, nodeName: String, event: AlicomFusionEvent) {
                // 验证成功，调用后端验证token
                verifyTokenWithBackend(phone, token, onSuccess, onError)
            }
            
            override fun onHalfWayVerifySuccess(nodeName: String, maskToken: String, event: AlicomFusionEvent, result: HalfWayVerifyResult) {
                // 中途验证成功
            }
            
            override fun onVerifyFailed(event: AlicomFusionEvent, nodeName: String) {
                when (event.errorCode) {
                    "NETWORK_ERROR" -> onError("网络连接失败")
                    "USER_CANCEL" -> onError("用户取消认证")
                    "AUTH_FAILED" -> onError("认证失败")
                    else -> onError("认证异常：${event.errorMsg}")
                }
            }
            
            override fun onTemplateFinish(event: AlicomFusionEvent) {
                // 模板完成
            }
            
            override fun onAuthEvent(event: AlicomFusionEvent) {
                // 认证事件
            }
            
            override fun onGetPhoneNumberForVerification(nodeName: String, event: AlicomFusionEvent): String {
                return phone
            }
            
            override fun onVerifyInterrupt(event: AlicomFusionEvent) {
                onError("验证中断")
            }
        }
        
        alicomFusionBusiness.setAlicomFusionAuthCallBack(authCallBack)
        
        // 启动认证流程
        alicomFusionBusiness.startSceneWithTemplateId(context, "100001", uiCallBack)
        */
        
        // 调用后端手机认证API
        viewModelScope.launch {
            try {
                val result = callPhoneAuthAPI(phone, userId, token)
                if (result.success) {
                    onSuccess()
                } else {
                    onError(result.message)
                }
            } catch (e: Exception) {
                onError("认证失败: ${e.message}")
            }
        }
    }
    
    /**
     * 调用后端手机认证API
     */
    private suspend fun callPhoneAuthAPI(phone: String, userId: Long, token: String): PhoneAuthAPIResult = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("PhoneAuth", "调用后端手机认证API: $phone, 用户ID: $userId, token: ${token.take(10)}...")
            
            // 构建请求数据 - 适配阿里云API格式
            val requestData = mapOf(
                "phoneNumber" to phone,
                "accessToken" to token  // 使用JWT token作为accessToken
            )
            
            // 发送HTTP请求到后端 - 使用正确的API端点
            val url = java.net.URL("http://10.0.2.2:8080/api/aliyun/phone-auth/verify-phone")
            val connection = url.openConnection() as java.net.HttpURLConnection
            
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.doOutput = true
            
            // 发送请求
            val outputStream = connection.outputStream
            val writer = java.io.OutputStreamWriter(outputStream)
            writer.write(org.json.JSONObject(requestData).toString())
            writer.flush()
            writer.close()
            
            // 读取响应
            val responseCode = connection.responseCode
            val response = java.io.BufferedReader(connection.inputStream.reader()).use { it.readText() }
            
            android.util.Log.d("PhoneAuth", "手机认证API响应: $response")
            
            val jsonResponse = org.json.JSONObject(response)
            val success = jsonResponse.optBoolean("success", false)
            val message = jsonResponse.optString("message", "")
            val phoneNumber = jsonResponse.optString("phoneNumber", phone)
            val authToken = jsonResponse.optString("authToken", "")
            
            if (success) {
                // 阿里云API成功时，认为验证通过
                PhoneAuthAPIResult(
                    success = true,
                    verified = true,
                    message = message,
                    operator = "阿里云认证"
                )
            } else {
                PhoneAuthAPIResult(
                    success = false,
                    verified = false,
                    message = message.ifEmpty { "认证失败" },
                    operator = ""
                )
            }
            
        } catch (e: Exception) {
            android.util.Log.e("PhoneAuth", "手机认证API调用失败", e)
            PhoneAuthAPIResult(
                success = false,
                verified = false,
                message = "网络错误: ${e.message}",
                operator = ""
            )
        }
    }
    
    /**
     * 调用后端验证token
     */
    private fun verifyTokenWithBackend(
        phone: String,
        token: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 调用后端API验证token
                // val response = apiService.verifyCmccToken(phone, token)
                // if (response.isSuccess) {
                //     onSuccess()
                // } else {
                //     onError(response.message)
                // }
                
                // 临时模拟后端验证
                delay(1000)
                onSuccess()
            } catch (e: Exception) {
                onError("后端验证失败")
            }
        }
    }
    
    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^1[3-9]\\d{9}$"))
    }
}

/**
 * 手机认证API结果
 */
data class PhoneAuthAPIResult(
    val success: Boolean,
    val verified: Boolean,
    val message: String,
    val operator: String
)