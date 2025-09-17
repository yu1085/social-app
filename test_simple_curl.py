#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_simple_api():
    print("=== 简单API测试 ===")
    
    # 测试验证码接口
    url = "http://localhost:8080/api/auth/send-sms"
    data = {"phone": "19825012076"}
    
    try:
        print(f"测试URL: {url}")
        response = requests.post(url, json=data, timeout=5)
        print(f"状态码: {response.status_code}")
        print(f"响应头: {dict(response.headers)}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"✅ API调用成功: {json.dumps(result, indent=2, ensure_ascii=False)}")
        else:
            print(f"❌ API调用失败")
            
    except Exception as e:
        print(f"❌ 请求异常: {e}")

if __name__ == "__main__":
    test_simple_api()
