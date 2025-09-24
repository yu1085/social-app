#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import json

def check_users():
    """检查数据库中的用户"""
    
    try:
        # 连接数据库
        connection = pymysql.connect(
            host='localhost',
            user='root',
            password='123456',
            database='socialmeet',
            charset='utf8mb4'
        )
        
        with connection.cursor() as cursor:
            # 查询所有用户
            cursor.execute("SELECT id, username, phone FROM users ORDER BY id")
            users = cursor.fetchall()
            
            print("=== 数据库中的用户列表 ===")
            for user in users:
                print(f"ID: {user[0]}, 用户名: {user[1]}, 手机号: {user[2]}")
            
            print(f"\n总共有 {len(users)} 个用户")
            
            # 检查特定用户
            cursor.execute("SELECT id, username, phone FROM users WHERE id = 65899033")
            target_user = cursor.fetchone()
            
            if target_user:
                print(f"\n✅ 找到目标用户: ID={target_user[0]}, 用户名={target_user[1]}, 手机号={target_user[2]}")
            else:
                print(f"\n❌ 未找到用户ID: 65899033")
                
                # 建议一个存在的用户ID
                if users:
                    suggested_id = users[0][0] if users[0][0] != 65899032 else (users[1][0] if len(users) > 1 else None)
                    if suggested_id:
                        print(f"建议使用用户ID: {suggested_id}")
        
        connection.close()
        
    except Exception as e:
        print(f"数据库连接错误: {e}")

if __name__ == "__main__":
    check_users()