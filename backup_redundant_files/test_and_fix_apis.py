#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SocialMeet API 完整测试和修复脚本
重点解决401认证错误和500服务器错误
特别关注用户编辑接口
"""

import requests
import json
import time
import jwt
import base64
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Tuple

class APITestAndFix:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.token = None
        self.user_id = None
        self.test_results = []
        self.error_analysis = {
            "401_errors": [],
            "500_errors": [],
            "other_errors": []
        }
        
    def analyze_jwt_token(self, token: str) -> Dict:
        """分析JWT token的结构和有效性"""
        try:
            # 分割token
            parts = token.split('.')
            if len(parts) != 3:
                return {"error": "Invalid token format"}
            
            # 解码header和payload（不验证签名）
            header = json.loads(base64.urlsafe_b64decode(parts[0] + '=='))
            payload = json.loads(base64.urlsafe_b64decode(parts[1] + '=='))
            
            # 检查过期时间
            if 'exp' in payload:
                exp_time = datetime.fromtimestamp(payload['exp'])
                is_expired = exp_time < datetime.now()
            else:
                exp_time = None
                is_expired = None
                
            return {
                "header": header,
                "payload": payload,
                "exp_time": exp_time.isoformat() if exp_time else None,
                "is_expired": is_expired,
                "user_id": payload.get("userId", payload.get("sub")),
                "username": payload.get("username"),
                "authorities": payload.get("authorities", [])
            }
        except Exception as e:
            return {"error": f"Token analysis failed: {str(e)}"}
    
    def log_result(self, endpoint: str, method: str, status_code: int, 
                   response_text: str, headers: Dict = None, error: str = None):
        """记录测试结果并分析错误"""
        result = {
            "timestamp": datetime.now().isoformat(),
            "endpoint": endpoint,
            "method": method,
            "status_code": status_code,
            "success": 200 <= status_code < 300,
            "response": response_text[:1000] if response_text else "",
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
        print(f"{status} {method:6} {endpoint:50} - {status_code}")
        
        if error:
            print(f"    Error: {error}")
        if not result["success"] and response_text:
            try:
                error_json = json.loads(response_text)
                if 'message' in error_json:
                    print(f"    Message: {error_json['message']}")
                if 'error' in error_json:
                    print(f"    Error: {error_json['error']}")
            except:
                print(f"    Response: {response_text[:200]}...")
    
    def test_health_check(self) -> bool:
        """测试健康检查接口"""
        print("\n=== 测试健康检查 ===")
        try:
            response = self.session.get(f"{self.base_url}/api/health")
            self.log_result("/api/health", "GET", response.status_code, response.text)
            return response.status_code == 200
        except Exception as e:
            self.log_result("/api/health", "GET", 0, "", error=str(e))
            return False
    
    def test_auth_flow(self) -> bool:
        """测试完整的认证流程"""
        print("\n=== 测试认证流程 ===")
        
        # 测试手机号
        test_phone = "13800138001"
        
        # 1. 发送验证码
        print(f"发送验证码到: {test_phone}")
        try:
            response = self.session.post(
                f"{self.base_url}/api/auth/send-code",
                params={"phone": test_phone}
            )
            self.log_result("/api/auth/send-code", "POST", response.status_code, response.text)
            
            if response.status_code != 200:
                print("发送验证码失败")
                return False
            
            # 获取验证码
            data = response.json()
            if data.get("success") and "data" in data:
                code = data["data"]
                print(f"获取到验证码: {code}")
            else:
                print("无法获取验证码")
                return False
                
        except Exception as e:
            self.log_result("/api/auth/send-code", "POST", 0, "", error=str(e))
            return False
        
        # 2. 使用验证码登录
        print(f"使用验证码登录...")
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
                    
                    print(f"✅ 登录成功!")
                    print(f"   用户ID: {self.user_id}")
                    print(f"   Token前50字符: {self.token[:50]}...")
                    
                    # 分析token
                    token_info = self.analyze_jwt_token(self.token)
                    print(f"   Token分析:")
                    print(f"     - 用户ID: {token_info.get('user_id')}")
                    print(f"     - 用户名: {token_info.get('username')}")
                    print(f"     - 过期时间: {token_info.get('exp_time')}")
                    print(f"     - 是否过期: {token_info.get('is_expired')}")
                    
                    return True
            return False
            
        except Exception as e:
            self.log_result("/api/auth/login-with-code", "POST", 0, "", error=str(e))
            return False
    
    def test_user_profile_operations(self):
        """重点测试用户资料相关操作"""
        if not self.token:
            print("❌ 没有有效的token，跳过用户资料测试")
            return
        
        print("\n=== 测试用户资料操作 ===")
        
        # 准备请求头
        headers = {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }
        
        # 1. 获取当前用户资料
        print("\n1. 获取当前用户资料...")
        try:
            response = self.session.get(
                f"{self.base_url}/api/users/profile",
                headers=headers
            )
            self.log_result("/api/users/profile", "GET", response.status_code, response.text, headers)
            
            if response.status_code == 200:
                current_profile = response.json().get("data", {})
                print(f"   当前昵称: {current_profile.get('nickname')}")
                print(f"   当前简介: {current_profile.get('bio')}")
        except Exception as e:
            self.log_result("/api/users/profile", "GET", 0, "", headers, str(e))
        
        # 2. 测试token验证接口
        print("\n2. 测试token验证...")
        try:
            response = self.session.get(
                f"{self.base_url}/api/users/profile/test-token",
                headers=headers
            )
            self.log_result("/api/users/profile/test-token", "GET", response.status_code, response.text, headers)
        except Exception as e:
            self.log_result("/api/users/profile/test-token", "GET", 0, "", headers, str(e))
        
        # 3. 更新用户资料（重点测试）
        print(f"\n3. 更新用户资料 (用户ID: {self.user_id})...")
        
        # 准备更新数据
        update_data = {
            "nickname": f"测试用户_{int(time.time())}",
            "bio": "这是更新后的个人简介",
            "location": "上海市",
            "height": 168,
            "weight": 58,
            "education": "硕士",
            "income": "10-15K",
            "hobbies": "阅读,运动,音乐",
            "languages": "中文,英文,日语",
            "bloodType": "O",
            "smoking": False,
            "drinking": False,
            "tags": "测试,更新,社交"
        }
        
        # 尝试不同的更新方式
        update_endpoints = [
            (f"/api/users/profile/{self.user_id}", "PUT"),
            (f"/api/users/profile", "PUT"),
            (f"/api/users/{self.user_id}/profile", "PUT"),
            (f"/api/users/profile/update", "POST")
        ]
        
        for endpoint, method in update_endpoints:
            print(f"\n   尝试 {method} {endpoint}")
            try:
                if method == "PUT":
                    response = self.session.put(
                        f"{self.base_url}{endpoint}",
                        headers=headers,
                        json=update_data
                    )
                else:
                    response = self.session.post(
                        f"{self.base_url}{endpoint}",
                        headers=headers,
                        json=update_data
                    )
                
                self.log_result(endpoint, method, response.status_code, response.text, headers)
                
                if response.status_code == 200:
                    print(f"   ✅ 更新成功!")
                    break
                    
            except Exception as e:
                self.log_result(endpoint, method, 0, "", headers, str(e))
    
    def test_all_authenticated_endpoints(self):
        """测试所有需要认证的接口"""
        if not self.token:
            print("❌ 没有有效的token，跳过认证接口测试")
            return
        
        print("\n=== 测试所有需要认证的接口 ===")
        
        headers = {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }
        
        # 所有需要测试的接口
        endpoints = [
            # 用户相关
            ("/api/users/profile", "GET"),
            ("/api/users/profile/test-token", "GET"),
            (f"/api/users/{self.user_id}", "GET"),
            ("/api/users/search", "GET"),
            
            # 动态相关
            ("/api/dynamics", "GET"),
            ("/api/dynamics/my", "GET"),
            
            # 消息相关
            ("/api/messages", "GET"),
            ("/api/messages/conversations", "GET"),
            ("/api/messages/unread-count", "GET"),
            
            # 通话相关
            ("/api/calls", "GET"),
            ("/api/calls/history", "GET"),
            
            # 钱包相关
            ("/api/wallet", "GET"),
            ("/api/wallet/balance", "GET"),
            ("/api/wallet/transactions", "GET"),
            
            # VIP相关
            ("/api/vip", "GET"),
            ("/api/vip/status", "GET"),
            
            # 礼物相关
            ("/api/gifts", "GET"),
            ("/api/gifts/received", "GET"),
            
            # 关系相关
            ("/api/relationships/friends", "GET"),
            ("/api/relationships/followers", "GET"),
            ("/api/relationships/following", "GET"),
            
            # 设置相关
            ("/api/settings", "GET"),
            ("/api/settings/privacy", "GET"),
            
            # 优惠券相关
            ("/api/coupons", "GET"),
            ("/api/coupons/my", "GET"),
        ]
        
        for endpoint, method in endpoints:
            try:
                if method == "GET":
                    response = self.session.get(f"{self.base_url}{endpoint}", headers=headers)
                elif method == "POST":
                    response = self.session.post(f"{self.base_url}{endpoint}", headers=headers, json={})
                elif method == "PUT":
                    response = self.session.put(f"{self.base_url}{endpoint}", headers=headers, json={})
                elif method == "DELETE":
                    response = self.session.delete(f"{self.base_url}{endpoint}", headers=headers)
                
                self.log_result(endpoint, method, response.status_code, response.text, headers)
                
            except Exception as e:
                self.log_result(endpoint, method, 0, "", headers, str(e))
    
    def test_public_endpoints(self):
        """测试所有公开接口"""
        print("\n=== 测试公开接口 ===")
        
        endpoints = [
            ("/api/health", "GET"),
            ("/swagger-ui.html", "GET"),
            ("/api-docs", "GET"),
            ("/api/auth/send-code", "POST"),
            ("/api/database/test", "GET"),
        ]
        
        for endpoint, method in endpoints:
            try:
                if method == "GET":
                    response = self.session.get(f"{self.base_url}{endpoint}")
                elif method == "POST":
                    if endpoint == "/api/auth/send-code":
                        response = self.session.post(f"{self.base_url}{endpoint}", 
                                                   params={"phone": "13800138002"})
                    else:
                        response = self.session.post(f"{self.base_url}{endpoint}")
                
                self.log_result(endpoint, method, response.status_code, response.text)
                
            except Exception as e:
                self.log_result(endpoint, method, 0, "", error=str(e))
    
    def analyze_errors(self):
        """分析错误并提供解决方案"""
        print("\n" + "="*60)
        print("错误分析和解决方案")
        print("="*60)
        
        # 分析401错误
        if self.error_analysis["401_errors"]:
            print("\n❌ 401 认证错误分析:")
            print(f"   共发现 {len(self.error_analysis['401_errors'])} 个401错误")
            
            for error in self.error_analysis["401_errors"][:3]:  # 只显示前3个
                print(f"\n   接口: {error['method']} {error['endpoint']}")
                if error.get('response'):
                    print(f"   响应: {error['response'][:200]}")
            
            print("\n   可能的原因:")
            print("   1. JWT token格式不正确")
            print("   2. Token已过期")
            print("   3. Authorization header格式错误")
            print("   4. Spring Security配置问题")
            
            print("\n   建议解决方案:")
            print("   1. 检查JWT密钥配置是否一致")
            print("   2. 确认token过期时间设置")
            print("   3. 检查Spring Security的JWT过滤器")
            print("   4. 确认请求头格式: 'Authorization: Bearer <token>'")
        
        # 分析500错误
        if self.error_analysis["500_errors"]:
            print("\n❌ 500 服务器错误分析:")
            print(f"   共发现 {len(self.error_analysis['500_errors'])} 个500错误")
            
            for error in self.error_analysis["500_errors"][:3]:  # 只显示前3个
                print(f"\n   接口: {error['method']} {error['endpoint']}")
                if error.get('response'):
                    try:
                        error_json = json.loads(error['response'])
                        if 'message' in error_json:
                            print(f"   错误信息: {error_json['message']}")
                        if 'trace' in error_json:
                            trace_lines = error_json['trace'].split('\n')[:3]
                            print("   堆栈跟踪:")
                            for line in trace_lines:
                                print(f"     {line}")
                    except:
                        print(f"   响应: {error['response'][:200]}")
            
            print("\n   可能的原因:")
            print("   1. 数据库连接问题")
            print("   2. 空指针异常")
            print("   3. 数据类型转换错误")
            print("   4. 业务逻辑异常")
            
            print("\n   建议解决方案:")
            print("   1. 检查数据库连接配置")
            print("   2. 添加空值检查")
            print("   3. 检查实体类映射")
            print("   4. 查看后端日志详细错误")
        
        # 其他错误
        if self.error_analysis["other_errors"]:
            print(f"\n⚠️ 其他错误: {len(self.error_analysis['other_errors'])} 个")
            for error in self.error_analysis["other_errors"][:3]:
                print(f"   {error['status_code']} - {error['method']} {error['endpoint']}")
    
    def generate_report(self):
        """生成详细的测试报告"""
        print("\n" + "="*60)
        print("测试报告总结")
        print("="*60)
        
        total_tests = len(self.test_results)
        passed_tests = sum(1 for r in self.test_results if r["success"])
        failed_tests = total_tests - passed_tests
        
        print(f"\n📊 测试统计:")
        print(f"   总测试数: {total_tests}")
        print(f"   ✅ 通过: {passed_tests}")
        print(f"   ❌ 失败: {failed_tests}")
        print(f"   成功率: {passed_tests/total_tests*100:.1f}%" if total_tests > 0 else "N/A")
        
        print(f"\n📈 错误分布:")
        print(f"   401错误: {len(self.error_analysis['401_errors'])}")
        print(f"   500错误: {len(self.error_analysis['500_errors'])}")
        print(f"   其他错误: {len(self.error_analysis['other_errors'])}")
        
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
        
        with open("api_test_report_detailed.json", "w", encoding="utf-8") as f:
            json.dump(report, f, ensure_ascii=False, indent=2)
        
        print(f"\n📄 详细报告已保存到: api_test_report_detailed.json")
        
        # 生成修复建议文件
        self.generate_fix_suggestions()
    
    def generate_fix_suggestions(self):
        """生成具体的修复建议"""
        suggestions = []
        
        if self.error_analysis["401_errors"]:
            suggestions.append({
                "issue": "401 Authentication Errors",
                "affected_endpoints": [e["endpoint"] for e in self.error_analysis["401_errors"]],
                "fixes": [
                    "Check JWT secret key configuration in application.yml",
                    "Verify token expiration time settings",
                    "Update JwtAuthenticationFilter to properly validate tokens",
                    "Ensure SecurityConfig allows proper authentication flow"
                ]
            })
        
        if self.error_analysis["500_errors"]:
            suggestions.append({
                "issue": "500 Server Errors",
                "affected_endpoints": [e["endpoint"] for e in self.error_analysis["500_errors"]],
                "fixes": [
                    "Add null checks in UserProfileController.updateProfile()",
                    "Verify database entity mappings",
                    "Check for missing required fields in DTOs",
                    "Add proper exception handling in service layer"
                ]
            })
        
        with open("api_fix_suggestions.json", "w", encoding="utf-8") as f:
            json.dump(suggestions, f, ensure_ascii=False, indent=2)
        
        print(f"🔧 修复建议已保存到: api_fix_suggestions.json")
    
    def run_all_tests(self):
        """运行所有测试"""
        print("="*60)
        print("SocialMeet API 完整测试和错误分析")
        print("="*60)
        print(f"目标服务器: {self.base_url}")
        print(f"测试时间: {datetime.now().isoformat()}")
        
        # 1. 健康检查
        if not self.test_health_check():
            print("\n❌ 后端服务不可用，请先启动后端服务")
            print("   运行: cd SocialMeet && ./gradlew bootRun")
            return
        
        # 2. 测试公开接口
        self.test_public_endpoints()
        
        # 3. 测试认证流程
        if self.test_auth_flow():
            # 4. 重点测试用户资料操作
            self.test_user_profile_operations()
            
            # 5. 测试所有需要认证的接口
            self.test_all_authenticated_endpoints()
        else:
            print("\n❌ 认证流程失败，无法测试需要认证的接口")
        
        # 6. 分析错误
        self.analyze_errors()
        
        # 7. 生成报告
        self.generate_report()

def main():
    """主函数"""
    import sys
    
    # 检查命令行参数
    base_url = "http://localhost:8080"
    if len(sys.argv) > 1:
        base_url = sys.argv[1]
    
    print(f"开始测试服务器: {base_url}")
    
    # 运行测试
    tester = APITestAndFix(base_url)
    tester.run_all_tests()

if __name__ == "__main__":
    main()
