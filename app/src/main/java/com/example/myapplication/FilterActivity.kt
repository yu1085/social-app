package com.example.myapplication

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class FilterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FilterScreen(
                onDismiss = { finish() },
                onConfirm = { filterData ->
                    // 处理筛选确认 - 返回筛选条件
                    val resultIntent = Intent().apply {
                        putExtra("gender", filterData.callType)
                        putExtra("location", if (filterData.currentCity != "不限") filterData.currentCity else null)
                        putExtra("minAge", filterData.ageRange.first)
                        putExtra("maxAge", filterData.ageRange.second)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            )
        }
    }
}

@Composable
fun FilterScreen(
    onDismiss: () -> Unit,
    onConfirm: (FilterData) -> Unit
) {
    var ageRange by remember { mutableStateOf(Pair(18, 50)) }
    var callPriceRange by remember { mutableStateOf(Pair(100, 500)) }
    var callType by remember { mutableStateOf("不限") }
    var currentCity by remember { mutableStateOf("不限") }
    var hometown by remember { mutableStateOf("不限") }
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var prioritizeOnline by remember { mutableStateOf(true) }
    var prioritizeHighValue by remember { mutableStateOf(false) }
    var prioritizeWellReviewed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 头部
                FilterHeader(onDismiss = onDismiss)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 基础筛选
                BasicFiltersSection(
                    ageRange = ageRange,
                    onAgeRangeChange = { ageRange = it },
                    callPriceRange = callPriceRange,
                    onCallPriceRangeChange = { callPriceRange = it },
                    callType = callType,
                    onCallTypeChange = { callType = it },
                    currentCity = currentCity,
                    onCurrentCityClick = { /* 处理城市选择 */ },
                    hometown = hometown,
                    onHometownClick = { /* 处理家乡选择 */ }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 高级筛选
                AdvancedFiltersSection(
                    selectedTags = selectedTags,
                    onTagToggle = { tag ->
                        selectedTags = if (selectedTags.contains(tag)) {
                            selectedTags - tag
                        } else {
                            selectedTags + tag
                        }
                    },
                    prioritizeOnline = prioritizeOnline,
                    onPrioritizeOnlineChange = { prioritizeOnline = it },
                    prioritizeHighValue = prioritizeHighValue,
                    onPrioritizeHighValueChange = { prioritizeHighValue = it },
                    prioritizeWellReviewed = prioritizeWellReviewed,
                    onPrioritizeWellReviewedChange = { prioritizeWellReviewed = it }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 底部按钮
                FilterButtons(
                    onReset = {
                        ageRange = Pair(18, 50)
                        callPriceRange = Pair(100, 500)
                        callType = "不限"
                        currentCity = "不限"
                        hometown = "不限"
                        selectedTags = emptyList()
                        prioritizeOnline = true
                        prioritizeHighValue = false
                        prioritizeWellReviewed = false
                    },
                    onConfirm = {
                        val filterData = FilterData(
                            ageRange = ageRange,
                            callPriceRange = callPriceRange,
                            callType = callType,
                            currentCity = currentCity,
                            hometown = hometown,
                            selectedTags = selectedTags,
                            prioritizeOnline = prioritizeOnline,
                            prioritizeHighValue = prioritizeHighValue,
                            prioritizeWellReviewed = prioritizeWellReviewed
                        )
                        onConfirm(filterData)
                    }
                )
            }
        }
    }
}

