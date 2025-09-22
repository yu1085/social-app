#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修改用户性别为女性，使其能在首页显示
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

def update_users_gender():
    """修改用户性别为女性"""
    try:
        connection = pymysql.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        print("修改用户性别为女性...")
        print("="*60)
        
        user_ids = [44479883, 65899032]
        
        for user_id in user_ids:
            # 更新用户性别为女性
            cursor.execute("""
                UPDATE users 
                SET gender = 'FEMALE', updated_at = NOW() 
                WHERE id = %s
            """, (user_id,))
            
            if cursor.rowcount > 0:
                print(f"✓ 用户 {user_id} 性别已更新为 FEMALE")
            else:
                print(f"✗ 用户 {user_id} 更新失败")
        
        print()
        
        # 验证更新结果
        print("验证更新结果:")
        print("-" * 40)
        for user_id in user_ids:
            cursor.execute("""
                SELECT id, username, nickname, gender, updated_at 
                FROM users 
                WHERE id = %s
            """, (user_id,))
            
            user = cursor.fetchone()
            if user:
                print(f"用户 {user[0]}: {user[1]} ({user[2]}) - 性别: {user[3]} - 更新时间: {user[4]}")
        
        connection.close()
        print("\n✓ 用户性别更新完成！现在这些用户应该能在首页显示了。")
        
    except Exception as e:
        print(f"更新失败: {e}")

if __name__ == "__main__":
    update_users_gender()

