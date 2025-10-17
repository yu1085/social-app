# 🚀 SocialMeet 后端启动指南

## ✅ 数据库已初始化成功！

数据库 `socialmeet` 已成功创建，包含 3 个测试用户：
- 📱 19812342076 (video_caller) - ID: 23820512
- 📱 19887654321 (video_receiver) - ID: 22491729
- 📱 13800138000 (test_user)

所有测试账号的验证码都是：**123456**

---

## 📍 项目位置

```
C:\Users\Administrator\IdeaProjects\social-meet-backend
```

---

## 🎯 推荐启动方式：使用 IntelliJ IDEA

### 方法 1：直接运行（最简单）

1. **打开 IntelliJ IDEA**

2. **打开项目**
   - File → Open
   - 选择文件夹：`C:\Users\Administrator\IdeaProjects\social-meet-backend`
   - 点击 OK

3. **等待项目加载**
   - IDEA 会自动识别 Maven 项目
   - 等待右下角进度条完成（下载依赖）
   - 如果提示 "Maven projects need to be imported"，点击 "Import"

4. **运行主类**
   - 在项目中找到：`src/main/java/com/socialmeet/backend/SocialMeetApplication.java`
   - 右键点击文件
   - 选择 **Run 'SocialMeetApplication'**

5. **等待启动完成**
   - 控制台会显示 Spring Boot 启动日志
   - 看到以下信息表示成功：
   ```
   ========================================
      SocialMeet Backend Started!
      服务地址: http://localhost:8080/api
   ========================================
   ```

---

### 方法 2：使用 Maven 面板

1. 打开 IntelliJ IDEA 并加载项目

2. 在右侧找到 **Maven** 面板
   - 如果没有看到，点击 View → Tool Windows → Maven

3. 展开项目树：
   ```
   socialmeet-backend
     └── Lifecycle
         └── spring-boot
             └── spring-boot:run
   ```

4. 双击 **spring-boot:run**

5. 等待启动完成

---

## 🔧 备选方法：命令行启动

### 前提条件

需要安装以下之一：
- **Maven 3.6+**（推荐）
- **Java 21**（必需）

### 检查 Java 版本

```bash
java -version
```

应该显示 Java 21 或更高版本。

### 启动命令

#### 如果已安装 Maven：

```bash
cd C:\Users\Administrator\IdeaProjects\social-meet-backend
mvn spring-boot:run
```

#### 如果没有 Maven，先编译再运行：

```bash
cd C:\Users\Administrator\IdeaProjects\social-meet-backend

# 编译项目（需要 Maven）
mvn clean package -DskipTests

# 运行 JAR
java -jar target/socialmeet-backend-1.0.0.jar
```

---

## 🧪 验证服务是否启动成功

### 1. 检查控制台输出

看到以下内容表示成功：
```
Started SocialMeetApplication in X.XXX seconds
========================================
   SocialMeet Backend Started!
   服务地址: http://localhost:8080/api
========================================
```

### 2. 测试健康检查接口

打开浏览器访问：
```
http://localhost:8080/api/auth/health
```

应该看到：
```json
{
  "success": true,
  "message": "操作成功",
  "data": "服务正常运行"
}
```

### 3. 测试发送验证码

使用 curl 或 Postman：
```bash
curl -X POST "http://localhost:8080/api/auth/send-code?phone=19812342076"
```

响应：
```json
{
  "success": true,
  "message": "验证码已发送",
  "data": "验证码已发送"
}
```

### 4. 测试登录

```bash
curl -X POST "http://localhost:8080/api/auth/login-with-code?phone=19812342076&code=123456"
```

响应包含 JWT token 和用户信息。

---

## 📱 Android 前端配置

### 修改网络配置

编辑 Android 项目的 `NetworkConfig.java`：

```java
// 模拟器访问本地服务器
private static final String BASE_URL = "http://10.0.2.2:8080/api/";

// 真机访问（替换为你的电脑IP）
// private static final String BASE_URL = "http://192.168.1.100:8080/api/";
```

### 查看电脑 IP 地址（真机调试）

```bash
ipconfig
```

