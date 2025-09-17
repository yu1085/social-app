#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_wallet_api():
    """测试钱包API"""
    
    base_url = "http://localhost:8080"
    
    # 1. 先登录获取token
    print("=== 测试登录 ===")
    login_data = {
        "phone": "19825012076",
        "code": "101852"  # 使用固定验证码
    }
    
    login_response = requests.post(f"{base_url}/api/auth/login-with-code", 
                                 params=login_data)
    
    if login_response.status_code == 200:
        login_result = login_response.json()
        if login_result.get('success'):
            token = login_result['data']['token']
            print(f"✅ 登录成功，Token: {token[:20]}...")
            
            # 2. 测试钱包余额API
            print("\n=== 测试钱包余额API ===")
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
                    if balance == 888:
                        print("🎉 余额正确显示为888！")
                    else:
                        print(f"❌ 余额不正确，期望888，实际{balance}")
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
    test_wallet_api()
