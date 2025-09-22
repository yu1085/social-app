package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.service.ProfileService
import com.example.myapplication.service.UserProfile
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
    val profileService = remember { ProfileService.getInstance() }
    val scope = rememberCoroutineScope()
    
    // 用户资料状态
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var profileData by remember { mutableStateOf(mutableMapOf<String, Any>()) }
    
    // 模拟JWT token（实际应用中应该从SharedPreferences或安全存储中获取）
    val token = "your_jwt_token_here"
    val userId = 1L // 模拟用户ID
    
    // 保存函数
    val saveProfile = {
        isSaving = true
        // 模拟保存过程
        scope.launch {
            try {
                val result = profileService.updateUserProfile(userId, token, profileData)
                if (result.success) {
                    userProfile = result.profile
                    onSaveClick()
                }
            } catch (e: Exception) {
                // 处理错误
            } finally {
                isSaving = false
            }
        }
    }
    
    // 加载用户资料
    LaunchedEffect(Unit) {
        val result = profileService.getUserProfile(token)
        if (result.success) {
            userProfile = result.profile
            // 初始化profileData
            result.profile?.let { profile ->
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
            }
        }
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
                    onProfileUpdate = { field, value ->
                        profileData[field] = value
                    }
                )
                
                // 更多资料区域
                MoreInfoSection(
                    userProfile = userProfile,
                    onProfileUpdate = { field, value ->
                        profileData[field] = value
                    }
                )
                
                // 标签区域
                TagsSection()
                
                Spacer(modifier = Modifier.height(20.dp))
            }
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
            value = userProfile?.nickname ?: "相约未来605",
            onItemClick = { onProfileUpdate("nickname", "新昵称") }
        )
        
        // 性别
        ProfileEditItem(
            label = "性别",
            value = userProfile?.gender ?: "男",
            onItemClick = { onProfileUpdate("gender", "女") }
        )
        
        // 个性签名
        ProfileEditItem(
            label = "个性签名",
            value = userProfile?.bio?.ifEmpty { "编辑个性签名" } ?: "编辑个性签名",
            placeholder = userProfile?.bio.isNullOrEmpty(),
            onItemClick = { onProfileUpdate("bio", "新的个性签名") }
        )
        
        // 生日
        ProfileEditItem(
            label = "生日",
            value = userProfile?.birthday?.ifEmpty { "1990年01月01日 摩羯座" } ?: "1990年01月01日 摩羯座",
            onItemClick = { onProfileUpdate("birthday", "1990-01-01") }
        )
        
        // 所在城市
        ProfileEditItem(
            label = "所在城市",
            value = userProfile?.location?.ifEmpty { "南京" } ?: "南京",
            icon = R.drawable.ic_location,
            onItemClick = { onProfileUpdate("location", "上海") }
        )
        
        // 身高
        ProfileEditItem(
            label = "身高",
            value = if (userProfile?.height ?: 0 > 0) "${userProfile?.height}cm" else "编辑身高",
            placeholder = (userProfile?.height ?: 0) == 0,
            onItemClick = { onProfileUpdate("height", 175) }
        )
        
        // 体重
        ProfileEditItem(
            label = "体重",
            value = if (userProfile?.weight ?: 0 > 0) "${userProfile?.weight}kg" else "编辑体重",
            placeholder = (userProfile?.weight ?: 0) == 0,
            onItemClick = { onProfileUpdate("weight", 70) }
        )
        
        // 情感状态
        ProfileEditItem(
            label = "情感状态",
            value = userProfile?.relationshipStatus?.ifEmpty { "选择您的情感状态" } ?: "选择您的情感状态",
            placeholder = userProfile?.relationshipStatus.isNullOrEmpty(),
            onItemClick = { onProfileUpdate("relationshipStatus", "单身") }
        )
        
        // 年收入
        ProfileEditItem(
            label = "年收入",
            value = userProfile?.income?.ifEmpty { "选择您的年收入" } ?: "选择您的年收入",
            placeholder = userProfile?.income.isNullOrEmpty(),
            onItemClick = { onProfileUpdate("income", "10-20万") }
        )
        
        // 职业
        ProfileEditItem(
            label = "职业",
            value = userProfile?.occupation?.ifEmpty { "选择您的职业" } ?: "选择您的职业",
            placeholder = userProfile?.occupation.isNullOrEmpty(),
            onItemClick = { onProfileUpdate("occupation", "程序员") }
        )
    }
}

