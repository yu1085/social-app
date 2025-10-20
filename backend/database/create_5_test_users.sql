-- Create 5 diverse test users for homepage display
-- Users will have different genders, locations, prices, and online statuses

INSERT INTO users (username, nickname, phone, gender, age, location, signature, online_status, balance, created_at, updated_at)
VALUES
-- User 1: Female, Beijing, online
('user_20250001', '北京小姐姐', '13900001111', 'FEMALE', 25, '北京', '喜欢旅游和摄影，期待遇见有趣的人', 'ONLINE', 500.00, NOW(), NOW()),

-- User 2: Male, Shanghai, online
('user_20250002', '上海阳光男孩', '13900002222', 'MALE', 28, '上海', '热爱运动和音乐，交个朋友吧', 'ONLINE', 300.00, NOW(), NOW()),

-- User 3: Female, Guangzhou, busy
('user_20250003', '广州甜心', '13900003333', 'FEMALE', 23, '广州', '爱笑的女孩运气不会太差', 'BUSY', 800.00, NOW(), NOW()),

-- User 4: Female, Shenzhen, online
('user_20250004', '深圳小仙女', '13900004444', 'FEMALE', 26, '深圳', '工作之余想认识更多朋友', 'ONLINE', 1200.00, NOW(), NOW()),

-- User 5: Male, Hangzhou, offline
('user_20250005', '杭州暖男', '13900005555', 'MALE', 29, '杭州', '性格温和，喜欢美食和电影', 'OFFLINE', 600.00, NOW(), NOW());

-- Set up user settings (call prices) for each user
INSERT INTO user_settings (user_id, voice_call_price, video_call_price, created_at, updated_at)
SELECT id,
    CASE
        WHEN phone = '13900001111' THEN 3.00
        WHEN phone = '13900002222' THEN 2.50
        WHEN phone = '13900003333' THEN 4.00
        WHEN phone = '13900004444' THEN 5.00
        WHEN phone = '13900005555' THEN 2.00
    END AS voice_call_price,
    CASE
        WHEN phone = '13900001111' THEN 6.00
        WHEN phone = '13900002222' THEN 5.00
        WHEN phone = '13900003333' THEN 8.00
        WHEN phone = '13900004444' THEN 10.00
        WHEN phone = '13900005555' THEN 4.00
    END AS video_call_price,
    NOW(), NOW()
FROM users
WHERE phone IN ('13900001111', '13900002222', '13900003333', '13900004444', '13900005555');

-- Create wallets for each user
INSERT INTO wallets (user_id, balance, total_recharged, total_consumed, created_at, updated_at)
SELECT id, balance, 0.00, 0.00, NOW(), NOW()
FROM users
WHERE phone IN ('13900001111', '13900002222', '13900003333', '13900004444', '13900005555');

-- Verify the new users
SELECT
    u.id,
    u.username,
    u.nickname,
    u.phone,
    u.gender,
    u.age,
    u.location,
    u.online_status,
    u.balance,
    us.video_call_price
FROM users u
LEFT JOIN user_settings us ON u.id = us.user_id
WHERE u.phone IN ('13900001111', '13900002222', '13900003333', '13900004444', '13900005555')
ORDER BY u.id;
