#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
最小化测试
"""

import requests
import json

def test_minimal():
    """最小化测试"""
    print("=== 最小化测试 ===")
    
    # 测试健康检查
    try:
        response = requests.get("http://localhost:8080/api/health", timeout=5)
        print(f"健康检查: {response.status_code}")
    except Exception as e:
        print(f"健康检查异常: {e}")
    
    # 测试Swagger
    try:
        response = requests.get("http://localhost:8080/swagger-ui/index.html", timeout=5)
        print(f"Swagger: {response.status_code}")
    except Exception as e:
        print(f"Swagger异常: {e}")
    
    # 测试API文档
    try:
        response = requests.get("http://localhost:8080/api-docs", timeout=5)
        print(f"API文档: {response.status_code}")
    except Exception as e:
        print(f"API文档异常: {e}")
    
    # 测试数据库
    try:
        response = requests.get("http://localhost:8080/api/admin/database/fix/test", timeout=5)
        print(f"数据库测试: {response.status_code}")
    except Exception as e:
        print(f"数据库测试异常: {e}")
    
    # 测试注册
    try:
        data = {"username": "test", "password": "test", "phone": "13800138000"}
        response = requests.post("http://localhost:8080/api/auth/register", 
                               json=data, 
                               headers={"Content-Type": "application/json"},
                               timeout=5)
        print(f"注册测试: {response.status_code}")
    except Exception as e:
        print(f"注册测试异常: {e}")

if __name__ == "__main__":
    test_minimal()
