# 黑名单管理系统实现文档

## 概述

完整实现了用户黑名单管理功能,包括添加/移除黑名单、批量操作、列表过滤等功能。

## 后端实现

### 1. Service 层新增方法 (UserRelationshipService.java)

#### 获取黑名单列表
```java
public List<UserDTO> getBlacklistUsers(Long userId)
```
- 返回当前用户的所有黑名单用户列表
- 包含完整的用户信息(UserDTO)

#### 获取黑名单用户ID列表
```java
public List<Long> getBlacklistedUserIds(Long userId)
```
- 返回当前用户的所有黑名单用户ID列表
- 用于快速过滤其他列表中的黑名单用户

#### 批量移出黑名单
```java
public int batchRemoveFromBlacklist(Long userId, List<Long> targetUserIds)
```
- 支持批量移出多个黑名单用户
- 返回成功移出的用户数量

### 2. Controller 层新增API接口 (UserController.java)

#### GET /api/users/blacklist
**功能**: 获取当前用户的黑名单列表

**请求头**:
```
Authorization: Bearer {token}
```

**响应**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 23820512,
      "username": "video_caller",
      "nickname": "video_caller",
      "isOnline": true,
      ...
    }
  ]
}
```

#### DELETE /api/users/blacklist/batch
**功能**: 批量移出黑名单

**请求头**:
```
Authorization: Bearer {token}
Content-Type: application/json
```

**请求体**:
```json
[23820512, 23820513, 23820516]
```

**响应**:
```json
{
  "success": true,
  "message": "成功移出3个用户"
}
```

### 3. 黑名单过滤集成

所有用户列表API已集成黑名单过滤功能:

#### GET /api/users/recommended
- **修改**: 自动过滤黑名单用户
- **日志**: 显示过滤的黑名单用户数量

#### GET /api/users/acquaintances
- **修改**: 知友列表自动过滤黑名单用户
- **行为**: 如果过滤后数量不足,会自动请求更多用户补充

#### GET /api/users/likes
- **修改**: 喜欢列表自动过滤黑名单用户
- **行为**: 确保返回的列表中不包含任何黑名单用户

#### GET /api/users/intimate
- **修改**: 亲密列表自动过滤黑名单用户
- **行为**: 保证用户看不到已拉黑的用户

## 实现细节

### 过滤逻辑

```java
// 获取黑名单用户ID列表
List<Long> blacklistedUserIds = currentUserId != null
    ? userRelationshipService.getBlacklistedUserIds(currentUserId)
    : List.of();

// 过滤黑名单用户
List<UserDTO> filteredUsers = users.stream()
        .filter(user -> !blacklistedUserIds.contains(user.getId()))
        .limit(size)
        .collect(Collectors.toList());
```

### 数据库表结构

`user_relationships` 表支持 `BLACKLIST` 关系类型:

```sql
relationship_type VARCHAR(50) NOT NULL
COMMENT '关系类型: FRIEND(知友), LIKE(喜欢), INTIMATE(亲密), BLACKLIST(黑名单), SUBSCRIBE(订阅)'
```

## API测试

### 测试脚本
- `test_subscribe_blacklist_api.ps1` - 基础黑名单和订阅功能测试
- `test_blacklist_full_features.ps1` - 完整黑名单管理功能测试

### 测试流程
1. 获取当前黑名单列表
2. 添加多个用户到黑名单
3. 验证黑名单列表已更新
4. 测试推荐用户列表过滤
5. 测试知友列表过滤
6. 批量移出黑名单
7. 验证移出成功

## Android 端实现建议

### 1. API Service 接口

在 `ApiService.java` 中添加:

```java
// 获取黑名单列表
@GET("users/blacklist")
Call<ApiResponse<List<UserDTO>>> getBlacklistUsers(
    @Header("Authorization") String authHeader
);

