#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复 min_contribution 字段默认值问题
"""

import pymysql

def connect_to_database():
    """连接到数据库"""
    try:
        connection = pymysql.connect(
            host='localhost',
            user='root',
            password='123456',
            database='socialmeet',
            charset='utf8mb4',
            autocommit=True
        )
        print("✅ 数据库连接成功")
        return connection
    except Exception as e:
        print(f"❌ 数据库连接失败: {e}")
        return None

def fix_min_contribution_field(connection):
    """修复 min_contribution 字段默认值"""
    cursor = connection.cursor()
    
    try:
        print("\n🔧 修复 min_contribution 字段默认值...")
        
        # 为 min_contribution 字段添加默认值
        alter_sql = "ALTER TABLE wealth_levels ALTER COLUMN min_contribution SET DEFAULT 0.00"
        cursor.execute(alter_sql)
        print("✅ 为 min_contribution 添加默认值 0.00")
        
        # 为 max_contribution 字段添加默认值
        alter_sql = "ALTER TABLE wealth_levels ALTER COLUMN max_contribution SET DEFAULT 0.00"
        cursor.execute(alter_sql)
        print("✅ 为 max_contribution 添加默认值 0.00")
        
        # 为 name 字段添加默认值
        alter_sql = "ALTER TABLE wealth_levels ALTER COLUMN name SET DEFAULT ''"
        cursor.execute(alter_sql)
        print("✅ 为 name 添加默认值 ''")
        
        # 为 benefits 字段添加默认值
        alter_sql = "ALTER TABLE wealth_levels ALTER COLUMN benefits SET DEFAULT ''"
        cursor.execute(alter_sql)
        print("✅ 为 benefits 添加默认值 ''")
        
        print("\n✅ 所有字段默认值修复完成")
        
    except Exception as e:
        print(f"❌ 修复字段默认值时出错: {e}")
        return False
    finally:
        cursor.close()
    
    return True

def test_wealth_level_api():
    """测试财富等级API"""
    import requests
    
    try:
        print("\n🧪 测试财富等级API...")
        
        # 测试等级规则API
        response = requests.get("http://localhost:8080/api/wealth-level/rules", timeout=10)
        if response.status_code == 200:
            data = response.json()
            print(f"✅ 等级规则API正常，返回 {len(data.get('data', []))} 条记录")
        else:
            print(f"❌ 等级规则API失败: {response.status_code}")
        
        # 测试健康检查API
        response = requests.get("http://localhost:8080/actuator/health", timeout=10)
        if response.status_code == 200:
            print("✅ 健康检查API正常")
        else:
            print(f"❌ 健康检查API失败: {response.status_code}")
            
    except Exception as e:
        print(f"❌ API测试失败: {e}")

def main():
    """主函数"""
    print("🚀 开始修复 min_contribution 字段问题...")
    
    connection = connect_to_database()
    if not connection:
        print("❌ 无法连接到数据库，退出")
        return
    
    try:
        if fix_min_contribution_field(connection):
            print("\n🎉 字段修复成功！")
            
            # 等待服务启动
            print("\n⏳ 等待后端服务启动...")
            import time
            time.sleep(3)
            
            # 测试API
            test_wealth_level_api()
            
            print("\n✅ 修复完成！财富等级页面现在应该可以正常显示了。")
        else:
            print("❌ 字段修复失败")
            
    except Exception as e:
        print(f"❌ 修复过程中出现错误: {e}")
    finally:
        connection.close()
        print("🔒 数据库连接已关闭")

if __name__ == "__main__":
    main()
