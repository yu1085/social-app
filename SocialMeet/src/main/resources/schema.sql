-- 数据库表结构定义

-- 钱包表
CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    frozen_amount DECIMAL(10,2) DEFAULT 0.00,
    currency VARCHAR(10) DEFAULT 'CNY',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_user_wallet (user_id)
);

-- 交易记录表
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'RECHARGE, WITHDRAW, GIFT, VIP, CONSUME, EARN',
    amount DECIMAL(10,2) NOT NULL,
    balance_after DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    status VARCHAR(20) DEFAULT 'SUCCESS' COMMENT 'PENDING, SUCCESS, FAILED',
    related_id BIGINT COMMENT '关联ID（如礼物ID、VIP订阅ID等）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_transactions (user_id, created_at),
    INDEX idx_transaction_type (type, created_at)
);

-- VIP等级表
CREATE TABLE IF NOT EXISTS vip_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    level INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    duration INTEGER NOT NULL COMMENT '有效期（天数）',
    benefits TEXT COMMENT '权益描述',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_vip_level (level)
);

-- VIP订阅表
CREATE TABLE IF NOT EXISTS vip_subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vip_level_id BIGINT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE, EXPIRED, CANCELLED',
    amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (vip_level_id) REFERENCES vip_levels(id),
    INDEX idx_user_vip (user_id, status),
    INDEX idx_vip_expiry (end_date, status)
);

-- 礼物表
CREATE TABLE IF NOT EXISTS gifts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_gift_category (category, is_active)
);

-- 礼物记录表
CREATE TABLE IF NOT EXISTS gift_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    gift_id BIGINT NOT NULL,
    quantity INTEGER DEFAULT 1,
    total_amount DECIMAL(10,2) NOT NULL,
    message VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id),
    FOREIGN KEY (gift_id) REFERENCES gifts(id),
    INDEX idx_sender_gifts (sender_id, created_at),
    INDEX idx_receiver_gifts (receiver_id, created_at)
);

-- 守护关系表
CREATE TABLE IF NOT EXISTS guard_relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    guardian_id BIGINT NOT NULL COMMENT '守护者ID',
    protected_id BIGINT NOT NULL COMMENT '被守护者ID',
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE, EXPIRED, CANCELLED',
    total_contribution DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (guardian_id) REFERENCES users(id),
    FOREIGN KEY (protected_id) REFERENCES users(id),
    UNIQUE KEY uk_guard_relationship (guardian_id, protected_id),
    INDEX idx_guardian (guardian_id, status),
    INDEX idx_protected (protected_id, status)
);

-- 卡券表
CREATE TABLE IF NOT EXISTS coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL COMMENT 'DISCOUNT, CASH, VIP',
    value DECIMAL(10,2) NOT NULL,
    min_amount DECIMAL(10,2) DEFAULT 0.00,
    max_discount DECIMAL(10,2),
    valid_days INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 用户卡券表
CREATE TABLE IF NOT EXISTS user_coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'UNUSED' COMMENT 'UNUSED, USED, EXPIRED',
    used_at TIMESTAMP NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (coupon_id) REFERENCES coupons(id),
    INDEX idx_user_coupons (user_id, status),
    INDEX idx_coupon_expiry (expires_at, status)
);

-- 财富等级表
CREATE TABLE IF NOT EXISTS wealth_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    level INTEGER NOT NULL,
    min_contribution DECIMAL(10,2) NOT NULL,
    max_contribution DECIMAL(10,2),
    benefits TEXT,
    icon_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_wealth_level (level)
);

-- 用户浏览记录表
CREATE TABLE IF NOT EXISTS user_views (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    viewer_id BIGINT NOT NULL,
    viewed_id BIGINT NOT NULL,
    view_type VARCHAR(50) DEFAULT 'PROFILE' COMMENT 'PROFILE, POST, MESSAGE',
    related_id BIGINT COMMENT '相关ID（如动态ID、消息ID等）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (viewer_id) REFERENCES users(id),
    FOREIGN KEY (viewed_id) REFERENCES users(id),
    INDEX idx_viewer (viewer_id, created_at),
    INDEX idx_viewed (viewed_id, created_at),
    INDEX idx_view_type (view_type, created_at)
);

