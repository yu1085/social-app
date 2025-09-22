# 广场动态 API 文档

## 概述

广场动态模块提供用户发布、查看、点赞、搜索动态等功能。支持多种动态类型筛选和缓存优化。

## 基础信息

- **基础URL**: `/api/dynamics`
- **认证方式**: Bearer Token
- **数据格式**: JSON
- **字符编码**: UTF-8

## API 接口

### 1. 发布动态

**接口地址**: `POST /api/dynamics`

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求参数**:
```json
{
  "content": "动态内容（必填，最大2000字符）",
  "location": "位置信息（可选，最大100字符）",
  "images": ["图片URL1", "图片URL2"] // 可选，最多9张
}
```

**响应示例**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 1,
    "userId": 123,
    "content": "今天天气真好！",
    "location": "北京市朝阳区",
    "images": ["https://example.com/image1.jpg"],
    "likeCount": 0,
    "commentCount": 0,
    "viewCount": 0,
    "status": "PUBLISHED",
    "publishTime": "2024-01-01T10:00:00",
    "userNickname": "张三",
    "userAvatar": "https://example.com/avatar.jpg",
    "isLiked": false,
    "isFreeMinute": false
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### 2. 获取动态列表

**接口地址**: `GET /api/dynamics`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| type | String | 否 | latest | 动态类型：latest(最新)、hot(热门)、nearby(附近)、following(关注)、liked(已点赞) |
| page | Integer | 否 | 0 | 页码，从0开始 |
| size | Integer | 否 | 20 | 每页大小 |

**响应示例**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 123,
        "content": "今天天气真好！",
        "location": "北京市朝阳区",
        "images": ["https://example.com/image1.jpg"],
        "likeCount": 5,
        "commentCount": 2,
        "viewCount": 100,
        "status": "PUBLISHED",
        "publishTime": "2024-01-01T10:00:00",
        "userNickname": "张三",
        "userAvatar": "https://example.com/avatar.jpg",
        "isLiked": true,
        "isFreeMinute": false
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false,
    "numberOfElements": 20
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### 3. 获取动态详情

**接口地址**: `GET /api/dynamics/{id}`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 动态ID |

**响应示例**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 1,
    "userId": 123,
    "content": "今天天气真好！",
    "location": "北京市朝阳区",
    "images": ["https://example.com/image1.jpg"],
    "likeCount": 5,
    "commentCount": 2,
    "viewCount": 101,
    "status": "PUBLISHED",
    "publishTime": "2024-01-01T10:00:00",
    "userNickname": "张三",
    "userAvatar": "https://example.com/avatar.jpg",
    "isLiked": true,
    "isFreeMinute": false
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### 4. 点赞动态

**接口地址**: `POST /api/dynamics/{id}/like`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 动态ID |

**响应示例**:
```json
{
  "success": true,
  "message": "点赞成功",
  "data": "点赞成功",
  "timestamp": "2024-01-01T10:00:00"
}
```

### 5. 删除动态

**接口地址**: `DELETE /api/dynamics/{id}`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 动态ID |

**响应示例**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": "删除成功",
  "timestamp": "2024-01-01T10:00:00"
}
```

### 6. 搜索动态

**接口地址**: `GET /api/dynamics/search`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| keyword | String | 是 | - | 搜索关键词 |
| page | Integer | 否 | 0 | 页码，从0开始 |
| size | Integer | 否 | 20 | 每页大小 |

**响应示例**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 123,
        "content": "今天天气真好！",
        "location": "北京市朝阳区",
        "images": ["https://example.com/image1.jpg"],
        "likeCount": 5,
        "commentCount": 2,
        "viewCount": 100,
        "status": "PUBLISHED",
        "publishTime": "2024-01-01T10:00:00",
        "userNickname": "张三",
        "userAvatar": "https://example.com/avatar.jpg",
        "isLiked": true,
        "isFreeMinute": false
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 10,
    "totalPages": 1,
    "first": true,
    "last": true,
    "numberOfElements": 10
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### 7. 获取性能统计

**接口地址**: `GET /api/dynamics/performance/stats`

**请求头**: 无

**响应示例**:
```json
{
  "totalRequests": 1000,
  "successfulRequests": 950,
  "failedRequests": 50,
  "averageResponseTime": 150.5,
  "maxResponseTime": 2000.0,
  "minResponseTime": 50.0,
  "apiStats": {
    "publishDynamic": {
      "count": 100,
      "averageTime": 200.0,
      "errorCount": 5
    },
    "getDynamics": {
      "count": 500,
      "averageTime": 100.0,
      "errorCount": 10
    }
  }
}
```

### 8. 重置性能统计

**接口地址**: `POST /api/dynamics/performance/reset`

**请求头**: 无

**响应示例**:
```json
"性能统计已重置"
```

## 错误码说明

| 错误码 | HTTP状态码 | 说明 |
|--------|------------|------|
| USER_NOT_LOGGED_IN | 400 | 用户未登录 |
| USER_NOT_FOUND | 400 | 用户不存在 |
| INVALID_REQUEST | 400 | 请求参数无效 |
| EMPTY_CONTENT | 400 | 动态内容为空 |
| CONTENT_TOO_LONG | 400 | 动态内容过长 |
| LOCATION_TOO_LONG | 400 | 位置信息过长 |
| TOO_MANY_IMAGES | 400 | 图片数量过多 |
| DYNAMIC_NOT_FOUND | 400 | 动态不存在 |
| NO_PERMISSION | 400 | 无权限操作 |

## 缓存策略

### 缓存键设计
- 动态列表: `dynamicLists:{type}_{page}_{size}`
- 动态详情: `dynamics:{id}`
- 搜索缓存: `dynamicSearch:{keyword}_{page}_{size}`
- 用户动态: `userDynamics:{userId}_{page}_{size}`
- 热门动态: `hotDynamics:{page}_{size}`
- 附近动态: `nearbyDynamics:{lat}_{lng}_{page}_{size}`

### 缓存失效策略
- 发布动态时清除所有相关缓存
- 点赞/取消点赞时清除动态详情和列表缓存
- 删除动态时清除所有相关缓存

## 性能优化

### 数据库优化
- 使用索引优化查询性能
- 批量操作减少数据库访问
- 分页查询避免大数据量加载

### 缓存优化
- Redis缓存热点数据
- 多级缓存策略
- 缓存预热机制

### 异步处理
- 用户统计更新异步处理
- 非关键操作异步执行
- 性能监控异步记录

## 安全考虑

### 输入验证
- 内容长度限制
- 图片数量限制
- 特殊字符过滤

### 权限控制
- Token验证
- 用户身份验证
- 操作权限检查

### 数据保护
- 软删除机制
- 敏感信息脱敏
- 操作日志记录

## 监控和日志

### 性能监控
- API响应时间统计
- 错误率监控
- 缓存命中率统计

### 日志记录
- 操作日志
- 错误日志
- 性能日志

## 版本历史

- **v1.0** (2024-01-01): 初始版本，基础功能实现
- **v1.1** (2024-01-15): 添加缓存优化和性能监控
- **v1.2** (2024-02-01): 完善错误处理和日志记录
