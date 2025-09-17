#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_balance_api():
    print("=== 测试余额API ===")
    
    # 使用已知的token（从之前的日志中获取）
    token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjg2OTQ1MDA4LCJ1c2VybmFtZSI6InVzZXJfMTk4MjUwMTIwNzYiLCJzdWIiOiJ1c2VyXzE5ODI1MDEyMDc2IiwiaWF0IjoxNzU4MDgzNjY4LCJleHAiOjE3NTgxNzAwNjh9.oj7QMAWfthwOws0ZUSXFgCWRdkV5mOinoRNMORnVP3w"
    
    # 测试钱包余额API
    url = "http://localhost:8080/api/wallet/balance"
    headers = {
        "Authorization": token,
        "Content-Type": "application/json"
    }
    
    try:
        print(f"调用API: {url}")
        print(f"Token: {token}")
        
        response = requests.get(url, headers=headers, timeout=10)
        
        print(f"响应状态码: {response.status_code}")
        print(f"响应头: {dict(response.headers)}")
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ API调用成功!")
            print(f"响应数据: {json.dumps(data, indent=2, ensure_ascii=False)}")
            
            if data.get('success') and data.get('data'):
                balance = data['data'].get('balance')
                print(f"💰 余额: {balance}")
            else:
                print(f"❌ API返回失败: {data.get('message', '未知错误')}")
        else:
            print(f"❌ API调用失败: {response.status_code}")
            print(f"响应内容: {response.text}")
            
    except Exception as e:
        print(f"❌ 请求异常: {e}")

if __name__ == "__main__":
    test_balance_api()