-- 关注关系表
CREATE TABLE IF NOT EXISTS follow_relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (follower_id) REFERENCES users(id),
    FOREIGN KEY (following_id) REFERENCES users(id),
    UNIQUE KEY uk_follow_relationship (follower_id, following_id),
    INDEX idx_follower (follower_id),
    INDEX idx_following (following_id)
);

-- 用户喜欢表
CREATE TABLE IF NOT EXISTS user_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    liked_user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (liked_user_id) REFERENCES users(id),
    UNIQUE KEY uk_user_like (user_id, liked_user_id),
    INDEX idx_user_likes (user_id),
    INDEX idx_liked_user (liked_user_id)
);

-- 亲密关系表
CREATE TABLE IF NOT EXISTS intimacy_relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    intimacy_score INTEGER DEFAULT 0,
    level VARCHAR(20) DEFAULT 'STRANGER' COMMENT 'STRANGER, ACQUAINTANCE, FRIEND, CLOSE_FRIEND',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user1_id) REFERENCES users(id),
    FOREIGN KEY (user2_id) REFERENCES users(id),
    UNIQUE KEY uk_intimacy_relationship (user1_id, user2_id),
    INDEX idx_user1_intimacy (user1_id),
    INDEX idx_user2_intimacy (user2_id)
);

-- 支付订单表
CREATE TABLE IF NOT EXISTS payment_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_no VARCHAR(64) NOT NULL,
    type VARCHAR(50) NOT NULL COMMENT 'RECHARGE, VIP, GIFT',
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING, SUCCESS, FAILED, CANCELLED',
    payment_method VARCHAR(20) COMMENT 'ALIPAY, WECHAT',
    payment_no VARCHAR(100) COMMENT '第三方支付单号',
    callback_data TEXT COMMENT '支付回调数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_user_orders (user_id, created_at),
    INDEX idx_order_status (status, created_at)
);

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL,
    config_value TEXT,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_config_key (config_key)
);

-- 插入初始数据
INSERT INTO vip_levels (name, level, price, duration, benefits) VALUES
('普通会员', 0, 0.00, 0, '基础功能'),
('VIP会员', 1, 99.00, 30, '无限制聊天、查看访客、优先推荐'),
('SVIP会员', 2, 334.00, 365, '所有VIP功能、专属客服、高级筛选');

INSERT INTO wealth_levels (name, level, min_contribution, max_contribution, benefits) VALUES
('青铜', 1, 0.00, 99.99, '基础财富等级'),
('白银', 2, 100.00, 499.99, '中等财富等级'),
('黄金', 3, 500.00, 999.99, '高级财富等级'),
('钻石', 4, 1000.00, 4999.99, '顶级财富等级'),
('王者', 5, 5000.00, NULL, '至尊财富等级');

INSERT INTO gifts (name, description, price, category) VALUES
('玫瑰花', '表达爱意的经典礼物', 1.00, 'LOVE'),
('巧克力', '甜蜜的象征', 5.00, 'LOVE'),
('钻戒', '永恒的承诺', 99.00, 'LOVE'),
('跑车', '豪华座驾', 999.00, 'LUXURY'),
('城堡', '梦幻家园', 9999.00, 'LUXURY');

INSERT INTO coupons (name, description, type, value, min_amount, valid_days) VALUES
('新用户优惠券', '新用户专享', 'DISCOUNT', 10.00, 50.00, 30),
('充值优惠券', '充值满减', 'CASH', 20.00, 100.00, 7),
('VIP体验券', 'VIP功能体验', 'VIP', 0.00, 0.00, 3);

INSERT INTO system_configs (config_key, config_value, description) VALUES
('app_version', '1.0.0', '应用版本号'),
('min_recharge_amount', '10.00', '最小充值金额'),
('max_recharge_amount', '10000.00', '最大充值金额'),
('gift_tax_rate', '0.05', '礼物税率'),
('vip_discount_rate', '0.1', 'VIP折扣率');
