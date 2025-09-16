package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * 支付方式枚举
 */
enum class PaymentMethod {
    ALIPAY,
    WECHAT
}

/**
 * 支付对话框
 */
@Composable
fun PaymentDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirmPayment: (PaymentMethod, String, String) -> Unit,
    membershipType: String = "SVIP会员12个月",
    price: String = "¥334",
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            PaymentDialogContent(
                onDismiss = onDismiss,
                onConfirmPayment = onConfirmPayment,
                membershipType = membershipType,
                price = price
            )
        }
    }
}

/**
 * 支付对话框内容
 */
@Composable
private fun PaymentDialogContent(
    onDismiss: () -> Unit,
    onConfirmPayment: (PaymentMethod, String, String) -> Unit,
    membershipType: String,
    price: String,
    modifier: Modifier = Modifier
) {
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.ALIPAY) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "支付方式",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = Color(0xFF666666)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 会员信息
            Text(
                text = membershipType,
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 价格
            Text(
                text = price,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B35),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 支付方式选择
            PaymentMethodSelection(
                selectedMethod = selectedPaymentMethod,
                onMethodSelected = { selectedPaymentMethod = it }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 确认支付按钮
            Button(
                onClick = {
                    onConfirmPayment(selectedPaymentMethod, membershipType, price)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "确认支付(${price.replace("¥", "")}元)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * 支付方式选择
 */
@Composable
private fun PaymentMethodSelection(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // 支付宝
        PaymentMethodItem(
            method = PaymentMethod.ALIPAY,
            title = "支付宝",
            icon = Icons.Default.AccountBalance,
            iconColor = Color(0xFF1677FF),
            isSelected = selectedMethod == PaymentMethod.ALIPAY,
            onClick = { onMethodSelected(PaymentMethod.ALIPAY) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 微信支付
        PaymentMethodItem(
            method = PaymentMethod.WECHAT,
            title = "微信",
            icon = Icons.Default.Chat,
            iconColor = Color(0xFF07C160),
            isSelected = selectedMethod == PaymentMethod.WECHAT,
            onClick = { onMethodSelected(PaymentMethod.WECHAT) }
        )
    }
}

/**
 * 支付方式项
 */
@Composable
private fun PaymentMethodItem(
    method: PaymentMethod,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 支付方式图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    iconColor.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 支付方式名称
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        
        // 选择状态
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    if (isSelected) Color(0xFF1677FF) else Color.Transparent,
                    CircleShape
                )
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "已选择",
                    modifier = Modifier.size(12.dp),
                    tint = Color.White
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            Color(0xFFE0E0E0),
                            CircleShape
                        )
                )
            }
        }
    }
}

/**
 * 预览
 */
@Preview(showBackground = true)
@Composable
fun PaymentDialogPreview() {
    PaymentDialog(
        isVisible = true,
        onDismiss = {},
        onConfirmPayment = { _, _, _ -> },
        membershipType = "SVIP会员12个月",
        price = "¥334"
    )
}
