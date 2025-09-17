#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试10.0.2.2地址连接
"""

import requests
import json

def test_10_0_2_2_connection():
    """测试10.0.2.2地址连接"""
    print("=== 测试10.0.2.2地址连接 ===\n")
    
    base_url = "http://10.0.2.2:8080/api"
    
    # 测试健康检查
    print("1. 测试健康检查...")
    try:
        response = requests.get(f"{base_url}/health", timeout=10)
        print(f"   状态码: {response.status_code}")
        if response.status_code == 200:
            print("   ✅ 10.0.2.2健康检查通过")
        else:
            print("   ❌ 10.0.2.2健康检查失败")
    except Exception as e:
        print(f"   ❌ 10.0.2.2健康检查异常: {e}")
    
    # 测试验证码接口
    print("\n2. 测试验证码接口...")
    try:
        response = requests.post(
            f"{base_url}/auth/send-code?phone=19825012076",
            timeout=10
        )
        print(f"   状态码: {response.status_code}")
        if response.status_code == 200:
            result = response.json()
            print(f"   ✅ 10.0.2.2验证码发送成功: {result.get('data', 'N/A')}")
        else:
            print(f"   ❌ 10.0.2.2验证码发送失败: {response.text}")
    except Exception as e:
        print(f"   ❌ 10.0.2.2验证码接口异常: {e}")
    
    print("\n=== 测试完成 ===")

if __name__ == "__main__":
    test_10_0_2_2_connection()
