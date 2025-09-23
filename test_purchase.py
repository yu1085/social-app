#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试靓号购买功能
"""

import requests
import json

def test_purchase():
    base_url = "http://localhost:8080"
    
    # 1. 获取靓号列表
    print("1. 获取靓号列表...")
    response = requests.get(f"{base_url}/api/lucky-numbers/items")
    if response.status_code == 200:
        data = response.json()
        print(f"✅ 获取到 {len(data['data'])} 个靓号")
        if data['data']:
            lucky_number = data['data'][0]
            print(f"   第一个靓号: {lucky_number['number']}, 价格: {lucky_number['price']}")
            return lucky_number
    else:
        print(f"❌ 获取靓号列表失败: {response.status_code}")
        return None

def test_purchase_with_token():
    base_url = "http://localhost:8080"
    
    # 使用测试token
    test_token = "test_token_12345"
    headers = {
        "Authorization": f"Bearer {test_token}",
        "Content-Type": "application/json"
    }
    
    # 获取靓号
    lucky_number = test_purchase()
    if not lucky_number:
        return
    
    # 2. 测试购买
    print(f"\n2. 测试购买靓号 {lucky_number['number']}...")
    
    purchase_data = {
        "itemId": lucky_number['id'],
        "itemType": "LUCKY_NUMBER",
        "price": lucky_number['price']  # 使用原价
    }
    
    response = requests.post(
        f"{base_url}/api/lucky-numbers/purchase",
        headers=headers,
        json=purchase_data
    )
    
    print(f"响应状态码: {response.status_code}")
    print(f"响应内容: {response.text}")
    
    if response.status_code == 200:
        print("✅ 购买成功！")
    elif response.status_code == 422:
        print("❌ 余额不足")
    else:
        print(f"❌ 购买失败: {response.status_code}")

if __name__ == "__main__":
    print("🚀 开始测试靓号购买功能...")
    test_purchase_with_token()
