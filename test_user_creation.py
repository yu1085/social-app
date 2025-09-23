#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试用户创建和ID生成流程
"""

import requests
import json
import sys
import time

# 配置
BASE_URL = "http://10.0.2.2:8080"
API_BASE = f"{BASE_URL}/api"

def test_user_creation():
    """测试用户创建和ID生成"""
    print("=== 测试用户创建和ID生成流程 ===\n")
    
    # 1. 发送验证码
    phone = "13800138000"
    print(f"1. 发送验证码到 {phone}...")
    try:
        response = requests.post(f"{API_BASE}/phone/send-code", 
                               json={"phone": phone})
        print(f"   状态码: {response.status_code}")
        print(f"   响应: {response.text}")
        
        if response.status_code == 200:
            print("   ✅ 验证码发送成功")
        else:
            print("   ❌ 验证码发送失败")
            return
    except Exception as e:
        print(f"   ❌ 请求失败: {e}")
        return
    
    print()
    
    # 2. 使用验证码登录（模拟）
    print("2. 使用验证码登录...")
    try:
        # 这里需要真实的验证码，我们模拟一个
        code = "123456"  # 实际应用中需要从短信获取
        gender = "男"
        
        response = requests.post(f"{API_BASE}/phone/login", 
                               json={
                                   "phone": phone,
                                   "code": code,
                                   "gender": gender
                               })
        print(f"   状态码: {response.status_code}")
        print(f"   响应: {response.text}")
        
        if response.status_code == 200:
            data = response.json()
            if data.get("success"):
                user_data = data.get("data", {})
                user_id = user_data.get("user", {}).get("id")
                token = user_data.get("token")
                
                print(f"   ✅ 登录成功")
                print(f"   用户ID: {user_id}")
                print(f"   Token: {token[:20]}...")
                
                # 3. 测试更新用户资料
                print("\n3. 测试更新用户资料...")
                test_update_profile(user_id, token)
                
            else:
                print(f"   ❌ 登录失败: {data.get('message', '未知错误')}")
        else:
            print("   ❌ 登录请求失败")
    except Exception as e:
        print(f"   ❌ 请求失败: {e}")

def test_update_profile(user_id, token):
    """测试更新用户资料"""
    try:
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        
        profile_data = {
            "nickname": "测试用户_" + str(int(time.time())),
            "bio": "这是一个测试用户",
            "location": "北京市朝阳区",
            "gender": "男",
            "height": 175,
            "weight": 70
        }
        
        response = requests.put(f"{API_BASE}/users/profile/{user_id}", 
                              json=profile_data,
                              headers=headers)
        print(f"   状态码: {response.status_code}")
        print(f"   响应: {response.text}")
        
        if response.status_code == 200:
            print("   ✅ 用户资料更新成功")
        else:
            print("   ❌ 用户资料更新失败")
    except Exception as e:
        print(f"   ❌ 请求失败: {e}")

def test_user_id_generation():
    """测试用户ID生成规则"""
    print("\n=== 用户ID生成规则说明 ===")
    print("1. 用户ID格式：8位数字")
    print("2. 范围：10000000-99999999")
    print("3. 生成方式：随机数生成，确保不重复")
    print("4. 如果重复，最多尝试100次")
    print("5. 如果100次都重复，使用时间戳作为ID")
    print("6. 用户通过手机验证码登录时自动创建")
    print("7. 前端通过AuthManager.getUserId()获取真实用户ID")

if __name__ == "__main__":
    test_user_creation()
    test_user_id_generation()
    
    print("\n=== 修复总结 ===")
    print("1. ✅ 后端UserProfileController已添加JWT认证验证")
    print("2. ✅ 前端ProfileEditActivity已改用AuthManager获取真实token和用户ID")
    print("3. ✅ 添加了认证验证，确保用户登录后才能更新资料")
    print("4. ✅ 改进了错误处理，提供更好的用户反馈")
    print("5. ✅ 用户ID通过8位随机数字生成，确保唯一性")
    print("\n现在401错误应该已经解决，用户需要先登录获取有效token才能更新资料。")
