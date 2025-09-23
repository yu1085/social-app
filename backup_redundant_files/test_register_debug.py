#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
调试注册功能
"""

import requests
import json

def test_register_debug():
    """调试注册功能"""
    print("=== 调试用户注册 ===")
    
    data = {
        "username": "testuser",
        "password": "testpass123",
        "phone": "13800138000",
        "gender": "MALE"
    }
    
    try:
        response = requests.post("http://localhost:8080/api/auth/register", 
                               json=data, 
                               headers={"Content-Type": "application/json"},
                               timeout=10)
        
        print(f"响应状态码: {response.status_code}")
        print(f"响应头: {dict(response.headers)}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            print("✅ 注册成功")
            return True
        else:
            print(f"❌ 注册失败: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ 注册异常: {e}")
        return False

def test_simple_endpoint():
    """测试简单端点"""
    print("=== 测试简单端点 ===")
    
    try:
        response = requests.get("http://localhost:8080/api/auth/health", timeout=10)
        print(f"健康检查响应: {response.status_code}")
        print(f"健康检查内容: {response.text}")
        
        if response.status_code == 200:
            print("✅ 简单端点正常")
            return True
        else:
            print(f"❌ 简单端点异常: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ 简单端点异常: {e}")
        return False

if __name__ == "__main__":
    test_simple_endpoint()
    test_register_debug()
