-- 创建财富等级表
CREATE TABLE IF NOT EXISTS wealth_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    wealth_value INT NOT NULL DEFAULT 0,
    level_name VARCHAR(50) NOT NULL,
    level_icon VARCHAR(10),
    level_color VARCHAR(20),
    min_wealth_value INT NOT NULL,
    max_wealth_value INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_wealth_value (wealth_value),
    INDEX idx_level_name (level_name),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认等级规则数据（可选，用于参考）
INSERT INTO wealth_levels (user_id, wealth_value, level_name, level_icon, level_color, min_wealth_value, max_wealth_value) VALUES
(0, 1000000, '黑钻', '💎', '#000000', 1000000, NULL),
(0, 700000, '金钻', '💎', '#FFD700', 700000, 999999),
(0, 500000, '红钻', '💎', '#FF69B4', 500000, 699999),
(0, 300000, '橙钻', '💎', '#FF8C00', 300000, 499999),
(0, 100000, '紫钻', '💎', '#8A2BE2', 100000, 299999),
(0, 50000, '蓝钻', '💎', '#1E90FF', 50000, 99999),
(0, 30000, '青钻', '💎', '#00CED1', 30000, 49999),
(0, 10000, '铂金', '💎', '#C0C0C0', 10000, 29999),
(0, 5000, '黄金', '💎', '#FFD700', 5000, 9999),
(0, 2000, '白银', '💎', '#C0C0C0', 2000, 4999),
(0, 1000, '青铜', '💎', '#CD7F32', 1000, 1999),
(0, 0, '普通', '⭐', '#808080', 0, 999)
ON DUPLICATE KEY UPDATE level_name = VALUES(level_name);
