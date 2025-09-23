#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查数据库表结构
"""

import pymysql

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

def check_table_structure(connection):
    """检查表结构"""
    cursor = connection.cursor()
    
    try:
        # 检查 wealth_levels 表结构
        print("\n🔍 检查 wealth_levels 表结构...")
        cursor.execute("DESCRIBE wealth_levels")
        columns = cursor.fetchall()
        
        print("字段列表:")
        for col in columns:
            print(f"  - {col[0]}: {col[1]} (可空: {col[2]}, 默认值: {col[4]})")
        
        # 检查是否有 min_contribution 字段
        min_contribution_fields = [col for col in columns if 'contribution' in col[0].lower()]
        if min_contribution_fields:
            print(f"\n⚠️  发现 contribution 相关字段: {[col[0] for col in min_contribution_fields]}")
        else:
            print("\n✅ 没有发现 contribution 相关字段")
        
        # 检查表数据
        print("\n📊 检查 wealth_levels 表数据...")
        cursor.execute("SELECT COUNT(*) FROM wealth_levels")
        count = cursor.fetchone()[0]
        print(f"表中记录数: {count}")
        
        if count > 0:
            cursor.execute("SELECT * FROM wealth_levels LIMIT 3")
            rows = cursor.fetchall()
            print("前3条记录:")
            for i, row in enumerate(rows, 1):
                print(f"  记录 {i}: {row}")
        
    except Exception as e:
        print(f"❌ 检查表结构时出错: {e}")
    finally:
        cursor.close()

def main():
    """主函数"""
    print("🚀 开始检查数据库表结构...")
    
    connection = connect_to_database()
    if not connection:
        print("❌ 无法连接到数据库，退出")
        return
    
    try:
        check_table_structure(connection)
    except Exception as e:
        print(f"❌ 检查过程中出现错误: {e}")
    finally:
        connection.close()
        print("🔒 数据库连接已关闭")

if __name__ == "__main__":
    main()
