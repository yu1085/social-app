#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
JWT完整测试脚本
测试JWT生成、验证和认证流程
"""

import requests
import json
import time
import sys
from datetime import datetime

# 配置
BASE_URL = "http://localhost:8080"
TEST_USERNAME = "testuser"
TEST_PASSWORD = "testpass123"
TEST_PHONE = "13800138000"

def log(message):
    """打印带时间戳的日志"""
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    print(f"[{timestamp}] {message}")

def test_health():
    """测试后端健康状态"""
    log("=== 测试后端健康状态 ===")
    try:
        response = requests.get(f"{BASE_URL}/api/health", timeout=10)
        log(f"健康检查响应: {response.status_code}")
        if response.status_code == 200:
            log("后端服务正常运行")
            return True
        else:
            log(f"后端服务异常: {response.text}")
            return False
    except Exception as e:
        log(f"无法连接到后端服务: {e}")
        return False

def test_user_registration():
    """测试用户注册"""
    log("=== 测试用户注册 ===")
    try:
        data = {
            "username": TEST_USERNAME,
            "password": TEST_PASSWORD,
            "phone": TEST_PHONE,
            "gender": "MALE"
        }
        response = requests.post(f"{BASE_URL}/api/auth/register", 
                               json=data, 
                               headers={"Content-Type": "application/json"},
                               timeout=10)
        log(f"注册响应: {response.status_code}")
        log(f"注册响应内容: {response.text}")
        
        if response.status_code == 200:
            log("用户注册成功")
            return True
        else:
            log("用户注册失败")
            return False
    except Exception as e:
        log(f"注册请求失败: {e}")
        return False

def test_user_login():
    """测试用户登录并获取JWT"""
    log("=== 测试用户登录 ===")
    try:
        data = {
            "username": TEST_USERNAME,
            "password": TEST_PASSWORD
        }
        response = requests.post(f"{BASE_URL}/api/auth/login", 
                               json=data, 
                               headers={"Content-Type": "application/json"},
                               timeout=10)
        log(f"登录响应: {response.status_code}")
        log(f"登录响应内容: {response.text}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("success") and "token" in result:
                token = result["token"]
                log(f"登录成功，获取到JWT Token: {token[:50]}...")
                return token
            else:
                log("登录响应格式错误")
                return None
        else:
            log("登录失败")
            return None
    except Exception as e:
        log(f"登录请求失败: {e}")
        return None

def test_jwt_validation(token):
    """测试JWT验证"""
    log("=== 测试JWT验证 ===")
    if not token:
        log("没有JWT Token，跳过验证测试")
        return False
    
    try:
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        response = requests.get(f"{BASE_URL}/api/users/profile/test-token", 
                              headers=headers, 
                              timeout=10)
        log(f"JWT验证响应: {response.status_code}")
        log(f"JWT验证响应内容: {response.text}")
        
        if response.status_code == 200:
            result = response.json()
            if result.get("isValid"):
                log("JWT验证成功")
                return True
            else:
                log("JWT验证失败")
                return False
        else:
            log(f"JWT验证请求失败: {response.status_code}")
            return False
    except Exception as e:
        log(f"JWT验证请求异常: {e}")
        return False

def test_protected_endpoint(token):
    """测试受保护的端点"""
    log("=== 测试受保护的端点 ===")
    if not token:
        log("没有JWT Token，跳过保护端点测试")
        return False
    
    try:
        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }
        response = requests.get(f"{BASE_URL}/api/users/profile", 
                              headers=headers, 
                              timeout=10)
        log(f"保护端点响应: {response.status_code}")
        log(f"保护端点响应内容: {response.text}")
        
        if response.status_code == 200:
            log("保护端点访问成功")
            return True
        else:
            log(f"保护端点访问失败: {response.status_code}")
            return False
    except Exception as e:
        log(f"保护端点请求异常: {e}")
        return False

def test_invalid_token():
    """测试无效Token"""
    log("=== 测试无效Token ===")
    try:
        headers = {
            "Authorization": "Bearer invalid_token_12345",
            "Content-Type": "application/json"
        }
        response = requests.get(f"{BASE_URL}/api/users/profile", 
                              headers=headers, 
                              timeout=10)
        log(f"无效Token响应: {response.status_code}")
        log(f"无效Token响应内容: {response.text}")
        
        if response.status_code == 401:
            log("无效Token正确被拒绝")
            return True
        else:
            log("无效Token未被正确拒绝")
            return False
    except Exception as e:
        log(f"无效Token测试异常: {e}")
        return False

def test_no_token():
    """测试无Token访问"""
    log("=== 测试无Token访问 ===")
    try:
        response = requests.get(f"{BASE_URL}/api/users/profile", 
                              timeout=10)
        log(f"无Token响应: {response.status_code}")
        log(f"无Token响应内容: {response.text}")
        
        if response.status_code == 401:
            log("无Token正确被拒绝")
            return True
        else:
            log("无Token未被正确拒绝")
            return False
    except Exception as e:
        log(f"无Token测试异常: {e}")
        return False

def main():
    """主测试函数"""
    log("开始JWT完整测试流程")
    log("=" * 50)
    
    # 等待后端启动
    log("等待后端服务启动...")
    time.sleep(5)
    
    # 测试后端健康状态
    if not test_health():
        log("后端服务未启动，退出测试")
        return
    
    # 测试用户注册
    test_user_registration()
    
    # 测试用户登录
    token = test_user_login()
    
    # 测试JWT验证
    test_jwt_validation(token)
    
    # 测试受保护的端点
    test_protected_endpoint(token)
    
    # 测试无效Token
    test_invalid_token()
    
    # 测试无Token访问
    test_no_token()
    
    log("=" * 50)
    log("JWT测试流程完成")

if __name__ == "__main__":
    main()
