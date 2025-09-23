#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SocialMeet 统一测试套件
整合所有测试功能，减少代码冗余
"""

import requests
import json
import time
import jwt
import base64
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Tuple
import argparse
import sys

class SocialMeetTestSuite:
    """SocialMeet 统一测试套件"""
    
    def __init__(self, base_url="http://localhost:8080", verbose=False):
        # 支持多种服务器配置
        self.server_configs = {
            "local": "http://localhost:8080",
            "overseas": "https://your-overseas-server.com:8080",  # 境外服务器
            "tencent": "https://socialchatai.cloud:8080",  # 腾讯云（需要备案）
            "aliyun": "http://your-aliyun-ecs-ip:8080"  # 阿里云ECS（推荐）
        }
        self.base_url = base_url
        self.session = requests.Session()
        self.token = None
        self.user_id = None
        self.verbose = verbose
        self.test_results = []
        self.error_analysis = {
            "401_errors": [],
            "500_errors": [],
            "other_errors": []
        }
        
    def log(self, message: str, level: str = "INFO"):
        """统一日志输出"""
        if self.verbose or level in ["ERROR", "WARNING"]:
            timestamp = datetime.now().strftime("%H:%M:%S")
            print(f"[{timestamp}] [{level}] {message}")
    
    def log_result(self, endpoint: str, method: str, status_code: int, 
                   response_text: str, headers: Dict = None, error: str = None):
        """记录测试结果"""
        result = {
            "timestamp": datetime.now().isoformat(),
            "endpoint": endpoint,
            "method": method,
            "status_code": status_code,
            "success": 200 <= status_code < 300,
            "response": response_text[:500] if response_text else "",
            "headers": headers,
            "error": error
        }
        self.test_results.append(result)
        
        # 分类错误
        if status_code == 401:
            self.error_analysis["401_errors"].append(result)
        elif status_code == 500:
            self.error_analysis["500_errors"].append(result)
        elif status_code >= 400:
            self.error_analysis["other_errors"].append(result)
        
        # 打印结果
        status = "✅" if result["success"] else "❌"
        self.log(f"{status} {method:6} {endpoint:50} - {status_code}")
        
        if error:
            self.log(f"    Error: {error}", "ERROR")
        if not result["success"] and response_text and self.verbose:
            try:
                error_json = json.loads(response_text)
                if 'message' in error_json:
                    self.log(f"    Message: {error_json['message']}", "ERROR")
            except:
                self.log(f"    Response: {response_text[:200]}...", "ERROR")
    
    def test_health_check(self) -> bool:
        """测试健康检查"""
        self.log("=== 测试健康检查 ===")
        try:
            response = self.session.get(f"{self.base_url}/api/health", timeout=5)
            self.log_result("/api/health", "GET", response.status_code, response.text)
            return response.status_code == 200
        except Exception as e:
            self.log_result("/api/health", "GET", 0, "", error=str(e))
            return False
    
    def test_swagger_access(self) -> bool:
        """测试Swagger访问"""
        self.log("=== 测试Swagger访问 ===")
        try:
            response = self.session.get(f"{self.base_url}/swagger-ui.html", timeout=5)
            self.log_result("/swagger-ui.html", "GET", response.status_code, response.text)
            return response.status_code == 200
        except Exception as e:
            self.log_result("/swagger-ui.html", "GET", 0, "", error=str(e))
            return False
    
    def test_auth_flow(self) -> bool:
        """测试认证流程"""
        self.log("=== 测试认证流程 ===")
        
        test_phone = "13800138001"
        
        # 1. 发送验证码
        try:
            response = self.session.post(
                f"{self.base_url}/api/auth/send-code",
                params={"phone": test_phone}
            )
            self.log_result("/api/auth/send-code", "POST", response.status_code, response.text)
            
            if response.status_code != 200:
                return False
            
            data = response.json()
            if data.get("success") and "data" in data:
                code = data["data"]
                self.log(f"获取到验证码: {code}")
            else:
                self.log("无法获取验证码", "ERROR")
                return False
                
        except Exception as e:
            self.log_result("/api/auth/send-code", "POST", 0, "", error=str(e))
            return False
        
        # 2. 使用验证码登录
        try:
            response = self.session.post(
                f"{self.base_url}/api/auth/login-with-code",
                params={
                    "phone": test_phone,
                    "code": code,
                    "gender": "FEMALE"
                }
            )
            self.log_result("/api/auth/login-with-code", "POST", response.status_code, response.text)
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success") and "data" in data:
                    self.token = data["data"].get("token")
                    user_info = data["data"].get("user", {})
                    self.user_id = user_info.get("id")
                    
                    self.log(f"✅ 登录成功! 用户ID: {self.user_id}")
                    return True
            return False
            
        except Exception as e:
            self.log_result("/api/auth/login-with-code", "POST", 0, "", error=str(e))
            return False
    
    def test_user_profile_operations(self):
        """测试用户资料操作"""
        if not self.token:
            self.log("❌ 没有有效的token，跳过用户资料测试", "WARNING")
            return
        
        self.log("=== 测试用户资料操作 ===")
        
        headers = {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }
        
        # 获取用户资料
        try:
            response = self.session.get(f"{self.base_url}/api/users/profile", headers=headers)
            self.log_result("/api/users/profile", "GET", response.status_code, response.text, headers)
        except Exception as e:
            self.log_result("/api/users/profile", "GET", 0, "", headers, str(e))
        
        # 更新用户资料
        if self.user_id:
            update_data = {
                "nickname": f"测试用户_{int(time.time())}",
                "bio": "这是更新后的个人简介",
                "location": "上海市"
            }
            
            try:
                response = self.session.put(
                    f"{self.base_url}/api/users/profile/{self.user_id}",
                    headers=headers,
                    json=update_data
                )
                self.log_result(f"/api/users/profile/{self.user_id}", "PUT", response.status_code, response.text, headers)
            except Exception as e:
                self.log_result(f"/api/users/profile/{self.user_id}", "PUT", 0, "", headers, str(e))
    
    def test_database_connection(self) -> bool:
        """测试数据库连接"""
        self.log("=== 测试数据库连接 ===")
        try:
            response = self.session.post(f"{self.base_url}/api/admin/database/fix/test", timeout=10)
            self.log_result("/api/admin/database/fix/test", "POST", response.status_code, response.text)
            return response.status_code == 200
        except Exception as e:
            self.log_result("/api/admin/database/fix/test", "POST", 0, "", error=str(e))
            return False
    
    def test_payment_apis(self):
        """测试支付相关API"""
        self.log("=== 测试支付API ===")
        
        # 测试支付宝连接
        try:
            response = self.session.get(f"{self.base_url}/api/test/alipay/connection", timeout=10)
            self.log_result("/api/test/alipay/connection", "GET", response.status_code, response.text)
        except Exception as e:
            self.log_result("/api/test/alipay/connection", "GET", 0, "", error=str(e))
    
    def run_basic_tests(self):
        """运行基础测试"""
        self.log("🚀 开始基础测试")
        
        # 健康检查
        if not self.test_health_check():
            self.log("❌ 后端服务不可用", "ERROR")
            return False
        
        # Swagger访问
        self.test_swagger_access()
        
        # 数据库连接
        self.test_database_connection()
        
        return True
    
    def run_auth_tests(self):
        """运行认证测试"""
        self.log("🔐 开始认证测试")
        
        if self.test_auth_flow():
            self.test_user_profile_operations()
            return True
        else:
            self.log("❌ 认证流程失败", "ERROR")
            return False
    
    def run_payment_tests(self):
        """运行支付测试"""
        self.log("💳 开始支付测试")
        self.test_payment_apis()
    
    def generate_report(self):
        """生成测试报告"""
        self.log("📊 生成测试报告")
        
        total_tests = len(self.test_results)
        passed_tests = sum(1 for r in self.test_results if r["success"])
        failed_tests = total_tests - passed_tests
        
        print("\n" + "="*60)
        print("测试报告总结")
        print("="*60)
        print(f"总测试数: {total_tests}")
        print(f"✅ 通过: {passed_tests}")
        print(f"❌ 失败: {failed_tests}")
        print(f"成功率: {passed_tests/total_tests*100:.1f}%" if total_tests > 0 else "N/A")
        
        print(f"\n错误分布:")
        print(f"401错误: {len(self.error_analysis['401_errors'])}")
        print(f"500错误: {len(self.error_analysis['500_errors'])}")
        print(f"其他错误: {len(self.error_analysis['other_errors'])}")
        
        # 保存详细报告
        report = {
            "test_time": datetime.now().isoformat(),
            "summary": {
                "total_tests": total_tests,
                "passed": passed_tests,
                "failed": failed_tests,
                "success_rate": f"{passed_tests/total_tests*100:.1f}%" if total_tests > 0 else "N/A"
            },
            "error_analysis": self.error_analysis,
            "test_results": self.test_results
        }
        
        with open("unified_test_report.json", "w", encoding="utf-8") as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
        
        self.log(f"📄 详细报告已保存到: unified_test_report.json")
    
    def run_all_tests(self):
        """运行所有测试"""
        print("="*60)
        print("SocialMeet 统一测试套件")
        print("="*60)
        print(f"目标服务器: {self.base_url}")
        print(f"测试时间: {datetime.now().isoformat()}")
        
        # 基础测试
        if not self.run_basic_tests():
            return
        
        # 认证测试
        self.run_auth_tests()
        
        # 支付测试
        self.run_payment_tests()
        
        # 生成报告
        self.generate_report()

def main():
    """主函数"""
    parser = argparse.ArgumentParser(description="SocialMeet 统一测试套件")
    parser.add_argument("--url", default="http://localhost:8080", help="API服务器地址")
    parser.add_argument("--server", choices=["local", "overseas", "tencent", "aliyun"], 
                       help="预设服务器配置 (local/overseas/tencent/aliyun)")
    parser.add_argument("--verbose", "-v", action="store_true", help="详细输出")
    parser.add_argument("--test", choices=["basic", "auth", "payment", "all"], 
                       default="all", help="测试类型")
    
    args = parser.parse_args()
    
    # 如果指定了预设服务器配置，使用对应的URL
    if args.server:
        tester = SocialMeetTestSuite(verbose=args.verbose)
        tester.base_url = tester.server_configs[args.server]
        print(f"使用预设服务器配置: {args.server} -> {tester.base_url}")
    else:
        tester = SocialMeetTestSuite(base_url=args.url, verbose=args.verbose)
    
    if args.test == "basic":
        tester.run_basic_tests()
    elif args.test == "auth":
        tester.run_auth_tests()
    elif args.test == "payment":
        tester.run_payment_tests()
    else:
        tester.run_all_tests()

if __name__ == "__main__":
    main()
