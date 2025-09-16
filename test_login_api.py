#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

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
        phone = "13800138000"
        response = requests.post(f"{BASE_URL}/auth/send-code", params={"phone": phone})
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
        phone = "13800138000"
        code = "123456"  # 测试验证码
        
        response = requests.post(f"{BASE_URL}/auth/login-with-code", 
                               params={"phone": phone, "code": code})
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

def test_username_password_login():
    """测试用户名密码登录接口"""
    print("\n=== 测试用户名密码登录接口 ===")
    try:
        login_data = {
            "username": "testuser",
            "password": "123456"
        }
        
        response = requests.post(f"{BASE_URL}/auth/login", json=login_data)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.json()}")
        
        if response.status_code == 200:
            data = response.json()
            if data.get("success"):
                return data.get("data", {}).get("token")
        return None
    except Exception as e:
        print(f"用户名密码登录失败: {e}")
        return None

def test_register():
    """测试用户注册接口"""
    print("\n=== 测试用户注册接口 ===")
    try:
        register_data = {
            "username": "newuser" + str(int(time.time())),
            "password": "123456"
        }
        
        response = requests.post(f"{BASE_URL}/auth/register", json=register_data)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.json()}")
        
        if response.status_code == 200:
            data = response.json()
            if data.get("success"):
                return data.get("data", {}).get("token")
        return None
    except Exception as e:
        print(f"用户注册失败: {e}")
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

def test_logout(token):
    """测试用户登出接口"""
    print("\n=== 测试用户登出接口 ===")
    try:
        headers = {"Authorization": f"Bearer {token}"}
        response = requests.post(f"{BASE_URL}/auth/logout", headers=headers)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.json()}")
        return response.status_code == 200
    except Exception as e:
        print(f"用户登出失败: {e}")
        return False

def main():
    """主测试函数"""
    print("开始测试SocialMeet登录功能...")
    
    # 测试健康检查
    if not test_health_check():
        print("❌ 健康检查失败，请确保后端服务已启动")
        return
    
    # 测试发送验证码
    if not test_send_verification_code():
        print("❌ 发送验证码失败")
        return
    
    # 测试验证码登录
    print("\n--- 测试验证码登录流程 ---")
    token1 = test_login_with_code()
    if not token1:
        print("❌ 验证码登录失败")
        return
    
    print(f"✅ 验证码登录成功，获得token: {token1[:20]}...")
    
    # 测试获取用户信息
    if not test_get_profile(token1):
        print("❌ 获取用户信息失败")
        return
    
    # 测试用户注册
    print("\n--- 测试用户注册流程 ---")
    token2 = test_register()
    if not token2:
        print("❌ 用户注册失败")
        return
    
    print(f"✅ 用户注册成功，获得token: {token2[:20]}...")
    
    # 测试用户名密码登录
    print("\n--- 测试用户名密码登录流程 ---")
    token3 = test_username_password_login()
    if not token3:
        print("❌ 用户名密码登录失败")
        return
    
    print(f"✅ 用户名密码登录成功，获得token: {token3[:20]}...")
    
    # 测试登出
    if not test_logout(token1):
        print("❌ 用户登出失败")
        return
    
    print("\n🎉 所有登录功能测试通过！")
    print("\n📱 Android应用现在可以正常使用以下功能：")
    print("✅ 验证码登录")
    print("✅ 用户名密码登录")
    print("✅ 用户注册")
    print("✅ 获取用户信息")
    print("✅ 用户登出")

if __name__ == "__main__":
    main()
