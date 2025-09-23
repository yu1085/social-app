#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
简单测试支付宝支付功能
"""

import requests
import json
from datetime import datetime

def test_simple_payment():
    """简单测试支付功能"""
    base_url = "http://localhost:8080"
    
    print(f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}] 🚀 简单测试支付宝支付功能")
    print("=" * 50)
    
    # 1. 注册用户
    print("👤 注册测试用户...")
    user_data = {
        "username": "testuser123",
        "password": "123456",
        "phone": "13800138001",
        "gender": "MALE"
    }
    
    try:
        response = requests.post(f"{base_url}/api/auth/register", json=user_data, timeout=10)
        print(f"注册响应: {response.status_code}")
        if response.status_code == 200:
            print("✅ 用户注册成功")
            user_info = response.json()
            token = user_info.get('data', {}).get('token')
            print(f"Token: {token[:50]}...")
        else:
            print(f"❌ 用户注册失败: {response.text}")
            return False
    except Exception as e:
        print(f"❌ 注册异常: {e}")
        return False
    
    # 2. 创建支付订单
    print("\n💳 创建支付订单...")
    order_data = {
        "packageId": "package_1200",
        "paymentMethod": "ALIPAY"
    }
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    try:
        response = requests.post(f"{base_url}/api/recharge/orders", json=order_data, headers=headers, timeout=30)
        print(f"订单创建响应: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            print("✅ 支付订单创建成功！")
            return True
        else:
            print(f"❌ 支付订单创建失败")
            return False
            
    except Exception as e:
        print(f"❌ 创建订单异常: {e}")
        return False

if __name__ == "__main__":
    success = test_simple_payment()
    if success:
        print("\n🎉 支付宝支付功能正常！")
    else:
        print("\n❌ 支付宝支付功能有问题")
