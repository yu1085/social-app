# 日期时间字段优化指南

## 问题背景

在Spring Boot应用中，经常出现无效的日期时间值（如`'0000-00-00 00:00:00'`），这些值会导致：
- MySQL查询错误
- 应用程序启动失败
- 数据一致性问题

## 优化策略

### 1. 代码层面优化

#### 1.1 基础实体类 (BaseEntity)
- 统一处理所有实体的日期时间字段
- 自动设置`createdAt`和`updatedAt`
- 提供`@PrePersist`和`@PreUpdate`回调确保字段正确设置

```java
@MappedSuperclass
public abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        // 自动设置创建和更新时间
    }
    
    @PreUpdate
    protected void onUpdate() {
        // 自动更新修改时间
    }
}
```

#### 1.2 日期时间验证
- 自定义`@ValidDateTime`注解
- 验证日期时间字段的有效性
- 支持null值、未来时间、过去时间的控制

```java
@ValidDateTime(allowNull = true, allowFuture = false)
@Column(name = "last_used")
private LocalDateTime lastUsed;
```

#### 1.3 审计服务 (AuditService)
- 统一处理实体的创建和更新操作
- 确保日期时间字段的正确设置
- 提供验证和修复功能

### 2. 数据库层面优化

#### 2.1 数据库约束
- 添加CHECK约束防止无效日期时间值
- 设置默认值确保字段不为空
- 使用`ON UPDATE CURRENT_TIMESTAMP`自动更新

```sql
ALTER TABLE device_tokens 
MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE device_tokens 
ADD CONSTRAINT chk_device_tokens_created_at 
CHECK (created_at IS NOT NULL AND created_at != '0000-00-00 00:00:00');
```

#### 2.2 数据库迁移
- 使用Flyway进行数据库版本管理
- 自动执行约束添加和修复脚本
- 确保数据库结构的一致性

### 3. 应用层面优化

#### 3.1 服务层改进
- 移除手动设置日期时间的代码
- 依赖BaseEntity的自动处理
- 使用审计服务进行验证

```java
// 旧代码 - 手动设置
deviceToken.setCreatedAt(LocalDateTime.now());
deviceToken.setUpdatedAt(LocalDateTime.now());

// 新代码 - 自动处理
// 不需要手动设置，BaseEntity会自动处理
```

#### 3.2 异常处理
- 创建专门的日期时间验证异常
- 提供清晰的错误信息
- 支持优雅的错误处理

## 实施步骤

### 1. 立即生效的改进
- ✅ 创建BaseEntity基础实体类
- ✅ 添加日期时间验证注解和验证器
- ✅ 创建审计服务
- ✅ 更新DeviceToken实体使用新架构
- ✅ 添加数据库约束

### 2. 逐步迁移的改进
- 🔄 更新其他实体类继承BaseEntity
- 🔄 移除所有手动设置日期时间的代码
- 🔄 添加全局异常处理
- 🔄 完善单元测试

### 3. 长期维护的改进
- 📋 定期检查数据库中的无效日期时间值
- 📋 监控应用程序的日期时间相关错误
- 📋 持续优化验证逻辑

## 最佳实践

### 1. 实体设计
- 所有实体都应该继承BaseEntity
- 使用`@ValidDateTime`注解验证日期时间字段
- 避免手动设置createdAt和updatedAt

### 2. 服务层设计
- 使用审计服务处理实体操作
- 依赖JPA的自动审计功能
- 添加适当的验证和错误处理

### 3. 数据库设计
- 为所有日期时间字段设置默认值
- 添加CHECK约束防止无效值
- 使用数据库迁移管理结构变更

## 效果预期

### 1. 问题预防
- 从源头避免无效日期时间值的产生
- 减少运行时错误和异常
- 提高数据质量和一致性

### 2. 开发效率
- 减少手动处理日期时间的代码
- 统一的数据处理逻辑
- 更好的代码可维护性

### 3. 系统稳定性
- 减少应用程序启动失败
- 提高数据库查询的稳定性
- 更好的错误处理和恢复能力

## 监控和维护

### 1. 日志监控
- 监控日期时间验证失败的情况
- 记录数据库修复操作的执行情况
- 跟踪异常和错误的频率

### 2. 定期检查
- 定期检查数据库中的无效日期时间值
- 验证约束的有效性
- 更新和优化验证逻辑

### 3. 性能优化
- 监控日期时间字段的查询性能
- 优化索引和约束
- 考虑分区和归档策略

通过这套完整的优化策略，我们可以从根本上解决日期时间字段的问题，提高系统的稳定性和可维护性。
