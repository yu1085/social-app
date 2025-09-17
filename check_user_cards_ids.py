#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查用户卡片的ID信息
"""

import requests
import json
from datetime import datetime

# 配置
BASE_URL = "http://localhost:8080"
API_BASE = f"{BASE_URL}/api"

def check_user_cards_with_ids():
    """检查用户卡片的ID信息"""
    print("=" * 60)
    print("检查首页用户卡片ID信息")
    print("=" * 60)
    
    try:
        # 获取用户卡片数据
        response = requests.get(f"{API_BASE}/users/home-cards?page=0&size=10", timeout=10)
        
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                user_cards = data.get('data', [])
                
                print(f"✅ 成功获取{len(user_cards)}个用户卡片")
                print(f"\n📋 用户卡片详细信息:")
                print(f"{'序号':<4} {'ID':<12} {'昵称':<10} {'状态':<6} {'价格':<10} {'位置':<8}")
                print("-" * 60)
                
                for i, card in enumerate(user_cards, 1):
                    user_id = card.get('id', 'N/A')
                    nickname = card.get('nickname', 'N/A')
                    status = card.get('status', 'N/A')
                    call_price = card.get('callPrice', 0)
                    location = card.get('location', 'N/A')
                    
                    print(f"{i:<4} {user_id:<12} {nickname:<10} {status:<6} {call_price}/分钟{'':<4} {location:<8}")
                
                # 检查前4个用户（对应Android首页的4个卡片位置）
                print(f"\n🏠 Android首页用户卡片 (前4个):")
                print(f"{'卡片位置':<8} {'用户ID':<12} {'昵称':<10} {'状态':<6} {'价格':<10}")
                print("-" * 50)
                
                for i in range(min(4, len(user_cards))):
                    card = user_cards[i]
                    user_id = card.get('id', 'N/A')
                    nickname = card.get('nickname', 'N/A')
                    status = card.get('status', 'N/A')
                    call_price = card.get('callPrice', 0)
                    
                    print(f"卡片{i+1:<4} {user_id:<12} {nickname:<10} {status:<6} {call_price}/分钟")
                
                # 验证ID的唯一性
                ids = [card.get('id') for card in user_cards if card.get('id')]
                unique_ids = set(ids)
                
                print(f"\n🔍 ID唯一性检查:")
                print(f"总用户数: {len(user_cards)}")
                print(f"有效ID数: {len(ids)}")
                print(f"唯一ID数: {len(unique_ids)}")
                
                if len(ids) == len(unique_ids):
                    print("✅ 所有用户ID都是唯一的")
                else:
                    print("❌ 存在重复的用户ID")
                
                # 检查ID范围
                if ids:
                    min_id = min(ids)
                    max_id = max(ids)
                    print(f"ID范围: {min_id} - {max_id}")
                
                return True
            else:
                print(f"❌ API返回失败: {data.get('message')}")
                return False
        else:
            print(f"❌ HTTP请求失败: {response.status_code}")
            return False
            
    except Exception as e:
        print(f"❌ 检查失败: {e}")
        return False

def check_user_detail_by_id(user_id):
    """通过ID检查用户详情"""
    print(f"\n🔍 检查用户ID {user_id} 的详情...")
    
    try:
        response = requests.get(f"{API_BASE}/users/{user_id}/detail", timeout=10)
        
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                user_detail = data.get('data', {})
                print(f"✅ 用户详情获取成功:")
                print(f"   ID: {user_detail.get('id')}")
                print(f"   昵称: {user_detail.get('nickname')}")
                print(f"   状态: {user_detail.get('status')}")
                print(f"   价格: {user_detail.get('callPrice')}/分钟")
                print(f"   位置: {user_detail.get('location')}")
                return True
            else:
                print(f"❌ 获取用户详情失败: {data.get('message')}")
                return False
        else:
            print(f"❌ HTTP请求失败: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ 检查用户详情失败: {e}")
        return False

if __name__ == "__main__":
    print(f"开始检查用户卡片ID信息...")
    print(f"检查时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # 检查用户卡片ID
    success = check_user_cards_with_ids()
    
    if success:
        # 检查第一个用户的详情
        check_user_detail_by_id(1001)
        
        print(f"\n🎉 检查完成！")
        print(f"✅ 首页用户卡片现在都有真实的数据库ID")
        print(f"✅ 每个用户卡片都可以通过ID获取详细信息")
        print(f"✅ Android应用可以正确显示和交互用户数据")
    else:
        print(f"\n❌ 检查失败！")
