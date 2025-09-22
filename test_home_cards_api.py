#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试首页用户卡片API
"""

import requests
import json

def test_home_cards_api():
    """测试首页用户卡片API"""
    try:
        # API地址
        url = "http://localhost:8080/api/users/home-cards"
        
        # 请求参数
        params = {
            'page': 0,
            'size': 10,
            'gender': '女'  # 请求女性用户
        }
        
        print("测试首页用户卡片API...")
        print("="*60)
        print(f"请求URL: {url}")
        print(f"请求参数: {params}")
        print()
        
        # 发送请求
        response = requests.get(url, params=params, timeout=10)
        
        print(f"响应状态码: {response.status_code}")
        print(f"响应头: {dict(response.headers)}")
        print()
        
        if response.status_code == 200:
            data = response.json()
            print("响应数据:")
            print(json.dumps(data, indent=2, ensure_ascii=False))
            
            if data.get('success'):
                users = data.get('data', [])
                print(f"\n找到 {len(users)} 个用户:")
                print("-" * 40)
                
                for i, user in enumerate(users, 1):
                    print(f"{i}. ID: {user.get('id')}")
                    print(f"   昵称: {user.get('nickname')}")
                    print(f"   性别: {user.get('gender')}")
                    print(f"   年龄: {user.get('age')}")
                    print(f"   位置: {user.get('location')}")
                    print(f"   在线状态: {user.get('isOnline')}")
                    print(f"   状态: {user.get('status')}")
                    print()
                
                # 检查目标用户是否在列表中
                target_ids = [44479883, 65899032]
                found_users = [user for user in users if user.get('id') in target_ids]
                
                if found_users:
                    print("✓ 目标用户已找到:")
                    for user in found_users:
                        print(f"  - {user.get('nickname')} (ID: {user.get('id')})")
                else:
                    print("✗ 目标用户未找到")
                    
            else:
                print(f"API返回失败: {data.get('message')}")
        else:
            print(f"请求失败: {response.status_code}")
            print(f"响应内容: {response.text}")
            
    except Exception as e:
        print(f"测试失败: {e}")

if __name__ == "__main__":
    test_home_cards_api()

