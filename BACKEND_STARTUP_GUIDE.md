# SocialMeet 后端启动指南

## 🚀 快速启动

### 方法1：使用统一管理脚本
```bash
scripts\unified_management.bat start-backend
```

### 方法2：使用简化启动脚本
```bash
start_backend_simple.bat
```

### 方法3：手动启动
```bash
cd SocialMeet
gradlew bootRun
```

## 🔍 常见问题诊断

### 1. 配置问题已修复 ✅

**问题**: `Could not resolve placeholder 'app.alipay.app-id'`

**解决方案**: 已修复配置文件 `application-alipay-real.yml`，将 `payment.alipay.*` 改为 `app.alipay.*`

### 2. 数据库连接问题

**检查MySQL是否运行**:
```bash
# 检查MySQL服务状态
sc query mysql

# 或者检查端口
netstat -an | findstr :3306
```

**启动MySQL**:
```bash
net start mysql
```

### 3. 端口占用问题

**检查8080端口**:
```bash
netstat -an | findstr :8080
```

**如果端口被占用，杀死进程**:
```bash
# 查找占用8080端口的进程
netstat -ano | findstr :8080

# 杀死进程（替换PID）
taskkill /PID <进程ID> /F
```

### 4. 配置文件问题

**检查配置文件**:
- `SocialMeet/src/main/resources/application.yml` - 主配置
- `SocialMeet/src/main/resources/application-alipay-real.yml` - 支付宝配置

**关键配置项**:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/socialmeet
    username: root
    password: 123456

app:
  alipay:
    app-id: 2021005195696348
    private-key: |
      -----BEGIN PRIVATE KEY-----
      ...
```

## 🛠 故障排除步骤

### 步骤1：检查环境
```bash
# 检查Java版本
java -version

# 检查Gradle版本
gradlew --version

# 检查MySQL状态
mysql --version
```

### 步骤2：清理和重建
```bash
cd SocialMeet
gradlew clean
gradlew build
gradlew bootRun
```

### 步骤3：查看详细日志
```bash
# 启用调试模式
gradlew bootRun --debug

# 或者查看日志文件
type logs\spring.log
```

### 步骤4：测试连接
```bash
# 使用统一测试套件
python scripts\unified_test_suite.py --test basic --verbose

# 或者手动测试
curl http://localhost:8080/api/health
```

## 📋 启动成功标志

看到以下日志表示启动成功：
```
Started SocialMeetApplication in X.XXX seconds (JVM running for X.XXX)
```

## 🔧 配置说明

### 数据库配置
- **主机**: localhost
- **端口**: 3306
- **数据库**: socialmeet
- **用户名**: root
- **密码**: 123456

### 支付宝配置
- **应用ID**: 2021005195696348
- **网关**: https://openapi.alipay.com/gateway.do
- **签名算法**: RSA2

### 服务器配置
- **端口**: 8080
- **上下文路径**: /
- **字符编码**: UTF-8

## 🆘 如果仍然无法启动

1. **检查完整错误日志**
2. **确认所有依赖服务运行正常**
3. **尝试使用不同的配置文件**
4. **检查防火墙设置**
5. **查看系统资源使用情况**

## 📞 技术支持

如果问题仍然存在，请提供：
1. 完整的错误日志
2. 系统环境信息
3. 配置文件内容
4. 网络连接状态

---

**最后更新**: 2025年9月23日  
**版本**: v2.0.0-optimized
