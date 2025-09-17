#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql

def check_user_table():
    """检查用户表结构"""
    
    config = {
        'host': 'localhost',
        'user': 'root',
        'password': '123456',
        'database': 'socialmeet',
        'charset': 'utf8mb4'
    }
    
    try:
        connection = pymysql.connect(**config)
        cursor = connection.cursor()
        
        print("用户表结构:")
        cursor.execute("DESCRIBE users")
        for row in cursor.fetchall():
            print(row)
            
    except Exception as e:
        print(f"错误: {e}")
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    check_user_table()
