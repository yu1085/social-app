import pymysql
import sys

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
    
    # 查询女性用户
    cursor.execute("SELECT id, username, nickname, gender, is_active FROM users WHERE gender = 'FEMALE' AND is_active = 1")
    female_users = cursor.fetchall()
    
    print(f'找到 {len(female_users)} 个女性用户:')
    for user in female_users:
        print(f'ID: {user[0]}, 用户名: {user[1]}, 昵称: {user[2]}, 性别: {user[3]}, 活跃: {user[4]}')
    
    # 查询所有用户
    cursor.execute("SELECT id, username, nickname, gender, is_active FROM users")
    all_users = cursor.fetchall()
    
    print(f'\n总共有 {len(all_users)} 个用户:')
    for user in all_users:
        print(f'ID: {user[0]}, 用户名: {user[1]}, 昵称: {user[2]}, 性别: {user[3]}, 活跃: {user[4]}')
    
    cursor.close()
    connection.close()
    
except Exception as e:
    print(f'数据库连接失败: {e}')
    sys.exit(1)
