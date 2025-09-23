# 增强礼物道具商城和VIP等级系统 API 文档

## 系统概述

本系统为社交应用提供了完整的礼物道具商城和VIP等级体系，包括：

- 🎁 **增强礼物系统**：支持分类、稀有度、特效、限量等
- 💰 **虚拟货币系统**：金币、钻石、积分、黄金等多种货币
- 👑 **VIP等级系统**：多级VIP特权，成长值机制
- 🎨 **特效系统**：礼物动画、音效、全屏特效
- 📊 **统计系统**：用户消费、收入、成长值统计

## 核心实体

### 1. 虚拟货币 (VirtualCurrency)
- **货币类型**：COINS(金币), DIAMONDS(钻石), POINTS(积分), GOLD(黄金)
- **功能**：余额管理、冻结解冻、转账

### 2. 礼物 (Gift)
- **分类**：EMOTION(情感), CELEBRATION(庆祝), ROMANCE(浪漫), FRIENDSHIP(友谊), HOLIDAY(节日), SPECIAL(特殊), LIMITED(限量)
- **稀有度**：COMMON(普通), RARE(稀有), EPIC(史诗), LEGENDARY(传说)
- **特效**：支持动画、音效、全屏特效

### 3. VIP等级 (VipLevel)
- **等级**：1-4级，每级对应不同特权
- **特权**：折扣、免费礼物、优先匹配、专属功能等

## API 接口

### 虚拟货币相关

#### 获取用户余额
```http
GET /api/enhanced-gifts/balance
Authorization: Bearer {token}
```

**响应示例：**
```json
{
  "success": true,
  "data": {
    "COINS": 1000.00,
    "DIAMONDS": 50.00,
    "POINTS": 200.00,
    "GOLD": 10.00
  }
}
```

#### 货币交易记录
```http
GET /api/currency/transactions?page=0&size=20
Authorization: Bearer {token}
```

### 礼物商城相关

#### 获取礼物列表
```http
GET /api/enhanced-gifts?category=ROMANCE&rarity=RARE&isLimited=true&page=0&size=20
```

**查询参数：**
- `category`: 礼物分类
- `subCategory`: 子分类
- `rarity`: 稀有度
- `isLimited`: 是否限量
- `isHot`: 是否热门
- `isNew`: 是否新品
- `minPrice`: 最低价格
- `maxPrice`: 最高价格
- `sortBy`: 排序字段
- `sortDir`: 排序方向 (asc/desc)

#### 获取礼物分类
```http
GET /api/enhanced-gifts/categories
```

**响应示例：**
```json
{
  "success": true,
  "data": [
    {
      "value": "EMOTION",
      "label": "情感表达",
      "icon": "❤️"
    },
    {
      "value": "ROMANCE",
      "label": "浪漫",
      "icon": "💕"
    }
  ]
}
```

#### 获取稀有度列表
```http
GET /api/enhanced-gifts/rarities
```

#### 获取热门礼物
```http
GET /api/enhanced-gifts/hot?limit=10
```

#### 获取新品礼物
```http
GET /api/enhanced-gifts/new?limit=10
```

#### 获取限量礼物
```http
GET /api/enhanced-gifts/limited
```

#### 搜索礼物
```http
GET /api/enhanced-gifts/search?keyword=玫瑰&page=0&size=20
```

### 礼物发送相关

#### 发送礼物
```http
POST /api/enhanced-gifts/send
Authorization: Bearer {token}
Content-Type: application/x-www-form-urlencoded

receiverId=123&giftId=456&quantity=1&message=生日快乐&currencyType=COINS
```

**参数说明：**
- `receiverId`: 接收者ID
- `giftId`: 礼物ID
- `quantity`: 数量
- `message`: 留言
- `currencyType`: 货币类型

#### 获取礼物特效
```http
GET /api/enhanced-gifts/{giftId}/effects
```

#### 获取礼物统计
```http
GET /api/enhanced-gifts/stats
Authorization: Bearer {token}
```

### VIP系统相关

#### 获取VIP等级列表
```http
GET /api/vip/levels
```

#### 订阅VIP
```http
POST /api/vip/subscribe?vipLevelId=1
Authorization: Bearer {token}
```

#### 获取当前VIP状态
```http
GET /api/vip/current
Authorization: Bearer {token}
```

#### 检查VIP状态
```http
GET /api/vip/check
Authorization: Bearer {token}
```

### 财富等级相关

#### 获取财富等级列表
```http
GET /api/wealth/levels
```

#### 根据贡献获取等级
```http
GET /api/wealth/level-by-contribution?contribution=1000.00
```

## 数据库设计

