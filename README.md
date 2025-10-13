# SocialMeet Android 客户端

## 📱 项目简介

SocialMeet Android 客户端是一个现代化的社交交友应用，采用 Kotlin + Jetpack Compose 混合架构开发。

## 🎯 项目特点

- **混合UI架构**: 传统 XML Views + Jetpack Compose
- **MVVM模式**: ViewModel + LiveData/StateFlow
- **现代化技术栈**: Kotlin Coroutines + Retrofit + Material3
- **完整功能**: 社交、消息、通话、支付等完整体验

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1+)
- JDK 17+
- Android SDK 34
- Gradle 8.9+

### 构建步骤

1. **配置后端地址**

编辑 `app/src/main/java/com/example/myapplication/network/NetworkConfig.java`:

```java
public static final String BASE_URL = "http://localhost:8080/api/";
```

如果使用模拟器，将 `localhost` 改为 `10.0.2.2`

2. **构建 APK**

```bash
# Debug 版本
gradlew assembleDebug

# Release 版本
gradlew assembleRelease
```

3. **安装到设备**

```bash
# 通过 USB 安装
gradlew installDebug

# 或手动安装
adb install app/build/outputs/apk/debug/app-debug.apk
```

4. **启动应用**

首次启动需要后端服务运行在 http://localhost:8080

## 📂 项目结构

```
app/
├── src/main/java/com/example/myapplication/
│   ├── compose/              # Jetpack Compose 宿主
│   │   ├── MessageComposeHost.kt
│   │   ├── ProfileComposeHost.kt
│   │   └── SquareComposeHost.kt
│   ├── ui/                   # Compose UI 组件
│   │   ├── screens/          # 屏幕页面
│   │   ├── components/       # 可复用组件
│   │   └── theme/            # Material3 主题
│   ├── viewmodel/            # MVVM ViewModel
│   │   ├── MessageViewModel.kt
│   │   ├── ProfileViewModel.kt
│   │   └── SquareViewModel.kt
│   ├── network/              # 网络层
│   │   ├── RetrofitClient.java
│   │   ├── ApiService.java
│   │   └── NetworkService.kt
│   ├── dto/                  # 数据传输对象
│   ├── model/                # 本地数据模型
│   ├── auth/                 # 认证模块
│   ├── payment/              # 支付模块
│   └── util/                 # 工具类
├── src/main/res/             # 资源文件
│   ├── layout/               # XML 布局
│   ├── drawable/             # 图片资源
│   ├── values/               # 主题、字符串等
│   └── xml/                  # 配置文件
└── build.gradle.kts          # 构建配置
```

## 🔧 主要功能

### 核心功能

- 🔐 **用户认证**: 手机号登录、一键登录、人脸认证
- 💬 **即时消息**: 实时聊天、表情、图片、语音消息
- 🎭 **社交广场**: 发布动态、点赞评论、关注好友
- 📞 **音视频通话**: 1对1语音/视频通话
- 👤 **个人中心**: 资料编辑、相册管理、隐私设置

### 增值功能

- 💎 **VIP会员**: 会员权益、特权功能
- 💰 **钱包充值**: 支付宝支付、虚拟货币
- 🎁 **礼物系统**: 发送礼物、礼物特效
- 🏆 **财富等级**: 等级系统、成长值

## ⚙️ 配置说明

### 网络配置

`network/NetworkConfig.java`:
```java
// 本地开发
BASE_URL = "http://10.0.2.2:8080/api/"  // 模拟器

// 生产环境
BASE_URL = "https://api.socialmeet.com/api/"
```

### 权限配置

`AndroidManifest.xml` 中已配置必要权限:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

### 依赖库版本

- **Kotlin**: 1.9.10
- **Compose**: 1.6.0
- **Material3**: 1.10.0
- **Retrofit**: 2.9.0
- **Coil**: 2.5.0

## 🎨 UI 架构

### 混合架构说明

项目采用 XML + Compose 混合架构:

- **Activity (XML)**: 作为容器和导航框架
- **Compose UI**: 复杂交互界面使用 Compose 实现
- **ComposeHost**: 桥接 Activity 和 Compose

示例:
```kotlin
// Activity 中使用 Compose
setContent {
    SocialMeetTheme {
        SquareScreen(viewModel)
    }
}
```

### 主题定制

`ui/theme/` 目录包含完整的 Material3 主题配置。

## 📦 构建和发布

### 构建类型

```bash
# Debug 构建 (开发调试)
gradlew assembleDebug

# Release 构建 (正式发布)
gradlew assembleRelease

# 指定 ABI
gradlew assembleArm64-v8aDebug     # 64位 ARM
gradlew assembleArmeabi-v7aDebug   # 32位 ARM
```

### 签名配置

创建 `keystore.properties`:
```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=your_key_alias
storeFile=path/to/keystore.jks
```

### APK 优化

当前 APK 约 74MB，主要由于:
- 阿里云 SDK (24MB): 人脸识别、OCR、NFC
- 多 ABI 支持: arm64-v8a, armeabi-v7a

优化建议:
1. 使用 APK Split 分离不同 ABI
2. 按需加载阿里云 SDK
3. 启用 ProGuard 混淆压缩

## 🔍 开发调试

### 日志查看

```bash
# 查看应用日志
adb logcat -s MyApplication

# 查看网络请求
# 在 NetworkConfig 中启用 OkHttp 日志拦截器
```

### 调试工具

- **Layout Inspector**: 查看视图层级
- **Network Profiler**: 分析网络请求
- **Database Inspector**: 查看本地数据库

## 🛠️ 故障排查

### APK 安装失败

```bash
# 卸载旧版本
adb uninstall com.example.myapplication

# 清理缓存重新构建
gradlew clean assembleDebug
```

### 网络请求失败

1. 检查后端服务是否启动
2. 确认网络配置地址正确
3. 检查 Android 权限是否授予
4. 查看 Logcat 网络请求日志

### Compose 预览不显示

1. 确保使用 `@Preview` 注解
2. 检查 Android Studio 版本
3. Invalidate Caches / Restart

## 🔗 相关项目

- **后端服务**: `C:\Users\Administrator\IdeaProjects\social-meet-backend`
- **后端 README**: ../social-meet-backend/README.md

## 📚 开发文档

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material3 Design](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Retrofit](https://square.github.io/retrofit/)

## 🤝 开发规范

### 代码风格

- 使用 Kotlin 官方代码风格
- 遵循 Material Design 设计规范
- Compose 使用声明式 UI 思维

### 命名规范

- Activity: `XxxActivity`
- ViewModel: `XxxViewModel`
- Composable: `XxxScreen` / `XxxComponent`
- DTO: `XxxDTO`

### 提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式
refactor: 重构
test: 测试
chore: 构建/工具
```

## 📱 设备兼容性

- **最低版本**: Android 7.0 (API 24)
- **目标版本**: Android 14 (API 34)
- **推荐设备**: 4GB+ RAM, 1080p+ 屏幕

## 📧 技术支持

如有问题:
1. 检查后端服务是否正常运行
2. 查看 Logcat 错误日志
3. 参考项目文档和示例代码

---

**版本**: v1.0.0
**最后更新**: 2025-10-13
**Android Target SDK**: 34
**Kotlin 版本**: 1.9.10
**Compose 版本**: 1.6.0
