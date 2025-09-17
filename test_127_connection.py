#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_127_connection():
    """测试127.0.0.1:8080连接"""
    
    print("=" * 60)
    print("测试127.0.0.1:8080连接")
    print("=" * 60)
    
    base_url = "http://127.0.0.1:8080/api"
    phone = "19825012076"
    
    try:
        # 1. 测试健康检查
        print("1. 测试健康检查...")
        health_url = f"{base_url}/health"
        health_response = requests.get(health_url, timeout=5)
        print(f"   健康检查状态码: {health_response.status_code}")
        
        if health_response.status_code == 200:
            print("   ✅ 健康检查通过")
            
            # 2. 测试验证码
            print("2. 测试验证码...")
            sms_url = f"{base_url}/auth/send-code"
            sms_params = {"phone": phone}
            
            sms_response = requests.post(sms_url, params=sms_params, timeout=10)
            print(f"   验证码请求状态码: {sms_response.status_code}")
            
            if sms_response.status_code == 200:
                sms_result = sms_response.json()
                if sms_result.get('success'):
                    verification_code = sms_result.get('data')
                    print(f"   ✅ 获取验证码成功: {verification_code}")
                    print(f"   🎉 127.0.0.1:8080 连接正常！")
                    return True
                else:
                    print(f"   ❌ 获取验证码失败: {sms_result.get('message')}")
            else:
                print(f"   ❌ 验证码请求失败: {sms_response.text}")
        else:
            print(f"   ❌ 健康检查失败: {health_response.text}")
            
    except Exception as e:
        print(f"   ❌ 连接异常: {e}")
    
    return False

if __name__ == "__main__":
    test_127_connection()
