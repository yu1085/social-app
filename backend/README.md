# SocialMeet 后端服务实现指南

## 项目概述

本目录包含 SocialMeet 社交应用的后端实现代码和配置。

## 技术栈

- **框架**: Spring Boot 3.3.5
- **语言**: Java 21
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA
- **安全**: Spring Security + JWT
- **构建工具**: Maven / Gradle

## 目录结构

```
backend-setup/
├── database/           # 数据库脚本
│   └── init.sql       # 数据库初始化脚本
├── src/               # Java源代码
│   ├── entity/        # JPA实体类
│   ├── dto/           # 数据传输对象
│   ├── repository/    # 数据仓库接口
│   ├── service/       # 业务逻辑服务
│   ├── controller/    # REST API控制器
│   ├── security/      # 安全配置和JWT工具
│   └── config/        # 应用配置
├── application.yml    # Spring Boot配置文件
└── README.md          # 本文件

## 快速开始

### 1. 数据库初始化

```bash
# 连接MySQL（用户名: root, 密码: root）
mysql -u root -proot

# 执行初始化脚本
source backend-setup/database/init.sql
```

### 2. 配置数据库连接

编辑 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/socialmeet?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4
    username: root
    password: root
```

### 3. 启动服务

```bash
# 使用Maven
mvn spring-boot:run

# 或使用Gradle
gradle bootRun
```

服务将在 `http://localhost:8080` 启动

## API 端点

### 认证相关

- `POST /api/auth/send-code` - 发送验证码
- `POST /api/auth/login-with-code` - 验证码登录/注册
- `POST /api/auth/login` - 密码登录
- `POST /api/auth/refresh-token` - 刷新令牌
- `POST /api/auth/logout` - 登出

### 用户相关

- `GET /api/users/profile` - 获取当前用户信息
- `PUT /api/users/profile` - 更新用户信息
- `GET /api/users/{id}` - 获取指定用户信息

## 数据库表结构

### users（用户表）
- 存储用户基本信息、账号信息、VIP状态等

### verification_codes（验证码表）
- 存储短信验证码，支持5分钟有效期

### user_authentications（用户认证表）
- 存储身份证、人脸、手机等多种认证信息

### refresh_tokens（刷新令牌表）
- 存储JWT刷新令牌

### login_logs（登录日志表）
- 记录用户登录历史

## 测试账号

数据库已包含以下测试账号：

1. **video_caller** (ID: 23820512)
   - 手机号: 19812342076
   - 测试验证码: 123456

2. **video_receiver** (ID: 22491729)
   - 手机号: 19887654321
   - 测试验证码: 123456

3. **test_user**
   - 手机号: 13800138000
   - 测试验证码: 123456

## JWT Token 配置

- Token 有效期: 24小时
- Refresh Token 有效期: 7天
- 密钥: 在 `application.yml` 中配置

## 开发注意事项

1. 所有需要认证的接口都需要在请求头中携带 `Authorization: Bearer {token}`
2. 验证码默认有效期5分钟
3. 测试模式下验证码固定为 `123456`
4. 生产环境需要配置真实的短信服务

## 下一步

1. 实现验证码发送服务（集成阿里云短信等）
2. 添加用户关系功能（关注、黑名单）
3. 实现消息系统
4. 添加钱包和支付功能
5. 实现实名认证接口
