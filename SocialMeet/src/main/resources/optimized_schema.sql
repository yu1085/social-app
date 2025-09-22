-- 优化后的数据库架构
-- 动态表 - 优化索引和字段
CREATE TABLE IF NOT EXISTS dynamics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    images JSON,
    location VARCHAR(100),
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    publish_time DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 优化索引
    INDEX idx_user_id (user_id),
    INDEX idx_publish_time (publish_time),
    INDEX idx_like_count (like_count),
    INDEX idx_status (status),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_user_status_time (user_id, status, publish_time),
    INDEX idx_status_time_like (status, publish_time, like_count),
    INDEX idx_location (location),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 动态点赞表 - 优化索引
CREATE TABLE IF NOT EXISTS dynamic_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 唯一约束和索引
    UNIQUE KEY uk_dynamic_user (dynamic_id, user_id),
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (dynamic_id) REFERENCES dynamics(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 动态评论表 - 优化索引
CREATE TABLE IF NOT EXISTS dynamic_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 优化索引
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_created_at (created_at),
    INDEX idx_dynamic_created (dynamic_id, created_at),
    
    -- 外键约束
    FOREIGN KEY (dynamic_id) REFERENCES dynamics(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES dynamic_comments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户表 - 优化索引（增强版）
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE,
    nickname VARCHAR(50),
    avatar VARCHAR(500),
    age INT,
    gender VARCHAR(10),
    location VARCHAR(100),
    bio TEXT,
    is_online BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
    last_seen DATETIME,
    
    -- 新增的增强功能字段
    call_price INT NOT NULL DEFAULT 0,
    message_price INT NOT NULL DEFAULT 0,
    video_call_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    voice_call_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    message_charge_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    city VARCHAR(50),
    hometown VARCHAR(50),
    beauty_score INT NOT NULL DEFAULT 0,
    review_score DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    follower_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    
    -- 原有字段
    password VARCHAR(100),
    email VARCHAR(100),
    avatar_url VARCHAR(500),
    birth_date DATETIME,
    latitude DOUBLE,
    longitude DOUBLE,
    height INT,
    weight INT,
    education VARCHAR(50),
    income VARCHAR(50),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 优化索引
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_nickname (nickname),
    INDEX idx_gender (gender),
    INDEX idx_location (location),
    INDEX idx_is_online (is_online),
    INDEX idx_status (status),
    INDEX idx_last_seen (last_seen),
    INDEX idx_gender_online (gender, is_online),
    INDEX idx_gender_status (gender, status),
    INDEX idx_location_gender (location, gender),
    INDEX idx_beauty_score (beauty_score),
    INDEX idx_review_score (review_score),
    INDEX idx_follower_count (follower_count),
    INDEX idx_like_count (like_count),
    INDEX idx_call_price (call_price),
    INDEX idx_message_price (message_price),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 关注关系表 - 优化索引
CREATE TABLE IF NOT EXISTS follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 唯一约束和索引
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    INDEX idx_follower_id (follower_id),
    INDEX idx_following_id (following_id),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 通话设置表
CREATE TABLE IF NOT EXISTS call_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    call_price INT NOT NULL DEFAULT 0,
    message_price INT NOT NULL DEFAULT 0,
    video_call_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    voice_call_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    message_charge_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_call_price (call_price),
    INDEX idx_message_price (message_price),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 验证码表 - 优化索引
CREATE TABLE IF NOT EXISTS verification_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL,
    code VARCHAR(10) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_phone (phone),
    INDEX idx_code (code),
    INDEX idx_expires_at (expires_at),
    INDEX idx_phone_code (phone, code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户会话表
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    device_id VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 索引
    UNIQUE KEY uk_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_device_id (device_id),
    INDEX idx_expires_at (expires_at),
    INDEX idx_user_device (user_id, device_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建视图 - 用户统计视图
CREATE OR REPLACE VIEW user_stats AS
SELECT 
    u.id,
    u.nickname,
    u.avatar,
    u.gender,
    u.location,
    u.is_online,
    u.status,
    u.follower_count,
    u.like_count,
    COUNT(DISTINCT d.id) as dynamic_count,
    COUNT(DISTINCT dl.id) as total_likes_received,
    u.last_seen,
    u.created_at
FROM users u
LEFT JOIN dynamics d ON u.id = d.user_id AND d.is_deleted = FALSE
LEFT JOIN dynamic_likes dl ON d.id = dl.dynamic_id
GROUP BY u.id;

-- 创建视图 - 动态统计视图
CREATE OR REPLACE VIEW dynamic_stats AS
SELECT 
    d.id,
    d.user_id,
    d.content,
    d.location,
    d.like_count,
    d.comment_count,
    d.view_count,
    d.publish_time,
    u.nickname,
    u.avatar,
    u.gender,
    u.location as user_location
FROM dynamics d
JOIN users u ON d.user_id = u.id
WHERE d.is_deleted = FALSE;

-- 创建存储过程 - 更新用户统计
DELIMITER //
CREATE PROCEDURE UpdateUserStats(IN user_id BIGINT)
BEGIN
    DECLARE dynamic_count INT DEFAULT 0;
    DECLARE total_likes INT DEFAULT 0;
    
    -- 计算动态数量
    SELECT COUNT(*) INTO dynamic_count 
    FROM dynamics 
    WHERE user_id = user_id AND is_deleted = FALSE;
    
    -- 计算总点赞数
    SELECT COUNT(*) INTO total_likes
    FROM dynamic_likes dl
    JOIN dynamics d ON dl.dynamic_id = d.id
    WHERE d.user_id = user_id AND d.is_deleted = FALSE;
    
    -- 更新用户统计
    UPDATE users 
    SET 
        follower_count = (SELECT COUNT(*) FROM follows WHERE following_id = user_id),
        like_count = total_likes
    WHERE id = user_id;
END //
DELIMITER ;

-- 创建触发器 - 动态点赞后更新统计
DELIMITER //
CREATE TRIGGER tr_dynamic_like_after_insert
AFTER INSERT ON dynamic_likes
FOR EACH ROW
BEGIN
    UPDATE dynamics 
    SET like_count = like_count + 1 
    WHERE id = NEW.dynamic_id;
    
    CALL UpdateUserStats((SELECT user_id FROM dynamics WHERE id = NEW.dynamic_id));
END //
DELIMITER ;

-- 创建触发器 - 动态点赞删除后更新统计
DELIMITER //
CREATE TRIGGER tr_dynamic_like_after_delete
AFTER DELETE ON dynamic_likes
FOR EACH ROW
BEGIN
    UPDATE dynamics 
    SET like_count = GREATEST(like_count - 1, 0) 
    WHERE id = OLD.dynamic_id;
    
    CALL UpdateUserStats((SELECT user_id FROM dynamics WHERE id = OLD.dynamic_id));
END //
DELIMITER ;

-- 创建触发器 - 关注后更新统计
DELIMITER //
CREATE TRIGGER tr_follow_after_insert
AFTER INSERT ON follows
FOR EACH ROW
BEGIN
    UPDATE users 
    SET follower_count = follower_count + 1 
    WHERE id = NEW.following_id;
END //
DELIMITER ;

-- 创建触发器 - 取消关注后更新统计
DELIMITER //
CREATE TRIGGER tr_follow_after_delete
AFTER DELETE ON follows
FOR EACH ROW
BEGIN
    UPDATE users 
    SET follower_count = GREATEST(follower_count - 1, 0) 
    WHERE id = OLD.following_id;
END //
DELIMITER ;


-- ==================== 消息系统相关表 ====================

-- 消息表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT,
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    media_url VARCHAR(500),
    media_thumbnail VARCHAR(500),
    media_duration INT,
    media_size BIGINT,
    message_status VARCHAR(20) NOT NULL DEFAULT 'SENDING',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    is_recalled BOOLEAN NOT NULL DEFAULT FALSE,
    send_time DATETIME NOT NULL,
    read_time DATETIME,
    recall_time DATETIME,
    extra_data JSON,
    reply_to_message_id BIGINT,
    forward_from_message_id BIGINT,
    conversation_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_send_time (send_time),
    INDEX idx_message_type (message_type),
    INDEX idx_is_read (is_read),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_sender_receiver_time (sender_id, receiver_id, send_time),
    INDEX idx_receiver_send_time (receiver_id, send_time),
    INDEX idx_conversation_id (conversation_id),
    
    -- 外键约束
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reply_to_message_id) REFERENCES messages(id) ON DELETE SET NULL,
    FOREIGN KEY (forward_from_message_id) REFERENCES messages(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 会话表
CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    last_message_id BIGINT,
    last_message_content TEXT,
    last_message_time DATETIME,
    unread_count_user1 INT NOT NULL DEFAULT 0,
    unread_count_user2 INT NOT NULL DEFAULT 0,
    is_pinned_user1 BOOLEAN NOT NULL DEFAULT FALSE,
    is_pinned_user2 BOOLEAN NOT NULL DEFAULT FALSE,
    is_muted_user1 BOOLEAN NOT NULL DEFAULT FALSE,
    is_muted_user2 BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted_user1 BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted_user2 BOOLEAN NOT NULL DEFAULT FALSE,
    conversation_type VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    conversation_name VARCHAR(100),
    conversation_avatar VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_user1_id (user1_id),
    INDEX idx_user2_id (user2_id),
    INDEX idx_last_message_time (last_message_time),
    INDEX idx_conversation_type (conversation_type),
    INDEX idx_user1_last_time (user1_id, last_message_time),
    INDEX idx_user2_last_time (user2_id, last_message_time),
    
    -- 外键约束
    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (last_message_id) REFERENCES messages(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户在线状态表
CREATE TABLE IF NOT EXISTS user_online_status (
    user_id BIGINT PRIMARY KEY,
    is_online BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
    last_seen DATETIME NOT NULL,
    device_type VARCHAR(50),
    device_id VARCHAR(100),
    app_version VARCHAR(20),
    os_version VARCHAR(20),
    network_type VARCHAR(20),
    ip_address VARCHAR(45),
    location VARCHAR(100),
    latitude DOUBLE,
    longitude DOUBLE,
    battery_level INT,
    is_charging BOOLEAN NOT NULL DEFAULT FALSE,
    screen_on BOOLEAN NOT NULL DEFAULT FALSE,
    in_call BOOLEAN NOT NULL DEFAULT FALSE,
    in_video_call BOOLEAN NOT NULL DEFAULT FALSE,
    do_not_disturb BOOLEAN NOT NULL DEFAULT FALSE,
    quiet_hours_start VARCHAR(10),
    quiet_hours_end VARCHAR(10),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_is_online (is_online),
    INDEX idx_status (status),
    INDEX idx_last_seen (last_seen),
    INDEX idx_device_type (device_type),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 通话记录表
CREATE TABLE IF NOT EXISTS call_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    caller_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    call_type VARCHAR(20) NOT NULL DEFAULT 'VOICE',
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    duration INT NOT NULL DEFAULT 0,
    call_status VARCHAR(20) NOT NULL DEFAULT 'INITIATED',
    is_missed BOOLEAN NOT NULL DEFAULT FALSE,
    is_answered BOOLEAN NOT NULL DEFAULT FALSE,
    is_rejected BOOLEAN NOT NULL DEFAULT FALSE,
    call_price INT NOT NULL DEFAULT 0,
    total_cost INT NOT NULL DEFAULT 0,
    quality_score DECIMAL(3,2),
    network_quality VARCHAR(20),
    caller_device_type VARCHAR(50),
    receiver_device_type VARCHAR(50),
    caller_location VARCHAR(100),
    receiver_location VARCHAR(100),
    call_notes TEXT,
    is_recorded BOOLEAN NOT NULL DEFAULT FALSE,
    recording_url VARCHAR(500),
    recording_duration INT,
    is_deleted_caller BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted_receiver BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_caller_id (caller_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_call_type (call_type),
    INDEX idx_start_time (start_time),
    INDEX idx_is_missed (is_missed),
    INDEX idx_is_answered (is_answered),
    INDEX idx_caller_receiver_time (caller_id, receiver_id, start_time),
    
    -- 外键约束
    FOREIGN KEY (caller_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 消息已读状态表
CREATE TABLE IF NOT EXISTS message_read_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_time DATETIME,
    device_type VARCHAR(50),
    ip_address VARCHAR(45),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_message_id (message_id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    UNIQUE KEY uk_message_user (message_id, user_id),
    
    -- 外键约束
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 消息媒体文件表
CREATE TABLE IF NOT EXISTS message_media (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    file_name VARCHAR(255),
    file_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    file_size BIGINT,
    duration INT,
    width INT,
    height INT,
    mime_type VARCHAR(100),
    file_hash VARCHAR(64),
    is_compressed BOOLEAN NOT NULL DEFAULT FALSE,
    compression_ratio DECIMAL(5,2),
    upload_progress INT NOT NULL DEFAULT 0,
    upload_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    upload_time DATETIME,
    expire_time DATETIME,
    download_count INT NOT NULL DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_message_id (message_id),
    INDEX idx_media_type (media_type),
    INDEX idx_upload_status (upload_status),
    INDEX idx_file_hash (file_hash),
    
    -- 外键约束
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户关系表
CREATE TABLE IF NOT EXISTS user_relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    relationship_type VARCHAR(20) NOT NULL,
    is_mutual BOOLEAN NOT NULL DEFAULT FALSE,
    initiated_by BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    intimacy_score INT NOT NULL DEFAULT 0,
    chat_frequency INT NOT NULL DEFAULT 0,
    last_chat_time DATETIME,
    last_interaction_time DATETIME,
    interaction_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    call_count INT NOT NULL DEFAULT 0,
    call_duration INT NOT NULL DEFAULT 0,
    gift_count INT NOT NULL DEFAULT 0,
    gift_value INT NOT NULL DEFAULT 0,
    notes TEXT,
    tags VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_user1_id (user1_id),
    INDEX idx_user2_id (user2_id),
    INDEX idx_relationship_type (relationship_type),
    INDEX idx_intimacy_score (intimacy_score),
    INDEX idx_last_chat_time (last_chat_time),
    UNIQUE KEY uk_user1_user2_type (user1_id, user2_id, relationship_type),
    
    -- 外键约束
    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (initiated_by) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
