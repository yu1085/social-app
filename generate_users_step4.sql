-- 第四步：创建交易记录
INSERT INTO transactions (user_id, type, amount, balance_after, description, status, created_at) VALUES
(1001, 'RECHARGE', 300.00, 300.00, '初始充值', 'SUCCESS', NOW()),
(1002, 'RECHARGE', 200.00, 200.00, '初始充值', 'SUCCESS', NOW()),
(1003, 'RECHARGE', 350.00, 350.00, '初始充值', 'SUCCESS', NOW()),
(1004, 'RECHARGE', 500.00, 500.00, '初始充值', 'SUCCESS', NOW()),
(1005, 'RECHARGE', 250.00, 250.00, '初始充值', 'SUCCESS', NOW()),
(1006, 'RECHARGE', 180.00, 180.00, '初始充值', 'SUCCESS', NOW()),
(1007, 'RECHARGE', 320.00, 320.00, '初始充值', 'SUCCESS', NOW()),
(1008, 'RECHARGE', 280.00, 280.00, '初始充值', 'SUCCESS', NOW()),
(1009, 'RECHARGE', 400.00, 400.00, '初始充值', 'SUCCESS', NOW()),
(1010, 'RECHARGE', 150.00, 150.00, '初始充值', 'SUCCESS', NOW());
