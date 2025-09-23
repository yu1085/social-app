#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_existing_user_wealth():
    """测试现有用户的财富等级功能"""
    base_url = "http://localhost:8080"
    
    print("测试现有用户的财富等级功能...")
    
    # 使用已知存在的用户进行登录
    print("\n1. 用户登录...")
    login_data = {
        "username": "user_138001380000",
        "password": "default_password"
    }
    
    try:
        login_response = requests.post(f"{base_url}/api/auth/login", json=login_data)
        print(f"登录响应状态码: {login_response.status_code}")
        
        if login_response.status_code == 200:
            login_result = login_response.json()
            if login_result.get("success"):
                token = login_result["data"]["accessToken"]
                print(f"登录成功，获取到token: {token[:50]}...")
                
                # 测试获取财富等级
                print("\n2. 测试获取财富等级...")
                headers = {"Authorization": f"Bearer {token}"}
                
                wealth_response = requests.get(f"{base_url}/api/wealth-level/my-level", headers=headers)
                print(f"财富等级API响应状态码: {wealth_response.status_code}")
                print(f"响应内容: {wealth_response.text}")
                
                if wealth_response.status_code == 200:
                    wealth_result = wealth_response.json()
                    print(f"财富等级数据: {json.dumps(wealth_result, indent=2, ensure_ascii=False)}")
                else:
                    print(f"财富等级API失败: {wealth_response.text}")
                    
                # 测试获取财富排行榜
                print("\n3. 测试获取财富排行榜...")
                ranking_response = requests.get(f"{base_url}/api/wealth-level/ranking?limit=5", headers=headers)
                print(f"排行榜API响应状态码: {ranking_response.status_code}")
                print(f"响应内容: {ranking_response.text}")
                
            else:
                print(f"登录失败: {login_result.get('message', '未知错误')}")
        else:
            print(f"登录请求失败: {login_response.text}")
            
    except Exception as e:
        print(f"测试过程中出现错误: {e}")

if __name__ == "__main__":
    test_existing_user_wealth()