找到 "IPv4 地址"，例如：`192.168.1.100`

---

## ❌ 常见问题

### 问题1：端口 8080 被占用

**错误信息：**
```
Port 8080 is already in use
```

**解决方案：**

方法1：停止占用端口的程序
```bash
# 查找占用端口的进程
netstat -ano | findstr :8080

# 结束进程（替换 PID）
taskkill /PID <进程ID> /F
```

方法2：修改端口
编辑 `src/main/resources/application.yml`：
```yaml
server:
  port: 8081  # 改成其他端口
```

### 问题2：数据库连接失败

**错误信息：**
```
Communications link failure
```

**解决方案：**
1. 检查 MySQL 服务是否启动
   - 打开服务管理器：`services.msc`
   - 找到 MySQL 服务，确保已启动

2. 检查数据库配置（`application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/socialmeet?...
    username: root
    password: root
```

### 问题3：Maven 依赖下载失败

**解决方案：**
1. 检查网络连接
2. 配置 Maven 镜像（阿里云）
3. 在 IntelliJ IDEA 中：
   - File → Settings → Build, Execution, Deployment → Build Tools → Maven
   - 点击 "Reload All Maven Projects"

### 问题4：Java 版本不匹配

**错误信息：**
```
Unsupported class file major version XX
```

**解决方案：**
- 项目需要 Java 21
- 在 IntelliJ IDEA 中：
  - File → Project Structure → Project
  - SDK: 选择 Java 21
  - Language Level: 21

---

## 📊 项目结构

```
social-meet-backend/
├── src/
│   └── main/
│       ├── java/com/socialmeet/backend/
│       │   ├── SocialMeetApplication.java  ← 主类（启动入口）
│       │   ├── controller/                 ← REST API 控制器
│       │   │   ├── AuthController.java
│       │   │   └── UserController.java
│       │   ├── service/                    ← 业务逻辑层
│       │   │   └── AuthService.java
│       │   ├── repository/                 ← 数据访问层
│       │   │   ├── UserRepository.java
│       │   │   └── VerificationCodeRepository.java
│       │   ├── entity/                     ← 数据库实体
│       │   │   ├── User.java
│       │   │   └── VerificationCode.java
│       │   ├── dto/                        ← 数据传输对象
│       │   └── util/                       ← 工具类
│       │       └── JwtUtil.java
│       └── resources/
│           └── application.yml             ← 配置文件
├── database/
│   └── init.sql                           ← 数据库初始化脚本
├── pom.xml                                ← Maven 配置
└── README-START.md                        ← 快速启动指南
```

---

## 🌐 API 端点

### 认证相关

- **POST** `/api/auth/send-code` - 发送验证码
  - 参数：`phone` (手机号)

- **POST** `/api/auth/login-with-code` - 验证码登录
  - 参数：`phone` (手机号), `code` (验证码)

- **GET** `/api/auth/health` - 健康检查

### 用户相关（需要 Token）

- **GET** `/api/users/profile` - 获取用户信息
  - Header: `Authorization: Bearer <token>`

- **PUT** `/api/users/profile` - 更新用户信息
  - Header: `Authorization: Bearer <token>`
  - Body: JSON (用户信息)

---

## 📝 测试流程

1. **启动后端服务**（按照上面的步骤）

2. **测试健康检查**
   ```
   http://localhost:8080/api/auth/health
   ```

3. **发送验证码**
   ```bash
   curl -X POST "http://localhost:8080/api/auth/send-code?phone=19812342076"
   ```

4. **登录获取 Token**
   ```bash
   curl -X POST "http://localhost:8080/api/auth/login-with-code?phone=19812342076&code=123456"
   ```

5. **使用 Token 访问受保护的接口**
   ```bash
   curl -H "Authorization: Bearer <你的token>" http://localhost:8080/api/users/profile
   ```

---

## 🎉 完成！

后端服务启动成功后，就可以使用 Android 前端进行测试了。

**提示：** 确保 Android 项目的 `NetworkConfig.java` 中的 BASE_URL 配置正确。

---

**如有问题，请查看日志文件：** `logs/socialmeet.log`
