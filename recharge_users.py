#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
用户充值脚本
给指定用户ID充值10000
"""

import pymysql
import sys
from decimal import Decimal
from datetime import datetime

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'socialmeet',
    'charset': 'utf8mb4',
    'autocommit': False
}

# 要充值的用户ID和金额
USER_IDS = [65899032, 44479883]
RECHARGE_AMOUNT = Decimal('10000.00')

def connect_database():
    """连接数据库"""
    try:
        connection = pymysql.connect(**DB_CONFIG)
        print("✓ 数据库连接成功")
        return connection
    except Exception as e:
        print(f"✗ 数据库连接失败: {e}")
        sys.exit(1)

def ensure_user_exists(cursor, user_id):
    """确保用户存在"""
    cursor.execute("SELECT COUNT(*) FROM users WHERE id = %s", (user_id,))
    if cursor.fetchone()[0] == 0:
        cursor.execute("""
            INSERT INTO users (id, username, password, nickname, is_active, created_at, updated_at) 
            VALUES (%s, %s, %s, %s, %s, NOW(), NOW())
        """, (user_id, f'user_{user_id}', 'default_password', f'用户{user_id}', True))
        print(f"  - 创建用户: {user_id}")

def ensure_wallet_exists(cursor, user_id):
    """确保钱包存在"""
    cursor.execute("SELECT COUNT(*) FROM wallets WHERE user_id = %s", (user_id,))
    if cursor.fetchone()[0] == 0:
        cursor.execute("""
            INSERT INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at) 
            VALUES (%s, 0.00, 0.00, 'CNY', NOW(), NOW())
        """, (user_id,))
        print(f"  - 创建钱包: {user_id}")

def recharge_user(cursor, user_id, amount):
    """给用户充值"""
    print(f"正在为用户 {user_id} 充值 {amount}...")
    
    # 确保用户和钱包存在
    ensure_user_exists(cursor, user_id)
    ensure_wallet_exists(cursor, user_id)
    
    # 更新钱包余额
    cursor.execute("""
        UPDATE wallets 
        SET balance = balance + %s, updated_at = NOW() 
        WHERE user_id = %s
    """, (amount, user_id))
    
    if cursor.rowcount == 0:
        raise Exception(f"更新钱包失败，用户ID: {user_id}")
    
    # 记录交易
    cursor.execute("""
        INSERT INTO transactions (user_id, type, amount, balance_after, description, status, created_at) 
        SELECT %s, 'RECHARGE', %s, balance, '管理员充值', 'SUCCESS', NOW() 
        FROM wallets WHERE user_id = %s
    """, (user_id, amount, user_id))
    
    print(f"✓ 用户 {user_id} 充值成功！")

def query_user_balances(cursor, user_ids):
    """查询用户余额"""
    print("\n" + "="*60)
    print("充值后的用户余额:")
    print("="*60)
    print(f"{'用户ID':<12} {'用户名':<20} {'余额':<12} {'货币':<8} {'更新时间'}")
    print("-"*60)
    
    placeholders = ','.join(['%s'] * len(user_ids))
    cursor.execute(f"""
        SELECT u.id, u.username, u.nickname, w.balance, w.currency, w.updated_at 
        FROM users u 
        JOIN wallets w ON u.id = w.user_id 
        WHERE u.id IN ({placeholders}) 
        ORDER BY u.id
    """, user_ids)
    
    for row in cursor.fetchall():
        user_id, username, nickname, balance, currency, updated_at = row
        print(f"{user_id:<12} {username:<20} {balance:<12} {currency:<8} {updated_at}")

def main():
    """主函数"""
    print("="*60)
    print("用户充值脚本")
    print("="*60)
    print(f"充值金额: {RECHARGE_AMOUNT}")
    print(f"目标用户ID: {USER_IDS}")
    print("="*60)
    
    connection = None
    try:
        # 连接数据库
        connection = connect_database()
        cursor = connection.cursor()
        
        # 开始事务
        connection.begin()
        
        # 给每个用户充值
        for user_id in USER_IDS:
            recharge_user(cursor, user_id, RECHARGE_AMOUNT)
        
        # 提交事务
        connection.commit()
        print("\n✓ 所有用户充值操作完成！")
        
        # 查询充值后的余额
        query_user_balances(cursor, USER_IDS)
        
    except Exception as e:
        if connection:
            connection.rollback()
        print(f"\n✗ 充值操作失败: {e}")
        sys.exit(1)
    finally:
        if connection:
            connection.close()
            print("\n✓ 数据库连接已关闭")

if __name__ == "__main__":
    main()
