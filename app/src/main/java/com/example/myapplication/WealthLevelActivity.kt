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
import com.example.myapplication.auth.AuthManager

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
    val authManager = AuthManager.getInstance(activity)
    val token = authManager.getToken()
    
    WealthLevelScreen(
        onBackClick = {
            // 返回上一页
            activity.finish()
        },
        onRulesClick = {
            // 跳转到规则说明页面
            val intent = android.content.Intent(activity, WealthRulesActivity::class.java)
            activity.startActivity(intent)
        },
        token = token
    )
}