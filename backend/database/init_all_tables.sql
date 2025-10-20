-- 完整的数据库初始化脚本
-- 包含所有表结构和初始数据

-- 1. 创建用户表（如果不存在）
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255),
    nickname VARCHAR(50),
    email VARCHAR(100),
    avatar_url VARCHAR(500),
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    birthday DATE,
    constellation VARCHAR(20),
    location VARCHAR(100),
    height INT,
    weight INT,
    income_level VARCHAR(50),
    education VARCHAR(50),
    marital_status VARCHAR(20),
    signature VARCHAR(200),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_vip BOOLEAN NOT NULL DEFAULT FALSE,
    vip_level INT NOT NULL DEFAULT 0,
    vip_expire_at TIMESTAMP NULL,
    wealth_level INT NOT NULL DEFAULT 0,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    is_online BOOLEAN NOT NULL DEFAULT FALSE,
    last_active_at TIMESTAMP NULL,
    jpush_registration_id VARCHAR(50),
    status ENUM('ACTIVE', 'BANNED', 'DELETED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. 创建验证码表
CREATE TABLE IF NOT EXISTS verification_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    code VARCHAR(10) NOT NULL,
    type ENUM('LOGIN', 'REGISTER', 'RESET_PASSWORD') NOT NULL DEFAULT 'LOGIN',
    is_used BOOLEAN NOT NULL DEFAULT FALSE,
    expired_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. 创建用户设备表
CREATE TABLE IF NOT EXISTS user_devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    registration_id VARCHAR(100) NOT NULL,
    device_name VARCHAR(100),
    device_type VARCHAR(20),
    app_version VARCHAR(20),
    os_version VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_active_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. 创建用户设置表
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

-- 5. 创建钱包表
CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_recharge DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_consume DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    transaction_count INT NOT NULL DEFAULT 0,
    last_transaction_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. 创建所有索引
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_verification_codes_phone ON verification_codes(phone);
CREATE INDEX IF NOT EXISTS idx_verification_codes_expired ON verification_codes(expired_at);
CREATE INDEX IF NOT EXISTS idx_user_devices_user_id ON user_devices(user_id);
CREATE INDEX IF NOT EXISTS idx_user_devices_registration_id ON user_devices(registration_id);
CREATE INDEX IF NOT EXISTS idx_user_settings_user_id ON user_settings(user_id);
CREATE INDEX IF NOT EXISTS idx_wallets_user_id ON wallets(user_id);
CREATE INDEX IF NOT EXISTS idx_wallets_balance ON wallets(balance);

-- 7. 插入测试用户数据
INSERT IGNORE INTO users (id, username, phone, nickname, is_verified, is_vip, vip_level, wealth_level, balance, is_online, status) VALUES
(1, 'test_user_1', '13800138000', '测试用户1', TRUE, FALSE, 0, 1, 100.00, TRUE, 'ACTIVE'),
(2, 'test_user_2', '13800138001', '测试用户2', TRUE, TRUE, 1, 2, 500.00, TRUE, 'ACTIVE'),
(3, 'test_user_3', '13800138002', '测试用户3', FALSE, FALSE, 0, 0, 0.00, FALSE, 'ACTIVE');

-- 8. 为测试用户创建默认设置和钱包
INSERT IGNORE INTO user_settings (user_id, voice_call_enabled, video_call_enabled, message_charge_enabled, voice_call_price, video_call_price, message_price)
SELECT id, TRUE, TRUE, FALSE, 0.00, 0.00, 0.00 FROM users WHERE id NOT IN (SELECT user_id FROM user_settings);

INSERT IGNORE INTO wallets (user_id, balance, total_recharge, total_consume, transaction_count)
SELECT id, balance, balance, 0.00, 0 FROM users WHERE id NOT IN (SELECT user_id FROM wallets);

-- 9. 更新钱包余额（从users表的balance字段同步）
UPDATE wallets w 
JOIN users u ON w.user_id = u.id 
SET w.balance = u.balance 
WHERE w.balance != u.balance;
