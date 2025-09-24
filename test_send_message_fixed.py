#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_send_message():
    """测试消息发送功能"""
    
    base_url = "http://localhost:8080"
    
    # 使用您提供的JWT token
    token = "eyJhbGciOiJIUzUxMiJ9.eyJ0eXBlIjoiYWNjZXNzIiwidXNlcklkIjo2NTg5OTAzMiwidXNlcm5hbWUiOiJ1c2VyXzEzODAwMTM4MDAwIiwic3ViIjoidXNlcl8xMzgwMDEzODAwMCIsImlhdCI6MTc1ODcyNTUzNSwiZXhwIjoxNzU4ODExOTM1fQ.nDdlIuQHMcr4Zcgd2acJpmp8Z1RY7rF39hjcUKw6tOSobWAbYF_NeJ0_SCyKT2AFCooqq36KPbzxJFhjo_0byA"
    
    print("=== 测试消息发送功能 ===")
    print(f"使用token: {token[:50]}...")
    
    # 测试发送消息给存在的用户
    message_data = {
        "receiverId": 1001,  # 使用存在的用户ID
        "content": "你平时什么时候比较空闲",
        "messageType": "TEXT"
    }
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    print("发送消息请求:")
    print(f"  URL: {base_url}/api/messages")
    print(f"  接收者ID: {message_data['receiverId']}")
    print(f"  消息内容: {message_data['content']}")
    print(f"  Authorization: Bearer {token[:20]}...")
    
    try:
        response = requests.post(f"{base_url}/api/messages", 
                              json=message_data, 
                              headers=headers,
                              timeout=10)
        
        print(f"\n响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("success"):
                print("✅ 消息发送成功！")
                print(f"消息ID: {result.get('data', {}).get('id')}")
                return True
            else:
                print(f"❌ 消息发送失败: {result.get('message')}")
                return False
        else:
            print(f"❌ HTTP请求失败，状态码: {response.status_code}")
            return False
            
    except requests.exceptions.ConnectionError:
        print("❌ 无法连接到服务器")
        return False
    except requests.exceptions.Timeout:
        print("❌ 请求超时")
        return False
    except Exception as e:
        print(f"❌ 发生错误: {e}")
        return False

if __name__ == "__main__":
    test_send_message()
