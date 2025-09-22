# 第三方实名认证API文档

## 概述
提供支付宝和微信第三方实名认证服务，用户可以通过第三方平台完成身份验证。

## 接口列表

### 1. 获取支付宝认证URL
**POST** `/api/auth/third-party/alipay/url`

**请求头：**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体：**
```json
{
  "authType": "ALIPAY",
  "realName": "张三",
  "idCardNumber": "110101199001011234",
  "phoneNumber": "13800138000",
  "redirectUrl": "https://yourapp.com/auth/callback",
  "extraData": "{\"source\":\"mobile\"}"
}
```

**响应：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "authId": "AUTH_1703123456789_abc12345",
    "authType": "ALIPAY",
    "status": "PENDING",
    "authUrl": "https://openapi.alipay.com/gateway.do?...",
    "message": "请跳转到支付宝完成认证",
    "createdAt": "2023-12-21T10:30:00",
    "expiresAt": "2023-12-21T11:00:00"
  }
}
```

### 2. 获取微信认证URL
**POST** `/api/auth/third-party/wechat/url`

**请求头：**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**请求体：**
```json
{
  "authType": "WECHAT",
  "realName": "李四",
  "idCardNumber": "110101199001011234",
  "phoneNumber": "13800138000",
  "redirectUrl": "https://yourapp.com/auth/callback",
  "extraData": "{\"source\":\"mobile\"}"
}
```

**响应：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "authId": "AUTH_1703123456789_def67890",
    "authType": "WECHAT",
    "status": "PENDING",
    "authUrl": "https://api.weixin.qq.com/wxa/business/getuserphonenumber?...",
    "message": "请跳转到微信完成认证",
    "createdAt": "2023-12-21T10:30:00",
    "expiresAt": "2023-12-21T11:00:00"
  }
}
```

### 3. 支付宝认证回调
**POST** `/api/auth/third-party/alipay/callback`

**请求体：**
```json
{
  "auth_id": "AUTH_1703123456789_abc12345",
  "status": "SUCCESS",
  "third_party_id": "alipay_auth_123456",
  "real_name": "张三",
  "id_card_number": "110101199001011234",
  "phone_number": "13800138000",
  "timestamp": "2023-12-21T10:45:00"
}
```

**响应：**
```json
{
  "success": true,
  "message": "认证状态更新成功",
  "data": "认证状态更新成功"
}
```

### 4. 微信认证回调
**POST** `/api/auth/third-party/wechat/callback`

**请求体：**
```json
{
  "auth_id": "AUTH_1703123456789_def67890",
  "status": "SUCCESS",
  "third_party_id": "wechat_auth_123456",
  "real_name": "李四",
  "id_card_number": "110101199001011234",
  "phone_number": "13800138000",
  "timestamp": "2023-12-21T10:45:00"
}
```

**响应：**
```json
{
  "success": true,
  "message": "认证状态更新成功",
  "data": "认证状态更新成功"
}
```

### 5. 查询认证状态
**GET** `/api/auth/third-party/status?authId=AUTH_1703123456789_abc12345`

**请求头：**
```
Authorization: Bearer <token>
```

**响应：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "authId": "AUTH_1703123456789_abc12345",
    "authType": "ALIPAY",
    "status": "SUCCESS",
    "message": "认证成功",
    "createdAt": "2023-12-21T10:30:00",
    "completedAt": "2023-12-21T10:45:00",
    "realName": "张三",
    "idCardNumber": "1101****1234",
    "phoneNumber": "138****8000"
  }
}
```

### 6. 取消认证
**DELETE** `/api/auth/third-party/cancel?authId=AUTH_1703123456789_abc12345`

**请求头：**
```
Authorization: Bearer <token>
```

**响应：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": "认证已取消"
}
```

### 7. 获取支持的认证方式
**GET** `/api/auth/third-party/methods`

**响应：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "alipay": {
      "name": "支付宝实名认证",
      "type": "ALIPAY",
      "description": "通过支付宝完成实名认证",
      "enabled": true
    },
    "wechat": {
      "name": "微信实名认证",
      "type": "WECHAT",
      "description": "通过微信完成实名认证",
      "enabled": true
    }
  }
}
```

## 状态码说明

### 认证状态 (status)
- `PENDING`: 待处理
- `SUCCESS`: 认证成功
- `FAILED`: 认证失败
- `CANCELLED`: 已取消

### 认证类型 (authType)
- `ALIPAY`: 支付宝认证
- `WECHAT`: 微信认证

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未授权访问 |
| 403 | 无权限操作 |
| 404 | 认证记录不存在 |
| 500 | 服务器内部错误 |

## 使用流程

1. **获取认证URL**：调用支付宝或微信认证URL接口
2. **跳转认证**：引导用户跳转到第三方平台完成认证
3. **处理回调**：第三方平台认证完成后会回调我们的接口
4. **查询状态**：可以随时查询认证状态
5. **取消认证**：如需要可以取消待处理的认证

## 注意事项

1. 认证URL有时效性，默认30分钟过期
2. 每个用户每种认证类型最多尝试3次
3. 认证过程中请妥善保存authId，用于后续查询状态
4. 回调接口需要配置在第三方平台的后台
5. 敏感信息（身份证号、手机号）在响应中会进行脱敏处理
