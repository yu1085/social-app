#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
简单API测试
"""

import requests
import json

# 配置
BASE_URL = "http://localhost:8080"

def test_login():
    """测试登录"""
    login_data = {
        "username": "user_1001",
        "password": "123456"
    }
    
    try:
        response = requests.post(
            f"{BASE_URL}/api/auth/login",
            json=login_data,
            headers={"Content-Type": "application/json"}
        )
        
        if response.status_code == 200:
            data = response.json()
            if data.get("success"):
                token = data["data"]["token"]
                print(f"✅ 登录成功，token: {token[:50]}...")
                return token
        return None
    except Exception as e:
        print(f"❌ 登录异常: {e}")
        return None

def test_user_profile(token):
    """测试用户资料API"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    print("\n🔍 测试用户资料API...")
    try:
        response = requests.get(
            f"{BASE_URL}/api/users/profile",
            headers=headers
        )
        
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")
        
        if response.status_code == 200:
            print("✅ 用户资料API成功")
            return True
        else:
            print("❌ 用户资料API失败")
            return False
            
    except Exception as e:
        print(f"❌ 用户资料API异常: {e}")
        return False

def test_conversations_simple(token):
    """测试会话API（简化版）"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    print("\n🔍 测试会话API...")
    try:
        response = requests.get(
            f"{BASE_URL}/api/conversations",
            headers=headers
        )
        
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")
        
        if response.status_code == 200:
            print("✅ 会话API成功")
            return True
        else:
            print("❌ 会话API失败")
            return False
            
    except Exception as e:
        print(f"❌ 会话API异常: {e}")
        return False

def main():
    """主函数"""
    print("🚀 简单API测试...")
    
    # 登录
    token = test_login()
    if not token:
        print("❌ 登录失败")
        return
    
    # 测试用户资料API
    test_user_profile(token)
    
    # 测试会话API
    test_conversations_simple(token)

if __name__ == "__main__":
    main()
