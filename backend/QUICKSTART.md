# SocialMeet 后端快速启动指南

## 前提条件

1. **Java 21** - 确保已安装 Java 21
2. **MySQL 8.0** - 确保 MySQL 服务已启动
3. **Maven** - 用于构建项目（或使用 IDE 内置 Maven）

## 快速启动步骤

### 步骤1: 初始化数据库

**Windows 用户：**
```bash
cd backend-setup/database
run-init.bat
```

**Linux/Mac 用户：**
```bash
cd backend-setup/database
mysql -u root -proot < init.sql
```

成功后会看到：
```
数据库初始化完成！
user_count: 3
```

### 步骤2: 配置数据库连接

编辑 `backend-setup/application.yml`，确认数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/socialmeet?...
    username: root
    password: root  # 如果你的MySQL密码不是root，请修改这里
```

### 步骤3: 复制代码到正确的目录结构

将 `backend-setup/src` 下的代码按照以下结构组织：

```
backend-setup/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── socialmeet/
│       │           └── backend/
│       │               ├── SocialMeetApplication.java  (主应用类)
│       │               ├── entity/                     (实体类)
│       │               │   ├── User.java
│       │               │   └── VerificationCode.java
│       │               ├── dto/                        (DTO类)
│       │               │   ├── ApiResponse.java
│       │               │   ├── LoginRequest.java
│       │               │   ├── LoginResponse.java
│       │               │   └── UserDTO.java
│       │               ├── repository/                 (Repository接口)
│       │               │   ├── UserRepository.java
│       │               │   └── VerificationCodeRepository.java
│       │               ├── service/                    (服务类)
│       │               │   └── AuthService.java
│       │               ├── controller/                 (控制器)
│       │               │   ├── AuthController.java
│       │               │   └── UserController.java
│       │               └── security/                   (安全配置)
│       │                   └── JwtUtil.java
│       └── resources/
│           └── application.yml
├── pom.xml
└── README.md
```

### 步骤4: 构建并运行

**使用 Maven 命令行：**
```bash
cd backend-setup
mvn clean install
mvn spring-boot:run
```

**使用 IDE (推荐)：**
1. 使用 IntelliJ IDEA 或 Eclipse 打开 `backend-setup` 目录
2. 确保识别为 Maven 项目
3. 找到 `SocialMeetApplication.java`
4. 右键选择 "Run"

### 步骤5: 验证服务

服务启动后，你会看到：

```
========================================
   SocialMeet Backend Started!
   服务地址: http://localhost:8080/api
========================================
```

**测试健康检查：**
```bash
curl http://localhost:8080/api/auth/health
```

预期响应：
```json
{
  "success": true,
  "message": "服务正常运行",
  "data": "服务正常运行"
}
```

## API 测试

### 1. 发送验证码

```bash
curl -X POST "http://localhost:8080/api/auth/send-code?phone=19812342076"
```

响应：
```json
{
  "success": true,
  "message": "验证码已发送（测试模式）: 123456",
  "data": "验证码已发送（测试模式）: 123456"
}
```

### 2. 验证码登录

```bash
curl -X POST "http://localhost:8080/api/auth/login-with-code?phone=19812342076&code=123456"
```

响应：
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 23820512,
      "username": "video_caller",
      "nickname": "video_caller",
      "phone": "19812342076",
      ...
    }
  }
}
```

### 3. 获取用户信息（需要Token）

```bash
curl -X GET "http://localhost:8080/api/users/profile" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 测试账号

数据库已包含以下测试账号（验证码统一为 `123456`）：

| 用户ID | 用户名 | 手机号 | 说明 |
|--------|--------|--------|------|
| 23820512 | video_caller | 19812342076 | 视频发起者 |
| 22491729 | video_receiver | 19887654321 | 视频接收者 |
| - | test_user | 13800138000 | 测试用户 |

## 常见问题

### 1. 数据库连接失败

**错误信息：** `Communications link failure`

**解决方案：**
- 检查 MySQL 服务是否启动
- 确认端口 3306 未被占用
- 验证用户名密码是否正确

### 2. 编译错误

**错误信息：** `Compilation failure`

**解决方案：**
- 确保 Java 版本为 21
- 运行 `mvn clean install -U` 强制更新依赖
- 检查 IDE 的 Java SDK 配置

### 3. JWT 相关错误

**错误信息：** `Invalid JWT token`

**解决方案：**
- 检查 `application.yml` 中的 `jwt.secret` 配置
- 确保密钥长度足够（至少32字符）

## 项目结构说明

```
backend-setup/
├── database/               # 数据库相关
│   ├── init.sql           # 初始化脚本
│   └── run-init.bat       # Windows 执行脚本
├── src/                   # 源代码
│   ├── entity/           # JPA 实体类
│   ├── dto/              # 数据传输对象
│   ├── repository/       # 数据仓库接口
│   ├── service/          # 业务逻辑层
│   ├── controller/       # REST API 控制器
│   └── security/         # 安全和JWT配置
├── application.yml       # Spring Boot 配置
├── pom.xml               # Maven 依赖配置
└── README.md             # 项目说明
```

## 下一步

1. **集成短信服务** - 在生产环境中集成阿里云短信等服务
2. **添加更多功能** - 实现消息、钱包、VIP等功能
3. **安全加固** - 添加 Spring Security 配置
4. **性能优化** - 添加缓存（Redis）
5. **监控和日志** - 集成 ELK 或其他监控工具

## 技术支持

遇到问题？
1. 检查日志文件: `logs/socialmeet.log`
2. 查看 Spring Boot 控制台输出
3. 参考 `README.md` 获取更多信息

**祝你开发顺利！** 🚀
