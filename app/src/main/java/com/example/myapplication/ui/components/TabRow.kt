package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.viewmodel.SquareTab

/**
 * 标签页行组件
 */
@Composable
fun TabRow(
    selectedTab: SquareTab,
    onTabSelected: (SquareTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 附近标签（默认选中）
        TabItem(
            tab = SquareTab.NEARBY,
            isSelected = selectedTab == SquareTab.NEARBY,
            onClick = { onTabSelected(SquareTab.NEARBY) }
        )
        
        // 最新标签
        TabItem(
            tab = SquareTab.LATEST,
            isSelected = selectedTab == SquareTab.LATEST,
            onClick = { onTabSelected(SquareTab.LATEST) }
        )
        
        // 知友标签
        TabItem(
            tab = SquareTab.FRIENDS,
            isSelected = selectedTab == SquareTab.FRIENDS,
            onClick = { onTabSelected(SquareTab.FRIENDS) }
        )
        
        // 喜欢标签
        TabItem(
            tab = SquareTab.LIKE,
            isSelected = selectedTab == SquareTab.LIKE,
            onClick = { onTabSelected(SquareTab.LIKE) }
        )
    }
}

/**
 * 单个标签项组件
 */
@Composable
private fun TabItem(
    tab: SquareTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Color(0xFFEDF3FD)
    } else {
        Color.Transparent
    }
    
    val textColor = if (isSelected) {
        Color(0xFF5690FF)
    } else {
        Color(0xFFABABAB)
    }
    
    val fontWeight = if (isSelected) {
        FontWeight.Medium
    } else {
        FontWeight.Normal
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(23.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(
                when (tab) {
                    SquareTab.NEARBY -> R.string.square_nearby
                    SquareTab.LATEST -> R.string.square_latest
                    SquareTab.FRIENDS -> R.string.square_friends
                    SquareTab.LIKE -> R.string.square_like
                }
            ),
            fontSize = 16.sp,
            color = textColor,
            fontWeight = fontWeight
        )
    }
}
