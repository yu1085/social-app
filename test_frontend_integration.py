#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import json

def test_frontend_integration():
    """测试前端集成 - 模拟从API获取用户数据并构建用户卡片"""
    
    print("=== 前端集成测试 ===")
    
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
        
        # 模拟API调用 - 获取首页用户卡片
        print("1. 模拟API调用 - 获取首页用户卡片...")
        
        # 查询活跃用户（模拟API逻辑）
        cursor.execute("""
            SELECT id, username, nickname, avatar_url, age, location, bio, is_online, gender, created_at
            FROM users 
            WHERE is_active = 1
            ORDER BY is_online DESC, created_at DESC
            LIMIT 10
        """)
        users = cursor.fetchall()
        
        # 构建用户卡片数据（模拟API响应）
        user_cards = []
        for user in users:
            user_id, username, nickname, avatar_url, age, location, bio, is_online, gender, created_at = user
            
            # 根据在线状态设置状态和价格
            if is_online:
                status = "空闲"
                status_color = "green"
                call_price = 300
                message_price = 10
            else:
                status = "离线"
                status_color = "gray"
                call_price = 200
                message_price = 8
            
            card = {
                "id": user_id,
                "nickname": nickname,
                "avatar": avatar_url,
                "age": age,
                "location": location,
                "bio": bio,
                "isOnline": bool(is_online),
                "status": status,
                "statusColor": status_color,
                "callPrice": call_price,
                "messagePrice": message_price,
                "gender": gender
            }
            user_cards.append(card)
        
        print(f"✅ 成功获取 {len(user_cards)} 个用户卡片")
        
        # 2. 模拟前端显示逻辑
        print("\n2. 模拟前端显示逻辑...")
        
        # 按在线状态分组
        online_users = [card for card in user_cards if card['isOnline']]
        offline_users = [card for card in user_cards if not card['isOnline']]
        
        print(f"在线用户: {len(online_users)} 个")
        print(f"离线用户: {len(offline_users)} 个")
        
        # 显示前4个用户卡片（模拟首页显示）
        print("\n首页用户卡片显示:")
        for i, card in enumerate(user_cards[:4]):
            status_icon = "🟢" if card['isOnline'] else "⚫"
            print(f"  卡片{i+1}: {status_icon} {card['nickname']} ({card['age']}岁) - {card['location']} - {card['status']} - {card['callPrice']}/分钟")
        
        # 3. 模拟用户详情页面
        print("\n3. 模拟用户详情页面...")
        if user_cards:
            first_user = user_cards[0]
            print(f"用户详情: {first_user['nickname']}")
            print(f"  年龄: {first_user['age']}岁")
            print(f"  位置: {first_user['location']}")
            print(f"  状态: {first_user['status']}")
            print(f"  通话价格: {first_user['callPrice']}/分钟")
            print(f"  消息价格: {first_user['messagePrice']}/条")
            print(f"  个人简介: {first_user['bio']}")
        
        # 4. 验证数据一致性
        print("\n4. 验证数据一致性...")
        
        # 检查钱包数据
        cursor.execute("""
            SELECT w.user_id, w.balance, u.nickname
            FROM wallets w
            JOIN users u ON w.user_id = u.id
            WHERE w.user_id IN (1001, 1002, 1003, 1004)
            ORDER BY w.user_id
        """)
        wallets = cursor.fetchall()
        
        print("钱包数据验证:")
        for wallet in wallets:
            user_id, balance, nickname = wallet
            print(f"  {nickname}: {balance} CNY")
        
        # 5. 生成前端可用的JSON数据
        print("\n5. 生成前端可用的JSON数据...")
        
        frontend_data = {
            "success": True,
            "message": "获取用户卡片成功",
            "data": {
                "users": user_cards,
                "total": len(user_cards),
                "onlineCount": len(online_users),
                "offlineCount": len(offline_users)
            },
            "timestamp": "2025-09-17T17:00:00"
        }
        
        print("前端可用数据:")
        print(json.dumps(frontend_data, indent=2, ensure_ascii=False))
        
        # 6. 模拟API响应格式
        print("\n6. 模拟API响应格式...")
        
        api_response = {
            "success": True,
            "message": "操作成功",
            "data": user_cards,
            "timestamp": "2025-09-17T17:00:00"
        }
        
        print("API响应格式:")
        print(json.dumps(api_response, indent=2, ensure_ascii=False))
        
        print("\n✅ 前端集成测试完成！")
        print("📊 数据统计:")
        print(f"  - 总用户数: {len(user_cards)}")
        print(f"  - 在线用户: {len(online_users)}")
        print(f"  - 离线用户: {len(offline_users)}")
        print(f"  - 平均年龄: {sum(card['age'] for card in user_cards) / len(user_cards):.1f}岁")
        
    except Exception as e:
        print(f"❌ 前端集成测试失败: {e}")
        import traceback
        traceback.print_exc()
        
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    test_frontend_integration()
