# 社交应用后端API文档

## 概述
这是一个完整的社交应用后端系统，包含用户管理、钱包系统、VIP会员、礼物系统、守护系统、卡券系统、财富等级等核心功能。

## 数据库表结构
系统包含以下主要数据表：
- `users` - 用户表
- `wallets` - 钱包表
- `transactions` - 交易记录表
- `vip_levels` - VIP等级表
- `vip_subscriptions` - VIP订阅表
- `gifts` - 礼物表
- `gift_records` - 礼物记录表
- `guard_relationships` - 守护关系表
- `coupons` - 卡券表
- `user_coupons` - 用户卡券表
- `wealth_levels` - 财富等级表
- `user_views` - 用户浏览记录表
- `follow_relationships` - 关注关系表
- `user_likes` - 用户喜欢表
- `intimacy_relationships` - 亲密关系表
- `payment_orders` - 支付订单表
- `system_configs` - 系统配置表

## API接口列表

### 1. 认证相关 (AuthController)
- `POST /api/auth/send-code` - 发送验证码
- `POST /api/auth/login-with-code` - 验证码登录
- `GET /api/auth/health` - 健康检查

### 2. 用户相关 (UserController)
- `GET /api/users/profile` - 获取用户信息
- `PUT /api/users/profile` - 更新用户信息
- `GET /api/users/search` - 搜索用户
- `GET /api/users/{id}` - 获取用户详情
- `POST /api/users/follow/{userId}` - 关注用户
- `DELETE /api/users/follow/{userId}` - 取消关注
- `GET /api/users/following` - 获取关注列表
- `GET /api/users/followers` - 获取粉丝列表

### 3. 钱包相关 (WalletController)
- `GET /api/wallet/balance` - 获取钱包余额
- `POST /api/wallet/recharge` - 充值
- `GET /api/wallet/transactions` - 获取交易记录
- `GET /api/wallet/transactions/type/{type}` - 按类型获取交易记录

### 4. VIP相关 (VipController)
- `GET /api/vip/levels` - 获取VIP等级列表
- `GET /api/vip/levels/{id}` - 获取VIP等级详情
- `POST /api/vip/subscribe` - 订阅VIP
- `GET /api/vip/current` - 获取当前VIP订阅
- `GET /api/vip/history` - 获取VIP历史
- `GET /api/vip/check` - 检查VIP状态
- `GET /api/vip/level` - 获取VIP等级

### 5. 礼物相关 (GiftController)
- `GET /api/gifts` - 获取礼物列表
- `GET /api/gifts/category/{category}` - 按分类获取礼物
- `GET /api/gifts/price-range` - 按价格范围获取礼物
- `GET /api/gifts/{id}` - 获取礼物详情
- `POST /api/gifts/send` - 发送礼物
- `GET /api/gifts/sent` - 获取发送的礼物
- `GET /api/gifts/received` - 获取收到的礼物
- `GET /api/gifts/history` - 获取礼物历史
- `GET /api/gifts/stats/sent-amount` - 获取发送总额
- `GET /api/gifts/stats/received-amount` - 获取接收总额

### 6. 守护相关 (GuardController)
- `POST /api/guard/become` - 成为守护者
- `POST /api/guard/stop` - 停止守护
- `GET /api/guard/guardians` - 获取守护者列表
- `GET /api/guard/protected` - 获取被守护用户列表
- `GET /api/guard/ranking` - 获取守护收入榜
- `GET /api/guard/income` - 获取守护收入
- `POST /api/guard/contribute` - 添加贡献
- `GET /api/guard/check` - 检查守护状态

### 7. 卡券相关 (CouponController)
- `GET /api/coupons` - 获取可用卡券列表
- `GET /api/coupons/type/{type}` - 按类型获取卡券
- `POST /api/coupons/give` - 领取卡券
- `GET /api/coupons/my` - 获取我的卡券
- `GET /api/coupons/usable` - 获取可用卡券
- `GET /api/coupons/status/{status}` - 按状态获取卡券
- `POST /api/coupons/use/{userCouponId}` - 使用卡券
- `POST /api/coupons/use-by-type/{type}` - 按类型使用卡券

