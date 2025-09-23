#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
财富等级API测试脚本
"""

import requests
import json
import sys
from datetime import datetime

# 配置
BASE_URL = "http://localhost:8080"
API_BASE = f"{BASE_URL}/api"

def test_wealth_level_api():
    """测试财富等级相关API"""
    print("=" * 60)
    print("财富等级API测试")
    print("=" * 60)
    
    # 测试用户登录获取token
    print("\n1. 测试用户登录...")
    login_data = {
        "username": "admin",
        "password": "admin123"
    }
    
    try:
        login_response = requests.post(f"{API_BASE}/auth/login", json=login_data)
        if login_response.status_code == 200:
            login_result = login_response.json()
            if login_result.get("success"):
                token = login_result["data"]["token"]
                print(f"✅ 登录成功，Token: {token[:20]}...")
            else:
                print(f"❌ 登录失败: {login_result.get('message')}")
                return False
        else:
            print(f"❌ 登录请求失败: {login_response.status_code}")
            return False
    except Exception as e:
        print(f"❌ 登录异常: {e}")
        return False
    
    # 测试获取财富等级信息
    print("\n2. 测试获取财富等级信息...")
    headers = {"Authorization": f"Bearer {token}"}
    
    try:
        wealth_response = requests.get(f"{API_BASE}/wealth-level/my-level", headers=headers)
        print(f"状态码: {wealth_response.status_code}")
        
        if wealth_response.status_code == 200:
            wealth_result = wealth_response.json()
            print(f"响应: {json.dumps(wealth_result, indent=2, ensure_ascii=False)}")
            
            if wealth_result.get("success"):
                data = wealth_result["data"]
                print(f"✅ 财富等级信息获取成功:")
                print(f"   - 用户ID: {data.get('userId')}")
                print(f"   - 财富值: {data.get('wealthValue')}")
                print(f"   - 等级名称: {data.get('levelName')}")
                print(f"   - 等级图标: {data.get('levelIcon')}")
                print(f"   - 等级颜色: {data.get('levelColor')}")
                print(f"   - 进度百分比: {data.get('progressPercentage')}%")
                print(f"   - 下一等级: {data.get('nextLevelName')}")
                print(f"   - 下一等级要求: {data.get('nextLevelRequirement')}")
                print(f"   - 用户排名: {data.get('userRank')}")
            else:
                print(f"❌ 获取财富等级失败: {wealth_result.get('message')}")
        else:
            print(f"❌ 财富等级请求失败: {wealth_response.status_code}")
            print(f"响应内容: {wealth_response.text}")
    except Exception as e:
        print(f"❌ 财富等级请求异常: {e}")
    
    # 测试获取用户特权
    print("\n3. 测试获取用户特权...")
    try:
        privilege_response = requests.get(f"{API_BASE}/wealth-level/privileges", headers=headers)
        print(f"状态码: {privilege_response.status_code}")
        
        if privilege_response.status_code == 200:
            privilege_result = privilege_response.json()
            print(f"响应: {json.dumps(privilege_result, indent=2, ensure_ascii=False)}")
            
            if privilege_result.get("success"):
                privileges = privilege_result["data"]
                print(f"✅ 用户特权获取成功:")
                for i, privilege in enumerate(privileges, 1):
                    print(f"   {i}. {privilege}")
            else:
                print(f"❌ 获取用户特权失败: {privilege_result.get('message')}")
        else:
            print(f"❌ 特权请求失败: {privilege_response.status_code}")
            print(f"响应内容: {privilege_response.text}")
    except Exception as e:
        print(f"❌ 特权请求异常: {e}")
    
    # 测试获取财富排行榜
    print("\n4. 测试获取财富排行榜...")
    try:
        ranking_response = requests.get(f"{API_BASE}/wealth-level/ranking?limit=5")
        print(f"状态码: {ranking_response.status_code}")
        
        if ranking_response.status_code == 200:
            ranking_result = ranking_response.json()
            print(f"响应: {json.dumps(ranking_result, indent=2, ensure_ascii=False)}")
            
            if ranking_result.get("success"):
                rankings = ranking_result["data"]
                print(f"✅ 财富排行榜获取成功 (前{len(rankings)}名):")
                for i, ranking in enumerate(rankings, 1):
                    print(f"   {i}. 用户{ranking.get('userId')} - {ranking.get('levelName')} ({ranking.get('wealthValue')}财富值)")
            else:
                print(f"❌ 获取财富排行榜失败: {ranking_result.get('message')}")
        else:
            print(f"❌ 排行榜请求失败: {ranking_response.status_code}")
            print(f"响应内容: {ranking_response.text}")
    except Exception as e:
        print(f"❌ 排行榜请求异常: {e}")
    
    # 测试获取等级规则
    print("\n5. 测试获取等级规则...")
    try:
        rules_response = requests.get(f"{API_BASE}/wealth-level/rules")
        print(f"状态码: {rules_response.status_code}")
        
        if rules_response.status_code == 200:
            rules_result = rules_response.json()
            print(f"响应: {json.dumps(rules_result, indent=2, ensure_ascii=False)}")
            
            if rules_result.get("success"):
                rules = rules_result["data"]
                print(f"✅ 等级规则获取成功 (共{len(rules)}个等级):")
                for rule in rules:
                    print(f"   - {rule.get('levelName')}: {rule.get('minWealthValue')}-{rule.get('maxWealthValue') or '∞'} ({rule.get('description')})")
            else:
                print(f"❌ 获取等级规则失败: {rules_result.get('message')}")
        else:
            print(f"❌ 规则请求失败: {rules_response.status_code}")
            print(f"响应内容: {rules_response.text}")
    except Exception as e:
        print(f"❌ 规则请求异常: {e}")
    
    print("\n" + "=" * 60)
    print("财富等级API测试完成")
    print("=" * 60)

if __name__ == "__main__":
    test_wealth_level_api()