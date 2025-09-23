#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
财富等级API直接测试脚本（跳过登录）
"""

import requests
import json

# 配置
BASE_URL = "http://localhost:8080"
API_BASE = f"{BASE_URL}/api"

def test_wealth_level_direct():
    """直接测试财富等级相关API"""
    print("=" * 60)
    print("财富等级API直接测试")
    print("=" * 60)
    
    # 使用一个模拟的token进行测试
    test_token = "test_token_123"
    headers = {"Authorization": f"Bearer {test_token}"}
    
    # 测试获取财富等级信息
    print("\n1. 测试获取财富等级信息...")
    try:
        wealth_response = requests.get(f"{API_BASE}/wealth-level/my-level", headers=headers)
        print(f"状态码: {wealth_response.status_code}")
        print(f"响应头: {dict(wealth_response.headers)}")
        
        if wealth_response.status_code == 200:
            wealth_result = wealth_response.json()
            print(f"响应: {json.dumps(wealth_result, indent=2, ensure_ascii=False)}")
        else:
            print(f"响应内容: {wealth_response.text}")
    except Exception as e:
        print(f"❌ 财富等级请求异常: {e}")
    
    # 测试获取财富排行榜（不需要认证）
    print("\n2. 测试获取财富排行榜...")
    try:
        ranking_response = requests.get(f"{API_BASE}/wealth-level/ranking?limit=5")
        print(f"状态码: {ranking_response.status_code}")
        
        if ranking_response.status_code == 200:
            ranking_result = ranking_response.json()
            print(f"响应: {json.dumps(ranking_result, indent=2, ensure_ascii=False)}")
        else:
            print(f"响应内容: {ranking_response.text}")
    except Exception as e:
        print(f"❌ 排行榜请求异常: {e}")
    
    # 测试获取等级规则（不需要认证）
    print("\n3. 测试获取等级规则...")
    try:
        rules_response = requests.get(f"{API_BASE}/wealth-level/rules")
        print(f"状态码: {rules_response.status_code}")
        
        if rules_response.status_code == 200:
            rules_result = rules_response.json()
            print(f"响应: {json.dumps(rules_result, indent=2, ensure_ascii=False)}")
        else:
            print(f"响应内容: {rules_response.text}")
    except Exception as e:
        print(f"❌ 规则请求异常: {e}")
    
    print("\n" + "=" * 60)
    print("财富等级API直接测试完成")
    print("=" * 60)

if __name__ == "__main__":
    test_wealth_level_direct()
