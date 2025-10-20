# 个人资料API文档

## 概述
本文档描述了个人资料相关的所有API接口，包括用户信息、钱包、VIP、设置等功能。

## 基础信息
- 基础URL: `http://localhost:8080/api`
- 认证方式: Bearer Token
- 内容类型: `application/json`

## API接口列表

### 1. 获取用户完整资料信息
**接口地址:** `GET /api/profile`

**请求头:**
```
Authorization: Bearer <token>
```

**响应示例:**
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "user": {
      "id": 1,
      "username": "test_user_1",
      "nickname": "测试用户1",
      "phone": "13800138000",
      "email": null,
      "avatarUrl": null,
      "gender": "MALE",
      "birthday": "1990-01-01",
      "constellation": "摩羯座",
      "location": "北京市",
      "height": 175,
      "weight": 70,
      "incomeLevel": "中等",
      "education": "本科",
      "maritalStatus": "未婚",
      "signature": "这是一个测试用户",
      "isVerified": true,
      "isVip": false,
      "vipLevel": 0,
      "wealthLevel": 1,
      "balance": 100.0,
      "isOnline": true,
      "lastActiveAt": "2024-01-01 12:00:00",
      "createdAt": "2024-01-01 10:00:00",
      "updatedAt": "2024-01-01 12:00:00"
    },
    "vipInfo": {
      "isVip": false,
      "vipLevel": 0,
      "vipLevelName": "普通用户",
      "vipExpireAt": null,
      "remainingDays": 0,
      "vipBenefits": "无特殊权益",
      "canUpgrade": true,
      "nextLevelName": "VIP1",
      "nextLevelRequirement": 100
    },
    "wallet": {
      "userId": 1,
      "balance": 100.00,
      "totalRecharge": 100.00,
      "totalConsume": 0.00,
      "transactionCount": 0,
      "lastTransactionAt": null,
      "createdAt": "2024-01-01 10:00:00",
      "updatedAt": "2024-01-01 12:00:00"
    },
    "settings": {
      "voiceCallEnabled": true,
      "videoCallEnabled": true,
      "messageChargeEnabled": false,
      "voiceCallPrice": 0.0,
      "videoCallPrice": 0.0,
      "messagePrice": 0.0
    }
  }
}
```

### 2. 更新用户资料
**接口地址:** `PUT /api/profile`

**请求头:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体:**
```json
{
  "nickname": "新昵称",
  "gender": "FEMALE",
  "avatarUrl": "https://example.com/avatar.jpg",
  "birthday": "1995-05-15",
  "location": "上海市",
  "signature": "这是我的新签名",
  "height": 165,
  "weight": 55,
  "incomeLevel": "高收入",
  "education": "硕士",
  "maritalStatus": "已婚"
}
```

**响应示例:**
```json
{
  "success": true,
  "message": "更新成功",
  "data": {
    "id": 1,
    "username": "test_user_1",
    "nickname": "新昵称",
    "phone": "13800138000",
    "email": null,
    "avatarUrl": "https://example.com/avatar.jpg",
    "gender": "FEMALE",
    "birthday": "1995-05-15",
    "constellation": "金牛座",
    "location": "上海市",
    "height": 165,
    "weight": 55,
    "incomeLevel": "高收入",
    "education": "硕士",
    "maritalStatus": "已婚",
    "signature": "这是我的新签名",
    "isVerified": true,
    "isVip": false,
    "vipLevel": 0,
    "wealthLevel": 1,
    "balance": 100.0,
    "isOnline": true,
    "lastActiveAt": "2024-01-01 12:00:00",
    "createdAt": "2024-01-01 10:00:00",
    "updatedAt": "2024-01-01 12:30:00"
  }
}
```

### 3. 获取用户设置
**接口地址:** `GET /api/profile/settings`

**请求头:**
```
Authorization: Bearer <token>
```

**响应示例:**
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "voiceCallEnabled": true,
    "videoCallEnabled": true,
    "messageChargeEnabled": false,
    "voiceCallPrice": 0.0,
    "videoCallPrice": 0.0,
    "messagePrice": 0.0
  }
}
```

### 4. 更新用户设置
**接口地址:** `PUT /api/profile/settings`

**请求头:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体:**
```json
{
  "voiceCallEnabled": true,
  "videoCallEnabled": true,
  "messageChargeEnabled": true,
  "voiceCallPrice": 1.5,
  "videoCallPrice": 3.0,
  "messagePrice": 0.5
}
```

**响应示例:**
```json
{
  "success": true,
  "message": "更新成功",
  "data": {
    "voiceCallEnabled": true,
    "videoCallEnabled": true,
    "messageChargeEnabled": true,
    "voiceCallPrice": 1.5,
    "videoCallPrice": 3.0,
    "messagePrice": 0.5
  }
}
```

### 5. 获取钱包信息
**接口地址:** `GET /api/profile/wallet`

**请求头:**
```
Authorization: Bearer <token>
```

**响应示例:**
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "userId": 1,
    "balance": 100.00,
    "totalRecharge": 100.00,
    "totalConsume": 0.00,
    "transactionCount": 0,
    "lastTransactionAt": null,
    "createdAt": "2024-01-01 10:00:00",
    "updatedAt": "2024-01-01 12:00:00"
  }
}
```

### 6. 获取VIP信息
**接口地址:** `GET /api/profile/vip`

**请求头:**
```
Authorization: Bearer <token>
```

**响应示例:**
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "isVip": false,
    "vipLevel": 0,
    "vipLevelName": "普通用户",
    "vipExpireAt": null,
    "remainingDays": 0,
    "vipBenefits": "无特殊权益",
    "canUpgrade": true,
    "nextLevelName": "VIP1",
    "nextLevelRequirement": 100
  }
}
```

### 7. 获取用户统计信息
**接口地址:** `GET /api/profile/stats`

**请求头:**
```
Authorization: Bearer <token>
```

**响应示例:**
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "userId": 1,
    "username": "test_user_1",
    "nickname": "测试用户1",
    "isOnline": true,
    "isVerified": true,
    "isVip": false,
    "vipLevel": 0,
    "wealthLevel": 1,
    "registerDays": 30,
    "lastActiveAt": "2024-01-01 12:00:00",
    "balance": 100.00,
    "totalRecharge": 100.00,
    "totalConsume": 0.00,
    "transactionCount": 0
  }
}
```

## 错误响应

所有接口在出错时都会返回以下格式的错误响应：

```json
{
  "success": false,
  "message": "错误描述",
  "data": null
}
```

## 常见错误码

- `400` - 请求参数错误
- `401` - 未授权（Token无效或过期）
- `404` - 资源不存在
- `500` - 服务器内部错误

## 测试脚本

项目根目录提供了测试脚本 `test_profile_api.ps1`，可以用于测试所有个人资料相关的API接口。

## 数据库表结构

### users表
存储用户基本信息，包括个人资料、VIP状态、财富等级等。

### user_settings表
存储用户设置，包括通话设置、收费设置等。

### wallets表
存储用户钱包信息，包括余额、充值记录、消费记录等。

### user_devices表
存储用户设备信息，用于多设备推送管理。

## 前端集成

前端已集成新的个人资料API，主要文件：
- `ApiService.java` - API接口定义
- `ProfileComposeHost.kt` - 个人资料页面
- `ProfileViewModel.kt` - 数据管理
- 相关DTO类 - 数据传输对象

## 启动说明

1. 确保MySQL服务已启动
2. 执行数据库初始化脚本：`init_all_tables.sql`
3. 启动后端服务：`start_backend_with_profile.ps1`
4. 运行测试脚本：`test_profile_api.ps1`
