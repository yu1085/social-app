# 后端SDK库文件目录

## 中国移动号卡认证后端SDK

### Java SDK
- 文件名: `cmcc-auth-sdk-java-x.x.x.jar`
- 下载地址: 中国移动开发者平台
- 集成方式: 将JAR文件放入此目录，在build.gradle.kts中引用

### 集成步骤
1. 从中国移动开发者平台下载最新后端SDK
2. 将JAR文件放入此目录
3. 在 `SocialMeet/build.gradle.kts` 中添加依赖：
   ```kotlin
   dependencies {
       implementation files('libs/cmcc-auth-sdk-java-x.x.x.jar')
   }
   ```
4. 取消注释 `CmccCardAuthService.java` 中的SDK相关代码

### 配置参数
在 `application.yml` 中配置真实的App ID和Secret：
```yaml
cmcc:
  card-auth:
    app-id: "your_real_app_id"
    app-secret: "your_real_app_secret"
    api-url: "https://api.cmcc.com/auth"
```

### 注意事项
- 确保SDK版本与Java版本兼容
- 妥善保管App Secret等敏感信息
- 在生产环境中使用HTTPS
