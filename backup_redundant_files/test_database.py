#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
数据库连接测试
"""

import requests
import json

def test_database_health():
    """测试数据库健康状态"""
    url = "http://localhost:8080/api/health"
    
    print("=== 测试数据库健康状态 ===")
    print(f"请求URL: {url}")
    
    try:
        response = requests.get(url, timeout=10)
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            print("数据库连接正常")
            return True
        else:
            print("数据库连接异常")
            return False
    except Exception as e:
        print(f"请求异常: {e}")
        return False

def test_database_fix():
    """测试数据库修复接口"""
    url = "http://localhost:8080/api/admin/database/fix/init"
    
    print("=== 测试数据库修复接口 ===")
    print(f"请求URL: {url}")
    
    try:
        response = requests.post(url, timeout=30)
        print(f"响应状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        
        if response.status_code == 200:
            print("数据库修复成功")
            return True
        else:
            print("数据库修复失败")
            return False
    except Exception as e:
        print(f"请求异常: {e}")
        return False

if __name__ == "__main__":
    test_database_health()
    test_database_fix()
