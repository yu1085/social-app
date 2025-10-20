package com.example.myapplication.compose

import android.content.Context
import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.myapplication.SettingsActivity
import com.example.myapplication.network.NetworkService
import com.example.myapplication.dto.UserDTO
import com.example.myapplication.dto.WalletDTO
import com.example.myapplication.dto.VipInfoDTO
import com.example.myapplication.dto.UserSettingsDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.myapplication.VipCenterActivity
import com.example.myapplication.WealthLevelActivity
import com.example.myapplication.PropMallActivity
import com.example.myapplication.InviteFriendsActivity
import com.example.myapplication.MyCertificationActivity
import com.example.myapplication.MyCouponsActivity
import com.example.myapplication.WhoViewedMeActivity
import com.example.myapplication.MyGuardActivity
import com.example.myapplication.MyGiftsActivity
import com.example.myapplication.MyCustomerServiceActivity
import com.example.myapplication.MyWalletActivity
import com.example.myapplication.ProfileDetailActivity
import com.example.myapplication.ui.screens.ProfileScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.ProfileViewModel

object ProfileComposeHost {
    private var isInitialized = false
    private var profileViewModel: ProfileViewModel? = null
    
    @JvmStatic
    fun attach(target: ComposeView) {
        target.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val viewModel = profileViewModel ?: viewModel<ProfileViewModel>().also { 
                        profileViewModel = it 
                    }
                    
                    // 加载用户数据
                    LaunchedEffect(Unit) {
                        android.util.Log.d("ProfileComposeHost", "LaunchedEffect 开始执行")
                        // 从后端API获取用户信息
                        loadUserInfoFromApi(viewModel, target.context)
                    }
                    
                    ProfileScreen(
                        profileViewModel = viewModel,
                        onUserInfoClick = {
                            // 跳转到个人资料详情页面
                            val context = target.context
                            val intent = Intent(context, ProfileDetailActivity::class.java)
                            context.startActivity(intent)
                        },
                        onSettingClick = {
                            // 跳转到设置页面
                            val context = target.context
                            val intent = Intent(context, SettingsActivity::class.java)
                            context.startActivity(intent)
                            android.widget.Toast.makeText(context, "跳转到设置页面", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        onVipClick = {
                            // 跳转到VIP会员中心页面
                            val context = target.context
                            val intent = Intent(context, VipCenterActivity::class.java)
                            context.startActivity(intent)
                            android.widget.Toast.makeText(context, "跳转到VIP会员中心", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        onRechargeClick = {
                            // 处理充值点击事件 - 直接跳转到充值页面
                            val context = target.context
                            val intent = Intent(context, com.example.myapplication.RechargeActivity::class.java)
                            context.startActivity(intent)
                        },
                        onWealthLevelClick = {
                            // 跳转到财富等级页面
                            val context = target.context
                            val intent = Intent(context, com.example.myapplication.WealthLevelActivity::class.java)
                            context.startActivity(intent)
                            android.widget.Toast.makeText(context, "跳转到财富等级页面", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        onPropMallClick = {
                            // 跳转到道具商城页面
                            val context = target.context
                            val intent = Intent(context, com.example.myapplication.PropMallActivity::class.java)
                            context.startActivity(intent)
                            android.widget.Toast.makeText(context, "跳转到道具商城页面", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        onMenuClick = { menuItem ->
                            // 处理菜单点击事件
                            val context = target.context
                            when (menuItem) {
                                "邀请好友" -> {
                                    val intent = Intent(context, com.example.myapplication.InviteFriendsActivity::class.java)
                                    context.startActivity(intent)
                                }
                                "我的认证" -> {
                                    val intent = Intent(context, com.example.myapplication.MyCertificationActivity::class.java)
                                    context.startActivity(intent)
                                }
                                "我的卡券" -> {
                                    val intent = Intent(context, com.example.myapplication.MyCouponsActivity::class.java)
                                    context.startActivity(intent)
                                }
                                "谁看过我" -> {
                                    val intent = Intent(context, com.example.myapplication.WhoViewedMeActivity::class.java)
                                    context.startActivity(intent)
                                }
                                "我的守护" -> {
                                    val intent = Intent(context, com.example.myapplication.MyGuardActivity::class.java)
                                    context.startActivity(intent)
                                }
                                "我的礼物" -> {
                                    val intent = Intent(context, com.example.myapplication.MyGiftsActivity::class.java)
                                    context.startActivity(intent)
                                }
                                "我的客服" -> {
                                    val intent = Intent(context, com.example.myapplication.MyCustomerServiceActivity::class.java)
                                    context.startActivity(intent)
                                }
                                "我的钱包" -> {
                                    val intent = Intent(context, com.example.myapplication.MyWalletActivity::class.java)
                                    context.startActivity(intent)
                                }
                                else -> {
                                    android.widget.Toast.makeText(context, "点击了: $menuItem", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    
    /**
     * 从后端API加载用户信息
     */
    private suspend fun loadUserInfoFromApi(viewModel: ProfileViewModel, context: android.content.Context) {
        android.util.Log.d("ProfileComposeHost", "loadUserInfoFromApi 开始执行")
        
        try {
            val authManager = com.example.myapplication.auth.AuthManager.getInstance(context)
            
            // 检查是否已登录
            if (!authManager.isLoggedIn()) {
                android.util.Log.e("ProfileComposeHost", "用户未登录")
                viewModel.updateUserInfo(
                    nickname = "未登录用户",
                    id = 0L,
                    avatarUrl = ""
                )
                return
            }
            
            android.util.Log.d("ProfileComposeHost", "用户已登录，开始获取用户信息...")
            android.util.Log.d("ProfileComposeHost", "Token: ${authManager.getToken()}")
            android.util.Log.d("ProfileComposeHost", "UserID: ${authManager.getUserId()}")
            
            // 使用新的个人资料API获取完整信息
            withContext(Dispatchers.IO) {
                try {
                    val apiService = com.example.myapplication.network.NetworkConfig.getApiService()
                    val call = apiService.getCompleteProfile(authManager.getAuthHeader() ?: "")
                    val response = call.execute()
                    
                    if (response.isSuccessful && response.body() != null) {
                        val apiResponse = response.body()
                        if (apiResponse?.isSuccess() == true) {
                            val profileData = apiResponse.data

                            android.util.Log.d("ProfileComposeHost", "API响应数据: $profileData")

                            // 从 Map 中提取用户信息
                            val userMap = profileData?.get("user") as? Map<*, *>
                            android.util.Log.d("ProfileComposeHost", "用户数据Map: $userMap")

                            val nickname = userMap?.get("nickname") as? String ?: "用户"
                            val userId = when (val id = userMap?.get("id")) {
                                is Number -> id.toLong()
                                else -> 0L
                            }
                            val avatarUrl = userMap?.get("avatarUrl") as? String ?: ""

                            android.util.Log.d("ProfileComposeHost", "解析后的用户信息: nickname=$nickname, id=$userId")

                            // 更新ViewModel中的用户信息
                            viewModel.updateUserInfo(
                                nickname = nickname,
                                id = userId,
                                avatarUrl = avatarUrl
                            )

                            // 更新钱包信息
                            val walletMap = profileData?.get("wallet") as? Map<*, *>
                            if (walletMap != null) {
                                val balance = when (val bal = walletMap["balance"]) {
                                    is Number -> bal.toDouble()
                                    is String -> bal.toDoubleOrNull() ?: 0.0
                                    else -> 0.0
                                }

                                android.util.Log.d("ProfileComposeHost", "钱包余额: $balance")

                                // 暂时只更新余额，其他字段后续添加
                                viewModel.updateWalletInfo(
                                    balance = balance,
                                    totalRecharge = 0.0,
                                    totalConsume = 0.0
                                )
                            }

                            // 更新VIP信息
                            val vipInfoMap = profileData?.get("vipInfo") as? Map<*, *>
                            if (vipInfoMap != null) {
                                val isVip = vipInfoMap["isVip"] as? Boolean ?: false
                                val vipLevel = when (val level = vipInfoMap["vipLevel"]) {
                                    is Number -> level.toInt()
                                    else -> 0
                                }
                                val remainingDays = when (val days = vipInfoMap["remainingDays"]) {
                                    is Number -> days.toLong()
                                    else -> 0L
                                }

                                viewModel.updateVipInfo(
                                    isVip = isVip,
                                    vipLevel = vipLevel,
                                    remainingDays = remainingDays
                                )
                            }

                            // 更新设置信息
                            val settingsMap = profileData?.get("settings") as? Map<*, *>
                            if (settingsMap != null) {
                                val voiceCallEnabled = settingsMap["voiceCallEnabled"] as? Boolean ?: true
                                val videoCallEnabled = settingsMap["videoCallEnabled"] as? Boolean ?: true
                                val messageChargeEnabled = settingsMap["messageChargeEnabled"] as? Boolean ?: false

                                viewModel.updateSettingsInfo(
                                    voiceCallEnabled = voiceCallEnabled,
                                    videoCallEnabled = videoCallEnabled,
                                    messageChargeEnabled = messageChargeEnabled
                                )
                            }
                            
                        } else {
                            android.util.Log.e("ProfileComposeHost", "API返回失败: ${apiResponse?.message}")
                            // 使用测试数据
                            viewModel.updateUserInfo(
                                nickname = "测试用户${System.currentTimeMillis() % 10000}",
                                id = 12345678L,
                                avatarUrl = ""
                            )
                        }
                    } else {
                        android.util.Log.e("ProfileComposeHost", "网络请求失败: ${response.code()}")
                        // 使用测试数据
                        viewModel.updateUserInfo(
                            nickname = "测试用户${System.currentTimeMillis() % 10000}",
                            id = 12345678L,
                            avatarUrl = ""
                        )
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ProfileComposeHost", "网络请求异常: ${e.message}")
                    // 使用测试数据
                    viewModel.updateUserInfo(
                        nickname = "测试用户${System.currentTimeMillis() % 10000}",
                        id = 12345678L,
                        avatarUrl = ""
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ProfileComposeHost", "获取用户信息异常: ${e.message}", e)
            // 异常处理，使用测试信息
            viewModel.updateUserInfo(
                nickname = "异常用户${System.currentTimeMillis() % 10000}",
                id = System.currentTimeMillis(),
                avatarUrl = ""
            )
        }
    }
}
