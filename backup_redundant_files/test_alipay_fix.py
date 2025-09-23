#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试支付宝私钥修复后的支付功能
"""

import requests
import json
import time
from datetime import datetime

def test_alipay_payment():
    """测试支付宝支付功能"""
    base_url = "http://localhost:8080"
    
    print(f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}] 🚀 开始测试支付宝支付功能修复")
    print("=" * 60)
    
    # 1. 检查服务器状态
    print("🔍 检查服务器状态...")
    try:
        response = requests.get(f"{base_url}/actuator/health", timeout=10)
        if response.status_code == 200:
            print("✅ 服务器运行正常")
        else:
            print(f"❌ 服务器状态异常: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ 无法连接到服务器: {e}")
        return False
    
    # 2. 创建测试用户
    print("\n👤 创建测试用户...")
    user_data = {
        "username": "testuser_alipay",
        "password": "123456",
        "phone": "13800138000",
        "gender": "MALE"
    }
    
    try:
        response = requests.post(f"{base_url}/api/auth/register", json=user_data, timeout=10)
        if response.status_code == 200:
            print("✅ 测试用户创建成功")
            user_info = response.json()
            user_id = user_info.get('data', {}).get('id')
            token = user_info.get('data', {}).get('token')
        else:
            print(f"❌ 用户创建失败: {response.status_code} - {response.text}")
            return False
    except Exception as e:
        print(f"❌ 用户创建异常: {e}")
        return False
    
    # 3. 测试创建支付订单
    print("\n💳 测试创建支付宝支付订单...")
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
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            print("✅ 支付宝订单创建成功！私钥问题已修复")
            order_info = response.json()
            print(f"订单ID: {order_info.get('data', {}).get('orderId')}")
            print(f"支付参数: {order_info.get('data', {}).get('paymentParams', {})}")
            return True
        else:
            print(f"❌ 支付宝订单创建失败: {response.status_code}")
            print(f"错误信息: {response.text}")
            return False
            
    except Exception as e:
        print(f"❌ 创建订单异常: {e}")
        return False

if __name__ == "__main__":
    success = test_alipay_payment()
    if success:
        print("\n🎉 支付宝支付功能修复成功！")
    else:
        print("\n❌ 支付宝支付功能仍有问题")
