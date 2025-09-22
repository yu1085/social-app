# 中国联通沃登录SDK集成指南

## 1. 申请开发者账号和配置

### 1.1 注册开发者账号
1. 访问 [中国联通开发者平台](https://dev.10086.cn/) (具体网址需通过官网查询)
2. 注册开发者账号并完成实名认证
3. 申请"沃登录"服务

### 1.2 获取配置信息
在开发者控制台获取以下信息：
- **AppKey**: 应用唯一标识
- **AppSecret**: 应用密钥
- **服务类型**: 沃登录SDK
- **认证模式**: 一键登录/隐式登录

## 2. 下载官方SDK

### 2.1 Android SDK
- **下载地址**: 开发者平台 -> 文档中心 -> 沃登录SDK -> Android版本
- **支持版本**: Android 4.4+ (API 19+)
- **文件**: `unicom-wologin-android-x.x.x.aar`
- **功能特性**: 支持一键登录、隐式登录等模式

### 2.2 后端SDK
- **下载地址**: 开发者平台 -> 文档中心 -> 沃登录SDK -> 服务端SDK
- **支持语言**: Java/PHP/Python/Node.js
- **文件**: `unicom-wologin-java-x.x.x.jar`

## 3. 集成步骤

### 3.1 Android端集成

#### 3.1.1 添加SDK依赖
```gradle
// 在 app/build.gradle.kts 中添加
dependencies {
    // 中国联通沃登录SDK
    implementation files('libs/unicom-wologin-android-x.x.x.aar')
    // 或者使用Maven仓库（如果支持）
    // implementation 'com.unicom.wologin:unicom-wologin-sdk:1.0.0'
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
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

#### 3.1.3 初始化SDK
```kotlin
// 在Application类中初始化
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化中国联通沃登录SDK
        UnicomWologinManager.getInstance().init(this, "your_app_key")
    }
}
```

#### 3.1.4 实现一键登录
```kotlin
class PhoneIdentityAuthActivity : ComponentActivity() {
    
    /**
     * 执行中国联通一键登录
     */
    private suspend fun performUnicomOneClickAuth(
        phone: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val wologinHelper = UnicomWologinHelper.getInstance(this)
        
        // 设置授权页面监听
        wologinHelper.setAuthPageListener(object : UnicomAuthPageListener {
            override fun onAuthPageComplete(resultCode: String) {
                if (resultCode == "200000") {
                    // 授权页面成功拉起
                    println("联通授权页面成功拉起")
                }
            }
        })
        
        // 实现取号回调
        val listener = object : UnicomTokenListener {
            override fun onGetTokenComplete(requestCode: Int, result: UnicomAuthResult?) {
                if (result != null && result.isSuccess) {
                    // 取号成功
                    val token = result.token
                    val phoneNumber = result.phoneNumber ?: phone
                    
                    // 调用后端验证token
                    verifyTokenWithBackend(phoneNumber, token, onSuccess, onError)
                } else {
                    // 取号失败
                    val errorMsg = result?.errorMessage ?: "联通取号失败"
                    onError(errorMsg)
                }
            }
        }
        
        // 调用一键登录方法
        wologinHelper.loginAuth("your_app_key", "your_app_secret", listener, 1002)
    }
}
```

### 3.2 后端集成

#### 3.2.1 添加SDK依赖
```gradle
// 在 SocialMeet/build.gradle.kts 中添加
dependencies {
    // 中国联通沃登录服务端SDK
    implementation files('libs/unicom-wologin-java-x.x.x.jar')
}
```

#### 3.2.2 配置参数
```yaml
# 在 application.yml 中添加
unicom:
  wologin:
    app-key: your_app_key
    app-secret: your_app_secret
    api-url: https://api.10010.com
    # 沃登录服务配置
    service-type: WOLOGIN
    # 认证模式: SDK模式
    auth-mode: SDK
    # 支持场景: 一键登录、隐式登录
    supported-modes: ["ONE_CLICK", "IMPLICIT"]
```

#### 3.2.3 实现服务类
```java
@Service
public class UnicomWologinService {
    
    @Value("${unicom.wologin.app-key}")
    private String appKey;
    
    @Value("${unicom.wologin.app-secret}")
    private String appSecret;
    
    @Value("${unicom.wologin.api-url}")
    private String apiUrl;
    
    /**
     * 验证联通沃登录token
     * @param token 前端获取的token
     * @return 验证结果
     */
    public UnicomAuthResult verifyToken(String token) {
        // 调用联通API验证token
        // 返回手机号验证结果
    }
}
```

## 4. 支持号段

### 中国联通号段
- **130-132**: 联通2G/3G号段
- **155-156**: 联通4G号段
- **185-186**: 联通4G号段
- **成功率**: 93%+

## 5. 功能特性

### 5.1 一键登录模式
- 用户点击按钮直接拉起授权页面
- 无需输入手机号和验证码
- 基于运营商网络验证

### 5.2 隐式登录模式
- 后台静默获取手机号
- 用户无感知验证过程
- 适合快速登录场景

## 6. 测试要求

### 6.1 测试环境
- 需要中国联通SIM卡的Android手机
- 确保网络连接正常
- 在真机上测试（模拟器可能不支持）

### 6.2 测试步骤
1. 安装APK到Android手机
2. 启动应用，进入手机身份认证界面
3. 点击"一键认证"按钮
4. 查看是否弹出中国联通授权页面
5. 确认授权后查看认证结果

## 7. 错误码说明

| 错误码 | 说明 | 处理方式 |
|--------|------|----------|
| 200000 | 成功 | 正常处理 |
| 200001 | 网络异常 | 检查网络连接 |
| 200002 | 取号失败 | 重试或降级到短信验证 |
| 200003 | 用户取消 | 用户主动取消操作 |
| 200004 | 超时 | 增加超时时间或重试 |

## 8. 技术支持

- **中国联通开发者平台**: https://dev.10086.cn/
- **沃登录SDK文档**: 开发者平台文档中心
- **技术支持**: 通过开发者平台提交工单

## 9. 注意事项

### 9.1 技术限制
- 需要联通网络环境
- 部分老设备可能不支持
- 需要用户授权才能获取手机号

### 9.2 合规要求
- 需要用户明确授权
- 保护用户隐私信息
- 符合相关安全标准

## 10. 集成完成检查清单

- [ ] 下载联通沃登录SDK文件
- [ ] 添加SDK依赖到build.gradle.kts
- [ ] 配置AndroidManifest.xml权限
- [ ] 实现Android端一键登录代码
- [ ] 配置后端application.yml
- [ ] 实现后端验证服务
- [ ] 真机测试验证功能
- [ ] 处理各种错误情况

一旦完成以上步骤，您的SocialMeet应用就可以使用中国联通的沃登录服务了！
