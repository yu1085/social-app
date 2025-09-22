#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查用户余额脚本
"""

import pymysql
import sys
from decimal import Decimal

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'socialmeet',
    'charset': 'utf8mb4',
    'autocommit': True
}

def check_user_balance(user_id):
    """检查用户余额"""
    try:
        connection = pymysql.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        print(f"检查用户 {user_id} 的详细信息...")
        print("="*60)
        
        # 检查用户是否存在
        cursor.execute("SELECT * FROM users WHERE id = %s", (user_id,))
        user = cursor.fetchone()
        if user:
            print(f"✓ 用户存在: ID={user[0]}, 用户名={user[1]}, 昵称={user[4]}")
        else:
            print(f"✗ 用户不存在: {user_id}")
            return
        
        # 检查钱包信息
        cursor.execute("SELECT * FROM wallets WHERE user_id = %s", (user_id,))
        wallet = cursor.fetchone()
        if wallet:
            print(f"✓ 钱包存在: ID={wallet[0]}, 余额={wallet[2]}, 货币={wallet[4]}")
        else:
            print(f"✗ 钱包不存在: {user_id}")
            return
        
        # 检查最近的交易记录
        cursor.execute("""
            SELECT type, amount, balance_after, description, created_at 
            FROM transactions 
            WHERE user_id = %s 
            ORDER BY created_at DESC 
            LIMIT 10
        """, (user_id,))
        
        transactions = cursor.fetchall()
        print(f"\n最近的交易记录 (共{len(transactions)}条):")
        print("-" * 80)
        print(f"{'类型':<10} {'金额':<12} {'余额':<12} {'描述':<20} {'时间'}")
        print("-" * 80)
        
        for txn in transactions:
            print(f"{txn[0]:<10} {txn[1]:<12} {txn[2]:<12} {txn[3]:<20} {txn[4]}")
        
        # 检查是否有充值记录
        cursor.execute("""
            SELECT COUNT(*), SUM(amount) 
            FROM transactions 
            WHERE user_id = %s AND type = 'RECHARGE'
        """, (user_id,))
        
        recharge_info = cursor.fetchone()
        print(f"\n充值统计:")
        print(f"充值次数: {recharge_info[0]}")
        print(f"充值总额: {recharge_info[1] or 0}")
        
        connection.close()
        
    except Exception as e:
        print(f"检查失败: {e}")

def main():
    user_id = 44479883
    print(f"检查用户 {user_id} 的余额信息")
    check_user_balance(user_id)

if __name__ == "__main__":
    main()
