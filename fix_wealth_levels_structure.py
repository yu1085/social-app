#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import sys

def connect_to_database():
    """连接数据库"""
    try:
        connection = pymysql.connect(
            host='localhost',
            user='root',
            password='123456',
            database='socialmeet',
            charset='utf8mb4'
        )
        print("✅ 数据库连接成功")
        return connection
    except Exception as e:
        print(f"❌ 数据库连接失败: {e}")
        return None

def create_wealth_level_rules_table(cursor):
    """创建财富等级规则表"""
    try:
        print("\n🔧 创建 wealth_level_rules 表...")
        
        # 删除已存在的表
        cursor.execute("DROP TABLE IF EXISTS wealth_level_rules")
        
        # 创建财富等级规则表
        create_sql = """
        CREATE TABLE wealth_level_rules (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            level_name VARCHAR(50) NOT NULL,
            level_icon VARCHAR(10),
            level_color VARCHAR(20),
            min_wealth_value INT NOT NULL,
            max_wealth_value INT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            
            INDEX idx_min_wealth_value (min_wealth_value),
            INDEX idx_level_name (level_name)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """
        
        cursor.execute(create_sql)
        print("✅ wealth_level_rules 表创建成功")
        
        # 插入默认等级规则数据
        insert_sql = """
        INSERT INTO wealth_level_rules (level_name, level_icon, level_color, min_wealth_value, max_wealth_value) VALUES
        ('黑钻', '💎', '#000000', 1000000, NULL),
        ('金钻', '💎', '#FFD700', 700000, 999999),
        ('红钻', '💎', '#FF69B4', 500000, 699999),
        ('橙钻', '💎', '#FF8C00', 300000, 499999),
        ('紫钻', '💎', '#8A2BE2', 100000, 299999),
        ('蓝钻', '💎', '#1E90FF', 50000, 99999),
        ('青钻', '💎', '#00CED1', 30000, 49999),
        ('铂金', '💎', '#C0C0C0', 10000, 29999),
        ('黄金', '💎', '#FFD700', 5000, 9999),
        ('白银', '💎', '#C0C0C0', 2000, 4999),
        ('青铜', '💎', '#CD7F32', 1000, 1999),
        ('普通', '⭐', '#808080', 0, 999)
        """
        
        cursor.execute(insert_sql)
        print("✅ 默认等级规则数据插入成功")
        
        return True
        
    except Exception as e:
        print(f"❌ 创建 wealth_level_rules 表失败: {e}")
        return False

def fix_wealth_levels_table(cursor):
    """修复 wealth_levels 表结构"""
    try:
        print("\n🔧 修复 wealth_levels 表结构...")
        
        # 备份现有数据
        print("📋 备份现有数据...")
        cursor.execute("CREATE TABLE wealth_levels_backup AS SELECT * FROM wealth_levels")
        
        # 删除现有表
        cursor.execute("DROP TABLE wealth_levels")
        
        # 重新创建正确的 wealth_levels 表
        create_sql = """
        CREATE TABLE wealth_levels (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            user_id BIGINT NOT NULL,
            wealth_value INT NOT NULL DEFAULT 0,
            level_name VARCHAR(50) NOT NULL,
            level_icon VARCHAR(10),
            level_color VARCHAR(20),
            min_wealth_value INT NOT NULL,
            max_wealth_value INT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            
            UNIQUE KEY uk_user_id (user_id),
            INDEX idx_wealth_value (wealth_value),
            INDEX idx_level_name (level_name),
            INDEX idx_created_at (created_at)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
        """
        
        cursor.execute(create_sql)
        print("✅ wealth_levels 表重新创建成功")
        
        return True
        
    except Exception as e:
        print(f"❌ 修复 wealth_levels 表失败: {e}")
        return False

def migrate_user_data(cursor):
    """迁移用户数据"""
    try:
        print("\n🔄 迁移用户数据...")
        
        # 从备份表中查找真实的用户数据（user_id > 0）
        cursor.execute("SELECT COUNT(*) FROM wealth_levels_backup WHERE user_id > 0")
        user_count = cursor.fetchone()[0]
        print(f"发现 {user_count} 条用户数据需要迁移")
        
        if user_count > 0:
            # 迁移用户数据
            migrate_sql = """
            INSERT INTO wealth_levels (user_id, wealth_value, level_name, level_icon, level_color, min_wealth_value, max_wealth_value, created_at, updated_at)
            SELECT 
                user_id,
                wealth_value,
                level_name,
                level_icon,
                level_color,
                min_wealth_value,
                max_wealth_value,
                created_at,
                updated_at
            FROM wealth_levels_backup 
            WHERE user_id > 0
            """
            
            cursor.execute(migrate_sql)
            print(f"✅ 成功迁移 {user_count} 条用户数据")
        else:
            print("ℹ️  没有用户数据需要迁移")
        
        return True
        
    except Exception as e:
        print(f"❌ 迁移用户数据失败: {e}")
        return False

def cleanup_backup_table(cursor):
    """清理备份表"""
    try:
        print("\n🧹 清理备份表...")
        cursor.execute("DROP TABLE wealth_levels_backup")
        print("✅ 备份表清理完成")
        return True
    except Exception as e:
        print(f"❌ 清理备份表失败: {e}")
        return False

def main():
    """主函数"""
    print("🚀 开始修复 wealth_levels 表结构...")
    
    connection = connect_to_database()
    if connection is None:
        return
    
    try:
        cursor = connection.cursor()
        
        # 1. 创建财富等级规则表
        if not create_wealth_level_rules_table(cursor):
            return
        
        # 2. 修复 wealth_levels 表结构
        if not fix_wealth_levels_table(cursor):
            return
        
        # 3. 迁移用户数据
        if not migrate_user_data(cursor):
            return
        
        # 4. 清理备份表
        cleanup_backup_table(cursor)
        
        # 提交事务
        connection.commit()
        print("\n🎉 wealth_levels 表结构修复完成！")
        
        # 验证修复结果
        print("\n🔍 验证修复结果...")
        cursor.execute("SELECT COUNT(*) FROM wealth_levels")
        user_count = cursor.fetchone()[0]
        print(f"wealth_levels 表中用户记录数: {user_count}")
        
        cursor.execute("SELECT COUNT(*) FROM wealth_level_rules")
        rule_count = cursor.fetchone()[0]
        print(f"wealth_level_rules 表中规则记录数: {rule_count}")
        
    except Exception as e:
        print(f"❌ 修复过程中出错: {e}")
        connection.rollback()
    finally:
        cursor.close()
        connection.close()
        print("\n🔒 数据库连接已关闭")

if __name__ == "__main__":
    main()
