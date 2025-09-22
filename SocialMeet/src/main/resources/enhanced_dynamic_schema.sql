-- 增强版动态系统数据库表结构
-- 支持完整的广场动态功能

-- 动态表（已存在，这里添加新字段）
ALTER TABLE dynamics ADD COLUMN IF NOT EXISTS view_count INT NOT NULL DEFAULT 0;
ALTER TABLE dynamics ADD COLUMN IF NOT EXISTS share_count INT NOT NULL DEFAULT 0;
ALTER TABLE dynamics ADD COLUMN IF NOT EXISTS is_featured BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE dynamics ADD COLUMN IF NOT EXISTS featured_at DATETIME;
ALTER TABLE dynamics ADD COLUMN IF NOT EXISTS last_activity_time DATETIME;

-- 动态举报表
CREATE TABLE IF NOT EXISTS dynamic_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reason VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admin_id BIGINT,
    admin_comment VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME,
    
    -- 索引
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_dynamic_user (dynamic_id, user_id),
    
    -- 外键约束
    FOREIGN KEY (dynamic_id) REFERENCES dynamics(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 动态标签表
CREATE TABLE IF NOT EXISTS dynamic_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id BIGINT NOT NULL,
    tag_name VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 唯一约束
    UNIQUE KEY uk_dynamic_tag (dynamic_id, tag_name),
    
    -- 索引
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_tag_name (tag_name),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (dynamic_id) REFERENCES dynamics(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 动态分享表
CREATE TABLE IF NOT EXISTS dynamic_shares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    share_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL', -- NORMAL, STORY, MOMENT
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_user_id (user_id),
    INDEX idx_share_type (share_type),
    INDEX idx_created_at (created_at),
    INDEX idx_dynamic_user (dynamic_id, user_id),
    
    -- 外键约束
    FOREIGN KEY (dynamic_id) REFERENCES dynamics(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 动态收藏表
CREATE TABLE IF NOT EXISTS dynamic_favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 唯一约束
    UNIQUE KEY uk_dynamic_user (dynamic_id, user_id),
    
    -- 索引
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (dynamic_id) REFERENCES dynamics(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 动态话题表
CREATE TABLE IF NOT EXISTS dynamic_topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    topic_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    cover_image VARCHAR(500),
    participant_count INT NOT NULL DEFAULT 0,
    dynamic_count INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_topic_name (topic_name),
    INDEX idx_participant_count (participant_count),
    INDEX idx_dynamic_count (dynamic_count),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 动态话题关联表
CREATE TABLE IF NOT EXISTS dynamic_topic_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id BIGINT NOT NULL,
    topic_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 唯一约束
    UNIQUE KEY uk_dynamic_topic (dynamic_id, topic_id),
    
    -- 索引
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_topic_id (topic_id),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (dynamic_id) REFERENCES dynamics(id) ON DELETE CASCADE,
    FOREIGN KEY (topic_id) REFERENCES dynamic_topics(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 动态通知表
CREATE TABLE IF NOT EXISTS dynamic_notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    dynamic_id BIGINT,
    type VARCHAR(20) NOT NULL, -- LIKE, COMMENT, SHARE, FOLLOW
    from_user_id BIGINT,
    content TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_dynamic_id (dynamic_id),
    INDEX idx_type (type),
    INDEX idx_from_user_id (from_user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at),
    INDEX idx_user_read (user_id, is_read),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (dynamic_id) REFERENCES dynamics(id) ON DELETE CASCADE,
    FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建视图 - 动态统计视图
CREATE OR REPLACE VIEW dynamic_stats_view AS
SELECT 
    d.id,
    d.user_id,
    d.content,
    d.location,
    d.publish_time,
    d.like_count,
    d.comment_count,
    d.view_count,
    d.share_count,
    d.is_featured,
    d.status,
    d.is_deleted,
    u.nickname as user_nickname,
    u.avatar as user_avatar,
    u.age as user_age,
    u.gender as user_gender,
    u.location as user_location,
    CASE 
        WHEN d.publish_time >= DATE_SUB(NOW(), INTERVAL 1 HOUR) THEN '刚刚'
        WHEN d.publish_time >= DATE_SUB(NOW(), INTERVAL 1 DAY) THEN CONCAT(TIMESTAMPDIFF(HOUR, d.publish_time, NOW()), '小时前')
        WHEN d.publish_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN CONCAT(TIMESTAMPDIFF(DAY, d.publish_time, NOW()), '天前')
        ELSE DATE_FORMAT(d.publish_time, '%m-%d')
    END as time_ago,
    CASE 
        WHEN d.like_count >= 1000 THEN CONCAT(ROUND(d.like_count/1000, 1), 'k')
        ELSE CAST(d.like_count AS CHAR)
    END as like_count_display,
    CASE 
        WHEN d.comment_count >= 1000 THEN CONCAT(ROUND(d.comment_count/1000, 1), 'k')
        ELSE CAST(d.comment_count AS CHAR)
    END as comment_count_display
FROM dynamics d
LEFT JOIN users u ON d.user_id = u.id
WHERE d.status = 'PUBLISHED' AND d.is_deleted = FALSE;

-- 创建视图 - 热门话题统计
CREATE OR REPLACE VIEW trending_topics_view AS
SELECT 
    dt.topic_name,
    dt.description,
    dt.cover_image,
    dt.participant_count,
    dt.dynamic_count,
    COUNT(dtr.dynamic_id) as recent_dynamic_count,
    MAX(d.publish_time) as last_activity_time
FROM dynamic_topics dt
LEFT JOIN dynamic_topic_relations dtr ON dt.id = dtr.topic_id
LEFT JOIN dynamics d ON dtr.dynamic_id = d.id AND d.publish_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
WHERE dt.is_active = TRUE
GROUP BY dt.id, dt.topic_name, dt.description, dt.cover_image, dt.participant_count, dt.dynamic_count
ORDER BY recent_dynamic_count DESC, dt.dynamic_count DESC;

-- 创建存储过程 - 更新动态统计
DELIMITER //
CREATE PROCEDURE UpdateDynamicStats(IN p_dynamic_id BIGINT)
BEGIN
    DECLARE v_like_count INT DEFAULT 0;
    DECLARE v_comment_count INT DEFAULT 0;
    DECLARE v_share_count INT DEFAULT 0;
    
    -- 获取点赞数
    SELECT COUNT(*) INTO v_like_count 
    FROM dynamic_likes 
    WHERE dynamic_id = p_dynamic_id;
    
    -- 获取评论数
    SELECT COUNT(*) INTO v_comment_count 
    FROM dynamic_comments 
    WHERE dynamic_id = p_dynamic_id AND is_deleted = FALSE;
    
    -- 获取分享数
    SELECT COUNT(*) INTO v_share_count 
    FROM dynamic_shares 
    WHERE dynamic_id = p_dynamic_id;
    
    -- 更新动态统计
    UPDATE dynamics 
    SET 
        like_count = v_like_count,
        comment_count = v_comment_count,
        share_count = v_share_count,
        last_activity_time = NOW(),
        updated_at = NOW()
    WHERE id = p_dynamic_id;
END //
DELIMITER ;

-- 创建触发器 - 点赞后更新统计
DELIMITER //
CREATE TRIGGER tr_dynamic_like_after
AFTER INSERT ON dynamic_likes
FOR EACH ROW
BEGIN
    CALL UpdateDynamicStats(NEW.dynamic_id);
END //
DELIMITER ;

-- 创建触发器 - 取消点赞后更新统计
DELIMITER //
CREATE TRIGGER tr_dynamic_unlike_after
AFTER DELETE ON dynamic_likes
FOR EACH ROW
BEGIN
    CALL UpdateDynamicStats(OLD.dynamic_id);
END //
DELIMITER ;

-- 创建触发器 - 评论后更新统计
DELIMITER //
CREATE TRIGGER tr_dynamic_comment_after
AFTER INSERT ON dynamic_comments
FOR EACH ROW
BEGIN
    CALL UpdateDynamicStats(NEW.dynamic_id);
END //
DELIMITER ;

-- 创建触发器 - 分享后更新统计
DELIMITER //
CREATE TRIGGER tr_dynamic_share_after
AFTER INSERT ON dynamic_shares
FOR EACH ROW
BEGIN
    CALL UpdateDynamicStats(NEW.dynamic_id);
END //
DELIMITER ;

-- 创建索引优化查询性能
CREATE INDEX idx_dynamics_status_deleted_publish_time ON dynamics(status, is_deleted, publish_time);
CREATE INDEX idx_dynamics_status_deleted_like_count ON dynamics(status, is_deleted, like_count);
CREATE INDEX idx_dynamics_user_status_deleted ON dynamics(user_id, status, is_deleted);
CREATE INDEX idx_dynamics_location_status ON dynamics(location, status);
CREATE INDEX idx_dynamics_content_fulltext ON dynamics(content(255));

-- 创建全文索引用于内容搜索
ALTER TABLE dynamics ADD FULLTEXT(content) WITH PARSER ngram;

-- 插入默认话题数据
INSERT INTO dynamic_topics (topic_name, description, participant_count, dynamic_count) VALUES
('生活日常', '分享生活中的点点滴滴', 0, 0),
('美食分享', '发现和分享美食', 0, 0),
('旅行见闻', '记录旅途中的美好', 0, 0),
('情感交流', '分享心情和感悟', 0, 0),
('兴趣爱好', '展示个人爱好和技能', 0, 0),
('工作学习', '职场和学习经验分享', 0, 0),
('运动健身', '健康生活方式分享', 0, 0),
('时尚美妆', '时尚搭配和美容心得', 0, 0),
('科技数码', '科技产品和数码生活', 0, 0),
('宠物萌宠', '可爱的宠物日常', 0, 0);
