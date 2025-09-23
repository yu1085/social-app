#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
最终API测试脚本
测试所有修复后的接口
"""

import requests
import json
import time
from datetime import datetime

def log(message):
    """打印带时间戳的日志"""
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"[{timestamp}] {message}")

def test_swagger_access():
    """测试Swagger访问"""
    log("=== 测试Swagger访问 ===")
    try:
        response = requests.get("http://localhost:8080/swagger-ui.html", timeout=10)
        log(f"Swagger UI响应: {response.status_code}")
        if response.status_code == 200:
            log("✅ Swagger UI可访问")
            return True
        else:
            log(f"❌ Swagger UI不可访问: {response.text}")
            return False
    except Exception as e:
        log(f"❌ Swagger UI异常: {e}")
        return False

def test_api_docs_access():
    """测试API文档访问"""
    log("=== 测试API文档访问 ===")
    try:
        response = requests.get("http://localhost:8080/api-docs", timeout=10)
        log(f"API文档响应: {response.status_code}")
        if response.status_code == 200:
            log("✅ API文档可访问")
            return True
        else:
            log(f"❌ API文档不可访问: {response.text}")
            return False
    except Exception as e:
        log(f"❌ API文档异常: {e}")
        return False

def test_health_check():
    """测试健康检查"""
    log("=== 测试健康检查 ===")
    try:
        response = requests.get("http://localhost:8080/api/health", timeout=10)
        log(f"健康检查响应: {response.status_code}")
        if response.status_code == 200:
            log("✅ 健康检查成功")
            return True
        else:
            log(f"❌ 健康检查失败: {response.text}")
            return False
    except Exception as e:
        log(f"❌ 健康检查异常: {e}")
        return False

def test_database_connection():
    """测试数据库连接"""
    log("=== 测试数据库连接 ===")
    try:
        response = requests.post("http://localhost:8080/api/admin/database/fix/init", timeout=10)
        log(f"数据库连接响应: {response.status_code}")
        log(f"数据库连接内容: {response.text}")
        if response.status_code == 200:
            log("✅ 数据库连接正常")
            return True
        else:
            log(f"❌ 数据库连接异常")
            return False
    except Exception as e:
        log(f"❌ 数据库连接异常: {e}")
        return False

def test_user_registration():
    """测试用户注册"""
    log("=== 测试用户注册 ===")
    try:
        data = {
            "username": "testuser",
            "password": "testpass123",
            "phone": "13800138000",
            "gender": "MALE"
        }
        response = requests.post("http://localhost:8080/api/auth/register", 
                               json=data, 
                               headers={"Content-Type": "application/json"},
                               timeout=10)
        log(f"注册响应: {response.status_code}")
        log(f"注册内容: {response.text}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("success"):
                log("✅ 用户注册成功")
                return result.get("data", {}).get("token")
            else:
                log(f"❌ 用户注册失败: {result.get('message')}")
                return None
        else:
            log(f"❌ 用户注册请求失败: {response.status_code}")
            return None
    except Exception as e:
        log(f"❌ 用户注册异常: {e}")
        return None

def test_user_login():
    """测试用户登录"""
    log("=== 测试用户登录 ===")
    try:
        data = {
            "username": "testuser",
            "password": "testpass123"
        }
        response = requests.post("http://localhost:8080/api/auth/login", 
                               json=data, 
                               headers={"Content-Type": "application/json"},
                               timeout=10)
        log(f"登录响应: {response.status_code}")
        log(f"登录内容: {response.text}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("success") and "token" in result.get("data", {}):
                token = result["data"]["token"]
                log("✅ 用户登录成功")
                return token
            else:
                log(f"❌ 用户登录失败: {result.get('message')}")
                return None
        else:
            log(f"❌ 用户登录请求失败: {response.status_code}")
            return None
    except Exception as e:
        log(f"❌ 用户登录异常: {e}")
        return None

def test_jwt_validation(token):
    """测试JWT验证"""
    log("=== 测试JWT验证 ===")
    if not token:
        log("❌ 没有JWT Token，跳过验证测试")
        return False
    
    try:
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        response = requests.get("http://localhost:8080/api/users/profile/test-token", 
                              headers=headers, 
                              timeout=10)
        log(f"JWT验证响应: {response.status_code}")
        log(f"JWT验证内容: {response.text}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("isValid"):
                log("✅ JWT验证成功")
                return True
            else:
                log("❌ JWT验证失败")
                return False
        else:
            log(f"❌ JWT验证请求失败: {response.status_code}")
            return False
    except Exception as e:
        log(f"❌ JWT验证异常: {e}")
        return False

def test_protected_endpoint(token):
    """测试受保护端点"""
    log("=== 测试受保护端点 ===")
    if not token:
        log("❌ 没有JWT Token，跳过保护端点测试")
        return False
    
    try:
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        response = requests.get("http://localhost:8080/api/users/profile", 
                              headers=headers, 
                              timeout=10)
        log(f"保护端点响应: {response.status_code}")
        log(f"保护端点内容: {response.text}")
        
        if response.status_code == 200:
            log("✅ 保护端点访问成功")
            return True
        else:
            log(f"❌ 保护端点访问失败: {response.status_code}")
            return False
    except Exception as e:
        log(f"❌ 保护端点异常: {e}")
        return False

def test_no_token_access():
    """测试无Token访问"""
    log("=== 测试无Token访问 ===")
    try:
        response = requests.get("http://localhost:8080/api/users/profile", timeout=10)
        log(f"无Token访问响应: {response.status_code}")
        if response.status_code == 401:
            log("✅ 无Token访问正确被拒绝")
            return True
        else:
            log(f"❌ 无Token访问未被正确拒绝: {response.status_code}")
            return False
    except Exception as e:
        log(f"❌ 无Token访问异常: {e}")
        return False

def main():
    """主测试函数"""
    log("🚀 开始最终API测试")
    log("=" * 60)
    
    # 等待后端启动
    log("⏳ 等待后端服务启动...")
    time.sleep(3)
    
    # 测试结果统计
    results = {}
    
    # 基础接口测试
    results['swagger'] = test_swagger_access()
    results['api_docs'] = test_api_docs_access()
    results['health'] = test_health_check()
    results['database'] = test_database_connection()
    
    # 认证相关测试
    results['register'] = test_user_registration()
    token = test_user_login()
    results['login'] = token is not None
    
    # JWT验证测试
    results['jwt_validation'] = test_jwt_validation(token)
    results['protected_endpoint'] = test_protected_endpoint(token)
    results['no_token_access'] = test_no_token_access()
    
    # 输出测试结果
    log("=" * 60)
    log("📊 测试结果汇总:")
    log(f"Swagger访问: {'✅' if results['swagger'] else '❌'}")
    log(f"API文档访问: {'✅' if results['api_docs'] else '❌'}")
    log(f"健康检查: {'✅' if results['health'] else '❌'}")
    log(f"数据库连接: {'✅' if results['database'] else '❌'}")
    log(f"用户注册: {'✅' if results['register'] else '❌'}")
    log(f"用户登录: {'✅' if results['login'] else '❌'}")
    log(f"JWT验证: {'✅' if results['jwt_validation'] else '❌'}")
    log(f"保护端点访问: {'✅' if results['protected_endpoint'] else '❌'}")
    log(f"无Token访问拒绝: {'✅' if results['no_token_access'] else '❌'}")
    
    # 计算成功率
    success_count = sum(1 for result in results.values() if result)
    total_count = len(results)
    success_rate = (success_count / total_count) * 100
    
    log("=" * 60)
    log(f"🎯 总体成功率: {success_count}/{total_count} ({success_rate:.1f}%)")
    
    if success_rate >= 80:
        log("🎉 测试通过！大部分功能正常")
    elif success_rate >= 60:
        log("⚠️ 测试部分通过，需要进一步修复")
    else:
        log("❌ 测试失败，需要大量修复")
    
    log("=" * 60)
    log("🏁 最终API测试完成")

if __name__ == "__main__":
    main()
