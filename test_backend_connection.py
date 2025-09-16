#!/usr/bin/env python3
"""
测试后端API连接和用户信息获取
"""

import requests
import json

# 后端API地址
BASE_URL = "http://localhost:8080/api"

def test_backend_connection():
    """测试后端连接"""
    try:
        # 测试发送验证码
        print("=== 测试发送验证码 ===")
        response = requests.post(f"{BASE_URL}/auth/send-code", params={"phone": "13800138000"})
        print(f"状态码: {response.status_code}")
        print(f"响应: {response.text}")
        
        if response.status_code == 200:
            data = response.json()
            if data.get("success"):
                print("✅ 发送验证码成功")
                code = data.get("data")
                print(f"验证码: {code}")
                
                # 测试登录
                print("\n=== 测试登录 ===")
                login_response = requests.post(f"{BASE_URL}/auth/login-with-code", 
                                             params={"phone": "13800138000", "code": code})
                print(f"状态码: {login_response.status_code}")
                print(f"响应: {login_response.text}")
                
                if login_response.status_code == 200:
                    login_data = login_response.json()
                    if login_data.get("success"):
                        print("✅ 登录成功")
                        user_data = login_data.get("data", {}).get("user", {})
                        print(f"用户ID: {user_data.get('id')}")
                        print(f"昵称: {user_data.get('nickname')}")
                        print(f"位置: {user_data.get('location')}")
                        print(f"年龄: {user_data.get('age')}")
                        
                        # 测试获取用户信息
                        print("\n=== 测试获取用户信息 ===")
                        token = login_data.get("data", {}).get("token")
                        if token:
                            headers = {"Authorization": f"Bearer {token}"}
                            profile_response = requests.get(f"{BASE_URL}/users/profile", headers=headers)
                            print(f"状态码: {profile_response.status_code}")
                            print(f"响应: {profile_response.text}")
                            
                            if profile_response.status_code == 200:
                                profile_data = profile_response.json()
                                if profile_data.get("success"):
                                    print("✅ 获取用户信息成功")
                                    user_info = profile_data.get("data", {})
                                    print(f"用户ID: {user_info.get('id')}")
                                    print(f"昵称: {user_info.get('nickname')}")
                                    print(f"位置: {user_info.get('location')}")
                                    print(f"年龄: {user_info.get('age')}")
                                    print(f"头像: {user_info.get('avatarUrl')}")
                                else:
                                    print(f"❌ 获取用户信息失败: {profile_data.get('message')}")
                            else:
                                print(f"❌ 获取用户信息请求失败: {profile_response.status_code}")
                        else:
                            print("❌ 未获取到token")
                    else:
                        print(f"❌ 登录失败: {login_data.get('message')}")
                else:
                    print(f"❌ 登录请求失败: {login_response.status_code}")
            else:
                print(f"❌ 发送验证码失败: {data.get('message')}")
        else:
            print(f"❌ 发送验证码请求失败: {response.status_code}")
            
    except requests.exceptions.ConnectionError:
        print("❌ 无法连接到后端服务器，请确保后端服务正在运行")
    except Exception as e:
        print(f"❌ 测试过程中出现错误: {e}")

if __name__ == "__main__":
    test_backend_connection()
