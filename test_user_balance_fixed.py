#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

def test_user_balance():
    """测试用户 19825012076 的余额"""
    
    print("=== 测试用户 19825012076 的余额 ===")
    
    # 尝试不同的URL
    urls_to_try = [
        "http://localhost:8080",
        "http://127.0.0.1:8080", 
        "http://10.0.2.2:8080"
    ]
    
    phone = "19825012076"
    
    for base_url in urls_to_try:
        print(f"\n尝试连接到: {base_url}")
        
        try:
            # 1. 先获取验证码
            print(f"1. 为手机号 {phone} 获取验证码...")
            sms_url = f"{base_url}/api/auth/send-sms"
            sms_data = {
                "phone": phone
            }
            
            sms_response = requests.post(sms_url, json=sms_data, timeout=5)
            print(f"验证码请求状态码: {sms_response.status_code}")
            
            if sms_response.status_code == 200:
                sms_result = sms_response.json()
                print(f"验证码响应: {json.dumps(sms_result, indent=2, ensure_ascii=False)}")
                
                if sms_result.get('success'):
                    verification_code = sms_result.get('data')
                    print(f"✅ 获取验证码成功: {verification_code}")
                    
                    # 2. 使用验证码登录
                    print(f"2. 使用验证码 {verification_code} 登录...")
                    login_url = f"{base_url}/api/auth/login"
                    login_data = {
                        "phone": phone,
                        "verificationCode": verification_code
                    }
                    
                    login_response = requests.post(login_url, json=login_data, timeout=5)
                    print(f"登录请求状态码: {login_response.status_code}")
                    
                    if login_response.status_code == 200:
                        login_result = login_response.json()
                        print(f"登录响应: {json.dumps(login_result, indent=2, ensure_ascii=False)}")
                        
                        if login_result.get('success'):
                            token = login_result.get('data', {}).get('token')
                            user_info = login_result.get('data', {}).get('user', {})
                            print(f"✅ 登录成功！")
                            print(f"Token: {token[:30]}...")
                            print(f"用户信息: {json.dumps(user_info, indent=2, ensure_ascii=False)}")
                            
                            # 3. 使用token获取余额
                            print(f"3. 获取用户余额...")
                            balance_url = f"{base_url}/api/wallet/balance"
                            headers = {
                                "Authorization": token,
                                "Content-Type": "application/json"
                            }
                            
                            balance_response = requests.get(balance_url, headers=headers, timeout=5)
                            print(f"余额请求状态码: {balance_response.status_code}")
                            
                            if balance_response.status_code == 200:
                                balance_result = balance_response.json()
                                print(f"✅ 余额API调用成功!")
                                print(f"余额响应: {json.dumps(balance_result, indent=2, ensure_ascii=False)}")
                                
                                if balance_result.get('success') and balance_result.get('data'):
                                    balance = balance_result['data'].get('balance')
                                    print(f"\n💰 用户 {phone} 的余额: {balance}")
                                    
                                    # 显示余额详情
                                    if balance is not None:
                                        print(f"   余额类型: {type(balance)}")
                                        print(f"   余额值: {balance}")
                                        if isinstance(balance, (int, float)):
                                            print(f"   格式化余额: {balance:,.2f}")
                                    else:
                                        print("   ❌ 余额为空")
                                    
                                    return True  # 成功获取余额，退出循环
                                else:
                                    print(f"❌ 余额API返回失败: {balance_result.get('message', '未知错误')}")
                            else:
                                print(f"❌ 余额API调用失败: {balance_response.status_code}")
                                print(f"响应内容: {balance_response.text}")
                        else:
                            print(f"❌ 登录失败: {login_result.get('message', '未知错误')}")
                    else:
                        print(f"❌ 登录请求失败: {login_response.status_code}")
                        print(f"响应内容: {login_response.text}")
                else:
                    print(f"❌ 获取验证码失败: {sms_result.get('message', '未知错误')}")
            else:
                print(f"❌ 验证码请求失败: {sms_response.status_code}")
                print(f"响应内容: {sms_response.text}")
                
        except Exception as e:
            print(f"❌ 请求异常: {e}")
            continue  # 尝试下一个URL
    
    print("\n❌ 所有URL都尝试失败")
    return False

def test_direct_balance():
    """直接测试余额API（使用已知token）"""
    print("\n=== 直接测试余额API ===")
    
    # 使用已知的token
    existing_token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjg2OTQ1MDA4LCJ1c2VybmFtZSI6InVzZXJfMTk4MjUwMTIwNzYiLCJzdWIiOiJ1c2VyXzE5ODI1MDEyMDc2IiwiaWF0IjoxNzU4MDgzNjY4LCJleHAiOjE3NTgxNzAwNjh9.oj7QMAWfthwOws0ZUSXFgCWRdkV5mOinoRNMORnVP3w"
    
    urls_to_try = [
        "http://localhost:8080",
        "http://127.0.0.1:8080", 
        "http://10.0.2.2:8080"
    ]
    
    for base_url in urls_to_try:
        print(f"\n尝试连接到: {base_url}")
        
        try:
            balance_url = f"{base_url}/api/wallet/balance"
            headers = {
                "Authorization": existing_token,
                "Content-Type": "application/json"
            }
            
            print(f"使用token: {existing_token[:30]}...")
            balance_response = requests.get(balance_url, headers=headers, timeout=5)
            print(f"余额请求状态码: {balance_response.status_code}")
            
            if balance_response.status_code == 200:
                balance_result = balance_response.json()
                print(f"✅ 余额API调用成功!")
                print(f"余额响应: {json.dumps(balance_result, indent=2, ensure_ascii=False)}")
                
                if balance_result.get('success') and balance_result.get('data'):
                    balance = balance_result['data'].get('balance')
                    print(f"\n💰 用户 19825012076 的余额: {balance}")
                    return True
                else:
                    print(f"❌ 余额API返回失败: {balance_result.get('message', '未知错误')}")
            else:
                print(f"❌ 余额API调用失败: {balance_response.status_code}")
                print(f"响应内容: {balance_response.text}")
                
        except Exception as e:
            print(f"❌ 请求异常: {e}")
            continue
    
    return False

if __name__ == "__main__":
    # 首先尝试直接使用现有token
    if test_direct_balance():
        print("\n✅ 使用现有token成功获取余额")
    else:
        print("\n❌ 使用现有token失败，尝试完整登录流程")
        test_user_balance()
