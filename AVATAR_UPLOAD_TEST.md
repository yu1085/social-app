# 头像上传功能测试指南

## 功能概述

已完成头像上传和显示功能的实现，包括：
1. **后端API**：头像文件上传、图片验证、文件存储
2. **Android客户端**：图片选择、压缩、上传、显示

---

## 后端实现

### 1. 文件上传配置
- **位置**：`backend/src/main/java/com/socialmeet/backend/config/FileUploadConfig.java`
- **功能**：
  - 配置上传目录：`uploads/avatars/`
  - 配置静态资源映射，使上传的文件可通过HTTP访问
  - 自动创建上传目录

### 2. 文件上传服务
- **位置**：`backend/src/main/java/com/socialmeet/backend/service/FileUploadService.java`
- **功能**：
  - 验证图片格式（jpg, jpeg, png, gif, bmp, webp）
  - 限制文件大小（最大5MB）
  - 生成唯一文件名（avatar_userId_timestamp_uuid.ext）
  - 保存文件并返回访问URL
  - 删除旧头像文件

### 3. 头像上传API
- **接口**：`POST /api/profile/upload-avatar`
- **请求头**：`Authorization: Bearer {token}`
- **请求体**：`multipart/form-data` (file字段)
- **响应**：
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "avatarUrl": "http://localhost:8080/uploads/avatars/avatar_1_20250120123456_abc123.jpg"
  }
}
```

---

## Android实现

### 1. 头像上传服务
- **位置**：`app/src/main/java/com/example/myapplication/service/AvatarUploadService.kt`
- **功能**：
  - 图片压缩（最大1024x1024）
  - EXIF旋转处理
  - JPEG质量压缩（85%）
  - 创建Multipart请求体
  - 临时文件管理

### 2. API接口
- **位置**：`app/src/main/java/com/example/myapplication/network/ApiService.java`
- **方法**：
```java
@Multipart
@POST("profile/upload-avatar")
Call<ApiResponse<Map<String, String>>> uploadAvatar(
    @Header("Authorization") String authHeader,
    @Part MultipartBody.Part file
);
```

### 3. UI实现
- **位置**：`app/src/main/java/com/example/myapplication/MyProfileEditActivity.kt`
- **功能**：
  - 圆形头像显示（使用Coil加载）
  - 点击头像选择图片
  - 上传进度显示
  - 编辑按钮图标
  - 实时更新头像

---

## 测试步骤

### 1. 启动后端服务

```bash
cd backend
../gradlew clean build
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

或使用PowerShell脚本：
```powershell
.\start_backend_with_profile.ps1
```

### 2. 验证后端配置

检查 `backend/src/main/resources/application.yml`：
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 20MB
      enabled: true

file:
  upload:
    path: uploads/
    avatar-path: uploads/avatars/

server:
  host: localhost
