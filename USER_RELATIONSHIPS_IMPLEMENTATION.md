# 用户关系功能实现文档

## 功能概述

完整实现了知友、喜欢、亲密三大用户关系功能，包括后端API、前端UI和数据库设计。

## 实现内容

### 1. 数据库设计

**表名**: `user_relationships`

**字段说明**:
- `id`: 关系ID (主键，自增)
- `user_id`: 用户ID (发起关系的用户)
- `target_user_id`: 目标用户ID (关系对象)
- `relationship_type`: 关系类型 (FRIEND/LIKE/INTIMATE)
- `intimacy_score`: 亲密度分数 (用于亲密关系的排序)
- `created_at`: 创建时间
- `updated_at`: 更新时间

**索引**:
- 唯一索引: `uk_user_target_type` (防止重复关系)
- 普通索引: `idx_user_id`, `idx_target_user_id`, `idx_relationship_type`

**建表SQL**: `backend/database/create_user_relationships_table.sql`

---

### 2. 后端实现

#### 2.1 实体类 (Entity)

**文件**: `backend/src/main/java/com/socialmeet/backend/entity/UserRelationship.java`

- 使用JPA注解映射数据库表
- 定义了三种关系类型枚举: `FRIEND`, `LIKE`, `INTIMATE`
- 自动管理创建时间和更新时间

#### 2.2 Repository层

**文件**: `backend/src/main/java/com/socialmeet/backend/repository/UserRelationshipRepository.java`

**提供的方法**:
- `findByUserIdAndType()` - 查询用户的某种关系列表
- `findByUserId()` - 查询用户的所有关系
- `findByUserIdAndTargetUserIdAndRelationshipType()` - 查询特定关系
- `findWhoAddedMeAsFriend()` - 查询谁加我为好友
- `findWhoLikesMe()` - 查询谁喜欢我

#### 2.3 Service层

**文件**: `backend/src/main/java/com/socialmeet/backend/service/UserRelationshipService.java`

**提供的功能**:
- ✅ **知友管理**:
  - `addFriend()` - 添加知友
  - `removeFriend()` - 删除知友
  - `isFriend()` - 检查是否是知友
  - `getFriendsList()` - 获取知友列表
  - `getWhoAddedMeAsFriend()` - 获取谁加我为知友

- ✅ **喜欢管理**:
  - `addLike()` - 添加喜欢
  - `removeLike()` - 取消喜欢
  - `isLiked()` - 检查是否已喜欢
  - `getLikesList()` - 获取喜欢列表
  - `getWhoLikesMe()` - 获取谁喜欢我

- ✅ **亲密管理**:
  - `getIntimateList()` - 获取亲密列表
  - `updateIntimacyScore()` - 更新亲密度分数

#### 2.4 Controller层

**文件**: `backend/src/main/java/com/socialmeet/backend/controller/UserController.java`

**新增的API端点**:

| HTTP方法 | 路径 | 说明 |
|---------|------|------|
| GET | `/api/users/acquaintances` | 获取知友列表 |
| GET | `/api/users/likes` | 获取喜欢列表 |
| GET | `/api/users/intimate` | 获取亲密列表 |
| POST | `/api/users/{targetUserId}/friend` | 添加知友 |
| DELETE | `/api/users/{targetUserId}/friend` | 删除知友 |
| POST | `/api/users/{targetUserId}/like` | 添加喜欢 |
| DELETE | `/api/users/{targetUserId}/like` | 取消喜欢 |
| GET | `/api/users/{targetUserId}/is-liked` | 检查是否已喜欢 |
| GET | `/api/users/{targetUserId}/is-friend` | 检查是否是知友 |

---

### 3. Android前端实现

#### 3.1 API接口定义

**文件**: `app/src/main/java/com/example/myapplication/network/ApiService.java`

添加了所有关系管理的Retrofit接口方法，与后端API一一对应。

#### 3.2 用户详情页喜欢功能

**文件**: `app/src/main/java/com/example/myapplication/UserDetailActivity.java`

**实现的功能**:
1. ✅ **自动加载喜欢状态** - 进入页面时自动检查是否已喜欢该用户
2. ✅ **切换喜欢状态** - 点击喜欢按钮可添加/取消喜欢
3. ✅ **防止喜欢自己** - 自动检测并阻止用户喜欢自己
4. ✅ **UI反馈** - 显示Toast提示操作结果
5. ✅ **异步处理** - 使用AsyncTask在后台线程处理网络请求

**关键方法**:
- `loadLikeStatus()` - 加载喜欢状态
- `toggleLike()` - 切换喜欢状态
- `updateLikeButtonUI()` - 更新按钮UI (可扩展)

#### 3.3 关系列表页面

已有的Activity页面:
- **AcquaintancesActivity** - 知友列表页
- **LikesActivity** - 喜欢列表页
- **IntimacyActivity** - 亲密列表页

这些页面已经集成了新的API，会自动从关系表获取数据。

---

### 4. 功能特性

#### 4.1 智能降级策略

所有列表API都实现了降级逻辑:
- 如果用户有真实的关系数据，返回关系表数据
- 如果用户没有关系数据，返回随机推荐用户列表
- 确保列表始终有内容显示

#### 4.2 关系唯一性保证

