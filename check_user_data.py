#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查用户数据
"""

import mysql.connector
from mysql.connector import Error
import hashlib

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'socialmeet',
    'charset': 'utf8mb4'
}

def check_user_data():
    """检查用户数据"""
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        cursor = connection.cursor(dictionary=True)
        
        print("🔍 检查用户数据...")
        
        # 查询所有用户
        cursor.execute("SELECT id, username, password, nickname, phone FROM users LIMIT 10")
        users = cursor.fetchall()
        
        print(f"找到 {len(users)} 个用户:")
        for user in users:
            print(f"  ID: {user['id']}, 用户名: {user['username']}, 昵称: {user['nickname']}, 手机: {user['phone']}")
            print(f"    密码: {user['password']}")
        
        # 测试密码加密
        test_password = "123456"
        md5_password = hashlib.md5(test_password.encode()).hexdigest()
        print(f"\n测试密码 '123456' 的MD5值: {md5_password}")
        
        # 查找匹配的用户
        cursor.execute("SELECT * FROM users WHERE password = %s", (md5_password,))
        matching_users = cursor.fetchall()
        
        print(f"找到 {len(matching_users)} 个密码匹配的用户:")
        for user in matching_users:
            print(f"  ID: {user['id']}, 用户名: {user['username']}, 昵称: {user['nickname']}")
        
        # 更新用户密码为明文（用于测试）
        if matching_users:
            user_id = matching_users[0]['id']
            cursor.execute("UPDATE users SET password = %s WHERE id = %s", ("123456", user_id))
            connection.commit()
            print(f"✅ 已更新用户 {user_id} 的密码为明文 '123456'")
        
    except Error as e:
        print(f"❌ 检查失败: {e}")
    finally:
        if cursor:
            cursor.close()
        if connection:
            connection.close()

if __name__ == "__main__":
    check_user_data()
