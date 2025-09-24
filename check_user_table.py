#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查用户表结构
"""

import mysql.connector
from mysql.connector import Error

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'socialmeet',
    'charset': 'utf8mb4'
}

def check_user_table():
    """检查用户表结构"""
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        print("🔍 检查用户表结构...")
        
        # 查看用户表结构
        cursor.execute("DESCRIBE users")
        columns = cursor.fetchall()
        
        print("用户表字段:")
        for column in columns:
            print(f"  {column[0]} - {column[1]} - {column[2]} - {column[3]} - {column[4]} - {column[5]}")
        
        # 检查是否有avatar字段
        avatar_exists = any(col[0] == 'avatar' for col in columns)
        print(f"\navatar字段存在: {avatar_exists}")
        
        if not avatar_exists:
            print("添加avatar字段...")
            cursor.execute("ALTER TABLE users ADD COLUMN avatar VARCHAR(500) DEFAULT NULL")
            print("✅ avatar字段添加成功")
        
        # 检查其他可能缺失的字段
        required_fields = ['nickname', 'phone', 'email', 'is_online', 'status']
        for field in required_fields:
            field_exists = any(col[0] == field for col in columns)
            if not field_exists:
                print(f"添加{field}字段...")
                if field == 'nickname':
                    cursor.execute("ALTER TABLE users ADD COLUMN nickname VARCHAR(50) DEFAULT NULL")
                elif field == 'phone':
                    cursor.execute("ALTER TABLE users ADD COLUMN phone VARCHAR(20) DEFAULT NULL")
                elif field == 'email':
                    cursor.execute("ALTER TABLE users ADD COLUMN email VARCHAR(100) DEFAULT NULL")
                elif field == 'is_online':
                    cursor.execute("ALTER TABLE users ADD COLUMN is_online BOOLEAN NOT NULL DEFAULT FALSE")
                elif field == 'status':
                    cursor.execute("ALTER TABLE users ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE'")
                print(f"✅ {field}字段添加成功")
        
        connection.commit()
        print("✅ 用户表结构检查完成")
        
    except Error as e:
        print(f"❌ 检查失败: {e}")
    finally:
        if cursor:
            cursor.close()
        if connection:
            connection.close()

if __name__ == "__main__":
    check_user_table()