### 8. 财富等级相关 (WealthController)
- `GET /api/wealth/levels` - 获取财富等级列表
- `GET /api/wealth/levels/{id}` - 获取财富等级详情
- `GET /api/wealth/level-by-contribution` - 根据贡献获取财富等级
- `GET /api/wealth/higher-levels/{currentLevel}` - 获取更高财富等级
- `GET /api/wealth/user-level` - 获取用户财富等级
- `GET /api/wealth/user-level-name` - 获取用户财富等级名称

### 9. 支付相关 (PaymentController)
- `POST /api/payment/create` - 创建支付订单
- `GET /api/payment/order/{orderNo}` - 获取支付订单
- `GET /api/payment/my-orders` - 获取我的支付订单
- `GET /api/payment/my-orders/status/{status}` - 按状态获取支付订单
- `POST /api/payment/success` - 支付成功回调
- `POST /api/payment/failed` - 支付失败回调
- `POST /api/payment/cancel/{orderNo}` - 取消支付订单
- `GET /api/payment/methods` - 获取支付方式

### 10. 动态相关 (PostController)
- `GET /api/posts` - 获取动态列表
- `POST /api/posts` - 发布动态
- `GET /api/posts/{id}` - 获取动态详情
- `PUT /api/posts/{id}` - 更新动态
- `DELETE /api/posts/{id}` - 删除动态
- `POST /api/posts/{id}/like` - 点赞动态
- `POST /api/posts/{id}/unlike` - 取消点赞动态

### 11. 消息相关 (MessageController)
- `GET /api/messages` - 获取消息列表
- `POST /api/messages` - 发送消息
- `GET /api/messages/conversations` - 获取会话列表
- `GET /api/messages/{userId}` - 获取与特定用户的消息
- `PUT /api/messages/{id}/read` - 标记消息已读
- `DELETE /api/messages/{id}` - 删除消息

### 12. 统计相关 (StatsController)
- `POST /api/stats/view` - 记录浏览
- `GET /api/stats/views` - 获取浏览记录
- `GET /api/stats/views/count` - 获取浏览次数
- `GET /api/stats/views/recent` - 获取最近浏览记录

## 数据模型

### 用户模型 (UserDTO)
```json
{
  "id": 1,
  "username": "user123",
  "nickname": "用户昵称",
  "phone": "13800138000",
  "email": "user@example.com",
  "avatarUrl": "https://example.com/avatar.jpg",
  "gender": "MALE",
  "birthday": "1990-01-01",
  "constellation": "水瓶座",
  "location": "北京市",
  "height": 175,
  "weight": 70,
  "incomeLevel": "HIGH",
  "education": "本科",
  "maritalStatus": "SINGLE",
  "signature": "个人签名",
  "isVerified": true,
  "isVip": false,
  "vipLevel": 0,
  "wealthLevel": 1,
  "balance": 100.00,
  "isOnline": true,
  "lastActiveAt": "2024-01-01T12:00:00",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

### 钱包模型 (WalletDTO)
```json
{
  "id": 1,
  "userId": 1,
  "balance": 100.00,
  "frozenAmount": 10.00,
  "availableBalance": 90.00,
  "currency": "CNY",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T12:00:00"
}
```

### VIP等级模型 (VipLevelDTO)
```json
{
  "id": 1,
  "name": "VIP会员",
  "level": 1,
  "price": 99.00,
  "duration": 30,
  "benefits": "无限制聊天、查看访客、优先推荐",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### 礼物模型 (GiftDTO)
```json
{
  "id": 1,
  "name": "玫瑰花",
  "description": "表达爱意的经典礼物",
  "imageUrl": "https://example.com/rose.jpg",
  "price": 1.00,
  "category": "LOVE",
  "isActive": true,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

## 启动说明

1. 确保已安装Java 17+和MySQL 8.0+
2. 创建数据库并配置连接信息
3. 运行`schema.sql`创建表结构
4. 启动Spring Boot应用
5. 系统会自动初始化基础数据

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带`Authorization: Bearer <token>`
2. 金额字段使用BigDecimal类型，确保精度
3. 时间字段使用ISO 8601格式
4. 分页参数：page从0开始，size默认为20
5. 所有API都支持CORS跨域访问

## 错误处理

所有API都返回统一的响应格式：
```json
{
  "success": true,
  "message": "操作成功",
  "data": {...},
  "timestamp": "2024-01-01T12:00:00"
}
```

错误响应：
```json
{
  "success": false,
  "message": "错误信息",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```
