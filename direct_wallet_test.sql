-- 直接数据库操作测试钱包功能
-- 注意：这是开发测试用的，生产环境不要这样做

-- 1. 查看所有用户的钱包信息
SELECT u.id, u.username, w.balance, w.currency, w.created_at 
FROM users u 
LEFT JOIN wallets w ON u.id = w.user_id;

-- 2. 为特定用户创建钱包（如果不存在）
INSERT INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at)
VALUES (1, 0.00, 0.00, 'CNY', NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- 3. 直接给用户充值（修改余额）
UPDATE wallets 
SET balance = balance + 100.00, updated_at = NOW() 
WHERE user_id = 1;

-- 4. 查看充值后的余额
SELECT u.id, u.username, w.balance, w.currency 
FROM users u 
LEFT JOIN wallets w ON u.id = w.user_id 
WHERE u.id = 1;

-- 5. 插入充值交易记录
INSERT INTO transactions (user_id, type, amount, balance_after, description, created_at)
VALUES (1, 'RECHARGE', 100.00, (SELECT balance FROM wallets WHERE user_id = 1), '数据库直接充值测试', NOW());

-- 6. 查看交易记录
SELECT * FROM transactions WHERE user_id = 1 ORDER BY created_at DESC LIMIT 10;
