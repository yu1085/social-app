#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import time

def create_simple_user():
    """创建简单的测试用户"""
    
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
            # 创建用户ID 65899033
            user_id = 65899033
            username = "user_13800138001"
            phone = "13800138001"
            password = "123456"  # 明文密码
            
            # 检查用户是否已存在
            cursor.execute("SELECT id FROM users WHERE id = %s", (user_id,))
            if cursor.fetchone():
                print(f"用户ID {user_id} 已存在")
                return
            
            # 插入新用户（只使用必要的字段）
            insert_sql = """
            INSERT INTO users (id, username, phone, password, nickname, gender, is_active)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            """
            
            cursor.execute(insert_sql, (
                user_id,           # id
                username,          # username
                phone,             # phone
                password,          # password (明文)
                "测试用户",        # nickname
                "FEMALE",          # gender
                True               # is_active
            ))
            
            connection.commit()
            print(f"✅ 成功创建用户:")
            print(f"   ID: {user_id}")
            print(f"   用户名: {username}")
            print(f"   手机号: {phone}")
            print(f"   密码: {password}")
            print(f"   昵称: 测试用户")
            
        connection.close()
        
    except Exception as e:
        print(f"创建用户失败: {e}")

if __name__ == "__main__":
    create_simple_user()
