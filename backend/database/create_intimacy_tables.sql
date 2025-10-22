-- 亲密度系统相关表
USE socialmeet;

-- 1. 亲密度等级配置表
CREATE TABLE IF NOT EXISTS intimacy_levels (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    level INT NOT NULL COMMENT '等级 1-6',
    level_name VARCHAR(50) NOT NULL COMMENT '等级名称: 相遇/相识/相知/相恋/相伴/相守',
    required_temperature INT NOT NULL COMMENT '所需温度(亲密度)',
    reward_type VARCHAR(50) COMMENT '奖励类型: MESSAGE_COUPON/CALL_COUPON/DAILY_CALL_COUPON/FREE_MESSAGE/VIP_MEMBERSHIP',
    reward_value TEXT COMMENT '奖励内容JSON',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='亲密度等级配置表';

-- 2. 用户亲密度记录表
CREATE TABLE IF NOT EXISTS user_intimacy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_user_id BIGINT NOT NULL COMMENT '目标用户ID',
    current_temperature INT DEFAULT 0 COMMENT '当前温度(亲密度值)',
    current_level INT DEFAULT 1 COMMENT '当前等级',

    -- 统计数据
    message_count INT DEFAULT 0 COMMENT '文字消息总数',
    gift_count INT DEFAULT 0 COMMENT '赠送礼物数量',
    video_call_minutes INT DEFAULT 0 COMMENT '视频通话分钟数',
    voice_call_minutes INT DEFAULT 0 COMMENT '语音通话分钟数',
    total_coins_spent BIGINT DEFAULT 0 COMMENT '累计消耗聊币',

    -- 时间记录
    first_interaction_date DATE COMMENT '首次互动日期',
    last_interaction_date DATE COMMENT '最后互动日期',
    days_known INT DEFAULT 0 COMMENT '相识天数',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_user_pair (user_id, target_user_id),
    KEY idx_user_id (user_id),
    KEY idx_target_user_id (target_user_id),
    KEY idx_current_level (current_level),
    KEY idx_current_temperature (current_temperature)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户亲密度记录表';

-- 3. 亲密度变更日志表
CREATE TABLE IF NOT EXISTS intimacy_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_user_id BIGINT NOT NULL COMMENT '目标用户ID',
    action_type VARCHAR(50) NOT NULL COMMENT '行为类型: MESSAGE/GIFT/VIDEO_CALL/VOICE_CALL',
    temperature_change INT NOT NULL COMMENT '温度变化值',
    coins_spent INT NOT NULL COMMENT '消耗聊币数',
    before_temperature INT NOT NULL COMMENT '变化前温度',
    after_temperature INT NOT NULL COMMENT '变化后温度',
    before_level INT NOT NULL COMMENT '变化前等级',
    after_level INT NOT NULL COMMENT '变化后等级',
    level_up BOOLEAN DEFAULT FALSE COMMENT '是否升级',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    KEY idx_user_id (user_id),
    KEY idx_target_user_id (target_user_id),
    KEY idx_created_at (created_at),
    KEY idx_action_type (action_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='亲密度变更日志表';

-- 4. 亲密度奖励发放记录表
CREATE TABLE IF NOT EXISTS intimacy_rewards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_user_id BIGINT NOT NULL COMMENT '目标用户ID',
    level INT NOT NULL COMMENT '等级',
    reward_type VARCHAR(50) NOT NULL COMMENT '奖励类型',
    reward_value TEXT COMMENT '奖励内容JSON',
    is_claimed BOOLEAN DEFAULT FALSE COMMENT '是否已领取',
    claimed_at TIMESTAMP NULL COMMENT '领取时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    KEY idx_user_id (user_id),
    KEY idx_is_claimed (is_claimed),
    KEY idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='亲密度奖励发放记录表';

-- 初始化等级配置数据
INSERT INTO intimacy_levels (level, level_name, required_temperature, reward_type, reward_value) VALUES
(1, '相遇', 3, NULL, NULL),
(2, '相识', 20, 'MESSAGE_COUPON', '{"count": 10}'),
(3, '相知', 600, 'CALL_COUPON', '{"count": 1}'),
(4, '相恋', 2000, 'DAILY_CALL_COUPON', '{"count": 7}'),
(5, '相伴', 30000, 'FREE_MESSAGE', '{}'),
(6, '相守', 300000, 'VIP_MEMBERSHIP', '{"duration": 365}')
ON DUPLICATE KEY UPDATE
    level_name = VALUES(level_name),
    required_temperature = VALUES(required_temperature),
    reward_type = VALUES(reward_type),
    reward_value = VALUES(reward_value);
