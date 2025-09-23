#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
添加靓号测试数据到数据库
"""

import mysql.connector
import sys

def add_lucky_numbers():
    try:
        # 连接数据库
        conn = mysql.connector.connect(
            host='localhost',
            port=3306,
            user='root',
            password='123456',
            database='socialmeet',
            charset='utf8mb4'
        )
        
        cursor = conn.cursor()
        
        # 检查表是否存在
        cursor.execute("SHOW TABLES LIKE 'lucky_numbers'")
        if not cursor.fetchone():
            print("❌ lucky_numbers表不存在，请先运行数据库迁移")
            return False
        
        # 检查是否已有数据
        cursor.execute("SELECT COUNT(*) FROM lucky_numbers")
        count = cursor.fetchone()[0]
        
        if count > 0:
            print(f"✅ 数据库中已有 {count} 条靓号数据")
            return True
        
        # 插入测试数据
        lucky_numbers = [
            # 限量靓号
            ('10000005', 'LIMITED', 88800, True, True, '限量靓号'),
            ('12345678', 'LIMITED', 128000, True, True, '限量靓号'),
            ('88888888', 'LIMITED', 188000, True, True, '限量靓号'),
            ('66666666', 'LIMITED', 168000, True, True, '限量靓号'),
            
            # 顶级靓号
            ('10000010', 'TOP', 88800, False, True, '顶级靓号'),
            ('10000011', 'TOP', 58800, False, True, '顶级靓号'),
            ('10000012', 'TOP', 58800, False, True, '顶级靓号'),
            ('10000013', 'TOP', 58800, False, True, '顶级靓号'),
            ('10000014', 'TOP', 58800, False, True, '顶级靓号'),
            ('10000015', 'TOP', 58800, False, True, '顶级靓号'),
            
            # 超级靓号
            ('99999998', 'SUPER', 5880, False, True, '超级靓号'),
            ('66666668', 'SUPER', 5880, False, True, '超级靓号'),
            ('12222222', 'SUPER', 5880, False, True, '超级靓号'),
            ('21212121', 'SUPER', 5880, False, True, '超级靓号'),
            ('89898989', 'SUPER', 5880, False, True, '超级靓号'),
            
            # 普通靓号
            ('18888828', 'NORMAL', 3800, False, True, '普通靓号'),
            ('18888868', 'NORMAL', 3800, False, True, '普通靓号'),
            ('18888878', 'NORMAL', 3800, False, True, '普通靓号'),
            ('19188888', 'NORMAL', 3800, False, True, '普通靓号'),
            ('19188818', 'NORMAL', 3800, False, True, '普通靓号'),
        ]
        
        # 插入数据
        insert_sql = """
        INSERT INTO lucky_numbers (number, tier, price, is_limited, is_available, description, icon, icon_color) 
        VALUES (%s, %s, %s, %s, %s, %s, '靓', '#FFD700')
        """
        
        cursor.executemany(insert_sql, lucky_numbers)
        conn.commit()
        
        print(f"✅ 成功添加 {len(lucky_numbers)} 条靓号数据")
        
        # 验证数据
        cursor.execute("SELECT COUNT(*) FROM lucky_numbers")
        count = cursor.fetchone()[0]
        print(f"✅ 数据库中现在有 {count} 条靓号数据")
        
        return True
        
    except mysql.connector.Error as e:
        print(f"❌ 数据库错误: {e}")
        return False
    except Exception as e:
        print(f"❌ 错误: {e}")
        return False
    finally:
        if 'cursor' in locals():
            cursor.close()
        if 'conn' in locals():
            conn.close()

if __name__ == "__main__":
    print("🚀 开始添加靓号测试数据...")
    success = add_lucky_numbers()
    if success:
        print("✅ 靓号数据添加完成！")
        sys.exit(0)
    else:
        print("❌ 靓号数据添加失败！")
        sys.exit(1)
