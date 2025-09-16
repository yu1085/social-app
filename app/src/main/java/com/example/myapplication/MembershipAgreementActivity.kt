package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.screens.MembershipAgreementScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * 会员服务协议Activity
 */
class MembershipAgreementActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MembershipAgreementActivityContent(activity = this)
                }
            }
        }
    }
}

/**
 * 会员服务协议Activity内容
 */
@Composable
private fun MembershipAgreementActivityContent(activity: ComponentActivity) {
    MembershipAgreementScreen(
        onBackClick = {
            // 返回上一页
            activity.finish()
        }
    )
}
