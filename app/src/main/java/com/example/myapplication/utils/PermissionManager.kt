package com.example.myapplication.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * 权限管理器 - 参考主流应用的权限处理方案
 * 支持Android 13+的新权限系统
 */
class PermissionManager(private val activity: ComponentActivity) {
    
    // 权限请求结果回调
    private var permissionCallback: ((Boolean) -> Unit)? = null
    
    // 权限请求启动器
    private val requestPermissionLauncher: ActivityResultLauncher<Array<String>> = 
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            permissionCallback?.invoke(allGranted)
        }
    
    /**
     * 权限类型枚举
     */
    enum class PermissionType {
        CAMERA,                    // 相机权限
        STORAGE,                   // 存储权限（Android 12及以下）
        MEDIA_IMAGES,              // 媒体图片权限（Android 13+）
        LOCATION,                  // 位置权限
        CONTACTS,                  // 联系人权限
        PHONE,                     // 电话权限
        MICROPHONE,                // 麦克风权限
        NOTIFICATION               // 通知权限
    }
    
    /**
     * 获取权限对应的字符串数组
     */
    private fun getPermissionStrings(types: List<PermissionType>): Array<String> {
        return types.mapNotNull { type ->
            when (type) {
                PermissionType.CAMERA -> Manifest.permission.CAMERA
                PermissionType.STORAGE -> if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                } else null
                PermissionType.MEDIA_IMAGES -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else null
                PermissionType.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
                PermissionType.CONTACTS -> Manifest.permission.READ_CONTACTS
                PermissionType.PHONE -> Manifest.permission.CALL_PHONE
                PermissionType.MICROPHONE -> Manifest.permission.RECORD_AUDIO
                PermissionType.NOTIFICATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.POST_NOTIFICATIONS
                } else null
            }
        }.toTypedArray()
    }
    
    /**
     * 检查权限是否已授予
     */
    fun hasPermission(types: List<PermissionType>): Boolean {
        val permissions = getPermissionStrings(types)
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    /**
     * 请求权限
     */
    fun requestPermissions(
        types: List<PermissionType>,
        callback: (Boolean) -> Unit
    ) {
        permissionCallback = callback
        
        val permissions = getPermissionStrings(types)
        if (permissions.isEmpty()) {
            callback(true)
            return
        }
        
        // 检查是否已经拥有所有权限
        if (hasPermission(types)) {
            callback(true)
            return
        }
        
        // 请求权限
        requestPermissionLauncher.launch(permissions)
    }
    
    /**
     * 检查权限并处理结果
     */
    fun checkAndRequestPermissions(
        types: List<PermissionType>,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        if (hasPermission(types)) {
            onGranted()
        } else {
            requestPermissions(types) { granted ->
                if (granted) {
                    onGranted()
                } else {
                    onDenied()
                }
            }
        }
    }
    
    /**
     * 打开应用设置页面
     */
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
        }
        activity.startActivity(intent)
    }
    
    /**
     * 获取权限说明文本
     */
    fun getPermissionDescription(types: List<PermissionType>): String {
        val descriptions = mutableListOf<String>()
        
        types.forEach { type ->
            when (type) {
                PermissionType.CAMERA -> descriptions.add("相机权限：用于拍摄照片和视频")
                PermissionType.STORAGE -> descriptions.add("存储权限：用于访问相册和保存文件")
                PermissionType.MEDIA_IMAGES -> descriptions.add("媒体权限：用于访问相册中的图片")
                PermissionType.LOCATION -> descriptions.add("位置权限：用于提供基于位置的服务")
                PermissionType.CONTACTS -> descriptions.add("联系人权限：用于查找和邀请好友")
                PermissionType.PHONE -> descriptions.add("电话权限：用于拨打语音通话")
                PermissionType.MICROPHONE -> descriptions.add("麦克风权限：用于录制语音消息")
                PermissionType.NOTIFICATION -> descriptions.add("通知权限：用于发送重要消息提醒")
            }
        }
        
        return descriptions.joinToString("\n")
    }
    
    /**
     * 检查权限是否被永久拒绝
     */
    fun isPermissionPermanentlyDenied(types: List<PermissionType>): Boolean {
        val permissions = getPermissionStrings(types)
        return permissions.any { permission ->
            !activity.shouldShowRequestPermissionRationale(permission)
        }
    }
}

/**
 * 权限管理扩展函数
 */
fun ComponentActivity.getPermissionManager(): PermissionManager {
    return PermissionManager(this)
}

/**
 * 常用权限组合
 */
object CommonPermissionGroups {
    // 相册和相机权限（用于上传照片）
    val PHOTO_UPLOAD = listOf(
        PermissionManager.PermissionType.CAMERA,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionManager.PermissionType.MEDIA_IMAGES
        } else {
            PermissionManager.PermissionType.STORAGE
        }
    )
    
    // 位置服务权限
    val LOCATION = listOf(PermissionManager.PermissionType.LOCATION)
    
    // 通讯权限
    val COMMUNICATION = listOf(
        PermissionManager.PermissionType.CONTACTS,
        PermissionManager.PermissionType.PHONE
    )
    
    // 媒体录制权限
    val MEDIA_RECORDING = listOf(
        PermissionManager.PermissionType.CAMERA,
        PermissionManager.PermissionType.MICROPHONE
    )
    
    // 通知权限
    val NOTIFICATION = listOf(PermissionManager.PermissionType.NOTIFICATION)
}
