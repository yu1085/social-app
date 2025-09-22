import requests
import json

# 测试完整的登录流程
base_url = "http://localhost:8080/api/auth"

# 1. 发送验证码
print("=== 发送验证码 ===")
send_code_url = f"{base_url}/send-code"
send_code_data = {"phone": "13800138000"}

try:
    response = requests.post(send_code_url, data=send_code_data)
    print(f"发送验证码状态码: {response.status_code}")
    if response.status_code == 200:
        result = response.json()
        print(f"发送验证码成功: {result}")
        verification_code = result.get('data')
    else:
        print(f"发送验证码失败: {response.text}")
        exit(1)
except Exception as e:
    print(f"发送验证码异常: {e}")
    exit(1)

# 2. 使用验证码登录
print("\n=== 使用验证码登录 ===")
login_url = f"{base_url}/login-with-code"
login_data = {
    "phone": "13800138000",
    "code": verification_code,
    "gender": "FEMALE"
}

try:
    response = requests.post(login_url, data=login_data)
    print(f"登录状态码: {response.status_code}")
    print(f"登录响应: {response.text}")
    
    if response.status_code == 200:
        result = response.json()
        print(f"登录成功: {result}")
        if 'data' in result and 'token' in result['data']:
            print(f"JWT Token: {result['data']['token'][:50]}...")
    else:
        print(f"登录失败: {response.text}")
        
except Exception as e:
    print(f"登录异常: {e}")
