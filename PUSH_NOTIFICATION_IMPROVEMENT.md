# 推送通知多设备支持改进方案

## 问题分析

### 当前设计的问题
1. **单设备限制**：每个用户账号只能存储一个Registration ID
2. **设备覆盖**：新设备登录会覆盖旧设备的Registration ID
3. **推送丢失**：只能向最后一个登录的设备发送推送
4. **用户体验差**：多设备用户容易错过重要通知

## 改进方案

### 1. 数据库设计改进

#### 新增用户设备表
```sql
CREATE TABLE user_devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    registration_id VARCHAR(100) NOT NULL,
    device_name VARCHAR(100),
    device_type VARCHAR(20) DEFAULT 'ANDROID',
    app_version VARCHAR(20),
    os_version VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    last_active_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 关系设计
```
用户账号 (users)
├── 设备1 (user_devices) - Registration ID 1
├── 设备2 (user_devices) - Registration ID 2
└── 设备3 (user_devices) - Registration ID 3
```

### 2. 核心服务改进

#### UserDeviceService
- `registerOrUpdateDevice()` - 注册或更新设备
- `getActiveDevices()` - 获取用户活跃设备
- `getActiveRegistrationIds()` - 获取所有活跃设备的Registration ID
- `deactivateDevice()` - 停用特定设备
- `cleanupInactiveDevices()` - 清理过期设备

#### JPushService 改进
- 支持向多个设备同时发送推送
- 使用 `audience.setRegistrationIdList(registrationIds)` 替代单个ID
- 推送统计显示设备数量而非单个ID

### 3. API 接口

#### 设备管理接口
- `POST /api/device/register` - 注册设备
- `GET /api/device/list` - 获取设备列表
- `POST /api/device/deactivate` - 停用设备
- `GET /api/device/stats` - 获取设备统计

### 4. 推送策略

#### 推送目标选择
1. **向所有设备推送**：重要通知（新消息、系统通知）
2. **向特定设备推送**：设备相关通知（来电、设备状态）
3. **智能推送**：根据设备活跃度选择推送

#### 推送时机
- 应用启动时注册设备
- 用户登录时更新设备信息
- 定期清理非活跃设备（30天）

## 主流应用设计对比

### WhatsApp
- 支持多设备同时在线
- 每个设备独立接收推送
- 设备间消息同步

### Telegram
- 多设备支持
- 可选择推送设备
- 设备管理界面

### 微信
- 手机+电脑双端
- 推送策略区分
- 设备状态管理

## 实施步骤

### 1. 数据库迁移
```bash
# 执行迁移脚本
mysql -u username -p database_name < backend/database/add_user_devices_table.sql
```

### 2. 后端代码更新
- 部署新的实体类和服务
- 更新推送逻辑
- 添加设备管理接口

### 3. 前端适配
- 更新设备注册逻辑
- 添加设备管理界面
- 优化推送处理

### 4. 测试验证
- 多设备登录测试
- 推送功能验证
- 性能压力测试

## 优势

1. **多设备支持**：用户可在多个设备上接收推送
2. **推送可靠性**：不会因为设备切换丢失推送
3. **用户体验**：重要通知不会错过
4. **可扩展性**：支持未来更多设备类型
5. **管理便利**：可查看和管理所有设备

## 兼容性

- 保持原有API兼容
- 渐进式迁移
- 向后兼容旧版本客户端

## 监控指标

- 设备注册数量
- 推送成功率
- 设备活跃度
- 推送延迟时间
