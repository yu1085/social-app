-- 动态/帖子表
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '发布者用户ID',
    content TEXT NOT NULL COMMENT '动态内容',
    images JSON COMMENT '图片URL列表(JSON格式)',
    location VARCHAR(255) COMMENT '位置信息',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    is_free_minute BOOLEAN DEFAULT FALSE COMMENT '是否为一分钟免费动态',
    status ENUM('PUBLISHED', 'DELETED', 'HIDDEN') DEFAULT 'PUBLISHED' COMMENT '动态状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_like_count (like_count),
    INDEX idx_free_minute (is_free_minute),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态/帖子表';
