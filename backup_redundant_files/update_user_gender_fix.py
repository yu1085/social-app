#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import mysql.connector
from mysql.connector import Error

def update_user_gender():
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
            
            # 查询当前用户信息
            print("=== 更新前的用户信息 ===")
            cursor.execute("""
                SELECT id, username, nickname, gender, is_online 
                FROM users 
                WHERE id IN (65899032, 44479883, 1001)
                ORDER BY id
            """)
            
            for row in cursor.fetchall():
                print(f"ID: {row[0]}, 用户名: {row[1]}, 昵称: {row[2]}, 性别: {row[3]}, 在线: {row[4]}")
            
            # 更新用户ID: 65899032 的性别为女性
            print("\n=== 更新用户性别 ===")
            update_query = """
                UPDATE users 
                SET gender = 'FEMALE' 
                WHERE id = 65899032
            """
            
            cursor.execute(update_query)
            connection.commit()
            print(f"已更新用户ID: 65899032 的性别为 FEMALE")
            
            # 查询更新后的用户信息
            print("\n=== 更新后的用户信息 ===")
            cursor.execute("""
                SELECT id, username, nickname, gender, is_online 
                FROM users 
                WHERE id IN (65899032, 44479883, 1001)
                ORDER BY id
            """)
            
            for row in cursor.fetchall():
                print(f"ID: {row[0]}, 用户名: {row[1]}, 昵称: {row[2]}, 性别: {row[3]}, 在线: {row[4]}")
            
            # 查询所有在线女性用户
            print("\n=== 所有在线女性用户 ===")
            cursor.execute("""
                SELECT id, username, nickname, gender, is_online 
                FROM users 
                WHERE gender = 'FEMALE' AND is_online = 1
                ORDER BY id
            """)
            
            for row in cursor.fetchall():
                print(f"ID: {row[0]}, 用户名: {row[1]}, 昵称: {row[2]}, 性别: {row[3]}, 在线: {row[4]}")
            
    except Error as e:
        print(f"数据库错误: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()
            print("\n数据库连接已关闭")

if __name__ == "__main__":
    update_user_gender()
