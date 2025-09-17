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
                            // 处理充值点击事件 - 跳转到我的钱包页面
                            val context = target.context
                            val intent = Intent(context, com.example.myapplication.MyWalletActivity::class.java)
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
                            // 处理菜单点击事件 - 直接调用ProfileActivity的handleMenuClick
                            val context = target.context
                            if (context is com.example.myapplication.ProfileActivity) {
                                com.example.myapplication.handleMenuClick(menuItem, context)
                            } else {
                                // 如果context不是ProfileActivity，则直接处理
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
            
            // 先设置一个测试值，看看UI是否会更新
            viewModel.updateUserInfo(
                nickname = "测试用户123",
                id = 12345678L,
                avatarUrl = ""
            )
            
            android.util.Log.d("ProfileComposeHost", "已设置测试用户信息")
            
            // 然后尝试从API获取真实数据
            withContext(Dispatchers.IO) {
                val networkService = NetworkService.getInstance(context)
                networkService.getProfile(object : NetworkService.NetworkCallback<UserDTO> {
                    override fun onSuccess(user: UserDTO) {
                        android.util.Log.d("ProfileComposeHost", "获取用户信息成功: ${user.nickname}")
                        // 更新ViewModel中的用户信息
                        viewModel.updateUserInfo(
                            nickname = user.nickname ?: "用户",
                            id = user.id ?: 0L,
                            avatarUrl = user.avatarUrl ?: ""
                        )
                    }
                    
                    override fun onError(error: String) {
                        android.util.Log.e("ProfileComposeHost", "获取用户信息失败: $error")
                        // 保持测试值
                    }
                })
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


