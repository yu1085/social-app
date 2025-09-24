#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
更新用户密码
"""

import mysql.connector
from mysql.connector import Error
import bcrypt

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'socialmeet',
    'charset': 'utf8mb4'
}

def update_user_password():
    """更新用户密码"""
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        print("🔧 更新用户密码...")
        
        # 使用BCrypt加密密码
        password = "123456"
        hashed_password = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')
        print(f"BCrypt加密后的密码: {hashed_password}")
        
        # 更新用户密码
        cursor.execute("UPDATE users SET password = %s WHERE username = 'user_1001'", (hashed_password,))
        affected_rows = cursor.rowcount
        
        if affected_rows > 0:
            print(f"✅ 已更新用户 user_1001 的密码")
        else:
            print("❌ 没有找到用户 user_1001")
        
        # 也更新其他测试用户
        cursor.execute("UPDATE users SET password = %s WHERE username LIKE 'user_%'", (hashed_password,))
        affected_rows = cursor.rowcount
        print(f"✅ 已更新 {affected_rows} 个用户的密码")
        
        connection.commit()
        print("✅ 密码更新完成")
        
    except Error as e:
        print(f"❌ 更新失败: {e}")
    finally:
        if cursor:
            cursor.close()
        if connection:
            connection.close()

if __name__ == "__main__":
    update_user_password()
