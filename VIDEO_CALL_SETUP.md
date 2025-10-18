# 视频通话功能修复说明

## 问题诊断

从Android应用日志中发现：
```
CallService: 获取通话价格失败: {"timestamp":"2025-10-16 19:45:50","status":404,"error":"Not Found","path":"/api/call/rate-info"}
CallService: 发起VIDEO通话失败: {"timestamp":"2025-10-16 19:45:59","status":404,"error":"Not Found","path":"/api/call/initiate"}
```

**根本原因**: 视频通话相关的后端API文件没有在正确的后端项目位置。

## 已完成的修复步骤

### 1. 文件复制

已将以下文件从 `social-app-android-backend/backend/` 复制到 `social-meet-backend/`:

- **Controller**:
  - `CallController.java` → `../social-meet-backend/src/main/java/com/socialmeet/backend/controller/`

- **Service**:
  - `CallService.java` → `../social-meet-backend/src/main/java/com/socialmeet/backend/service/`

- **Entity**:
  - `CallSession.java` → `../social-meet-backend/src/main/java/com/socialmeet/backend/entity/`

- **Repository**:
  - `CallSessionRepository.java` → `../social-meet-backend/src/main/java/com/socialmeet/backend/repository/`

### 2. 文件验证

所有文件已成功复制到后端项目，当前结构：
```
social-meet-backend/
├── controller/
│   ├── AuthController.java
│   ├── CallController.java ✅ (新增)
│   └── UserController.java
├── service/
│   ├── AuthService.java
│   └── CallService.java ✅ (新增)
├── entity/
│   ├── User.java
│   ├── VerificationCode.java
│   └── CallSession.java ✅ (新增)
└── repository/
    ├── UserRepository.java
    ├── VerificationCodeRepository.java
    └── CallSessionRepository.java ✅ (新增)
```

## 下一步操作 - **重启后端服务**

### 方法1: 使用启动脚本（推荐）

1. 停止当前运行的后端服务（如果在运行）
2. 导航到后端项目目录：
   ```cmd
   cd C:\Users\Administrator\IdeaProjects\social-meet-backend
   ```

3. 运行启动脚本：
   ```cmd
   START.bat
   ```
   或
   ```cmd
   setup-and-start.bat
   ```

### 方法2: 使用Gradle命令

```cmd
cd C:\Users\Administrator\IdeaProjects\social-meet-backend
gradlew bootRun
```

### 方法3: 在IntelliJ IDEA中重启

1. 打开IntelliJ IDEA
2. 打开项目：`C:\Users\Administrator\IdeaProjects\social-meet-backend`
3. 停止当前运行的应用（如果有）
4. 右键点击 `SocialMeetApplication.java`
5. 选择 "Run 'SocialMeetApplication'"

## 验证步骤

### 1. 检查后端日志

重启后端服务后，在控制台日志中应该能看到：

```
Mapped "{[/api/call/rate-info],methods=[GET]}" onto public ApiResponse CallController.getUserCallPrices(String)
Mapped "{[/api/call/initiate],methods=[POST]}" onto public ApiResponse CallController.initiateCall(String,Map)
```

### 2. 检查Swagger文档

访问：http://localhost:8080/swagger-ui.html

应该能看到新的API端点：
- `GET /api/call/rate-info` - 获取通话价格信息
- `POST /api/call/initiate` - 发起通话
- `POST /api/call/accept` - 接受通话
- `POST /api/call/reject` - 拒绝通话
- `POST /api/call/end` - 结束通话

### 3. 测试Android应用

1. 打开Android应用
2. 登录账号：`19812342076` (video_caller)
3. 点击首页的用户卡片（video_receiver）
4. 在用户详情页点击"视频通话"按钮
5. 应该能成功发起通话，不再出现404错误

## API端点说明

### GET /api/call/rate-info
获取用户的通话价格信息
- **Headers**: `Authorization: Bearer {token}`
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "voiceCallRate": 1.0,
      "videoCallRate": 2.0,
      "currency": "CNY"
    }
  }
  ```

### POST /api/call/initiate
发起视频或语音通话
- **Headers**: `Authorization: Bearer {token}`
- **Body**:
  ```json
  {
    "receiverId": 22491729,
    "callType": "VIDEO"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "message": "通话发起成功",
    "data": {
      "callSessionId": "uuid-string",
      "callerId": 23820512,
      "receiverId": 22491729,
      "callType": "VIDEO",
      "status": "PENDING"
    }
  }
  ```

## 数据库表

`CallSession` 实体会自动创建 `call_sessions` 表（如果使用了JPA自动建表）。

如果需要手动创建表，SQL如下：
```sql
CREATE TABLE call_sessions (
    call_session_id VARCHAR(255) PRIMARY KEY,
    caller_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    call_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_seconds BIGINT,
    rate_per_minute DECIMAL(10,2),
    total_cost DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (caller_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);
```

## 问题排查

如果重启后仍然出现404错误：

1. **确认后端服务已重启**: 查看日志最后的时间戳
2. **确认端口正确**: 后端应该运行在 `http://localhost:8080`
3. **检查路由映射**: 在启动日志中搜索 "/api/call"
4. **检查包扫描**: 确保 `@SpringBootApplication` 注解的类在正确的包路径
5. **清理并重新构建**:
   ```cmd
   gradlew clean build
   gradlew bootRun
   ```

## 总结

- ✅ 所有视频通话相关文件已复制到正确位置
- ⏳ 需要重启后端服务以加载新的API端点
- ✅ Android客户端代码已经就绪，等待后端API可用

**下一步**: 请重启后端服务，然后测试Android应用的视频通话功能！
