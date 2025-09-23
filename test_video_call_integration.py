#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
视频通话功能联调测试脚本
测试Android端与后端API的集成
"""

import requests
import json
import time
from typing import Dict, Any

class VideoCallIntegrationTest:
    def __init__(self, base_url: str = "http://192.168.1.100:8080"):
        self.base_url = base_url
        self.session = requests.Session()
        self.test_token = None
        
    def test_api_connectivity(self) -> bool:
        """测试API连通性"""
        print("🔍 测试API连通性...")
        try:
            response = self.session.get(f"{self.base_url}/api/health", timeout=10)
            if response.status_code == 200:
                print("✅ API连通性正常")
                return True
            else:
                print(f"❌ API连通性异常: {response.status_code}")
                return False
        except Exception as e:
            print(f"❌ API连通性异常: {e}")
            return False
    
    def test_user_login(self) -> bool:
        """测试用户登录获取Token"""
        print("\n🔐 测试用户登录...")
        try:
            # 使用测试用户登录
            login_data = {
                "username": "testuser",
                "password": "123456"
            }
            
            response = self.session.post(
                f"{self.base_url}/api/auth/login",
                json=login_data,
                headers={"Content-Type": "application/json"}
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    self.test_token = result["data"]["token"]
                    print(f"✅ 用户登录成功，Token: {self.test_token[:20]}...")
                    return True
                else:
                    print(f"❌ 用户登录失败: {result.get('message')}")
                    return False
            else:
                print(f"❌ 用户登录失败: {response.status_code}")
                return False
        except Exception as e:
            print(f"❌ 用户登录异常: {e}")
            return False
    
    def test_get_call_prices(self) -> bool:
        """测试获取通话价格信息"""
        print("\n💰 测试获取通话价格信息...")
        if not self.test_token:
            print("❌ 未获取到Token，跳过测试")
            return False
            
        try:
            headers = {
                "Authorization": f"Bearer {self.test_token}",
                "Content-Type": "application/json"
            }
            
            response = self.session.get(
                f"{self.base_url}/api/call-settings",
                headers=headers
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    data = result["data"]
                    print("✅ 获取通话价格成功:")
                    print(f"   📹 视频通话价格: {data.get('videoCallPrice')}元/分钟")
                    print(f"   🎤 语音通话价格: {data.get('voiceCallPrice')}元/分钟")
                    print(f"   💬 消息价格: {data.get('messagePrice')}元/条")
                    print(f"   📹 视频通话开启: {data.get('videoCallEnabled')}")
                    print(f"   🎤 语音通话开启: {data.get('voiceCallEnabled')}")
                    return True
                else:
                    print(f"❌ 获取通话价格失败: {result.get('message')}")
                    return False
            else:
                print(f"❌ 获取通话价格失败: {response.status_code}")
                return False
        except Exception as e:
            print(f"❌ 获取通话价格异常: {e}")
            return False
    
    def test_initiate_video_call(self) -> bool:
        """测试发起视频通话"""
        print("\n📹 测试发起视频通话...")
        if not self.test_token:
            print("❌ 未获取到Token，跳过测试")
            return False
            
        try:
            headers = {
                "Authorization": f"Bearer {self.test_token}",
                "Content-Type": "application/json"
            }
            
            # 发起视频通话（目标用户ID为2）
            call_data = {
                "receiverId": 2,
                "callType": "VIDEO"
            }
            
            response = self.session.post(
                f"{self.base_url}/api/call/initiate",
                json=call_data,
                headers=headers
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    data = result["data"]
                    print("✅ 发起视频通话成功:")
                    print(f"   📞 通话会话ID: {data.get('callSessionId')}")
                    print(f"   👤 发起方ID: {data.get('callerId')}")
                    print(f"   👤 接收方ID: {data.get('receiverId')}")
                    print(f"   📹 通话类型: {data.get('callType')}")
                    print(f"   📊 通话状态: {data.get('status')}")
                    return True
                else:
                    print(f"❌ 发起视频通话失败: {result.get('message')}")
                    return False
            else:
                print(f"❌ 发起视频通话失败: {response.status_code}")
                print(f"   响应内容: {response.text}")
                return False
        except Exception as e:
            print(f"❌ 发起视频通话异常: {e}")
            return False
    
    def test_initiate_voice_call(self) -> bool:
        """测试发起语音通话"""
        print("\n🎤 测试发起语音通话...")
        if not self.test_token:
            print("❌ 未获取到Token，跳过测试")
            return False
            
        try:
            headers = {
                "Authorization": f"Bearer {self.test_token}",
                "Content-Type": "application/json"
            }
            
            # 发起语音通话（目标用户ID为2）
            call_data = {
                "receiverId": 2,
                "callType": "VOICE"
            }
            
            response = self.session.post(
                f"{self.base_url}/api/call/initiate",
                json=call_data,
                headers=headers
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    data = result["data"]
                    print("✅ 发起语音通话成功:")
                    print(f"   📞 通话会话ID: {data.get('callSessionId')}")
                    print(f"   👤 发起方ID: {data.get('callerId')}")
                    print(f"   👤 接收方ID: {data.get('receiverId')}")
                    print(f"   🎤 通话类型: {data.get('callType')}")
                    print(f"   📊 通话状态: {data.get('status')}")
                    return True
                else:
                    print(f"❌ 发起语音通话失败: {result.get('message')}")
                    return False
            else:
                print(f"❌ 发起语音通话失败: {response.status_code}")
                print(f"   响应内容: {response.text}")
                return False
        except Exception as e:
            print(f"❌ 发起语音通话异常: {e}")
            return False
    
    def test_call_rate_info(self) -> bool:
        """测试获取通话费率信息"""
        print("\n📊 测试获取通话费率信息...")
        if not self.test_token:
            print("❌ 未获取到Token，跳过测试")
            return False
            
        try:
            headers = {
                "Authorization": f"Bearer {self.test_token}",
                "Content-Type": "application/json"
            }
            
            response = self.session.get(
                f"{self.base_url}/api/call/rate-info",
                headers=headers
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    data = result["data"]
                    print("✅ 获取通话费率信息成功:")
                    print(f"   费率信息: {json.dumps(data, indent=2, ensure_ascii=False)}")
                    return True
                else:
                    print(f"❌ 获取通话费率信息失败: {result.get('message')}")
                    return False
            else:
                print(f"❌ 获取通话费率信息失败: {response.status_code}")
                return False
        except Exception as e:
            print(f"❌ 获取通话费率信息异常: {e}")
            return False
    
    def test_voice_call_price_display(self) -> bool:
        """测试语音通话价格显示"""
        print("\n🎤 测试语音通话价格显示...")
        if not self.test_token:
            print("❌ 未获取到Token，跳过测试")
            return False
            
        try:
            headers = {
                "Authorization": f"Bearer {self.test_token}",
                "Content-Type": "application/json"
            }
            
            response = self.session.get(
                f"{self.base_url}/api/call-settings",
                headers=headers
            )
            
            if response.status_code == 200:
                result = response.json()
                if result.get("success"):
                    data = result["data"]
                    voice_price = data.get("voiceCallPrice")
                    voice_enabled = data.get("voiceCallEnabled")
                    
                    print("✅ 语音通话价格显示测试成功:")
                    print(f"   🎤 语音通话价格: {voice_price}元/分钟")
                    print(f"   🎤 语音通话开启: {voice_enabled}")
                    
                    # 验证价格合理性
                    if voice_price and voice_price > 0:
                        print("   ✅ 价格设置合理")
                        return True
                    else:
                        print("   ❌ 价格设置异常")
                        return False
                else:
                    print(f"❌ 获取语音通话价格失败: {result.get('message')}")
                    return False
            else:
                print(f"❌ 获取语音通话价格失败: {response.status_code}")
                return False
        except Exception as e:
            print(f"❌ 语音通话价格显示测试异常: {e}")
            return False
    
    def run_all_tests(self) -> Dict[str, bool]:
        """运行所有测试"""
        print("🚀 开始视频通话功能联调测试")
        print("=" * 50)
        
        results = {}
        
        # 1. 测试API连通性
        results["api_connectivity"] = self.test_api_connectivity()
        
        # 2. 测试用户登录
        results["user_login"] = self.test_user_login()
        
        # 3. 测试获取通话价格
        results["get_call_prices"] = self.test_get_call_prices()
        
        # 4. 测试获取通话费率信息
        results["get_call_rate_info"] = self.test_call_rate_info()
        
        # 5. 测试发起视频通话
        results["initiate_video_call"] = self.test_initiate_video_call()
        
        # 6. 测试发起语音通话
        results["initiate_voice_call"] = self.test_initiate_voice_call()
        
        # 7. 测试语音通话价格显示
        results["voice_call_price_display"] = self.test_voice_call_price_display()
        
        # 输出测试结果汇总
        print("\n" + "=" * 50)
        print("📋 测试结果汇总:")
        print("=" * 50)
        
        total_tests = len(results)
        passed_tests = sum(1 for result in results.values() if result)
        
        for test_name, result in results.items():
            status = "✅ 通过" if result else "❌ 失败"
            print(f"   {test_name}: {status}")
        
        print(f"\n📊 总体结果: {passed_tests}/{total_tests} 测试通过")
        
        if passed_tests == total_tests:
            print("🎉 所有测试通过！视频通话功能联调成功！")
        else:
            print("⚠️  部分测试失败，请检查后端服务状态")
        
        return results

def main():
    """主函数"""
    print("🎯 视频通话功能联调测试工具")
    print("=" * 50)
    
    # 创建测试实例
    tester = VideoCallIntegrationTest()
    
    # 运行所有测试
    results = tester.run_all_tests()
    
    # 返回测试结果
    return results

if __name__ == "__main__":
    main()
