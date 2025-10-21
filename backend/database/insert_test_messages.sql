-- 插入测试消息数据
-- 使用现有用户ID: 22491729, 23820512, 23820513

-- 用户22491729和23820512之间的对话
INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at, updated_at) VALUES
(22491729, 23820512, '你好啊，在干嘛呢？', 'TEXT', true, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
(23820512, 22491729, '刚吃完饭，你呢？', 'TEXT', true, DATE_SUB(NOW(), INTERVAL 110 MINUTE), NOW()),
(22491729, 23820512, '我也是，要不要一起视频聊聊？', 'TEXT', true, DATE_SUB(NOW(), INTERVAL 105 MINUTE), NOW()),
(23820512, 22491729, '好呀，等我一下', 'TEXT', false, DATE_SUB(NOW(), INTERVAL 100 MINUTE), NOW());

-- 用户22491729和23820513之间的对话
INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, created_at, updated_at) VALUES
(23820513, 22491729, '晚上好呀，在线吗？', 'TEXT', true, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW()),
(22491729, 23820513, '在的在的', 'TEXT', true, DATE_SUB(NOW(), INTERVAL 25 MINUTE), NOW()),
(23820513, 22491729, '可以打个视频电话吗？', 'TEXT', false, DATE_SUB(NOW(), INTERVAL 20 MINUTE), NOW());

-- 插入测试通话记录
INSERT INTO call_records (session_id, caller_id, callee_id, call_type, call_status, duration, price_per_min, total_cost, start_time, end_time, created_at, updated_at) VALUES
('SESSION_001', 22491729, 23820512, 'VIDEO', 'ENDED', 162, 200.00, 600.00, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 117 MINUTE), DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
('SESSION_002', 23820512, 22491729, 'VIDEO', 'CANCELLED', 0, 200.00, 0.00, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
('SESSION_003', 23820513, 22491729, 'VIDEO', 'MISSED', 0, 200.00, 0.00, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NULL, DATE_SUB(NOW(), INTERVAL 30 MINUTE), NOW());

-- 插入用户关系数据
INSERT INTO user_relationships (user_id, target_user_id, relationship_type, intimacy_score, created_at, updated_at) VALUES
(22491729, 23820512, 'FRIEND', 500, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(23820512, 22491729, 'FRIEND', 500, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
(22491729, 23820513, 'LIKE', 100, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(23820513, 22491729, 'LIKE', 150, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW());
