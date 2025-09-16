package com.example.myapplication.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.ComposeView
import com.example.myapplication.ui.screens.MessageScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.SearchActivity

object MessageComposeHost {
    @JvmStatic
    fun attach(target: ComposeView) {
        target.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MessageScreen(
                        onSearchClick = {
                            // 跳转到搜索页面
                            val intent = android.content.Intent(target.context, SearchActivity::class.java)
                            target.context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}


