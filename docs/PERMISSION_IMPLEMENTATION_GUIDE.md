# Android权限管理实现方案

## 主流应用的权限策略

### 1. 微信 (WeChat)
**需要的权限：**
- 相机权限：拍照、扫码
- 存储权限：保存图片、视频
- 麦克风权限：语音消息
- 位置权限：附近的人
- 联系人权限：添加好友
- 电话权限：语音通话

**实现特点：**
- 分步请求权限，避免一次性请求过多
- 权限被拒绝时提供替代方案
- 详细的权限说明和引导

### 2. 抖音 (TikTok)
**需要的权限：**
- 相机权限：拍摄视频
- 麦克风权限：录制音频
- 存储权限：保存视频
- 位置权限：同城推荐
- 通知权限：消息提醒

**实现特点：**
- 权限请求与功能紧密结合
- 使用引导页面解释权限用途
- 权限被拒绝时降级功能

### 3. 小红书
**需要的权限：**
- 相机权限：拍照分享
- 存储权限：访问相册
- 位置权限：地点标记
- 通知权限：消息推送

**实现特点：**
- 优雅的权限请求界面
- 权限说明清晰易懂
- 支持手动设置权限

## 完整实现方案

### 1. AndroidManifest.xml 权限声明

```xml
<!-- 基础权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- 相机权限 -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Android 13+ 新权限 -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Android 12 及以下权限 -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
    android:maxSdkVersion="32" />

<!-- 位置权限 -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- 通讯权限 -->
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.CALL_PHONE" />

<!-- 麦克风权限 -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

### 2. 权限管理器实现

```kotlin
class PermissionManager(private val activity: ComponentActivity) {
    
    enum class PermissionType {
        CAMERA, STORAGE, MEDIA_IMAGES, LOCATION, 
        CONTACTS, PHONE, MICROPHONE, NOTIFICATION
    }
    
    // 检查权限
    fun hasPermission(types: List<PermissionType>): Boolean
    
    // 请求权限
    fun requestPermissions(types: List<PermissionType>, callback: (Boolean) -> Unit)
    
    // 检查并请求权限
    fun checkAndRequestPermissions(
        types: List<PermissionType>,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    )
}
```

### 3. 权限对话框组件

```kotlin
@Composable
fun PermissionDialog(
    isVisible: Boolean,
    permissionTypes: List<PermissionManager.PermissionType>,
    onGrant: () -> Unit,
    onDeny: () -> Unit,
    onOpenSettings: () -> Unit
)
```

### 4. 使用示例

```kotlin
class EditAlbumActivity : ComponentActivity() {
    private val permissionManager = PermissionManager(this)
    
    private fun uploadPhoto() {
        permissionManager.checkAndRequestPermissions(
            types = CommonPermissionGroups.PHOTO_UPLOAD,
            onGranted = {
                // 权限已授予，执行上传
                openImagePicker()
            },
            onDenied = {
                // 权限被拒绝，显示说明
                showPermissionDeniedDialog()
            }
        )
    }
}
```

## 最佳实践

### 1. 权限请求时机
- **功能触发时**：用户点击相关功能时再请求权限
- **分步请求**：避免一次性请求过多权限
- **合理引导**：在请求前解释权限用途

### 2. 权限被拒绝处理
- **提供替代方案**：权限被拒绝时提供其他选择
- **引导手动设置**：提供跳转到设置页面的选项
- **优雅降级**：禁用相关功能但不影响其他功能

### 3. 用户体验优化
- **清晰的说明**：用简单易懂的语言解释权限用途
- **视觉引导**：使用图标和动画增强用户体验
- **一致性**：保持权限请求界面与应用整体风格一致

### 4. 技术实现要点
- **版本兼容**：支持Android 13+的新权限系统
- **状态管理**：正确处理权限状态变化
- **错误处理**：妥善处理权限请求异常情况

## 常见问题解决

### 1. Android 13+ 权限问题
- 使用 `READ_MEDIA_IMAGES` 替代 `READ_EXTERNAL_STORAGE`
- 添加 `POST_NOTIFICATIONS` 权限
- 更新权限检查逻辑

### 2. 权限被永久拒绝
- 检查 `shouldShowRequestPermissionRationale()` 返回值
- 提供跳转到设置页面的选项
- 在设置页面提供详细说明

### 3. 权限状态不一致
- 在 `onResume()` 中重新检查权限状态
- 监听权限变化并更新UI
- 提供刷新权限状态的机制
