#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查用户性别信息
"""

import pymysql
import sys

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'socialmeet',
    'charset': 'utf8mb4',
    'autocommit': True
}

def check_users_gender():
    """检查用户性别信息"""
    try:
        connection = pymysql.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        print("检查用户性别信息...")
        print("="*60)
        
        # 检查指定用户
        user_ids = [44479883, 65899032]
        
        for user_id in user_ids:
            cursor.execute("""
                SELECT id, username, nickname, gender, is_active, created_at 
                FROM users 
                WHERE id = %s
            """, (user_id,))
            
            user = cursor.fetchone()
            if user:
                print(f"用户 {user_id}:")
                print(f"  ID: {user[0]}")
                print(f"  用户名: {user[1]}")
                print(f"  昵称: {user[2]}")
                print(f"  性别: {user[3]}")
                print(f"  是否活跃: {user[4]}")
                print(f"  创建时间: {user[5]}")
                print()
            else:
                print(f"用户 {user_id} 不存在")
                print()
        
        # 检查所有女性用户
        print("所有女性用户:")
        print("-" * 40)
        cursor.execute("""
            SELECT id, username, nickname, gender, is_active 
            FROM users 
            WHERE gender = 'FEMALE' AND is_active = TRUE
            ORDER BY id
        """)
        
        female_users = cursor.fetchall()
        print(f"找到 {len(female_users)} 个女性用户:")
        for user in female_users:
            print(f"  ID: {user[0]}, 用户名: {user[1]}, 昵称: {user[2]}, 性别: {user[3]}")
        
        print()
        
        # 检查所有男性用户
        print("所有男性用户:")
        print("-" * 40)
        cursor.execute("""
            SELECT id, username, nickname, gender, is_active 
            FROM users 
            WHERE gender = 'MALE' AND is_active = TRUE
            ORDER BY id
        """)
        
        male_users = cursor.fetchall()
        print(f"找到 {len(male_users)} 个男性用户:")
        for user in male_users:
            print(f"  ID: {user[0]}, 用户名: {user[1]}, 昵称: {user[2]}, 性别: {user[3]}")
        
        connection.close()
        
    except Exception as e:
        print(f"检查失败: {e}")

if __name__ == "__main__":
    check_users_gender()

