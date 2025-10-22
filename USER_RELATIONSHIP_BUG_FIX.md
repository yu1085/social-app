# 用户关系功能Bug修复文档

## 问题描述

在 Android 应用的用户详情页面,当用户尝试订阅其他用户或将其加入黑名单时,后端返回错误:

```
"Data truncated for column 'relationship_type' at row 1"
```

### 错误日志
```
订阅失败: could not execute statement [Data truncated for column 'relationship_type' at row 1]
加入黑名单失败: could not execute statement [Data truncated for column 'relationship_type' at row 1]
```

## 根本原因

数据库表 `user_relationships` 的 `relationship_type` 列被定义为 **ENUM** 类型,仅包含三个值:

```sql
relationship_type enum('FRIEND','LIKE','INTIMATE')
```

但是 Java 实体类 `UserRelationship.java` 中的枚举定义包含了 **5个值**:

```java
public enum RelationshipType {
    FRIEND,     // 知友
    LIKE,       // 喜欢
    INTIMATE,   // 亲密
    BLACKLIST,  // 黑名单
    SUBSCRIBE   // 订阅
}
```

当后端尝试插入 `BLACKLIST` 或 `SUBSCRIBE` 值时,由于数据库 ENUM 类型不包含这些值,导致"数据被截断"错误。

## 修复方案

### 1. 修改数据库表结构

将 `relationship_type` 列从 ENUM 类型改为 VARCHAR(50) 类型,以支持所有枚举值:

```sql
ALTER TABLE user_relationships
MODIFY COLUMN relationship_type VARCHAR(50) NOT NULL
COMMENT '关系类型: FRIEND(知友), LIKE(喜欢), INTIMATE(亲密), BLACKLIST(黑名单), SUBSCRIBE(订阅)';
```

### 2. 更新表创建脚本

修改 `backend/database/create_user_relationships_table.sql`,确保将来创建表时使用正确的列类型:

**之前:**
```sql
relationship_type VARCHAR(20) NOT NULL COMMENT '关系类型: FRIEND(知友), LIKE(喜欢), INTIMATE(亲密)'
```

**之后:**
```sql
relationship_type VARCHAR(50) NOT NULL COMMENT '关系类型: FRIEND(知友), LIKE(喜欢), INTIMATE(亲密), BLACKLIST(黑名单), SUBSCRIBE(订阅)',
remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
is_subscribed BOOLEAN DEFAULT FALSE COMMENT '是否订阅状态通知',
```

## 修复步骤

### 1. 创建数据库修复脚本

文件: `backend/database/alter_user_relationships_table.sql`

```sql
USE socialmeet;

-- 确保 relationship_type 列长度足够
ALTER TABLE user_relationships
MODIFY COLUMN relationship_type VARCHAR(50) NOT NULL
COMMENT '关系类型: FRIEND(知友), LIKE(喜欢), INTIMATE(亲密), BLACKLIST(黑名单), SUBSCRIBE(订阅)';
```

### 2. 执行修复脚本

```powershell
Get-Content backend/database/alter_user_relationships_table.sql |
  & 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe' -u root -proot
```

### 3. 验证修复

执行测试脚本 `test_subscribe_blacklist_api.ps1`:

```powershell
powershell.exe -ExecutionPolicy Bypass -File test_subscribe_blacklist_api.ps1
```

## 测试结果

所有测试用例均通过:

### 订阅功能测试
- ✅ 订阅用户成功
- ✅ 检查订阅状态 (返回 true)
- ✅ 取消订阅成功
- ✅ 再次检查订阅状态 (返回 false)

### 黑名单功能测试
- ✅ 加入黑名单成功
- ✅ 检查黑名单状态 (返回 true)
- ✅ 移出黑名单成功
- ✅ 再次检查黑名单状态 (返回 false)

## 数据库变更详情

### 修改前表结构
```
relationship_type enum('FRIEND','LIKE','INTIMATE') NO NULL
```

### 修改后表结构
```
relationship_type varchar(50) NO NULL
remark            varchar(255) YES NULL
is_subscribed     bit(1)       YES NULL
```

## 影响范围

### 后端 API
- `POST /api/users/{targetUserId}/subscribe` - 订阅用户
- `DELETE /api/users/{targetUserId}/subscribe` - 取消订阅
- `GET /api/users/{targetUserId}/is-subscribed` - 检查订阅状态
- `POST /api/users/{targetUserId}/blacklist` - 加入黑名单
- `DELETE /api/users/{targetUserId}/blacklist` - 移出黑名单
- `GET /api/users/{targetUserId}/is-blacklisted` - 检查黑名单状态

### Android 应用
- `UserDetailActivity` - 用户详情页的订阅和黑名单按钮现在可以正常工作

## 相关文件

### 数据库脚本
- `backend/database/create_user_relationships_table.sql` - 表创建脚本 (已更新)
- `backend/database/alter_user_relationships_table.sql` - 表修复脚本 (新建)

### 后端代码
- `backend/src/main/java/com/socialmeet/backend/entity/UserRelationship.java` - 实体类
- `backend/src/main/java/com/socialmeet/backend/service/UserRelationshipService.java` - 关系服务
- `backend/src/main/java/com/socialmeet/backend/controller/UserController.java` - 控制器

### 测试脚本
- `test_subscribe_blacklist_api.ps1` - 订阅和黑名单 API 测试脚本

## 经验教训

1. **数据库与代码同步**: 确保数据库表结构与 Java 实体类定义保持一致
2. **避免使用 ENUM**: 在关系型数据库中,VARCHAR 比 ENUM 更灵活,尤其是当枚举值可能扩展时
3. **完整的表结构**: 确保数据库表包含实体类中定义的所有字段 (`remark`, `is_subscribed`)
4. **版本控制**: 数据库表结构变更应该有对应的迁移脚本

## 版本信息

- 修复日期: 2025-10-22
- 数据库: MySQL 8.0
- 后端框架: Spring Boot 3.3.5
- Android SDK: 34

## 总结

问题的根本原因是数据库表结构与代码实体类不一致。通过将 `relationship_type` 列从 ENUM 类型改为 VARCHAR(50) 类型,解决了订阅和黑名单功能的插入错误。所有相关功能现已正常工作。
