package com.example.myapplication.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.ComposeView
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.screens.SquareScreen
import com.example.myapplication.ui.screens.PublishPostScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.EnhancedSquareViewModel

object SquareComposeHost {
    @JvmStatic
    fun attach(target: ComposeView) {
        target.setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SquareContent()
                }
            }
        }
    }
}

@Composable
private fun SquareContent() {
    var showPublishScreen by remember { mutableStateOf(false) }
    val squareViewModel: EnhancedSquareViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    
    if (showPublishScreen) {
        PublishPostScreen(
            onBackClick = { 
                showPublishScreen = false
                android.util.Log.d("SquareComposeHost", "返回广场页面")
            },
            onPublishSuccess = { 
                showPublishScreen = false
                // 发布成功后刷新动态列表
                squareViewModel.refresh()
                android.util.Log.d("SquareComposeHost", "动态发布成功，返回广场页面并刷新数据")
            }
        )
    } else {
        SquareScreen(
            onUserClick = { _ -> },
            onPublishClick = { 
                showPublishScreen = true
                android.util.Log.d("SquareComposeHost", "跳转到发布页面")
            },
            viewModel = squareViewModel
        )
    }
}


