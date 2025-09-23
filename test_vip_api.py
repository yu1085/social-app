#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

# 配置
BASE_URL = "http://localhost:8080"
API_BASE = f"{BASE_URL}/api"

def test_vip_api():
    """测试VIP相关API"""
    print("🚀 开始测试VIP API...")
    
    # 1. 测试获取VIP等级列表
    print("\n1. 测试获取VIP等级列表...")
    try:
        response = requests.get(f"{API_BASE}/vip/levels", timeout=10)
        print(f"状态码: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print(f"✅ 获取VIP等级成功: {json.dumps(data, indent=2, ensure_ascii=False)}")
        else:
            print(f"❌ 获取VIP等级失败: {response.text}")
    except Exception as e:
        print(f"❌ 请求失败: {e}")
    
    # 2. 测试用户登录（获取token）
    print("\n2. 测试用户登录...")
    phone = "13800138000"
    code = "123456"
    
    try:
        # 发送验证码
        send_response = requests.post(f"{API_BASE}/auth/send-code", params={"phone": phone}, timeout=10)
        print(f"发送验证码状态码: {send_response.status_code}")
        
        # 验证码登录
        login_response = requests.post(f"{API_BASE}/auth/login-with-code", 
                                     params={"phone": phone, "code": code}, timeout=10)
        print(f"登录状态码: {login_response.status_code}")
        
        if login_response.status_code == 200:
            login_data = login_response.json()
            if login_data.get("success"):
                token = login_data["data"]["token"]
                print(f"✅ 登录成功，获取到token: {token[:20]}...")
                
                # 3. 测试检查VIP状态
                print("\n3. 测试检查VIP状态...")
                try:
                    vip_check_response = requests.get(f"{API_BASE}/vip/check", 
                                                    headers={"Authorization": f"Bearer {token}"}, 
                                                    timeout=10)
                    print(f"VIP状态检查状态码: {vip_check_response.status_code}")
                    if vip_check_response.status_code == 200:
                        vip_data = vip_check_response.json()
                        print(f"✅ VIP状态: {json.dumps(vip_data, indent=2, ensure_ascii=False)}")
                    else:
                        print(f"❌ VIP状态检查失败: {vip_check_response.text}")
                except Exception as e:
                    print(f"❌ VIP状态检查请求失败: {e}")
                
                # 4. 测试获取当前VIP订阅
                print("\n4. 测试获取当前VIP订阅...")
                try:
                    current_response = requests.get(f"{API_BASE}/vip/current", 
                                                  headers={"Authorization": f"Bearer {token}"}, 
                                                  timeout=10)
                    print(f"当前VIP订阅状态码: {current_response.status_code}")
                    if current_response.status_code == 200:
                        current_data = current_response.json()
                        print(f"✅ 当前VIP订阅: {json.dumps(current_data, indent=2, ensure_ascii=False)}")
                    else:
                        print(f"❌ 获取当前VIP订阅失败: {current_response.text}")
                except Exception as e:
                    print(f"❌ 获取当前VIP订阅请求失败: {e}")
                
                # 5. 测试订阅VIP（VIP会员）
                print("\n5. 测试订阅VIP（VIP会员）...")
                try:
                    subscribe_response = requests.post(f"{API_BASE}/vip/subscribe", 
                                                     params={"vipLevelId": 1}, 
                                                     headers={"Authorization": f"Bearer {token}"}, 
                                                     timeout=10)
                    print(f"订阅VIP状态码: {subscribe_response.status_code}")
                    if subscribe_response.status_code == 200:
                        subscribe_data = subscribe_response.json()
                        print(f"✅ VIP订阅成功: {json.dumps(subscribe_data, indent=2, ensure_ascii=False)}")
                    else:
                        print(f"❌ VIP订阅失败: {subscribe_response.text}")
                except Exception as e:
                    print(f"❌ VIP订阅请求失败: {e}")
                
                # 6. 再次检查VIP状态
                print("\n6. 再次检查VIP状态...")
                try:
                    vip_check_response2 = requests.get(f"{API_BASE}/vip/check", 
                                                     headers={"Authorization": f"Bearer {token}"}, 
                                                     timeout=10)
                    print(f"VIP状态检查状态码: {vip_check_response2.status_code}")
                    if vip_check_response2.status_code == 200:
                        vip_data2 = vip_check_response2.json()
                        print(f"✅ 订阅后VIP状态: {json.dumps(vip_data2, indent=2, ensure_ascii=False)}")
                    else:
                        print(f"❌ 订阅后VIP状态检查失败: {vip_check_response2.text}")
                except Exception as e:
                    print(f"❌ 订阅后VIP状态检查请求失败: {e}")
                
            else:
                print(f"❌ 登录失败: {login_data}")
        else:
            print(f"❌ 登录请求失败: {login_response.text}")
            
    except Exception as e:
        print(f"❌ 登录过程失败: {e}")

if __name__ == "__main__":
    test_vip_api()
