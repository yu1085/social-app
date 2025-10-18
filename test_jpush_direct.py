#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
直接通过JPush API发送推送测试
绕过极光控制台，直接测试API
"""

import requests
import json
import base64

# 极光推送配置
APP_KEY = "ff90a2867fcf541a3f3e8ed4"
MASTER_SECRET = "112ee5a04324ae703d2d6b3d"
REGISTRATION_ID = "100d8559086375f6db0"

# 构建认证头
auth_string = f"{APP_KEY}:{MASTER_SECRET}"
auth_bytes = auth_string.encode('utf-8')
auth_b64 = base64.b64encode(auth_bytes).decode('utf-8')

# 请求头
headers = {
    'Authorization': f'Basic {auth_b64}',
    'Content-Type': 'application/json'
}

# 测试1: 简单通知推送
print("=== 测试1: 简单通知推送 ===")
push_data_1 = {
    "platform": "android",
    "audience": {
        "registration_id": [REGISTRATION_ID]
    },
    "notification": {
        "android": {
            "alert": "这是一条API测试推送",
            "title": "API推送测试"
        }
    },
    "options": {
        "apns_production": False,
        "time_to_live": 86400
    }
}

url = "https://api.jpush.cn/v3/push"
response1 = requests.post(url, headers=headers, data=json.dumps(push_data_1))
print(f"状态码: {response1.status_code}")
print(f"响应内容: {response1.text}")

print("\n" + "="*50 + "\n")

# 测试2: 带自定义数据的推送
print("=== 测试2: 带自定义数据的推送 ===")
push_data_2 = {
    "platform": "android",
    "audience": {
        "registration_id": [REGISTRATION_ID]
    },
    "notification": {
        "android": {
            "alert": "这是一条带自定义数据的推送",
            "title": "自定义数据推送测试",
            "extras": {
                "type": "MESSAGE",
                "senderId": "12345",
                "senderName": "测试用户",
                "messageType": "TEXT",
                "timestamp": "1697620800000"
            }
        }
    },
    "options": {
        "apns_production": False,
        "time_to_live": 86400
    }
}

response2 = requests.post(url, headers=headers, data=json.dumps(push_data_2))
print(f"状态码: {response2.status_code}")
print(f"响应内容: {response2.text}")

print("\n" + "="*50 + "\n")

# 测试3: 自定义消息推送
print("=== 测试3: 自定义消息推送 ===")
push_data_3 = {
    "platform": "android",
    "audience": {
        "registration_id": [REGISTRATION_ID]
    },
    "message": {
        "msg_content": "这是一条自定义消息",
        "title": "自定义消息测试",
        "extras": {
            "type": "CUSTOM_MESSAGE",
            "data": "test_data"
        }
    },
    "options": {
        "apns_production": False,
        "time_to_live": 86400
    }
}

response3 = requests.post(url, headers=headers, data=json.dumps(push_data_3))
print(f"状态码: {response3.status_code}")
print(f"响应内容: {response3.text}")

print("\n" + "="*50 + "\n")

# 总结
print("=== 测试总结 ===")
if response1.status_code == 200:
    print("✅ 简单通知推送: 成功")
else:
    print("❌ 简单通知推送: 失败")

if response2.status_code == 200:
    print("✅ 自定义数据推送: 成功")
else:
    print("❌ 自定义数据推送: 失败")

if response3.status_code == 200:
    print("✅ 自定义消息推送: 成功")
else:
    print("❌ 自定义消息推送: 失败")
