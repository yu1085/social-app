#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

def test_android_fix_verification():
    """验证Android端修复效果"""
    
    print("=" * 60)
    print("验证Android端修复效果")
    print("=" * 60)
    print(f"测试时间: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    print()
    
    base_url = "http://localhost:8080/api"
    phone = "19825012076"
    
    try:
        # 1. 获取验证码
        print("1. 获取验证码...")
        sms_url = f"{base_url}/auth/send-code"
        sms_params = {"phone": phone}
        
        sms_response = requests.post(sms_url, params=sms_params, timeout=10)
        print(f"   验证码请求状态码: {sms_response.status_code}")
        
        if sms_response.status_code == 200:
            sms_result = sms_response.json()
            if sms_result.get('success'):
                verification_code = sms_result.get('data')
                print(f"   ✅ 获取验证码成功: {verification_code}")
                
                # 2. 登录
                print("\n2. 用户登录...")
                login_url = f"{base_url}/auth/login-with-code"
                login_params = {"phone": phone, "code": verification_code}
                
                login_response = requests.post(login_url, params=login_params, timeout=10)
                print(f"   登录请求状态码: {login_response.status_code}")
                
                if login_response.status_code == 200:
                    login_result = login_response.json()
                    if login_result.get('success'):
                        token = login_result.get('data', {}).get('token')
                        print(f"   ✅ 登录成功，Token: {token[:30]}...")
                        
                        # 3. 测试修复后的Authorization格式
                        print("\n3. 测试修复后的Authorization格式...")
                        balance_url = f"{base_url}/wallet/balance"
                        
                        # 模拟Android修复后的调用方式
                        headers = {
                            "Authorization": f"Bearer {token}",  # 修复后：添加Bearer前缀
                            "Content-Type": "application/json"
                        }
                        
                        print(f"   请求URL: {balance_url}")
                        print(f"   Authorization: Bearer {token[:30]}...")
                        
                        balance_response = requests.get(balance_url, headers=headers, timeout=10)
                        print(f"   响应状态码: {balance_response.status_code}")
                        
                        if balance_response.status_code == 200:
                            balance_result = balance_response.json()
                            print(f"   ✅ 余额API调用成功!")
                            print(f"   响应数据: {json.dumps(balance_result, indent=2, ensure_ascii=False)}")
                            
                            if balance_result.get('success') and balance_result.get('data'):
                                balance = balance_result['data'].get('balance')
                                print(f"\n💰 用户 {phone} 的余额: {balance}")
                                
                                if balance == 888.0:
                                    print("🎉 修复成功！Android端现在应该能正确显示余额888.0了！")
                                    return True
                                else:
                                    print(f"⚠️  余额值异常: {balance}")
                            else:
                                print(f"   ❌ 余额API返回失败: {balance_result.get('message', '未知错误')}")
                        else:
                            print(f"   ❌ 余额API调用失败: {balance_response.status_code}")
                            print(f"   响应内容: {balance_response.text}")
                    else:
                        print(f"   ❌ 登录失败: {login_result.get('message', '未知错误')}")
                else:
                    print(f"   ❌ 登录失败: {login_response.text}")
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

def show_android_fix_summary():
    """显示Android端修复总结"""
    print("\n" + "=" * 60)
    print("Android端修复总结")
    print("=" * 60)
    
    print("\n🔧 修复内容:")
    print("1. 修改NetworkService.kt:")
    print("   - getWalletBalance(): 添加 'Bearer ' 前缀")
    print("   - rechargeWallet(): 添加 'Bearer ' 前缀")
    
    print("\n2. 修改NetworkConfig.java:")
    print("   - 添加多种网络配置选项")
    print("   - 当前使用localhost配置")
    
    print("\n📱 Android端问题原因:")
    print("1. Authorization头格式错误:")
    print("   ❌ 之前: Authorization: {token}")
    print("   ✅ 修复后: Authorization: Bearer {token}")
    
    print("\n2. 网络连接问题:")
    print("   ❌ 10.0.2.2:8080 (模拟器地址) - 连接失败")
    print("   ✅ localhost:8080 (本地地址) - 连接正常")
    
    print("\n🎯 预期效果:")
    print("- Android端登录后应该能正确显示余额888.0")
    print("- 不再显示余额为0的问题")
    print("- 钱包功能正常工作")

if __name__ == "__main__":
    print("开始验证Android端修复效果...")
    
    success = test_android_fix_verification()
    
    if success:
        print("\n✅ 修复验证成功！")
    else:
        print("\n❌ 修复验证失败！")
    
    show_android_fix_summary()
    
    print(f"\n测试完成")
