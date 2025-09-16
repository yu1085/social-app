package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.screens.WealthLevelScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * 财富等级Activity
 */
class WealthLevelActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WealthLevelActivityContent(activity = this)
                }
            }
        }
    }
}

/**
 * 财富等级Activity内容
 */
@Composable
private fun WealthLevelActivityContent(activity: ComponentActivity) {
    WealthLevelScreen(
        onBackClick = {
            // 返回上一页
            activity.finish()
        },
        onRulesClick = {
            // 处理规则说明点击
            handleRulesClick(activity)
        },
        onPromotionMallClick = {
            // 处理促销商城点击
            handlePromotionMallClick(activity)
        }
    )
}

/**
 * 处理规则说明点击事件
 */
private fun handleRulesClick(activity: ComponentActivity) {
    // 跳转到规则说明页面
    val intent = android.content.Intent(activity, WealthRulesActivity::class.java)
    activity.startActivity(intent)
}

/**
 * 处理促销商城点击事件
 */
private fun handlePromotionMallClick(activity: ComponentActivity) {
    // 跳转到道具商城页面
    val intent = android.content.Intent(activity, PropMallActivity::class.java)
    activity.startActivity(intent)
}
