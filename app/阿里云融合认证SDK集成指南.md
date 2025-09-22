# 阿里云融合认证SDK集成指南

## ✅ 已完成配置

### 1. 应用信息
- **应用名称**: SocialMeet手机认证
- **包名**: com.example.myapplication
- **SDK版本**: fusionauth-1.2.12-online-release

### 2. 配置文件已更新
- ✅ `app/build.gradle.kts` - Android依赖配置
- ✅ `app/src/main/java/com/example/myapplication/PhoneIdentityAuthActivity.kt` - Android实现

## 🚀 下一步操作

### 1. 获取阿里云认证服务配置
1. 登录 [阿里云控制台](https://dypns.console.aliyun.com/)
2. 进入"号码认证服务" → "应用管理"
3. 创建应用或选择现有应用
4. 获取以下配置信息：
   - **AccessKey ID**
   - **AccessKey Secret**
   - **Scheme Code** (应用标识)
   - **Auth Token** (认证令牌)

### 2. 配置后端服务
在 `SocialMeet/src/main/resources/application.yml` 中添加：
```yaml
aliyun:
  fusion:
    access-key-id: your_access_key_id
    access-key-secret: your_access_key_secret
    scheme-code: your_scheme_code
    auth-token: your_auth_token
```

### 3. 实现后端Token获取接口
创建API接口获取认证Token：
```java
@RestController
@RequestMapping("/api/auth")
public class AliyunAuthController {
    
    @GetMapping("/token")
    public ResponseEntity<String> getAuthToken() {
        // 调用阿里云API获取认证Token
        // 返回给前端使用
    }
}
```

### 4. 更新Android端配置
在 `PhoneIdentityAuthActivity.kt` 中：
1. 取消注释SDK集成代码
2. 替换 `"your_auth_token_here"` 为实际Token
3. 替换 `"your_scheme_code"` 为实际Scheme Code

## 📱 功能说明

### 一键登录流程
1. 用户点击"一键认证"按钮
2. 调用阿里云融合认证SDK
3. 显示运营商授权页面
4. 用户完成授权后获取手机号
5. 后端验证Token有效性
6. 认证成功，更新用户状态

### 支持的运营商
- 中国移动
- 中国联通  
- 中国电信

### 支持场景
- 一键登录
- 本机号码校验
- 短信验证码登录

## 🔧 故障排除

### 常见问题
1. **SDK初始化失败**: 检查AccessKey和Secret是否正确
2. **Token获取失败**: 检查网络连接和API配置
3. **授权页无法显示**: 检查Scheme Code配置
4. **认证失败**: 确认使用对应运营商SIM卡

### 调试方法
1. 开启SDK日志：`AlicomFusionLog.setLogEnable(true)`
2. 查看Android Logcat输出
3. 检查后端服务日志
4. 使用阿里云控制台查看调用统计

## 📞 技术支持

- 阿里云号码认证服务: https://dypns.console.aliyun.com/
- 技术支持: 阿里云工单系统
- 文档中心: 查看最新API文档

## 🎉 完成状态

- ✅ SDK依赖已配置
- ✅ Android代码已更新
- ⏳ 等待阿里云服务配置
- ⏳ 等待后端Token接口实现
- ⏳ 等待真实环境测试

一旦完成配置，你的SocialMeet应用就可以使用阿里云的一键登录服务了！
