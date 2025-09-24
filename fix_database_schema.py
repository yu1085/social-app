#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复数据库表结构
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

def fix_database_schema():
    """修复数据库表结构"""
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        print("🔧 修复数据库表结构...")
        
        # 修复messages表
        print("修复messages表...")
        alter_queries = [
            "ALTER TABLE messages MODIFY COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE",
            "ALTER TABLE messages MODIFY COLUMN is_recalled BOOLEAN NOT NULL DEFAULT FALSE",
            "ALTER TABLE messages MODIFY COLUMN message_status VARCHAR(20) NOT NULL DEFAULT 'SENT'",
            "ALTER TABLE messages ADD COLUMN IF NOT EXISTS media_duration INT DEFAULT NULL",
            "ALTER TABLE messages ADD COLUMN IF NOT EXISTS media_size BIGINT DEFAULT NULL",
            "ALTER TABLE messages ADD COLUMN IF NOT EXISTS conversation_id BIGINT DEFAULT NULL"
        ]
        
        for query in alter_queries:
            try:
                cursor.execute(query)
                print(f"✅ 执行成功: {query}")
            except Error as e:
                print(f"⚠️ 执行失败: {query} - {e}")
        
        # 修复conversations表
        print("修复conversations表...")
        conv_queries = [
            "ALTER TABLE conversations MODIFY COLUMN is_pinned_user1 BOOLEAN NOT NULL DEFAULT FALSE",
            "ALTER TABLE conversations MODIFY COLUMN is_pinned_user2 BOOLEAN NOT NULL DEFAULT FALSE",
            "ALTER TABLE conversations MODIFY COLUMN is_muted_user1 BOOLEAN NOT NULL DEFAULT FALSE",
            "ALTER TABLE conversations MODIFY COLUMN is_muted_user2 BOOLEAN NOT NULL DEFAULT FALSE",
            "ALTER TABLE conversations MODIFY COLUMN is_deleted_user1 BOOLEAN NOT NULL DEFAULT FALSE",
            "ALTER TABLE conversations MODIFY COLUMN is_deleted_user2 BOOLEAN NOT NULL DEFAULT FALSE",
            "ALTER TABLE conversations MODIFY COLUMN conversation_type VARCHAR(20) NOT NULL DEFAULT 'PRIVATE'",
            "ALTER TABLE conversations MODIFY COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE"
        ]
        
        for query in conv_queries:
            try:
                cursor.execute(query)
                print(f"✅ 执行成功: {query}")
            except Error as e:
                print(f"⚠️ 执行失败: {query} - {e}")
        
        connection.commit()
        print("✅ 数据库表结构修复完成")
        
    except Error as e:
        print(f"❌ 修复失败: {e}")
    finally:
        if cursor:
            cursor.close()
        if connection:
            connection.close()

if __name__ == "__main__":
    fix_database_schema()
