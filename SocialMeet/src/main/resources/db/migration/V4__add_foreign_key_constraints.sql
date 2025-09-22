-- 添加缺失的外键约束和索引
-- 版本: V4
-- 描述: 完善数据库关联关系设计，添加外键约束和优化索引

-- 添加第三方认证表的外键约束
ALTER TABLE third_party_auths 
ADD CONSTRAINT fk_third_party_auths_user_id 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 添加身份证认证表的外键约束
ALTER TABLE id_card_verify 
ADD CONSTRAINT fk_id_card_verify_user_id 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 添加关注关系表的外键约束
ALTER TABLE follow_relationships 
ADD CONSTRAINT fk_follow_relationships_follower_id 
FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE follow_relationships 
ADD CONSTRAINT fk_follow_relationships_following_id 
FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE;

-- 添加用户照片表的外键约束
ALTER TABLE user_photos 
ADD CONSTRAINT fk_user_photos_user_id 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 添加消息表的外键约束
ALTER TABLE messages 
ADD CONSTRAINT fk_messages_sender_id 
FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE messages 
ADD CONSTRAINT fk_messages_receiver_id 
FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE;

-- 添加缺失的索引
-- 消息表复合索引
CREATE INDEX idx_messages_sender_receiver ON messages(sender_id, receiver_id);
CREATE INDEX idx_messages_created_at ON messages(created_at);
CREATE INDEX idx_messages_is_read ON messages(is_read);

-- 用户照片表索引
CREATE INDEX idx_user_photos_is_avatar ON user_photos(is_avatar);
CREATE INDEX idx_user_photos_uploaded_at ON user_photos(uploaded_at);

-- 关注关系表时间索引
CREATE INDEX idx_follow_relationships_created_at ON follow_relationships(created_at);

-- 用户表优化索引
CREATE INDEX idx_users_gender_age ON users(gender, age);
CREATE INDEX idx_users_city ON users(city);
CREATE INDEX idx_users_is_online ON users(is_online);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);

-- 动态表优化索引
CREATE INDEX idx_dynamics_user_status ON dynamics(user_id, status);
CREATE INDEX idx_dynamics_publish_time_desc ON dynamics(publish_time DESC);
CREATE INDEX idx_dynamics_like_count_desc ON dynamics(like_count DESC);

-- 添加表注释
ALTER TABLE third_party_auths COMMENT = '第三方认证表';
ALTER TABLE id_card_verify COMMENT = '身份证认证表';
ALTER TABLE follow_relationships COMMENT = '关注关系表';
ALTER TABLE user_photos COMMENT = '用户照片表';
ALTER TABLE messages COMMENT = '消息表';
ALTER TABLE dynamics COMMENT = '动态表';
ALTER TABLE dynamic_likes COMMENT = '动态点赞表';
ALTER TABLE dynamic_comments COMMENT = '动态评论表';