### 核心表结构

#### virtual_currencies (虚拟货币表)
```sql
CREATE TABLE virtual_currencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency_type VARCHAR(20) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    frozen_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_earned DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_spent DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    last_updated DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_currency (user_id, currency_type)
);
```

#### currency_transactions (货币交易记录表)
```sql
CREATE TABLE currency_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency_type VARCHAR(20) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    balance_before DECIMAL(15,2),
    balance_after DECIMAL(15,2),
    description VARCHAR(500),
    related_id BIGINT,
    related_type VARCHAR(50),
    status VARCHAR(20) DEFAULT 'SUCCESS',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

#### gift_effects (礼物特效表)
```sql
CREATE TABLE gift_effects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gift_id BIGINT NOT NULL,
    effect_type VARCHAR(50) NOT NULL,
    effect_name VARCHAR(100) NOT NULL,
    effect_url VARCHAR(500),
    duration INT NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    is_loop BOOLEAN DEFAULT FALSE,
    loop_count INT DEFAULT 1,
    trigger_condition VARCHAR(200),
    effect_config TEXT,
    is_active BOOLEAN DEFAULT TRUE
);
```

#### vip_privileges (VIP特权表)
```sql
CREATE TABLE vip_privileges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vip_level_id BIGINT NOT NULL,
    privilege_type VARCHAR(50) NOT NULL,
    privilege_name VARCHAR(100) NOT NULL,
    privilege_description TEXT,
    privilege_value VARCHAR(200),
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0
);
```

#### user_growth (用户成长值表)
```sql
CREATE TABLE user_growth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    total_points INT NOT NULL DEFAULT 0,
    current_level INT NOT NULL DEFAULT 1,
    current_level_points INT NOT NULL DEFAULT 0,
    next_level_points INT NOT NULL DEFAULT 100,
    daily_points INT NOT NULL DEFAULT 0,
    weekly_points INT NOT NULL DEFAULT 0,
    monthly_points INT NOT NULL DEFAULT 0,
    last_daily_reset DATETIME,
    last_weekly_reset DATETIME,
    last_monthly_reset DATETIME
);
```

## 业务逻辑

### 1. 礼物发送流程
1. 验证礼物存在且可用
2. 检查用户余额是否充足
3. 扣除发送者货币
4. 创建礼物记录
5. 给接收者添加收益（扣除平台费）
6. 触发礼物特效

### 2. VIP特权应用
1. 检查用户VIP状态
2. 根据VIP等级应用相应特权
3. 计算折扣、免费次数等
4. 记录特权使用情况

### 3. 成长值计算
1. 用户行为触发成长值增加
2. 检查是否满足升级条件
3. 自动升级并解锁新特权
4. 记录成长值变化

## 配置说明

### 货币类型配置
```yaml
currency:
  types:
    COINS: 
      name: "金币"
      description: "用于购买礼物、道具等"
    DIAMONDS:
      name: "钻石" 
      description: "高级货币，用于购买VIP、特殊道具"
    POINTS:
      name: "积分"
      description: "通过活动获得，可兑换奖励"
    GOLD:
      name: "黄金"
      description: "最高级货币，用于购买稀有道具"
```

### VIP等级配置
```yaml
vip:
  levels:
    1:
      name: "青铜会员"
      price: 30.00
      duration: 30
      privileges:
        - type: "DISCOUNT"
          value: "0.95"
        - type: "FREE_GIFTS"
          value: "3"
    2:
      name: "白银会员"
      price: 60.00
      duration: 30
      privileges:
        - type: "DISCOUNT"
          value: "0.9"
        - type: "FREE_GIFTS"
          value: "5"
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 1001 | 礼物不存在 |
| 1002 | 礼物已下架 |
| 1003 | 余额不足 |
| 1004 | 礼物数量必须大于0 |
| 1005 | 限量礼物库存不足 |
| 2001 | VIP等级不存在 |
| 2002 | 用户已是VIP |
| 2003 | VIP订阅失败 |
| 3001 | 货币类型不支持 |
| 3002 | 转账失败 |
| 3003 | 冻结金额不足 |

## 部署说明

1. 执行数据库迁移脚本
2. 配置虚拟货币类型
3. 初始化VIP等级和特权
4. 上传礼物资源文件
5. 配置特效资源路径

## 监控指标

- 礼物发送成功率
- 用户消费金额统计
- VIP订阅转化率
- 货币交易量统计
- 用户成长值分布
- 热门礼物排行

## 安全考虑

1. 货币交易需要事务保证
2. 防止重复扣费
3. 限制单日消费金额
4. 敏感操作需要二次确认
5. 定期对账检查
