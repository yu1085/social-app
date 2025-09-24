#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
使用数据库中的测试数据测试API
"""

import requests
import json
from datetime import datetime

# 配置
BASE_URL = "http://localhost:8080"

def test_login():
    """测试登录"""
    login_data = {
        "username": "testuser001",
        "password": "123456"
    }
    
    try:
        response = requests.post(
            f"{BASE_URL}/api/auth/login",
            json=login_data,
            headers={"Content-Type": "application/json"}
        )
        
        print(f"登录状态码: {response.status_code}")
        print(f"登录响应: {response.text}")
        
        if response.status_code == 200:
            data = response.json()
            if data.get("success"):
                print("✅ 登录成功")
                return data["data"]["token"]
            else:
                print(f"❌ 登录失败: {data.get('message')}")
                return None
        else:
            print(f"❌ 登录请求失败: {response.status_code}")
            return None
            
    except Exception as e:
        print(f"❌ 登录异常: {e}")
        return None

def test_conversations_api(token):
    """测试会话列表API"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    print("\n🔍 测试获取会话列表...")
    try:
        response = requests.get(
            f"{BASE_URL}/api/conversations?page=0&size=20",
            headers=headers
        )
        
        print(f"状态码: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print("✅ 获取会话列表成功")
            print(f"数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
            return data
        else:
            print(f"❌ 获取会话列表失败: {response.text}")
            return None
            
    except Exception as e:
        print(f"❌ 获取会话列表异常: {e}")
        return None

def test_messages_api(token, other_user_id=1002):
    """测试消息列表API"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    print(f"\n🔍 测试获取消息列表 (对方用户ID: {other_user_id})...")
    try:
        response = requests.get(
            f"{BASE_URL}/api/messages/conversation/{other_user_id}?page=0&size=20",
            headers=headers
        )
        
        print(f"状态码: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print("✅ 获取消息列表成功")
            print(f"数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
            return data
        else:
            print(f"❌ 获取消息列表失败: {response.text}")
            return None
            
    except Exception as e:
        print(f"❌ 获取消息列表异常: {e}")
        return None

def test_send_message_api(token, receiver_id=1002):
    """测试发送消息API"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    message_data = {
        "receiverId": receiver_id,
        "content": f"测试消息 - {datetime.now().strftime('%H:%M:%S')}",
        "messageType": "TEXT"
    }
    
    print(f"\n🔍 测试发送消息 (接收者ID: {receiver_id})...")
    try:
        response = requests.post(
            f"{BASE_URL}/api/messages",
            json=message_data,
            headers=headers
        )
        
        print(f"状态码: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print("✅ 发送消息成功")
            print(f"数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
            return data
        else:
            print(f"❌ 发送消息失败: {response.text}")
            return None
            
    except Exception as e:
        print(f"❌ 发送消息异常: {e}")
        return None

def test_unread_count_api(token):
    """测试未读消息数量API"""
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    print("\n🔍 测试获取未读消息数量...")
    try:
        response = requests.get(
            f"{BASE_URL}/api/messages/unread-count",
            headers=headers
        )
        
        print(f"状态码: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print("✅ 获取未读消息数量成功")
            print(f"数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
            return data
        else:
            print(f"❌ 获取未读消息数量失败: {response.text}")
            return None
            
    except Exception as e:
        print(f"❌ 获取未读消息数量异常: {e}")
        return None

def main():
    """主函数"""
    print("🚀 开始API测试...")
    print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # 登录
    token = test_login()
    if not token:
        print("❌ 登录失败，无法继续测试")
        return
    
    # 测试各个API
    test_conversations_api(token)
    test_messages_api(token)
    test_send_message_api(token)
    test_unread_count_api(token)
    
    print("\n✅ 所有API测试完成")

if __name__ == "__main__":
    main()
