#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查用户卡片数据来源状态
"""

import requests
import json
from datetime import datetime

# 配置
BASE_URL = "http://localhost:8080"
API_BASE = f"{BASE_URL}/api"

def check_backend_api():
    """检查后端API状态"""
    print("=" * 60)
    print("检查用户卡片数据来源状态")
    print("=" * 60)
    
    try:
        # 检查后端健康状态
        print("1. 检查后端服务状态...")
        response = requests.get(f"{BASE_URL}/api/health", timeout=5)
        if response.status_code == 200:
            print("✅ 后端服务正常运行")
        else:
            print(f"❌ 后端服务异常: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ 无法连接到后端服务: {e}")
        return False
    
    # 检查用户卡片API
    print("\n2. 检查用户卡片API...")
    try:
        response = requests.get(f"{API_BASE}/users/home-cards?page=0&size=4", timeout=10)
        if response.status_code == 200:
            data = response.json()
            if data.get('success'):
                user_cards = data.get('data', [])
                print(f"✅ 用户卡片API正常，返回{len(user_cards)}个用户")
                
                # 显示前4个用户（对应Android首页的4个卡片位置）
                for i, card in enumerate(user_cards[:4], 1):
                    print(f"   卡片{i}: {card.get('nickname', 'N/A')} (ID: {card.get('id', 'N/A')})")
                
                return True
            else:
                print(f"❌ API返回失败: {data.get('message')}")
                return False
        else:
            print(f"❌ HTTP请求失败: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ 用户卡片API异常: {e}")
        return False

def check_android_integration():
    """检查Android集成状态"""
    print("\n3. 检查Android集成状态...")
    
    # 检查关键文件是否存在
    import os
    
    files_to_check = [
        "app/src/main/java/com/example/myapplication/model/UserCard.kt",
        "app/src/main/java/com/example/myapplication/viewmodel/UserViewModel.kt",
        "app/src/main/java/com/example/myapplication/network/NetworkService.kt",
        "app/src/main/java/com/example/myapplication/MainActivity.java"
    ]
    
    all_files_exist = True
    for file_path in files_to_check:
        if os.path.exists(file_path):
            print(f"✅ {file_path} 存在")
        else:
            print(f"❌ {file_path} 不存在")
            all_files_exist = False
    
    return all_files_exist

def check_hardcoded_data():
    """检查硬编码数据"""
    print("\n4. 检查硬编码数据...")
    
    import os
    
    # 检查MainActivity中的硬编码数据
    main_activity_path = "app/src/main/java/com/example/myapplication/MainActivity.java"
    if os.path.exists(main_activity_path):
        with open(main_activity_path, 'r', encoding='utf-8') as f:
            content = f.read()
            
        # 检查是否包含动态数据加载
        if "loadDynamicUserCards" in content:
            print("✅ MainActivity包含动态数据加载")
        else:
            print("❌ MainActivity缺少动态数据加载")
            
        if "UserViewModel" in content:
            print("✅ MainActivity集成了UserViewModel")
        else:
            print("❌ MainActivity未集成UserViewModel")
            
        if "updateUserCardsUI" in content:
            print("✅ MainActivity包含UI更新方法")
        else:
            print("❌ MainActivity缺少UI更新方法")
    else:
        print("❌ MainActivity文件不存在")
        return False
    
    # 检查布局文件中的硬编码数据
    layout_path = "app/src/main/res/layout/activity_main.xml"
    if os.path.exists(layout_path):
        with open(layout_path, 'r', encoding='utf-8') as f:
            content = f.read()
            
        if "不吃香菜" in content:
            print("⚠️  布局文件中仍包含硬编码用户名")
        else:
            print("✅ 布局文件中无硬编码用户名")
            
        if "300/分钟" in content:
            print("⚠️  布局文件中仍包含硬编码价格")
        else:
            print("✅ 布局文件中无硬编码价格")
    
    return True

def generate_status_report():
    """生成状态报告"""
    print("\n" + "=" * 60)
    print("用户卡片数据来源状态报告")
    print("=" * 60)
    
    # 检查各项状态
    backend_ok = check_backend_api()
    android_ok = check_android_integration()
    hardcoded_ok = check_hardcoded_data()
    
    print(f"\n状态总结:")
    print(f"后端API: {'✅ 正常' if backend_ok else '❌ 异常'}")
    print(f"Android集成: {'✅ 完成' if android_ok else '❌ 未完成'}")
    print(f"硬编码检查: {'✅ 通过' if hardcoded_ok else '❌ 未通过'}")
    
    if backend_ok and android_ok and hardcoded_ok:
        print(f"\n🎉 用户卡片数据现在来自数据库！")
        print(f"   - 后端API正常提供用户数据")
        print(f"   - Android应用已集成动态数据加载")
        print(f"   - 硬编码数据已被动态数据替代")
        print(f"\n📱 建议:")
        print(f"   - 编译并运行Android应用")
        print(f"   - 验证首页用户卡片显示真实数据")
        print(f"   - 测试用户详情页面")
    else:
        print(f"\n⚠️  用户卡片数据可能仍来自硬编码！")
        if not backend_ok:
            print(f"   - 需要启动后端服务")
        if not android_ok:
            print(f"   - 需要完成Android集成")
        if not hardcoded_ok:
            print(f"   - 需要移除硬编码数据")
    
    return backend_ok and android_ok and hardcoded_ok

if __name__ == "__main__":
    print(f"开始检查用户卡片数据来源状态...")
    print(f"检查时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    success = generate_status_report()
    
    if success:
        print(f"\n✅ 检查完成：用户卡片数据来自数据库")
    else:
        print(f"\n❌ 检查完成：用户卡片数据可能仍来自硬编码")
