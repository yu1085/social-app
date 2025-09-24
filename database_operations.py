#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
直接操作数据库脚本
创建测试用户和消息数据
"""

import mysql.connector
from mysql.connector import Error
import json
from datetime import datetime, timedelta
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

class DatabaseManager:
    def __init__(self):
        self.connection = None
        self.cursor = None
    
    def connect(self):
        """连接数据库"""
        try:
            self.connection = mysql.connector.connect(**DB_CONFIG)
            self.cursor = self.connection.cursor(dictionary=True)
            print("✅ 数据库连接成功")
            return True
        except Error as e:
            print(f"❌ 数据库连接失败: {e}")
            return False
    
    def disconnect(self):
        """断开数据库连接"""
        if self.cursor:
            self.cursor.close()
        if self.connection:
            self.connection.close()
        print("✅ 数据库连接已断开")
    
    def execute_query(self, query, params=None):
        """执行查询"""
        try:
            self.cursor.execute(query, params)
            return self.cursor.fetchall()
        except Error as e:
            print(f"❌ 查询执行失败: {e}")
            return None
    
    def execute_update(self, query, params=None):
        """执行更新操作"""
        try:
            self.cursor.execute(query, params)
            self.connection.commit()
            return self.cursor.rowcount
        except Error as e:
            print(f"❌ 更新执行失败: {e}")
            self.connection.rollback()
            return 0
    
    def check_tables(self):
        """检查表是否存在"""
        tables = ['users', 'messages', 'conversations']
        existing_tables = []
        
        for table in tables:
            query = f"SHOW TABLES LIKE '{table}'"
            result = self.execute_query(query)
            if result:
                existing_tables.append(table)
                print(f"✅ 表 {table} 存在")
            else:
                print(f"❌ 表 {table} 不存在")
        
        return existing_tables
    
    def create_test_users(self):
        """创建测试用户"""
        users = [
            {
                'username': 'testuser001',
                'password': hashlib.md5('123456'.encode()).hexdigest(),
                'nickname': '测试用户001',
                'phone': '13800138001',
                'email': 'test001@example.com',
                'avatar': 'https://example.com/avatar1.jpg',
                'is_online': True,
                'status': 'ONLINE'
            },
            {
                'username': 'testuser002',
                'password': hashlib.md5('123456'.encode()).hexdigest(),
                'nickname': '测试用户002',
                'phone': '13800138002',
                'email': 'test002@example.com',
                'avatar': 'https://example.com/avatar2.jpg',
                'is_online': False,
                'status': 'OFFLINE'
            },
            {
                'username': 'testuser003',
                'password': hashlib.md5('123456'.encode()).hexdigest(),
                'nickname': '测试用户003',
                'phone': '13800138003',
                'email': 'test003@example.com',
                'avatar': 'https://example.com/avatar3.jpg',
                'is_online': True,
                'status': 'ONLINE'
            }
        ]
        
        created_users = []
        for user in users:
            # 检查用户是否已存在
            check_query = "SELECT id FROM users WHERE username = %s OR phone = %s"
            existing = self.execute_query(check_query, (user['username'], user['phone']))
            
            if existing:
                print(f"⚠️ 用户 {user['username']} 已存在，跳过创建")
                created_users.append(existing[0])
                continue
            
            # 创建用户
            insert_query = """
            INSERT INTO users (username, password, nickname, phone, email, avatar, is_online, status, created_at, updated_at)
            VALUES (%(username)s, %(password)s, %(nickname)s, %(phone)s, %(email)s, %(avatar)s, %(is_online)s, %(status)s, NOW(), NOW())
            """
            
            self.cursor.execute(insert_query, user)
            user_id = self.cursor.lastrowid
            user['id'] = user_id
            created_users.append(user)
            print(f"✅ 创建用户: {user['username']} (ID: {user_id})")
        
        self.connection.commit()
        return created_users
    
    def create_test_messages(self, users):
        """创建测试消息"""
        if len(users) < 2:
            print("❌ 用户数量不足，无法创建消息")
            return []
        
        messages = [
            {
                'sender_id': users[0]['id'],
                'receiver_id': users[1]['id'],
                'content': '你好，很高兴认识你！',
                'message_type': 'TEXT',
                'is_read': False,
                'send_time': datetime.now() - timedelta(hours=2)
            },
            {
                'sender_id': users[1]['id'],
                'receiver_id': users[0]['id'],
                'content': '你好！我也很高兴认识你',
                'message_type': 'TEXT',
                'is_read': True,
                'send_time': datetime.now() - timedelta(hours=1, minutes=30)
            },
            {
                'sender_id': users[0]['id'],
                'receiver_id': users[1]['id'],
                'content': '今天天气真不错',
                'message_type': 'TEXT',
                'is_read': False,
                'send_time': datetime.now() - timedelta(minutes=30)
            },
            {
                'sender_id': users[2]['id'],
                'receiver_id': users[0]['id'],
                'content': '[视频通话]',
                'message_type': 'CALL',
                'is_read': False,
                'send_time': datetime.now() - timedelta(minutes=15)
            },
            {
                'sender_id': users[0]['id'],
                'receiver_id': users[2]['id'],
                'content': '好的，我们开始视频通话吧',
                'message_type': 'TEXT',
                'is_read': True,
                'send_time': datetime.now() - timedelta(minutes=10)
            }
        ]
        
        created_messages = []
        for message in messages:
            insert_query = """
            INSERT INTO messages (sender_id, receiver_id, content, message_type, is_read, send_time, created_at, updated_at)
            VALUES (%(sender_id)s, %(receiver_id)s, %(content)s, %(message_type)s, %(is_read)s, %(send_time)s, NOW(), NOW())
            """
            
            self.cursor.execute(insert_query, message)
            message_id = self.cursor.lastrowid
            message['id'] = message_id
            created_messages.append(message)
            print(f"✅ 创建消息: {message['content'][:20]}... (ID: {message_id})")
        
        self.connection.commit()
        return created_messages
    
    def create_test_conversations(self, users, messages):
        """创建测试会话"""
        if len(users) < 2 or len(messages) < 2:
            print("❌ 数据不足，无法创建会话")
            return []
        
        # 获取最新的消息作为会话的最后消息
        last_message_1 = messages[-1]  # 用户0和用户2的最新消息
        last_message_2 = messages[-2]  # 用户0和用户1的最新消息
        
        conversations = [
            {
                'user1_id': users[0]['id'],
                'user2_id': users[1]['id'],
                'last_message_id': last_message_2['id'],
                'last_message_content': last_message_2['content'],
                'last_message_time': last_message_2['send_time'],
                'unread_count_user1': 1,
                'unread_count_user2': 0,
                'is_pinned_user1': False,
                'is_pinned_user2': False,
                'is_muted_user1': False,
                'is_muted_user2': False,
                'is_deleted_user1': False,
                'is_deleted_user2': False,
                'conversation_type': 'PRIVATE'
            },
            {
                'user1_id': users[0]['id'],
                'user2_id': users[2]['id'],
                'last_message_id': last_message_1['id'],
                'last_message_content': last_message_1['content'],
                'last_message_time': last_message_1['send_time'],
                'unread_count_user1': 0,
                'unread_count_user2': 1,
                'is_pinned_user1': True,
                'is_pinned_user2': False,
                'is_muted_user1': False,
                'is_muted_user2': False,
                'is_deleted_user1': False,
                'is_deleted_user2': False,
                'conversation_type': 'PRIVATE'
            }
        ]
        
        created_conversations = []
        for conv in conversations:
            # 检查会话是否已存在
            check_query = """
            SELECT id FROM conversations 
            WHERE (user1_id = %s AND user2_id = %s) OR (user1_id = %s AND user2_id = %s)
            """
            existing = self.execute_query(check_query, (conv['user1_id'], conv['user2_id'], conv['user2_id'], conv['user1_id']))
            
            if existing:
                print(f"⚠️ 会话已存在，跳过创建")
                continue
            
            insert_query = """
            INSERT INTO conversations (
                user1_id, user2_id, last_message_id, last_message_content, last_message_time,
                unread_count_user1, unread_count_user2, is_pinned_user1, is_pinned_user2,
                is_muted_user1, is_muted_user2, is_deleted_user1, is_deleted_user2,
                conversation_type, created_at, updated_at
            ) VALUES (
                %(user1_id)s, %(user2_id)s, %(last_message_id)s, %(last_message_content)s, %(last_message_time)s,
                %(unread_count_user1)s, %(unread_count_user2)s, %(is_pinned_user1)s, %(is_pinned_user2)s,
                %(is_muted_user1)s, %(is_muted_user2)s, %(is_deleted_user1)s, %(is_deleted_user2)s,
                %(conversation_type)s, NOW(), NOW()
            )
            """
            
            self.cursor.execute(insert_query, conv)
            conv_id = self.cursor.lastrowid
            conv['id'] = conv_id
            created_conversations.append(conv)
            print(f"✅ 创建会话: 用户{conv['user1_id']} <-> 用户{conv['user2_id']} (ID: {conv_id})")
        
        self.connection.commit()
        return created_conversations
    
    def query_conversations_for_user(self, user_id):
        """查询用户的会话列表"""
        query = """
        SELECT 
            c.*,
            u.nickname as other_user_nickname,
            u.avatar as other_user_avatar,
            u.is_online as other_user_online,
            u.status as other_user_status
        FROM conversations c
        LEFT JOIN users u ON (
            CASE 
                WHEN c.user1_id = %s THEN c.user2_id
                ELSE c.user1_id
            END = u.id
        )
        WHERE (c.user1_id = %s OR c.user2_id = %s) 
        AND (c.is_deleted_user1 = FALSE OR c.is_deleted_user2 = FALSE)
        ORDER BY c.last_message_time DESC
        """
        
        result = self.execute_query(query, (user_id, user_id, user_id))
        return result
    
    def query_messages_between_users(self, user1_id, user2_id, limit=20):
        """查询两个用户之间的消息"""
        query = """
        SELECT 
            m.*,
            s.nickname as sender_nickname,
            s.avatar as sender_avatar,
            r.nickname as receiver_nickname,
            r.avatar as receiver_avatar
        FROM messages m
        LEFT JOIN users s ON m.sender_id = s.id
        LEFT JOIN users r ON m.receiver_id = r.id
        WHERE ((m.sender_id = %s AND m.receiver_id = %s) OR (m.sender_id = %s AND m.receiver_id = %s))
        AND m.is_deleted = FALSE
        ORDER BY m.send_time DESC
        LIMIT %s
        """
        
        result = self.execute_query(query, (user1_id, user2_id, user2_id, user1_id, limit))
        return result

def main():
    """主函数"""
    db = DatabaseManager()
    
    if not db.connect():
        return
    
    try:
        print("🔍 检查数据库表...")
        tables = db.check_tables()
        
        if 'users' not in tables:
            print("❌ users表不存在，请先创建数据库表")
            return
        
        print("\n👥 创建测试用户...")
        users = db.create_test_users()
        
        print("\n💬 创建测试消息...")
        messages = db.create_test_messages(users)
        
        print("\n🗨️ 创建测试会话...")
        conversations = db.create_test_conversations(users, messages)
        
        print("\n📊 查询测试数据...")
        if users:
            user_id = users[0]['id']
            print(f"\n查询用户 {user_id} 的会话列表:")
            convs = db.query_conversations_for_user(user_id)
            for conv in convs:
                print(f"  会话ID: {conv['id']}, 对方: {conv['other_user_nickname']}, 最后消息: {conv['last_message_content']}")
            
            if len(users) > 1:
                print(f"\n查询用户 {users[0]['id']} 和 {users[1]['id']} 的消息:")
                msgs = db.query_messages_between_users(users[0]['id'], users[1]['id'])
                for msg in msgs:
                    print(f"  消息: {msg['content']} (发送者: {msg['sender_nickname']}, 时间: {msg['send_time']})")
        
        print("\n✅ 数据库操作完成")
        
    except Exception as e:
        print(f"❌ 操作异常: {e}")
    finally:
        db.disconnect()

if __name__ == "__main__":
    main()
