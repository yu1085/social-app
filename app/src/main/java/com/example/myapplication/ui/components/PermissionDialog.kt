package com.example.myapplication.ui.components

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.utils.PermissionManager

/**
 * 权限对话框组件 - 参考主流应用的设计
 */
@Composable
fun PermissionDialog(
    isVisible: Boolean,
    permissionTypes: List<PermissionManager.PermissionType>,
    activity: ComponentActivity,
    onGrant: () -> Unit,
    onDeny: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDeny) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 图标
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF2196F3)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 标题
                    Text(
                        text = "需要权限",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 权限说明
                    val permissionManager = PermissionManager(activity)
                    val description = permissionManager.getPermissionDescription(permissionTypes)
                    
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 权限列表
                    PermissionList(permissionTypes = permissionTypes)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 按钮组
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 拒绝按钮
                        OutlinedButton(
                            onClick = onDeny,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Gray
                            )
                        ) {
                            Text("拒绝")
                        }
                        
                        // 授权按钮
                        Button(
                            onClick = onGrant,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2196F3)
                            )
                        ) {
                            Text("授权")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 设置按钮
                    TextButton(
                        onClick = onOpenSettings,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text("手动设置权限")
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionList(
    permissionTypes: List<PermissionManager.PermissionType>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        permissionTypes.forEach { type ->
            PermissionItem(
                permissionType = type,
                modifier = Modifier.fillMaxWidth()
            )
            if (type != permissionTypes.last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PermissionItem(
    permissionType: PermissionManager.PermissionType,
    modifier: Modifier = Modifier
) {
    val (icon, title, description) = when (permissionType) {
        PermissionManager.PermissionType.CAMERA -> Triple(
            Icons.Default.CameraAlt,
            "相机权限",
            "拍摄照片和视频"
        )
        PermissionManager.PermissionType.STORAGE -> Triple(
            Icons.Default.Storage,
            "存储权限",
            "访问相册和文件"
        )
        PermissionManager.PermissionType.MEDIA_IMAGES -> Triple(
            Icons.Default.PhotoLibrary,
            "媒体权限",
            "访问相册中的图片"
        )
        PermissionManager.PermissionType.LOCATION -> Triple(
            Icons.Default.LocationOn,
            "位置权限",
            "提供基于位置的服务"
        )
        PermissionManager.PermissionType.CONTACTS -> Triple(
            Icons.Default.Contacts,
            "联系人权限",
            "查找和邀请好友"
        )
        PermissionManager.PermissionType.PHONE -> Triple(
            Icons.Default.Phone,
            "电话权限",
            "拨打语音通话"
        )
        PermissionManager.PermissionType.MICROPHONE -> Triple(
            Icons.Default.Mic,
            "麦克风权限",
            "录制语音消息"
        )
        PermissionManager.PermissionType.NOTIFICATION -> Triple(
            Icons.Default.Notifications,
            "通知权限",
            "发送消息提醒"
        )
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF2196F3)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}
