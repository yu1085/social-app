#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Android前端集成测试脚本
测试用户API与Android应用的集成
"""

import requests
import json
import time
import sys
from datetime import datetime

# 配置
BASE_URL = "http://localhost:8080"
API_BASE = f"{BASE_URL}/api"

def test_user_api_integration():
    """测试用户API集成"""
    print("=" * 60)
    print("Android前端集成测试")
    print("=" * 60)
    
    # 测试用户卡片API
    print("\n1. 测试用户卡片API...")
    try:
        response = requests.get(f"{API_BASE}/users/home-cards?page=0&size=10", timeout=10)
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                user_cards = data.get('data', [])
                print(f"✅ 用户卡片API测试成功")
                print(f"   返回用户数量: {len(user_cards)}")
                
                # 显示前4个用户卡片信息（对应Android首页的4个卡片）
                for i, card in enumerate(user_cards[:4], 1):
                    print(f"   卡片{i}: {card.get('nickname', 'N/A')} - {card.get('status', 'N/A')} - {card.get('callPrice', 0)}/分钟")
                
                # 验证数据结构
                if user_cards:
                    sample_card = user_cards[0]
                    required_fields = ['id', 'nickname', 'avatar', 'age', 'location', 'bio', 'isOnline', 'status', 'callPrice']
                    missing_fields = [field for field in required_fields if field not in sample_card]
                    if missing_fields:
                        print(f"⚠️  缺少必要字段: {missing_fields}")
                    else:
                        print("✅ 数据结构验证通过")
                
                return user_cards
            else:
                print(f"❌ API返回失败: {data.get('message', 'Unknown error')}")
                return None
        else:
            print(f"❌ HTTP请求失败: {response.status_code}")
            return None
    except Exception as e:
        print(f"❌ 用户卡片API测试异常: {e}")
        return None

def test_user_detail_api(user_cards):
    """测试用户详情API"""
    print("\n2. 测试用户详情API...")
    
    if not user_cards:
        print("❌ 没有用户卡片数据，跳过详情测试")
        return False
    
    # 测试第一个用户的详情
    user_id = user_cards[0].get('id')
    if not user_id:
        print("❌ 用户ID为空，跳过详情测试")
        return False
    
    try:
        response = requests.get(f"{API_BASE}/users/{user_id}/detail", timeout=10)
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                user_detail = data.get('data', {})
                print(f"✅ 用户详情API测试成功")
                print(f"   用户: {user_detail.get('nickname', 'N/A')}")
                print(f"   状态: {user_detail.get('status', 'N/A')}")
                print(f"   价格: {user_detail.get('callPrice', 0)}/分钟")
                print(f"   消息价格: {user_detail.get('messagePrice', 0)}/条")
                return True
            else:
                print(f"❌ API返回失败: {data.get('message', 'Unknown error')}")
                return False
        else:
            print(f"❌ HTTP请求失败: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ 用户详情API测试异常: {e}")
        return False

def test_search_api():
    """测试搜索API"""
    print("\n3. 测试搜索API...")
    
    try:
        # 测试按位置搜索
        response = requests.get(f"{API_BASE}/users/search?location=北京&page=0&size=5", timeout=10)
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                search_results = data.get('data', [])
                print(f"✅ 搜索API测试成功")
                print(f"   北京用户数量: {len(search_results)}")
                return True
            else:
                print(f"❌ 搜索API返回失败: {data.get('message', 'Unknown error')}")
                return False
        else:
            print(f"❌ 搜索API请求失败: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ 搜索API测试异常: {e}")
        return False

def simulate_android_data_flow():
    """模拟Android数据流"""
    print("\n4. 模拟Android数据流...")
    
    try:
        # 模拟Android应用启动时加载用户卡片
        print("   模拟应用启动...")
        response = requests.get(f"{API_BASE}/users/home-cards?page=0&size=4", timeout=10)
        
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                user_cards = data.get('data', [])
                print(f"   ✅ 成功加载{len(user_cards)}个用户卡片")
                
                # 模拟用户点击第一个卡片
                if user_cards:
                    first_user = user_cards[0]
                    user_id = first_user.get('id')
                    print(f"   模拟点击用户: {first_user.get('nickname', 'N/A')}")
                    
                    # 模拟获取用户详情
                    detail_response = requests.get(f"{API_BASE}/users/{user_id}/detail", timeout=10)
                    if detail_response.status_code == 200:
                        detail_data = detail_response.json()
                        if detail_data.get('success'):
                            print("   ✅ 成功获取用户详情")
                            return True
                        else:
                            print("   ❌ 获取用户详情失败")
                            return False
                    else:
                        print("   ❌ 用户详情请求失败")
                        return False
                else:
                    print("   ❌ 没有用户卡片数据")
                    return False
            else:
                print("   ❌ 用户卡片API返回失败")
                return False
        else:
            print("   ❌ 用户卡片请求失败")
            return False
    except Exception as e:
        print(f"   ❌ 模拟Android数据流异常: {e}")
        return False

def generate_integration_report():
    """生成集成测试报告"""
    print("\n" + "=" * 60)
    print("Android前端集成测试报告")
    print("=" * 60)
    
    # 测试结果统计
    test_results = []
    
    # 1. 测试用户卡片API
    user_cards = test_user_api_integration()
    test_results.append(("用户卡片API", user_cards is not None))
    
    # 2. 测试用户详情API
    detail_success = test_user_detail_api(user_cards)
    test_results.append(("用户详情API", detail_success))
    
    # 3. 测试搜索API
    search_success = test_search_api()
    test_results.append(("搜索API", search_success))
    
    # 4. 模拟Android数据流
    flow_success = simulate_android_data_flow()
    test_results.append(("Android数据流", flow_success))
    
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
        print("  🎉 所有测试通过！Android前端可以正常集成用户API。")
        print("  📱 建议在Android应用中实现以下功能：")
        print("     - 首页用户卡片动态加载")
        print("     - 用户详情页面数据绑定")
        print("     - 搜索功能集成")
        print("     - 错误处理和加载状态显示")
    else:
        print("  ⚠️  部分测试失败，需要检查以下问题：")
        for test_name, success in test_results:
            if not success:
                print(f"     - {test_name}")
        print("  🔧 建议修复问题后重新测试")
    
    return passed_tests == total_tests

if __name__ == "__main__":
    print(f"开始Android前端集成测试...")
    print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # 检查后端服务
    try:
        response = requests.get(f"{BASE_URL}/api/health", timeout=5)
        if response.status_code != 200:
            print("❌ 后端服务不可用，请先启动后端服务")
            sys.exit(1)
    except:
        print("❌ 无法连接到后端服务，请先启动后端服务")
        sys.exit(1)
    
    # 运行集成测试
    success = generate_integration_report()
    
    if success:
        print(f"\n🎉 Android前端集成测试完成！")
        sys.exit(0)
    else:
        print(f"\n❌ Android前端集成测试失败！")
        sys.exit(1)
