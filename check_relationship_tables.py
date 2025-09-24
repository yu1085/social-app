#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql

def check_relationship_tables():
    """检查关系相关的数据库表"""
    
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
            # 检查所有表
            cursor.execute("SHOW TABLES")
            tables = cursor.fetchall()
            
            print("=== 数据库中的所有表 ===")
            relationship_tables = []
            for table in tables:
                table_name = table[0]
                print(f"- {table_name}")
                if 'relationship' in table_name.lower() or 'follow' in table_name.lower():
                    relationship_tables.append(table_name)
            
            print(f"\n=== 关系相关的表 ===")
            if relationship_tables:
                for table in relationship_tables:
                    print(f"\n表: {table}")
                    cursor.execute(f"DESCRIBE {table}")
                    columns = cursor.fetchall()
                    for column in columns:
                        print(f"  {column[0]}: {column[1]} {column[2]} {column[3]} {column[4]} {column[5]}")
                    
                    # 查看表中的数据
                    cursor.execute(f"SELECT COUNT(*) FROM {table}")
                    count = cursor.fetchone()[0]
                    print(f"  数据行数: {count}")
            else:
                print("❌ 没有找到关系相关的表")
            
            # 检查是否有user_relationships表
            cursor.execute("SHOW TABLES LIKE 'user_relationships'")
            if cursor.fetchone():
                print("\n✅ user_relationships表存在")
                cursor.execute("SELECT * FROM user_relationships LIMIT 5")
                relationships = cursor.fetchall()
                if relationships:
                    print("关系数据示例:")
                    for rel in relationships:
                        print(f"  {rel}")
                else:
                    print("user_relationships表为空")
            else:
                print("\n❌ user_relationships表不存在")
            
            # 检查是否有follow_relationships表
            cursor.execute("SHOW TABLES LIKE 'follow_relationships'")
            if cursor.fetchone():
                print("\n✅ follow_relationships表存在")
                cursor.execute("SELECT * FROM follow_relationships LIMIT 5")
                follows = cursor.fetchall()
                if follows:
                    print("关注关系数据示例:")
                    for follow in follows:
                        print(f"  {follow}")
                else:
                    print("follow_relationships表为空")
            else:
                print("\n❌ follow_relationships表不存在")
        
        connection.close()
        
    except Exception as e:
        print(f"查询失败: {e}")

if __name__ == "__main__":
    check_relationship_tables()
