#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试Android应用连接后端的功能
"""

import requests
import json
import time

def test_android_connection():
    """测试Android应用连接后端"""
    print("=== 测试Android应用连接后端 ===\n")
    
    base_url = "http://localhost:8080/api"
    
    # 测试健康检查
    print("1. 测试健康检查...")
    try:
        response = requests.get(f"{base_url}/health", timeout=10)
        print(f"   状态码: {response.status_code}")
        if response.status_code == 200:
            print("   ✅ 健康检查通过")
        else:
            print("   ❌ 健康检查失败")
    except Exception as e:
        print(f"   ❌ 健康检查异常: {e}")
    
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
            print(f"   ✅ 验证码发送成功: {result.get('data', 'N/A')}")
            verification_code = result.get('data', '')
        else:
            print(f"   ❌ 验证码发送失败: {response.text}")
            verification_code = "123456"  # 使用默认验证码
    except Exception as e:
        print(f"   ❌ 验证码接口异常: {e}")
        verification_code = "123456"  # 使用默认验证码
    
    # 测试登录接口
    print("\n3. 测试登录接口...")
    try:
        response = requests.post(
            f"{base_url}/auth/login-with-code?phone=19825012076&code={verification_code}",
            timeout=10
        )
        print(f"   状态码: {response.status_code}")
        if response.status_code == 200:
            result = response.json()
            print("   ✅ 登录成功")
            token = result.get('data', {}).get('token', '')
            print(f"   令牌: {token[:20]}..." if token else "   无令牌")
        else:
            print(f"   ❌ 登录失败: {response.text}")
            token = ""
    except Exception as e:
        print(f"   ❌ 登录接口异常: {e}")
        token = ""
    
    # 测试余额接口
    if token:
        print("\n4. 测试余额接口...")
        try:
            headers = {
                "Content-Type": "application/json",
                "Authorization": f"Bearer {token}"
            }
            response = requests.get(
                f"{base_url}/wallet/balance",
                headers=headers,
                timeout=10
            )
            print(f"   状态码: {response.status_code}")
            if response.status_code == 200:
                result = response.json()
                balance = result.get('data', {}).get('balance', 0)
                print(f"   ✅ 余额查询成功: {balance} CNY")
            else:
                print(f"   ❌ 余额查询失败: {response.text}")
        except Exception as e:
            print(f"   ❌ 余额接口异常: {e}")
    
    print("\n=== 测试完成 ===")
    print("\n现在请在Android模拟器中测试应用：")
    print("1. 清除应用数据")
    print("2. 重新打开应用")
    print("3. 输入手机号: 19825012076")
    print("4. 点击发送验证码")
    print("5. 输入收到的验证码")
    print("6. 检查余额是否显示为888.0 CNY")

if __name__ == "__main__":
    test_android_connection()
