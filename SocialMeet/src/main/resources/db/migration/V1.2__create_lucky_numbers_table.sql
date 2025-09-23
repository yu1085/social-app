-- 创建靓号表
CREATE TABLE IF NOT EXISTS lucky_numbers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(20) NOT NULL UNIQUE,
    tier ENUM('LIMITED', 'TOP', 'SUPER', 'NORMAL') NOT NULL,
    price BIGINT NOT NULL,
    is_limited BOOLEAN NOT NULL DEFAULT FALSE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    icon VARCHAR(10) NOT NULL DEFAULT '靓',
    icon_color VARCHAR(20) NOT NULL DEFAULT '#FFD700',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_tier (tier),
    INDEX idx_price (price),
    INDEX idx_available (is_available),
    INDEX idx_limited (is_limited),
    INDEX idx_tier_price (tier, price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入示例靓号数据
INSERT INTO lucky_numbers (number, tier, price, is_limited, is_available, description) VALUES
-- 限量靓号
('10000005', 'LIMITED', 88800, TRUE, TRUE, '限量靓号'),
('12345678', 'LIMITED', 128000, TRUE, TRUE, '限量靓号'),
('88888888', 'LIMITED', 188000, TRUE, TRUE, '限量靓号'),
('66666666', 'LIMITED', 168000, TRUE, TRUE, '限量靓号'),

-- 顶级靓号
('10000010', 'TOP', 88800, FALSE, TRUE, '顶级靓号'),
('10000011', 'TOP', 58800, FALSE, TRUE, '顶级靓号'),
('10000012', 'TOP', 58800, FALSE, TRUE, '顶级靓号'),
('10000013', 'TOP', 58800, FALSE, TRUE, '顶级靓号'),
('10000014', 'TOP', 58800, FALSE, TRUE, '顶级靓号'),
('10000015', 'TOP', 58800, FALSE, TRUE, '顶级靓号'),
('10000016', 'TOP', 58800, FALSE, TRUE, '顶级靓号'),
('10000017', 'TOP', 58800, FALSE, TRUE, '顶级靓号'),
('10000018', 'TOP', 58800, FALSE, TRUE, '顶级靓号'),
('10000019', 'TOP', 58800, FALSE, TRUE, '顶级靓号'),
('10000020', 'TOP', 58800, FALSE, TRUE, '顶级靓号'),
('10000002', 'TOP', 88800, FALSE, TRUE, '顶级靓号'),
('10000006', 'TOP', 88800, FALSE, TRUE, '顶级靓号'),
('10000008', 'TOP', 88800, FALSE, TRUE, '顶级靓号'),
('10000009', 'TOP', 88800, FALSE, TRUE, '顶级靓号'),
('28888888', 'TOP', 5880, FALSE, TRUE, '顶级靓号'),

-- 超级靓号
('99999998', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('66666668', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('12222222', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('21212121', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('89898989', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('16666666', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('11111112', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('18181818', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('68686868', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('80808080', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('90909090', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('12341234', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('28002800', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('58005800', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('62226222', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('66006600', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('66806680', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('88808880', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('99989998', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),
('12211221', 'SUPER', 5880, FALSE, TRUE, '超级靓号'),

-- 普通靓号
('18888828', 'NORMAL', 3800, FALSE, TRUE, '普通靓号'),
('18888868', 'NORMAL', 3800, FALSE, TRUE, '普通靓号'),
('18888878', 'NORMAL', 3800, FALSE, TRUE, '普通靓号'),
('19188888', 'NORMAL', 3800, FALSE, TRUE, '普通靓号'),
('19188818', 'NORMAL', 3800, FALSE, TRUE, '普通靓号'),
('19901818', 'NORMAL', 3800, FALSE, TRUE, '普通靓号'),
('19911818', 'NORMAL', 3800, FALSE, TRUE, '普通靓号'),
('19921818', 'NORMAL', 3800, FALSE, TRUE, '普通靓号'),
('19931818', 'NORMAL', 3800, FALSE, TRUE, '普通靓号'),
('19941818', 'NORMAL', 3800, FALSE, TRUE, '普通靓号');
