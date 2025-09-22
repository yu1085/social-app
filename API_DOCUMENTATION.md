# 后端API接口文档

## 基础信息

- **基础URL**: `https://your-api-server.com/api/v1`
- **认证方式**: Bearer Token
- **数据格式**: JSON
- **字符编码**: UTF-8

## 认证接口

### 1. 用户登录
```
POST /auth/login
```

**请求参数:**
```json
{
    "phoneNumber": "13800138000",
    "password": "password123"
}
```

**响应:**
```json
{
    "success": true,
    "message": "登录成功",
    "data": {
        "userId": "user_123456",
        "authToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "refreshToken": "refresh_token_here",
        "expiresIn": 3600
    }
}
```

### 2. 刷新Token
```
POST /auth/refresh
```

**请求参数:**
```json
{
    "refreshToken": "refresh_token_here"
}
```

## 用户资料接口

### 1. 保存用户资料
```
POST /profile/save
```

**请求头:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数:**
```json
{
    "nickname": "相约未来605",
    "birthday": "1990年01月01日",
    "gender": "男",
    "height": "175cm",
    "weight": "70kg",
    "emotionalStatus": "单身",
    "annualIncome": "10-20万",
    "occupation": "工程师",
    "hometown": "北京",
    "education": "本科",
    "city": "上海",
    "livingSituation": "独自居住",
    "propertyOwnership": "已购房",
    "carOwnership": "已购车",
    "signature": "热爱生活，追求梦想",
    "constellation": "摩羯座",
    "tags": ["技术", "旅行", "音乐"],
    "updateTime": 1640995200000
}
```

**响应:**
```json
{
    "success": true,
    "message": "资料保存成功",
    "data": {
        "profileId": "profile_123456",
        "updateTime": 1640995200000
    }
}
```

### 2. 获取用户资料
```
GET /profile/get?userId={userId}
```

**请求头:**
```
Authorization: Bearer {token}
```

**响应:**
```json
{
    "success": true,
    "message": "获取成功",
    "data": {
        "nickname": "相约未来605",
        "birthday": "1990年01月01日",
        "gender": "男",
        "height": "175cm",
        "weight": "70kg",
        "emotionalStatus": "单身",
        "annualIncome": "10-20万",
        "occupation": "工程师",
        "hometown": "北京",
        "education": "本科",
        "city": "上海",
        "livingSituation": "独自居住",
        "propertyOwnership": "已购房",
        "carOwnership": "已购车",
        "signature": "热爱生活，追求梦想",
        "constellation": "摩羯座",
        "tags": ["技术", "旅行", "音乐"],
        "updateTime": 1640995200000
    }
}
```

## 认证验证接口

### 1. 实名认证
```
POST /verification/realname
```

**请求头:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数:**
```json
{
    "realName": "张三",
    "idCardNumber": "110101199001011234"
}
```

**响应:**
```json
{
    "success": true,
    "message": "实名认证成功",
    "data": {
        "verificationId": "realname_ver_123456",
        "status": "approved",
        "age": 34,
        "gender": "男",
        "birthDate": "1990年01月01日",
        "address": "北京市朝阳区"
    }
}
```

### 2. 手机号验证
```
POST /verification/phone
```

**请求头:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数:**
```json
{
    "phoneNumber": "13800138000"
}
```

**响应:**
```json
{
    "success": true,
    "message": "手机号验证成功",
    "data": {
        "verificationId": "phone_ver_123456",
        "status": "approved",
        "operator": "中国移动",
        "location": "北京"
    }
}
```

### 3. 人脸识别验证
```
POST /verification/face
```

**请求头:**
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**请求参数:**
- `faceImage`: 人脸图片文件
- `idCardImage`: 身份证图片文件

**响应:**
```json
{
    "success": true,
    "message": "人脸识别验证成功",
    "data": {
        "verificationId": "face_ver_123456",
        "status": "approved",
        "similarity": 0.95
    }
}
```

## 文件上传接口

### 1. 上传图片
```
POST /upload/image
```

**请求头:**
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**请求参数:**
- `image`: 图片文件
- `type`: 图片类型 (avatar, idcard, face, album)

**响应:**
```json
{
    "success": true,
    "message": "图片上传成功",
    "data": {
        "imageUrl": "https://cdn.example.com/images/avatar_123456.jpg",
        "imageId": "img_123456"
    }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token无效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 通用响应格式

### 成功响应
```json
{
    "success": true,
    "message": "操作成功",
    "data": {
        // 具体数据
    }
}
```

### 错误响应
```json
{
    "success": false,
    "message": "错误信息",
    "errorCode": "ERROR_CODE",
    "data": null
}
```

## 安全要求

1. **HTTPS**: 所有API必须使用HTTPS协议
2. **Token认证**: 除登录接口外，所有接口都需要Bearer Token认证
3. **数据加密**: 敏感数据（如身份证号）需要加密传输
4. **频率限制**: 防止API滥用，建议设置请求频率限制
5. **日志记录**: 记录所有API调用日志，便于问题排查

## 数据库设计建议

### 用户表 (users)
```sql
CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 用户资料表 (user_profiles)
```sql
CREATE TABLE user_profiles (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    nickname VARCHAR(50),
    birthday VARCHAR(20),
    gender VARCHAR(10),
    height VARCHAR(10),
    weight VARCHAR(10),
    emotional_status VARCHAR(20),
    annual_income VARCHAR(20),
    occupation VARCHAR(50),
    hometown VARCHAR(50),
    education VARCHAR(20),
    city VARCHAR(50),
    living_situation VARCHAR(50),
    property_ownership VARCHAR(50),
    car_ownership VARCHAR(50),
    signature TEXT,
    constellation VARCHAR(20),
    tags JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 认证记录表 (verification_records)
```sql
CREATE TABLE verification_records (
    id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    verification_type VARCHAR(20) NOT NULL,
    verification_data JSON,
    status VARCHAR(20) NOT NULL,
    result_data JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```
