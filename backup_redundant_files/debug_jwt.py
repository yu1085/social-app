#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
调试JWT token问题
"""

import jwt
import json
import base64
from datetime import datetime

def decode_jwt_token(token):
    """解码JWT token（不验证签名）"""
    try:
        # 分割JWT token
        parts = token.split('.')
        if len(parts) != 3:
            print("❌ JWT token格式错误：应该有3个部分")
            return None
        
        # 解码header
        header = json.loads(base64.urlsafe_b64decode(parts[0] + '=='))
        print(f"Header: {header}")
        
        # 解码payload
        payload = json.loads(base64.urlsafe_b64decode(parts[1] + '=='))
        print(f"Payload: {payload}")
        
        # 检查过期时间
        if 'exp' in payload:
            exp_timestamp = payload['exp']
            exp_date = datetime.fromtimestamp(exp_timestamp)
            current_date = datetime.now()
            print(f"Token过期时间: {exp_date}")
            print(f"当前时间: {current_date}")
            print(f"是否过期: {current_date > exp_date}")
        
        return payload
        
    except Exception as e:
        print(f"❌ 解码JWT token失败: {e}")
        return None

def test_jwt_validation():
    """测试JWT验证"""
    print("=== 调试JWT Token ===\n")
    
    # 从日志中获取的完整token
    token = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjY1ODk5MDMyLCJ1c2VybmFtZSI6IjEzODAwMTM4MDAwIiwidHlwZSI6ImFjY2VzcyIsImlhdCI6MTczNzU0NzU0MCwiZXhwIjoxNzM3NjM0MDQwfQ.test"
    
    print(f"Token: {token}")
    print(f"Token长度: {len(token)}")
    print()
    
    # 解码token
    payload = decode_jwt_token(token)
    
    if payload:
        print(f"\n✅ Token解码成功")
        print(f"用户ID: {payload.get('userId')}")
        print(f"用户名: {payload.get('username')}")
        print(f"Token类型: {payload.get('type')}")
    else:
        print(f"\n❌ Token解码失败")

if __name__ == "__main__":
    test_jwt_validation()
