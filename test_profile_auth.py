#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试用户资料更新认证流程
"""

import requests
import json
import sys

# 配置
BASE_URL = "http://10.0.2.2:8080"
API_BASE = f"{BASE_URL}/api"

def test_profile_auth():
    """测试用户资料认证流程"""
    print("=== 测试用户资料更新认证流程 ===\n")
    
    # 1. 测试无token访问（应该返回401）
    print("1. 测试无token访问...")
    try:
        response = requests.put(f"{API_BASE}/users/profile/1", 
                              json={"nickname": "测试用户"})
        print(f"   状态码: {response.status_code}")
        print(f"   响应: {response.text}")
        
        if response.status_code == 401:
            print("   ✅ 正确返回401未授权错误")
        else:
            print("   ❌ 应该返回401错误")
    except Exception as e:
        print(f"   ❌ 请求失败: {e}")
    
    print()
    
    # 2. 测试无效token访问（应该返回401）
    print("2. 测试无效token访问...")
    try:
        headers = {"Authorization": "Bearer invalid_token_12345"}
        response = requests.put(f"{API_BASE}/users/profile/1", 
                              json={"nickname": "测试用户"},
                              headers=headers)
        print(f"   状态码: {response.status_code}")
        print(f"   响应: {response.text}")
        
        if response.status_code == 401:
            print("   ✅ 正确返回401未授权错误")
        else:
            print("   ❌ 应该返回401错误")
    except Exception as e:
        print(f"   ❌ 请求失败: {e}")
    
    print()
    
    # 3. 测试获取用户资料（需要先登录获取token）
    print("3. 测试获取用户资料...")
    try:
        # 先尝试获取用户资料（需要token）
        response = requests.get(f"{API_BASE}/users/profile")
        print(f"   状态码: {response.status_code}")
        print(f"   响应: {response.text}")
        
        if response.status_code == 401:
            print("   ✅ 正确返回401未授权错误（需要登录）")
        else:
            print("   ❌ 应该返回401错误")
    except Exception as e:
        print(f"   ❌ 请求失败: {e}")
    
    print()
    
    # 4. 测试手机验证码登录获取token
    print("4. 测试手机验证码登录...")
    try:
        # 发送验证码
        phone = "13800138000"
        print(f"   发送验证码到 {phone}...")
        response = requests.post(f"{API_BASE}/phone/send-code", 
                               json={"phone": phone})
        print(f"   发送验证码状态码: {response.status_code}")
        print(f"   发送验证码响应: {response.text}")
        
        if response.status_code == 200:
            print("   ✅ 验证码发送成功")
            
            # 使用验证码登录（这里需要真实的验证码，暂时跳过）
            print("   ⚠️  需要真实验证码才能完成登录测试")
        else:
            print("   ❌ 验证码发送失败")
    except Exception as e:
        print(f"   ❌ 请求失败: {e}")
    
    print()
    print("=== 测试完成 ===")
    print("\n修复说明:")
    print("1. 后端UserProfileController已添加JWT认证验证")
    print("2. 前端ProfileEditActivity已改用AuthManager获取真实token")
    print("3. 添加了更好的错误处理和用户反馈")
    print("4. 需要用户先登录获取有效token才能更新资料")

if __name__ == "__main__":
    test_profile_auth()
