-- ================================================================
-- SocialMeet 社交应用数据库初始化脚本
-- 数据库: socialmeet
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ================================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS socialmeet
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE socialmeet;

-- ================================================================
-- 用户表 (users)
-- 存储用户基本信息和账号信息
-- ================================================================
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，唯一',
  `phone` VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号，唯一',
  `password` VARCHAR(255) DEFAULT NULL COMMENT '密码（加密），可为空（支持验证码登录）',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `gender` ENUM('MALE', 'FEMALE', 'OTHER') DEFAULT NULL COMMENT '性别',
  `birthday` DATE DEFAULT NULL COMMENT '生日',
  `constellation` VARCHAR(20) DEFAULT NULL COMMENT '星座',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '所在地',
  `height` INT DEFAULT NULL COMMENT '身高（厘米）',
  `weight` INT DEFAULT NULL COMMENT '体重（公斤）',
  `income_level` VARCHAR(50) DEFAULT NULL COMMENT '收入水平',
  `education` VARCHAR(50) DEFAULT NULL COMMENT '学历',
  `marital_status` VARCHAR(20) DEFAULT NULL COMMENT '婚姻状况',
  `signature` VARCHAR(200) DEFAULT NULL COMMENT '个性签名',
  `is_verified` BOOLEAN DEFAULT FALSE COMMENT '是否实名认证',
  `is_vip` BOOLEAN DEFAULT FALSE COMMENT '是否VIP',
  `vip_level` INT DEFAULT 0 COMMENT 'VIP等级',
  `vip_expire_at` DATETIME DEFAULT NULL COMMENT 'VIP过期时间',
  `wealth_level` INT DEFAULT 0 COMMENT '财富等级',
  `balance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
  `is_online` BOOLEAN DEFAULT FALSE COMMENT '是否在线',
  `last_active_at` DATETIME DEFAULT NULL COMMENT '最后活跃时间',
  `status` ENUM('ACTIVE', 'BANNED', 'DELETED') DEFAULT 'ACTIVE' COMMENT '账号状态',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_phone` (`phone`),
  INDEX `idx_username` (`username`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ================================================================
-- 验证码表 (verification_codes)
-- 存储短信验证码
-- ================================================================
CREATE TABLE IF NOT EXISTS `verification_codes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '验证码ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `code` VARCHAR(10) NOT NULL COMMENT '验证码',
  `type` ENUM('LOGIN', 'REGISTER', 'RESET_PASSWORD', 'CHANGE_PHONE') DEFAULT 'LOGIN' COMMENT '验证码类型',
  `is_used` BOOLEAN DEFAULT FALSE COMMENT '是否已使用',
  `expired_at` DATETIME NOT NULL COMMENT '过期时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_phone_code` (`phone`, `code`),
  INDEX `idx_expired_at` (`expired_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码表';

-- ================================================================
-- 用户认证表 (user_authentications)
-- 存储用户的多种认证信息
-- ================================================================
CREATE TABLE IF NOT EXISTS `user_authentications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '认证ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `auth_type` ENUM('ID_CARD', 'PHONE', 'FACE', 'ALIPAY') NOT NULL COMMENT '认证类型',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `id_card_number` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
  `auth_status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING' COMMENT '认证状态',
  `auth_data` TEXT DEFAULT NULL COMMENT '认证数据（JSON格式）',
  `verified_at` DATETIME DEFAULT NULL COMMENT '认证通过时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_auth_type` (`auth_type`),
  INDEX `idx_auth_status` (`auth_status`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户认证表';

-- ================================================================
-- 刷新令牌表 (refresh_tokens)
-- 存储JWT刷新令牌
-- ================================================================
CREATE TABLE IF NOT EXISTS `refresh_tokens` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '令牌ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `token` VARCHAR(500) NOT NULL UNIQUE COMMENT '刷新令牌',
  `expired_at` DATETIME NOT NULL COMMENT '过期时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_token` (`token`),
  INDEX `idx_expired_at` (`expired_at`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='刷新令牌表';

-- ================================================================
-- 登录日志表 (login_logs)
-- 记录用户登录历史
-- ================================================================
CREATE TABLE IF NOT EXISTS `login_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `login_type` ENUM('PASSWORD', 'VERIFICATION_CODE', 'PHONE_AUTH', 'THIRD_PARTY') DEFAULT 'VERIFICATION_CODE' COMMENT '登录方式',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `device_info` VARCHAR(200) DEFAULT NULL COMMENT '设备信息',
  `login_status` ENUM('SUCCESS', 'FAILED') DEFAULT 'SUCCESS' COMMENT '登录状态',
  `failure_reason` VARCHAR(200) DEFAULT NULL COMMENT '失败原因',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ================================================================
-- 插入测试数据
-- ================================================================

-- 测试用户1: video_caller (视频发起者)
INSERT INTO `users` (
  `id`, `username`, `phone`, `nickname`, `gender`, `birthday`, `location`,
  `height`, `signature`, `is_verified`, `is_online`, `status`
) VALUES (
  23820512, 'video_caller', '19812342076', 'video_caller', 'MALE', '1993-01-15', '杭州市',
  175, '喜欢音乐和电影，享受简单快乐的生活', TRUE, TRUE, 'ACTIVE'
) ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- 测试用户2: video_receiver (视频接收者)
INSERT INTO `users` (
  `id`, `username`, `phone`, `nickname`, `gender`, `birthday`, `location`,
  `height`, `signature`, `is_verified`, `is_online`, `status`
) VALUES (
  22491729, 'video_receiver', '19887654321', 'video_receiver', 'FEMALE', '1994-03-20', '西安市',
  165, '喜欢音乐和艺术，享受安静美好的时光', TRUE, TRUE, 'ACTIVE'
) ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- 测试用户3: 测试账号
INSERT INTO `users` (
  `username`, `phone`, `nickname`, `gender`, `birthday`, `location`,
  `signature`, `is_verified`, `is_online`, `status`
) VALUES (
  'test_user', '13800138000', '测试用户', 'MALE', '1995-06-01', '北京市',
  '这是一个测试账号', TRUE, FALSE, 'ACTIVE'
) ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- ================================================================
-- 数据库视图和存储过程（可选）
-- ================================================================

-- 创建用户活跃度视图
CREATE OR REPLACE VIEW `v_user_active_status` AS
SELECT
  u.id,
  u.username,
  u.nickname,
  u.phone,
  u.is_online,
  u.last_active_at,
  CASE
    WHEN u.last_active_at IS NULL THEN '从未登录'
    WHEN TIMESTAMPDIFF(MINUTE, u.last_active_at, NOW()) <= 5 THEN '在线'
    WHEN TIMESTAMPDIFF(HOUR, u.last_active_at, NOW()) <= 24 THEN '最近活跃'
    WHEN TIMESTAMPDIFF(DAY, u.last_active_at, NOW()) <= 7 THEN '一周内活跃'
    ELSE '不活跃'
  END AS activity_status,
  u.created_at,
  u.status
FROM users u;

-- ================================================================
-- 完成提示
-- ================================================================
SELECT '数据库初始化完成！' AS message;
SELECT COUNT(*) AS user_count FROM users;
