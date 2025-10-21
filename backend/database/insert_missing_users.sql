-- Insert missing test users (23820514, 23820515)
USE socialmeet;

-- User 23820514
INSERT INTO users (id, username, nickname, phone, password, gender, birthday, location, signature, avatar_url, vip_level, vip_expiry_date, balance, created_at, updated_at)
VALUES
(23820514, 'user_14514', '可爱的小猫咪', '13900003333', '$2a$10$dummy.password.hash.value.here', 'FEMALE', '1998-08-15', '深圳', '爱笑的女孩', '/uploads/default_avatar_female.jpg', 0, NULL, 0.00, NOW(), NOW());

-- User 23820515
INSERT INTO users (id, username, nickname, phone, password, gender, birthday, location, signature, avatar_url, vip_level, vip_expiry_date, balance, created_at, updated_at)
VALUES
(23820515, 'user_14515', '阳光运动男', '13900004444', '$2a$10$dummy.password.hash.value.here', 'MALE', '1996-05-20', '杭州', '热爱运动和旅行', '/uploads/default_avatar_male.jpg', 0, NULL, 0.00, NOW(), NOW());

-- Create wallets for these users
INSERT INTO wallets (user_id, balance, total_recharge, total_consumption, created_at, updated_at)
VALUES
(23820514, 0.00, 0.00, 0.00, NOW(), NOW()),
(23820515, 0.00, 0.00, 0.00, NOW(), NOW());

-- Create user settings for these users
INSERT INTO user_settings (user_id, voice_call_enabled, video_call_enabled, message_enabled, voice_call_price, video_call_price, message_price, created_at, updated_at)
VALUES
(23820514, true, true, true, 10.00, 20.00, 1.00, NOW(), NOW()),
(23820515, true, true, true, 10.00, 20.00, 1.00, NOW(), NOW());

SELECT 'Missing users created successfully!' AS result;
