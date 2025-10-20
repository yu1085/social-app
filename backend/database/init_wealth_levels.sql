-- 初始化财富等级数据
INSERT INTO wealth_levels (level_name, level_id, min_wealth_value, max_wealth_value, level_description, level_icon, is_max_level, sort_order, created_at, updated_at) VALUES
('青铜', 1, 1000, 1999, '青铜等级', 'bronze_icon', false, 1, NOW(), NOW()),
('白银', 2, 2000, 4999, '白银等级', 'silver_icon', false, 2, NOW(), NOW()),
('黄金', 3, 5000, 9999, '黄金等级', 'gold_icon', false, 3, NOW(), NOW()),
('铂金', 4, 10000, 29999, '铂金等级', 'platinum_icon', false, 4, NOW(), NOW()),
('青钻', 5, 30000, 49999, '青钻等级', 'blue_diamond_icon', false, 5, NOW(), NOW()),
('蓝钻', 6, 50000, 99999, '蓝钻等级', 'blue_diamond_icon', false, 6, NOW(), NOW()),
('紫钻', 7, 100000, 299999, '紫钻等级', 'purple_diamond_icon', false, 7, NOW(), NOW()),
('橙钻', 8, 300000, 499999, '橙钻等级', 'orange_diamond_icon', false, 8, NOW(), NOW()),
('红钻', 9, 500000, 699999, '红钻等级', 'red_diamond_icon', false, 9, NOW(), NOW()),
('金钻', 10, 700000, 999999, '金钻等级', 'gold_diamond_icon', false, 10, NOW(), NOW()),
('黑钻', 11, 1000000, NULL, '黑钻等级', 'black_diamond_icon', true, 11, NOW(), NOW());

-- 创建交易记录表
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    coin_source VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    coin_amount INT NOT NULL,
    wealth_value INT NOT NULL DEFAULT 0,
    description TEXT,
    order_id VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_coin_source (coin_source)
);

-- 更新用户表，添加财富等级字段
ALTER TABLE users ADD COLUMN IF NOT EXISTS wealth_level INT DEFAULT 1;