@Composable
fun FilterHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "筛选",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "关闭",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun BasicFiltersSection(
    ageRange: Pair<Int, Int>,
    onAgeRangeChange: (Pair<Int, Int>) -> Unit,
    callPriceRange: Pair<Int, Int>,
    onCallPriceRangeChange: (Pair<Int, Int>) -> Unit,
    callType: String,
    onCallTypeChange: (String) -> Unit,
    currentCity: String,
    onCurrentCityClick: () -> Unit,
    hometown: String,
    onHometownClick: () -> Unit
) {
    Column {
        Text(
            text = "基础筛选",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // 年龄筛选
        AgeFilter(
            ageRange = ageRange,
            onAgeRangeChange = onAgeRangeChange
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 通话价格筛选
        CallPriceFilter(
            callPriceRange = callPriceRange,
            onCallPriceRangeChange = onCallPriceRangeChange
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 通话类型筛选
        CallTypeFilter(
            callType = callType,
            onCallTypeChange = onCallTypeChange
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 所在城市
        CityFilter(
            label = "所在城市",
            value = currentCity,
            onClick = onCurrentCityClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 家乡
        CityFilter(
            label = "家乡",
            value = hometown,
            onClick = onHometownClick
        )
    }
}

@Composable
fun AgeFilter(
    ageRange: Pair<Int, Int>,
    onAgeRangeChange: (Pair<Int, Int>) -> Unit
) {
    Column {
        Text(
            text = "年龄",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${ageRange.first}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            // 简化的范围显示
            Text(
                text = "${ageRange.first}-${ageRange.second}+",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
            
            Text(
                text = "${ageRange.second}+",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CallPriceFilter(
    callPriceRange: Pair<Int, Int>,
    onCallPriceRangeChange: (Pair<Int, Int>) -> Unit
) {
    Column {
        Text(
            text = "通话价格",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${callPriceRange.first}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            
            // 简化的范围显示
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${callPriceRange.first}-${callPriceRange.second}",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .background(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "金币",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Text(
                text = "${callPriceRange.second}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CallTypeFilter(
    callType: String,
    onCallTypeChange: (String) -> Unit
) {
    Column {
        Text(
            text = "通话类型",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val callTypes = listOf("不限", "视频", "语音")
            callTypes.forEach { type ->
                CallTypeButton(
                    text = type,
                    isSelected = type == callType,
                    onClick = { onCallTypeChange(type) }
                )
            }
        }
    }
}

@Composable
fun CallTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color(0xFF2196F3) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun CityFilter(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "进入",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun AdvancedFiltersSection(
    selectedTags: List<String>,
    onTagToggle: (String) -> Unit,
    prioritizeOnline: Boolean,
    onPrioritizeOnlineChange: (Boolean) -> Unit,
    prioritizeHighValue: Boolean,
    onPrioritizeHighValueChange: (Boolean) -> Unit,
    prioritizeWellReviewed: Boolean,
    onPrioritizeWellReviewedChange: (Boolean) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "高级",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = "高级筛选",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        
        // 标签筛选
        TagFilter(
            selectedTags = selectedTags,
            onTagToggle = onTagToggle
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 优先选项
        PriorityOptions(
            prioritizeOnline = prioritizeOnline,
            onPrioritizeOnlineChange = onPrioritizeOnlineChange,
            prioritizeHighValue = prioritizeHighValue,
            onPrioritizeHighValueChange = onPrioritizeHighValueChange,
            prioritizeWellReviewed = prioritizeWellReviewed,
            onPrioritizeWellReviewedChange = onPrioritizeWellReviewedChange
        )
    }
}

@Composable
fun TagFilter(
    selectedTags: List<String>,
    onTagToggle: (String) -> Unit
) {
    Column {
        Text(
            text = "标签",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tags = listOf("甜美可爱", "性感成熟", "知性", "阳光")
            tags.forEach { tag ->
                TagButton(
                    text = tag,
                    isSelected = selectedTags.contains(tag),
                    onClick = { onTagToggle(tag) }
                )
            }
        }
    }
}

@Composable
fun TagButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color(0xFF2196F3) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun PriorityOptions(
    prioritizeOnline: Boolean,
    onPrioritizeOnlineChange: (Boolean) -> Unit,
    prioritizeHighValue: Boolean,
    onPrioritizeHighValueChange: (Boolean) -> Unit,
    prioritizeWellReviewed: Boolean,
    onPrioritizeWellReviewedChange: (Boolean) -> Unit
) {
    Column {
        PriorityOption(
            text = "优先看在线的女生",
            isChecked = prioritizeOnline,
            onCheckedChange = onPrioritizeOnlineChange
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        PriorityOption(
            text = "优先查看高颜女生",
            isChecked = prioritizeHighValue,
            onCheckedChange = onPrioritizeHighValueChange
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        PriorityOption(
            text = "优先查看好评女生",
            isChecked = prioritizeWellReviewed,
            onCheckedChange = onPrioritizeWellReviewedChange
        )
    }
}

@Composable
fun PriorityOption(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black
        )
        
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF2196F3),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }
}

@Composable
fun FilterButtons(
    onReset: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onReset,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "重置",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
        
        Button(
            onClick = onConfirm,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "确定",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

data class FilterData(
    val ageRange: Pair<Int, Int>,
    val callPriceRange: Pair<Int, Int>,
    val callType: String,
    val currentCity: String,
    val hometown: String,
    val selectedTags: List<String>,
    val prioritizeOnline: Boolean,
    val prioritizeHighValue: Boolean,
    val prioritizeWellReviewed: Boolean
)
