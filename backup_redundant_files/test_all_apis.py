#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SocialMeet API 全面测试脚本
测试所有接口并修复401和500错误
"""

import requests
import json
import time
from datetime import datetime

class SocialMeetAPITester:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.token = None
        self.user_id = None
        self.test_results = []
        
    def log_result(self, endpoint, method, status_code, response_text, error=None):
        """记录测试结果"""
        result = {
            "timestamp": datetime.now().isoformat(),
            "endpoint": endpoint,
            "method": method,
            "status_code": status_code,
            "success": 200 <= status_code < 300,
            "response": response_text[:500] if response_text else "",
            "error": error
        }
        self.test_results.append(result)
        
        status = "✅ PASS" if result["success"] else "❌ FAIL"
        print(f"{status} {method} {endpoint} - {status_code}")
        if error:
            print(f"    Error: {error}")
        if not result["success"] and response_text:
            print(f"    Response: {response_text[:200]}...")
        print()
    
    def test_health_check(self):
        """测试健康检查接口"""
        try:
            response = self.session.get(f"{self.base_url}/api/health")
            self.log_result("/api/health", "GET", response.status_code, response.text)
            return response.status_code == 200
        except Exception as e:
            self.log_result("/api/health", "GET", 0, "", str(e))
            return False
    
    def test_auth_flow(self):
        """测试认证流程"""
        print("=== 测试认证流程 ===")
        
        # 1. 发送验证码
        try:
            response = self.session.post(f"{self.base_url}/api/auth/send-code", 
                                       params={"phone": "13800138000"})
            self.log_result("/api/auth/send-code", "POST", response.status_code, response.text)
            
            if response.status_code != 200:
                return False
                
            # 提取验证码
            data = response.json()
            if data.get("success") and "data" in data:
                code = data["data"]
                print(f"获取到验证码: {code}")
            else:
                print("无法获取验证码")
                return False
                
        except Exception as e:
            self.log_result("/api/auth/send-code", "POST", 0, "", str(e))
            return False
        
        # 2. 登录获取token
        try:
            response = self.session.post(f"{self.base_url}/api/auth/login-with-code",
                                       params={
                                           "phone": "13800138000",
                                           "code": code,
                                           "gender": "FEMALE"
                                       })
            self.log_result("/api/auth/login-with-code", "POST", response.status_code, response.text)
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success") and "data" in data:
                    self.token = data["data"]["token"]
                    self.user_id = data["data"]["user"]["id"]
                    print(f"登录成功，用户ID: {self.user_id}")
                    print(f"Token: {self.token[:50]}...")
                    return True
            return False
            
        except Exception as e:
            self.log_result("/api/auth/login-with-code", "POST", 0, "", str(e))
            return False
    
    def test_authenticated_endpoints(self):
        """测试需要认证的接口"""
        if not self.token:
            print("❌ 没有有效的token，跳过认证接口测试")
            return
        
        print("=== 测试需要认证的接口 ===")
        
        headers = {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }
        
        # 测试用户资料获取
        try:
            response = self.session.get(f"{self.base_url}/api/users/profile", headers=headers)
            self.log_result("/api/users/profile", "GET", response.status_code, response.text)
        except Exception as e:
            self.log_result("/api/users/profile", "GET", 0, "", str(e))
        
        # 测试用户资料更新
        try:
            profile_data = {
                "nickname": "测试用户_" + str(int(time.time())),
                "bio": "这是一个测试用户",
                "location": "北京市",
                "height": 165,
                "weight": 55,
                "education": "本科",
                "income": "5-8K",
                "hobbies": "音乐,电影,旅行",
                "languages": "中文,英文",
                "bloodType": "A",
                "smoking": False,
                "drinking": False,
                "tags": "测试,社交,交友"
            }
            
            response = self.session.put(f"{self.base_url}/api/users/profile/{self.user_id}", 
                                      headers=headers, 
                                      json=profile_data)
            self.log_result(f"/api/users/profile/{self.user_id}", "PUT", response.status_code, response.text)
        except Exception as e:
            self.log_result(f"/api/users/profile/{self.user_id}", "PUT", 0, "", str(e))
        
        # 测试其他需要认证的接口
        auth_endpoints = [
            ("/api/users/profile/test-token", "GET"),
            ("/api/dynamics", "GET"),
            ("/api/calls", "GET"),
            ("/api/messages", "GET"),
            ("/api/wallet", "GET"),
            ("/api/vip", "GET"),
            ("/api/gifts", "GET"),
        ]
        
        for endpoint, method in auth_endpoints:
            try:
                if method == "GET":
                    response = self.session.get(f"{self.base_url}{endpoint}", headers=headers)
                elif method == "POST":
                    response = self.session.post(f"{self.base_url}{endpoint}", headers=headers)
                elif method == "PUT":
                    response = self.session.put(f"{self.base_url}{endpoint}", headers=headers)
                elif method == "DELETE":
                    response = self.session.delete(f"{self.base_url}{endpoint}", headers=headers)
                
                self.log_result(endpoint, method, response.status_code, response.text)
            except Exception as e:
                self.log_result(endpoint, method, 0, "", str(e))
    
    def test_public_endpoints(self):
        """测试公开接口"""
        print("=== 测试公开接口 ===")
        
        public_endpoints = [
            ("/api/health", "GET"),
            ("/api/auth/send-code", "POST"),
            ("/swagger-ui.html", "GET"),
            ("/api-docs", "GET"),
        ]
        
        for endpoint, method in public_endpoints:
            try:
                if method == "GET":
                    response = self.session.get(f"{self.base_url}{endpoint}")
                elif method == "POST":
                    response = self.session.post(f"{self.base_url}{endpoint}")
                
                self.log_result(endpoint, method, response.status_code, response.text)
            except Exception as e:
                self.log_result(endpoint, method, 0, "", str(e))
    
    def generate_report(self):
        """生成测试报告"""
        print("\n" + "="*60)
        print("测试报告")
        print("="*60)
        
        total_tests = len(self.test_results)
        passed_tests = sum(1 for r in self.test_results if r["success"])
        failed_tests = total_tests - passed_tests
        
        print(f"总测试数: {total_tests}")
        print(f"通过: {passed_tests}")
        print(f"失败: {failed_tests}")
        print(f"成功率: {passed_tests/total_tests*100:.1f}%")
        
        if failed_tests > 0:
            print("\n失败的测试:")
            for result in self.test_results:
                if not result["success"]:
                    print(f"  {result['method']} {result['endpoint']} - {result['status_code']}")
                    if result["error"]:
                        print(f"    错误: {result['error']}")
                    if result["response"]:
                        print(f"    响应: {result['response'][:100]}...")
        
        # 保存详细报告
        with open("api_test_report.json", "w", encoding="utf-8") as f:
            json.dump(self.test_results, f, ensure_ascii=False, indent=2)
        print(f"\n详细报告已保存到: api_test_report.json")
    
    def run_all_tests(self):
        """运行所有测试"""
        print("开始测试 SocialMeet API...")
        print("="*60)
        
        # 1. 健康检查
        if not self.test_health_check():
            print("❌ 后端服务不可用，停止测试")
            return
        
        # 2. 测试公开接口
        self.test_public_endpoints()
        
        # 3. 测试认证流程
        if not self.test_auth_flow():
            print("❌ 认证流程失败，跳过需要认证的接口测试")
        else:
            # 4. 测试需要认证的接口
            self.test_authenticated_endpoints()
        
        # 5. 生成报告
        self.generate_report()

if __name__ == "__main__":
    tester = SocialMeetAPITester()
    tester.run_all_tests()