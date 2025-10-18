# 修复 RTC SDK Native 库问题

## 问题原因

Android应用无法加载 `libvolcenginertc.so`，错误信息：
```
dlopen failed: library "libvolcenginertc.so" not found
```

**根本原因**: 本地的 `VolcEngineRTC-lite.aar` 文件缺少 native 库（.so文件）

## 解决方案

已将 build.gradle.kts 中的依赖从本地AAR改为Maven仓库：

### 修改前
```kotlin
// VolcEngineRTC SDK for video calling
implementation(files("libs/VolcEngineRTC-lite.aar"))
```

### 修改后
```kotlin
// VolcEngineRTC SDK for video calling - 使用完整SDK从Maven仓库
implementation("com.volcengine.rtc:VolcEngineRTC:3.58.1")
```

## 下一步操作

### 1. 同步Gradle依赖

在Android Studio中执行：
```
File → Sync Project with Gradle Files
```

或在命令行执行：
```bash
cd C:\Users\Administrator\IdeaProjects\social-app-android-backend
gradlew --refresh-dependencies build
```

### 2. 重新编译并安装APK

```bash
gradlew clean
gradlew assembleDebug
gradlew installDebug
```

### 3. 验证修复

1. 打开Android应用
2. 登录账号（video_caller 或 video_receiver）
3. 点击用户卡片进入详情页
4. 点击"视频通话"按钮
5. **应该能成功初始化RTC Engine**，不再出现 `libvolcenginertc.so not found` 错误

## Maven仓库配置

VolcEngine Maven仓库已在 `settings.gradle.kts` 中配置：
```kotlin
maven {
    url = uri("https://artifact.bytedance.com/repository/Volcengine/")
}
```

## RTC SDK版本说明

- **当前使用**: `com.volcengine.rtc:VolcEngineRTC:3.58.1`
- **包含内容**:
  - Java/Kotlin API
  - Native 库 (armeabi-v7a, arm64-v8a, x86, x86_64)
  - 完整的RTC功能

## 备用方案

如果Maven仓库下载失败，可以：

1. **使用RTCVideoCall-Android项目的配置**:
   - 复制 `RTCVideoCall-Android/app/libs/` 下的所有文件
   - 使用 `fileTree` 方式加载所有AAR

2. **手动下载完整SDK**:
   - 访问: https://www.volcengine.com/docs/6348/68916
   - 下载包含native库的完整SDK
   - 解压并放入 `app/libs/` 目录
   - 使用 `implementation(files(...))` 引用

## 技术说明

### 为什么本地AAR缺少native库？

`VolcEngineRTC-lite.aar` 是一个精简版本，只包含：
- classes.jar (Java/Kotlin代码)
- AndroidManifest.xml
- R.txt (资源)

**缺少的内容**:
- jni/ 目录
- libvolcenginertc.so (各种架构的native库)

### 完整SDK包含的Native库

完整的VolcEngineRTC SDK包含以下架构的.so文件：
- `armeabi-v7a/libvolcenginertc.so` - 32位ARM设备
- `arm64-v8a/libvolcenginertc.so` - 64位ARM设备（大多数现代手机）
- `x86/libvolcenginertc.so` - x86模拟器
- `x86_64/libvolcenginertc.so` - x86_64模拟器

## 已完成的工作

✅ 后端视频通话API已修复并正常工作
✅ GET /api/call/rate-info 正常
✅ POST /api/call/initiate 正常
✅ 通话会话成功创建到数据库
✅ Android客户端成功调用API
⏳ 等待Gradle同步下载完整RTC SDK

## 参考资料

- [VolcEngine RTC SDK文档](https://www.volcengine.com/docs/6348/68916)
- [RTCVideoCall-Android CLAUDE.md](RTCVideoCall-Android/CLAUDE.md)
- [VIDEO_CALL_SETUP.md](VIDEO_CALL_SETUP.md)
