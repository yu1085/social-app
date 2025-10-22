# 知友/喜欢/亲密功能 - 快速开始指南

## 🚀 5分钟快速启动

### 1️⃣ 创建数据库表 (1分钟)

打开命令行，执行以下命令创建关系表：

**方式A: 使用PowerShell执行SQL文件**
```powershell
Get-Content backend/database/create_user_relationships_table.sql | mysql -u root -proot socialmeet
```

**方式B: 手动复制SQL**
```sql
USE socialmeet;

CREATE TABLE IF NOT EXISTS user_relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_user_id BIGINT NOT NULL,
    relationship_type VARCHAR(20) NOT NULL,
    intimacy_score INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_target_type (user_id, target_user_id, relationship_type),
    INDEX idx_user_id (user_id),
    INDEX idx_target_user_id (target_user_id)
);
```

### 2️⃣ 启动后端服务器 (1分钟)

```bash
# 方式A: 使用Gradle
cd backend
../gradlew bootRun

# 方式B: 使用现有脚本
.\start_backend_with_profile.ps1
```

等待看到以下日志表示启动成功:
```
Started SocialMeetApplication in X.XXX seconds
```

### 3️⃣ 验证API可用 (1分钟)

打开新的PowerShell窗口，执行测试脚本:

```powershell
.\test_relationships_api.ps1
```

**注意**: 首次运行需要先登录获取token，替换脚本中的token值。

### 4️⃣ 编译Android APK (2分钟)

```bash
.\gradlew :app:assembleDebug
```

APK输出位置: `app/build/outputs/apk/debug/app-debug.apk`

### 5️⃣ 安装并测试 (1分钟)

```bash
# 安装APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 或使用Android Studio直接运行
```

---

## ✅ 功能验证清单

### 后端API测试

- [ ] 数据库表已创建
- [ ] 后端服务器正常运行
- [ ] 可以添加喜欢
- [ ] 可以取消喜欢
- [ ] 可以添加知友
- [ ] 可以删除知友
- [ ] 可以获取知友列表
- [ ] 可以获取喜欢列表
- [ ] 可以获取亲密列表

### Android App测试

- [ ] APK编译成功
- [ ] App可以正常启动
- [ ] 可以登录账号
- [ ] 打开用户详情页
- [ ] 点击喜欢按钮有反应
- [ ] 显示"已喜欢"或"已取消喜欢"Toast
- [ ] 消息页-关系标签可以查看列表
- [ ] 知友列表可以显示
- [ ] 喜欢列表可以显示
- [ ] 亲密列表可以显示

---

## 🔥 核心功能说明

### 1. 用户详情页喜欢功能

**位置**: 首页用户列表 → 点击用户 → 用户详情页右下角

**功能**:
- ✅ 自动加载喜欢状态 (进入页面时)
- ✅ 点击按钮切换喜欢/取消喜欢
- ✅ 防止喜欢自己
- ✅ Toast提示操作结果

### 2. 关系列表

**位置**: 消息页底部 → 关系标签 → 查看按钮

- **知友**: 显示互相添加为好友的用户列表
- **喜欢**: 显示你喜欢的用户列表
- **亲密**: 显示聊天频率高的用户列表

### 3. 智能降级

- 有关系数据时 → 显示真实关系
- 无关系数据时 → 显示推荐用户
- 保证列表始终有内容

---

## 🛠️ 快速测试指令

### 获取登录Token

```bash
# 发送验证码
curl -X POST "http://localhost:8080/api/auth/send-code?phone=18888888888"

# 登录 (验证码固定为 123456)
curl -X POST "http://localhost:8080/api/auth/login-with-code?phone=18888888888&code=123456"
```

复制返回的token，格式如: `eyJhbGciOiJIUzI1NiJ9...`

### 添加喜欢

```bash
curl -X POST http://localhost:8080/api/users/23820512/like \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 检查喜欢状态

```bash
curl http://localhost:8080/api/users/23820512/is-liked \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 查看喜欢列表

```bash
curl "http://localhost:8080/api/users/likes?size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📁 关键文件位置

### 后端
```
backend/
├── database/
│   └── create_user_relationships_table.sql    # 建表SQL
├── src/main/java/com/socialmeet/backend/
│   ├── entity/UserRelationship.java          # 实体类
│   ├── repository/UserRelationshipRepository.java  # Repository
│   ├── service/UserRelationshipService.java       # Service
│   └── controller/UserController.java             # API控制器
```

### Android
```
app/src/main/java/com/example/myapplication/
├── network/ApiService.java               # API接口定义
├── UserDetailActivity.java               # 用户详情页(喜欢功能)
├── AcquaintancesActivity.java            # 知友列表
├── LikesActivity.java                    # 喜欢列表
└── IntimacyActivity.java                 # 亲密列表
```

---

## 🐛 常见问题排查

### 问题1: 数据库表创建失败
```bash
# 检查MySQL是否运行
mysql -u root -proot -e "SHOW DATABASES;"

# 手动创建数据库
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS socialmeet;"
```

### 问题2: 后端启动失败
```bash
# 检查端口占用
netstat -ano | findstr :8080

# 杀死占用进程
taskkill /F /PID <进程ID>
```

### 问题3: API返回401
- 确保token有效（未过期）
- 检查Authorization头格式: `Bearer <token>`

### 问题4: Android编译失败
```bash
# 清理缓存
.\gradlew clean

# 重新编译
.\gradlew :app:assembleDebug
```

---

## 📊 API端点总览

| 功能 | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 添加喜欢 | POST | `/api/users/{id}/like` | 添加喜欢 |
| 取消喜欢 | DELETE | `/api/users/{id}/like` | 取消喜欢 |
| 检查喜欢 | GET | `/api/users/{id}/is-liked` | 是否已喜欢 |
| 添加知友 | POST | `/api/users/{id}/friend` | 添加知友 |
| 删除知友 | DELETE | `/api/users/{id}/friend` | 删除知友 |
| 检查知友 | GET | `/api/users/{id}/is-friend` | 是否是知友 |
| 知友列表 | GET | `/api/users/acquaintances` | 获取知友列表 |
| 喜欢列表 | GET | `/api/users/likes` | 获取喜欢列表 |
| 亲密列表 | GET | `/api/users/intimate` | 获取亲密列表 |

---

## 🎯 下一步

1. ✅ **基础功能已完成** - 所有核心功能已实现
2. 🎨 **UI美化** - 为喜欢按钮添加视觉反馈
3. 📈 **数据统计** - 显示知友数、被喜欢数
4. 🔔 **消息通知** - 有人喜欢你时发送通知
5. 🚀 **性能优化** - 添加缓存机制

---

## 💡 提示

- 所有API都需要JWT认证
- 数据为空时会自动显示推荐用户
- 防止重复添加关系（数据库唯一索引）
- 不能喜欢或添加自己为知友
- 所有操作都有完整的日志记录

---

## 📞 获取帮助

遇到问题？检查以下日志:
- **后端日志**: 控制台输出
- **Android日志**: `adb logcat | grep UserDetailActivity`
- **数据库日志**: `SHOW CREATE TABLE user_relationships;`

更多详细信息请查看: `USER_RELATIONSHIPS_IMPLEMENTATION.md`
