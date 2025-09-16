package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.screens.ProfileScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.ProfileViewModel

/**
 * 个人中心页面Activity - 完全独立实现
 * 不依赖MainActivity或其他现有代码
 */
class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                ProfileActivityContent(activity = this)
            }
        }
    }
}

/**
 * 个人中心页面内容
 */
@Composable
fun ProfileActivityContent(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = viewModel(),
    activity: ComponentActivity? = null
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ProfileScreen(
            modifier = modifier,
            onSettingClick = {
                // 处理设置点击事件
                activity?.let { 
                    val intent = android.content.Intent(it, SettingsActivity::class.java)
                    it.startActivity(intent)
                }
            },
            onVipClick = {
                // 处理VIP点击事件 - 跳转到VIP会员中心
                activity?.let { 
                    val intent = android.content.Intent(it, VipCenterActivity::class.java)
                    it.startActivity(intent)
                }
            },
            onRechargeClick = {
                // 处理充值点击事件
                handleRechargeClick()
            },
            onWealthLevelClick = {
                // 处理财富等级点击事件
                activity?.let { 
                    val intent = android.content.Intent(it, WealthLevelActivity::class.java)
                    it.startActivity(intent)
                }
            },
            onPropMallClick = {
                // 处理道具商城点击事件
                activity?.let { 
                    val intent = android.content.Intent(it, PropMallActivity::class.java)
                    it.startActivity(intent)
                }
            },
            onMenuClick = { menuItem ->
                // 处理菜单点击事件
                handleMenuClick(menuItem, activity)
            },
            profileViewModel = profileViewModel
        )
    }
}

/**
 * 处理设置点击事件
 */
fun handleSettingClick() {
    // 跳转到设置页面
    // val intent = android.content.Intent(this, SettingsActivity::class.java)
    // startActivity(intent)
}

/**
 * 处理充值点击事件
 */
fun handleRechargeClick() {
    // 这里可以实现跳转到充值页面的逻辑
    // 或者显示充值对话框
}

/**
 * 处理菜单点击事件
 */
fun handleMenuClick(menuItem: String, activity: ComponentActivity? = null) {
    when (menuItem) {
        "邀请好友" -> {
            // 跳转到邀请好友页面
            activity?.let {
                val intent = android.content.Intent(it, InviteFriendsActivity::class.java)
                it.startActivity(intent)
            }
        }
        "我的认证" -> {
            // 跳转到我的认证页面
            activity?.let {
                val intent = android.content.Intent(it, MyCertificationActivity::class.java)
                it.startActivity(intent)
            }
        }
        "我的卡券" -> {
            // 跳转到我的卡券页面
            activity?.let {
                val intent = android.content.Intent(it, MyCouponsActivity::class.java)
                it.startActivity(intent)
            }
        }
        "谁看过我" -> {
            // 跳转到谁看过我页面
            activity?.let {
                val intent = android.content.Intent(it, WhoViewedMeActivity::class.java)
                it.startActivity(intent)
            }
        }
        "我的守护" -> {
            // 跳转到我的守护页面
            activity?.let {
                val intent = android.content.Intent(it, MyGuardActivity::class.java)
                it.startActivity(intent)
            }
        }
        "我的客服" -> {
            // 跳转到我的客服页面
            activity?.let {
                val intent = android.content.Intent(it, MyCustomerServiceActivity::class.java)
                it.startActivity(intent)
            }
        }
        "我的礼物" -> {
            // 跳转到我的礼物页面
            activity?.let {
                val intent = android.content.Intent(it, MyGiftsActivity::class.java)
                it.startActivity(intent)
            }
        }
        "我的钱包" -> {
            // 跳转到我的钱包页面
            activity?.let {
                val intent = android.content.Intent(it, MyWalletActivity::class.java)
                it.startActivity(intent)
            }
        }
    }
}

/**
 * 预览
 */
@Preview(showBackground = true)
@Composable
fun ProfileActivityContentPreview() {
    MyApplicationTheme {
        ProfileActivityContent()
    }
}
