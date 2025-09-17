#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_admin_balance():
    """使用管理员API测试余额获取"""
    
    print("=== 使用管理员API测试余额获取 ===")
    
    # 使用管理员API直接通过用户ID获取余额（不需要认证）
    user_id = 86945008  # 从数据库查询得到的用户ID
    url = f"http://localhost:8080/api/admin/balance/{user_id}"
    
    try:
        print(f"调用管理员API: {url}")
        
        response = requests.get(url, timeout=10)
        
        print(f"响应状态码: {response.status_code}")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ 管理员API调用成功!")
            print(f"响应数据: {json.dumps(data, indent=2, ensure_ascii=False)}")
            
            if data.get('success') and data.get('data'):
                wallet_data = data['data'].get('wallet', {})
                balance = wallet_data.get('balance')
                print(f"💰 通过API获取的余额: {balance}")
                print(f"   余额类型: {type(balance)}")
                print(f"   用户信息: {data['data'].get('user', {})}")
                return balance
            else:
                print(f"❌ API返回失败: {data.get('message', '未知错误')}")
        else:
            print(f"❌ API调用失败: {response.status_code}")
            print(f"响应内容: {response.text}")
            
    except Exception as e:
        print(f"❌ 请求异常: {e}")
        import traceback
        traceback.print_exc()
    
    return None

if __name__ == "__main__":
    test_admin_balance()
