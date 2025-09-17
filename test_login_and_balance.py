#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

def test_login_and_balance():
    print("=== 测试登录和余额API ===")
    
    # 1. 先获取验证码
    print("\n1. 获取验证码...")
    sms_url = "http://10.0.2.2:8080/api/auth/send-sms"
    sms_data = {
        "phone": "19825012076"
    }
    
    try:
        sms_response = requests.post(sms_url, json=sms_data, timeout=10)
        print(f"验证码请求状态码: {sms_response.status_code}")
        
        if sms_response.status_code == 200:
            sms_result = sms_response.json()
            print(f"验证码响应: {json.dumps(sms_result, indent=2, ensure_ascii=False)}")
            
            if sms_result.get('success'):
                verification_code = sms_result.get('data')
                print(f"✅ 获取验证码成功: {verification_code}")
                
                # 2. 使用验证码登录
                print(f"\n2. 使用验证码 {verification_code} 登录...")
                login_url = "http://10.0.2.2:8080/api/auth/login"
                login_data = {
                    "phone": "19825012076",
                    "verificationCode": verification_code
                }
                
                login_response = requests.post(login_url, json=login_data, timeout=10)
                print(f"登录请求状态码: {login_response.status_code}")
                
                if login_response.status_code == 200:
                    login_result = login_response.json()
                    print(f"登录响应: {json.dumps(login_result, indent=2, ensure_ascii=False)}")
                    
                    if login_result.get('success'):
                        token = login_result.get('data', {}).get('token')
                        print(f"✅ 登录成功，获取到token: {token}")
                        
                        # 3. 使用token获取余额
                        print(f"\n3. 使用token获取余额...")
                        balance_url = "http://10.0.2.2:8080/api/wallet/balance"
                        headers = {
                            "Authorization": token,
                            "Content-Type": "application/json"
                        }
                        
                        balance_response = requests.get(balance_url, headers=headers, timeout=10)
                        print(f"余额请求状态码: {balance_response.status_code}")
                        
                        if balance_response.status_code == 200:
                            balance_result = balance_response.json()
                            print(f"✅ 余额API调用成功!")
                            print(f"余额响应: {json.dumps(balance_result, indent=2, ensure_ascii=False)}")
                            
                            if balance_result.get('success') and balance_result.get('data'):
                                balance = balance_result['data'].get('balance')
                                print(f"💰 用户余额: {balance}")
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

if __name__ == "__main__":
    test_login_and_balance()
