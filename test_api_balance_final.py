#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

def test_api_balance_final():
    """使用正确的Authorization头格式测试用户 19825012076 的余额"""
    
    print("=" * 60)
    print("使用正确的Authorization头格式测试用户 19825012076 的余额")
    print("=" * 60)
    print(f"测试时间: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    print()
    
    base_url = "http://localhost:8080"
    phone = "19825012076"
    
    try:
        # 1. 获取验证码
        print("1. 为手机号获取验证码...")
        sms_url = f"{base_url}/api/auth/send-code"
        sms_params = {
            "phone": phone
        }
        sms_headers = {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
        
        print(f"   请求URL: {sms_url}")
        print(f"   请求参数: {sms_params}")
        
        sms_response = requests.post(sms_url, params=sms_params, headers=sms_headers, timeout=10)
        print(f"   响应状态码: {sms_response.status_code}")
        
        if sms_response.status_code == 200:
            sms_result = sms_response.json()
            print(f"   ✅ 验证码请求成功")
            print(f"   响应数据: {json.dumps(sms_result, indent=2, ensure_ascii=False)}")
            
            if sms_result.get('success'):
                verification_code = sms_result.get('data')
                print(f"   📱 获取到验证码: {verification_code}")
                
                # 2. 使用验证码登录
                print(f"\n2. 使用验证码 {verification_code} 登录...")
                login_url = f"{base_url}/api/auth/login-with-code"
                login_params = {
                    "phone": phone,
                    "code": verification_code
                }
                
                print(f"   请求URL: {login_url}")
                print(f"   请求参数: {login_params}")
                
                login_response = requests.post(login_url, params=login_params, headers=sms_headers, timeout=10)
                print(f"   响应状态码: {login_response.status_code}")
                
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
                        
                        # 3. 使用token获取余额 - 修正Authorization头格式
                        print(f"\n3. 使用token获取余额...")
                        balance_url = f"{base_url}/api/wallet/balance"
                        balance_headers = {
                            "Authorization": f"Bearer {token}",  # 修正：添加Bearer前缀
                            "Content-Type": "application/json",
                            "Accept": "application/json"
                        }
                        
                        print(f"   请求URL: {balance_url}")
                        print(f"   请求头: {json.dumps({k: v for k, v in balance_headers.items() if k != 'Authorization'}, ensure_ascii=False)}")
                        print(f"   Authorization: Bearer {token[:30]}...")
                        
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
    
    return False

def test_with_curl_final():
    """使用正确的curl命令测试API"""
    print("\n" + "=" * 60)
    print("使用正确的curl命令测试API")
    print("=" * 60)
    
    phone = "19825012076"
    
    # 1. 获取验证码
    print("1. 使用curl获取验证码...")
    curl_sms = f'curl -X POST "http://localhost:8080/api/auth/send-code?phone={phone}" -H "Content-Type: application/json"'
    print(f"命令: {curl_sms}")
    
    try:
        import subprocess
        result = subprocess.run(curl_sms, shell=True, capture_output=True, text=True, timeout=10, encoding='utf-8')
        print(f"返回码: {result.returncode}")
        print(f"输出: {result.stdout}")
        print(f"错误: {result.stderr}")
        
        if result.returncode == 0 and result.stdout:
            try:
                response_data = json.loads(result.stdout)
                if response_data.get('success'):
                    verification_code = response_data.get('data')
                    print(f"获取到验证码: {verification_code}")
                    
                    # 2. 使用验证码登录
                    print(f"\n2. 使用验证码 {verification_code} 登录...")
                    curl_login = f'curl -X POST "http://localhost:8080/api/auth/login-with-code?phone={phone}&code={verification_code}" -H "Content-Type: application/json"'
                    print(f"命令: {curl_login}")
                    
                    login_result = subprocess.run(curl_login, shell=True, capture_output=True, text=True, timeout=10, encoding='utf-8')
                    print(f"返回码: {login_result.returncode}")
                    print(f"输出: {login_result.stdout}")
                    print(f"错误: {login_result.stderr}")
                    
                    if login_result.returncode == 0 and login_result.stdout:
                        try:
                            login_data = json.loads(login_result.stdout)
                            if login_data.get('success'):
                                token = login_data.get('data', {}).get('token')
                                print(f"获取到Token: {token[:50]}...")
                                
                                # 3. 获取余额 - 使用正确的Bearer格式
                                print(f"\n3. 使用Token获取余额...")
                                curl_balance = f'curl -X GET "http://localhost:8080/api/wallet/balance" -H "Authorization: Bearer {token}" -H "Content-Type: application/json"'
                                print(f"命令: {curl_balance}")
                                
                                balance_result = subprocess.run(curl_balance, shell=True, capture_output=True, text=True, timeout=10, encoding='utf-8')
                                print(f"返回码: {balance_result.returncode}")
                                print(f"输出: {balance_result.stdout}")
                                print(f"错误: {balance_result.stderr}")
                                
                                if balance_result.returncode == 0 and balance_result.stdout:
                                    try:
                                        balance_data = json.loads(balance_result.stdout)
                                        if balance_data.get('success'):
                                            balance = balance_data.get('data', {}).get('balance')
                                            print(f"\n💰 用户 {phone} 的余额: {balance}")
                                        else:
                                            print(f"余额API返回失败: {balance_data.get('message')}")
                                    except json.JSONDecodeError:
                                        print("余额响应不是有效的JSON")
                            else:
                                print(f"登录失败: {login_data.get('message')}")
                        except json.JSONDecodeError:
                            print("登录响应不是有效的JSON")
            except json.JSONDecodeError:
                print("验证码响应不是有效的JSON")
    except Exception as e:
        print(f"curl执行失败: {e}")

if __name__ == "__main__":
    print("开始使用正确的Authorization头格式测试用户 19825012076 的余额...")
    
    # 测试API
    success = test_api_balance_final()
    
    if not success:
        print("\n尝试使用curl命令测试...")
        test_with_curl_final()
    
    print(f"\n测试完成")
