#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
域名配置管理脚本
解决域名备案问题，提供多种部署方案
"""

import json
import os
import argparse
import requests
from datetime import datetime
from typing import Dict, List, Optional

class DomainManager:
    """域名配置管理器"""
    
    def __init__(self):
        self.config_file = "domain_config.json"
        self.server_configs = {
            "local": {
                "url": "http://localhost:8080",
                "status": "available",
                "description": "本地开发服务器",
                "recommended": True
            },
            "overseas": {
                "url": "https://your-overseas-server.com:8080",
                "status": "pending",
                "description": "境外服务器（无需备案）",
                "recommended": False
            },
            "tencent": {
                "url": "https://socialchatai.cloud:8080",
                "status": "blocked",
                "description": "腾讯云服务器（需要备案）",
                "recommended": False,
                "issue": "域名未备案，被腾讯云拦截"
            },
            "aliyun": {
                "url": "http://120.26.92.211:8080",
                "status": "pending",
                "description": "阿里云ECS服务器（推荐）",
                "recommended": True,
                "note": "已购买ECS 99套餐，IP: 120.26.92.211"
            }
        }
        
    def load_config(self) -> Dict:
        """加载配置文件"""
        if os.path.exists(self.config_file):
            with open(self.config_file, 'r', encoding='utf-8') as f:
                return json.load(f)
        return {"servers": self.server_configs, "current": "local"}
    
    def save_config(self, config: Dict):
        """保存配置文件"""
        with open(self.config_file, 'w', encoding='utf-8') as f:
            json.dump(config, f, ensure_ascii=False, indent=2)
    
    def test_server_connectivity(self, url: str) -> Dict:
        """测试服务器连接性"""
        try:
            response = requests.get(f"{url}/api/health", timeout=10)
            return {
                "status": "available" if response.status_code == 200 else "error",
                "status_code": response.status_code,
                "response_time": response.elapsed.total_seconds(),
                "last_check": datetime.now().isoformat()
            }
        except requests.exceptions.RequestException as e:
            return {
                "status": "unavailable",
                "error": str(e),
                "last_check": datetime.now().isoformat()
            }
    
    def update_server_status(self, server_name: str, url: str = None):
        """更新服务器状态"""
        config = self.load_config()
        
        if url:
            config["servers"][server_name]["url"] = url
        
        # 测试连接性
        test_url = config["servers"][server_name]["url"]
        connectivity = self.test_server_connectivity(test_url)
        
        config["servers"][server_name].update(connectivity)
        self.save_config(config)
        
        return connectivity
    
    def list_servers(self):
        """列出所有服务器配置"""
        config = self.load_config()
        
        print("=" * 60)
        print("服务器配置列表")
        print("=" * 60)
        
        for name, server in config["servers"].items():
            status_icon = {
                "available": "✅",
                "unavailable": "❌", 
                "blocked": "🚫",
                "pending": "⏳",
                "error": "⚠️"
            }.get(server.get("status", "unknown"), "❓")
            
            print(f"{status_icon} {name.upper()}")
            print(f"   URL: {server['url']}")
            print(f"   状态: {server.get('status', 'unknown')}")
            print(f"   描述: {server.get('description', 'N/A')}")
            
            if server.get('issue'):
                print(f"   问题: {server['issue']}")
            
            if server.get('response_time'):
                print(f"   响应时间: {server['response_time']:.2f}s")
            
            if server.get('last_check'):
                print(f"   最后检查: {server['last_check']}")
            
            print()
    
    def set_current_server(self, server_name: str):
        """设置当前使用的服务器"""
        config = self.load_config()
        
        if server_name not in config["servers"]:
            print(f"❌ 服务器配置 '{server_name}' 不存在")
            return False
        
        config["current"] = server_name
        self.save_config(config)
        
        current_url = config["servers"][server_name]["url"]
        print(f"✅ 已设置当前服务器为: {server_name} -> {current_url}")
        return True
    
    def get_current_server(self) -> Dict:
        """获取当前服务器配置"""
        config = self.load_config()
        current = config.get("current", "local")
        return config["servers"][current]
    
    def generate_deployment_guide(self):
        """生成部署指导文档"""
        guide = """
