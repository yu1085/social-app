-- 创建缺失的数据库表
-- 用于修复SocialMeet项目的数据库问题

USE socialmeet;

-- 创建手机验证码表
CREATE TABLE IF NOT EXISTS phone_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20) NOT NULL,
    verification_code VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    expires_at DATETIME NOT NULL,
    verified_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_phone_number (phone_number),
    INDEX idx_phone_code (phone_number, verification_code),
    INDEX idx_expires_at (expires_at)
);

-- 创建通话设置表
CREATE TABLE IF NOT EXISTS call_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    voice_call_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    video_call_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    voice_call_price INT NOT NULL DEFAULT 100, -- 语音通话价格（分/分钟）
    video_call_price INT NOT NULL DEFAULT 200, -- 视频通话价格（分/分钟）
    auto_accept_calls BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建消息表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT,
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT', -- TEXT, IMAGE, VIDEO, VOICE, FILE
    file_url VARCHAR(500),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sender (sender_id),
    INDEX idx_receiver (receiver_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建通话记录表
CREATE TABLE IF NOT EXISTS call_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    caller_id BIGINT NOT NULL,
    callee_id BIGINT NOT NULL,
    call_type VARCHAR(20) NOT NULL, -- VOICE, VIDEO
    duration INT NOT NULL DEFAULT 0, -- 通话时长（秒）
    cost INT NOT NULL DEFAULT 0, -- 通话费用（分）
    status VARCHAR(20) NOT NULL, -- CALLING, CONNECTED, ENDED, MISSED, REJECTED
    started_at DATETIME NOT NULL,
    ended_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_caller (caller_id),
    INDEX idx_callee (callee_id),
    INDEX idx_started_at (started_at),
    FOREIGN KEY (caller_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (callee_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建动态表
CREATE TABLE IF NOT EXISTS dynamics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    image_urls JSON, -- 图片URL数组
    video_url VARCHAR(500),
    location VARCHAR(100),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    share_count INT NOT NULL DEFAULT 0,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_location (latitude, longitude),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建钱包表
CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance INT NOT NULL DEFAULT 0, -- 余额（分）
    frozen_balance INT NOT NULL DEFAULT 0, -- 冻结余额（分）
    total_recharge INT NOT NULL DEFAULT 0, -- 总充值（分）
    total_consumption INT NOT NULL DEFAULT 0, -- 总消费（分）
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建交易记录表
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL, -- RECHARGE, CONSUMPTION, REFUND, WITHDRAW
    amount INT NOT NULL, -- 交易金额（分）
    balance_after INT NOT NULL, -- 交易后余额（分）
    description VARCHAR(200),
    related_id BIGINT, -- 关联ID（如通话记录ID、订单ID等）
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS', -- SUCCESS, PENDING, FAILED
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_type (transaction_type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建VIP会员表
CREATE TABLE IF NOT EXISTS vip_memberships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vip_level INT NOT NULL DEFAULT 0, -- VIP等级
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_end_date (end_date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建礼物表
CREATE TABLE IF NOT EXISTS gifts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price INT NOT NULL, -- 价格（分）
    image_url VARCHAR(500),
    category VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_is_active (is_active)
);

-- 创建礼物记录表
CREATE TABLE IF NOT EXISTS gift_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    gift_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    total_cost INT NOT NULL, -- 总费用（分）
    message VARCHAR(200),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sender (sender_id),
    INDEX idx_receiver (receiver_id),
    INDEX idx_gift (gift_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (gift_id) REFERENCES gifts(id) ON DELETE CASCADE
);

-- 插入一些基础数据
INSERT IGNORE INTO gifts (name, description, price, category) VALUES
('玫瑰花', '表达爱意的经典礼物', 100, 'love'),
('巧克力', '甜蜜的惊喜', 200, 'sweet'),
('钻石', '珍贵的象征', 1000, 'luxury'),
('爱心', '简单而真诚的祝福', 50, 'love'),
('蛋糕', '庆祝的甜蜜', 300, 'celebration');

-- 为现有用户创建默认钱包
INSERT IGNORE INTO wallets (user_id, balance, frozen_balance, total_recharge, total_consumption)
SELECT id, 10000, 0, 10000, 0 FROM users WHERE id NOT IN (SELECT user_id FROM wallets);

-- 为现有用户创建默认通话设置
INSERT IGNORE INTO call_settings (user_id, voice_call_enabled, video_call_enabled, voice_call_price, video_call_price)
SELECT id, TRUE, TRUE, 100, 200 FROM users WHERE id NOT IN (SELECT user_id FROM call_settings);

COMMIT;
