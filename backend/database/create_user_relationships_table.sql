-- 创建用户关系表
CREATE TABLE IF NOT EXISTS user_relationships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关系ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_user_id BIGINT NOT NULL COMMENT '目标用户ID',
    relationship_type VARCHAR(50) NOT NULL COMMENT '关系类型: FRIEND(知友), LIKE(喜欢), INTIMATE(亲密), BLACKLIST(黑名单), SUBSCRIBE(订阅)',
    intimacy_score INT DEFAULT 0 COMMENT '亲密度分数',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    is_subscribed BOOLEAN DEFAULT FALSE COMMENT '是否订阅状态通知',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_target_type (user_id, target_user_id, relationship_type) COMMENT '防止重复关系',
    INDEX idx_user_id (user_id),
    INDEX idx_target_user_id (target_user_id),
    INDEX idx_relationship_type (relationship_type),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关系表';