```

### 3. 构建Android应用

```bash
gradlew :app:assembleDebug
```

### 4. 在Android模拟器/真机上测试

#### 步骤 A：登录应用
1. 启动应用
2. 输入手机号登录（测试模式验证码：123456）

#### 步骤 B：进入资料编辑页面
1. 进入"我的"页面
2. 点击"编辑资料"或"我的资料"

#### 步骤 C：上传头像
1. 点击圆形头像区域
2. 从相册选择图片
3. 等待上传（显示上传进度）
4. 上传成功后，头像自动更新

#### 步骤 D：验证头像显示
1. 返回个人中心，查看头像是否更新
2. 重新进入资料编辑页面，验证头像持久化
3. 退出重新登录，验证头像仍然显示

---

## 测试用例

### 功能测试

| 测试项 | 测试步骤 | 预期结果 |
|--------|----------|----------|
| 选择图片 | 点击头像，从相册选择图片 | 图片选择器正常打开 |
| 图片压缩 | 选择大图片（>5MB） | 自动压缩到合理大小 |
| 上传进度 | 上传图片 | 显示加载动画 |
| 上传成功 | 等待上传完成 | Toast提示"头像上传成功"，头像更新 |
| 上传失败 | 断开网络上传 | 显示错误提示对话框 |
| 图片格式 | 上传不同格式图片 | 支持jpg, png, gif, webp等格式 |
| 图片旋转 | 上传带EXIF旋转信息的图片 | 自动旋转到正确方向 |
| 头像显示 | 查看个人中心 | 头像正确显示 |
| 持久化 | 重启应用 | 头像仍然显示 |

### 异常测试

| 测试项 | 测试步骤 | 预期结果 |
|--------|----------|----------|
| 未登录 | 未登录状态上传 | 提示"请先登录" |
| Token过期 | Token过期后上传 | 提示重新登录 |
| 网络断开 | 上传时断网 | 显示"网络错误"提示 |
| 文件过大 | 上传>10MB文件 | 后端返回"文件大小不能超过5MB" |
| 格式错误 | 上传非图片文件 | 后端返回"不支持的图片格式" |
| 服务器错误 | 后端服务未启动 | 显示"上传失败"提示 |

---

## API测试（使用Postman或curl）

### 1. 获取Token
```bash
curl -X POST http://localhost:8080/api/auth/send-code?phone=13800138000
curl -X POST http://localhost:8080/api/auth/login-with-code?phone=13800138000&code=123456
```

### 2. 上传头像
```bash
curl -X POST http://localhost:8080/api/profile/upload-avatar \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

### 3. 验证头像URL
访问返回的avatarUrl，验证图片是否可以正常访问：
```
http://localhost:8080/uploads/avatars/avatar_1_20250120123456_abc123.jpg
```

---

## 故障排查

### 问题1：上传失败，提示"未提供有效的认证令牌"
**原因**：Token无效或未登录
**解决**：
1. 检查AuthManager中的Token是否有效
2. 重新登录获取新Token

### 问题2：上传后图片无法访问
**原因**：静态资源映射未生效
**解决**：
1. 检查FileUploadConfig配置
2. 确认uploads目录已创建
3. 重启后端服务

### 问题3：图片上传很慢
**原因**：图片未压缩或网络慢
**解决**：
1. AvatarUploadService已实现自动压缩
2. 检查网络连接
3. 查看日志确认压缩是否生效

### 问题4：头像显示为空白
**原因**：Coil无法加载图片
**解决**：
1. 检查avatarUrl是否正确
2. 确认网络权限已授予
3. 查看Logcat中的Coil日志

---

## 日志查看

### 后端日志
关键日志标签：
- `FileUploadService` - 文件上传相关
- `ProfileController` - 头像上传API
- `ProfileService` - 用户资料更新

### Android日志
关键日志标签：
```bash
adb logcat | grep -E "AvatarUpload|ProfileEdit"
```

日志示例：
```
AvatarUpload: 开始准备头像文件
AvatarUpload: 原始图片尺寸: 4000x3000
AvatarUpload: 压缩图片: 4000x3000 -> 1024x768
AvatarUpload: 图片文件准备完成: /data/user/0/.../avatar_1234567890.jpg, 大小: 256KB
AvatarUpload: 头像上传成功: http://localhost:8080/uploads/avatars/...
ProfileEdit: 头像上传成功，更新userProfile
```

---

## 性能优化建议

### 已实现的优化
1. ✅ 图片自动压缩（最大1024x1024）
2. ✅ JPEG质量压缩（85%）
3. ✅ 临时文件清理
4. ✅ 异步上传（不阻塞UI）

### 未来可以优化
1. 图片裁剪功能（正方形裁剪）
2. 缓存头像到本地
3. 支持拍照上传
4. 多头像管理（相册功能）
5. CDN集成（生产环境）

---

## 总结

✅ **已完成功能**：
- 后端头像上传API
- 文件验证和存储
- Android图片选择和压缩
- 头像上传和显示UI
- 实时更新头像

🔄 **测试状态**：待测试

📝 **下一步**：
1. 运行后端服务
2. 安装Android应用
3. 按照测试步骤验证功能
4. 标记任务完成
