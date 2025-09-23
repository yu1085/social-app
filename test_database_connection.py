#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

def test_database_connection():
    """测试数据库连接和表结构"""
    base_url = "http://localhost:8080"
    
    print("测试数据库连接和表结构...")
    
    # 测试不需要认证的API
    try:
        print("\n1. 测试等级规则API...")
        response = requests.get(f"{base_url}/api/wealth-level/rules")
        print(f"状态码: {response.status_code}")
        if response.status_code == 200:
            data = response.json()
            print(f"成功获取等级规则: {len(data.get('data', []))} 条记录")
        else:
            print(f"失败: {response.text}")
    except Exception as e:
        print(f"等级规则API测试失败: {e}")
    
    # 测试健康检查
    try:
        print("\n2. 测试健康检查...")
        response = requests.get(f"{base_url}/actuator/health")
        print(f"状态码: {response.status_code}")
        if response.status_code == 200:
            print(f"健康检查通过: {response.text}")
        else:
            print(f"健康检查失败: {response.text}")
    except Exception as e:
        print(f"健康检查失败: {e}")
    
    # 测试数据库信息
    try:
        print("\n3. 测试数据库信息...")
        response = requests.get(f"{base_url}/actuator/info")
        print(f"状态码: {response.status_code}")
        if response.status_code == 200:
            print(f"应用信息: {response.text}")
        else:
            print(f"应用信息获取失败: {response.text}")
    except Exception as e:
        print(f"应用信息获取失败: {e}")

if __name__ == "__main__":
    test_database_connection()
