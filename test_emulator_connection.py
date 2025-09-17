#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json
import time

def test_emulator_connection():
    """测试Android模拟器地址连接"""
    
    print("=" * 60)
    print("测试Android模拟器地址连接")
    print("=" * 60)
    print(f"测试时间: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    print()
    
    # 测试不同的地址
    urls_to_test = [
        ("Android模拟器地址", "http://10.0.2.2:8080/api"),
        ("本地地址", "http://localhost:8080/api"),
        ("127.0.0.1地址", "http://127.0.0.1:8080/api")
    ]
    
    phone = "19825012076"
    
    for url_name, base_url in urls_to_test:
        print(f"\n🔗 测试 {url_name}: {base_url}")
        print("-" * 40)
        
        try:
            # 1. 测试健康检查
            print("1. 测试健康检查...")
            health_url = f"{base_url}/health"
            health_response = requests.get(health_url, timeout=5)
            print(f"   健康检查状态码: {health_response.status_code}")
            
            if health_response.status_code == 200:
                print("   ✅ 健康检查通过")
                
                # 2. 测试获取验证码
                print("2. 测试获取验证码...")
                sms_url = f"{base_url}/auth/send-code"
                sms_params = {"phone": phone}
                
                sms_response = requests.post(sms_url, params=sms_params, timeout=10)
                print(f"   验证码请求状态码: {sms_response.status_code}")
                
                if sms_response.status_code == 200:
                    sms_result = sms_response.json()
                    if sms_result.get('success'):
                        verification_code = sms_result.get('data')
                        print(f"   ✅ 获取验证码成功: {verification_code}")
                        print(f"   🎉 {url_name} 连接正常，Android应用可以使用此地址！")
                        return base_url
                    else:
                        print(f"   ❌ 获取验证码失败: {sms_result.get('message')}")
                else:
                    print(f"   ❌ 验证码请求失败: {sms_response.text}")
            else:
                print(f"   ❌ 健康检查失败: {health_response.text}")
                
        except Exception as e:
            print(f"   ❌ 连接异常: {e}")
    
    print("\n❌ 所有地址都无法连接")
    return None

def show_android_network_solution():
    """显示Android网络连接解决方案"""
    print("\n" + "=" * 60)
    print("Android网络连接解决方案")
    print("=" * 60)
    
    print("\n📱 Android模拟器网络配置说明:")
    print("1. Android模拟器访问宿主机localhost的方法:")
    print("   - localhost → 10.0.2.2")
    print("   - 127.0.0.1 → 10.0.2.2")
    print("   - 这是Android模拟器的特殊网络映射")
    
    print("\n🔧 当前配置:")
    print("- 已修改NetworkConfig.java使用10.0.2.2:8080")
    print("- 这是Android模拟器访问宿主机的正确地址")
    
    print("\n📋 如果仍然连接失败，请检查:")
    print("1. 后端服务是否在0.0.0.0:8080上监听（不是127.0.0.1:8080）")
    print("2. 防火墙是否阻止了8080端口")
    print("3. 网络连接是否正常")
    
    print("\n🔄 重新编译步骤:")
    print("1. 运行: .\\gradlew.bat assembleDebug --no-daemon -x lint")
    print("2. 重新安装应用到模拟器")
    print("3. 测试登录功能")

if __name__ == "__main__":
    print("开始测试Android模拟器连接...")
    
    working_url = test_emulator_connection()
    
    if working_url:
        print(f"\n✅ 找到可用的地址: {working_url}")
    else:
        print("\n❌ 没有找到可用的地址")
    
    show_android_network_solution()
    
    print(f"\n测试完成")
