package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.service.ProfileService
import com.example.myapplication.service.UserProfile
import com.example.myapplication.auth.AuthManager
import kotlinx.coroutines.launch

/**
 * 我的资料编辑页面
 * 根据设计稿实现，包含基本资料和更多资料的编辑功能
 */
class MyProfileEditActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                MyProfileEditScreen(
                    onBackClick = { finish() },
                    onSaveClick = { 
                        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun MyProfileEditScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val profileService = remember { ProfileService.getInstance() }
    val scope = rememberCoroutineScope()
    
    // 用户资料状态
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var profileData by remember { mutableStateOf(mutableMapOf<String, Any>()) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // 获取真实的认证token和用户ID
    val authManager = AuthManager.getInstance(context)
    val token = authManager.getToken()
    val userId = authManager.getUserId()
    
    // 保存函数
    fun saveProfile() {
        Log.d("ProfileEdit", "=== 开始保存用户资料 ===")
        Log.d("ProfileEdit", "当前用户状态:")
        Log.d("ProfileEdit", "- 是否已登录: ${authManager.isLoggedIn()}")
        Log.d("ProfileEdit", "- Token: ${token?.take(20)}...")
        Log.d("ProfileEdit", "- Token长度: ${token?.length ?: 0}")
        Log.d("ProfileEdit", "- 用户ID: $userId")
        Log.d("ProfileEdit", "- 资料数据字段数: ${profileData.size}")
        
        // 详细记录资料数据
        profileData.forEach { (key, value) ->
            Log.d("ProfileEdit", "资料字段: $key = $value (类型: ${value::class.simpleName})")
        }
        
        // 检查用户是否已登录
        if (!authManager.isLoggedIn()) {
            Log.e("ProfileEdit", "用户未登录，无法保存资料")
            Log.e("ProfileEdit", "认证状态: ${authManager.isLoggedIn()}")
            Log.e("ProfileEdit", "Token调试信息: ${authManager.getTokenDebugInfo()}")
            errorMessage = "请先登录后再编辑资料"
            showErrorDialog = true
            return
        }
        
        // 验证Token格式
        if (!authManager.isTokenFormatValid()) {
            Log.e("ProfileEdit", "Token格式无效")
            Log.e("ProfileEdit", "Token调试信息: ${authManager.getTokenDebugInfo()}")
            errorMessage = "Token格式错误，请重新登录"
            showErrorDialog = true
            return
        }
        
        // 验证Token有效性
        if (!authManager.isTokenValid()) {
            Log.e("ProfileEdit", "Token无效或已过期")
            Log.e("ProfileEdit", "Token调试信息: ${authManager.getTokenDebugInfo()}")
            
            // 尝试刷新Token
            Log.d("ProfileEdit", "尝试刷新Token")
            if (authManager.validateAndRefreshToken()) {
                Log.d("ProfileEdit", "Token刷新成功，继续保存")
            } else {
                Log.e("ProfileEdit", "Token刷新失败，需要重新登录")
                errorMessage = "登录已过期，请重新登录"
                showErrorDialog = true
                return
            }
        }
        
        // 测试Token有效性（可选，用于调试）
        Log.d("ProfileEdit", "测试Token有效性")
        scope.launch {
            try {
                val testResult = profileService.testToken(token)
                Log.d("ProfileEdit", "Token测试结果: $testResult")
                
                if (testResult["success"] == false) {
                    Log.w("ProfileEdit", "Token测试失败: ${testResult["error"]}")
                } else {
                    Log.d("ProfileEdit", "Token测试成功: 有效=${testResult["isValid"]}, 用户ID=${testResult["userId"]}")
                }
            } catch (e: Exception) {
                Log.e("ProfileEdit", "Token测试异常", e)
            }
        }
        
        // 检查token和userId是否有效
        if (token == null || userId == -1L) {
            Log.e("ProfileEdit", "认证信息无效")
            Log.e("ProfileEdit", "- Token: $token")
            Log.e("ProfileEdit", "- Token是否为空: ${token.isNullOrEmpty()}")
            Log.e("ProfileEdit", "- 用户ID: $userId")
            Log.e("ProfileEdit", "- 用户ID是否有效: ${userId != -1L}")
            Log.e("ProfileEdit", "Token调试信息: ${authManager.getTokenDebugInfo()}")
            errorMessage = "认证信息无效，请重新登录"
            showErrorDialog = true
            return
        }
        
        Log.d("ProfileEdit", "认证信息验证通过，开始调用API")
        
        isSaving = true
        Log.d("ProfileEdit", "设置保存状态为true，开始协程")
        scope.launch {
            try {
                Log.d("ProfileEdit", "调用ProfileService.updateUserProfile")
                Log.d("ProfileEdit", "参数: userId=$userId, token=${token?.take(10)}..., profileData=${profileData.size}个字段")
                
                val result = profileService.updateUserProfile(userId, token, profileData)
                
                Log.d("ProfileEdit", "API调用完成")
                Log.d("ProfileEdit", "结果: success=${result.success}, profile=${result.profile != null}, error=${result.error}")
                Log.d("ProfileEdit", "服务器保存状态: ${result.isServerSaved}")
                
                if (result.success && result.profile != null) {
                    userProfile = result.profile
                    Log.d("ProfileEdit", "用户资料保存成功")
                    Log.d("ProfileEdit", "更新后的用户资料: ${result.profile.nickname}")
                    
                    // 检查是否真的保存到服务器
                    if (result.isServerSaved == true) {
                        Log.d("ProfileEdit", "资料已成功保存到服务器")
                        showSuccessDialog = true
                        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.w("ProfileEdit", "资料只保存到本地，服务器连接失败")
                        // 只保存到本地
                        Toast.makeText(context, "已保存到本地（服务器连接失败）", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("ProfileEdit", "更新用户资料失败")
                    Log.e("ProfileEdit", "错误信息: ${result.error}")
                    Log.e("ProfileEdit", "成功状态: ${result.success}")
                    Log.e("ProfileEdit", "资料对象: ${result.profile}")
                    errorMessage = result.error ?: "保存失败，请重试"
                    showErrorDialog = true
                }
            } catch (e: Exception) {
                Log.e("ProfileEdit", "保存用户资料时发生异常", e)
                Log.e("ProfileEdit", "异常类型: ${e::class.simpleName}")
                Log.e("ProfileEdit", "异常消息: ${e.message}")
                Log.e("ProfileEdit", "异常堆栈: ${e.stackTrace.joinToString("\n")}")
                errorMessage = "网络错误，请检查网络连接后重试"
                showErrorDialog = true
            } finally {
                Log.d("ProfileEdit", "保存操作完成，设置保存状态为false")
                isSaving = false
            }
        }
    }
    
    // 加载用户资料
    LaunchedEffect(Unit) {
        Log.d("ProfileEdit", "=== 开始加载用户资料 ===")
        Log.d("ProfileEdit", "当前认证状态:")
        Log.d("ProfileEdit", "- 是否已登录: ${authManager.isLoggedIn()}")
        Log.d("ProfileEdit", "- Token: ${token?.take(20)}...")
        Log.d("ProfileEdit", "- Token长度: ${token?.length ?: 0}")
        Log.d("ProfileEdit", "- 用户ID: $userId")
        
        val result = profileService.getUserProfile(token)
        
        Log.d("ProfileEdit", "获取用户资料API调用完成")
        Log.d("ProfileEdit", "结果: success=${result.success}, profile=${result.profile != null}, error=${result.error}")
        
        if (result.success && result.profile != null) {
            userProfile = result.profile
            Log.d("ProfileEdit", "用户资料加载成功: ${result.profile.nickname}")
            Log.d("ProfileEdit", "用户资料详情:")
            Log.d("ProfileEdit", "- ID: ${result.profile.id}")
            Log.d("ProfileEdit", "- 昵称: ${result.profile.nickname}")
            Log.d("ProfileEdit", "- 性别: ${result.profile.gender}")
            Log.d("ProfileEdit", "- 邮箱: ${result.profile.email}")
            Log.d("ProfileEdit", "- 手机: ${result.profile.phone}")
            
            // 初始化profileData
            result.profile.let { profile ->
                Log.d("ProfileEdit", "初始化profileData字段")
                profileData["nickname"] = profile.nickname
                profileData["gender"] = profile.gender
                profileData["bio"] = profile.bio
                profileData["birthday"] = profile.birthday
                profileData["location"] = profile.location
                profileData["height"] = profile.height
                profileData["weight"] = profile.weight
                profileData["income"] = profile.income
                profileData["education"] = profile.education
                profileData["city"] = profile.city
                profileData["hometown"] = profile.hometown
                profileData["hobbies"] = profile.hobbies
                profileData["bloodType"] = profile.bloodType
                profileData["relationshipStatus"] = profile.relationshipStatus
                profileData["occupation"] = profile.occupation
                profileData["residenceStatus"] = profile.residenceStatus
                profileData["houseOwnership"] = profile.houseOwnership
                profileData["carOwnership"] = profile.carOwnership
                profileData["smoking"] = profile.smoking
                profileData["drinking"] = profile.drinking
                
                Log.d("ProfileEdit", "profileData初始化完成，字段数: ${profileData.size}")
            }
        } else {
            // 显示错误信息，但仍然允许编辑
            Log.e("ProfileEdit", "获取用户资料失败")
            Log.e("ProfileEdit", "错误信息: ${result.error}")
            Log.e("ProfileEdit", "成功状态: ${result.success}")
            Log.e("ProfileEdit", "资料对象: ${result.profile}")
            
            Log.d("ProfileEdit", "初始化空的profileData，让用户可以输入")
            // 初始化空的profileData，让用户可以输入
            profileData["nickname"] = ""
            profileData["gender"] = "男"
            profileData["bio"] = ""
            profileData["birthday"] = ""
            profileData["location"] = ""
            profileData["height"] = 0
            profileData["weight"] = 0
            profileData["income"] = ""
            profileData["education"] = ""
            profileData["city"] = ""
            profileData["hometown"] = ""
            profileData["hobbies"] = ""
            profileData["bloodType"] = ""
            profileData["relationshipStatus"] = ""
            profileData["occupation"] = ""
            profileData["residenceStatus"] = ""
            profileData["houseOwnership"] = false
            profileData["carOwnership"] = false
            profileData["smoking"] = false
            profileData["drinking"] = false
            
            Log.d("ProfileEdit", "空profileData初始化完成，字段数: ${profileData.size}")
        }
        
        Log.d("ProfileEdit", "设置加载状态为false")
        isLoading = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部导航栏
        TopNavigationBar(
            onBackClick = { onBackClick() },
            onSaveClick = { saveProfile() },
            isSaving = isSaving
        )
        
        if (isLoading) {
            // 加载状态
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // 主要内容区域
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // 编辑相册按钮
                EditAlbumButton()
                
                // 基本资料区域
                BasicInfoSection(
                    userProfile = userProfile,
                    profileData = profileData,
                    onProfileUpdate = { field, value ->
                        profileData[field] = value
                    }
                )
                
                // 更多资料区域
                MoreInfoSection(
                    userProfile = userProfile,
                    profileData = profileData,
                    onProfileUpdate = { field, value ->
                        profileData[field] = value
                    }
                )
                
                // 标签区域
                TagsSection()
                
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
    
    // 成功对话框
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("保存成功") },
            text = { Text("您的资料已成功保存") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showSuccessDialog = false
                        onSaveClick()
                    }
                ) {
                    Text("确定")
                }
            }
        )
    }
    
    // 错误对话框
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { 
                Text(
                    text = when {
                        errorMessage.contains("认证") || errorMessage.contains("登录") -> "认证失败"
                        errorMessage.contains("网络") -> "网络错误"
                        errorMessage.contains("权限") -> "权限不足"
                        else -> "保存失败"
                    }
                )
            },
            text = { 
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showErrorDialog = false
                        // 如果是认证错误，可以尝试重新保存
                        if (errorMessage.contains("认证") || errorMessage.contains("登录")) {
                            Log.d("ProfileEdit", "用户选择重试保存")
                            saveProfile()
                        }
                    }
                ) {
                    Text(
                        text = when {
                            errorMessage.contains("认证") || errorMessage.contains("登录") -> "重新登录"
                            else -> "重试"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}
}

@Composable
private fun TopNavigationBar(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    isSaving: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回按钮
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black
            )
        }
        
        // 页面标题
        Text(
            text = "我的资料",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        // 保存按钮
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color(0xFF2196F3)
            )
        } else {
            TextButton(
                onClick = onSaveClick
            ) {
                Text(
                    text = "保存",
                    fontSize = 16.sp,
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}

@Composable
private fun EditAlbumButton() {
    Button(
        onClick = { 
            // TODO: 跳转到编辑相册页面
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "编辑相册",
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Composable
private fun BasicInfoSection(
    userProfile: UserProfile?,
    profileData: MutableMap<String, Any>,
    onProfileUpdate: (String, Any) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "基本资料",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 昵称
        ProfileEditItem(
            label = "昵称",
            value = profileData["nickname"]?.toString() ?: "",
            placeholder = profileData["nickname"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("nickname", newValue) }
        )
        
        // 性别
        ProfileEditItem(
            label = "性别",
            value = profileData["gender"]?.toString() ?: "",
            placeholder = profileData["gender"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("gender", newValue) }
        )
        
        // 个性签名
        ProfileEditItem(
            label = "个性签名",
            value = profileData["bio"]?.toString() ?: "",
            placeholder = profileData["bio"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("bio", newValue) }
        )
        
        // 生日
        ProfileEditItem(
            label = "生日",
            value = profileData["birthday"]?.toString() ?: "",
            placeholder = profileData["birthday"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("birthday", newValue) }
        )
        
        // 所在城市
        ProfileEditItem(
            label = "所在城市",
            value = profileData["location"]?.toString() ?: "",
            placeholder = profileData["location"]?.toString().isNullOrEmpty(),
            icon = R.drawable.ic_location,
            onValueChange = { newValue -> onProfileUpdate("location", newValue) }
        )
        
        // 身高
        ProfileEditItem(
            label = "身高",
            value = if ((profileData["height"] as? Number)?.toInt() ?: 0 > 0) "${profileData["height"]}cm" else "",
            placeholder = ((profileData["height"] as? Number)?.toInt() ?: 0) == 0,
            onValueChange = { newValue -> 
                val height = newValue.replace("cm", "").trim().toIntOrNull() ?: 0
                onProfileUpdate("height", height)
            }
        )
        
        // 体重
        ProfileEditItem(
            label = "体重",
            value = if ((profileData["weight"] as? Number)?.toInt() ?: 0 > 0) "${profileData["weight"]}kg" else "",
            placeholder = ((profileData["weight"] as? Number)?.toInt() ?: 0) == 0,
            onValueChange = { newValue -> 
                val weight = newValue.replace("kg", "").trim().toIntOrNull() ?: 0
                onProfileUpdate("weight", weight)
            }
        )
        
        // 情感状态
        ProfileEditItem(
            label = "情感状态",
            value = profileData["relationshipStatus"]?.toString() ?: "",
            placeholder = profileData["relationshipStatus"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("relationshipStatus", newValue) }
        )
        
        // 年收入
        ProfileEditItem(
            label = "年收入",
            value = profileData["income"]?.toString() ?: "",
            placeholder = profileData["income"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("income", newValue) }
        )
        
        // 职业
        ProfileEditItem(
            label = "职业",
            value = profileData["occupation"]?.toString() ?: "",
            placeholder = profileData["occupation"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("occupation", newValue) }
        )
    }
}

@Composable
private fun MoreInfoSection(
    userProfile: UserProfile?,
    profileData: MutableMap<String, Any>,
    onProfileUpdate: (String, Any) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "更多资料",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 家乡
        ProfileEditItem(
            label = "家乡",
            value = profileData["hometown"]?.toString() ?: "",
            placeholder = profileData["hometown"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("hometown", newValue) }
        )
        
        // 学历
        ProfileEditItem(
            label = "学历",
            value = profileData["education"]?.toString() ?: "",
            placeholder = profileData["education"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("education", newValue) }
        )
        
        // 居住城市
        ProfileEditItem(
            label = "居住城市",
            value = profileData["city"]?.toString() ?: "",
            placeholder = profileData["city"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("city", newValue) }
        )
        
        // 居住情况
        ProfileEditItem(
            label = "居住情况",
            value = profileData["residenceStatus"]?.toString() ?: "",
            placeholder = profileData["residenceStatus"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("residenceStatus", newValue) }
        )
        
        // 是否购房
        ProfileEditItem(
            label = "是否购房",
            value = when (profileData["houseOwnership"] as? Boolean) {
                true -> "已购房"
                false -> "未购房"
                else -> ""
            },
            placeholder = profileData["houseOwnership"] == null,
            onValueChange = { newValue -> 
                val isOwned = when (newValue.lowercase()) {
                    "是", "已购房", "true", "1" -> true
                    "否", "未购车", "false", "0" -> false
                    else -> !(profileData["houseOwnership"] as? Boolean ?: false)
                }
                onProfileUpdate("houseOwnership", isOwned)
            }
        )
        
        // 是否购车
        ProfileEditItem(
            label = "是否购车",
            value = when (profileData["carOwnership"] as? Boolean) {
                true -> "已购车"
                false -> "未购车"
                else -> ""
            },
            placeholder = profileData["carOwnership"] == null,
            onValueChange = { newValue -> 
                val hasCar = when (newValue.lowercase()) {
                    "是", "已购车", "true", "1" -> true
                    "否", "未购车", "false", "0" -> false
                    else -> !(profileData["carOwnership"] as? Boolean ?: false)
                }
                onProfileUpdate("carOwnership", hasCar)
            }
        )
        
        // 兴趣爱好
        ProfileEditItem(
            label = "兴趣爱好",
            value = profileData["hobbies"]?.toString() ?: "",
            placeholder = profileData["hobbies"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("hobbies", newValue) }
        )
        
        // 血型
        ProfileEditItem(
            label = "血型",
            value = profileData["bloodType"]?.toString() ?: "",
            placeholder = profileData["bloodType"]?.toString().isNullOrEmpty(),
            onValueChange = { newValue -> onProfileUpdate("bloodType", newValue) }
        )
        
        // 是否吸烟
        ProfileEditItem(
            label = "是否吸烟",
            value = when (profileData["smoking"] as? Boolean) {
                true -> "吸烟"
                false -> "不吸烟"
                else -> ""
            },
            placeholder = profileData["smoking"] == null,
            onValueChange = { newValue -> 
                val isSmoking = when (newValue.lowercase()) {
                    "是", "吸烟", "true", "1" -> true
                    "否", "不吸烟", "false", "0" -> false
                    else -> !(profileData["smoking"] as? Boolean ?: false)
                }
                onProfileUpdate("smoking", isSmoking)
            }
        )
        
        // 是否饮酒
        ProfileEditItem(
            label = "是否饮酒",
            value = when (profileData["drinking"] as? Boolean) {
                true -> "饮酒"
                false -> "不饮酒"
                else -> ""
            },
            placeholder = profileData["drinking"] == null,
            onValueChange = { newValue -> 
                val isDrinking = when (newValue.lowercase()) {
                    "是", "饮酒", "true", "1" -> true
                    "否", "不饮酒", "false", "0" -> false
                    else -> !(profileData["drinking"] as? Boolean ?: false)
                }
                onProfileUpdate("drinking", isDrinking)
            }
        )
    }
}

@Composable
private fun TagsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "标签",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 添加标签按钮
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF2196F3), RoundedCornerShape(30.dp))
                    .clickable { /* TODO: 添加标签 */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加标签",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "添加标签, 让更懂你的人找到你",
                fontSize = 14.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

@Composable
private fun ProfileEditItem(
    label: String,
    value: String,
    placeholder: Boolean = false,
    icon: Int? = null,
    onValueChange: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editValue by remember { mutableStateOf(value) }
    
    // 当value变化时更新editValue
    LaunchedEffect(value) {
        editValue = value
    }
    
    if (isEditing) {
        // 编辑模式
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = editValue,
                    onValueChange = { editValue = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { 
                        Text(
                            text = if (placeholder) "请输入$label" else "",
                            color = Color(0xFF999999)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedBorderColor = Color(0xFFE0E0E0)
                    ),
                    singleLine = true,
                    keyboardOptions = when {
                        label.contains("身高") || label.contains("体重") -> KeyboardOptions(keyboardType = KeyboardType.Number)
                        label.contains("邮箱") -> KeyboardOptions(keyboardType = KeyboardType.Email)
                        else -> KeyboardOptions(keyboardType = KeyboardType.Text)
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 保存按钮
                IconButton(
                    onClick = {
                        if (editValue.isNotBlank()) {
                            onValueChange(editValue)
                        }
                        isEditing = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "保存",
                        tint = Color(0xFF4CAF50)
                    )
                }
                
                // 取消按钮
                IconButton(
                    onClick = {
                        isEditing = false
                        editValue = value
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "取消",
                        tint = Color(0xFFF44336)
                    )
                }
            }
        }
    } else {
        // 显示模式
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isEditing = true }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Text(
                text = value,
                fontSize = 16.sp,
                color = if (placeholder) Color(0xFF999999) else Color.Black,
                modifier = Modifier.weight(2f)
            )
            
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "编辑",
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF2196F3)
            )
        }
    }
}

