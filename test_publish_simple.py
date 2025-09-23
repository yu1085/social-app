#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import json

BASE_URL = "http://localhost:8080"

def test_publish_with_existing_token():
    """使用现有token测试发布功能"""
    print("测试发布动态功能...")
    print("=" * 50)
    
    # 使用之前生成的token（从日志中获取）
    token = "eyJhbGciOiJIUzUxMiJ9.eyJ0eXBlIjoiYWNjZXNzIiwidXNlcklkIjo0NDQ3OTg4MywidXNlcm5hbWUiOiJ1c2VyXzEzOTAwMTM5MDAwIiwic3ViIjoidXNlcl8xMzkwMDEzOTAwMCIsImlhdCI6MTc1ODY2MjkwMiwiZXhwIjoxNzU4NzQ5MzAyfQ.x6ROkf0whKx_cKPSa6EbcUSYjiGgs8CKEyOcsD4ZCfQa29OTkDZREM7mmcw7ML4yerWuwxxlpmYnD3ZXIixhIA"
    
    headers = {"Authorization": f"Bearer {token}"}
    
    # 测试发布不同类型的动态
    test_posts = [
        {
            "content": "🧪 测试发布功能 - 纯文本动态",
            "location": "北京市朝阳区"
        },
        {
            "content": "📸 测试发布功能 - 带图片的动态",
            "location": "上海市浦东新区",
            "imageUrl": "https://picsum.photos/400/300?random=200"
        },
        {
            "content": "📝 测试发布功能 - 长文本动态，包含更多内容来测试字符限制功能是否正常工作，这个动态用来验证系统对长文本的处理能力。",
            "location": "深圳市南山区",
            "imageUrl": "https://picsum.photos/400/300?random=201"
        }
    ]
    
    print("开始发布测试动态...")
    published_posts = []
    
    for i, post_data in enumerate(test_posts, 1):
        print(f"\n--- 测试动态 {i} ---")
        print(f"内容: {post_data['content']}")
        print(f"位置: {post_data['location']}")
        if 'imageUrl' in post_data:
            print(f"图片: {post_data['imageUrl']}")
        
        response = requests.post(f"{BASE_URL}/api/posts", json=post_data, headers=headers)
        
        if response.status_code == 200:
            result = response.json()
            if result.get("success"):
                post_id = result["data"]["id"]
                published_posts.append(post_id)
                print(f"✅ 动态 {i} 发布成功，ID: {post_id}")
            else:
                print(f"❌ 发布失败: {result.get('message', '未知错误')}")
        else:
            print(f"❌ 发布失败: HTTP {response.status_code}")
            print(f"响应: {response.text}")
    
    print(f"\n成功发布 {len(published_posts)} 条测试动态")
    
    # 验证动态是否出现在列表中
    print("\n验证动态列表...")
    response = requests.get(f"{BASE_URL}/api/posts/enhanced?page=0&size=10", headers=headers)
    
    if response.status_code == 200:
        result = response.json()
        if result.get("success"):
            posts = result["data"]["content"]
            print(f"✅ 获取到 {len(posts)} 条动态")
            
            print("\n最新的动态列表:")
            for i, post in enumerate(posts[:5], 1):
                print(f"{i}. {post.get('content', '')[:50]}...")
                print(f"   位置: {post.get('location', '无')}")
                print(f"   图片: {'有' if post.get('imageUrl') else '无'}")
                print(f"   点赞: {post.get('likeCount', 0)}")
                print(f"   评论: {post.get('commentCount', 0)}")
                print()
        else:
            print(f"❌ 获取动态失败: {result.get('message', '未知错误')}")
    else:
        print(f"❌ 获取动态失败: HTTP {response.status_code}")
    
    print("=" * 50)
    print("测试完成！")

if __name__ == "__main__":
    test_publish_with_existing_token()
