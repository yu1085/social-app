#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_user_wealth_level():
    """测试用户财富等级功能"""
    base_url = "http://localhost:8080"
    
    print("测试用户财富等级功能...")
    
    # 1. 先创建一个测试用户
    print("\n1. 创建测试用户...")
    register_data = {
        "username": "test_wealth_user",
        "password": "test123456",
        "phone": "13900139002",
        "nickname": "财富测试用户",
        "gender": "MALE"
    }
    
    try:
        register_response = requests.post(f"{base_url}/api/auth/register", json=register_data)
        print(f"注册响应状态码: {register_response.status_code}")
        if register_response.status_code == 200:
            print("用户注册成功")
        else:
            print(f"用户注册失败: {register_response.text}")
    except Exception as e:
        print(f"用户注册异常: {e}")
    
    # 2. 用户登录
    print("\n2. 用户登录...")
    login_data = {
        "username": "test_wealth_user",
        "password": "test123456"
    }
    
    try:
        login_response = requests.post(f"{base_url}/api/auth/login", json=login_data)
        print(f"登录响应状态码: {login_response.status_code}")
        
        if login_response.status_code == 200:
            login_result = login_response.json()
            if login_result.get("success"):
                token = login_result["data"]["token"]
                print(f"登录成功，获取到token: {token[:50]}...")
                
                # 3. 测试获取财富等级
                print("\n3. 测试获取财富等级...")
                headers = {"Authorization": f"Bearer {token}"}
                
                wealth_response = requests.get(f"{base_url}/api/wealth-level/my-level", headers=headers)
                print(f"财富等级API响应状态码: {wealth_response.status_code}")
                print(f"响应内容: {wealth_response.text}")
                
                if wealth_response.status_code == 200:
                    wealth_result = wealth_response.json()
                    print(f"财富等级数据: {json.dumps(wealth_result, indent=2, ensure_ascii=False)}")
                else:
                    print(f"财富等级API失败: {wealth_response.text}")
                    
            else:
                print(f"登录失败: {login_result.get('message', '未知错误')}")
        else:
            print(f"登录请求失败: {login_response.text}")
            
    except Exception as e:
        print(f"测试过程中出现错误: {e}")

if __name__ == "__main__":
    test_user_wealth_level()
