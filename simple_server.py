#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import http.server
import socketserver
import urllib.parse
import json
import random
import time

# 存储验证码
verification_codes = {}

class VerificationHandler(http.server.BaseHTTPRequestHandler):
    def do_OPTIONS(self):
        # 处理CORS预检请求
        self.send_response(200)
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()
    
    def do_POST(self):
        # 解析URL
        parsed_url = urllib.parse.urlparse(self.path)
        path = parsed_url.path
        query_params = urllib.parse.parse_qs(parsed_url.query)
        
        print(f"\n=== 收到请求 ===")
        print(f"路径: {path}")
        print(f"参数: {query_params}")
        
        # 设置CORS头
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        self.end_headers()
        
        if path == '/api/auth/send-code':
            self.handle_send_code(query_params)
        elif path == '/api/auth/login-with-code':
            self.handle_login(query_params)
        else:
            self.send_error_response("Not Found")
    
    def handle_send_code(self, params):
        phone = params.get('phone', [''])[0]
        print(f"手机号: {phone}")
        
        # 生成6位验证码
        code = str(random.randint(100000, 999999))
        verification_codes[phone] = code
        
        print(f"验证码: {code}")
        print("==================\n")
        
        response = {
            "success": True,
            "message": "验证码已发送",
            "data": "验证码已发送"
        }
        self.wfile.write(json.dumps(response, ensure_ascii=False).encode('utf-8'))
    
    def handle_login(self, params):
        phone = params.get('phone', [''])[0]
        code = params.get('code', [''])[0]
        
        print(f"手机号: {phone}")
        print(f"验证码: {code}")
        
        stored_code = verification_codes.get(phone)
        if stored_code and stored_code == code:
            print("登录成功！")
            del verification_codes[phone]  # 删除已使用的验证码
            
            response = {
                "success": True,
                "message": "登录成功",
                "data": {
                    "token": f"test_token_{int(time.time())}",
                    "user": {
                        "id": 1,
                        "username": phone,
                        "nickname": f"用户{phone[-4:]}",
                        "phone": phone,
                        "gender": "MALE"
                    }
                }
            }
        else:
            print("验证码错误或已过期")
            response = {
                "success": False,
                "message": "验证码错误或已过期"
            }
        
        print("==================\n")
        self.wfile.write(json.dumps(response, ensure_ascii=False).encode('utf-8'))
    
    def send_error_response(self, message):
        response = {
            "success": False,
            "message": message
        }
        self.wfile.write(json.dumps(response, ensure_ascii=False).encode('utf-8'))

def run_server():
    PORT = 8080
    with socketserver.TCPServer(("", PORT), VerificationHandler) as httpd:
        print("=== 验证码服务器已启动 ===")
        print(f"服务地址: http://localhost:{PORT}")
        print("=========================")
        httpd.serve_forever()

if __name__ == "__main__":
    run_server()
