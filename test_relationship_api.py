#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_relationship_api():
    """测试关系功能API"""
    
    base_url = "http://localhost:8080"
    
    # 使用您提供的JWT token
    token = "eyJhbGciOiJIUzUxMiJ9.eyJ0eXBlIjoiYWNjZXNzIiwidXNlcklkIjo2NTg5OTAzMiwidXNlcm5hbWUiOiJ1c2VyXzEzODAwMTM4MDAwIiwic3ViIjoidXNlcl8xMzgwMDEzODAwMCIsImlhdCI6MTc1ODcyNTUzNSwiZXhwIjoxNzU4ODExOTM1fQ.nDdlIuQHMcr4Zcgd2acJpmp8Z1RY7rF39hjcUKw6tOSobWAbYF_NeJ0_SCyKT2AFCooqq36KPbzxJFhjo_0byA"
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    print("=== 测试关系功能API ===")
    
    # 1. 测试获取关系列表
    print("\n1. 测试获取关系列表...")
    try:
        response = requests.get(f"{base_url}/api/relationships", headers=headers)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")
    except Exception as e:
        print(f"错误: {e}")
    
    # 2. 测试获取知友列表
    print("\n2. 测试获取知友列表...")
    try:
        response = requests.get(f"{base_url}/api/relationships/friends", headers=headers)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")
    except Exception as e:
        print(f"错误: {e}")
    
    # 3. 测试获取喜欢列表
    print("\n3. 测试获取喜欢列表...")
    try:
        response = requests.get(f"{base_url}/api/relationships/likes", headers=headers)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")
    except Exception as e:
        print(f"错误: {e}")
    
    # 4. 测试创建关系（加为知友）
    print("\n4. 测试创建关系（加为知友）...")
    try:
        relationship_data = {
            "targetUserId": 65899033,  # 我们之前创建的用户
            "relationshipType": "FRIEND",
            "notes": "测试知友关系",
            "tags": "测试,知友"
        }
        
        response = requests.post(f"{base_url}/api/relationships", 
                              json=relationship_data, 
                              headers=headers)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("success"):
                print("✅ 创建关系成功！")
                relationship_id = result.get("data", {}).get("id")
                print(f"关系ID: {relationship_id}")
            else:
                print(f"❌ 创建关系失败: {result.get('message')}")
        else:
            print(f"❌ HTTP请求失败")
            
    except Exception as e:
        print(f"错误: {e}")
    
    # 5. 再次测试获取知友列表
    print("\n5. 再次测试获取知友列表...")
    try:
        response = requests.get(f"{base_url}/api/relationships/friends", headers=headers)
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")
    except Exception as e:
        print(f"错误: {e}")

if __name__ == "__main__":
    test_relationship_api()
