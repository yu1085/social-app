package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.screens.MessageScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MessageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MessageScreen(
                        onSearchClick = {
                            // 跳转到搜索页面
                            val intent = android.content.Intent(this, SearchActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
