#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
消息API测试脚本
测试消息列表相关API接口
"""

import requests
import json
import time
from datetime import datetime

# 配置
BASE_URL = "http://localhost:8080"
TEST_USER = {
    "username": "testuser",
    "password": "123456"
}

class MessageAPITester:
    def __init__(self):
        self.session = requests.Session()
        self.token = None
        
    def login(self):
        """登录获取token"""
        try:
            response = self.session.post(
                f"{BASE_URL}/api/auth/login",
                json=TEST_USER,
                headers={"Content-Type": "application/json"}
            )
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success"):
                    self.token = data["data"]["token"]
                    print(f"✅ 登录成功，获取到token: {self.token[:20]}...")
                    return True
                else:
                    print(f"❌ 登录失败: {data.get('message')}")
                    return False
            else:
                print(f"❌ 登录请求失败: {response.status_code}")
                return False
                
        except Exception as e:
            print(f"❌ 登录异常: {e}")
            return False
    
    def get_headers(self):
        """获取请求头"""
        if not self.token:
            raise Exception("未登录，请先调用login()方法")
        return {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }
    
    def test_get_conversations(self):
        """测试获取会话列表"""
        print("\n🔍 测试获取会话列表...")
        try:
            response = self.session.get(
                f"{BASE_URL}/api/conversations?page=0&size=20",
                headers=self.get_headers()
            )
            
            print(f"状态码: {response.status_code}")
            if response.status_code == 200:
                data = response.json()
                print(f"✅ 获取会话列表成功")
                print(f"数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
                return data
            else:
                print(f"❌ 获取会话列表失败: {response.text}")
                return None
                
        except Exception as e:
            print(f"❌ 获取会话列表异常: {e}")
            return None
    
    def test_get_unread_conversations(self):
        """测试获取未读会话"""
        print("\n🔍 测试获取未读会话...")
        try:
            response = self.session.get(
                f"{BASE_URL}/api/conversations/unread?page=0&size=20",
                headers=self.get_headers()
            )
            
            print(f"状态码: {response.status_code}")
            if response.status_code == 200:
                data = response.json()
                print(f"✅ 获取未读会话成功")
                print(f"数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
                return data
            else:
                print(f"❌ 获取未读会话失败: {response.text}")
                return None
                
        except Exception as e:
            print(f"❌ 获取未读会话异常: {e}")
            return None
    
    def test_search_conversations(self, keyword="测试"):
        """测试搜索会话"""
        print(f"\n🔍 测试搜索会话 (关键词: {keyword})...")
        try:
            response = self.session.get(
                f"{BASE_URL}/api/conversations/search?keyword={keyword}&page=0&size=20",
                headers=self.get_headers()
            )
            
            print(f"状态码: {response.status_code}")
            if response.status_code == 200:
                data = response.json()
                print(f"✅ 搜索会话成功")
                print(f"数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
                return data
            else:
                print(f"❌ 搜索会话失败: {response.text}")
                return None
                
        except Exception as e:
            print(f"❌ 搜索会话异常: {e}")
            return None
    
    def test_get_messages(self, other_user_id=2):
        """测试获取消息列表"""
        print(f"\n🔍 测试获取消息列表 (对方用户ID: {other_user_id})...")
        try:
            response = self.session.get(
                f"{BASE_URL}/api/messages/conversation/{other_user_id}?page=0&size=20",
                headers=self.get_headers()
            )
            
            print(f"状态码: {response.status_code}")
            if response.status_code == 200:
                data = response.json()
                print(f"✅ 获取消息列表成功")
                print(f"数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
                return data
            else:
                print(f"❌ 获取消息列表失败: {response.text}")
                return None
                
        except Exception as e:
            print(f"❌ 获取消息列表异常: {e}")
            return None
    
    def test_send_message(self, receiver_id=2, content="测试消息"):
        """测试发送消息"""
        print(f"\n🔍 测试发送消息 (接收者ID: {receiver_id}, 内容: {content})...")
        try:
            message_data = {
                "receiverId": receiver_id,
                "content": content,
                "messageType": "TEXT"
            }
            
            response = self.session.post(
                f"{BASE_URL}/api/messages",
                json=message_data,
                headers=self.get_headers()
            )
            
            print(f"状态码: {response.status_code}")
            if response.status_code == 200:
                data = response.json()
                print(f"✅ 发送消息成功")
                print(f"数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
                return data
            else:
                print(f"❌ 发送消息失败: {response.text}")
                return None
                
        except Exception as e:
            print(f"❌ 发送消息异常: {e}")
            return None
    
    def test_get_unread_count(self):
        """测试获取未读消息数量"""
        print("\n🔍 测试获取未读消息数量...")
        try:
            response = self.session.get(
                f"{BASE_URL}/api/messages/unread-count",
                headers=self.get_headers()
            )
            
            print(f"状态码: {response.status_code}")
            if response.status_code == 200:
                data = response.json()
                print(f"✅ 获取未读消息数量成功")
                print(f"数据: {json.dumps(data, ensure_ascii=False, indent=2)}")
                return data
            else:
                print(f"❌ 获取未读消息数量失败: {response.text}")
                return None
                
        except Exception as e:
            print(f"❌ 获取未读消息数量异常: {e}")
            return None
    
    def run_all_tests(self):
        """运行所有测试"""
        print("🚀 开始消息API测试...")
        print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        
        # 登录
        if not self.login():
            print("❌ 登录失败，无法继续测试")
            return
        
        # 测试各个API
        self.test_get_conversations()
        self.test_get_unread_conversations()
        self.test_search_conversations()
        self.test_get_messages()
        self.test_send_message()
        self.test_get_unread_count()
        
        print("\n✅ 所有测试完成")

if __name__ == "__main__":
    tester = MessageAPITester()
    tester.run_all_tests()
