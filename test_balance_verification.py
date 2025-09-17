#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import pymysql

def test_balance_verification():
    """验证余额数据的一致性：数据库 -> 后端API -> 前端显示"""
    
    print("=== 余额数据一致性验证 ===")
    
    # 1. 直接从数据库查询余额
    print("\n1. 从数据库查询余额...")
    db_balance = get_balance_from_database()
    
    # 2. 通过后端API查询余额
    print("\n2. 通过后端API查询余额...")
    api_balance = get_balance_from_api()
    
    # 3. 比较结果
    print("\n3. 数据一致性检查...")
    if db_balance is not None and api_balance is not None:
        if abs(db_balance - api_balance) < 0.01:  # 允许小的浮点数误差
            print("✅ 数据库余额与API余额一致!")
            print(f"   数据库余额: {db_balance}")
            print(f"   API余额: {api_balance}")
            print("\n🎉 结论: 余额数据来自真实的数据库，不是硬编码!")
        else:
            print("❌ 数据库余额与API余额不一致!")
            print(f"   数据库余额: {db_balance}")
            print(f"   API余额: {api_balance}")
    else:
        print("❌ 无法获取余额数据进行比较")

def get_balance_from_database():
    """从数据库直接查询余额"""
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
        
        cursor.execute("""
            SELECT balance FROM wallets 
            WHERE user_id = 86945008
        """)
        result = cursor.fetchone()
        
        if result:
            balance = float(result[0])
            print(f"   数据库余额: {balance}")
            return balance
        else:
            print("   未找到钱包记录")
            return None
            
    except Exception as e:
        print(f"   数据库查询失败: {e}")
        return None
    finally:
        if 'connection' in locals():
            connection.close()

def get_balance_from_api():
    """通过API查询余额"""
    try:
        url = "http://localhost:8080/api/admin/balance/86945008"
        response = requests.get(url, timeout=10)
        
        if response.status_code == 200:
            data = response.json()
            if data.get('success') and data.get('data'):
                wallet_data = data['data'].get('wallet', {})
                balance = wallet_data.get('balance')
                print(f"   API余额: {balance}")
                return balance
        else:
            print(f"   API调用失败: {response.status_code}")
            return None
            
    except Exception as e:
        print(f"   API调用异常: {e}")
        return None

if __name__ == "__main__":
    test_balance_verification()
