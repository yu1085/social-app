#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
import random

# 数据库连接配置
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'socialmeet',
    'charset': 'utf8mb4'
}

def update_posts_with_images():
    """为动态添加图片URL"""
    try:
        # 连接数据库
        connection = pymysql.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        print("✅ 数据库连接成功")
        
        # 示例图片URL
        sample_images = [
            "https://picsum.photos/400/300?random=1",
            "https://picsum.photos/400/300?random=2", 
            "https://picsum.photos/400/300?random=3",
            "https://picsum.photos/400/300?random=4",
            "https://picsum.photos/400/300?random=5",
            "https://picsum.photos/400/300?random=6",
            "https://picsum.photos/400/300?random=7",
            "https://picsum.photos/400/300?random=8",
            "https://picsum.photos/400/300?random=9",
            "https://picsum.photos/400/300?random=10",
            None,  # 一些动态没有图片
            None,
            None,
        ]
        
        # 查询所有动态
        cursor.execute("SELECT id, content, image_url FROM posts WHERE is_active = 1 ORDER BY created_at DESC")
        posts = cursor.fetchall()
        
        print(f"找到 {len(posts)} 条动态")
        
        # 为动态添加图片
        success_count = 0
        for post in posts:
            post_id, content, current_image_url = post
            
            # 只更新没有图片的动态
            if current_image_url is None:
                image_url = random.choice(sample_images)
                if image_url:
                    cursor.execute(
                        "UPDATE posts SET image_url = %s WHERE id = %s",
                        (image_url, post_id)
                    )
                    success_count += 1
                    print(f"✅ 动态 {post_id} 添加图片: {image_url}")
        
        # 提交更改
        connection.commit()
        print(f"\n成功为 {success_count} 条动态添加了图片")
        
        # 查看更新结果
        cursor.execute("SELECT id, content, image_url FROM posts WHERE is_active = 1 ORDER BY created_at DESC LIMIT 5")
        updated_posts = cursor.fetchall()
        
        print("\n更新后的动态示例:")
        for post in updated_posts:
            post_id, content, image_url = post
            image_info = f" (图片: {image_url})" if image_url else " (无图片)"
            print(f"  {post_id}: {content[:30]}...{image_info}")
        
    except Exception as e:
        print(f"❌ 数据库操作失败: {e}")
    finally:
        if 'connection' in locals():
            connection.close()
            print("✅ 数据库连接已关闭")

if __name__ == "__main__":
    update_posts_with_images()
