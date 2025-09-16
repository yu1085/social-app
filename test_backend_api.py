#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

# 后端API基础URL
BASE_URL = "http://localhost:8080/api"

def test_health_check():
    """测试健康检查接口"""
    print("=== 测试健康检查接口 ===")
    try:
        response = requests.get(f"{BASE_URL}/health")
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.json()}")
        return response.status_code == 200
    except Exception as e:
        print(f"健康检查失败: {e}")
        return False

def test_send_verification_code():
    """测试发送验证码接口"""
    print("\n=== 测试发送验证码接口 ===")
    try:
        response = requests.post(f"{BASE_URL}/auth/send-code", params={"phone": "13800138000"})
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.json()}")
        return response.status_code == 200
    except Exception as e:
        print(f"发送验证码失败: {e}")
        return False

def test_login_with_code():
    """测试验证码登录接口"""
    print("\n=== 测试验证码登录接口 ===")
    try:
        response = requests.post(f"{BASE_URL}/auth/login-with-code", 
                               params={"phone": "13800138000", "code": "123456"})
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.json()}")
        if response.status_code == 200:
            data = response.json()
            if data.get("success"):
                return data.get("data", {}).get("token")
        return None
    except Exception as e:
        print(f"验证码登录失败: {e}")
        return None

def test_get_profile(token):
    """测试获取用户信息接口"""
    print("\n=== 测试获取用户信息接口 ===")
    try:
        headers = {"Authorization": f"Bearer {token}"}
        response = requests.get(f"{BASE_URL}/users/profile", headers=headers)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.json()}")
        return response.status_code == 200
    except Exception as e:
        print(f"获取用户信息失败: {e}")
        return False

def test_search_users():
    """测试搜索用户接口"""
    print("\n=== 测试搜索用户接口 ===")
    try:
        response = requests.get(f"{BASE_URL}/users/search", 
                              params={"gender": "FEMALE", "page": 0, "size": 10})
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.json()}")
        return response.status_code == 200
    except Exception as e:
        print(f"搜索用户失败: {e}")
        return False

def main():
    """主测试函数"""
    print("开始测试SocialMeet后端API...")
    
    # 测试健康检查
    if not test_health_check():
        print("❌ 健康检查失败，请确保后端服务已启动")
        return
    
    # 测试发送验证码
    if not test_send_verification_code():
        print("❌ 发送验证码失败")
        return
    
    # 测试验证码登录
    token = test_login_with_code()
    if not token:
        print("❌ 验证码登录失败")
        return
    
    print(f"✅ 登录成功，获得token: {token[:20]}...")
    
    # 测试获取用户信息
    if not test_get_profile(token):
        print("❌ 获取用户信息失败")
        return
    
    # 测试搜索用户
    if not test_search_users():
        print("❌ 搜索用户失败")
        return
    
    print("\n🎉 所有API测试通过！")

if __name__ == "__main__":
    main()
