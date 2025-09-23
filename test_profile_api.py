#!/usr/bin/env python3
"""
测试用户资料API接口
用于验证后端接口是否正常工作
"""

import requests
import json
import time

# 配置
BASE_URL = "http://10.0.2.2:8080/api/users/profile"
TEST_TOKEN = "test_token_12345"
TEST_USER_ID = 1

def test_get_profile():
    """测试获取用户资料接口"""
    print("=== 测试获取用户资料接口 ===")
    
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {TEST_TOKEN}"
    }
    
    try:
        response = requests.get(BASE_URL, headers=headers, timeout=10)
        print(f"状态码: {response.status_code}")
        print(f"响应头: {dict(response.headers)}")
        print(f"响应体: {response.text}")
        
        if response.status_code == 200:
            print("✅ 获取用户资料成功")
            return response.json()
        else:
            print("❌ 获取用户资料失败")
            return None
            
    except requests.exceptions.RequestException as e:
        print(f"❌ 请求异常: {e}")
        return None

def test_update_profile():
    """测试更新用户资料接口"""
    print("\n=== 测试更新用户资料接口 ===")
    
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {TEST_TOKEN}"
    }
    
    # 测试数据
    test_data = {
        "nickname": "测试用户",
        "gender": "男",
        "bio": "这是一个测试用户",
        "birthday": "1990-01-01",
        "location": "北京",
        "height": 175,
        "weight": 70,
        "income": "10-20万",
        "education": "本科",
        "city": "北京",
        "hometown": "上海",
        "hobbies": "编程,旅游",
        "bloodType": "A型",
        "relationshipStatus": "单身",
        "occupation": "程序员",
        "residenceStatus": "自有住房",
        "houseOwnership": True,
        "carOwnership": False,
        "smoking": False,
        "drinking": False
    }
    
    try:
        response = requests.put(
            f"{BASE_URL}/{TEST_USER_ID}", 
            headers=headers, 
            data=json.dumps(test_data),
            timeout=10
        )
        print(f"状态码: {response.status_code}")
        print(f"响应头: {dict(response.headers)}")
        print(f"响应体: {response.text}")
        
        if response.status_code == 200:
            print("✅ 更新用户资料成功")
            return response.json()
        else:
            print("❌ 更新用户资料失败")
            return None
            
    except requests.exceptions.RequestException as e:
        print(f"❌ 请求异常: {e}")
        return None

def test_without_token():
    """测试无token的情况"""
    print("\n=== 测试无token的情况 ===")
    
    headers = {
        "Content-Type": "application/json"
    }
    
    try:
        response = requests.get(BASE_URL, headers=headers, timeout=10)
        print(f"状态码: {response.status_code}")
        print(f"响应体: {response.text}")
        
        if response.status_code == 401:
            print("✅ 正确返回401未授权")
        else:
            print("❌ 应该返回401未授权")
            
    except requests.exceptions.RequestException as e:
        print(f"❌ 请求异常: {e}")

def main():
    """主函数"""
    print("开始测试用户资料API接口...")
    print(f"测试URL: {BASE_URL}")
    print(f"测试Token: {TEST_TOKEN}")
    print(f"测试用户ID: {TEST_USER_ID}")
    print("-" * 50)
    
    # 测试获取用户资料
    profile_data = test_get_profile()
    
    # 测试更新用户资料
    update_result = test_update_profile()
    
    # 测试无token情况
    test_without_token()
    
    print("\n" + "=" * 50)
    print("测试完成！")
    
    if profile_data:
        print("✅ 获取用户资料接口正常")
    else:
        print("❌ 获取用户资料接口异常")
        
    if update_result:
        print("✅ 更新用户资料接口正常")
    else:
        print("❌ 更新用户资料接口异常")

if __name__ == "__main__":
    main()
