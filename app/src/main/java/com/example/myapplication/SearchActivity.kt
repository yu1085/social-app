package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchScreen(
                onBackClick = { finish() },
                onCancelClick = { finish() },
                onSearchClick = { searchId ->
                    // 处理搜索
                    android.widget.Toast.makeText(
                        this,
                        "搜索ID: $searchId",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit,
    onSearchClick: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(0) } // 0: 全部, 1: 小哥哥, 2: 小姐姐
    var selectedCity by remember { mutableStateOf(0) } // 0: 不限, 1: 同城, 2: 选择城市
    var selectedAge by remember { mutableStateOf("不限") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 顶部搜索栏
        SearchHeader(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onBackClick = onBackClick,
            onCancelClick = onCancelClick
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 会员专享提示
        MemberExclusiveSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 筛选选项
        FilterSections(
            selectedGender = selectedGender,
            onGenderSelected = { selectedGender = it },
            selectedCity = selectedCity,
            onCitySelected = { selectedCity = it },
            selectedAge = selectedAge,
            onAgeClick = { /* 处理年龄选择 */ }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 开始搜索按钮
        SearchButton(
            onSearchClick = { onSearchClick(searchText) }
        )
    }
}

@Composable
fun SearchHeader(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 搜索框
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = {
                Text(
                    text = "请输入您要搜索的ID",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = "取消",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.clickable { onCancelClick() }
        )
    }
}

@Composable
fun MemberExclusiveSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(Color.Gray)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "会员专享",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(1.dp)
                    .background(Color.Gray)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "VIP4、VIP5及SVIP专享",
            fontSize = 12.sp,
            color = Color(0xFFFF8C00)
        )
    }
}

@Composable
fun FilterSections(
    selectedGender: Int,
    onGenderSelected: (Int) -> Unit,
    selectedCity: Int,
    onCitySelected: (Int) -> Unit,
    selectedAge: String,
    onAgeClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 性别筛选
        FilterSection(
            title = "性别",
            options = listOf("全部", "小哥哥", "小姐姐"),
            selectedIndex = selectedGender,
            onOptionSelected = onGenderSelected
        )
        
        // 城市筛选
        FilterSection(
            title = "城市",
            options = listOf("不限", "同城", "选择城市"),
            selectedIndex = selectedCity,
            onOptionSelected = onCitySelected
        )
        
        // 年龄筛选
        AgeFilter(
            selectedAge = selectedAge,
            onAgeClick = onAgeClick
        )
    }
}

@Composable
fun FilterSection(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            options.forEachIndexed { index, option ->
                FilterOption(
                    text = option,
                    isSelected = selectedIndex == index,
                    onClick = { onOptionSelected(index) }
                )
            }
        }
    }
}

@Composable
fun FilterOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                color = if (isSelected) Color.White else Color(0xFFF5F5F5)
            )
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) Color(0xFF2196F3) else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color(0xFF2196F3) else Color.Black
        )
    }
}

@Composable
fun AgeFilter(
    selectedAge: String,
    onAgeClick: () -> Unit
) {
    Column {
        Text(
            text = "年龄",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAgeClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedAge,
                fontSize = 14.sp,
                color = Color.Black
            )
            
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "选择年龄",
                tint = Color.Gray,
                modifier = Modifier
                    .size(16.dp)
            )
        }
    }
}

@Composable
fun SearchButton(
    onSearchClick: () -> Unit
) {
    Button(
        onClick = onSearchClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(
            text = "开始搜索",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}
