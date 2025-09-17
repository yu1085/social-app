#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql

def check_mysql_tables():
    """检查MySQL数据库中的表结构"""
    
    config = {
        'host': 'localhost',
        'user': 'root',
        'password': '123456',
        'database': 'socialmeet',
        'charset': 'utf8mb4'
    }
    
    try:
        connection = pymysql.connect(**config)
        cursor = connection.cursor()
        
        print("=== 检查MySQL数据库表结构 ===")
        
        # 1. 检查所有表
        cursor.execute("SHOW TABLES")
        tables = cursor.fetchall()
        print(f"数据库中的表: {[table[0] for table in tables]}")
        
        # 2. 检查wallets表结构
        if ('wallets',) in tables:
            print("\n=== wallets表结构 ===")
            cursor.execute("DESCRIBE wallets")
            columns = cursor.fetchall()
            for column in columns:
                print(f"  {column[0]}: {column[1]} {column[2]} {column[3]} {column[4]}")
            
            # 3. 检查wallets表数据
            print("\n=== wallets表数据 ===")
            cursor.execute("SELECT * FROM wallets")
            rows = cursor.fetchall()
            for row in rows:
                print(f"  {row}")
        else:
            print("❌ wallets表不存在")
        
        # 4. 检查users表结构
        if ('users',) in tables:
            print("\n=== users表结构 ===")
            cursor.execute("DESCRIBE users")
            columns = cursor.fetchall()
            for column in columns:
                print(f"  {column[0]}: {column[1]} {column[2]} {column[3]} {column[4]}")
            
            # 5. 检查users表数据
            print("\n=== users表数据 ===")
            cursor.execute("SELECT id, username, nickname, phone FROM users WHERE id = 86945008")
            rows = cursor.fetchall()
            for row in rows:
                print(f"  {row}")
        else:
            print("❌ users表不存在")
        
    except Exception as e:
        print(f"❌ 数据库操作失败: {e}")
        
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    check_mysql_tables()
