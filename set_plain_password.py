#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
设置明文密码
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

def set_plain_password():
    """设置明文密码"""
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        print("🔧 设置明文密码...")
        
        # 设置明文密码
        password = "123456"
        
        # 更新用户密码为明文
        cursor.execute("UPDATE users SET password = %s WHERE username = 'user_1001'", (password,))
        affected_rows = cursor.rowcount
        
        if affected_rows > 0:
            print(f"✅ 已更新用户 user_1001 的密码为明文 '123456'")
        else:
            print("❌ 没有找到用户 user_1001")
        
        # 也更新其他测试用户
        cursor.execute("UPDATE users SET password = %s WHERE username LIKE 'user_%'", (password,))
        affected_rows = cursor.rowcount
        print(f"✅ 已更新 {affected_rows} 个用户的密码为明文")
        
        connection.commit()
        print("✅ 密码设置完成")
        
    except Error as e:
        print(f"❌ 设置失败: {e}")
    finally:
        if cursor:
            cursor.close()
        if connection:
            connection.close()

if __name__ == "__main__":
    set_plain_password()
