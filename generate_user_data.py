#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import json

def generate_user_data():
    """生成用户数据"""
    
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
        
        print("开始生成用户数据...")
        
        # 1. 清空现有用户数据（保留测试用户）
        print("1. 清空现有用户数据...")
        cursor.execute("DELETE FROM transactions WHERE user_id NOT IN (86945008)")
        cursor.execute("DELETE FROM wallets WHERE user_id NOT IN (86945008)")
        cursor.execute("DELETE FROM users WHERE id NOT IN (86945008)")
        
        # 2. 插入用户数据
        print("2. 插入用户数据...")
        users_data = [
            (1001, 'user_1001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '13800138001', 'user1001@example.com', '不吃香菜', 'https://picsum.photos/200/200?random=1', 'FEMALE', '1999-05-15', '北京', '我是一个活泼开朗的女孩，喜欢聊天和交朋友。希望能遇到有趣的人一起分享生活的美好。', True, True, 25),
            (1002, 'user_1002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '13800138002', 'user1002@example.com', '你的菜', 'https://picsum.photos/200/200?random=2', 'FEMALE', '2001-03-22', '广州', '温柔可爱的女孩，喜欢听音乐和看电影。希望能找到志同道合的朋友。', True, False, 23),
            (1003, 'user_1003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '13800138003', 'user1003@example.com', '小仙女', 'https://picsum.photos/200/200?random=3', 'FEMALE', '1998-08-10', '上海', '充满正能量的女孩，喜欢运动和旅行。希望能遇到有趣的人一起分享快乐。', True, True, 26),
            (1004, 'user_1004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '13800138004', 'user1004@example.com', '甜心宝贝', 'https://picsum.photos/200/200?random=4', 'FEMALE', '1996-12-05', '深圳', '成熟稳重的姐姐，善解人意，喜欢读书和品茶。希望能找到真诚的朋友。', True, False, 28),
            (1005, 'user_1005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '13800138005', 'user1005@example.com', '阳光女孩', 'https://picsum.photos/200/200?random=5', 'FEMALE', '2000-07-18', '杭州', '热爱生活的阳光女孩，喜欢摄影和美食。希望能记录下每一个美好瞬间。', True, True, 24),
            (1006, 'user_1006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '13800138006', 'user1006@example.com', '文艺青年', 'https://picsum.photos/200/200?random=6', 'FEMALE', '1997-11-30', '成都', '文艺范十足的女孩，喜欢诗歌和艺术。希望能找到懂我的人。', True, False, 27),
            (1007, 'user_1007', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '13800138007', 'user1007@example.com', '运动达人', 'https://picsum.photos/200/200?random=7', 'FEMALE', '1999-04-12', '武汉', '热爱运动的健康女孩，喜欢跑步和瑜伽。希望能找到一起运动的伙伴。', True, True, 25),
            (1008, 'user_1008', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '13800138008', 'user1008@example.com', '音乐爱好者', 'https://picsum.photos/200/200?random=8', 'FEMALE', '2002-01-25', '西安', '音乐是我的生命，喜欢各种类型的音乐。希望能找到音乐上的知音。', True, False, 22),
            (1009, 'user_1009', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '13800138009', 'user1009@example.com', '旅行者', 'https://picsum.photos/200/200?random=9', 'FEMALE', '1995-09-08', '重庆', '世界那么大，我想去看看。喜欢探索未知的地方，希望能找到旅行的伙伴。', True, True, 29),
            (1010, 'user_1010', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '13800138010', 'user1010@example.com', '美食家', 'https://picsum.photos/200/200?random=10', 'FEMALE', '1998-06-14', '南京', '美食是我的最爱，喜欢尝试各种美味。希望能找到一起品尝美食的朋友。', True, False, 26)
        ]
        
        for user in users_data:
            cursor.execute("""
                INSERT INTO users (id, username, password, phone, email, nickname, avatar_url, gender, birth_date, location, bio, is_active, is_online, age, created_at, updated_at) 
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW())
            """, user)
        
        # 3. 创建钱包数据
        print("3. 创建钱包数据...")
        wallet_data = [
            (1001, 300.00, 0.00, 'CNY'),
            (1002, 200.00, 0.00, 'CNY'),
            (1003, 350.00, 0.00, 'CNY'),
            (1004, 500.00, 0.00, 'CNY'),
            (1005, 250.00, 0.00, 'CNY'),
            (1006, 180.00, 0.00, 'CNY'),
            (1007, 320.00, 0.00, 'CNY'),
            (1008, 280.00, 0.00, 'CNY'),
            (1009, 400.00, 0.00, 'CNY'),
            (1010, 150.00, 0.00, 'CNY')
        ]
        
        for wallet in wallet_data:
            cursor.execute("""
                INSERT INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at) 
                VALUES (%s, %s, %s, %s, NOW(), NOW())
            """, wallet)
        
        # 4. 创建交易记录
        print("4. 创建交易记录...")
        transaction_data = [
            (1001, 'RECHARGE', 300.00, 300.00, '初始充值', 'SUCCESS'),
            (1002, 'RECHARGE', 200.00, 200.00, '初始充值', 'SUCCESS'),
            (1003, 'RECHARGE', 350.00, 350.00, '初始充值', 'SUCCESS'),
            (1004, 'RECHARGE', 500.00, 500.00, '初始充值', 'SUCCESS'),
            (1005, 'RECHARGE', 250.00, 250.00, '初始充值', 'SUCCESS'),
            (1006, 'RECHARGE', 180.00, 180.00, '初始充值', 'SUCCESS'),
            (1007, 'RECHARGE', 320.00, 320.00, '初始充值', 'SUCCESS'),
            (1008, 'RECHARGE', 280.00, 280.00, '初始充值', 'SUCCESS'),
            (1009, 'RECHARGE', 400.00, 400.00, '初始充值', 'SUCCESS'),
            (1010, 'RECHARGE', 150.00, 150.00, '初始充值', 'SUCCESS')
        ]
        
        for transaction in transaction_data:
            cursor.execute("""
                INSERT INTO transactions (user_id, type, amount, balance_after, description, status, created_at) 
                VALUES (%s, %s, %s, %s, %s, %s, NOW())
            """, transaction)
        
        # 5. 创建用户状态表
        print("5. 创建用户状态表...")
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS user_status (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                user_id BIGINT NOT NULL,
                status VARCHAR(20) DEFAULT 'OFFLINE',
                call_price DECIMAL(10,2) DEFAULT 0.00,
                message_price DECIMAL(10,2) DEFAULT 0.00,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """)
        
        # 6. 插入用户状态数据
        print("6. 插入用户状态数据...")
        status_data = [
            (1001, 'FREE', 300.00, 10.00),
            (1002, 'BUSY', 200.00, 8.00),
            (1003, 'ONLINE', 350.00, 12.00),
            (1004, 'OFFLINE', 500.00, 15.00),
            (1005, 'ONLINE', 250.00, 9.00),
            (1006, 'OFFLINE', 180.00, 6.00),
            (1007, 'ONLINE', 320.00, 11.00),
            (1008, 'OFFLINE', 280.00, 10.00),
            (1009, 'ONLINE', 400.00, 14.00),
            (1010, 'OFFLINE', 150.00, 5.00)
        ]
        
        for status in status_data:
            cursor.execute("""
                INSERT INTO user_status (user_id, status, call_price, message_price, created_at, updated_at) 
                VALUES (%s, %s, %s, %s, NOW(), NOW())
            """, status)
        
        connection.commit()
        print("✅ 用户数据生成成功！")
        
        # 验证数据
        print("\n验证生成的数据...")
        cursor.execute("SELECT COUNT(*) FROM users WHERE id >= 1001")
        user_count = cursor.fetchone()[0]
        print(f"生成了 {user_count} 个用户")
        
        cursor.execute("SELECT COUNT(*) FROM wallets WHERE user_id >= 1001")
        wallet_count = cursor.fetchone()[0]
        print(f"生成了 {wallet_count} 个钱包")
        
        cursor.execute("SELECT COUNT(*) FROM user_status WHERE user_id >= 1001")
        status_count = cursor.fetchone()[0]
        print(f"生成了 {status_count} 个用户状态")
        
    except Exception as e:
        print(f"❌ 生成用户数据失败: {e}")
        import traceback
        traceback.print_exc()
        
    finally:
        if 'connection' in locals():
            connection.close()

if __name__ == "__main__":
    generate_user_data()
