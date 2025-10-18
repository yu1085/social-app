# 安全修复说明

## 问题描述

您的账户被禁用是因为项目中包含了硬编码的敏感信息，这些信息可能被自动安全扫描系统检测到。

## 已修复的安全问题

### 1. 硬编码的JWT密钥
**问题**: `application.yml` 中包含硬编码的JWT密钥
**修复**: 使用环境变量 `${JWT_SECRET:default-secret-key-change-in-production}`

### 2. 硬编码的数据库密码
**问题**: 数据库密码直接写在配置文件中
**修复**: 使用环境变量 `${DB_PASSWORD:}`

### 3. 硬编码的测试验证码
**问题**: 验证码 `123456` 硬编码在多个文件中
**修复**: 使用环境变量 `${VERIFICATION_TEST_CODE:123456}`

### 4. 占位符API密钥
**问题**: 阿里云API密钥使用占位符
**修复**: 使用环境变量 `${ALIYUN_ACCESS_KEY_ID:}` 等

## 环境变量配置

创建 `backend/.env` 文件并设置以下环境变量：

```bash
# 数据库配置
DB_USERNAME=root
DB_PASSWORD=your_database_password

# JWT 配置
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production

# 验证码配置
VERIFICATION_TEST_MODE=true
VERIFICATION_TEST_CODE=123456

# 阿里云短信配置
ALIYUN_ACCESS_KEY_ID=your_aliyun_access_key_id
ALIYUN_ACCESS_KEY_SECRET=your_aliyun_access_key_secret
ALIYUN_SIGN_NAME=SocialMeet
ALIYUN_TEMPLATE_CODE=your_template_code
```

## 安全建议

1. **生产环境**：
   - 设置强密码的JWT密钥（至少32位随机字符串）
   - 禁用测试模式
   - 使用真实的阿里云API密钥
   - 定期更换密钥

2. **开发环境**：
   - 可以使用默认值进行测试
   - 确保 `.env` 文件不被提交到版本控制

3. **版本控制**：
   - `.env` 文件已在 `.gitignore` 中
   - 只提交配置模板文件

## 如何启动项目

1. 创建 `backend/.env` 文件
2. 填入必要的环境变量
3. 启动后端：`cd backend && gradlew bootRun`
4. 启动Android应用

## 注意事项

- 所有敏感信息现在都通过环境变量配置
- 配置文件不再包含硬编码的密码或密钥
- 测试验证码仍然可用，但通过环境变量控制
- 生产环境请务必更换所有默认值