// 批量移出黑名单
@HTTP(method = "DELETE", path = "users/blacklist/batch", hasBody = true)
Call<ApiResponse<String>> batchRemoveFromBlacklist(
    @Header("Authorization") String authHeader,
    @Body List<Long> userIds
);
```

### 2. 黑名单管理Activity

创建 `BlacklistActivity.java`:

**功能**:
- 显示黑名单用户列表
- 支持多选模式
- 批量移出黑名单
- 下拉刷新

**布局**:
```xml
<LinearLayout>
    <androidx.appcompat.widget.Toolbar />
    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
        <androidx.recyclerview.widget.RecyclerView />
    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
    <Button android:id="@+id/btnBatchRemove" />
</LinearLayout>
```

### 3. 用户列表适配器

在 `UserListAdapter.java` 中添加:
- 多选模式切换
- 选中状态显示
- 批量操作回调

### 4. 集成到个人中心

在"我的"页面添加"黑名单管理"入口:

```java
blacklistManagement.setOnClickListener(v -> {
    Intent intent = new Intent(this, BlacklistActivity.class);
    startActivity(intent);
});
```

## 功能特性

### ✅ 已实现
- [x] 添加用户到黑名单 (POST /api/users/{id}/blacklist)
- [x] 移出黑名单 (DELETE /api/users/{id}/blacklist)
- [x] 检查黑名单状态 (GET /api/users/{id}/is-blacklisted)
- [x] 获取黑名单列表 (GET /api/users/blacklist)
- [x] 批量移出黑名单 (DELETE /api/users/blacklist/batch)
- [x] 推荐列表自动过滤黑名单用户
- [x] 知友列表自动过滤黑名单用户
- [x] 喜欢列表自动过滤黑名单用户
- [x] 亲密列表自动过滤黑名单用户

### 🔄 待实现 (Android端)
- [ ] 黑名单管理页面UI
- [ ] 黑名单用户列表展示
- [ ] 多选模式和批量移出
- [ ] 下拉刷新功能
- [ ] 与个人中心集成

### 💡 未来优化
- [ ] 会话列表过滤黑名单用户
- [ ] 搜索结果过滤黑名单用户
- [ ] 黑名单用户无法发起通话
- [ ] 黑名单用户消息自动屏蔽
- [ ] 添加黑名单原因标签
- [ ] 黑名单统计和分析

## 技术亮点

1. **智能过滤**: 所有用户列表API自动过滤黑名单,无需客户端额外处理
2. **性能优化**: 使用`getBlacklistedUserIds()`只获取ID列表,减少网络传输
3. **批量操作**: 支持批量移出,提升用户体验
4. **日志完善**: 详细记录黑名单操作和过滤日志,便于调试
5. **向后兼容**: 过滤逻辑不影响未登录用户访问

## 数据库影响

### 表结构修改
- `user_relationships.relationship_type`: 从 ENUM 改为 VARCHAR(50)
- 新增支持: `BLACKLIST`, `SUBSCRIBE` 类型

### 性能考虑
- 黑名单查询使用索引: `idx_user_id`, `idx_relationship_type`
- 批量操作在事务中执行,保证一致性

## 安全性

1. **权限验证**: 所有API都需要JWT认证
2. **自我保护**: 不能将自己加入黑名单
3. **数据隔离**: 每个用户只能管理自己的黑名单
4. **重复处理**: 重复添加/移除不会报错,幂等操作

## 错误处理

所有API都有完善的错误处理:
- Token无效/缺失
- 用户不存在
- 数据库操作失败
- 参数验证失败

## 版本信息

- 实现日期: 2025-10-22
- 后端版本: Spring Boot 3.3.5
- 数据库: MySQL 8.0
- 涉及文件:
  - `UserRelationshipService.java`
  - `UserController.java`
  - `create_user_relationships_table.sql`
  - `alter_user_relationships_table.sql`

## 相关文档

- `USER_RELATIONSHIP_BUG_FIX.md` - 关系类型字段修复文档
- `PROFILE_API_DOCUMENTATION.md` - 用户资料API文档
- `USER_RELATIONSHIPS_IMPLEMENTATION.md` - 用户关系系统实现文档
