#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
Android客户端支付功能测试脚本
测试RechargeViewModel的支付功能
"""

import json
import time
import sys
from datetime import datetime

class AndroidPaymentTester:
    def __init__(self):
        self.test_results = []
        
    def log(self, message):
        """打印带时间戳的日志"""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        print(f"[{timestamp}] {message}")
        
    def test_recharge_viewmodel_structure(self):
        """测试RechargeViewModel结构"""
        self.log("🔍 测试RechargeViewModel结构...")
        
        try:
            # 读取RechargeViewModel文件
            with open('app/src/main/java/com/example/myapplication/viewmodel/RechargeViewModel.kt', 'r', encoding='utf-8') as f:
                content = f.read()
            
            # 检查关键组件
            checks = [
                ("AuthManager导入", "import com.example.myapplication.auth.AuthManager" in content),
                ("AuthManager初始化", "authManager = AuthManager.getInstance(context)" in content),
                ("支付宝支付方法", "processAlipayPayment" in content),
                ("微信支付方法", "processWechatPayment" in content),
                ("创建订单方法", "createBackendOrder" in content),
                ("支付结果处理", "handlePaymentResult" in content),
                ("UI状态管理", "RechargeUiState" in content),
                ("充值套餐数据", "RechargePackage" in content),
                ("支付方式枚举", "PaymentMethod" in content)
            ]
            
            all_passed = True
            for check_name, passed in checks:
                if passed:
                    self.log(f"   ✅ {check_name}: 通过")
                else:
                    self.log(f"   ❌ {check_name}: 失败")
                    all_passed = False
                    
            self.test_results.append(("RechargeViewModel结构", all_passed))
            return all_passed
            
        except Exception as e:
            self.log(f"   ❌ 测试RechargeViewModel结构失败: {e}")
            self.test_results.append(("RechargeViewModel结构", False))
            return False
            
    def test_payment_models(self):
        """测试支付相关模型"""
        self.log("🔍 测试支付相关模型...")
        
        try:
            # 检查模型文件
            model_files = [
                'app/src/main/java/com/example/myapplication/model/RechargePackage.kt',
                'app/src/main/java/com/example/myapplication/model/RechargeOrder.kt',
                'app/src/main/java/com/example/myapplication/model/PaymentMethod.kt',
                'app/src/main/java/com/example/myapplication/model/WalletBalance.kt'
            ]
            
            all_exist = True
            for model_file in model_files:
                try:
                    with open(model_file, 'r', encoding='utf-8') as f:
                        content = f.read()
                    self.log(f"   ✅ {model_file}: 存在")
                except FileNotFoundError:
                    self.log(f"   ❌ {model_file}: 不存在")
                    all_exist = False
                    
            self.test_results.append(("支付相关模型", all_exist))
            return all_exist
            
        except Exception as e:
            self.log(f"   ❌ 测试支付相关模型失败: {e}")
            self.test_results.append(("支付相关模型", False))
            return False
            
    def test_auth_manager(self):
        """测试AuthManager"""
        self.log("🔍 测试AuthManager...")
        
        try:
            with open('app/src/main/java/com/example/myapplication/auth/AuthManager.java', 'r', encoding='utf-8') as f:
                content = f.read()
            
            checks = [
                ("getUserId方法", "getUserId()" in content),
                ("getToken方法", "getToken()" in content),
                ("isLoggedIn方法", "isLoggedIn()" in content),
                ("SharedPreferences存储", "SharedPreferences" in content),
                ("JWT Token管理", "JWT" in content or "Token" in content)
            ]
            
            all_passed = True
            for check_name, passed in checks:
                if passed:
                    self.log(f"   ✅ {check_name}: 通过")
                else:
                    self.log(f"   ❌ {check_name}: 失败")
                    all_passed = False
                    
            self.test_results.append(("AuthManager", all_passed))
            return all_passed
            
        except Exception as e:
            self.log(f"   ❌ 测试AuthManager失败: {e}")
            self.test_results.append(("AuthManager", False))
            return False
            
    def test_backend_payment_services(self):
        """测试后端支付服务"""
        self.log("🔍 测试后端支付服务...")
        
        try:
            # 检查后端支付服务文件
            service_files = [
                'SocialMeet/src/main/java/com/example/socialmeet/service/RechargeService.java',
                'SocialMeet/src/main/java/com/example/socialmeet/service/AlipayService.java',
                'SocialMeet/src/main/java/com/example/socialmeet/service/WechatPayService.java',
                'SocialMeet/src/main/java/com/example/socialmeet/controller/RechargeController.java'
            ]
            
            all_exist = True
            for service_file in service_files:
                try:
                    with open(service_file, 'r', encoding='utf-8') as f:
                        content = f.read()
                    self.log(f"   ✅ {service_file}: 存在")
                except FileNotFoundError:
                    self.log(f"   ❌ {service_file}: 不存在")
                    all_exist = False
                    
            # 检查支付配置
            config_files = [
                'SocialMeet/src/main/java/com/example/socialmeet/config/PaymentConfig.java',
                'SocialMeet/src/main/resources/application-payment.yml'
            ]
            
            for config_file in config_files:
                try:
                    with open(config_file, 'r', encoding='utf-8') as f:
                        content = f.read()
                    self.log(f"   ✅ {config_file}: 存在")
                except FileNotFoundError:
                    self.log(f"   ❌ {config_file}: 不存在")
                    all_exist = False
                    
            self.test_results.append(("后端支付服务", all_exist))
            return all_exist
            
        except Exception as e:
            self.log(f"   ❌ 测试后端支付服务失败: {e}")
            self.test_results.append(("后端支付服务", False))
            return False
            
    def test_payment_flow_logic(self):
        """测试支付流程逻辑"""
        self.log("🔍 测试支付流程逻辑...")
        
        try:
            with open('app/src/main/java/com/example/myapplication/viewmodel/RechargeViewModel.kt', 'r', encoding='utf-8') as f:
                content = f.read()
            
            # 检查支付流程关键步骤
            flow_checks = [
                ("用户认证检查", "authManager.getUserId()" in content),
                ("订单创建", "createBackendOrder" in content),
                ("支付方式选择", "PaymentMethod.ALIPAY" in content and "PaymentMethod.WECHAT" in content),
                ("API调用", "HttpURLConnection" in content),
                ("错误处理", "catch" in content and "Exception" in content),
                ("状态更新", "_uiState.value" in content),
                ("支付结果处理", "handlePaymentResult" in content)
            ]
            
            all_passed = True
            for check_name, passed in flow_checks:
                if passed:
                    self.log(f"   ✅ {check_name}: 通过")
                else:
                    self.log(f"   ❌ {check_name}: 失败")
                    all_passed = False
                    
            self.test_results.append(("支付流程逻辑", all_passed))
            return all_passed
            
        except Exception as e:
            self.log(f"   ❌ 测试支付流程逻辑失败: {e}")
            self.test_results.append(("支付流程逻辑", False))
            return False
            
    def test_payment_configuration(self):
        """测试支付配置"""
        self.log("🔍 测试支付配置...")
        
        try:
            # 检查支付配置文件
            with open('SocialMeet/src/main/resources/application-payment.yml', 'r', encoding='utf-8') as f:
                config_content = f.read()
            
            config_checks = [
                ("支付宝配置", "alipay:" in config_content),
                ("微信支付配置", "wechat:" in config_content),
                ("回调URL配置", "notify-url" in config_content),
                ("返回URL配置", "return-url" in config_content),
                ("环境配置", "on-profile: dev" in config_content and "on-profile: prod" in config_content)
            ]
            
            all_passed = True
            for check_name, passed in config_checks:
                if passed:
                    self.log(f"   ✅ {check_name}: 通过")
                else:
                    self.log(f"   ❌ {check_name}: 失败")
                    all_passed = False
                    
            self.test_results.append(("支付配置", all_passed))
            return all_passed
            
        except Exception as e:
            self.log(f"   ❌ 测试支付配置失败: {e}")
            self.test_results.append(("支付配置", False))
            return False
            
    def generate_test_report(self):
        """生成测试报告"""
        self.log("📊 生成测试报告...")
        
        total_tests = len(self.test_results)
        passed_tests = sum(1 for _, passed in self.test_results if passed)
        failed_tests = total_tests - passed_tests
        
        self.log("=" * 60)
        self.log("📋 支付功能测试报告")
        self.log("=" * 60)
        
        for test_name, passed in self.test_results:
            status = "✅ 通过" if passed else "❌ 失败"
            self.log(f"{test_name}: {status}")
            
        self.log("-" * 60)
        self.log(f"总测试数: {total_tests}")
        self.log(f"通过数: {passed_tests}")
        self.log(f"失败数: {failed_tests}")
        self.log(f"通过率: {(passed_tests/total_tests)*100:.1f}%")
        
        if failed_tests == 0:
            self.log("🎉 所有测试通过！支付功能配置正确。")
        else:
            self.log("⚠️  部分测试失败，需要修复相关问题。")
            
        return failed_tests == 0
        
    def run_all_tests(self):
        """运行所有测试"""
        self.log("🚀 开始Android支付功能测试")
        self.log("=" * 60)
        
        # 运行各项测试
        self.test_recharge_viewmodel_structure()
        self.test_payment_models()
        self.test_auth_manager()
        self.test_backend_payment_services()
        self.test_payment_flow_logic()
        self.test_payment_configuration()
        
        # 生成测试报告
        return self.generate_test_report()

def main():
    """主函数"""
    tester = AndroidPaymentTester()
    
    try:
        success = tester.run_all_tests()
        if success:
            print("\n🎉 Android支付功能测试全部通过！")
            sys.exit(0)
        else:
            print("\n❌ Android支付功能测试部分失败！")
            sys.exit(1)
            
    except KeyboardInterrupt:
        print("\n⏹️  测试被用户中断")
        sys.exit(1)
    except Exception as e:
        print(f"\n💥 测试过程中发生异常: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
