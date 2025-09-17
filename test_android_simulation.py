#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

def test_android_simulation():
    """模拟Android应用的请求方式"""
    
    print("=" * 60)
    print("模拟Android应用的请求方式")
    print("=" * 60)
    print(f"测试时间: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    print()
    
    # 模拟Android应用使用的URL
    base_url = "http://localhost:8080/api"
    phone = "19825012076"
    
    try:
        # 1. 模拟Android发送验证码请求
        print("1. 模拟Android发送验证码请求...")
        sms_url = f"{base_url}/auth/send-code"
        sms_params = {"phone": phone}
        sms_headers = {
            "Content-Type": "application/json",
            "Accept": "application/json",
            "User-Agent": "Android-App"
        }
        
        print(f"   请求URL: {sms_url}")
        print(f"   请求参数: {sms_params}")
        print(f"   请求头: {sms_headers}")
        
        sms_response = requests.post(sms_url, params=sms_params, headers=sms_headers, timeout=10)
        print(f"   响应状态码: {sms_response.status_code}")
        print(f"   响应头: {dict(sms_response.headers)}")
        
        if sms_response.status_code == 200:
            sms_result = sms_response.json()
            print(f"   ✅ 验证码请求成功")
            print(f"   响应数据: {json.dumps(sms_result, indent=2, ensure_ascii=False)}")
            
            if sms_result.get('success'):
                verification_code = sms_result.get('data')
                print(f"   📱 获取到验证码: {verification_code}")
                
                # 2. 模拟Android登录请求
                print(f"\n2. 模拟Android登录请求...")
                login_url = f"{base_url}/auth/login-with-code"
                login_params = {"phone": phone, "code": verification_code}
                
                print(f"   请求URL: {login_url}")
                print(f"   请求参数: {login_params}")
                
                login_response = requests.post(login_url, params=login_params, headers=sms_headers, timeout=10)
                print(f"   响应状态码: {login_response.status_code}")
                
                if login_response.status_code == 200:
                    login_result = login_response.json()
                    print(f"   ✅ 登录请求成功")
                    
                    if login_result.get('success'):
                        token = login_result.get('data', {}).get('token')
                        print(f"   🎉 登录成功，Token: {token[:30]}...")
                        
                        # 3. 模拟Android获取余额请求
                        print(f"\n3. 模拟Android获取余额请求...")
                        balance_url = f"{base_url}/wallet/balance"
                        balance_headers = {
                            "Authorization": f"Bearer {token}",
                            "Content-Type": "application/json",
                            "Accept": "application/json",
                            "User-Agent": "Android-App"
                        }
                        
                        print(f"   请求URL: {balance_url}")
                        print(f"   Authorization: Bearer {token[:30]}...")
                        
                        balance_response = requests.get(balance_url, headers=balance_headers, timeout=10)
                        print(f"   响应状态码: {balance_response.status_code}")
                        
                        if balance_response.status_code == 200:
                            balance_result = balance_response.json()
                            print(f"   ✅ 余额API调用成功!")
                            
                            if balance_result.get('success') and balance_result.get('data'):
                                balance = balance_result['data'].get('balance')
                                print(f"\n💰 用户 {phone} 的余额: {balance}")
                                print(f"🎉 Android模拟测试完全成功！")
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

def show_android_troubleshooting():
    """显示Android问题排查建议"""
    print("\n" + "=" * 60)
    print("Android问题排查建议")
    print("=" * 60)
    
    print("\n🔍 可能的问题原因:")
    print("1. Android模拟器网络问题:")
    print("   - 模拟器无法访问localhost")
    print("   - 需要检查模拟器网络设置")
    
    print("\n2. 应用缓存问题:")
    print("   - 应用可能使用了旧的网络配置")
    print("   - 需要清除应用数据重新安装")
    
    print("\n3. 网络权限问题:")
    print("   - 检查AndroidManifest.xml中的网络权限")
    print("   - 确保应用有INTERNET权限")
    
    print("\n🛠️ 解决步骤:")
    print("1. 清除应用数据:")
    print("   - 设置 → 应用 → 知聊 → 存储 → 清除数据")
    
    print("\n2. 重新安装应用:")
    print("   - 卸载应用")
    print("   - 重新安装APK")
    
    print("\n3. 检查网络配置:")
    print("   - 确保使用localhost:8080")
    print("   - 检查后端服务是否在0.0.0.0:8080监听")
    
    print("\n4. 使用真实设备测试:")
    print("   - 如果模拟器有问题，可以尝试真实设备")
    print("   - 真实设备需要确保在同一网络环境")

if __name__ == "__main__":
    print("开始模拟Android应用请求...")
    
    success = test_android_simulation()
    
    if success:
        print("\n✅ 后端API完全正常，问题在Android端")
    else:
        print("\n❌ 后端API可能有问题")
    
    show_android_troubleshooting()
    
    print(f"\n测试完成")
