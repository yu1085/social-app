-- 添加用户设备表
-- 用于支持多设备推送通知

CREATE TABLE IF NOT EXISTS user_devices (
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_registration_id (registration_id),
    INDEX idx_user_active (user_id, is_active),
    INDEX idx_last_active (last_active_at),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- 唯一约束：同一用户的同一设备只能有一个活跃记录
    UNIQUE KEY uk_user_registration (user_id, registration_id)
);

-- 添加注释
ALTER TABLE user_devices COMMENT = '用户设备表，用于管理多设备推送通知';

-- 为现有用户创建设备记录（从旧的jpush_registration_id迁移）
INSERT INTO user_devices (user_id, registration_id, device_name, device_type, is_active, created_at, updated_at)
SELECT 
    id as user_id,
    jpush_registration_id as registration_id,
    CONCAT('设备_', id) as device_name,
    'ANDROID' as device_type,
    TRUE as is_active,
    NOW() as created_at,
    NOW() as updated_at
FROM users 
WHERE jpush_registration_id IS NOT NULL 
  AND jpush_registration_id != '' 
  AND jpush_registration_id != '0';

-- 显示迁移结果
SELECT 
    '迁移完成' as status,
    COUNT(*) as migrated_devices
FROM user_devices;
