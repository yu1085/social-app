#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SocialMeet 统一配置管理
整合所有配置相关功能
"""

import json
import os
import yaml
from typing import Dict, Any, Optional
import argparse

class SocialMeetConfigManager:
    """SocialMeet 配置管理器"""
    
    def __init__(self, config_dir="."):
        self.config_dir = config_dir
        self.config_files = {
            "app": "app/build.gradle.kts",
            "backend": "SocialMeet/src/main/resources/application.yml",
            "database": "SocialMeet/src/main/resources/application.yml",
            "payment": "payment_config.json",
            "deploy": "deploy/config.yml"
        }
    
    def load_config(self, config_type: str) -> Dict[str, Any]:
        """加载配置文件"""
        config_file = self.config_files.get(config_type)
        if not config_file or not os.path.exists(config_file):
            print(f"配置文件不存在: {config_file}")
            return {}
        
        try:
            if config_file.endswith('.json'):
                with open(config_file, 'r', encoding='utf-8') as f:
                    return json.load(f)
            elif config_file.endswith('.yml') or config_file.endswith('.yaml'):
                with open(config_file, 'r', encoding='utf-8') as f:
                    return yaml.safe_load(f)
            else:
                print(f"不支持的配置文件格式: {config_file}")
                return {}
        except Exception as e:
            print(f"加载配置文件失败: {e}")
            return {}
    
    def save_config(self, config_type: str, config: Dict[str, Any]) -> bool:
        """保存配置文件"""
        config_file = self.config_files.get(config_type)
        if not config_file:
            print(f"未知的配置类型: {config_type}")
            return False
        
        try:
            os.makedirs(os.path.dirname(config_file), exist_ok=True)
            
            if config_file.endswith('.json'):
                with open(config_file, 'w', encoding='utf-8') as f:
                    json.dump(config, f, ensure_ascii=False, indent=2)
            elif config_file.endswith('.yml') or config_file.endswith('.yaml'):
                with open(config_file, 'w', encoding='utf-8') as f:
                    yaml.dump(config, f, default_flow_style=False, allow_unicode=True)
            else:
                print(f"不支持的配置文件格式: {config_file}")
                return False
            
            print(f"配置已保存到: {config_file}")
            return True
        except Exception as e:
            print(f"保存配置文件失败: {e}")
            return False
    
    def update_database_config(self, host: str, port: int, database: str, 
                              username: str, password: str) -> bool:
        """更新数据库配置"""
        config = self.load_config("backend")
        
        if not config:
            config = {}
        
        # 确保spring配置存在
        if "spring" not in config:
            config["spring"] = {}
        if "datasource" not in config["spring"]:
            config["spring"]["datasource"] = {}
        
        # 更新数据库配置
        config["spring"]["datasource"]["url"] = f"jdbc:mysql://{host}:{port}/{database}?useSSL=false&serverTimezone=UTC"
        config["spring"]["datasource"]["username"] = username
        config["spring"]["datasource"]["password"] = password
        config["spring"]["datasource"]["driver-class-name"] = "com.mysql.cj.jdbc.Driver"
        
        return self.save_config("backend", config)
    
    def update_payment_config(self, alipay_app_id: str, alipay_private_key: str,
                             alipay_public_key: str, wechat_app_id: str = None) -> bool:
        """更新支付配置"""
        config = {
            "alipay": {
                "app_id": alipay_app_id,
                "private_key": alipay_private_key,
                "public_key": alipay_public_key,
                "gateway_url": "https://openapi.alipay.com/gateway.do",
                "charset": "utf-8",
                "sign_type": "RSA2"
            }
        }
        
        if wechat_app_id:
            config["wechat"] = {
                "app_id": wechat_app_id,
                "mch_id": "your_mch_id",
                "api_key": "your_api_key"
            }
        
        return self.save_config("payment", config)
    
    def update_app_config(self, package_name: str, version_name: str, 
                          version_code: int) -> bool:
        """更新应用配置"""
        # 这里需要解析和修改Gradle文件，比较复杂
        # 简化版本，只提供配置模板
        config = {
            "package_name": package_name,
            "version_name": version_name,
            "version_code": version_code,
            "min_sdk": 21,
            "target_sdk": 34,
            "compile_sdk": 34
        }
        
        return self.save_config("app", config)
    
    def generate_env_file(self, env_type: str = "development") -> bool:
        """生成环境配置文件"""
        env_configs = {
            "development": {
                "SPRING_PROFILES_ACTIVE": "dev",
                "SERVER_PORT": "8080",
                "DATABASE_URL": "jdbc:mysql://localhost:3306/socialmeet_dev",
                "DATABASE_USERNAME": "root",
                "DATABASE_PASSWORD": "password",
                "JWT_SECRET": "your-jwt-secret-key",
                "JWT_EXPIRATION": "86400000"
            },
            "production": {
                "SPRING_PROFILES_ACTIVE": "prod",
                "SERVER_PORT": "8080",
                "DATABASE_URL": "jdbc:mysql://your-db-host:3306/socialmeet_prod",
                "DATABASE_USERNAME": "your-db-user",
                "DATABASE_PASSWORD": "your-db-password",
                "JWT_SECRET": "your-production-jwt-secret",
                "JWT_EXPIRATION": "86400000"
            }
        }
        
        config = env_configs.get(env_type, env_configs["development"])
        
        env_file = f".env.{env_type}"
        try:
            with open(env_file, 'w', encoding='utf-8') as f:
                for key, value in config.items():
                    f.write(f"{key}={value}\n")
            
            print(f"环境配置文件已生成: {env_file}")
            return True
        except Exception as e:
            print(f"生成环境配置文件失败: {e}")
            return False
    
    def validate_config(self, config_type: str) -> bool:
        """验证配置文件"""
        config = self.load_config(config_type)
        
        if not config:
            print(f"❌ 配置文件无效: {config_type}")
            return False
        
        # 根据配置类型进行验证
        if config_type == "backend":
            required_keys = ["spring"]
            if not all(key in config for key in required_keys):
                print("❌ 后端配置缺少必要字段")
                return False
        
        elif config_type == "payment":
            required_keys = ["alipay"]
            if not all(key in config for key in required_keys):
                print("❌ 支付配置缺少必要字段")
                return False
        
        print(f"✅ 配置文件验证通过: {config_type}")
        return True
    
    def backup_config(self, config_type: str) -> bool:
        """备份配置文件"""
        config_file = self.config_files.get(config_type)
        if not config_file or not os.path.exists(config_file):
            print(f"配置文件不存在: {config_file}")
            return False
        
        backup_file = f"{config_file}.backup"
        try:
            import shutil
            shutil.copy2(config_file, backup_file)
            print(f"配置文件已备份到: {backup_file}")
            return True
        except Exception as e:
            print(f"备份配置文件失败: {e}")
            return False
    
    def restore_config(self, config_type: str) -> bool:
        """恢复配置文件"""
        config_file = self.config_files.get(config_type)
        backup_file = f"{config_file}.backup"
        
        if not os.path.exists(backup_file):
            print(f"备份文件不存在: {backup_file}")
            return False
        
        try:
            import shutil
            shutil.copy2(backup_file, config_file)
            print(f"配置文件已从备份恢复: {backup_file}")
            return True
        except Exception as e:
            print(f"恢复配置文件失败: {e}")
            return False

def main():
    """主函数"""
    parser = argparse.ArgumentParser(description="SocialMeet 统一配置管理")
    parser.add_argument("--config-type", choices=["app", "backend", "database", "payment", "deploy"],
                       help="配置类型")
    parser.add_argument("--action", choices=["load", "save", "validate", "backup", "restore"],
                       help="操作类型")
    parser.add_argument("--file", help="配置文件路径")
    
    # 数据库配置参数
    parser.add_argument("--db-host", help="数据库主机")
    parser.add_argument("--db-port", type=int, help="数据库端口")
    parser.add_argument("--db-name", help="数据库名称")
    parser.add_argument("--db-user", help="数据库用户名")
    parser.add_argument("--db-password", help="数据库密码")
    
    # 支付配置参数
    parser.add_argument("--alipay-app-id", help="支付宝应用ID")
    parser.add_argument("--alipay-private-key", help="支付宝私钥")
    parser.add_argument("--alipay-public-key", help="支付宝公钥")
    
    # 环境配置
    parser.add_argument("--env", choices=["development", "production"], 
                       default="development", help="环境类型")
    parser.add_argument("--generate-env", action="store_true", help="生成环境配置文件")
    
    args = parser.parse_args()
    
    manager = SocialMeetConfigManager()
    
    if args.generate_env:
        manager.generate_env_file(args.env)
        return
    
    if not args.config_type or not args.action:
        print("请指定配置类型和操作")
        print("使用 --help 查看帮助信息")
        return
    
    if args.action == "load":
        config = manager.load_config(args.config_type)
        print(json.dumps(config, ensure_ascii=False, indent=2))
    
    elif args.action == "validate":
        manager.validate_config(args.config_type)
    
    elif args.action == "backup":
        manager.backup_config(args.config_type)
    
    elif args.action == "restore":
        manager.restore_config(args.config_type)
    
    elif args.action == "save":
        if args.config_type == "database":
            if not all([args.db_host, args.db_port, args.db_name, args.db_user, args.db_password]):
                print("数据库配置需要所有参数: --db-host, --db-port, --db-name, --db-user, --db-password")
                return
            manager.update_database_config(args.db_host, args.db_port, args.db_name, 
                                          args.db_user, args.db_password)
        
        elif args.config_type == "payment":
            if not all([args.alipay_app_id, args.alipay_private_key, args.alipay_public_key]):
                print("支付配置需要: --alipay-app-id, --alipay-private-key, --alipay-public-key")
                return
            manager.update_payment_config(args.alipay_app_id, args.alipay_private_key, 
                                        args.alipay_public_key)

if __name__ == "__main__":
    main()
