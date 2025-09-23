#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试动态API接口
"""

import requests
import json
import time
import random

BASE_URL = "http://localhost:8080"

def test_health():
    """测试健康检查"""
    try:
        response = requests.get(f"{BASE_URL}/api/health", timeout=5)
        print(f"✅ 健康检查: {response.status_code}")
        return True
    except Exception as e:
        print(f"❌ 健康检查失败: {e}")
        return False

def test_get_posts():
    """测试获取动态列表"""
    try:
        # 先注册用户
        phone = f"138{random.randint(10000000, 99999999)}"
        register_data = {
            "username": f"testuser{random.randint(1000, 9999)}",
            "password": "123456",
            "phone": phone,
            "gender": "MALE"
        }
        
        # 注册用户
        response = requests.post(f"{BASE_URL}/api/auth/register", json=register_data)
        if response.status_code == 200:
            token = response.json()["data"]["token"]
            print(f"✅ 用户注册成功，Token: {token[:20]}...")
            
            # 测试获取动态列表
            headers = {"Authorization": f"Bearer {token}"}
            response = requests.get(f"{BASE_URL}/api/posts/enhanced?filter=nearby&page=0&size=10", headers=headers)
            
            if response.status_code == 200:
                data = response.json()
                print(f"✅ 获取动态列表成功: {data['success']}")
                if data.get('data') and data['data'].get('content'):
                    print(f"   动态数量: {len(data['data']['content'])}")
                    if data['data']['content']:
                        first_post = data['data']['content'][0]
                        print(f"   第一个动态: {first_post.get('content', '无内容')[:50]}...")
                return True
            else:
                print(f"❌ 获取动态列表失败: {response.status_code} - {response.text}")
                return False
        else:
            print(f"❌ 用户注册失败: {response.status_code} - {response.text}")
            return False
            
    except Exception as e:
        print(f"❌ 测试获取动态列表异常: {e}")
        return False

def test_like_post():
    """测试点赞动态"""
    try:
        # 先注册用户
        phone = f"139{random.randint(10000000, 99999999)}"
        register_data = {
            "username": f"testuser{random.randint(1000, 9999)}",
            "password": "123456",
            "phone": phone,
            "gender": "FEMALE"
        }
        
        response = requests.post(f"{BASE_URL}/api/auth/register", json=register_data)
        if response.status_code == 200:
            token = response.json()["data"]["token"]
            headers = {"Authorization": f"Bearer {token}"}
            
            # 先发布一个动态
            post_data = {
                "content": "这是一条测试动态",
                "location": "北京市"
            }
            response = requests.post(f"{BASE_URL}/api/posts", json=post_data, headers=headers)
            if response.status_code == 200:
                post_id = response.json()["data"]["id"]
                print(f"✅ 发布动态成功，ID: {post_id}")
                
                # 点赞动态
                response = requests.post(f"{BASE_URL}/api/posts/{post_id}/toggle-like", headers=headers)
                if response.status_code == 200:
                    data = response.json()
                    print(f"✅ 点赞动态成功: {data['data']['isLiked']}")
                    return True
                else:
                    print(f"❌ 点赞动态失败: {response.status_code} - {response.text}")
                    return False
            else:
                print(f"❌ 发布动态失败: {response.status_code} - {response.text}")
                return False
        else:
            print(f"❌ 用户注册失败: {response.status_code} - {response.text}")
            return False
            
    except Exception as e:
        print(f"❌ 测试点赞动态异常: {e}")
        return False

def main():
    print("开始测试动态API接口...")
    print("=" * 50)
    
    # 等待服务启动
    print("等待服务启动...")
    time.sleep(5)
    
    # 测试健康检查
    if not test_health():
        print("服务未启动，请先启动后端服务")
        return
    
    print("\n" + "=" * 50)
    
    # 测试获取动态列表
    test_get_posts()
    
    print("\n" + "=" * 50)
    
    # 测试点赞动态
    test_like_post()
    
    print("\n" + "=" * 50)
    print("测试完成！")

if __name__ == "__main__":
    main()
