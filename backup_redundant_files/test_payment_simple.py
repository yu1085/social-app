#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
简化的支付功能测试脚本
测试Android客户端的支付功能
"""

import requests
import json
import time
import sys
from datetime import datetime

# 服务器配置
BASE_URL = "http://10.0.2.2:8080"  # Android模拟器访问本机地址
API_BASE = f"{BASE_URL}/api"

# 测试用户信息
TEST_USER = {
    "phone": "13800138000",
    "password": "123456"
}

class SimplePaymentTester:
    def __init__(self):
        self.session = requests.Session()
        self.jwt_token = None
        self.user_id = None
        
    def log(self, message):
        """打印带时间戳的日志"""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        print(f"[{timestamp}] {message}")
        
    def test_server_connection(self):
        """测试服务器连接"""
        try:
            self.log("🔍 测试服务器连接...")
            response = self.session.get(f"{API_BASE}/auth/health", timeout=5)
            
            if response.status_code == 200:
                self.log("✅ 服务器连接成功")
                return True
            else:
                self.log(f"❌ 服务器响应异常: {response.status_code}")
                return False
                
        except requests.exceptions.RequestException as e:
            self.log(f"❌ 服务器连接失败: {e}")
            return False
            
    def test_login(self):
        """测试用户登录"""
        try:
            self.log("🔐 测试用户登录...")
            
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
            
    def test_create_order(self):
        """测试创建支付订单"""
        try:
            self.log("💳 测试创建支付订单...")
            
            order_data = {
                "packageId": "package_1200",
                "coins": 1200,
                "amount": 12.00,
                "paymentMethod": "ALIPAY",
                "description": "充值1200金币"
            }
            
            headers = {
                "Authorization": f"Bearer {self.jwt_token}",
                "Content-Type": "application/json"
            }
            
            response = self.session.post(
                f"{API_BASE}/recharge/create-order",
                json=order_data,
                headers=headers,
                timeout=15
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    order_info = result["data"]
                    self.log(f"✅ 创建订单成功")
                    self.log(f"   订单号: {order_info.get('orderId')}")
                    self.log(f"   金额: ¥{order_info.get('amount')}")
                    self.log(f"   金币: {order_info.get('coins')}")
                    
                    # 检查支付信息
                    if "alipayOrderInfo" in order_info:
                        self.log("   ✅ 支付宝订单信息已生成")
                        self.log(f"   订单信息长度: {len(str(order_info['alipayOrderInfo']))}")
                    else:
                        self.log("   ⚠️  支付宝订单信息缺失")
                        
                    return order_info
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
            
    def test_wechat_order(self):
        """测试微信支付订单"""
        try:
            self.log("💳 测试创建微信支付订单...")
            
            order_data = {
                "packageId": "package_5800",
                "coins": 5800,
                "amount": 58.00,
                "paymentMethod": "WECHAT",
                "description": "充值5800金币"
            }
            
            headers = {
                "Authorization": f"Bearer {self.jwt_token}",
                "Content-Type": "application/json"
            }
            
            response = self.session.post(
                f"{API_BASE}/recharge/create-order",
                json=order_data,
                headers=headers,
                timeout=15
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    order_info = result["data"]
                    self.log(f"✅ 创建微信支付订单成功")
                    self.log(f"   订单号: {order_info.get('orderId')}")
                    self.log(f"   金额: ¥{order_info.get('amount')}")
                    self.log(f"   金币: {order_info.get('coins')}")
                    
                    # 检查微信支付信息
                    if "wechatPayInfo" in order_info:
                        wechat_info = order_info["wechatPayInfo"]
                        self.log("   ✅ 微信支付信息已生成")
                        self.log(f"   AppId: {wechat_info.get('appId', 'N/A')}")
                        self.log(f"   PartnerId: {wechat_info.get('partnerId', 'N/A')}")
                        self.log(f"   PrepayId: {wechat_info.get('prepayId', 'N/A')}")
                    else:
                        self.log("   ⚠️  微信支付信息缺失")
                        
                    return order_info
                else:
                    self.log(f"❌ 创建微信支付订单失败: {result.get('message', '未知错误')}")
                    return None
            else:
                self.log(f"❌ 创建微信支付订单请求失败: {response.status_code}")
                return None
                
        except requests.exceptions.RequestException as e:
            self.log(f"❌ 创建微信支付订单异常: {e}")
            return None
            
    def test_wallet_balance(self):
        """测试获取钱包余额"""
        try:
            self.log("💰 测试获取钱包余额...")
            
            headers = {
                "Authorization": f"Bearer {self.jwt_token}",
                "Content-Type": "application/json"
            }
            
            response = self.session.get(
                f"{API_BASE}/wallet/balance",
                headers=headers,
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
            
    def run_payment_test(self):
        """运行支付功能测试"""
        self.log("🚀 开始支付功能测试")
        self.log("=" * 50)
        
        # 1. 测试服务器连接
        if not self.test_server_connection():
            self.log("❌ 服务器不可用，终止测试")
            return False
            
        # 2. 测试用户登录
        if not self.test_login():
            self.log("❌ 用户登录失败，终止测试")
            return False
            
        # 3. 测试获取钱包余额
        self.test_wallet_balance()
        
        # 4. 测试创建支付宝订单
        alipay_order = self.test_create_order()
        if not alipay_order:
            self.log("❌ 支付宝订单创建失败")
            
        # 5. 测试创建微信支付订单
        wechat_order = self.test_wechat_order()
        if not wechat_order:
            self.log("❌ 微信支付订单创建失败")
            
        # 6. 再次测试钱包余额
        self.log("🔄 测试后再次查询钱包余额...")
        self.test_wallet_balance()
        
        self.log("=" * 50)
        self.log("✅ 支付功能测试完成")
        return True

def main():
    """主函数"""
    tester = SimplePaymentTester()
    
    try:
        success = tester.run_payment_test()
        if success:
            print("\n🎉 支付功能测试完成！")
            sys.exit(0)
        else:
            print("\n❌ 支付功能测试失败！")
            sys.exit(1)
            
    except KeyboardInterrupt:
        print("\n⏹️  测试被用户中断")
        sys.exit(1)
    except Exception as e:
        print(f"\n💥 测试过程中发生异常: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()