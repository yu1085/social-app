-- 给用户ID 86945008 充值888元

-- 1. 确保用户存在
INSERT INTO users (id, username, phone, nickname, gender, is_active, is_online, created_at, updated_at)
VALUES (86945008, 'user_86945008', '13800138008', '神秘小猫咪887', 'FEMALE', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE username = username;

-- 2. 创建钱包（如果不存在）
INSERT INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at)
VALUES (86945008, 888.00, 0.00, 'CNY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE balance = 888.00, updated_at = CURRENT_TIMESTAMP;

-- 3. 添加充值交易记录
INSERT INTO transactions (user_id, type, amount, balance_after, description, status, created_at, updated_at)
VALUES (86945008, 'RECHARGE', 888.00, 888.00, '给用户86945008充值888元', 'SUCCESS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. 查询结果
SELECT '用户信息' as info, id, username, phone, nickname FROM users WHERE id = 86945008;
SELECT '钱包信息' as info, user_id, balance, frozen_amount, currency FROM wallets WHERE user_id = 86945008;
SELECT '交易记录' as info, id, user_id, type, amount, balance_after, description FROM transactions WHERE user_id = 86945008;
