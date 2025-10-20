# 道具商城靓号系统 API 文档

## 概述

道具商城靓号系统提供了完整的靓号购买、管理和展示功能，支持限量、超级、顶级三个等级的靓号，以及特殊靓号的识别和管理。

## 核心功能

### 1. 靓号等级体系
- **限量靓号 (LIMITED)**: 价格 3800-88800 聊币
- **超级靓号 (SUPER)**: 价格 8800 聊币，具有特殊标识
- **顶级靓号 (TOP_TIER)**: 价格 10800-108000 聊币，最高等级

### 2. 靓号状态
- **AVAILABLE**: 可购买
- **SOLD**: 已售出
- **RESERVED**: 已预订

## API 接口

### 1. 获取可购买的靓号列表

**接口地址**: `GET /api/prop-mall/lucky-numbers`

**请求参数**:
- `page` (int, 可选): 页码，默认 0
- `size` (int, 可选): 每页数量，默认 20

**响应示例**:
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 26,
        "number": "16881688",
        "price": 2880.00,
        "tier": "LIMITED",
        "tierDisplayName": "限量",
        "status": "AVAILABLE",
        "statusDisplayName": "可购买",
        "description": "靓号",
        "isSpecial": false,
        "createdAt": "2025-10-19T00:40:24.282735",
        "updatedAt": "2025-10-19T00:40:24.282735"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "totalElements": 45,
      "totalPages": 5
    }
  }
}
```

### 2. 根据等级获取靓号列表

**接口地址**: `GET /api/prop-mall/lucky-numbers/tier/{tier}`

**路径参数**:
- `tier`: 等级名称 (LIMITED/SUPER/TOP_TIER)

**请求参数**:
- `page` (int, 可选): 页码，默认 0
- `size` (int, 可选): 每页数量，默认 20

### 3. 根据价格范围获取靓号列表

**接口地址**: `GET /api/prop-mall/lucky-numbers/price-range`

**请求参数**:
- `minPrice` (double): 最低价格
- `maxPrice` (double): 最高价格
- `page` (int, 可选): 页码，默认 0
- `size` (int, 可选): 每页数量，默认 20

### 4. 获取特殊靓号列表

**接口地址**: `GET /api/prop-mall/lucky-numbers/special`

**请求参数**:
- `page` (int, 可选): 页码，默认 0
- `size` (int, 可选): 每页数量，默认 20

### 5. 购买靓号

**接口地址**: `POST /api/prop-mall/lucky-numbers/purchase`

**请求头**:
- `Authorization`: Bearer {token}

**请求体**:
```json
{
  "luckyNumberId": 26,
  "validityDays": 30
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "购买成功",
  "data": {
    "id": 26,
    "number": "16881688",
    "price": 2880.00,
    "tier": "LIMITED",
    "tierDisplayName": "限量",
    "status": "SOLD",
    "statusDisplayName": "已售出",
    "ownerId": 22491729,
    "purchaseTime": "2025-10-19T00:50:22.719695",
    "validityDays": 30,
    "expireTime": "2025-11-18T00:50:22.719695",
    "description": "靓号",
    "isSpecial": false
  }
}
```

### 6. 获取用户拥有的靓号

**接口地址**: `GET /api/prop-mall/my-lucky-numbers`

**请求头**:
- `Authorization`: Bearer {token}

**响应示例**:
```json
{
  "success": true,
  "message": "获取成功",
  "data": [
    {
      "id": 26,
      "number": "16881688",
      "price": 2880.00,
      "tier": "LIMITED",
      "tierDisplayName": "限量",
      "status": "SOLD",
      "statusDisplayName": "已售出",
      "ownerId": 22491729,
      "purchaseTime": "2025-10-19T00:50:22.719695",
      "validityDays": 30,
      "expireTime": "2025-11-18T00:50:22.719695",
      "description": "靓号",
      "isSpecial": false
    }
  ]
}
```

### 7. 获取靓号详情

**接口地址**: `GET /api/prop-mall/lucky-numbers/{id}`

**路径参数**:
- `id`: 靓号ID

### 8. 检查靓号是否可用

**接口地址**: `GET /api/prop-mall/lucky-numbers/check-availability`

**请求参数**:
- `number` (string): 靓号数字

### 9. 获取用户靓号统计

**接口地址**: `GET /api/prop-mall/my-lucky-numbers/stats`

**请求头**:
- `Authorization`: Bearer {token}

**响应示例**:
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "totalCount": 1,
    "expiringCount": 0,
    "hasExpiring": false
  }
}
```

