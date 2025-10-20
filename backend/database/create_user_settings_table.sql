-- 创建用户设置表
CREATE TABLE IF NOT EXISTS user_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    voice_call_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    video_call_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    message_charge_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    voice_call_price DECIMAL(10,2) DEFAULT 0.00,
    video_call_price DECIMAL(10,2) DEFAULT 0.00,
    message_price DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_user_settings_user_id ON user_settings(user_id);
