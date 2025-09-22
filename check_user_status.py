#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import mysql.connector
from mysql.connector import Error

def check_user_status():
    try:
        # 连接数据库
        connection = mysql.connector.connect(
            host='localhost',
            user='root',
            password='123456',
            database='socialmeet'
        )
        
        if connection.is_connected():
            cursor = connection.cursor()
            
            # 查询测试账号状态
            print("=== 测试账号状态 ===")
            cursor.execute("""
                SELECT id, username, nickname, gender, is_online, last_seen 
                FROM users 
                WHERE id IN (65899032, 44479883, 1001)
                ORDER BY id
            """)
            
            for row in cursor.fetchall():
                print(f"ID: {row[0]}, 用户名: {row[1]}, 昵称: {row[2]}, 性别: {row[3]}, 在线: {row[4]}, 最后在线: {row[5]}")
            
            # 查询所有在线女性用户
            print("\n=== 所有在线女性用户 ===")
            cursor.execute("""
                SELECT id, username, nickname, gender, is_online, last_seen 
                FROM users 
                WHERE gender = 'FEMALE' AND is_online = 1
                ORDER BY id
            """)
            
            for row in cursor.fetchall():
                print(f"ID: {row[0]}, 用户名: {row[1]}, 昵称: {row[2]}, 性别: {row[3]}, 在线: {row[4]}, 最后在线: {row[5]}")
            
            # 强制更新用户ID: 65899032 为在线状态
            print("\n=== 强制更新用户在线状态 ===")
            cursor.execute("""
                UPDATE users 
                SET is_online = 1, last_seen = NOW() 
                WHERE id = 65899032
            """)
            connection.commit()
            print("已更新用户ID: 65899032 为在线状态")
            
            # 再次查询测试账号状态
            print("\n=== 更新后的测试账号状态 ===")
            cursor.execute("""
                SELECT id, username, nickname, gender, is_online, last_seen 
                FROM users 
                WHERE id IN (65899032, 44479883, 1001)
                ORDER BY id
            """)
            
            for row in cursor.fetchall():
                print(f"ID: {row[0]}, 用户名: {row[1]}, 昵称: {row[2]}, 性别: {row[3]}, 在线: {row[4]}, 最后在线: {row[5]}")
            
    except Error as e:
        print(f"数据库错误: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("\n数据库连接已关闭")

if __name__ == "__main__":
    check_user_status()
