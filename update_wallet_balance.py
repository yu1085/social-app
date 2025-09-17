#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import sys

def update_wallet_balance():
    """更新用户86945008的余额为888"""
    
    # 数据库连接配置
    config = {
        'host': 'localhost',
        'user': 'root',
        'password': '123456',
        'database': 'socialmeet',
        'charset': 'utf8mb4'
    }
    
    try:
        # 连接数据库
        connection = pymysql.connect(**config)
        cursor = connection.cursor()
        
        print("正在连接数据库...")
        
        # 1. 确保用户存在
        user_id = 86945008
        cursor.execute("""
            INSERT INTO users (id, username, phone, nickname, gender, is_active, is_online, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, NOW(), NOW())
            ON DUPLICATE KEY UPDATE username = username
        """, (user_id, f'user_{user_id}', '13800138008', '酷炫小仙女520', 'FEMALE', True, True))
        
        print(f"✓ 用户 {user_id} 已确保存在")
        
        # 2. 创建或更新钱包
        cursor.execute("""
            INSERT INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at)
            VALUES (%s, %s, %s, %s, NOW(), NOW())
            ON DUPLICATE KEY UPDATE 
                balance = %s, 
                updated_at = NOW()
        """, (user_id, 888.00, 0.00, 'CNY', 888.00))
        
        print(f"✓ 用户 {user_id} 的余额已设置为 888.00 CNY")
        
        # 3. 添加充值交易记录
        cursor.execute("""
            INSERT INTO transactions (user_id, type, amount, balance_after, description, status, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, NOW(), NOW())
        """, (user_id, 'RECHARGE', 888.00, 888.00, f'给用户{user_id}充值888元', 'SUCCESS'))
        
        print(f"✓ 已添加充值交易记录")
        
        # 4. 查询验证结果
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
    update_wallet_balance()
