-- 创建充值订单表
CREATE TABLE IF NOT EXISTS recharge_orders (
    order_id VARCHAR(50) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    package_id VARCHAR(50),
    coins BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    third_party_order_id VARCHAR(100),
    third_party_transaction_id VARCHAR(100),
    notify_url VARCHAR(500),
    return_url VARCHAR(500),
    description VARCHAR(200),
    paid_at DATETIME,
    expired_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_third_party_order_id (third_party_order_id),
    INDEX idx_third_party_transaction_id (third_party_transaction_id)
);

-- 创建充值套餐表
CREATE TABLE IF NOT EXISTS recharge_packages (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    coins BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    bonus_coins BIGINT DEFAULT 0,
    first_time_bonus BIGINT DEFAULT 0,
    is_recommended BOOLEAN DEFAULT FALSE,
    is_popular BOOLEAN DEFAULT FALSE,
    discount_label VARCHAR(50),
    description VARCHAR(500),
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_is_active (is_active),
    INDEX idx_sort_order (sort_order)
);

-- 插入充值套餐数据（参考截图的档位）
INSERT INTO recharge_packages (id, name, coins, price, sort_order, is_recommended, is_popular) VALUES
('package_1200', '小试牛刀', 1200, 12.00, 1, TRUE, FALSE),
('package_5800', '小有收获', 5800, 58.00, 2, FALSE, TRUE),
('package_9800', '超值优选', 9800, 98.00, 3, FALSE, FALSE),
('package_800', '入门体验', 800, 8.00, 4, FALSE, FALSE),
('package_2800', '进阶选择', 2800, 28.00, 5, FALSE, FALSE),
('package_3800', '品质之选', 3800, 38.00, 6, FALSE, FALSE),
('package_19800', '豪华套餐', 19800, 198.00, 7, FALSE, FALSE),
('package_23800', '尊享套餐', 23800, 238.00, 8, FALSE, FALSE),
('package_51800', '至尊套餐', 51800, 518.00, 9, FALSE, FALSE);

-- 为已存在的钱包表添加索引（如果还没有的话）
CREATE INDEX IF NOT EXISTS idx_wallets_user_id ON wallets(user_id);

-- 为交易表添加索引（如果还没有的话）  
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);
