#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_simple_api():
    """测试简单的API接口"""
    
    base_url = "http://localhost:8080"
    
    print("=== 测试后端服务状态 ===")
    
    # 测试健康检查接口（如果有的话）
    try:
        response = requests.get(f"{base_url}/actuator/health", timeout=5)
        print(f"健康检查: {response.status_code}")
        if response.status_code == 200:
            print(f"响应: {response.text}")
    except Exception as e:
        print(f"健康检查失败: {e}")
    
    # 测试根路径
    try:
        response = requests.get(f"{base_url}/", timeout=5)
        print(f"根路径: {response.status_code}")
    except Exception as e:
        print(f"根路径失败: {e}")
    
    # 测试验证码接口
    print("\n=== 测试验证码接口 ===")
    try:
        response = requests.post(f"{base_url}/api/auth/send-code", 
                               params={"phone": "19825012076"}, 
                               timeout=10)
        print(f"验证码接口: {response.status_code}")
        if response.status_code == 200:
            result = response.json()
            print(f"响应: {json.dumps(result, indent=2, ensure_ascii=False)}")
        else:
            print(f"错误响应: {response.text}")
    except Exception as e:
        print(f"验证码接口失败: {e}")

if __name__ == "__main__":
    test_simple_api()
