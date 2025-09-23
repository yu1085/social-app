-- 创建VIP等级表
CREATE TABLE IF NOT EXISTS vip_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    level INT NOT NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    duration INT NOT NULL,
    benefits TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_level (level),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认VIP等级数据
INSERT INTO vip_levels (name, level, price, duration, benefits, is_active) VALUES
('普通会员', 0, 0.00, 0, '基础功能', TRUE),
('VIP会员', 1, 29.90, 30, 'VIP专享、优先客服、专属内容、折扣特权', TRUE),
('SVIP会员', 2, 59.90, 30, 'SVIP专享、优先客服、专属内容、更高折扣、提前体验', TRUE),
('钻石会员', 3, 99.90, 30, '钻石专享、优先客服、专属内容、最高折扣、提前体验、自定义头像', TRUE),
('至尊会员', 4, 199.90, 30, '至尊专享、优先客服、专属内容、最高折扣、提前体验、自定义头像、无限消息、高级筛选', TRUE)
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    price = VALUES(price),
    duration = VALUES(duration),
    benefits = VALUES(benefits),
    is_active = VALUES(is_active);
