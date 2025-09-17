#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_user_api():
    """测试用户API接口"""
    
    print("=== 测试用户API接口 ===")
    
    # 1. 测试获取首页用户卡片
    print("\n1. 测试获取首页用户卡片...")
    url = "http://localhost:8080/api/users/home-cards"
    params = {
        "page": 0,
        "size": 10
    }
    
    try:
        response = requests.get(url, params=params, timeout=10)
        print(f"响应状态码: {response.status_code}")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ 获取用户卡片成功!")
            print(f"响应数据: {json.dumps(data, indent=2, ensure_ascii=False)}")
            
            if data.get('success') and data.get('data'):
                users = data['data']
                print(f"\n用户数量: {len(users)}")
                for i, user in enumerate(users):
                    print(f"用户{i+1}: {user.get('nickname')} - {user.get('status')} - {user.get('callPrice')}/分钟")
        else:
            print(f"❌ API调用失败: {response.status_code}")
            print(f"响应内容: {response.text}")
            
    except Exception as e:
        print(f"❌ 请求异常: {e}")
    
    # 2. 测试获取用户详情
    print("\n2. 测试获取用户详情...")
    user_id = 1001  # 测试用户ID
    url = f"http://localhost:8080/api/users/{user_id}/detail"
    
    try:
        response = requests.get(url, timeout=10)
        print(f"响应状态码: {response.status_code}")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ 获取用户详情成功!")
            print(f"响应数据: {json.dumps(data, indent=2, ensure_ascii=False)}")
        else:
            print(f"❌ API调用失败: {response.status_code}")
            print(f"响应内容: {response.text}")
            
    except Exception as e:
        print(f"❌ 请求异常: {e}")
    
    # 3. 测试搜索用户
    print("\n3. 测试搜索用户...")
    url = "http://localhost:8080/api/users/search"
    params = {
        "gender": "FEMALE",
        "page": 0,
        "size": 5
    }
    
    try:
        response = requests.get(url, params=params, timeout=10)
        print(f"响应状态码: {response.status_code}")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ 搜索用户成功!")
            print(f"响应数据: {json.dumps(data, indent=2, ensure_ascii=False)}")
        else:
            print(f"❌ API调用失败: {response.status_code}")
            print(f"响应内容: {response.text}")
            
    except Exception as e:
        print(f"❌ 请求异常: {e}")

if __name__ == "__main__":
    test_user_api()
