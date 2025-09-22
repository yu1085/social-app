# 身份证二要素核验API文档

## 概述

身份证二要素核验是支付宝官方推荐的实名认证方式，通过验证用户的真实姓名和身份证号码来确认身份。这种方式简单、安全、可靠，无需集成客户端SDK，只需服务端调用支付宝API即可。

## 接口列表

### 1. 提交身份证二要素核验

**接口地址**: `POST /api/auth/third-party/submit-verification`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**请求参数**:
```json
{
    "certName": "张三",
    "certNo": "110101199001011234"
}
```

**参数说明**:
- `certName`: 真实姓名，必填，最大长度20字符
- `certNo`: 身份证号码，必填，18位身份证号码

**响应示例**:
```json
{
    "success": true,
    "message": "实名认证提交成功",
    "data": {
        "verificationResult": {
            "authId": "AUTH_1703123456789_abc12345",
            "match": true,
            "orderNo": "ORDER_1703123456789",
            "message": "身份证二要素核验通过",
            "status": "SUCCESS",
            "verifiedAt": "2023-12-21T10:30:45"
        }
    }
}
```

### 2. 获取实名认证状态

**接口地址**: `GET /api/auth/third-party/verification-status`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "hasVerified": true,
        "verificationStatus": "VERIFIED",
        "verificationTime": "2023-12-21T10:30:45",
        "verificationMethod": "ID_CARD_VERIFY",
        "realName": "张三",
        "idCardNumber": "1101****1234"
    }
}
```

**状态说明**:
- `NOT_VERIFIED`: 未认证
- `PENDING`: 认证中
- `VERIFIED`: 已认证
- `FAILED`: 认证失败

### 3. 查询认证结果

**接口地址**: `GET /api/auth/third-party/verification-result`

**请求头**:
```
Authorization: Bearer {JWT_TOKEN}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "hasRecord": true,
        "verificationResult": {
            "authId": "AUTH_1703123456789_abc12345",
            "authType": "ID_CARD_VERIFY",
            "status": "SUCCESS",
            "message": "身份证二要素核验通过",
            "realName": "张三",
            "idCardNumber": "1101****1234",
            "createdAt": "2023-12-21T10:30:00",
            "completedAt": "2023-12-21T10:30:45",
            "rejectReason": null
        }
    }
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未授权，请检查JWT Token |
| 500 | 服务器内部错误 |

## 使用流程

### 1. 用户提交认证信息
```javascript
// 前端调用示例
const response = await fetch('/api/auth/third-party/submit-verification', {
    method: 'POST',
    headers: {
        'Authorization': 'Bearer ' + token,
        'Content-Type': 'application/json'
    },
    body: JSON.stringify({
        certName: '张三',
        certNo: '110101199001011234'
    })
});

const result = await response.json();
if (result.success) {
    console.log('认证成功');
} else {
    console.log('认证失败:', result.message);
}
```

### 2. 查询认证状态
```javascript
// 查询用户认证状态
const response = await fetch('/api/auth/third-party/verification-status', {
    headers: {
        'Authorization': 'Bearer ' + token
    }
});

const result = await response.json();
if (result.data.hasVerified) {
    console.log('用户已认证');
} else {
    console.log('用户未认证');
}
```

## 安全说明

1. **数据加密**: 所有敏感信息在传输过程中都使用HTTPS加密
2. **数据脱敏**: 返回的身份证号码会自动脱敏处理
3. **权限控制**: 用户只能查询自己的认证信息
4. **数据保护**: 认证信息仅用于身份验证，不会用于其他用途

## 注意事项

1. **身份证格式**: 必须输入18位有效身份证号码
2. **姓名格式**: 姓名必须与身份证上的姓名完全一致
3. **认证限制**: 每个用户只能进行一次认证，重复认证会返回已认证状态
4. **数据存储**: 认证信息会安全存储在数据库中，用于后续验证

## 支付宝API配置

要使用此功能，需要在支付宝开放平台配置以下信息：

1. **应用信息**:
   - APPID: 支付宝应用的唯一标识
   - 应用私钥: 用于签名请求
   - 支付宝公钥: 用于验证响应

2. **接口权限**:
   - `datadigital.fincloud.generalsaas.twometa.check`: 身份证二要素核验接口

3. **配置示例**:
```yaml
app:
  alipay:
    app-id: 2021005195696348
    private-key: "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC..."
    public-key: "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA..."
```

## 测试说明

当前实现使用模拟数据，实际部署时需要：

1. 配置真实的支付宝应用信息
2. 集成支付宝SDK
3. 实现真实的API调用逻辑

测试时可以使用以下测试数据：
- 姓名: 张三
- 身份证: 110101199001011234

## 更新日志

- **v1.0.0** (2023-12-21): 初始版本，支持身份证二要素核验
