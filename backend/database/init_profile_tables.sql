-- 个人资料相关表初始化脚本
-- 执行顺序：先执行基础表，再执行关联表

-- 1. 创建用户设置表
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

-- 2. 创建钱包表
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

-- 3. 创建索引
CREATE INDEX IF NOT EXISTS idx_user_settings_user_id ON user_settings(user_id);
CREATE INDEX IF NOT EXISTS idx_wallets_user_id ON wallets(user_id);
CREATE INDEX IF NOT EXISTS idx_wallets_balance ON wallets(balance);

-- 4. 为现有用户创建默认设置和钱包
INSERT IGNORE INTO user_settings (user_id, voice_call_enabled, video_call_enabled, message_charge_enabled)
SELECT id, TRUE, TRUE, FALSE FROM users WHERE id NOT IN (SELECT user_id FROM user_settings);

INSERT IGNORE INTO wallets (user_id, balance, total_recharge, total_consume, transaction_count)
SELECT id, 0.00, 0.00, 0.00, 0 FROM users WHERE id NOT IN (SELECT user_id FROM wallets);

-- 5. 更新现有用户的钱包余额（从users表的balance字段同步）
UPDATE wallets w 
JOIN users u ON w.user_id = u.id 
SET w.balance = u.balance 
WHERE w.balance = 0.00 AND u.balance > 0.00;
