# SocialMeet API 文档

## 基础信息

- **基础URL**: `http://localhost:8080/api`
- **认证方式**: JWT Bearer Token
- **数据格式**: JSON

## 统一响应格式

所有API返回统一的响应格式：

```json
{
  "success": true,       // 请求是否成功
  "message": "操作成功",  // 提示消息
  "data": {}             // 响应数据
}
```

## API 接口列表

### 1. 认证相关 (`/auth`)

#### 1.1 健康检查

**接口**: `GET /auth/health`

**说明**: 检查服务是否正常运行

**请求示例**:
```bash
curl http://localhost:8080/api/auth/health
```

**响应示例**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": "服务正常运行"
}
```

---

#### 1.2 发送验证码

**接口**: `POST /auth/send-code`

**说明**: 向指定手机号发送登录验证码

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号（11位） |

**请求示例**:
```bash
curl -X POST "http://localhost:8080/api/auth/send-code?phone=19812342076"
```

**响应示例**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": "验证码已发送（测试模式）: 123456"
}
```

**错误示例**:
```json
{
  "success": false,
  "message": "手机号格式不正确",
  "data": null
}
```

---

#### 1.3 验证码登录/注册

**接口**: `POST /auth/login-with-code`

**说明**: 使用手机号和验证码登录，如果用户不存在则自动注册

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号 |
| code | String | 是 | 验证码 |

**请求示例**:
```bash
curl -X POST "http://localhost:8080/api/auth/login-with-code?phone=19812342076&code=123456"
```

**响应示例**:
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIzODIwNTEyLCJ1c2VybmFtZSI6InZpZGVvX2NhbGxlciIsInN1YiI6InZpZGVvX2NhbGxlciIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDg2NDAwfQ...",
    "user": {
      "id": 23820512,
      "username": "video_caller",
      "nickname": "video_caller",
      "phone": "19812342076",
      "email": null,
      "avatarUrl": null,
      "gender": "MALE",
      "birthday": "1993-01-15",
      "constellation": null,
      "location": "杭州市",
      "height": 175,
      "weight": null,
      "incomeLevel": null,
      "education": null,
      "maritalStatus": null,
      "signature": "喜欢音乐和电影，享受简单快乐的生活",
      "isVerified": true,
      "isVip": false,
      "vipLevel": 0,
      "wealthLevel": 0,
      "balance": 0.0,
      "isOnline": true,
      "lastActiveAt": "2024-01-01 12:00:00",
      "createdAt": "2024-01-01 00:00:00",
      "updatedAt": "2024-01-01 12:00:00"
    }
  }
}
```

**错误示例**:
```json
{
  "success": false,
  "message": "验证码无效或已过期",
  "data": null
}
```

---

### 2. 用户相关 (`/users`)

#### 2.1 获取当前用户信息

**接口**: `GET /users/profile`

**说明**: 获取当前登录用户的详细信息

**请求头**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| Authorization | String | 是 | Bearer {token} |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/api/users/profile" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**响应示例**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 23820512,
    "username": "video_caller",
    "nickname": "video_caller",
    "phone": "19812342076",
    ...
  }
}
```

---

#### 2.2 更新用户信息

**接口**: `PUT /users/profile`

**说明**: 更新当前用户的个人信息

**请求头**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| Authorization | String | 是 | Bearer {token} |

**请求体**:
```json
{
  "nickname": "新昵称",
  "gender": "MALE",
  "birthday": "1990-01-01",
  "location": "北京市",
  "signature": "这是我的个性签名",
  "height": 180,
  "weight": 70
}
```

**请求示例**:
```bash
curl -X PUT "http://localhost:8080/api/users/profile" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "新昵称",
    "signature": "这是我的个性签名"
  }'
```

**响应示例**:
```json
{
  "success": true,
  "message": "更新成功",
  "data": {
    "id": 23820512,
    "username": "video_caller",
    "nickname": "新昵称",
    "signature": "这是我的个性签名",
    ...
  }
}
```

---

#### 2.3 根据ID获取用户信息

**接口**: `GET /users/{id}`

**说明**: 获取指定用户的公开信息

**路径参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 用户ID |

**请求示例**:
```bash
curl -X GET "http://localhost:8080/api/users/23820512"
```

**响应示例**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 23820512,
    "username": "video_caller",
    "nickname": "video_caller",
    ...
  }
}
```

---

## 测试账号

| 用户ID | 用户名 | 手机号 | 验证码 |
|--------|--------|--------|--------|
| 23820512 | video_caller | 19812342076 | 123456 |
| 22491729 | video_receiver | 19887654321 | 123456 |
| - | test_user | 13800138000 | 123456 |

## 错误代码

| HTTP状态码 | 说明 |
|-----------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权（Token无效或过期） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## JWT Token 说明

### Token 格式
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Token 内容
```json
{
  "userId": 23820512,
  "username": "video_caller",
  "sub": "video_caller",
  "iat": 1700000000,
  "exp": 1700086400
}
```

### Token 有效期
- **访问令牌**: 24小时
- **刷新令牌**: 7天（后续实现）

### 使用示例

**JavaScript/Axios:**
```javascript
axios.get('http://localhost:8080/api/users/profile', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
```

**Android/Retrofit:**
```java
@GET("users/profile")
Call<ApiResponse<UserDTO>> getProfile(
    @Header("Authorization") String authHeader
);

// 调用
String authHeader = "Bearer " + token;
Call<ApiResponse<UserDTO>> call = apiService.getProfile(authHeader);
```

## Postman 导入

可以使用以下 cURL 命令在 Postman 中创建请求：

1. 打开 Postman
2. 点击 "Import" > "Raw text"
3. 粘贴 cURL 命令
4. 点击 "Import"

## 开发建议

1. **测试环境** - 使用测试验证码 `123456` 进行测试
2. **Token管理** - 在客户端安全存储Token，每次请求携带
3. **错误处理** - 捕获401错误，提示用户重新登录
4. **超时设置** - 建议设置30秒超时
5. **重试机制** - 网络失败时实现重试逻辑

## 更新日志

### v1.0.0 (2024-01-15)
- 实现基础认证功能
- 支持验证码登录/注册
- 实现JWT Token认证
- 用户信息CRUD接口
