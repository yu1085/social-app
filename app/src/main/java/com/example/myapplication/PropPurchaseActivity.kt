package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.screens.PropPurchaseScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.auth.AuthManager
import com.example.myapplication.model.LuckyNumber

/**
 * 道具购买Activity
 */
class PropPurchaseActivity : ComponentActivity() {
    
    companion object {
        const val EXTRA_LUCKY_NUMBER = "lucky_number"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PropPurchaseActivityContent(activity = this)
                }
            }
        }
    }
}

/**
 * 道具购买Activity内容
 */
@Composable
private fun PropPurchaseActivityContent(activity: ComponentActivity) {
    val authManager = AuthManager.getInstance(activity)
    val token = authManager.getToken()
    
    // 从Intent获取靓号数据
    val luckyNumber = activity.intent.getSerializableExtra(PropPurchaseActivity.EXTRA_LUCKY_NUMBER) as? LuckyNumber
    
    if (luckyNumber != null) {
        PropPurchaseScreen(
            luckyNumber = luckyNumber,
            token = token,
            onBackClick = {
                activity.finish()
            },
            onPurchaseSuccess = { message ->
                // 购买成功处理
                android.widget.Toast.makeText(activity, message, android.widget.Toast.LENGTH_LONG).show()
                activity.finish()
            },
            onPurchaseError = { error ->
                // 购买失败处理
                android.widget.Toast.makeText(activity, error, android.widget.Toast.LENGTH_LONG).show()
            }
        )
    } else {
        // 如果没有靓号数据，显示错误信息
        androidx.compose.material3.Text(
            text = "商品信息错误",
            modifier = Modifier.fillMaxSize()
        )
    }
}
