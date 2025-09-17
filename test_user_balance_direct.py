#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import json

def test_user_balance_direct():
    """直接查询数据库获取用户 19825012076 的余额"""
    
    print("=== 直接查询数据库获取用户 19825012076 的余额 ===")
    
    config = {
        'host': 'localhost',
        'user': 'root',
        'password': '123456',
        'database': 'socialmeet',
        'charset': 'utf8mb4'
    }
    
    try:
        connection = pymysql.connect(**config)
        cursor = connection.cursor()
        
        # 1. 查找用户信息
        print("1. 查找用户信息...")
        cursor.execute("""
            SELECT id, username, nickname, phone, created_at 
            FROM users 
            WHERE phone = '19825012076' OR username = 'user_19825012076'
        """)
        user_rows = cursor.fetchall()
        
        if user_rows:
            user = user_rows[0]
            user_id, username, nickname, phone, created_at = user
            print(f"✅ 找到用户:")
            print(f"   ID: {user_id}")
            print(f"   用户名: {username}")
            print(f"   昵称: {nickname}")
            print(f"   手机号: {phone}")
            print(f"   创建时间: {created_at}")
            
            # 2. 查找用户的钱包信息
            print(f"\n2. 查找用户 {user_id} 的钱包信息...")
            cursor.execute("""
                SELECT id, user_id, balance, created_at, updated_at
                FROM wallets 
                WHERE user_id = %s
            """, (user_id,))
            wallet_rows = cursor.fetchall()
            
            if wallet_rows:
                wallet = wallet_rows[0]
                wallet_id, wallet_user_id, balance, created_at, updated_at = wallet
                print(f"✅ 找到钱包信息:")
                print(f"   钱包ID: {wallet_id}")
                print(f"   用户ID: {wallet_user_id}")
                print(f"   余额: {balance}")
                print(f"   创建时间: {created_at}")
                print(f"   更新时间: {updated_at}")
                
                print(f"\n💰 用户 19825012076 的余额: {balance}")
                
                # 显示余额详情
                if balance is not None:
                    print(f"   余额类型: {type(balance)}")
                    print(f"   余额值: {balance}")
                    if isinstance(balance, (int, float)):
                        print(f"   格式化余额: {balance:,.2f}")
                else:
                    print("   ❌ 余额为空")
            else:
                print(f"❌ 用户 {user_id} 没有钱包记录")
                
                # 3. 检查是否有其他用户ID的钱包
                print(f"\n3. 检查所有钱包记录...")
                cursor.execute("SELECT user_id, balance FROM wallets ORDER BY user_id")
                all_wallets = cursor.fetchall()
                print(f"所有钱包记录:")
                for wallet in all_wallets:
                    print(f"   用户ID: {wallet[0]}, 余额: {wallet[1]}")
        else:
            print("❌ 未找到手机号为 19825012076 的用户")
            
            # 显示所有用户
            print(f"\n所有用户:")
            cursor.execute("SELECT id, username, nickname, phone FROM users ORDER BY id")
            all_users = cursor.fetchall()
            for user in all_users:
                print(f"   ID: {user[0]}, 用户名: {user[1]}, 昵称: {user[2]}, 手机: {user[3]}")
        
    except Exception as e:
        print(f"❌ 数据库操作失败: {e}")
        import traceback
        traceback.print_exc()
        
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    test_user_balance_direct()
