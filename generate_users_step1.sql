-- 第一步：清空现有用户数据（保留测试用户）
DELETE FROM transactions WHERE user_id NOT IN (86945008);
DELETE FROM wallets WHERE user_id NOT IN (86945008);
DELETE FROM users WHERE id NOT IN (86945008);
