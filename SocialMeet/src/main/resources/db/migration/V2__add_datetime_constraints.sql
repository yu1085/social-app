-- 添加日期时间字段约束，防止无效值
-- 这个文件会在应用启动时自动执行

-- 1. 修改device_tokens表，添加默认值和约束
ALTER TABLE device_tokens 
MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 2. 添加检查约束，确保日期时间值有效
-- 注意：MySQL 8.0.16+支持CHECK约束
ALTER TABLE device_tokens 
ADD CONSTRAINT chk_device_tokens_created_at 
CHECK (created_at IS NOT NULL AND created_at != '0000-00-00 00:00:00');

ALTER TABLE device_tokens 
ADD CONSTRAINT chk_device_tokens_updated_at 
CHECK (updated_at IS NOT NULL AND updated_at != '0000-00-00 00:00:00');

-- 3. 如果last_used字段存在，也添加约束
ALTER TABLE device_tokens 
ADD CONSTRAINT chk_device_tokens_last_used 
CHECK (last_used IS NULL OR (last_used IS NOT NULL AND last_used != '0000-00-00 00:00:00'));

-- 4. 为其他主要表添加类似的约束
-- users表
ALTER TABLE users 
MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE users 
ADD CONSTRAINT chk_users_created_at 
CHECK (created_at IS NOT NULL AND created_at != '0000-00-00 00:00:00');

ALTER TABLE users 
ADD CONSTRAINT chk_users_updated_at 
CHECK (updated_at IS NOT NULL AND updated_at != '0000-00-00 00:00:00');

-- dynamics表
ALTER TABLE dynamics 
MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE dynamics 
ADD CONSTRAINT chk_dynamics_created_at 
CHECK (created_at IS NOT NULL AND created_at != '0000-00-00 00:00:00');

ALTER TABLE dynamics 
ADD CONSTRAINT chk_dynamics_updated_at 
CHECK (updated_at IS NOT NULL AND updated_at != '0000-00-00 00:00:00');

-- messages表
ALTER TABLE messages 
MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE messages 
ADD CONSTRAINT chk_messages_created_at 
CHECK (created_at IS NOT NULL AND created_at != '0000-00-00 00:00:00');

ALTER TABLE messages 
ADD CONSTRAINT chk_messages_updated_at 
CHECK (updated_at IS NOT NULL AND updated_at != '0000-00-00 00:00:00');
