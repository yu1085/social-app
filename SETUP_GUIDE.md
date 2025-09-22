# SocialMeet 项目启动指南

## 项目结构说明

```
F:\MyApplication\
├── app/                          # Android客户端（前端）
│   ├── src/main/java/com/example/myapplication/
│   │   ├── UserProfileDetailActivity.kt      # 用户资料详情
│   │   ├── EditProfileActivity.kt            # 编辑资料
│   │   ├── RealNameVerificationActivity.kt   # 实名认证
│   │   ├── PhoneVerificationActivity.kt      # 手机认证
│   │   ├── service/
│   │   │   ├── ProfileService.kt             # 资料服务（已连接后端）
│   │   │   ├── RealNameVerificationService.kt # 认证服务（已连接后端）
│   │   │   └── RealNetworkService.kt         # 网络服务（调用Node.js API）
│   │   ├── config/
│   │   │   └── ApiConfig.kt                  # API配置
│   │   └── model/                            # 数据模型
│   └── src/main/res/                         # 布局和资源文件
├── backend/                       # Node.js服务端（后端）
│   ├── server.js                  # 主服务器文件
│   ├── package.json               # 依赖配置
│   ├── database/
│   │   └── init.sql               # 数据库初始化
│   └── README.md                  # 后端说明
└── API_DOCUMENTATION.md           # API接口文档
```

## 启动步骤

### 1. 启动后端服务（Node.js）

```bash
# 进入后端目录
cd backend

# 安装依赖
npm install

# 配置环境变量
cp env.example .env
# 编辑 .env 文件，配置数据库连接

# 初始化数据库
mysql -u root -p < database/init.sql

# 启动服务
npm run dev
```

**后端服务将在 `http://localhost:3000` 启动**

### 2. 启动Android客户端

```bash
# 在Android Studio中打开项目
# 或者使用命令行编译
cd F:\MyApplication
.\gradlew assembleDebug
```

**Android应用将连接到 `http://10.0.2.2:3000`（模拟器访问本地服务器）**

## 代码分布说明

### 📱 Android端（前端）
- **位置**: `app/src/main/java/com/example/myapplication/`
- **作用**: 用户界面、用户交互、数据展示
- **特点**: 已连接真实后端API

### 🖥️ Node.js端（后端）
- **位置**: `backend/`
- **作用**: 数据处理、业务逻辑、数据库操作
- **特点**: 提供RESTful API接口

## 数据流向

```
Android客户端 → HTTP请求 → Node.js服务器 → MySQL数据库
     ↑                                           ↓
     ← JSON响应 ← 业务处理 ← 数据查询 ←────────────
```

## 功能对应关系

| Android功能 | Node.js API | 数据库表 |
|------------|-------------|----------|
| 用户资料编辑 | POST /api/v1/profile/save | user_profiles |
| 实名认证 | POST /api/v1/verification/realname | verification_records |
| 手机认证 | POST /api/v1/verification/phone | verification_records |
| 图片上传 | POST /api/v1/upload/image | images |

## 配置说明

### Android端配置
```kotlin
// ApiConfig.kt
object ApiConfig {
    private const val DEV_BASE_URL = "http://10.0.2.2:3000/api/v1"  // 开发环境
    private const val PROD_BASE_URL = "https://your-api-server.com/api/v1"  // 生产环境
}
```

### Node.js端配置
```javascript
// server.js
const PORT = process.env.PORT || 3000;
const DB_HOST = process.env.DB_HOST || 'localhost';
```

## 测试验证

### 1. 测试后端API
```bash
# 测试健康检查
curl http://localhost:3000/health

# 测试用户注册
curl -X POST http://localhost:3000/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber":"13800138000","password":"123456"}'
```

### 2. 测试Android连接
1. 启动Android应用
2. 进入"我的"页面
3. 点击"用户信息区域"
4. 点击"编辑资料"
5. 修改资料并保存
6. 检查后端日志确认数据保存

## 常见问题

### Q: Android无法连接后端？
A: 检查：
- 后端服务是否启动（端口3000）
- 网络配置是否正确（10.0.2.2:3000）
- 防火墙是否阻止连接

### Q: 数据库连接失败？
A: 检查：
- MySQL服务是否启动
- 数据库配置是否正确
- 用户权限是否足够

### Q: API请求失败？
A: 检查：
- 请求URL是否正确
- 请求头是否完整
- 数据格式是否正确

## 开发模式切换

### 使用模拟服务（演示模式）
```kotlin
// 在ProfileService.kt中注释掉真实API调用
// val success = RealNetworkService.saveProfile(profileData, context)
val success = simulateSaveToServer(profileData) // 使用模拟服务
```

### 使用真实服务（生产模式）
```kotlin
// 在ProfileService.kt中使用真实API调用
val success = RealNetworkService.saveProfile(profileData, context) // 使用真实服务
```

## 部署说明

### 开发环境
- Android: 本地调试
- 后端: localhost:3000
- 数据库: 本地MySQL

### 生产环境
- Android: 发布到应用商店
- 后端: 部署到云服务器
- 数据库: 云数据库服务

现在您的项目已经完整连接了前后端，可以开始真正的开发和测试了！
