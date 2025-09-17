#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time
import sys

def test_api_balance():
    """测试用户 19825012076 的余额API接口"""
    
    print("=" * 60)
    print("测试用户 19825012076 的余额API接口")
    print("=" * 60)
    print(f"测试时间: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    print()
    
    # 尝试不同的后端地址
    backend_urls = [
        "http://localhost:8080",
        "http://127.0.0.1:8080",
        "http://10.0.2.2:8080"
    ]
    
    phone = "19825012076"
    
    for base_url in backend_urls:
        print(f"🔗 尝试连接到后端: {base_url}")
        print("-" * 40)
        
        try:
            # 1. 测试后端服务是否可达
            print("1. 检查后端服务状态...")
            health_response = requests.get(f"{base_url}/actuator/health", timeout=5)
            print(f"   健康检查状态码: {health_response.status_code}")
            
            if health_response.status_code == 200:
                print("   ✅ 后端服务运行正常")
            else:
                print(f"   ⚠️  健康检查返回: {health_response.status_code}")
            
        except Exception as e:
            print(f"   ❌ 健康检查失败: {e}")
        
        try:
            # 2. 获取验证码
            print(f"\n2. 为手机号 {phone} 获取验证码...")
            sms_url = f"{base_url}/api/auth/send-sms"
            sms_data = {
                "phone": phone
            }
            sms_headers = {
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
            
            print(f"   请求URL: {sms_url}")
            print(f"   请求数据: {json.dumps(sms_data, ensure_ascii=False)}")
            
            sms_response = requests.post(sms_url, json=sms_data, headers=sms_headers, timeout=10)
            print(f"   响应状态码: {sms_response.status_code}")
            print(f"   响应头: {dict(sms_response.headers)}")
            
            if sms_response.status_code == 200:
                sms_result = sms_response.json()
                print(f"   ✅ 验证码请求成功")
                print(f"   响应数据: {json.dumps(sms_result, indent=2, ensure_ascii=False)}")
                
                if sms_result.get('success'):
                    verification_code = sms_result.get('data')
                    print(f"   📱 获取到验证码: {verification_code}")
                    
                    # 3. 使用验证码登录
                    print(f"\n3. 使用验证码 {verification_code} 登录...")
                    login_url = f"{base_url}/api/auth/login"
                    login_data = {
                        "phone": phone,
                        "verificationCode": verification_code
                    }
                    
                    print(f"   请求URL: {login_url}")
                    print(f"   请求数据: {json.dumps(login_data, ensure_ascii=False)}")
                    
                    login_response = requests.post(login_url, json=login_data, headers=sms_headers, timeout=10)
                    print(f"   响应状态码: {login_response.status_code}")
                    print(f"   响应头: {dict(login_response.headers)}")
                    
                    if login_response.status_code == 200:
                        login_result = login_response.json()
                        print(f"   ✅ 登录请求成功")
                        print(f"   响应数据: {json.dumps(login_result, indent=2, ensure_ascii=False)}")
                        
                        if login_result.get('success'):
                            token = login_result.get('data', {}).get('token')
                            user_info = login_result.get('data', {}).get('user', {})
                            print(f"   🎉 登录成功！")
                            print(f"   Token: {token[:50]}...")
                            print(f"   用户信息: {json.dumps(user_info, indent=2, ensure_ascii=False)}")
                            
                            # 4. 使用token获取余额
                            print(f"\n4. 使用token获取余额...")
                            balance_url = f"{base_url}/api/wallet/balance"
                            balance_headers = {
                                "Authorization": token,
                                "Content-Type": "application/json",
                                "Accept": "application/json"
                            }
                            
                            print(f"   请求URL: {balance_url}")
                            print(f"   请求头: {json.dumps({k: v for k, v in balance_headers.items() if k != 'Authorization'}, ensure_ascii=False)}")
                            print(f"   Authorization: {token[:30]}...")
                            
                            balance_response = requests.get(balance_url, headers=balance_headers, timeout=10)
                            print(f"   响应状态码: {balance_response.status_code}")
                            print(f"   响应头: {dict(balance_response.headers)}")
                            
                            if balance_response.status_code == 200:
                                balance_result = balance_response.json()
                                print(f"   ✅ 余额API调用成功!")
                                print(f"   响应数据: {json.dumps(balance_result, indent=2, ensure_ascii=False)}")
                                
                                if balance_result.get('success') and balance_result.get('data'):
                                    balance = balance_result['data'].get('balance')
                                    print(f"\n💰 用户 {phone} 的余额: {balance}")
                                    
                                    # 显示余额详情
                                    if balance is not None:
                                        print(f"   余额类型: {type(balance)}")
                                        print(f"   余额值: {balance}")
                                        if isinstance(balance, (int, float)):
                                            print(f"   格式化余额: {balance:,.2f}")
                                    
                                    print(f"\n🎉 API测试成功完成！")
                                    return True
                                else:
                                    print(f"   ❌ 余额API返回失败: {balance_result.get('message', '未知错误')}")
                            else:
                                print(f"   ❌ 余额API调用失败: {balance_response.status_code}")
                                print(f"   响应内容: {balance_response.text}")
                        else:
                            print(f"   ❌ 登录失败: {login_result.get('message', '未知错误')}")
                    else:
                        print(f"   ❌ 登录请求失败: {login_response.status_code}")
                        print(f"   响应内容: {login_response.text}")
                else:
                    print(f"   ❌ 获取验证码失败: {sms_result.get('message', '未知错误')}")
            else:
                print(f"   ❌ 验证码请求失败: {sms_response.status_code}")
                print(f"   响应内容: {sms_response.text}")
                
        except Exception as e:
            print(f"   ❌ 请求异常: {e}")
            import traceback
            traceback.print_exc()
        
        print(f"\n" + "=" * 40)
        print(f"后端 {base_url} 测试完成")
        print("=" * 40)
        print()
    
    print("❌ 所有后端地址都测试失败")
    return False

def test_with_curl():
    """使用curl命令测试API"""
    print("\n" + "=" * 60)
    print("使用curl命令测试API")
    print("=" * 60)
    
    phone = "19825012076"
    
    # 1. 获取验证码
    print("1. 使用curl获取验证码...")
    curl_sms = f'curl -X POST "http://localhost:8080/api/auth/send-sms" -H "Content-Type: application/json" -d "{{\\"phone\\": \\"{phone}\\"}}"'
    print(f"命令: {curl_sms}")
    
    try:
        import subprocess
        result = subprocess.run(curl_sms, shell=True, capture_output=True, text=True, timeout=10)
        print(f"返回码: {result.returncode}")
        print(f"输出: {result.stdout}")
        print(f"错误: {result.stderr}")
    except Exception as e:
        print(f"curl执行失败: {e}")

if __name__ == "__main__":
    print("开始测试用户 19825012076 的余额API接口...")
    
    # 测试API
    success = test_api_balance()
    
    if not success:
        print("\n尝试使用curl命令测试...")
        test_with_curl()
    
    print(f"\n测试完成")
