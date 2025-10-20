-- 创建用户设备表
CREATE TABLE IF NOT EXISTS user_devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    registration_id VARCHAR(100) NOT NULL,
    device_name VARCHAR(100),
    device_type VARCHAR(20) DEFAULT 'ANDROID',
    app_version VARCHAR(20),
    os_version VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_active_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_registration_id (registration_id),
    INDEX idx_user_active (user_id, is_active),
    INDEX idx_last_active (last_active_at),
    
    -- 唯一约束：同一用户不能有重复的registration_id
    UNIQUE KEY uk_user_registration (user_id, registration_id),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
