-- 第三步：创建钱包数据
INSERT INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at) VALUES
(1001, 300.00, 0.00, 'CNY', NOW(), NOW()),
(1002, 200.00, 0.00, 'CNY', NOW(), NOW()),
(1003, 350.00, 0.00, 'CNY', NOW(), NOW()),
(1004, 500.00, 0.00, 'CNY', NOW(), NOW()),
(1005, 250.00, 0.00, 'CNY', NOW(), NOW()),
(1006, 180.00, 0.00, 'CNY', NOW(), NOW()),
(1007, 320.00, 0.00, 'CNY', NOW(), NOW()),
(1008, 280.00, 0.00, 'CNY', NOW(), NOW()),
(1009, 400.00, 0.00, 'CNY', NOW(), NOW()),
(1010, 150.00, 0.00, 'CNY', NOW(), NOW());
