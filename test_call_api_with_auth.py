#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
测试通话API功能（带认证）
"""

import requests
import json
import time
from datetime import datetime

# 配置
BASE_URL = "http://localhost:8080"
API_BASE = f"{BASE_URL}/api"

def get_auth_token():
    """获取认证token"""
    print("获取认证token...")
    try:
        # 使用测试验证码登录
        login_data = {
            "phone": "19825012076",
            "code": "123456"
        }
        
        response = requests.post(f"{API_BASE}/auth/login-with-code", 
                               json=login_data, 
                               timeout=10)
        
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                login_response = data.get('data', {})
                token = login_response.get('token')
                print(f"✅ 登录成功，获取到token")
                return token
            else:
                print(f"❌ 登录失败: {data.get('message')}")
                return None
        else:
            print(f"❌ 登录请求失败: {response.status_code}")
            return None
    except Exception as e:
        print(f"❌ 获取token异常: {e}")
        return None

def test_call_api_with_auth():
    """测试通话API（带认证）"""
    print("=" * 60)
    print("测试通话API功能（带认证）")
    print("=" * 60)
    
    # 获取认证token
    token = get_auth_token()
    if not token:
        print("❌ 无法获取认证token，测试终止")
        return False
    
    headers = {"Authorization": f"Bearer {token}"}
    
    # 测试通话API健康检查
    print("\n1. 测试通话API健康检查...")
    try:
        response = requests.get(f"{API_BASE}/call/health", headers=headers, timeout=10)
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                print("✅ 通话API健康检查通过")
            else:
                print(f"❌ 通话API健康检查失败: {data.get('message')}")
                return False
        else:
            print(f"❌ 通话API健康检查失败: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ 通话API健康检查异常: {e}")
        return False
    
    # 测试发起通话
    print("\n2. 测试发起通话...")
    try:
        # 发起通话请求
        call_request = {
            "receiverId": 1002  # 向用户1002发起通话
        }
        
        response = requests.post(f"{API_BASE}/call/initiate", 
                               headers=headers, 
                               json=call_request, 
                               timeout=10)
        
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                call_data = data.get('data', {})
                call_session_id = call_data.get('callSessionId')
                print(f"✅ 通话发起成功")
                print(f"   通话会话ID: {call_session_id}")
                print(f"   发起方ID: {call_data.get('callerId')}")
                print(f"   接收方ID: {call_data.get('receiverId')}")
                print(f"   通话状态: {call_data.get('status')}")
                print(f"   费率: {call_data.get('rate')}元/分钟")
                print(f"   发起方余额: {call_data.get('callerBalance')}元")
                print(f"   接收方在线: {call_data.get('isOnline')}")
                
                # 测试获取通话状态
                print("\n3. 测试获取通话状态...")
                status_response = requests.get(f"{API_BASE}/call/status/{call_session_id}", 
                                             headers=headers, 
                                             timeout=10)
                
                if status_response.status_code == 200:
                    status_data = status_response.json()
                    if status_data.get('success'):
                        print("✅ 获取通话状态成功")
                        status_info = status_data.get('data', {})
                        print(f"   当前状态: {status_info.get('status')}")
                        print(f"   创建时间: {status_info.get('createdAt')}")
                    else:
                        print(f"❌ 获取通话状态失败: {status_data.get('message')}")
                else:
                    print(f"❌ 获取通话状态请求失败: {status_response.status_code}")
                
                # 测试接受通话
                print("\n4. 测试接受通话...")
                accept_request = {
                    "callSessionId": call_session_id
                }
                
                accept_response = requests.post(f"{API_BASE}/call/accept", 
                                              headers=headers, 
                                              json=accept_request, 
                                              timeout=10)
                
                if accept_response.status_code == 200:
                    accept_data = accept_response.json()
                    if accept_data.get('success'):
                        print("✅ 通话接受成功")
                        accept_info = accept_data.get('data', {})
                        print(f"   通话状态: {accept_info.get('status')}")
                        print(f"   开始时间: {accept_info.get('startTime')}")
                    else:
                        print(f"❌ 通话接受失败: {accept_data.get('message')}")
                else:
                    print(f"❌ 通话接受请求失败: {accept_response.status_code}")
                
                # 等待几秒钟模拟通话
                print("\n5. 模拟通话进行中...")
                time.sleep(3)
                
                # 测试结束通话
                print("\n6. 测试结束通话...")
                end_request = {
                    "callSessionId": call_session_id,
                    "reason": "NORMAL"
                }
                
                end_response = requests.post(f"{API_BASE}/call/end", 
                                           headers=headers, 
                                           json=end_request, 
                                           timeout=10)
                
                if end_response.status_code == 200:
                    end_data = end_response.json()
                    if end_data.get('success'):
                        print("✅ 通话结束成功")
                        end_info = end_data.get('data', {})
                        print(f"   通话状态: {end_info.get('status')}")
                        print(f"   通话时长: {end_info.get('duration')}秒")
                        print(f"   总费用: {end_info.get('totalCost')}元")
                        print(f"   结束时间: {end_info.get('endTime')}")
                    else:
                        print(f"❌ 通话结束失败: {end_data.get('message')}")
                else:
                    print(f"❌ 通话结束请求失败: {end_response.status_code}")
                
                return True
            else:
                print(f"❌ 通话发起失败: {data.get('message')}")
                return False
        else:
            print(f"❌ 通话发起请求失败: {response.status_code}")
            print(f"   响应内容: {response.text}")
            return False
    except Exception as e:
        print(f"❌ 发起通话异常: {e}")
        return False

def test_offline_user_call_with_auth():
    """测试向离线用户发起通话（带认证）"""
    print("\n7. 测试向离线用户发起通话...")
    try:
        token = get_auth_token()
        if not token:
            return False
            
        headers = {"Authorization": f"Bearer {token}"}
        
        # 向离线用户发起通话
        call_request = {
            "receiverId": 1004  # 用户1004是离线状态
        }
        
        response = requests.post(f"{API_BASE}/call/initiate", 
                               headers=headers, 
                               json=call_request, 
                               timeout=10)
        
        if response.status_code == 200:
            data = response.json()
            if not data.get('success'):
                print("✅ 正确拒绝离线用户通话")
                print(f"   错误信息: {data.get('message')}")
                return True
            else:
                print("❌ 应该拒绝离线用户通话")
                return False
        else:
            print(f"❌ 请求失败: {response.status_code}")
            print(f"   响应内容: {response.text}")
            return False
    except Exception as e:
        print(f"❌ 测试离线用户通话异常: {e}")
        return False

def generate_call_test_report():
    """生成通话测试报告"""
    print("\n" + "=" * 60)
    print("通话API测试报告（带认证）")
    print("=" * 60)
    
    # 测试结果统计
    test_results = []
    
    # 1. 测试通话API
    call_ok = test_call_api_with_auth()
    test_results.append(("通话API功能测试", call_ok))
    
    # 2. 测试离线用户通话
    offline_ok = test_offline_user_call_with_auth()
    test_results.append(("离线用户通话测试", offline_ok))
    
    # 统计结果
    total_tests = len(test_results)
    passed_tests = sum(1 for _, success in test_results if success)
    failed_tests = total_tests - passed_tests
    
    print(f"\n测试结果统计:")
    print(f"总测试数: {total_tests}")
    print(f"通过: {passed_tests}")
    print(f"失败: {failed_tests}")
    print(f"成功率: {(passed_tests/total_tests)*100:.1f}%")
    
    print(f"\n详细结果:")
    for test_name, success in test_results:
        status = "✅ 通过" if success else "❌ 失败"
        print(f"  {test_name}: {status}")
    
    # 生成建议
    print(f"\n建议:")
    if passed_tests == total_tests:
        print("  🎉 所有测试通过！通话API功能正常。")
        print("  📱 建议在Android应用中实现以下功能：")
        print("     - 发起视频通话")
        print("     - 接受/拒绝通话")
        print("     - 实时计费显示")
        print("     - 通话历史记录")
    else:
        print("  ⚠️  部分测试失败，需要检查以下问题：")
        for test_name, success in test_results:
            if not success:
                print(f"     - {test_name}")
        print("  🔧 建议修复问题后重新测试")
    
    return passed_tests == total_tests

if __name__ == "__main__":
    print(f"开始通话API测试（带认证）...")
    print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # 检查后端服务
    try:
        response = requests.get(f"{BASE_URL}/api/health", timeout=5)
        if response.status_code != 200:
            print("❌ 后端服务不可用，请先启动后端服务")
            exit(1)
    except:
        print("❌ 无法连接到后端服务，请先启动后端服务")
        exit(1)
    
    # 运行通话测试
    success = generate_call_test_report()
    
    if success:
        print(f"\n🎉 通话API测试完成！")
        exit(0)
    else:
        print(f"\n❌ 通话API测试失败！")
        exit(1)
