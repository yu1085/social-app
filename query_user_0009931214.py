#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
查询用户0009931214的数据库数据
"""

import pymysql
import json
from datetime import datetime

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'socialmeet',
    'charset': 'utf8mb4'
}

def query_user_data():
    """查询用户0009931214的完整数据"""
    print("=" * 60)
    print("查询用户0009931214的数据库数据")
    print("=" * 60)
    
    try:
        # 连接数据库
        connection = pymysql.connect(**DB_CONFIG)
        cursor = connection.cursor(pymysql.cursors.DictCursor)
        
        print(f"✅ 数据库连接成功")
        print(f"查询时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        
        # 查询用户基本信息
        print(f"\n1. 查询用户基本信息...")
        cursor.execute("""
            SELECT * FROM users 
            WHERE id = %s OR username = %s OR phone = %s
        """, (9931214, '0009931214', '0009931214'))
        
        user_data = cursor.fetchone()
        
        if user_data:
            print(f"✅ 找到用户数据:")
            print(f"   用户ID: {user_data.get('id')}")
            print(f"   用户名: {user_data.get('username')}")
            print(f"   手机号: {user_data.get('phone')}")
            print(f"   邮箱: {user_data.get('email')}")
            print(f"   昵称: {user_data.get('nickname')}")
            print(f"   头像: {user_data.get('avatar_url')}")
            print(f"   性别: {user_data.get('gender')}")
            print(f"   年龄: {user_data.get('age')}")
            print(f"   生日: {user_data.get('birth_date')}")
            print(f"   位置: {user_data.get('location')}")
            print(f"   个人简介: {user_data.get('bio')}")
            print(f"   是否激活: {user_data.get('is_active')}")
            print(f"   是否在线: {user_data.get('is_online')}")
            print(f"   创建时间: {user_data.get('created_at')}")
            print(f"   更新时间: {user_data.get('updated_at')}")
            
            user_id = user_data.get('id')
            
            # 查询用户钱包信息
            print(f"\n2. 查询用户钱包信息...")
            cursor.execute("""
                SELECT * FROM wallets 
                WHERE user_id = %s
            """, (user_id,))
            
            wallet_data = cursor.fetchone()
            
            if wallet_data:
                print(f"✅ 找到钱包数据:")
                print(f"   钱包ID: {wallet_data.get('id')}")
                print(f"   用户ID: {wallet_data.get('user_id')}")
                print(f"   余额: {wallet_data.get('balance')}")
                print(f"   冻结金额: {wallet_data.get('frozen_amount')}")
                print(f"   货币: {wallet_data.get('currency')}")
                print(f"   创建时间: {wallet_data.get('created_at')}")
                print(f"   更新时间: {wallet_data.get('updated_at')}")
            else:
                print(f"❌ 未找到钱包数据")
            
            # 查询用户交易记录
            print(f"\n3. 查询用户交易记录...")
            cursor.execute("""
                SELECT * FROM transactions 
                WHERE user_id = %s
                ORDER BY created_at DESC
                LIMIT 10
            """, (user_id,))
            
            transactions = cursor.fetchall()
            
            if transactions:
                print(f"✅ 找到{len(transactions)}条交易记录:")
                for i, transaction in enumerate(transactions, 1):
                    print(f"   交易{i}:")
                    print(f"     交易ID: {transaction.get('id')}")
                    print(f"     类型: {transaction.get('type')}")
                    print(f"     金额: {transaction.get('amount')}")
                    print(f"     描述: {transaction.get('description')}")
                    print(f"     状态: {transaction.get('status')}")
                    print(f"     创建时间: {transaction.get('created_at')}")
                    print(f"     ---")
            else:
                print(f"❌ 未找到交易记录")
            
            # 查询用户VIP信息
            print(f"\n4. 查询用户VIP信息...")
            cursor.execute("""
                SELECT * FROM user_vip 
                WHERE user_id = %s
            """, (user_id,))
            
            vip_data = cursor.fetchone()
            
            if vip_data:
                print(f"✅ 找到VIP数据:")
                print(f"   VIP等级: {vip_data.get('vip_level')}")
                print(f"   到期时间: {vip_data.get('expires_at')}")
                print(f"   创建时间: {vip_data.get('created_at')}")
            else:
                print(f"❌ 未找到VIP数据")
            
            # 查询用户财富等级
            print(f"\n5. 查询用户财富等级...")
            cursor.execute("""
                SELECT * FROM user_wealth_level 
                WHERE user_id = %s
            """, (user_id,))
            
            wealth_data = cursor.fetchone()
            
            if wealth_data:
                print(f"✅ 找到财富等级数据:")
                print(f"   财富等级: {wealth_data.get('wealth_level')}")
                print(f"   总消费: {wealth_data.get('total_spent')}")
                print(f"   创建时间: {wealth_data.get('created_at')}")
            else:
                print(f"❌ 未找到财富等级数据")
            
            # 生成完整数据报告
            print(f"\n" + "=" * 60)
            print(f"用户0009931214完整数据报告")
            print(f"=" * 60)
            
            report = {
                "user_id": user_id,
                "username": user_data.get('username'),
                "phone": user_data.get('phone'),
                "email": user_data.get('email'),
                "nickname": user_data.get('nickname'),
                "avatar_url": user_data.get('avatar_url'),
                "gender": user_data.get('gender'),
                "age": user_data.get('age'),
                "birth_date": str(user_data.get('birth_date')) if user_data.get('birth_date') else None,
                "location": user_data.get('location'),
                "bio": user_data.get('bio'),
                "is_active": bool(user_data.get('is_active')),
                "is_online": bool(user_data.get('is_online')),
                "created_at": str(user_data.get('created_at')),
                "updated_at": str(user_data.get('updated_at')),
                "wallet": {
                    "id": wallet_data.get('id') if wallet_data else None,
                    "balance": float(wallet_data.get('balance')) if wallet_data else 0.0,
                    "frozen_amount": float(wallet_data.get('frozen_amount')) if wallet_data else 0.0,
                    "currency": wallet_data.get('currency') if wallet_data else None
                } if wallet_data else None,
                "transactions": [
                    {
                        "id": t.get('id'),
                        "type": t.get('type'),
                        "amount": float(t.get('amount')),
                        "description": t.get('description'),
                        "status": t.get('status'),
                        "created_at": str(t.get('created_at'))
                    } for t in transactions
                ] if transactions else [],
                "vip": {
                    "vip_level": vip_data.get('vip_level'),
                    "expires_at": str(vip_data.get('expires_at')) if vip_data and vip_data.get('expires_at') else None
                } if vip_data else None,
                "wealth_level": {
                    "wealth_level": wealth_data.get('wealth_level'),
                    "total_spent": float(wealth_data.get('total_spent')) if wealth_data else 0.0
                } if wealth_data else None
            }
            
            # 保存报告到文件
            with open('user_0009931214_report.json', 'w', encoding='utf-8') as f:
                json.dump(report, f, ensure_ascii=False, indent=2)
            
            print(f"✅ 完整数据报告已保存到 user_0009931214_report.json")
            print(f"   用户状态: {'在线' if report['is_online'] else '离线'}")
            print(f"   账户余额: {report['wallet']['balance'] if report['wallet'] else 0.0} {report['wallet']['currency'] if report['wallet'] else 'CNY'}")
            print(f"   交易记录数: {len(report['transactions'])}")
            print(f"   VIP状态: {'是' if report['vip'] else '否'}")
            print(f"   财富等级: {report['wealth_level']['wealth_level'] if report['wealth_level'] else '无'}")
            
        else:
            print(f"❌ 未找到用户0009931214的数据")
            print(f"   可能的原因:")
            print(f"   - 用户ID不存在")
            print(f"   - 用户名不匹配")
            print(f"   - 手机号不匹配")
            
            # 查询所有用户ID
            print(f"\n查询所有用户ID...")
            cursor.execute("SELECT id, username, phone FROM users ORDER BY id")
            all_users = cursor.fetchall()
            
            if all_users:
                print(f"数据库中的所有用户:")
                for user in all_users:
                    print(f"   ID: {user.get('id')}, 用户名: {user.get('username')}, 手机: {user.get('phone')}")
            else:
                print(f"❌ 数据库中没有任何用户数据")
        
    except Exception as e:
        print(f"❌ 查询失败: {e}")
        import traceback
        traceback.print_exc()
    
    finally:
        if 'connection' in locals():
            connection.close()
            print(f"\n✅ 数据库连接已关闭")

if __name__ == "__main__":
    query_user_data()
