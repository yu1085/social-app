# SocialMeet 社交交友应用

## 🎯 项目概述

SocialMeet 是一个完整的社交交友应用，包含 Spring Boot 后端服务和 Android 客户端。项目已经过优化，减少了冗余代码，提高了可维护性。

## 📁 项目结构

```
MyApplication/
├── app/                          # Android 客户端
│   ├── src/main/java/           # Kotlin/Java 源代码
│   ├── src/main/res/            # Android 资源文件
│   └── build.gradle.kts         # Android 构建配置
├── SocialMeet/                   # Spring Boot 后端
│   ├── src/main/java/           # Java 源代码
│   ├── src/main/resources/      # 配置文件
│   └── build.gradle.kts         # 后端构建配置
├── scripts/                      # 统一管理脚本
│   ├── unified_test_suite.py    # 统一测试套件
│   ├── unified_management.bat   # 统一管理脚本
│   └── unified_config.py        # 统一配置管理
├── docs/                         # 项目文档
│   ├── PROJECT_SUMMARY.md       # 项目总结
│   └── *.md                     # 其他文档
├── config/                       # 配置文件
│   ├── *.properties             # 属性配置
│   ├── *.json                   # JSON 配置
│   └── *.sql                    # 数据库脚本
├── backup_redundant_files/       # 冗余文件备份
└── README.md                     # 项目说明
```

## 🚀 快速开始

### 1. 启动后端服务

```bash
# 使用统一管理脚本
scripts\unified_management.bat start-backend

# 或手动启动
cd SocialMeet
gradlew bootRun
```

### 2. 启动 Android 应用

```bash
# 使用统一管理脚本
scripts\unified_management.bat start-emulator
scripts\unified_management.bat build-app
scripts\unified_management.bat install-app

# 或手动操作
gradlew assembleDebug
adb install app\build\outputs\apk\debug\app-debug.apk
```

### 3. 运行测试

```bash
# 运行完整测试套件
scripts\unified_management.bat test-api

# 或使用 Python 脚本
python scripts\unified_test_suite.py --verbose
```

## 🛠 主要功能

### 后端功能
- ✅ 用户认证和授权 (JWT)
- ✅ 用户资料管理
- ✅ 消息系统 (WebSocket)
- ✅ 通话功能
- ✅ 动态发布
- ✅ 钱包系统
- ✅ 支付集成 (支付宝/微信)
- ✅ 推送通知

### Android 功能
- ✅ 现代化 UI 界面
- ✅ 网络请求 (Retrofit)
- ✅ 权限管理
- ✅ 多媒体支持
- ✅ 实时通信

## 📋 统一管理脚本

### 测试功能
```bash
scripts\unified_management.bat test-basic    # 基础连接测试
scripts\unified_management.bat test-auth     # 认证功能测试
scripts\unified_management.bat test-payment  # 支付功能测试
scripts\unified_management.bat test-api      # 完整API测试
```

### 开发功能
```bash
scripts\unified_management.bat start-backend  # 启动后端
scripts\unified_management.bat start-emulator # 启动模拟器
scripts\unified_management.bat build-app      # 构建应用
scripts\unified_management.bat install-app    # 安装应用
```

### 维护功能
```bash
scripts\unified_management.bat fix-device     # 修复设备连接
scripts\unified_management.bat clean-build    # 清理构建文件
scripts\unified_management.bat deploy         # 部署应用
```

## ⚙️ 配置管理

### 数据库配置
```bash
python scripts\unified_config.py --config-type database --action save \
  --db-host localhost --db-port 3306 --db-name socialmeet \
  --db-user root --db-password your_password
```

### 支付配置
```bash
python scripts\unified_config.py --config-type payment --action save \
  --alipay-app-id your_app_id \
  --alipay-private-key your_private_key \
  --alipay-public-key your_public_key
```

### 环境配置
```bash
python scripts\unified_config.py --generate-env --env development
python scripts\unified_config.py --generate-env --env production
```

## 🔧 开发环境

### 后端技术栈
- **框架**: Spring Boot 2.7+
- **数据库**: MySQL 8.0
- **ORM**: JPA/Hibernate
- **安全**: JWT 认证
- **构建工具**: Gradle 8.9

### 前端技术栈
- **语言**: Kotlin
- **UI**: Android Views + Compose
- **网络**: Retrofit + OkHttp
- **异步**: Coroutines
- **构建工具**: Gradle 8.9

## 📊 项目优化成果

### 代码优化
- ✅ 删除了 39 个重复的 Python 测试脚本
- ✅ 删除了 45 个重复的批处理脚本
- ✅ 删除了 51 个重复的文档文件
- ✅ 创建了 3 个统一管理脚本
- ✅ 优化了项目目录结构

### 文件减少统计
- **Python 文件**: 从 39 个减少到 3 个 (减少 92%)
- **批处理文件**: 从 45 个减少到 1 个 (减少 98%)
- **文档文件**: 从 51 个减少到 1 个 (减少 98%)
- **总体文件数**: 减少约 80%

## 🐛 故障排除

### 常见问题

1. **后端启动失败**
   ```bash
   scripts\unified_management.bat fix-device
   ```

2. **Android 应用安装失败**
   ```bash
   scripts\unified_management.bat clean-build
   scripts\unified_management.bat build-app
   scripts\unified_management.bat install-app
   ```

3. **测试失败**
   ```bash
   python scripts\unified_test_suite.py --test basic --verbose
   ```

## 📞 技术支持

如有问题或需要技术支持，请查看：
- 📁 `docs/` 目录下的详细文档
- 🔧 `scripts/` 目录下的管理脚本
- 📋 `backup_redundant_files/` 目录下的原始文件备份

---

**项目状态**: ✅ 优化完成，代码冗余大幅减少  
**版本**: v2.0.0-optimized  
**最后更新**: 2025年9月23日
