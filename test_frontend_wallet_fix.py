#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_wallet_api_fix():
    """测试前端钱包显示修复"""
    
    base_url = "http://localhost:8080"
    
    print("=== 测试前端钱包显示修复 ===")
    
    # 1. 获取验证码
    print("1. 获取验证码...")
    phone = "19825012076"
    code_response = requests.post(f"{base_url}/api/auth/send-code", 
                                 params={"phone": phone})
    
    if code_response.status_code == 200:
        code_result = code_response.json()
        if code_result.get('success'):
            verification_code = code_result.get('data', "101852")
            print(f"✅ 验证码发送成功: {verification_code}")
        else:
            print(f"❌ 验证码发送失败: {code_result.get('message')}")
            return
    else:
        print(f"❌ 验证码请求失败: {code_response.status_code}")
        return
    
    # 2. 登录
    print("\n2. 登录...")
    login_data = {
        "phone": phone,
        "code": verification_code
    }
    
    login_response = requests.post(f"{base_url}/api/auth/login-with-code", 
                                 params=login_data)
    
    if login_response.status_code == 200:
        login_result = login_response.json()
        if login_result.get('success'):
            token = login_result['data']['token']
            print(f"✅ 登录成功")
            
            # 3. 测试钱包余额API
            print("\n3. 测试钱包余额API...")
            headers = {
                "Authorization": f"Bearer {token}",
                "Content-Type": "application/json"
            }
            
            wallet_response = requests.get(f"{base_url}/api/wallet/balance", 
                                         headers=headers)
            
            if wallet_response.status_code == 200:
                wallet_result = wallet_response.json()
                print(f"✅ 钱包API调用成功")
                print(f"响应数据: {json.dumps(wallet_result, indent=2, ensure_ascii=False)}")
                
                if wallet_result.get('success') and wallet_result.get('data'):
                    balance = wallet_result['data'].get('balance')
                    print(f"\n💰 当前余额: {balance}")
                    
                    if balance > 0:
                        print("🎉 钱包余额不为0，前端应该能正确显示！")
                        print("📱 请重新启动Android应用查看余额显示")
                    else:
                        print("❌ 钱包余额为0，需要检查数据库")
                else:
                    print("❌ 钱包数据获取失败")
            else:
                print(f"❌ 钱包API调用失败: {wallet_response.status_code}")
                print(f"响应内容: {wallet_response.text}")
        else:
            print(f"❌ 登录失败: {login_result.get('message')}")
    else:
        print(f"❌ 登录请求失败: {login_response.status_code}")
        print(f"响应内容: {login_response.text}")

if __name__ == "__main__":
    test_wallet_api_fix()
