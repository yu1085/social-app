-- SocialMeet数据库初始化脚本
CREATE DATABASE IF NOT EXISTS socialmeet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE socialmeet;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    avatar_url VARCHAR(500),
    is_online BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone_number),
    INDEX idx_username (username)
);

-- 用户资料表
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    nickname VARCHAR(50),
    bio TEXT,
    gender ENUM('男', '女', '其他') DEFAULT '其他',
    birth_date DATE,
    age INT,
    height INT,
    weight INT,
    education VARCHAR(50),
    income VARCHAR(50),
    occupation VARCHAR(100),
    hometown VARCHAR(100),
    city VARCHAR(100),
    living_situation VARCHAR(100),
    property_ownership VARCHAR(100),
    car_ownership VARCHAR(100),
    signature TEXT,
    constellation VARCHAR(20),
    tags JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- 认证记录表
CREATE TABLE IF NOT EXISTS verification_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    verification_type ENUM('REALNAME', 'PHONE', 'FACE') NOT NULL,
    verification_data JSON,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    result_data JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_verification (user_id, verification_type)
);

-- 钱包表
CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    frozen_balance DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_wallet (user_id)
);

-- 交易记录表
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    transaction_type ENUM('RECHARGE', 'CONSUME', 'REFUND', 'WITHDRAW') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    balance_after DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    related_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_transactions (user_id, created_at)
);

-- 通话记录表
CREATE TABLE IF NOT EXISTS call_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    caller_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    call_type ENUM('VOICE', 'VIDEO') NOT NULL,
    duration INT DEFAULT 0,
    cost DECIMAL(10,2) DEFAULT 0.00,
    status ENUM('INITIATED', 'RINGING', 'CONNECTED', 'ENDED', 'MISSED', 'REJECTED') DEFAULT 'INITIATED',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (caller_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_caller_calls (caller_id, started_at),
    INDEX idx_receiver_calls (receiver_id, started_at)
);

-- 匹配记录表
CREATE TABLE IF NOT EXISTS match_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    requester_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    message TEXT,
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (target_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_requester_matches (requester_id, status),
    INDEX idx_target_matches (target_id, status)
);

-- 图片表
CREATE TABLE IF NOT EXISTS images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    image_type ENUM('AVATAR', 'IDCARD', 'FACE', 'ALBUM') NOT NULL,
    file_size BIGINT,
    width INT,
    height INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_images (user_id, image_type)
);

-- 插入默认管理员用户
INSERT INTO users (username, phone_number, password_hash, email, is_verified) VALUES 
('admin', '13800138000', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'admin@socialmeet.com', TRUE)
ON DUPLICATE KEY UPDATE username=username;

-- 为管理员创建钱包
INSERT INTO wallets (user_id, balance) 
SELECT id, 1000.00 FROM users WHERE username = 'admin'
ON DUPLICATE KEY UPDATE balance = balance;
