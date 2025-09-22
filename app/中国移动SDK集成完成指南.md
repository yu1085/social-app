# 中国移动号卡认证SDK集成完成指南

## ✅ 已完成配置

### 1. 应用信息
- **应用名称**: SocialMeet手机认证
- **APP ID**: 300013116387
- **APP Secret**: 985E36132015F45031E9D653343C6DBD
- **包名**: com.example.myapplication
- **签名**: 3ADE57AC69D149E797A3D874A52B3D96925E2E86DC7AF21A2DC975324E6D441A

### 2. 配置文件已更新
- ✅ `SocialMeet/src/main/resources/application.yml` - 后端配置
- ✅ `app/src/main/java/com/example/myapplication/PhoneIdentityAuthActivity.kt` - Android配置

## 🚀 下一步操作

### 1. 下载官方SDK
1. 登录 [中国移动开发者平台](https://dev.10086.cn/)
2. 进入"应用管理" → "SocialMeet手机认证"
3. 点击"能力配置"或"SDK下载"
4. 下载以下文件：
   - **Android SDK**: `cmcc-auth-sdk-android-x.x.x.aar`
   - **后端SDK**: `cmcc-auth-sdk-java-x.x.x.jar`

### 2. 集成Android SDK
1. 将AAR文件放入 `app/libs/` 目录
2. 在 `app/build.gradle.kts` 中添加依赖：
   ```kotlin
   dependencies {
       implementation files('libs/cmcc-auth-sdk-android-x.x.x.aar')
   }
   ```
3. 取消注释 `PhoneIdentityAuthActivity.kt` 中的SDK代码

### 3. 集成后端SDK
1. 将JAR文件放入 `SocialMeet/libs/` 目录
2. 在 `SocialMeet/build.gradle.kts` 中添加依赖：
   ```kotlin
   dependencies {
       implementation files('libs/cmcc-auth-sdk-java-x.x.x.jar')
   }
   ```
3. 取消注释 `CmccCardAuthService.java` 中的SDK代码

### 4. 添加必要权限
在 `app/src/main/AndroidManifest.xml` 中添加：
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
```

## 🧪 测试步骤

### 1. 编译项目
```bash
# 编译Android应用
.\gradle-8.9\bin\gradle.bat :app:assembleDebug

# 编译后端服务
cd SocialMeet
.\gradlew.bat build
```

### 2. 测试功能
1. 安装APK到Android手机（需要中国移动SIM卡）
2. 启动应用，进入手机身份认证界面
3. 点击"一键认证"按钮
4. 验证认证流程是否正常

## 📱 功能说明

### 号卡认证流程
1. 用户点击"一键认证"
2. 调用中国移动SDK进行号卡认证
3. 获取用户手机号和认证token
4. 后端验证token的有效性
5. 认证成功，更新用户状态

### 支持场景
- PC端
- 手机端
- H5
- 短链

## 🔧 故障排除

### 常见问题
1. **网络连接失败**: 检查网络权限和网络状态
2. **认证失败**: 确认使用中国移动SIM卡
3. **SDK初始化失败**: 检查APP ID和Secret是否正确
4. **签名验证失败**: 确认应用签名与平台配置一致

### 调试方法
1. 查看Android Logcat输出
2. 检查后端服务日志
3. 使用中国移动提供的调试工具

## 📞 技术支持

- 中国移动开发者平台: https://dev.10086.cn/
- 技术支持: 开发者平台在线客服
- 文档中心: 查看最新API文档

## 🎉 完成状态

- ✅ 应用创建成功
- ✅ 配置信息已更新
- ✅ 代码结构已准备
- ⏳ 等待SDK下载和集成
- ⏳ 等待真实环境测试

一旦完成SDK集成，你的SocialMeet应用就可以使用中国移动的号卡认证服务了！
