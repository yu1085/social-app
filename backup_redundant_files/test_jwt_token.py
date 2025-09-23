#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试JWT token验证
"""

import requests
import json
import sys

# 配置
BASE_URL = "http://10.0.2.2:8080"
API_BASE = f"{BASE_URL}/api"

def test_jwt_token():
    """测试JWT token验证"""
    print("=== 测试JWT Token验证 ===\n")
    
    # 从日志中获取的token（截取前50个字符用于测试）
    test_token = "eyJhbGciOi..."  # 这是从日志中看到的token开头
    
    print(f"测试Token: {test_token}")
    print(f"Token长度: {len(test_token)}")
    print()
    
    # 测试token验证接口
    print("1. 测试JWT token验证接口...")
    try:
        headers = {
            "Authorization": f"Bearer {test_token}",
            "Content-Type": "application/json"
        }
        
        response = requests.get(f"{API_BASE}/users/profile/test-token", headers=headers)
        print(f"   状态码: {response.status_code}")
        print(f"   响应: {response.text}")
        
        if response.status_code == 200:
            data = response.json()
            print(f"   ✅ 验证成功")
            print(f"   - Token有效: {data.get('isValid', False)}")
            print(f"   - 用户ID: {data.get('userId', 'N/A')}")
            print(f"   - 用户名: {data.get('username', 'N/A')}")
        else:
            print(f"   ❌ 验证失败")
            
    except Exception as e:
        print(f"   ❌ 请求失败: {e}")
    
    print()
    
    # 测试用户资料更新接口
    print("2. 测试用户资料更新接口...")
    try:
        headers = {
            "Authorization": f"Bearer {test_token}",
            "Content-Type": "application/json"
        }
        
        data = {
            "nickname": "测试用户",
            "gender": "女",
            "bio": "测试个人简介"
        }
        
        response = requests.put(f"{API_BASE}/users/profile/65899032", 
                              headers=headers, 
                              json=data)
        print(f"   状态码: {response.status_code}")
        print(f"   响应: {response.text}")
        
        if response.status_code == 200:
            print(f"   ✅ 更新成功")
        else:
            print(f"   ❌ 更新失败")
            
    except Exception as e:
        print(f"   ❌ 请求失败: {e}")

if __name__ == "__main__":
    test_jwt_token()