- 数据库层面通过唯一索引防止重复关系
- Service层面在添加前检查是否已存在
- 防止数据冗余和重复操作

#### 4.3 防御性编程

- 不能添加自己为知友
- 不能喜欢自己
- 所有操作都有完善的错误处理
- 日志记录完整，便于调试

---

## 使用说明

### 步骤1: 初始化数据库

```bash
# 使用MySQL命令行
mysql -u root -proot < init_relationships_and_test.sql

# 或者手动执行
mysql -u root -proot socialmeet < backend/database/create_user_relationships_table.sql
```

### 步骤2: 启动后端服务

```bash
cd backend
../gradlew bootRun
```

或使用现有的启动脚本:
```bash
.\start_backend_with_profile.ps1
```

### 步骤3: 测试API

使用提供的测试脚本:
```bash
.\test_relationships_api.ps1
```

**注意**: 需要先获取真实的JWT token并替换脚本中的token值。

### 步骤4: 编译Android APK

```bash
.\gradlew :app:assembleDebug
```

### 步骤5: 安装并测试

1. 安装APK到模拟器/真机
2. 登录账号
3. 访问用户详情页
4. 点击喜欢按钮测试功能
5. 进入消息页-关系标签查看知友/喜欢/亲密列表

---

## API测试示例

### 添加喜欢

```bash
curl -X POST http://localhost:8080/api/users/23820512/like \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json"
```

**响应**:
```json
{
  "success": true,
  "message": "添加喜欢成功",
  "data": null
}
```

### 检查是否已喜欢

```bash
curl -X GET http://localhost:8080/api/users/23820512/is-liked \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": true
}
```

### 获取喜欢列表

```bash
curl -X GET "http://localhost:8080/api/users/likes?size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**响应**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": [
    {
      "id": 23820512,
      "username": "user512",
      "nickname": "小雅",
      "gender": "FEMALE",
      "location": "北京",
      "signature": "热爱生活"
    }
  ]
}
```

---

## 文件清单

### 后端文件
- ✅ `backend/database/create_user_relationships_table.sql` - 建表SQL
- ✅ `backend/src/main/java/com/socialmeet/backend/entity/UserRelationship.java` - 实体类
- ✅ `backend/src/main/java/com/socialmeet/backend/repository/UserRelationshipRepository.java` - Repository
- ✅ `backend/src/main/java/com/socialmeet/backend/service/UserRelationshipService.java` - Service
- ✅ `backend/src/main/java/com/socialmeet/backend/controller/UserController.java` - Controller (已更新)

### 前端文件
- ✅ `app/src/main/java/com/example/myapplication/network/ApiService.java` - API接口 (已更新)
- ✅ `app/src/main/java/com/example/myapplication/UserDetailActivity.java` - 用户详情页 (已更新)
- ✅ `app/src/main/java/com/example/myapplication/AcquaintancesActivity.java` - 知友列表页 (已存在)
- ✅ `app/src/main/java/com/example/myapplication/LikesActivity.java` - 喜欢列表页 (已存在)
- ✅ `app/src/main/java/com/example/myapplication/IntimacyActivity.java` - 亲密列表页 (已存在)

### 测试文件
- ✅ `test_relationships_api.ps1` - API测试脚本
- ✅ `init_relationships_and_test.sql` - 数据库初始化脚本

---

## 下一步优化建议

### 1. UI优化
- 为喜欢按钮添加视觉反馈 (红色心形 = 已喜欢，灰色 = 未喜欢)
- 添加动画效果提升用户体验
- 在列表中显示关系状态标识

### 2. 功能扩展
- 实现双向知友系统 (互相添加才成为知友)
- 基于聊天频率自动计算亲密度
- 添加关系通知功能
- 实现关系搜索和筛选

### 3. 性能优化
- 添加Redis缓存关系数据
- 实现批量查询接口
- 添加分页优化

### 4. 数据统计
- 统计用户的知友数、被喜欢数
- 生成亲密度排行榜
- 提供关系变化历史

---

## 常见问题

### Q1: 数据库表创建失败？
**A**: 确保MySQL服务正在运行，并且数据库 `socialmeet` 已创建。

### Q2: API返回401未授权？
**A**: 确保在请求头中包含有效的JWT token: `Authorization: Bearer YOUR_TOKEN`

### Q3: 喜欢按钮点击无反应？
**A**: 检查以下几点:
- 确保已登录
- 确保后端服务器正在运行
- 查看Logcat日志排查错误
- 确认网络连接正常

### Q4: 列表为空？
**A**:
- 如果是首次使用，关系表为空是正常的
- 系统会自动降级显示推荐用户列表
- 可以手动添加关系后再查看

---

## 技术栈

### 后端
- Spring Boot 3.3.5
- Spring Data JPA
- MySQL 8.0
- JWT认证

### 前端
- Android (Kotlin/Java)
- Retrofit 2
- AsyncTask
- Jetpack Compose (部分UI)

---

## 版本历史

**v1.0.0** (2025-01-XX)
- ✅ 初始版本发布
- ✅ 实现知友、喜欢、亲密三大功能
- ✅ 完整的前后端集成
- ✅ 提供测试脚本和文档

---

## 作者
Claude Code Assistant

## 许可证
内部项目，仅供学习使用