# SocialMeet 域名备案解决方案

## 🚨 当前问题
域名 `socialchatai.cloud:8080` 被腾讯云拦截，需要完成备案。

## 💡 解决方案

### 方案一：完成域名备案（推荐）

#### 1. 备案流程
1. 登录 [腾讯云备案控制台](https://console.cloud.tencent.com/beian)
2. 选择"新增备案"
3. 填写网站信息：
   - 网站名称：SocialMeet 社交交友应用
   - 网站域名：socialchatai.cloud
   - 网站用途：社交交友平台
4. 上传所需材料
5. 等待审核（通常3-20个工作日）

#### 2. 备案所需材料
**个人备案：**
- 身份证正反面照片
- 网站真实性核验单
- 域名证书

**企业备案：**
- 营业执照
- 法人身份证
- 网站真实性核验单
- 域名证书

### 方案二：使用境外服务器（临时方案）

#### 1. 推荐境外服务器提供商
- **AWS (Amazon Web Services)**
  - 区域：新加坡、日本、香港
  - 优势：稳定、速度快
  
- **Google Cloud Platform**
  - 区域：asia-southeast1 (新加坡)
  - 优势：价格便宜、性能好
  
- **DigitalOcean**
  - 区域：新加坡、日本
  - 优势：简单易用、价格透明

#### 2. 部署步骤
1. 购买境外服务器
2. 安装 Java 8+ 和 MySQL
3. 上传项目文件
4. 配置域名解析
5. 启动服务

### 方案三：使用CDN加速（混合方案）

1. 将静态资源部署到CDN
2. API服务使用境外服务器
3. 通过CDN加速访问

## 🔧 配置更新

### 更新测试脚本
```bash
# 使用境外服务器测试
python scripts/unified_test_suite.py --server overseas --verbose

# 使用本地服务器测试
python scripts/unified_test_suite.py --server local --verbose
```

### 更新应用配置
```bash
# 设置当前服务器
python scripts/domain_manager.py set-current overseas
```

## 📞 技术支持

如有问题，请查看：
- 腾讯云备案帮助文档
- 项目文档：docs/
- 配置管理：scripts/domain_manager.py

---
**最后更新**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
        """.strip()
        
        with open("DOMAIN_SOLUTION_GUIDE.md", "w", encoding="utf-8") as f:
            f.write(guide)
        
        print("✅ 部署指导文档已生成: DOMAIN_SOLUTION_GUIDE.md")

def main():
    """主函数"""
    parser = argparse.ArgumentParser(description="域名配置管理工具")
    parser.add_argument("action", choices=[
        "list", "test", "set-current", "get-current", "generate-guide"
    ], help="操作类型")
    parser.add_argument("--server", help="服务器名称")
    parser.add_argument("--url", help="服务器URL")
    
    args = parser.parse_args()
    
    manager = DomainManager()
    
    if args.action == "list":
        manager.list_servers()
    
    elif args.action == "test":
        if not args.server:
            print("❌ 请指定服务器名称: --server <name>")
            return
        
        print(f"🔍 测试服务器: {args.server}")
        config = manager.load_config()
        if args.server not in config["servers"]:
            print(f"❌ 服务器配置 '{args.server}' 不存在")
            return
        
        url = args.url or config["servers"][args.server]["url"]
        result = manager.update_server_status(args.server, args.url)
        
        if result["status"] == "available":
            print(f"✅ 服务器连接正常 (响应时间: {result['response_time']:.2f}s)")
        else:
            print(f"❌ 服务器连接失败: {result.get('error', 'Unknown error')}")
    
    elif args.action == "set-current":
        if not args.server:
            print("❌ 请指定服务器名称: --server <name>")
            return
        
        manager.set_current_server(args.server)
    
    elif args.action == "get-current":
        current = manager.get_current_server()
        print(f"当前服务器: {current['url']}")
        print(f"状态: {current.get('status', 'unknown')}")
    
    elif args.action == "generate-guide":
        manager.generate_deployment_guide()

if __name__ == "__main__":
    main()
