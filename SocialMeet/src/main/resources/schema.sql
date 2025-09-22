-- 动态表
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
    INDEX idx_user_id (user_id),
    INDEX idx_publish_time (publish_time),
    INDEX idx_like_count (like_count),
    INDEX idx_status (status),
    INDEX idx_is_deleted (is_deleted)
);

-- 动态点赞表
CREATE TABLE IF NOT EXISTS dynamic_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_dynamic_user (dynamic_id, user_id),
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (dynamic_id) REFERENCES dynamics(id) ON DELETE CASCADE
);

-- 动态评论表
CREATE TABLE IF NOT EXISTS dynamic_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_is_deleted (is_deleted),
    FOREIGN KEY (dynamic_id) REFERENCES dynamics(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES dynamic_comments(id) ON DELETE CASCADE
);

-- 关注关系表（如果不存在）
CREATE TABLE IF NOT EXISTS follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    INDEX idx_follower_id (follower_id),
    INDEX idx_following_id (following_id)
);