#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import sys

def create_database_and_update():
    """创建数据库并更新用户86945008的余额为888"""
    
    # 数据库连接配置（不指定数据库）
    config = {
        'host': 'localhost',
        'user': 'root',
        'password': '123456',
        'charset': 'utf8mb4'
    }
    
    try:
        # 连接MySQL服务器
        connection = pymysql.connect(**config)
        cursor = connection.cursor()
        
        print("正在连接MySQL服务器...")
        
        # 1. 创建数据库
        cursor.execute("CREATE DATABASE IF NOT EXISTS socialmeet CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
        print("✓ 数据库 socialmeet 已创建或已存在")
        
        # 2. 选择数据库
        cursor.execute("USE socialmeet")
        print("✓ 已切换到 socialmeet 数据库")
        
        # 3. 创建用户表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                phone VARCHAR(20) UNIQUE,
                nickname VARCHAR(100),
                gender ENUM('MALE', 'FEMALE'),
                is_active BOOLEAN DEFAULT TRUE,
                is_online BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
        """)
        print("✓ 用户表已创建或已存在")
        
        # 4. 创建钱包表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS wallets (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id BIGINT UNIQUE NOT NULL,
                balance DECIMAL(10,2) DEFAULT 0.00,
                frozen_amount DECIMAL(10,2) DEFAULT 0.00,
                currency VARCHAR(10) DEFAULT 'CNY',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """)
        print("✓ 钱包表已创建或已存在")
        
        # 5. 创建交易记录表
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS transactions (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id BIGINT NOT NULL,
                type VARCHAR(20) NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                balance_after DECIMAL(10,2) NOT NULL,
                description TEXT,
                status VARCHAR(20) DEFAULT 'SUCCESS',
                related_id BIGINT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """)
        print("✓ 交易记录表已创建或已存在")
        
        # 6. 确保用户存在
        user_id = 86945008
        cursor.execute("""
            INSERT INTO users (id, username, phone, nickname, gender, is_active, is_online, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, NOW(), NOW())
            ON DUPLICATE KEY UPDATE username = username
        """, (user_id, f'user_{user_id}', '13800138008', '酷炫小仙女520', 'FEMALE', True, True))
        
        print(f"✓ 用户 {user_id} 已确保存在")
        
        # 7. 创建或更新钱包
        cursor.execute("""
            INSERT INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at)
            VALUES (%s, %s, %s, %s, NOW(), NOW())
            ON DUPLICATE KEY UPDATE 
                balance = %s, 
                updated_at = NOW()
        """, (user_id, 888.00, 0.00, 'CNY', 888.00))
        
        print(f"✓ 用户 {user_id} 的余额已设置为 888.00 CNY")
        
        # 8. 添加充值交易记录
        cursor.execute("""
            INSERT INTO transactions (user_id, type, amount, balance_after, description, status, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, NOW(), NOW())
        """, (user_id, 'RECHARGE', 888.00, 888.00, f'给用户{user_id}充值888元', 'SUCCESS'))
        
        print(f"✓ 已添加充值交易记录")
        
        # 9. 查询验证结果
        cursor.execute("""
            SELECT user_id, balance, frozen_amount, currency, updated_at 
            FROM wallets 
            WHERE user_id = %s
        """, (user_id,))
        
        result = cursor.fetchone()
        if result:
            print(f"\n📊 钱包信息验证:")
            print(f"   用户ID: {result[0]}")
            print(f"   余额: {result[1]} {result[3]}")
            print(f"   冻结金额: {result[2]} {result[3]}")
            print(f"   更新时间: {result[4]}")
        
        # 提交事务
        connection.commit()
        print(f"\n✅ 数据库更新成功！用户 {user_id} 的余额已设置为 888.00 CNY")
        
    except Exception as e:
        print(f"❌ 数据库操作失败: {e}")
        if 'connection' in locals():
            connection.rollback()
        sys.exit(1)
        
    finally:
        if 'connection' in locals():
            connection.close()
            print("数据库连接已关闭")

if __name__ == "__main__":
    create_database_and_update()
