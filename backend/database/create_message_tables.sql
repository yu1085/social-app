-- 消息表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type ENUM('TEXT', 'IMAGE', 'VOICE', 'VIDEO', 'SYSTEM') DEFAULT 'TEXT' COMMENT '消息类型',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sender_receiver (sender_id, receiver_id),
    INDEX idx_receiver_read (receiver_id, is_read),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 通话记录表
CREATE TABLE IF NOT EXISTS call_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64) UNIQUE NOT NULL COMMENT '会话ID',
    caller_id BIGINT NOT NULL COMMENT '主叫ID',
    callee_id BIGINT NOT NULL COMMENT '被叫ID',
    call_type ENUM('VOICE', 'VIDEO') DEFAULT 'VIDEO' COMMENT '通话类型',
    call_status ENUM('RINGING', 'CONNECTED', 'ENDED', 'CANCELLED', 'MISSED') DEFAULT 'RINGING' COMMENT '通话状态',
    duration INT DEFAULT 0 COMMENT '通话时长(秒)',
    price_per_min DECIMAL(10, 2) DEFAULT 0.00 COMMENT '每分钟价格',
    total_cost DECIMAL(10, 2) DEFAULT 0.00 COMMENT '总费用',
    start_time TIMESTAMP NULL COMMENT '开始时间',
    end_time TIMESTAMP NULL COMMENT '结束时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_caller (caller_id),
    INDEX idx_callee (callee_id),
    INDEX idx_session (session_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (caller_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (callee_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通话记录表';

-- 用户关系表
CREATE TABLE IF NOT EXISTS user_relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_user_id BIGINT NOT NULL COMMENT '目标用户ID',
    relationship_type ENUM('FRIEND', 'LIKE', 'INTIMATE') NOT NULL COMMENT '关系类型',
    intimacy_score INT DEFAULT 0 COMMENT '亲密度分数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_target_type (user_id, target_user_id, relationship_type),
    INDEX idx_user_type (user_id, relationship_type),
    INDEX idx_target (target_user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (target_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关系表';

-- 消息配额表（记录用户每日免费消息使用情况）
CREATE TABLE IF NOT EXISTS message_quotas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    quota_date DATE NOT NULL COMMENT '配额日期',
    used_count INT DEFAULT 0 COMMENT '已使用数量',
    free_limit INT DEFAULT 3 COMMENT '免费限额',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_date (user_id, quota_date),
    INDEX idx_user_date (user_id, quota_date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息配额表';
