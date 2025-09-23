#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
简单数据库测试
"""

import requests
import json

def test_database_simple():
    """测试数据库连接"""
    print("=== 测试数据库连接 ===")
    
    # 测试数据库修复接口
    try:
        response = requests.post("http://localhost:8080/api/admin/database/fix/test", timeout=10)
        print(f"数据库测试响应: {response.status_code}")
        print(f"数据库测试内容: {response.text}")
        
        if response.status_code == 200:
            print("✅ 数据库连接正常")
            return True
        else:
            print("❌ 数据库连接异常")
            return False
    except Exception as e:
        print(f"❌ 数据库连接异常: {e}")
        return False

def test_health_check():
    """测试健康检查"""
    print("=== 测试健康检查 ===")
    
    try:
        response = requests.get("http://localhost:8080/api/health", timeout=10)
        print(f"健康检查响应: {response.status_code}")
        print(f"健康检查内容: {response.text}")
        
        if response.status_code == 200:
            print("✅ 健康检查成功")
            return True
        else:
            print("❌ 健康检查失败")
            return False
    except Exception as e:
        print(f"❌ 健康检查异常: {e}")
        return False

if __name__ == "__main__":
    test_health_check()
    test_database_simple()
