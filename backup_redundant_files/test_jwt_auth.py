#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
JWT认证调试脚本
直接测试JWT token的生成和验证
"""

import requests
import json
import base64

def decode_jwt_payload(token):
    """解码JWT payload（不验证签名）"""
    try:
        # JWT格式: header.payload.signature
        parts = token.split('.')
        if len(parts) != 3:
            return None
        
        # 解码payload部分
        payload = parts[1]
        # 添加padding
        padding = 4 - len(payload) % 4
        if padding != 4:
            payload += '=' * padding
        
        decoded = base64.urlsafe_b64decode(payload)
        return json.loads(decoded)
    except Exception as e:
        print(f"解码JWT失败: {e}")
        return None

def test_jwt_authentication():
    """测试JWT认证流程"""
    base_url = "http://localhost:8080"
    
    print("="*60)
    print("JWT认证测试")
    print("="*60)
    
    # Step 1: 获取验证码并登录
    print("\n1. 登录获取JWT Token...")
    
    # 发送验证码
    phone = "13800138003"
    response = requests.post(f"{base_url}/api/auth/send-code", params={"phone": phone})
    if response.status_code != 200:
        print(f"发送验证码失败: {response.text}")
        return
    
    code = response.json()["data"]
    print(f"   验证码: {code}")
    
    # 登录
    response = requests.post(
        f"{base_url}/api/auth/login-with-code",
        params={"phone": phone, "code": code, "gender": "FEMALE"}
    )
    
    if response.status_code != 200:
        print(f"登录失败: {response.text}")
        return
    
    login_data = response.json()["data"]
    token = login_data["token"]
    user_id = login_data["user"]["id"]
    
    print(f"   Token获取成功!")
    print(f"   用户ID: {user_id}")
    print(f"   Token长度: {len(token)}")
    print(f"   Token前50字符: {token[:50]}...")
    
    # 解码JWT payload
    payload = decode_jwt_payload(token)
    if payload:
        print(f"\n   JWT Payload内容:")
        for key, value in payload.items():
            print(f"     {key}: {value}")
    
    # Step 2: 使用不同的Authorization格式测试
    print("\n2. 测试不同的Authorization格式...")
    
    test_endpoints = [
        "/api/users/profile/test-token",
        "/api/users/profile",
        "/api/health"
    ]
    
    auth_formats = [
        ("Bearer " + token, "标准格式: Bearer <token>"),
        (token, "只有token，没有Bearer前缀"),
        ("bearer " + token, "小写bearer"),
        ("BEARER " + token, "大写BEARER"),
    ]
    
    for endpoint in test_endpoints:
        print(f"\n   测试端点: {endpoint}")
        
        for auth_value, description in auth_formats:
            headers = {"Authorization": auth_value}
            response = requests.get(f"{base_url}{endpoint}", headers=headers)
            
            status = "✅" if response.status_code == 200 else "❌"
            print(f"     {status} {description}: {response.status_code}")
            
            if response.status_code == 200:
                break  # 找到有效格式就继续下一个端点
    
    # Step 3: 测试具体的用户资料接口
    print("\n3. 测试用户资料接口...")
    
    headers = {"Authorization": f"Bearer {token}"}
    
    # 测试获取用户资料
    print("\n   GET /api/users/profile")
    response = requests.get(f"{base_url}/api/users/profile", headers=headers)
    print(f"     状态码: {response.status_code}")
    if response.status_code == 200:
        user_data = response.json()["data"]
        print(f"     用户昵称: {user_data.get('nickname')}")
        print(f"     用户ID: {user_data.get('id')}")
    else:
        print(f"     错误: {response.text[:200]}")
    
    # 测试更新用户资料
    print(f"\n   PUT /api/users/profile/{user_id}")
    update_data = {
        "nickname": "测试用户_JWT",
        "bio": "通过JWT认证测试更新",
        "location": "深圳市"
    }
    
    headers["Content-Type"] = "application/json"
    response = requests.put(
        f"{base_url}/api/users/profile/{user_id}",
        headers=headers,
        json=update_data
    )
    print(f"     状态码: {response.status_code}")
    if response.status_code == 200:
        print(f"     ✅ 更新成功!")
    else:
        print(f"     错误: {response.text[:200]}")
    
    # Step 4: 测试其他需要认证的接口
    print("\n4. 批量测试需要认证的接口...")
    
    auth_endpoints = [
        "/api/dynamics",
        "/api/messages",
        "/api/wallet",
        "/api/vip",
        "/api/calls"
    ]
    
    headers = {"Authorization": f"Bearer {token}"}
    
    for endpoint in auth_endpoints:
        response = requests.get(f"{base_url}{endpoint}", headers=headers)
        status = "✅" if response.status_code == 200 else "❌"
        print(f"   {status} GET {endpoint}: {response.status_code}")
    
    # Step 5: 测试无效token
    print("\n5. 测试无效Token...")
    
    invalid_tokens = [
        ("invalid.token.here", "完全无效的token"),
        (token[:-10] + "corrupted", "损坏的token"),
        ("", "空token"),
    ]
    
    for invalid_token, description in invalid_tokens:
        headers = {"Authorization": f"Bearer {invalid_token}"}
        response = requests.get(f"{base_url}/api/users/profile", headers=headers)
        print(f"   {description}: {response.status_code} - 预期401")
    
    print("\n" + "="*60)
    print("测试完成!")
    print("="*60)

if __name__ == "__main__":
    test_jwt_authentication()
