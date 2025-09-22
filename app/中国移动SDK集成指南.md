# 中国移动号卡认证SDK集成指南

## 1. 申请开发者账号和配置

### 1.1 注册开发者账号
1. 访问 [中国移动开发者平台](https://dev.10086.cn/)
2. 注册开发者账号并完成实名认证
3. 申请"号卡认证"服务

### 1.2 获取配置信息
在开发者控制台获取以下信息：
- **App ID**: 应用唯一标识
- **App Secret**: 应用密钥
- **服务类型**: 号卡认证
- **认证模式**: 一键登录/本机号码校验

## 2. 下载官方SDK

### 2.1 Android SDK
- 下载地址: 开发者平台 -> 产品服务 -> 号卡认证 -> 下载SDK
- 支持版本: Android 4.4+ (API 19+)
- 文件: `cmcc-auth-sdk-android-x.x.x.aar`

### 2.2 后端SDK
- 下载地址: 开发者平台 -> 产品服务 -> 号卡认证 -> 服务端SDK
- 支持语言: Java/PHP/Python/Node.js
- 文件: `cmcc-auth-sdk-java-x.x.x.jar`

## 3. 集成步骤

### 3.1 Android端集成

#### 3.1.1 添加SDK依赖
```gradle
// 在 app/build.gradle.kts 中添加
dependencies {
    // 中国移动号卡认证SDK
    implementation files('libs/cmcc-auth-sdk-android-x.x.x.aar')
    // 或者使用Maven仓库（如果支持）
    // implementation 'com.cmcc.auth:cmcc-auth-sdk:1.0.0'
}
```

#### 3.1.2 添加权限
```xml
<!-- 在 AndroidManifest.xml 中添加 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
```

#### 3.1.3 初始化SDK
```kotlin
// 在Application类中初始化
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化中国移动认证SDK
        CmccAuthManager.getInstance().init(this, "your_app_id")
    }
}
```

### 3.2 后端集成

#### 3.2.1 添加SDK依赖
```gradle
// 在 SocialMeet/build.gradle.kts 中添加
dependencies {
    // 中国移动号卡认证后端SDK
    implementation files('libs/cmcc-auth-sdk-java-x.x.x.jar')
}
```

#### 3.2.2 配置参数
```yaml
# 在 application.yml 中配置
cmcc:
  card-auth:
    app-id: "your_app_id"
    app-secret: "your_app_secret"
    api-url: "https://api.cmcc.com/auth"
    service-type: "card_auth"
    auth-mode: "one_click"
    supported-scenarios: ["login", "register", "verify"]
```

## 4. 代码实现

### 4.1 Android端实现
```kotlin
// 一键登录
CmccAuthManager.getInstance().oneClickLogin(object : CmccAuthCallback {
    override fun onSuccess(result: CmccAuthResult) {
        // 登录成功，获取手机号
        val phoneNumber = result.phoneNumber
        // 调用后端验证接口
        verifyWithBackend(phoneNumber, result.token)
    }
    
    override fun onError(error: CmccAuthError) {
        // 处理错误
        handleAuthError(error)
    }
})
```

### 4.2 后端实现
```java
// 验证客户端token
public boolean verifyClientToken(String phoneNumber, String token) {
    try {
        // 调用中国移动服务端验证接口
        CmccAuthClient client = new CmccAuthClient(appId, appSecret);
        CmccAuthResponse response = client.verifyToken(phoneNumber, token);
        
        return response.isSuccess();
    } catch (Exception e) {
        log.error("验证客户端token失败", e);
        return false;
    }
}
```

## 5. 测试和调试

### 5.1 测试环境
- 使用真实的中国移动SIM卡
- 确保网络环境支持运营商认证
- 在真机上测试，模拟器可能不支持

### 5.2 常见问题
1. **网络问题**: 确保设备能访问运营商网络
2. **权限问题**: 检查READ_PHONE_STATE权限
3. **配置问题**: 验证App ID和App Secret是否正确
4. **SDK版本**: 使用最新版本的SDK

## 6. 注意事项

1. **隐私合规**: 遵守《个人信息保护法》等相关法规
2. **用户授权**: 明确告知用户认证目的和数据处理方式
3. **安全存储**: 妥善保管App Secret等敏感信息
4. **错误处理**: 提供完善的错误处理和用户提示

## 7. 联系支持

- 官方文档: https://dev.10086.cn/docs
- 技术支持: 开发者平台在线客服
- 问题反馈: 开发者社区论坛
