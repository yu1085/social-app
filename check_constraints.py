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

def check_constraints(connection):
    """检查表约束"""
    cursor = connection.cursor()
    
    try:
        # 检查 wealth_levels 表的约束
        print("\n🔍 检查 wealth_levels 表约束...")
        cursor.execute("SHOW CREATE TABLE wealth_levels")
        result = cursor.fetchone()
        create_sql = result[1]
        print("建表语句:")
        print(create_sql)
        
        # 检查是否有重复的 user_id = 0 的记录
        print("\n🔍 检查 user_id = 0 的记录...")
        cursor.execute("SELECT COUNT(*) FROM wealth_levels WHERE user_id = 0")
        count = cursor.fetchone()[0]
        print(f"user_id = 0 的记录数: {count}")
        
        if count > 0:
            cursor.execute("SELECT id, user_id, wealth_value, level_name FROM wealth_levels WHERE user_id = 0")
            records = cursor.fetchall()
            print("user_id = 0 的记录:")
            for record in records:
                print(f"  ID: {record[0]}, user_id: {record[1]}, wealth_value: {record[2]}, level_name: {record[3]}")
        
    except Exception as e:
        print(f"❌ 检查约束时出错: {e}")
    finally:
        cursor.close()

def main():
    """主函数"""
    print("🚀 开始检查数据库约束...")
    
    connection = connect_to_database()
    if connection is None:
        return
    
    try:
        check_constraints(connection)
    finally:
        connection.close()
        print("\n🔒 数据库连接已关闭")

if __name__ == "__main__":
    main()
