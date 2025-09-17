#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_wallet_direct():
    """直接测试钱包API（管理员接口）"""
    
    base_url = "http://localhost:8080"
    user_id = 86945008
    
    print("=== 直接测试钱包余额API（管理员接口） ===")
    
    # 先尝试登录获取token
    print("=== 先登录获取token ===")
    login_data = {
        "phone": "19825012076",
        "code": "849123"  # 使用之前成功的验证码
    }
    
    login_response = requests.post(f"{base_url}/api/auth/login-with-code", params=login_data)
    
    if login_response.status_code == 200:
        login_result = login_response.json()
        if login_result.get('success') and login_result.get('data'):
            token = login_result['data'].get('token')
            print(f"✅ 登录成功，获取到token")
            
            # 使用token获取钱包余额
            headers = {"Authorization": f"Bearer {token}"}
            wallet_response = requests.get(f"{base_url}/api/wallet/balance", headers=headers)
            
            if wallet_response.status_code == 200:
                wallet_result = wallet_response.json()
                print(f"✅ 钱包API调用成功")
                print(f"响应数据: {json.dumps(wallet_result, indent=2, ensure_ascii=False)}")
                
                if wallet_result.get('success') and wallet_result.get('data'):
                    balance = wallet_result['data'].get('balance')
                    print(f"\n💰 用户{user_id}的余额: {balance}")
                    if balance == 0:
                        print("❌ 余额为0，这可能是问题所在")
                    else:
                        print(f"✅ 余额不为0: {balance}")
                else:
                    print("❌ 钱包数据获取失败")
            else:
                print(f"❌ 钱包API调用失败: {wallet_response.status_code}")
                print(f"响应内容: {wallet_response.text}")
        else:
            print("❌ 登录失败")
    else:
        print(f"❌ 登录请求失败: {login_response.status_code}")
        print(f"响应内容: {login_response.text}")
        
        # 如果登录失败，尝试直接查询管理员接口
        print("\n=== 尝试管理员接口 ===")
        wallet_response = requests.get(f"{base_url}/api/wallet/admin/balance/{user_id}")
        
        if wallet_response.status_code == 200:
            wallet_result = wallet_response.json()
            print(f"✅ 管理员钱包API调用成功")
            print(f"响应数据: {json.dumps(wallet_result, indent=2, ensure_ascii=False)}")
        else:
            print(f"❌ 管理员钱包API调用失败: {wallet_response.status_code}")
            print(f"响应内容: {wallet_response.text}")

if __name__ == "__main__":
    test_wallet_direct()
