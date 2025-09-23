-- 创建虚拟货币表
CREATE TABLE virtual_currencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency_type VARCHAR(20) NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    frozen_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_earned DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_spent DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    last_updated DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_currency (user_id, currency_type),
    INDEX idx_user_id (user_id),
    INDEX idx_currency_type (currency_type)
);

-- 创建货币交易记录表
CREATE TABLE currency_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency_type VARCHAR(20) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    balance_before DECIMAL(15,2),
    balance_after DECIMAL(15,2),
    description VARCHAR(500),
    related_id BIGINT,
    related_type VARCHAR(50),
    status VARCHAR(20) DEFAULT 'SUCCESS',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_currency_type (currency_type),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_created_at (created_at),
    INDEX idx_related (related_id, related_type)
);

-- 创建礼物特效表
CREATE TABLE gift_effects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gift_id BIGINT NOT NULL,
    effect_type VARCHAR(50) NOT NULL,
    effect_name VARCHAR(100) NOT NULL,
    effect_url VARCHAR(500),
    duration INT NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    is_loop BOOLEAN DEFAULT FALSE,
    loop_count INT DEFAULT 1,
    trigger_condition VARCHAR(200),
    effect_config TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_gift_id (gift_id),
    INDEX idx_effect_type (effect_type),
    INDEX idx_priority (priority)
);

-- 创建VIP特权表
CREATE TABLE vip_privileges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vip_level_id BIGINT NOT NULL,
    privilege_type VARCHAR(50) NOT NULL,
    privilege_name VARCHAR(100) NOT NULL,
    privilege_description TEXT,
    privilege_value VARCHAR(200),
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_vip_level_id (vip_level_id),
    INDEX idx_privilege_type (privilege_type),
    INDEX idx_sort_order (sort_order)
);

-- 创建用户成长值表
CREATE TABLE user_growth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    total_points INT NOT NULL DEFAULT 0,
    current_level INT NOT NULL DEFAULT 1,
    current_level_points INT NOT NULL DEFAULT 0,
    next_level_points INT NOT NULL DEFAULT 100,
    daily_points INT NOT NULL DEFAULT 0,
    weekly_points INT NOT NULL DEFAULT 0,
    monthly_points INT NOT NULL DEFAULT 0,
    last_daily_reset DATETIME,
    last_weekly_reset DATETIME,
    last_monthly_reset DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_current_level (current_level),
    INDEX idx_total_points (total_points)
);

-- 创建成长值记录表
CREATE TABLE growth_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    points INT NOT NULL,
    description VARCHAR(200),
    related_id BIGINT,
    related_type VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_action_type (action_type),
    INDEX idx_created_at (created_at),
    INDEX idx_related (related_id, related_type)
);

-- 增强礼物表 - 添加新字段
ALTER TABLE gifts ADD COLUMN sub_category VARCHAR(50) AFTER category;
ALTER TABLE gifts ADD COLUMN rarity VARCHAR(20) DEFAULT 'COMMON' AFTER sub_category;
ALTER TABLE gifts ADD COLUMN effect_type VARCHAR(50) AFTER rarity;
ALTER TABLE gifts ADD COLUMN animation_url VARCHAR(500) AFTER effect_type;
ALTER TABLE gifts ADD COLUMN sound_url VARCHAR(500) AFTER animation_url;
ALTER TABLE gifts ADD COLUMN is_limited BOOLEAN DEFAULT FALSE AFTER sound_url;
ALTER TABLE gifts ADD COLUMN limited_quantity INT AFTER is_limited;
ALTER TABLE gifts ADD COLUMN sold_quantity INT DEFAULT 0 AFTER limited_quantity;
ALTER TABLE gifts ADD COLUMN is_hot BOOLEAN DEFAULT FALSE AFTER sold_quantity;
ALTER TABLE gifts ADD COLUMN is_new BOOLEAN DEFAULT FALSE AFTER is_hot;
ALTER TABLE gifts ADD COLUMN sort_order INT DEFAULT 0 AFTER is_new;
ALTER TABLE gifts ADD COLUMN tags VARCHAR(500) AFTER sort_order;

-- 添加索引
ALTER TABLE gifts ADD INDEX idx_sub_category (sub_category);
ALTER TABLE gifts ADD INDEX idx_rarity (rarity);
ALTER TABLE gifts ADD INDEX idx_is_limited (is_limited);
ALTER TABLE gifts ADD INDEX idx_is_hot (is_hot);
ALTER TABLE gifts ADD INDEX idx_is_new (is_new);
ALTER TABLE gifts ADD INDEX idx_sort_order (sort_order);

-- 插入默认的虚拟货币类型数据
INSERT INTO virtual_currencies (user_id, currency_type, balance, created_at) 
SELECT id, 'COINS', 100.00, NOW() FROM users WHERE id NOT IN (SELECT user_id FROM virtual_currencies WHERE currency_type = 'COINS');

INSERT INTO virtual_currencies (user_id, currency_type, balance, created_at) 
SELECT id, 'DIAMONDS', 0.00, NOW() FROM users WHERE id NOT IN (SELECT user_id FROM virtual_currencies WHERE currency_type = 'DIAMONDS');

INSERT INTO virtual_currencies (user_id, currency_type, balance, created_at) 
SELECT id, 'POINTS', 0.00, NOW() FROM users WHERE id NOT IN (SELECT user_id FROM virtual_currencies WHERE currency_type = 'POINTS');

INSERT INTO virtual_currencies (user_id, currency_type, balance, created_at) 
SELECT id, 'GOLD', 0.00, NOW() FROM users WHERE id NOT IN (SELECT user_id FROM virtual_currencies WHERE currency_type = 'GOLD');

