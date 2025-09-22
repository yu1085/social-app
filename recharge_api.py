#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
使用Spring Boot API进行用户充值
给指定用户ID充值10000
"""

import requests
import json
import sys
from decimal import Decimal

# API配置
API_BASE_URL = "http://localhost:8080/api"
ADMIN_RECHARGE_URL = f"{API_BASE_URL}/wallet/admin/recharge"

# 要充值的用户ID和金额
USER_IDS = [65899032, 44479883]
RECHARGE_AMOUNT = 10000.00

def check_api_health():
    """检查API服务是否可用"""
    try:
        response = requests.get(f"{API_BASE_URL}/health", timeout=5)
        if response.status_code == 200:
            print("✓ API服务运行正常")
            return True
        else:
            print(f"✗ API服务异常，状态码: {response.status_code}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"✗ 无法连接到API服务: {e}")
        return False

def recharge_user_api(user_id, amount):
    """通过API给用户充值"""
    url = f"{ADMIN_RECHARGE_URL}/{user_id}"
    params = {
        'amount': amount,
        'description': '管理员充值10000'
    }
    
    try:
        print(f"正在通过API为用户 {user_id} 充值 {amount}...")
        response = requests.post(url, params=params, timeout=10)
        
        if response.status_code == 200:
            result = response.json()
            if result.get('success'):
                print(f"✓ 用户 {user_id} 充值成功！")
                return True
            else:
                print(f"✗ 用户 {user_id} 充值失败: {result.get('message', '未知错误')}")
                return False
        else:
            print(f"✗ 用户 {user_id} 充值失败，HTTP状态码: {response.status_code}")
            print(f"  响应内容: {response.text}")
            return False
            
    except requests.exceptions.RequestException as e:
        print(f"✗ 用户 {user_id} 充值请求失败: {e}")
        return False

def get_user_balance_api(user_id):
    """通过API获取用户余额"""
    url = f"{API_BASE_URL}/wallet/admin/balance/{user_id}"
    
    try:
        response = requests.get(url, timeout=5)
        if response.status_code == 200:
            result = response.json()
            if result.get('success'):
                wallet_data = result.get('data', {})
                return {
                    'user_id': wallet_data.get('userId'),
                    'balance': wallet_data.get('balance'),
                    'currency': wallet_data.get('currency'),
                    'updated_at': wallet_data.get('updatedAt')
                }
            else:
                print(f"✗ 获取用户 {user_id} 余额失败: {result.get('message', '未知错误')}")
                return None
        else:
            print(f"✗ 获取用户 {user_id} 余额失败，HTTP状态码: {response.status_code}")
            return None
            
    except requests.exceptions.RequestException as e:
        print(f"✗ 获取用户 {user_id} 余额请求失败: {e}")
        return None

def main():
    """主函数"""
    print("="*60)
    print("用户充值脚本 (API版本)")
    print("="*60)
    print(f"充值金额: {RECHARGE_AMOUNT}")
    print(f"目标用户ID: {USER_IDS}")
    print("="*60)
    
    # 检查API服务
    if not check_api_health():
        print("\n请确保Spring Boot服务正在运行 (http://localhost:8080)")
        print("可以运行 start_backend.bat 启动服务")
        sys.exit(1)
    
    print("\n开始充值操作...")
    success_count = 0
    
    # 给每个用户充值
    for user_id in USER_IDS:
        if recharge_user_api(user_id, RECHARGE_AMOUNT):
            success_count += 1
        print()  # 空行分隔
    
    print("="*60)
    print(f"充值操作完成！成功: {success_count}/{len(USER_IDS)}")
    print("="*60)
    
    if success_count > 0:
        print("\n查询充值后的用户余额:")
        print("-" * 60)
        print(f"{'用户ID':<12} {'余额':<12} {'货币':<8} {'更新时间'}")
        print("-" * 60)
        
        for user_id in USER_IDS:
            balance_info = get_user_balance_api(user_id)
            if balance_info:
                print(f"{balance_info['user_id']:<12} {balance_info['balance']:<12} {balance_info['currency']:<8} {balance_info['updated_at']}")
            else:
                print(f"{user_id:<12} {'查询失败':<12} {'-':<8} {'-'}")

if __name__ == "__main__":
    main()
