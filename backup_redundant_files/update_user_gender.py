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
    
    # 将用户ID 99999999 改为女性
    cursor.execute("""
        UPDATE users 
        SET gender = 'FEMALE', nickname = '测试女生', age = 25, location = '北京'
        WHERE id = 99999999
    """)
    
    print("已将用户 99999999 更新为女性用户")
    
    # 查询所有用户
    cursor.execute("SELECT id, username, nickname, gender, is_active FROM users")
    all_users = cursor.fetchall()
    
    print(f'\n现在有 {len(all_users)} 个用户:')
    for user in all_users:
        print(f'ID: {user[0]}, 用户名: {user[1]}, 昵称: {user[2]}, 性别: {user[3]}, 活跃: {user[4]}')
    
    connection.commit()
    cursor.close()
    connection.close()
    
except Exception as e:
    print(f'数据库操作失败: {e}')
    sys.exit(1)
