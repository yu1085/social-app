#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql

def check_user_table():
    """检查用户表结构"""
    
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
            # 查看用户表结构
            cursor.execute("DESCRIBE users")
            columns = cursor.fetchall()
            
            print("=== 用户表结构 ===")
            for column in columns:
                print(f"{column[0]}: {column[1]} {column[2]} {column[3]} {column[4]} {column[5]}")
        
        connection.close()
        
    except Exception as e:
        print(f"查询失败: {e}")

if __name__ == "__main__":
    check_user_table()
