# SDK 库文件目录

## 中国移动号卡认证SDK

### Android SDK
- 文件名: `cmcc-auth-sdk-android-x.x.x.aar`
- 下载地址: 中国移动开发者平台
- 集成方式: 将AAR文件放入此目录，在build.gradle.kts中引用

### 集成步骤
1. 从中国移动开发者平台下载最新SDK
2. 将AAR文件放入此目录
3. 在 `app/build.gradle.kts` 中添加依赖：
   ```kotlin
   dependencies {
       implementation files('libs/cmcc-auth-sdk-android-x.x.x.aar')
   }
   ```
4. 取消注释 `PhoneIdentityAuthActivity.kt` 中的SDK相关代码

### 注意事项
- 确保SDK版本与目标Android版本兼容
- 需要添加相应的权限声明
- 在真机上测试，模拟器可能不支持运营商认证
