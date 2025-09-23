#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
完整的支付功能测试脚本
测试支付宝支付的完整流程
"""

import requests
import json
import time
import sys
from datetime import datetime

# 服务器配置
BASE_URL = "http://localhost:8080"
API_BASE = f"{BASE_URL}/api"

# 测试用户信息
TEST_USER = {
    "phone": "13800138000",
    "password": "123456"
}

class PaymentTester:
    def __init__(self):
        self.session = requests.Session()
        self.jwt_token = None
        self.user_id = None
        
    def log(self, message):
        """打印带时间戳的日志"""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        print(f"[{timestamp}] {message}")
        
    def test_server_health(self):
        """测试服务器健康状态"""
        try:
            self.log("🔍 测试服务器健康状态...")
            response = self.session.get(f"{API_BASE}/auth/health", timeout=10)
            
            if response.status_code == 200:
                self.log("✅ 服务器运行正常")
                return True
            else:
                self.log(f"❌ 服务器健康检查失败: {response.status_code}")
                return False
                
        except requests.exceptions.RequestException as e:
            self.log(f"❌ 服务器连接失败: {e}")
            return False
            
    def login_test_user(self):
        """登录测试用户"""
        try:
            self.log("🔐 登录测试用户...")
            
            login_data = {
                "phone": TEST_USER["phone"],
                "password": TEST_USER["password"]
            }
            
            response = self.session.post(
                f"{API_BASE}/auth/login",
                json=login_data,
                headers={"Content-Type": "application/json"},
                timeout=10
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    self.jwt_token = result["data"]["token"]
                    self.user_id = result["data"]["userId"]
                    self.log(f"✅ 登录成功，用户ID: {self.user_id}")
                    return True
                else:
                    self.log(f"❌ 登录失败: {result.get('message', '未知错误')}")
                    return False
            else:
                self.log(f"❌ 登录请求失败: {response.status_code}")
                return False
                
        except requests.exceptions.RequestException as e:
            self.log(f"❌ 登录请求异常: {e}")
            return False
            
    def get_auth_headers(self):
        """获取认证头"""
        if not self.jwt_token:
            return {}
        return {"Authorization": f"Bearer {self.jwt_token}"}
        
    def test_get_recharge_packages(self):
        """测试获取充值套餐"""
        try:
            self.log("📦 测试获取充值套餐...")
            
            response = self.session.get(
                f"{API_BASE}/recharge-packages",
                headers=self.get_auth_headers(),
                timeout=10
            )
            
            if response.status_code == 200:
                packages = response.json()
                self.log(f"✅ 获取充值套餐成功，共 {len(packages)} 个套餐")
                
                # 显示套餐信息
                for package in packages[:3]:  # 只显示前3个
                    self.log(f"   📦 套餐: {package['name']} - ¥{package['price']} = {package['coins']}金币")
                    
                return packages
            else:
                self.log(f"❌ 获取充值套餐失败: {response.status_code}")
                return []
                
        except requests.exceptions.RequestException as e:
            self.log(f"❌ 获取充值套餐异常: {e}")
            return []
            
    def test_create_order(self, package_data=None):
        """测试创建支付订单"""
        try:
            self.log("💳 测试创建支付订单...")
            
            # 默认测试套餐
            if not package_data:
                package_data = {
                    "packageId": "package_1",
                    "amount": 6.00,
                    "coins": 60,
                    "paymentMethod": "ALIPAY",
                    "description": "充值60金币"
                }
            
            response = self.session.post(
                f"{API_BASE}/recharge/create-order",
                json=package_data,
                headers={
                    **self.get_auth_headers(),
                    "Content-Type": "application/json"
                },
                timeout=15
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    order_data = result["data"]
                    self.log(f"✅ 创建订单成功")
                    self.log(f"   订单号: {order_data.get('orderId')}")
                    self.log(f"   金额: ¥{order_data.get('amount')}")
                    self.log(f"   金币: {order_data.get('coins')}")
                    
                    # 检查支付宝订单信息
                    if "alipayOrderInfo" in order_data:
                        self.log("   ✅ 支付宝订单信息已生成")
                    else:
                        self.log("   ⚠️  支付宝订单信息缺失")
                        
                    return order_data
                else:
                    self.log(f"❌ 创建订单失败: {result.get('message', '未知错误')}")
                    return None
            else:
                self.log(f"❌ 创建订单请求失败: {response.status_code}")
                try:
                    error_data = response.json()
                    self.log(f"   错误详情: {error_data}")
                except:
                    self.log(f"   响应内容: {response.text}")
                return None
                
        except requests.exceptions.RequestException as e:
            self.log(f"❌ 创建订单异常: {e}")
            return None
            
    def test_query_order_status(self, order_id):
        """测试查询订单状态"""
        try:
            self.log(f"🔍 测试查询订单状态: {order_id}")
            
            response = self.session.get(
                f"{API_BASE}/recharge/order/{order_id}",
                headers=self.get_auth_headers(),
                timeout=10
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    order = result["data"]
                    self.log(f"✅ 查询订单状态成功")
                    self.log(f"   订单状态: {order.get('status')}")
                    self.log(f"   创建时间: {order.get('createdAt')}")
                    self.log(f"   更新时间: {order.get('updatedAt')}")
                    return order
                else:
                    self.log(f"❌ 查询订单失败: {result.get('message', '未知错误')}")
                    return None
            else:
                self.log(f"❌ 查询订单请求失败: {response.status_code}")
                return None
                
        except requests.exceptions.RequestException as e:
            self.log(f"❌ 查询订单异常: {e}")
            return None
            
    def test_wallet_balance(self):
        """测试获取钱包余额"""
        try:
            self.log("💰 测试获取钱包余额...")
            
            response = self.session.get(
                f"{API_BASE}/wallet/balance",
                headers=self.get_auth_headers(),
                timeout=10
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    wallet = result["data"]
                    self.log(f"✅ 获取钱包余额成功")
                    self.log(f"   总余额: {wallet.get('totalBalance', 0)}金币")
                    self.log(f"   充值余额: {wallet.get('rechargeBalance', 0)}金币")
                    self.log(f"   礼物余额: {wallet.get('giftBalance', 0)}金币")
                    return wallet
                else:
                    self.log(f"❌ 获取钱包余额失败: {result.get('message', '未知错误')}")
                    return None
            else:
                self.log(f"❌ 获取钱包余额请求失败: {response.status_code}")
                return None
                
        except requests.exceptions.RequestException as e:
            self.log(f"❌ 获取钱包余额异常: {e}")
            return None
            
    def simulate_payment_callback(self, order_id):
        """模拟支付回调"""
        try:
            self.log(f"📞 模拟支付宝回调: {order_id}")
            
            # 模拟支付宝回调参数
            callback_data = {
                "out_trade_no": order_id,
                "trade_no": f"2024{int(time.time())}001",
                "trade_status": "TRADE_SUCCESS",
                "total_amount": "6.00",
                "buyer_id": "2088102177846875",
                "seller_id": "2088102177846875",
                "gmt_payment": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
                "sign": "mock_signature"
            }
            
            response = self.session.post(
                f"{API_BASE}/recharge/callback/alipay",
                data=callback_data,
                timeout=10
            )
            
            if response.status_code == 200:
                self.log("✅ 支付回调模拟成功")
                return True
            else:
                self.log(f"❌ 支付回调模拟失败: {response.status_code}")
                return False
                
        except requests.exceptions.RequestException as e:
            self.log(f"❌ 支付回调模拟异常: {e}")
            return False
            
    def run_complete_test(self):
        """运行完整的支付测试"""
        self.log("🚀 开始完整支付功能测试")
        self.log("=" * 50)
        
        # 1. 测试服务器健康状态
        if not self.test_server_health():
            self.log("❌ 服务器不可用，终止测试")
            return False
            
        # 等待服务器完全启动
        self.log("⏳ 等待服务器完全启动...")
        time.sleep(5)
        
        # 2. 登录测试用户
        if not self.login_test_user():
            self.log("❌ 用户登录失败，终止测试")
            return False
            
        # 3. 测试获取充值套餐
        packages = self.test_get_recharge_packages()
        if not packages:
            self.log("⚠️  无法获取充值套餐，使用默认套餐继续测试")
            
        # 4. 测试创建订单
        order_data = self.test_create_order()
        if not order_data:
            self.log("❌ 创建订单失败，终止测试")
            return False
            
        order_id = order_data.get("orderId")
        
        # 5. 测试查询订单状态
        self.test_query_order_status(order_id)
        
        # 6. 测试钱包余额
        self.test_wallet_balance()
        
        # 7. 模拟支付回调
        self.simulate_payment_callback(order_id)
        
        # 8. 再次查询订单状态
        self.log("🔄 支付后再次查询订单状态...")
        time.sleep(2)
        self.test_query_order_status(order_id)
        
        # 9. 再次查询钱包余额
        self.log("🔄 支付后再次查询钱包余额...")
        self.test_wallet_balance()
        
        self.log("=" * 50)
        self.log("✅ 完整支付功能测试完成")
        return True

def main():
    """主函数"""
    tester = PaymentTester()
    
    try:
        success = tester.run_complete_test()
        if success:
            print("\n🎉 所有测试通过！")
            sys.exit(0)
        else:
            print("\n❌ 测试失败！")
            sys.exit(1)
            
    except KeyboardInterrupt:
        print("\n⏹️  测试被用户中断")
        sys.exit(1)
    except Exception as e:
        print(f"\n💥 测试过程中发生异常: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
