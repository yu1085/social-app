# 🚀 SocialMeet 后端快速启动指南

## 📍 项目位置
```
C:\Users\Administrator\IdeaProjects\social-meet-backend
```

## ⚡ 快速启动（3步）

### 第1步：初始化数据库

**打开 CMD 或 PowerShell，执行：**
```bash
cd C:\Users\Administrator\IdeaProjects\social-meet-backend\database
run-init.bat
```

**或者手动执行：**
```bash
mysql -u root -proot < database/init.sql
```

✅ 成功标志：看到 "数据库初始化完成！"

---

### 第2步：启动后端服务

**方式1：使用启动脚本（推荐）**
```bash
cd C:\Users\Administrator\IdeaProjects\social-meet-backend
START.bat
```

**方式2：使用 Maven 命令**
```bash
cd C:\Users\Administrator\IdeaProjects\social-meet-backend
mvn spring-boot:run
```

**方式3：使用 IDE**
1. 用 IntelliJ IDEA 打开项目
2. 找到 `SocialMeetApplication.java`
3. 右键 → Run

✅ 成功标志：看到以下输出
```
========================================
   SocialMeet Backend Started!
   服务地址: http://localhost:8080/api
========================================
```

---

### 第3步：测试 API

**打开新的 CMD 窗口，执行：**
```bash
cd C:\Users\Administrator\IdeaProjects\social-meet-backend
test-api.bat
```

**或者手动测试：**
```bash
# 测试健康检查
curl http://localhost:8080/api/auth/health

# 发送验证码
curl -X POST "http://localhost:8080/api/auth/send-code?phone=19812342076"

# 验证码登录（验证码固定为 123456）
curl -X POST "http://localhost:8080/api/auth/login-with-code?phone=19812342076&code=123456"
```

---

## 📋 前提条件

### 必需：
- ✅ **Java 21** - [下载地址](https://www.oracle.com/java/technologies/downloads/)
- ✅ **MySQL 8.0** - 确保服务已启动
- ✅ **MySQL 用户**: root / 密码: root

### 可选：
- **Maven 3.9+** - 如未安装会自动使用 Maven Wrapper
- **IntelliJ IDEA** - 推荐用于开发

---

## 🔧 常见问题

### ❌ 问题1：找不到 Java 命令
```
'java' 不是内部或外部命令
```

**解决方案：**
1. 安装 Java 21：https://www.oracle.com/java/technologies/downloads/
2. 配置环境变量 JAVA_HOME
3. 重启 CMD

---

### ❌ 问题2：数据库连接失败
```
Communications link failure
```

**解决方案：**
1. 检查 MySQL 服务是否启动
   ```bash
   # Windows 服务管理器
   services.msc
   # 找到 MySQL 服务，确保已启动
   ```
2. 检查用户名密码（默认 root/root）
3. 修改 `src/main/resources/application.yml` 中的数据库配置

---

### ❌ 问题3：端口 8080 被占用
```
Port 8080 is already in use
```

**解决方案：**
修改 `application.yml` 中的端口：
```yaml
server:
  port: 8081  # 改成其他端口
```

---

### ❌ 问题4：Maven 依赖下载失败

**解决方案：**
1. 配置 Maven 镜像（阿里云）
2. 删除 `.m2/repository` 目录
3. 重新运行 `mvn clean install`

---

## 📖 详细文档

- **快速开始**: `QUICKSTART.md`
- **API 文档**: `API_DOCUMENTATION.md`
- **完整总结**: `IMPLEMENTATION_SUMMARY.md`

---

## 🧪 测试账号

| 手机号 | 验证码 | 用户ID | 用户名 |
|--------|--------|--------|--------|
| 19812342076 | 123456 | 23820512 | video_caller |
| 19887654321 | 123456 | 22491729 | video_receiver |
| 13800138000 | 123456 | - | test_user |

---

## 🎯 API 地址

- **基础URL**: `http://localhost:8080/api`
- **健康检查**: `http://localhost:8080/api/auth/health`

### 主要端点：
- `POST /api/auth/send-code` - 发送验证码
- `POST /api/auth/login-with-code` - 验证码登录
- `GET /api/users/profile` - 获取用户信息（需要Token）
- `PUT /api/users/profile` - 更新用户信息（需要Token）

---

## 📱 Android 前端配置

修改 Android 项目的 `NetworkConfig.java`：

```java
// 模拟器访问本地服务器
private static final String BASE_URL = "http://10.0.2.2:8080/api/";

// 真机访问（替换为你的电脑IP）
// private static final String BASE_URL = "http://192.168.1.100:8080/api/";
```

---

## 🎉 启动成功的标志

控制台输出：
```
========================================
   SocialMeet Backend Started!
   服务地址: http://localhost:8080/api
========================================
```

浏览器访问 `http://localhost:8080/api/auth/health` 显示：
```json
{
  "success": true,
  "message": "操作成功",
  "data": "服务正常运行"
}
```

---

**祝你使用愉快！** 🚀

如有问题，请查看详细文档或检查日志文件：`logs/socialmeet.log`