-- 插入默认的VIP特权数据
INSERT INTO vip_privileges (vip_level_id, privilege_type, privilege_name, privilege_description, privilege_value, sort_order) VALUES
(1, 'DISCOUNT', '购物折扣', '购买商品享受9.5折优惠', '0.95', 1),
(1, 'FREE_GIFTS', '免费礼物', '每日可发送3个免费礼物', '3', 2),
(1, 'PRIORITY_MATCH', '优先匹配', '优先匹配其他用户', 'true', 3),

(2, 'DISCOUNT', '购物折扣', '购买商品享受9折优惠', '0.9', 1),
(2, 'FREE_GIFTS', '免费礼物', '每日可发送5个免费礼物', '5', 2),
(2, 'PRIORITY_MATCH', '优先匹配', '优先匹配其他用户', 'true', 3),
(2, 'EXCLUSIVE_AVATAR', '专属头像', 'VIP专属头像框', 'vip_avatar_2', 4),
(2, 'AD_FREE', '免广告', '免除广告干扰', 'true', 5),

(3, 'DISCOUNT', '购物折扣', '购买商品享受8.5折优惠', '0.85', 1),
(3, 'FREE_GIFTS', '免费礼物', '每日可发送10个免费礼物', '10', 2),
(3, 'PRIORITY_MATCH', '优先匹配', '优先匹配其他用户', 'true', 3),
(3, 'EXCLUSIVE_AVATAR', '专属头像', 'VIP专属头像框', 'vip_avatar_3', 4),
(3, 'AD_FREE', '免广告', '免除广告干扰', 'true', 5),
(3, 'CUSTOMER_SERVICE', '专属客服', 'VIP专属客服', 'true', 6),
(3, 'EARLY_ACCESS', '提前体验', '提前体验新功能', 'true', 7),

(4, 'DISCOUNT', '购物折扣', '购买商品享受8折优惠', '0.8', 1),
(4, 'FREE_GIFTS', '免费礼物', '每日可发送20个免费礼物', '20', 2),
(4, 'PRIORITY_MATCH', '优先匹配', '优先匹配其他用户', 'true', 3),
(4, 'EXCLUSIVE_AVATAR', '专属头像', 'VIP专属头像框', 'vip_avatar_4', 4),
(4, 'AD_FREE', '免广告', '免除广告干扰', 'true', 5),
(4, 'CUSTOMER_SERVICE', '专属客服', 'VIP专属客服', 'true', 6),
(4, 'EARLY_ACCESS', '提前体验', '提前体验新功能', 'true', 7),
(4, 'BONUS_COINS', '额外金币', '每日额外金币奖励', '100', 8),
(4, 'EXTENDED_PROFILE', '扩展资料', '更多个人资料选项', 'true', 9),
(4, 'UNLIMITED_MESSAGES', '无限消息', '无限制发送消息', 'true', 10);

-- 插入示例礼物数据
INSERT INTO gifts (name, description, image_url, price, category, sub_category, rarity, effect_type, animation_url, sound_url, is_limited, limited_quantity, is_hot, is_new, sort_order, tags) VALUES
('玫瑰花', '表达爱意的经典礼物', '/images/gifts/rose.png', 1.00, 'ROMANCE', 'FLOWER', 'COMMON', 'ANIMATION', '/animations/rose.json', '/sounds/rose.mp3', FALSE, NULL, TRUE, FALSE, 1, '["浪漫", "经典", "爱情"]'),
('爱心', '传递温暖的心意', '/images/gifts/heart.png', 2.00, 'EMOTION', 'LOVE', 'COMMON', 'PARTICLE', '/animations/heart.json', '/sounds/heart.mp3', FALSE, NULL, TRUE, FALSE, 2, '["情感", "温暖", "爱心"]'),
('钻石', '珍贵的象征', '/images/gifts/diamond.png', 10.00, 'SPECIAL', 'JEWELRY', 'RARE', 'LIGHTNING', '/animations/diamond.json', '/sounds/diamond.mp3', FALSE, NULL, FALSE, TRUE, 3, '["珍贵", "稀有", "象征"]'),
('皇冠', '尊贵的象征', '/images/gifts/crown.png', 50.00, 'SPECIAL', 'CROWN', 'EPIC', 'FULLSCREEN', '/animations/crown.json', '/sounds/crown.mp3', TRUE, 100, FALSE, FALSE, 4, '["尊贵", "皇冠", "特殊"]'),
('彩虹', '美好的祝愿', '/images/gifts/rainbow.png', 5.00, 'EMOTION', 'WISH', 'COMMON', 'RAINBOW', '/animations/rainbow.json', '/sounds/rainbow.mp3', FALSE, NULL, FALSE, FALSE, 5, '["美好", "祝愿", "彩虹"]'),
('烟花', '庆祝的喜悦', '/images/gifts/fireworks.png', 8.00, 'CELEBRATION', 'FIREWORKS', 'RARE', 'FIREWORKS', '/animations/fireworks.json', '/sounds/fireworks.mp3', FALSE, NULL, TRUE, FALSE, 6, '["庆祝", "喜悦", "烟花"]'),
('限量版爱心', '限量的珍贵礼物', '/images/gifts/limited_heart.png', 100.00, 'LIMITED', 'SPECIAL', 'LEGENDARY', 'FULLSCREEN', '/animations/limited_heart.json', '/sounds/limited_heart.mp3', TRUE, 50, FALSE, TRUE, 7, '["限量", "珍贵", "传说"]');
