#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

def test_message_send():
    """测试消息发送功能"""
    
    # 服务器地址
    base_url = "http://localhost:8080"
    
    # 测试用户登录获取token
    print("=== 测试消息发送功能 ===")
    
    # 1. 先登录获取token
    login_data = {
        "username": "user_13800138000",
        "password": "123456"
    }
    
    print("1. 尝试登录获取token...")
    try:
        login_response = requests.post(f"{base_url}/api/auth/login", json=login_data)
        print(f"登录响应状态码: {login_response.status_code}")
        print(f"登录响应内容: {login_response.text}")
        
        if login_response.status_code == 200:
            login_result = login_response.json()
            if login_result.get("success"):
                token = login_result.get("data", {}).get("token")
                print(f"登录成功，获取到token: {token[:50]}...")
                
                # 2. 测试发送消息
                print("\n2. 测试发送消息...")
                message_data = {
                    "receiverId": 65899033,  # 另一个测试用户ID
                    "content": "这是一条测试消息",
                    "messageType": "TEXT"
                }
                
                headers = {
                    "Authorization": f"Bearer {token}",
                    "Content-Type": "application/json"
                }
                
                send_response = requests.post(f"{base_url}/api/messages", 
                                            json=message_data, 
                                            headers=headers)
                
                print(f"发送消息响应状态码: {send_response.status_code}")
                print(f"发送消息响应内容: {send_response.text}")
                
                if send_response.status_code == 200:
                    print("✅ 消息发送成功！")
                    return True
                else:
                    print("❌ 消息发送失败")
                    return False
            else:
                print(f"❌ 登录失败: {login_result.get('message')}")
                return False
        else:
            print(f"❌ 登录请求失败，状态码: {login_response.status_code}")
            return False
            
    except requests.exceptions.ConnectionError:
        print("❌ 无法连接到服务器，请确保后端服务正在运行")
        return False
    except Exception as e:
        print(f"❌ 测试过程中发生错误: {e}")
        return False

if __name__ == "__main__":
    # 等待服务启动
    print("等待服务启动...")
    time.sleep(5)
    
    # 测试消息发送
    success = test_message_send()
    
    if success:
        print("\n🎉 消息发送功能测试通过！")
    else:
        print("\n💥 消息发送功能测试失败！")
