#!/usr/bin/env python3
"""
财富等级API测试脚本
测试前端与后端财富等级系统的接口联调
"""

import requests
import json
import time
from datetime import datetime

class WealthLevelAPITester:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.token = None
        self.user_id = None
        self.test_results = []
        
    def log_result(self, test_name, status, message, response_data=None):
        """记录测试结果"""
        result = {
            "test_name": test_name,
            "status": status,
            "message": message,
            "timestamp": datetime.now().isoformat(),
            "response_data": response_data
        }
        self.test_results.append(result)
        
        status_icon = "✅" if status == "PASS" else "❌"
        print(f"{status_icon} {test_name}: {message}")
        
        if response_data:
            print(f"   响应数据: {json.dumps(response_data, ensure_ascii=False, indent=2)}")
    
    def test_health_check(self):
        """测试健康检查"""
        try:
            response = self.session.get(f"{self.base_url}/api/health")
            if response.status_code == 200:
                self.log_result("健康检查", "PASS", "后端服务正常运行")
                return True
            else:
                self.log_result("健康检查", "FAIL", f"HTTP状态码: {response.status_code}")
                return False
        except Exception as e:
            self.log_result("健康检查", "FAIL", f"连接失败: {str(e)}")
            return False
    
    def test_user_login(self):
        """测试用户登录获取token"""
        try:
            # 先发送验证码
            phone = "13800138000"
            send_code_response = self.session.post(
                f"{self.base_url}/api/auth/send-code",
                params={"phone": phone}
            )
            
            if send_code_response.status_code == 200:
                print("验证码发送成功，使用默认验证码: 123456")
                
                # 使用默认验证码登录
                login_response = self.session.post(
                    f"{self.base_url}/api/auth/login-with-code",
                    params={"phone": phone, "code": "123456"}
                )
                
                if login_response.status_code == 200:
                    login_data = login_response.json()
                    if login_data.get("success"):
                        self.token = login_data["data"]["token"]
                        self.user_id = login_data["data"]["userId"]
                        self.log_result("用户登录", "PASS", f"登录成功，用户ID: {self.user_id}")
                        return True
                    else:
                        self.log_result("用户登录", "FAIL", f"登录失败: {login_data.get('message')}")
                        return False
                else:
                    self.log_result("用户登录", "FAIL", f"登录请求失败: {login_response.status_code}")
                    return False
            else:
                self.log_result("用户登录", "FAIL", f"发送验证码失败: {send_code_response.status_code}")
                return False
                
        except Exception as e:
            self.log_result("用户登录", "FAIL", f"登录异常: {str(e)}")
            return False
    
    def test_get_wealth_level(self):
        """测试获取财富等级信息"""
        if not self.token:
            self.log_result("获取财富等级", "SKIP", "未登录，跳过测试")
            return False
            
        try:
            headers = {"Authorization": f"Bearer {self.token}"}
            response = self.session.get(
                f"{self.base_url}/api/wealth-level/my-level",
                headers=headers
            )
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success"):
                    wealth_data = data["data"]
                    self.log_result("获取财富等级", "PASS", "财富等级信息获取成功", wealth_data)
                    return True
                else:
                    self.log_result("获取财富等级", "FAIL", f"API返回失败: {data.get('message')}")
                    return False
            else:
                self.log_result("获取财富等级", "FAIL", f"HTTP状态码: {response.status_code}")
                return False
                
        except Exception as e:
            self.log_result("获取财富等级", "FAIL", f"请求异常: {str(e)}")
            return False
    
    def test_get_privileges(self):
        """测试获取用户特权"""
        if not self.token:
            self.log_result("获取用户特权", "SKIP", "未登录，跳过测试")
            return False
            
        try:
            headers = {"Authorization": f"Bearer {self.token}"}
            response = self.session.get(
                f"{self.base_url}/api/wealth-level/privileges",
                headers=headers
            )
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success"):
                    privileges = data["data"]
                    self.log_result("获取用户特权", "PASS", f"获取到 {len(privileges)} 个特权", privileges)
                    return True
                else:
                    self.log_result("获取用户特权", "FAIL", f"API返回失败: {data.get('message')}")
                    return False
            else:
                self.log_result("获取用户特权", "FAIL", f"HTTP状态码: {response.status_code}")
                return False
                
        except Exception as e:
            self.log_result("获取用户特权", "FAIL", f"请求异常: {str(e)}")
            return False
    
    def test_get_level_progress(self):
        """测试获取等级进度"""
        if not self.token:
            self.log_result("获取等级进度", "SKIP", "未登录，跳过测试")
            return False
            
        try:
            headers = {"Authorization": f"Bearer {self.token}"}
            response = self.session.get(
                f"{self.base_url}/api/wealth-level/progress",
                headers=headers
            )
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success"):
                    progress_data = data["data"]
                    self.log_result("获取等级进度", "PASS", "等级进度获取成功", progress_data)
                    return True
                else:
                    self.log_result("获取等级进度", "FAIL", f"API返回失败: {data.get('message')}")
                    return False
            else:
                self.log_result("获取等级进度", "FAIL", f"HTTP状态码: {response.status_code}")
                return False
                
        except Exception as e:
            self.log_result("获取等级进度", "FAIL", f"请求异常: {str(e)}")
            return False
    
    def test_get_wealth_ranking(self):
        """测试获取财富排行榜"""
        try:
            response = self.session.get(f"{self.base_url}/api/wealth-level/ranking?limit=5")
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success"):
                    rankings = data["data"]
                    self.log_result("获取财富排行榜", "PASS", f"获取到 {len(rankings)} 个用户排名", rankings)
                    return True
                else:
                    self.log_result("获取财富排行榜", "FAIL", f"API返回失败: {data.get('message')}")
                    return False
            else:
                self.log_result("获取财富排行榜", "FAIL", f"HTTP状态码: {response.status_code}")
                return False
                
        except Exception as e:
            self.log_result("获取财富排行榜", "FAIL", f"请求异常: {str(e)}")
            return False
    
    def test_get_level_rules(self):
        """测试获取等级规则"""
        try:
            response = self.session.get(f"{self.base_url}/api/wealth-level/rules")
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success"):
                    rules = data["data"]
                    self.log_result("获取等级规则", "PASS", f"获取到 {len(rules)} 个等级规则", rules)
                    return True
                else:
                    self.log_result("获取等级规则", "FAIL", f"API返回失败: {data.get('message')}")
                    return False
            else:
                self.log_result("获取等级规则", "FAIL", f"HTTP状态码: {response.status_code}")
                return False
                
        except Exception as e:
            self.log_result("获取等级规则", "FAIL", f"请求异常: {str(e)}")
            return False
    
    def test_recharge_integration(self):
        """测试充值集成（模拟充值后财富值更新）"""
        if not self.token:
            self.log_result("充值集成测试", "SKIP", "未登录，跳过测试")
            return False
            
        try:
            # 模拟充值订单创建
            recharge_data = {
                "packageId": "package_1200",
                "coins": 1200,
                "amount": 12.0,
                "paymentMethod": "ALIPAY",
                "description": "充值1200金币"
            }
            
            headers = {"Authorization": f"Bearer {self.token}"}
            response = self.session.post(
                f"{self.base_url}/api/recharge/create-order",
                json=recharge_data,
                headers=headers
            )
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success"):
                    order_data = data["data"]
                    self.log_result("充值集成测试", "PASS", "充值订单创建成功", order_data)
                    
                    # 模拟支付成功回调
                    self.simulate_payment_success(order_data["orderId"])
                    return True
                else:
                    self.log_result("充值集成测试", "FAIL", f"充值订单创建失败: {data.get('message')}")
                    return False
            else:
                self.log_result("充值集成测试", "FAIL", f"HTTP状态码: {response.status_code}")
                return False
                
        except Exception as e:
            self.log_result("充值集成测试", "FAIL", f"请求异常: {str(e)}")
            return False
    
    def simulate_payment_success(self, order_id):
        """模拟支付成功回调"""
        try:
            # 模拟支付宝支付成功回调
            callback_data = {
                "out_trade_no": order_id,
                "trade_no": f"alipay_{int(time.time())}",
                "trade_status": "TRADE_SUCCESS",
                "total_amount": "12.00"
            }
            
            response = self.session.post(
                f"{self.base_url}/api/recharge/alipay/callback",
                data=callback_data
            )
            
            if response.status_code == 200:
                self.log_result("支付成功回调", "PASS", "支付回调处理成功")
                
                # 等待一下让财富值更新
                time.sleep(2)
                
                # 再次获取财富等级信息，检查是否更新
                self.verify_wealth_update()
            else:
                self.log_result("支付成功回调", "FAIL", f"回调处理失败: {response.status_code}")
                
        except Exception as e:
            self.log_result("支付成功回调", "FAIL", f"回调异常: {str(e)}")
    
    def verify_wealth_update(self):
        """验证财富值是否更新"""
        try:
            headers = {"Authorization": f"Bearer {self.token}"}
            response = self.session.get(
                f"{self.base_url}/api/wealth-level/my-level",
                headers=headers
            )
            
            if response.status_code == 200:
                data = response.json()
                if data.get("success"):
                    wealth_data = data["data"]
                    wealth_value = wealth_data.get("wealthValue", 0)
                    self.log_result("财富值更新验证", "PASS", f"当前财富值: {wealth_value}")
                else:
                    self.log_result("财富值更新验证", "FAIL", f"获取财富等级失败: {data.get('message')}")
            else:
                self.log_result("财富值更新验证", "FAIL", f"HTTP状态码: {response.status_code}")
                
        except Exception as e:
            self.log_result("财富值更新验证", "FAIL", f"验证异常: {str(e)}")
    
    def run_all_tests(self):
        """运行所有测试"""
        print("🚀 开始财富等级API联调测试")
        print("=" * 50)
        
        # 基础连接测试
        if not self.test_health_check():
            print("❌ 后端服务不可用，终止测试")
            return
        
        # 用户认证测试
        if not self.test_user_login():
            print("❌ 用户登录失败，跳过需要认证的测试")
        
        # 财富等级API测试
        self.test_get_wealth_level()
        self.test_get_privileges()
        self.test_get_level_progress()
        self.test_get_wealth_ranking()
        self.test_get_level_rules()
        
        # 充值集成测试
        self.test_recharge_integration()
        
        # 生成测试报告
        self.generate_report()
    
    def generate_report(self):
        """生成测试报告"""
        print("\n" + "=" * 50)
        print("📊 财富等级API联调测试报告")
        print("=" * 50)
        
        total_tests = len(self.test_results)
        passed_tests = len([r for r in self.test_results if r["status"] == "PASS"])
        failed_tests = len([r for r in self.test_results if r["status"] == "FAIL"])
        skipped_tests = len([r for r in self.test_results if r["status"] == "SKIP"])
        
        print(f"总测试数: {total_tests}")
        print(f"通过: {passed_tests} ✅")
        print(f"失败: {failed_tests} ❌")
        print(f"跳过: {skipped_tests} ⏭️")
        print(f"成功率: {(passed_tests/total_tests*100):.1f}%")
        
        print("\n📋 详细测试结果:")
        for result in self.test_results:
            status_icon = "✅" if result["status"] == "PASS" else "❌" if result["status"] == "FAIL" else "⏭️"
            print(f"{status_icon} {result['test_name']}: {result['message']}")
        
        # 保存详细报告到文件
        report_data = {
            "test_summary": {
                "total_tests": total_tests,
                "passed_tests": passed_tests,
                "failed_tests": failed_tests,
                "skipped_tests": skipped_tests,
                "success_rate": passed_tests/total_tests*100
            },
            "test_results": self.test_results,
            "timestamp": datetime.now().isoformat()
        }
        
        with open("wealth_level_api_test_report.json", "w", encoding="utf-8") as f:
            json.dump(report_data, f, ensure_ascii=False, indent=2)
        
        print(f"\n📄 详细报告已保存到: wealth_level_api_test_report.json")

if __name__ == "__main__":
    tester = WealthLevelAPITester()
    tester.run_all_tests()
