import pymysql
import sys
from datetime import datetime

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'socialmeet',
    'charset': 'utf8mb4'
}

try:
    connection = pymysql.connect(**DB_CONFIG)
    cursor = connection.cursor()
    
    # 创建几个女性测试用户
    female_users = [
        {
            'username': 'female_user_1',
            'nickname': '小美',
            'phone': '13800138001',
            'gender': 'FEMALE',
            'age': 25,
            'location': '北京'
        },
        {
            'username': 'female_user_2', 
            'nickname': '小丽',
            'phone': '13800138002',
            'gender': 'FEMALE',
            'age': 23,
            'location': '上海'
        },
        {
            'username': 'female_user_3',
            'nickname': '小芳',
            'phone': '13800138003', 
            'gender': 'FEMALE',
            'age': 27,
            'location': '广州'
        }
    ]
    
    for user_data in female_users:
        # 检查用户是否已存在
        cursor.execute("SELECT id FROM users WHERE username = %s", (user_data['username'],))
        if cursor.fetchone():
            print(f"用户 {user_data['username']} 已存在，跳过")
            continue
            
        # 创建用户
        cursor.execute("""
            INSERT INTO users (username, nickname, phone, gender, age, location, is_active, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, 1, %s, %s)
        """, (
            user_data['username'],
            user_data['nickname'], 
            user_data['phone'],
            user_data['gender'],
            user_data['age'],
            user_data['location'],
            datetime.now(),
            datetime.now()
        ))
        
        user_id = cursor.lastrowid
        
        # 创建钱包
        cursor.execute("""
            INSERT INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at)
            VALUES (%s, 10000.00, 0.00, 'CNY', %s, %s)
        """, (user_id, datetime.now(), datetime.now()))
        
        # 创建默认通话设置
        cursor.execute("""
            INSERT INTO call_settings (user_id, video_call_enabled, video_call_price, voice_call_enabled, voice_call_price, 
                                     message_charge_enabled, message_price, free_call_duration, auto_answer_enabled, created_at, updated_at)
            VALUES (%s, 1, 200.0, 1, 150.0, 0, 0.0, 0, 0, %s, %s)
        """, (user_id, datetime.now(), datetime.now()))
        
        print(f"创建女性用户: {user_data['nickname']} (ID: {user_id})")
    
    connection.commit()
    print("女性用户创建完成！")
    
    # 查询所有女性用户
    cursor.execute("SELECT id, username, nickname, gender, is_active FROM users WHERE gender = 'FEMALE'")
    female_users = cursor.fetchall()
    
    print(f'\n现在有 {len(female_users)} 个女性用户:')
    for user in female_users:
        print(f'ID: {user[0]}, 用户名: {user[1]}, 昵称: {user[2]}, 性别: {user[3]}, 活跃: {user[4]}')
    
    cursor.close()
    connection.close()
    
except Exception as e:
    print(f'数据库操作失败: {e}')
    sys.exit(1)
