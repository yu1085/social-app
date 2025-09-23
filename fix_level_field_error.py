#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复数据库 level 字段默认值问题
解决 Field 'level' doesn't have a default value 错误
"""

import pymysql
import sys
import time

def connect_to_database():
    """连接到数据库"""
    try:
        connection = pymysql.connect(
            host='localhost',
            user='root',
            password='123456',
            database='socialmeet',
            charset='utf8mb4',
            autocommit=True
        )
        print("✅ 数据库连接成功")
        return connection
    except Exception as e:
        print(f"❌ 数据库连接失败: {e}")
        return None

def fix_level_field_issues(connection):
    """修复 level 字段相关问题"""
    cursor = connection.cursor()
    
    try:
        print("\n🔍 检查数据库表结构...")
        
        # 1. 检查 vip_levels 表是否存在
        cursor.execute("SHOW TABLES LIKE 'vip_levels'")
        if not cursor.fetchone():
            print("❌ vip_levels 表不存在，创建表...")
            create_vip_levels_table(cursor)
        else:
            print("✅ vip_levels 表已存在")
        
        # 2. 检查 wealth_levels 表结构
        cursor.execute("DESCRIBE wealth_levels")
        wealth_levels_columns = cursor.fetchall()
        print(f"✅ wealth_levels 表有 {len(wealth_levels_columns)} 个字段")
        
        # 3. 检查是否有 level 字段
        level_fields = [col for col in wealth_levels_columns if 'level' in col[0].lower()]
        if level_fields:
            print(f"⚠️  发现 level 相关字段: {[col[0] for col in level_fields]}")
        
        # 4. 检查 user_growth 表结构
        cursor.execute("DESCRIBE user_growth")
        user_growth_columns = cursor.fetchall()
        print(f"✅ user_growth 表有 {len(user_growth_columns)} 个字段")
        
        # 5. 检查是否有 level 字段
        level_fields = [col for col in user_growth_columns if 'level' in col[0].lower()]
        if level_fields:
            print(f"⚠️  发现 level 相关字段: {[col[0] for col in level_fields]}")
        
        # 6. 修复可能的 level 字段默认值问题
        fix_level_defaults(cursor)
        
        print("\n✅ 数据库修复完成")
        
    except Exception as e:
        print(f"❌ 修复过程中出错: {e}")
        return False
    finally:
        cursor.close()
    
    return True

def create_vip_levels_table(cursor):
    """创建 vip_levels 表"""
    try:
        create_table_sql = """
        CREATE TABLE IF NOT EXISTS vip_levels (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(50) NOT NULL,
            level INT NOT NULL UNIQUE DEFAULT 0,
            price DECIMAL(10,2) NOT NULL,
            duration INT NOT NULL,
            benefits TEXT,
            is_active BOOLEAN DEFAULT TRUE,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            
            INDEX idx_level (level),
            INDEX idx_is_active (is_active)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
        """
        
        cursor.execute(create_table_sql)
        print("✅ vip_levels 表创建成功")
        
        # 插入默认数据
        insert_data_sql = """
        INSERT INTO vip_levels (name, level, price, duration, benefits, is_active) VALUES
        ('普通会员', 0, 0.00, 0, '基础功能', TRUE),
        ('VIP会员', 1, 29.90, 30, 'VIP专享、优先客服、专属内容、折扣特权', TRUE),
        ('SVIP会员', 2, 59.90, 30, 'SVIP专享、优先客服、专属内容、更高折扣、提前体验', TRUE),
        ('钻石会员', 3, 99.90, 30, '钻石专享、优先客服、专属内容、最高折扣、提前体验、自定义头像', TRUE),
        ('至尊会员', 4, 199.90, 30, '至尊专享、优先客服、专属内容、最高折扣、提前体验、自定义头像、无限消息、高级筛选', TRUE)
        ON DUPLICATE KEY UPDATE 
            name = VALUES(name),
            price = VALUES(price),
            duration = VALUES(duration),
            benefits = VALUES(benefits),
            is_active = VALUES(is_active);
        """
        
        cursor.execute(insert_data_sql)
        print("✅ VIP等级数据插入成功")
        
    except Exception as e:
        print(f"❌ 创建 vip_levels 表失败: {e}")

def fix_level_defaults(cursor):
    """修复 level 字段的默认值问题"""
    try:
        # 检查并修复可能的 level 字段
        tables_to_check = ['wealth_levels', 'user_growth', 'vip_levels']
        
        for table in tables_to_check:
            try:
                # 检查表是否存在
                cursor.execute(f"SHOW TABLES LIKE '{table}'")
                if not cursor.fetchone():
                    print(f"⚠️  表 {table} 不存在，跳过")
                    continue
                
                # 检查表结构
                cursor.execute(f"DESCRIBE {table}")
                columns = cursor.fetchall()
                
                # 查找 level 相关字段
                level_columns = [col for col in columns if 'level' in col[0].lower()]
                
                if level_columns:
                    print(f"🔧 修复表 {table} 的 level 字段...")
                    for col in level_columns:
                        col_name = col[0]
                        col_type = col[1]
                        is_nullable = col[2]
                        default_value = col[4]
                        
                        print(f"   字段: {col_name}, 类型: {col_type}, 可空: {is_nullable}, 默认值: {default_value}")
                        
                        # 如果字段没有默认值且不允许NULL，添加默认值
                        if is_nullable == 'NO' and default_value is None:
                            if 'int' in col_type.lower():
                                alter_sql = f"ALTER TABLE {table} ALTER COLUMN {col_name} SET DEFAULT 0"
                                cursor.execute(alter_sql)
                                print(f"   ✅ 为 {col_name} 添加默认值 0")
                            elif 'varchar' in col_type.lower():
                                alter_sql = f"ALTER TABLE {table} ALTER COLUMN {col_name} SET DEFAULT ''"
                                cursor.execute(alter_sql)
                                print(f"   ✅ 为 {col_name} 添加默认值 ''")
                
            except Exception as e:
                print(f"⚠️  处理表 {table} 时出错: {e}")
                continue
        
        print("✅ level 字段默认值修复完成")
        
    except Exception as e:
        print(f"❌ 修复 level 字段默认值时出错: {e}")

def test_wealth_level_api():
    """测试财富等级API"""
    import requests
    
    try:
        print("\n🧪 测试财富等级API...")
        
        # 测试等级规则API
        response = requests.get("http://localhost:8080/api/wealth-level/rules", timeout=10)
        if response.status_code == 200:
            data = response.json()
            print(f"✅ 等级规则API正常，返回 {len(data.get('data', []))} 条记录")
        else:
            print(f"❌ 等级规则API失败: {response.status_code}")
        
        # 测试健康检查API
        response = requests.get("http://localhost:8080/actuator/health", timeout=10)
        if response.status_code == 200:
            print("✅ 健康检查API正常")
        else:
            print(f"❌ 健康检查API失败: {response.status_code}")
            
    except Exception as e:
        print(f"❌ API测试失败: {e}")

def main():
    """主函数"""
    print("🚀 开始修复数据库 level 字段问题...")
    
    # 连接数据库
    connection = connect_to_database()
    if not connection:
        print("❌ 无法连接到数据库，退出")
        sys.exit(1)
    
    try:
        # 修复数据库问题
        if fix_level_field_issues(connection):
            print("\n🎉 数据库修复成功！")
            
            # 等待服务启动
            print("\n⏳ 等待后端服务启动...")
            time.sleep(5)
            
            # 测试API
            test_wealth_level_api()
            
            print("\n✅ 修复完成！财富等级页面现在应该可以正常显示了。")
        else:
            print("❌ 数据库修复失败")
            
    except Exception as e:
        print(f"❌ 修复过程中出现错误: {e}")
    finally:
        connection.close()
        print("🔒 数据库连接已关闭")

if __name__ == "__main__":
    main()
