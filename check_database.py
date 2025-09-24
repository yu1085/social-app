#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查数据库连接和用户数据
"""

import requests
import json

# 配置
BASE_URL = "http://localhost:8080"

def test_health_check():
    """测试健康检查"""
    try:
        response = requests.get(f"{BASE_URL}/actuator/health")
        print(f"健康检查状态码: {response.status_code}")
        print(f"健康检查响应: {response.text}")
        return response.status_code == 200
    except Exception as e:
        print(f"健康检查异常: {e}")
        return False

def test_swagger():
    """测试Swagger文档"""
    try:
        response = requests.get(f"{BASE_URL}/swagger-ui/index.html")
        print(f"Swagger状态码: {response.status_code}")
        if response.status_code == 200:
            print("✅ Swagger文档可访问")
            return True
        else:
            print("❌ Swagger文档不可访问")
            return False
    except Exception as e:
        print(f"Swagger检查异常: {e}")
        return False

def test_api_docs():
    """测试API文档"""
    try:
        response = requests.get(f"{BASE_URL}/v3/api-docs")
        print(f"API文档状态码: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print("✅ API文档可访问")
            print(f"API信息: {data.get('info', {}).get('title', 'Unknown')}")
            return True
        else:
            print("❌ API文档不可访问")
            return False
    except Exception as e:
        print(f"API文档检查异常: {e}")
        return False

def test_user_endpoints():
    """测试用户相关端点"""
    endpoints = [
        "/api/users",
        "/api/users/profile",
        "/api/auth/register",
        "/api/auth/login"
    ]
    
    for endpoint in endpoints:
        try:
            response = requests.get(f"{BASE_URL}{endpoint}")
            print(f"{endpoint} 状态码: {response.status_code}")
            if response.status_code not in [404, 405]:  # 404和405是正常的
                print(f"响应: {response.text[:200]}...")
        except Exception as e:
            print(f"{endpoint} 异常: {e}")

def create_user_via_api():
    """通过API创建用户"""
    user_data = {
        "username": "testuser001",
        "password": "123456",
        "nickname": "测试用户001",
        "phone": "13800138001",
        "email": "test001@example.com"
    }
    
    try:
        response = requests.post(
            f"{BASE_URL}/api/auth/register",
            json=user_data,
            headers={"Content-Type": "application/json"}
        )
        
        print(f"注册状态码: {response.status_code}")
        print(f"注册响应: {response.text}")
        
        if response.status_code == 200:
            data = response.json()
            if data.get("success"):
                print("✅ 用户创建成功")
                return True
            else:
                print(f"❌ 用户创建失败: {data.get('message')}")
                return False
        else:
            print(f"❌ 注册请求失败: {response.status_code}")
            return False
            
    except Exception as e:
        print(f"❌ 创建用户异常: {e}")
        return False

if __name__ == "__main__":
    print("🚀 检查数据库和后端服务...")
    
    print("\n1. 健康检查:")
    test_health_check()
    
    print("\n2. Swagger文档:")
    test_swagger()
    
    print("\n3. API文档:")
    test_api_docs()
    
    print("\n4. 用户端点测试:")
    test_user_endpoints()
    
    print("\n5. 创建测试用户:")
    create_user_via_api()
