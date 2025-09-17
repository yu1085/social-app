#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

def test_android_network_config():
    """测试Android端网络配置"""
    
    print("=" * 60)
    print("测试Android端网络配置")
    print("=" * 60)
    print(f"测试时间: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    print()
    
    # Android端使用的URL
    android_base_url = "http://10.0.2.2:8080/api"
    local_base_url = "http://localhost:8080/api"
    
    phone = "19825012076"
    
    # 测试两个URL的连通性
    urls_to_test = [
        ("Android模拟器URL", android_base_url),
        ("本地URL", local_base_url)
    ]
    
    for url_name, base_url in urls_to_test:
        print(f"\n🔗 测试 {url_name}: {base_url}")
        print("-" * 40)
        
        try:
            # 1. 测试健康检查
            print("1. 测试健康检查...")
            health_url = f"{base_url}/health"
            health_response = requests.get(health_url, timeout=5)
            print(f"   健康检查状态码: {health_response.status_code}")
            
            if health_response.status_code == 200:
                print("   ✅ 健康检查通过")
            else:
                print(f"   ❌ 健康检查失败: {health_response.text}")
                continue
            
            # 2. 获取验证码
            print("2. 获取验证码...")
            sms_url = f"{base_url}/auth/send-code"
            sms_params = {"phone": phone}
            
            sms_response = requests.post(sms_url, params=sms_params, timeout=10)
            print(f"   验证码请求状态码: {sms_response.status_code}")
            
            if sms_response.status_code == 200:
                sms_result = sms_response.json()
                if sms_result.get('success'):
                    verification_code = sms_result.get('data')
                    print(f"   ✅ 获取验证码成功: {verification_code}")
                    
                    # 3. 登录
                    print("3. 用户登录...")
                    login_url = f"{base_url}/auth/login-with-code"
                    login_params = {"phone": phone, "code": verification_code}
                    
                    login_response = requests.post(login_url, params=login_params, timeout=10)
                    print(f"   登录请求状态码: {login_response.status_code}")
                    
                    if login_response.status_code == 200:
                        login_result = login_response.json()
                        if login_result.get('success'):
                            token = login_result.get('data', {}).get('token')
                            print(f"   ✅ 登录成功，Token: {token[:30]}...")
                            
                            # 4. 测试两种Authorization格式
                            print("4. 测试Authorization头格式...")
                            
                            # 格式1: 直接使用token (Android当前使用的方式)
                            print("   格式1: 直接使用token")
                            balance_url = f"{base_url}/wallet/balance"
                            headers1 = {
                                "Authorization": token,  # 直接使用token
                                "Content-Type": "application/json"
                            }
                            
                            balance_response1 = requests.get(balance_url, headers=headers1, timeout=10)
                            print(f"   响应状态码: {balance_response1.status_code}")
                            if balance_response1.status_code == 200:
                                balance_result1 = balance_response1.json()
                                if balance_result1.get('success'):
                                    balance1 = balance_result1.get('data', {}).get('balance')
                                    print(f"   ✅ 格式1成功，余额: {balance1}")
                                else:
                                    print(f"   ❌ 格式1失败: {balance_result1.get('message')}")
                            else:
                                print(f"   ❌ 格式1失败: {balance_response1.text}")
                            
                            # 格式2: 使用Bearer前缀 (后端期望的格式)
                            print("   格式2: 使用Bearer前缀")
                            headers2 = {
                                "Authorization": f"Bearer {token}",  # 使用Bearer前缀
                                "Content-Type": "application/json"
                            }
                            
                            balance_response2 = requests.get(balance_url, headers=headers2, timeout=10)
                            print(f"   响应状态码: {balance_response2.status_code}")
                            if balance_response2.status_code == 200:
                                balance_result2 = balance_response2.json()
                                if balance_result2.get('success'):
                                    balance2 = balance_result2.get('data', {}).get('balance')
                                    print(f"   ✅ 格式2成功，余额: {balance2}")
                                else:
                                    print(f"   ❌ 格式2失败: {balance_result2.get('message')}")
                            else:
                                print(f"   ❌ 格式2失败: {balance_response2.text}")
                            
                        else:
                            print(f"   ❌ 登录失败: {login_result.get('message')}")
                    else:
                        print(f"   ❌ 登录失败: {login_response.text}")
                else:
                    print(f"   ❌ 获取验证码失败: {sms_result.get('message')}")
            else:
                print(f"   ❌ 验证码请求失败: {sms_response.text}")
                
        except Exception as e:
            print(f"   ❌ 请求异常: {e}")
    
    print(f"\n" + "=" * 60)
    print("测试完成")

if __name__ == "__main__":
    test_android_network_config()
