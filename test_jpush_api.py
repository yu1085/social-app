#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
JPush API 测试脚本
直接通过极光API发送推送消息
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

# 推送数据
push_data = {
    "platform": "android",
    "audience": {
        "registration_id": [REGISTRATION_ID]
    },
    "notification": {
        "android": {
            "alert": "这是一条API测试推送",
            "title": "API推送测试",
            "builder_id": 1
        }
    },
    "options": {
        "apns_production": False,
        "time_to_live": 86400
    }
}

# 发送推送
url = "https://api.jpush.cn/v3/push"
response = requests.post(url, headers=headers, data=json.dumps(push_data))

print(f"状态码: {response.status_code}")
print(f"响应内容: {response.text}")

if response.status_code == 200:
    print("✅ 推送发送成功！")
else:
    print("❌ 推送发送失败！")
