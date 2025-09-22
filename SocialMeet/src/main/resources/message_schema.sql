-- 消息相关数据库表结构
-- 用于支持完整的私信消息功能

-- 消息表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    media_url VARCHAR(500),
    media_thumbnail VARCHAR(500),
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    is_recalled BOOLEAN NOT NULL DEFAULT FALSE,
    send_time DATETIME NOT NULL,
    read_time DATETIME,
    recall_time DATETIME,
    extra_data JSON,
    reply_to_message_id BIGINT,
    forward_from_message_id BIGINT,
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
    
    -- 外键约束
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reply_to_message_id) REFERENCES messages(id) ON DELETE SET NULL,
    FOREIGN KEY (forward_from_message_id) REFERENCES messages(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 会话表
CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    other_user_id BIGINT NOT NULL,
    conversation_type VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    last_message_id BIGINT,
    last_message_content TEXT,
    last_message_time DATETIME,
    last_message_type VARCHAR(20),
    unread_count INT NOT NULL DEFAULT 0,
    total_message_count INT NOT NULL DEFAULT 0,
    is_pinned BOOLEAN NOT NULL DEFAULT FALSE,
    is_muted BOOLEAN NOT NULL DEFAULT FALSE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    extra_data JSON,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_other_user_id (other_user_id),
    INDEX idx_conversation_type (conversation_type),
    INDEX idx_last_message_time (last_message_time),
    INDEX idx_unread_count (unread_count),
    INDEX idx_is_pinned (is_pinned),
    INDEX idx_is_muted (is_muted),
    INDEX idx_is_deleted (is_deleted),
    INDEX idx_user_other (user_id, other_user_id),
    INDEX idx_user_pinned_time (user_id, is_pinned, last_message_time),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (other_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (last_message_id) REFERENCES messages(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户在线状态表
CREATE TABLE IF NOT EXISTS user_online_status (
    user_id BIGINT PRIMARY KEY,
    is_online BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
    last_seen DATETIME NOT NULL,
    device_type VARCHAR(50),
    app_version VARCHAR(20),
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
    is_missed BOOLEAN NOT NULL DEFAULT FALSE,
    is_answered BOOLEAN NOT NULL DEFAULT FALSE,
    call_price INT NOT NULL DEFAULT 0,
    total_cost INT NOT NULL DEFAULT 0,
    quality_score DECIMAL(3,2),
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

-- 消息已读状态表（用于群聊等复杂场景）
CREATE TABLE IF NOT EXISTS message_read_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    read_time DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 唯一约束
    UNIQUE KEY uk_message_user (message_id, user_id),
    
    -- 索引
    INDEX idx_message_id (message_id),
    INDEX idx_user_id (user_id),
    INDEX idx_read_time (read_time),
    
    -- 外键约束
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 消息媒体文件表
CREATE TABLE IF NOT EXISTS message_media (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    file_size BIGINT,
    duration INT,
    width INT,
    height INT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_message_id (message_id),
    INDEX idx_media_type (media_type),
    INDEX idx_file_url (file_url),
    
    -- 外键约束
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建视图 - 会话统计视图
CREATE OR REPLACE VIEW conversation_stats AS
SELECT 
    c.id,
    c.user_id,
    c.other_user_id,
    c.conversation_type,
    c.last_message_content,
    c.last_message_time,
    c.last_message_type,
    c.unread_count,
    c.total_message_count,
    c.is_pinned,
    c.is_muted,
    c.is_deleted,
    u.nickname as other_user_nickname,
    u.avatar as other_user_avatar,
    u.is_online as other_user_online,
    u.status as other_user_status,
    u.last_seen as other_user_last_seen,
    c.created_at,
    c.updated_at
FROM conversations c
LEFT JOIN users u ON c.other_user_id = u.id
WHERE c.is_deleted = FALSE;

-- 创建视图 - 消息统计视图
CREATE OR REPLACE VIEW message_stats AS
SELECT 
    m.id,
    m.sender_id,
    m.receiver_id,
    m.content,
    m.message_type,
    m.media_url,
    m.is_read,
    m.is_deleted,
    m.is_recalled,
    m.send_time,
    m.read_time,
    m.recall_time,
    s.nickname as sender_nickname,
    s.avatar as sender_avatar,
    r.nickname as receiver_nickname,
    r.avatar as receiver_avatar,
    m.created_at,
    m.updated_at
FROM messages m
LEFT JOIN users s ON m.sender_id = s.id
LEFT JOIN users r ON m.receiver_id = r.id
WHERE m.is_deleted = FALSE;

-- 创建存储过程 - 更新会话最后消息
DELIMITER //
CREATE PROCEDURE UpdateConversationLastMessage(
    IN p_user_id BIGINT,
    IN p_other_user_id BIGINT,
    IN p_message_id BIGINT
)
BEGIN
    DECLARE v_message_content TEXT;
    DECLARE v_message_time DATETIME;
    DECLARE v_message_type VARCHAR(20);
    
    -- 获取消息信息
    SELECT content, send_time, message_type 
    INTO v_message_content, v_message_time, v_message_type
    FROM messages 
    WHERE id = p_message_id;
    
    -- 更新或插入会话记录
    INSERT INTO conversations (
        user_id, other_user_id, last_message_id, 
        last_message_content, last_message_time, last_message_type,
        total_message_count, unread_count
    ) VALUES (
        p_user_id, p_other_user_id, p_message_id,
        v_message_content, v_message_time, v_message_type,
        1, 0
    ) ON DUPLICATE KEY UPDATE
        last_message_id = p_message_id,
        last_message_content = v_message_content,
        last_message_time = v_message_time,
        last_message_type = v_message_type,
        total_message_count = total_message_count + 1,
        unread_count = CASE 
            WHEN p_user_id != user_id THEN unread_count + 1 
            ELSE unread_count 
        END,
        updated_at = CURRENT_TIMESTAMP;
END //
DELIMITER ;

-- 创建触发器 - 消息插入后更新会话
DELIMITER //
CREATE TRIGGER tr_message_insert_after
AFTER INSERT ON messages
FOR EACH ROW
BEGIN
    -- 更新发送者的会话
    CALL UpdateConversationLastMessage(NEW.sender_id, NEW.receiver_id, NEW.id);
    
    -- 更新接收者的会话
    CALL UpdateConversationLastMessage(NEW.receiver_id, NEW.sender_id, NEW.id);
END //
DELIMITER ;

-- 创建触发器 - 消息删除后更新会话
DELIMITER //
CREATE TRIGGER tr_message_delete_after
AFTER UPDATE ON messages
FOR EACH ROW
BEGIN
    -- 如果消息被删除，需要更新会话的最后消息
    IF NEW.is_deleted = TRUE AND OLD.is_deleted = FALSE THEN
        -- 这里可以添加逻辑来更新会话的最后消息为前一条消息
        -- 简化处理：暂时不处理
    END IF;
END //
DELIMITER ;
