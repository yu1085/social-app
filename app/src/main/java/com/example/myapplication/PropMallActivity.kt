package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.screens.PropMallScreen
import com.example.myapplication.ui.screens.PropItem
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.auth.AuthManager

/**
 * 道具商城Activity
 */
class PropMallActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PropMallActivityContent(activity = this)
                }
            }
        }
    }
}

/**
 * 道具商城Activity内容
 */
@Composable
private fun PropMallActivityContent(activity: ComponentActivity) {
    val authManager = AuthManager.getInstance(activity)
    val token = authManager.getToken()
    
    PropMallScreen(
        onBackClick = {
            // 返回上一页
            activity.finish()
        },
        onMyPropsClick = {
            // 处理我的道具点击
            handleMyPropsClick(activity)
        },
        onItemClick = { item ->
            // 处理商品点击
            handleItemClick(activity, item)
        },
        token = token
    )
}

/**
 * 处理我的道具点击事件
 */
private fun handleMyPropsClick(activity: ComponentActivity) {
    // 跳转到我的道具页面
    val intent = android.content.Intent(activity, MyPropsActivity::class.java)
    activity.startActivity(intent)
}

/**
 * 处理商品点击事件
 */
private fun handleItemClick(activity: ComponentActivity, item: PropItem) {
    // 跳转到道具购买页面
    if (item.luckyNumber != null) {
        val intent = android.content.Intent(activity, PropPurchaseActivity::class.java)
        intent.putExtra(PropPurchaseActivity.EXTRA_LUCKY_NUMBER, item.luckyNumber)
        activity.startActivity(intent)
    } else {
        android.widget.Toast.makeText(activity, "商品信息错误", android.widget.Toast.LENGTH_SHORT).show()
    }
}
