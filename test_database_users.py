#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import json

def test_database_users():
    """测试数据库中的用户数据"""
    
    print("=== 测试数据库用户数据 ===")
    
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
        
        # 1. 查询所有用户
        print("1. 查询所有用户...")
        cursor.execute("""
            SELECT id, username, nickname, avatar_url, age, location, bio, is_online, gender, created_at
            FROM users 
            WHERE id >= 1001
            ORDER BY id
        """)
        users = cursor.fetchall()
        
        print(f"找到 {len(users)} 个用户:")
        for user in users:
            user_id, username, nickname, avatar_url, age, location, bio, is_online, gender, created_at = user
            status = "在线" if is_online else "离线"
            print(f"  ID: {user_id}, 昵称: {nickname}, 年龄: {age}, 位置: {location}, 状态: {status}")
        
        # 2. 构建用户卡片数据
        print("\n2. 构建用户卡片数据...")
        user_cards = []
        for user in users:
            user_id, username, nickname, avatar_url, age, location, bio, is_online, gender, created_at = user
            
            card = {
                "id": user_id,
                "nickname": nickname,
                "avatar": avatar_url,
                "age": age,
                "location": location,
                "bio": bio,
                "isOnline": bool(is_online),
                "status": "空闲" if is_online else "离线",
                "statusColor": "green" if is_online else "gray",
                "callPrice": 300 if is_online else 200,
                "messagePrice": 10 if is_online else 8
            }
            user_cards.append(card)
        
        print(f"构建了 {len(user_cards)} 个用户卡片:")
        for i, card in enumerate(user_cards[:5]):  # 只显示前5个
            print(f"  卡片{i+1}: {card['nickname']} - {card['status']} - {card['callPrice']}/分钟")
        
        # 3. 模拟API响应
        print("\n3. 模拟API响应...")
        api_response = {
            "success": True,
            "message": "获取用户卡片成功",
            "data": user_cards,
            "timestamp": "2025-09-17T17:00:00"
        }
        
        print("API响应示例:")
        print(json.dumps(api_response, indent=2, ensure_ascii=False))
        
        # 4. 验证数据完整性
        print("\n4. 验证数据完整性...")
        online_users = [card for card in user_cards if card['isOnline']]
        offline_users = [card for card in user_cards if not card['isOnline']]
        
        print(f"在线用户: {len(online_users)} 个")
        print(f"离线用户: {len(offline_users)} 个")
        
        # 5. 检查钱包数据
        print("\n5. 检查钱包数据...")
        cursor.execute("""
            SELECT w.user_id, w.balance, u.nickname
            FROM wallets w
            JOIN users u ON w.user_id = u.id
            WHERE w.user_id >= 1001
            ORDER BY w.user_id
        """)
        wallets = cursor.fetchall()
        
        print(f"找到 {len(wallets)} 个钱包:")
        for wallet in wallets:
            user_id, balance, nickname = wallet
            print(f"  用户: {nickname}, 余额: {balance} CNY")
        
    except Exception as e:
        print(f"❌ 数据库操作失败: {e}")
        import traceback
        traceback.print_exc()
        
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    test_database_users()
