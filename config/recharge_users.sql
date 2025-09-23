-- 给指定用户充值10000的SQL脚本
-- 用户ID: 65899032 和 44479883

-- 首先确保用户存在，如果不存在则创建
INSERT IGNORE INTO users (id, username, password, nickname, is_active, created_at, updated_at) 
VALUES 
(65899032, 'user_65899032', 'default_password', '用户65899032', TRUE, NOW(), NOW()),
(44479883, 'user_44479883', 'default_password', '用户44479883', TRUE, NOW(), NOW());

-- 确保用户钱包存在，如果不存在则创建
INSERT IGNORE INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at)
VALUES 
(65899032, 0.00, 0.00, 'CNY', NOW(), NOW()),
(44479883, 0.00, 0.00, 'CNY', NOW(), NOW());

-- 给用户充值10000
UPDATE wallets 
SET balance = balance + 10000.00, updated_at = NOW()
WHERE user_id IN (65899032, 44479883);

-- 记录充值交易
INSERT INTO transactions (user_id, type, amount, balance_after, description, status, created_at)
SELECT 
    w.user_id,
    'RECHARGE',
    10000.00,
    w.balance,
    '管理员充值10000',
    'SUCCESS',
    NOW()
FROM wallets w
WHERE w.user_id IN (65899032, 44479883);

-- 查询充值后的余额
SELECT 
    u.id as user_id,
    u.username,
    u.nickname,
    w.balance,
    w.currency,
    w.updated_at as last_updated
FROM users u
JOIN wallets w ON u.id = w.user_id
WHERE u.id IN (65899032, 44479883)
ORDER BY u.id;