## 错误处理

### 常见错误码

- `400`: 请求参数错误
- `401`: 未授权，需要登录
- `404`: 资源不存在
- `500`: 服务器内部错误

### 业务错误

- `余额不足`: 用户钱包余额不足以购买靓号
- `靓号不可购买`: 靓号状态不是可购买状态
- `靓号不存在`: 指定的靓号ID不存在

## 数据库设计

### 靓号表 (lucky_numbers)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| number | VARCHAR(50) | 靓号数字，唯一 |
| price | DECIMAL(10,2) | 价格（聊币） |
| tier | ENUM | 等级：LIMITED/SUPER/TOP_TIER |
| status | ENUM | 状态：AVAILABLE/SOLD/RESERVED |
| owner_id | BIGINT | 拥有者ID |
| purchase_time | DATETIME | 购买时间 |
| validity_days | INT | 有效期（天） |
| expire_time | DATETIME | 过期时间 |
| description | TEXT | 描述 |
| is_special | BOOLEAN | 是否特殊靓号 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

## 前端集成

### Android 前端 API 调用

```kotlin
// 获取靓号列表
val call = apiService.getAvailableLuckyNumbers(0, 20)
call.enqueue(object : Callback<ApiResponse<LuckyNumberPageDTO>> {
    override fun onResponse(call: Call<ApiResponse<LuckyNumberPageDTO>>, response: Response<ApiResponse<LuckyNumberPageDTO>>) {
        if (response.isSuccessful) {
            val luckyNumbers = response.body()?.data?.content
            // 处理靓号列表
        }
    }
    
    override fun onFailure(call: Call<ApiResponse<LuckyNumberPageDTO>>, t: Throwable) {
        // 处理错误
    }
})

// 购买靓号
val purchaseRequest = PurchaseRequest(luckyNumberId, 30)
val call = apiService.purchaseLuckyNumber(authHeader, purchaseRequest)
call.enqueue(object : Callback<ApiResponse<LuckyNumberDTO>> {
    override fun onResponse(call: Call<ApiResponse<LuckyNumberDTO>>, response: Response<ApiResponse<LuckyNumberDTO>>) {
        if (response.isSuccessful) {
            val purchasedLuckyNumber = response.body()?.data
            // 处理购买成功
        }
    }
    
    override fun onFailure(call: Call<ApiResponse<LuckyNumberDTO>>, t: Throwable) {
        // 处理错误
    }
})
```

## 测试用例

### 1. 基本功能测试

```bash
# 获取靓号列表
curl -X GET "http://localhost:8080/api/prop-mall/lucky-numbers?page=0&size=10"

# 获取特殊靓号
curl -X GET "http://localhost:8080/api/prop-mall/lucky-numbers/special?page=0&size=10"

# 购买靓号
curl -X POST "http://localhost:8080/api/prop-mall/lucky-numbers/purchase" \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"luckyNumberId": 26, "validityDays": 30}'
```

### 2. 权限测试

- 未登录用户无法购买靓号
- 已登录用户可以正常购买
- 余额不足时购买失败

### 3. 数据一致性测试

- 购买后靓号状态变为已售出
- 用户钱包余额正确扣除
- 交易记录正确创建

## 扩展功能

### 1. 竞拍系统（可选）
- 靓号竞拍功能
- 出价历史记录
- 自动出价功能

### 2. 靓号转让（可选）
- 用户间靓号转让
- 转让价格协商
- 转让记录管理

### 3. 靓号定制（可选）
- 自定义靓号生成
- 特殊靓号申请
- 靓号价值评估

## 总结

道具商城靓号系统提供了完整的靓号购买和管理功能，支持多等级靓号体系，具备良好的扩展性和可维护性。系统已经过充分测试，可以满足基本的商业需求。