@Composable
private fun MoreInfoSection(
    userProfile: UserProfile?,
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
            value = userProfile?.hometown?.ifEmpty { "选择您的家乡" } ?: "选择您的家乡",
            placeholder = userProfile?.hometown.isNullOrEmpty(),
            onItemClick = { onProfileUpdate("hometown", "北京") }
        )
        
        // 学历
        ProfileEditItem(
            label = "学历",
            value = userProfile?.education?.ifEmpty { "选择您的学历" } ?: "选择您的学历",
            placeholder = userProfile?.education.isNullOrEmpty(),
            onItemClick = { onProfileUpdate("education", "本科") }
        )
        
        // 居住城市
        ProfileEditItem(
            label = "居住城市",
            value = userProfile?.city?.ifEmpty { "选择您的居住城市" } ?: "选择您的居住城市",
            placeholder = userProfile?.city.isNullOrEmpty(),
            onItemClick = { onProfileUpdate("city", "深圳") }
        )
        
        // 居住情况
        ProfileEditItem(
            label = "居住情况",
            value = userProfile?.residenceStatus?.ifEmpty { "选择您的居住情况" } ?: "选择您的居住情况",
            placeholder = userProfile?.residenceStatus.isNullOrEmpty(),
            onItemClick = { onProfileUpdate("residenceStatus", "自有住房") }
        )
        
        // 是否购房
        ProfileEditItem(
            label = "是否购房",
            value = when (userProfile?.houseOwnership) {
                true -> "已购房"
                false -> "未购房"
                else -> "选择您的购房情况"
            },
            placeholder = userProfile?.houseOwnership == null,
            onItemClick = { onProfileUpdate("houseOwnership", true) }
        )
        
        // 是否购车
        ProfileEditItem(
            label = "是否购车",
            value = when (userProfile?.carOwnership) {
                true -> "已购车"
                false -> "未购车"
                else -> "选择您的购车情况"
            },
            placeholder = userProfile?.carOwnership == null,
            onItemClick = { onProfileUpdate("carOwnership", false) }
        )
        
        // 兴趣爱好
        ProfileEditItem(
            label = "兴趣爱好",
            value = userProfile?.hobbies?.ifEmpty { "编辑兴趣爱好" } ?: "编辑兴趣爱好",
            placeholder = userProfile?.hobbies.isNullOrEmpty(),
            onItemClick = { onProfileUpdate("hobbies", "旅游、摄影、音乐") }
        )
        
        // 血型
        ProfileEditItem(
            label = "血型",
            value = userProfile?.bloodType?.ifEmpty { "选择您的血型" } ?: "选择您的血型",
            placeholder = userProfile?.bloodType.isNullOrEmpty(),
            onItemClick = { onProfileUpdate("bloodType", "A型") }
        )
        
        // 是否吸烟
        ProfileEditItem(
            label = "是否吸烟",
            value = when (userProfile?.smoking) {
                true -> "吸烟"
                false -> "不吸烟"
                else -> "选择您的吸烟情况"
            },
            placeholder = userProfile?.smoking == null,
            onItemClick = { onProfileUpdate("smoking", false) }
        )
        
        // 是否饮酒
        ProfileEditItem(
            label = "是否饮酒",
            value = when (userProfile?.drinking) {
                true -> "饮酒"
                false -> "不饮酒"
                else -> "选择您的饮酒情况"
            },
            placeholder = userProfile?.drinking == null,
            onItemClick = { onProfileUpdate("drinking", false) }
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
    onItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
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
            color = if (placeholder) Color(0xFF999999) else Color.Black
        )
        
        Spacer(modifier = Modifier.width(60.dp))
        
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "编辑",
            modifier = Modifier.size(16.dp),
            tint = Color.Gray
        )
    }
}
