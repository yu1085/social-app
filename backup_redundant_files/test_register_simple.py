#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
简单注册测试脚本
"""

import requests
import json

def test_register():
    """测试用户注册"""
    url = "http://localhost:8080/api/auth/register"
    data = {
        "username": "testuser",
        "password": "testpass123",
        "phone": "13800138000",
        "gender": "MALE"
    }
    
    print("=== 测试用户注册 ===")
    print(f"请求URL: {url}")
    print(f"请求数据: {data}")
    
    try:
        response = requests.post(url, json=data, headers={"Content-Type": "application/json"}, timeout=10)
        print(f"响应状态码: {response.status_code}")
        print(f"响应头: {dict(response.headers)}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"注册成功: {result}")
            return result.get("data", {}).get("token")
        else:
            print(f"注册失败: {response.status_code}")
            return None
    except Exception as e:
        print(f"请求异常: {e}")
        return None

if __name__ == "__main__":
    test_register()
