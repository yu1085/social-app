#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import random

BASE_URL = "http://localhost:8080"

def send_verification_code(phone):
    """发送验证码"""
    response = requests.post(f"{BASE_URL}/api/auth/send-code", json={"phone": phone})
    if response.status_code == 200:
        print(f"✅ 验证码已发送到 {phone}")
        return True
    else:
        print(f"❌ 发送验证码失败: {response.text}")
        return False

def login_with_code(phone, code):
    """验证码登录"""
    login_data = {
        "phone": phone,
        "code": code
    }
    
    response = requests.post(f"{BASE_URL}/api/auth/login-with-code", json=login_data)
    if response.status_code == 200:
        return response.json()["data"]["token"]
    else:
        print(f"❌ 登录失败: {response.text}")
        return None

def create_post(token, content, location=None, image_url=None):
    """创建动态"""
    headers = {"Authorization": f"Bearer {token}"}
    post_data = {
        "content": content,
        "location": location or ""
    }
    
    if image_url:
        post_data["imageUrl"] = image_url
    
    response = requests.post(f"{BASE_URL}/api/posts", json=post_data, headers=headers)
    if response.status_code == 200:
        result = response.json()
        if result.get("success"):
            print(f"✅ 动态发布成功: {content[:30]}...")
            return result["data"]["id"]
        else:
            print(f"❌ 发布失败: {result.get('message', '未知错误')}")
            return None
    else:
        print(f"❌ 发布失败: {response.text}")
        return None

def get_posts(token):
    """获取动态列表"""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(f"{BASE_URL}/api/posts/enhanced?page=0&size=5", headers=headers)
    if response.status_code == 200:
        result = response.json()
        if result.get("success"):
            posts = result["data"]["content"]
            print(f"✅ 获取到 {len(posts)} 条动态")
            return posts
        else:
            print(f"❌ 获取动态失败: {result.get('message', '未知错误')}")
            return []
    else:
        print(f"❌ 获取动态失败: {response.text}")
        return []

def main():
    print("开始测试发布动态功能...")
    print("=" * 50)
    
    # 使用现有用户
    phone = "13900139000"
    
    # 发送验证码
    if not send_verification_code(phone):
        return
    
    # 等待用户输入验证码
    code = input("请输入收到的验证码: ")
    
    # 登录
    token = login_with_code(phone, code)
    if not token:
        print("❌ 登录失败，无法继续测试")
        return
    
    print("✅ 登录成功")
    print()
    
    # 测试发布不同类型的动态
    test_posts = [
        {
            "content": "测试发布功能 - 纯文本动态",
            "location": "北京市朝阳区",
            "image_url": None
        },
        {
            "content": "测试发布功能 - 带图片的动态",
            "location": "上海市浦东新区", 
            "image_url": "https://picsum.photos/400/300?random=100"
        },
        {
            "content": "测试发布功能 - 长文本动态，包含更多内容来测试字符限制功能是否正常工作",
            "location": "深圳市南山区",
            "image_url": "https://picsum.photos/400/300?random=101"
        }
    ]
    
    print("开始发布测试动态...")
    published_posts = []
    
    for i, post_data in enumerate(test_posts, 1):
        print(f"\n--- 测试动态 {i} ---")
        post_id = create_post(
            token=token,
            content=post_data["content"],
            location=post_data["location"],
            image_url=post_data["image_url"]
        )
        
        if post_id:
            published_posts.append(post_id)
            print(f"✅ 动态 {i} 发布成功，ID: {post_id}")
        else:
            print(f"❌ 动态 {i} 发布失败")
    
    print(f"\n成功发布 {len(published_posts)} 条测试动态")
    
    # 验证动态是否出现在列表中
    print("\n验证动态列表...")
    posts = get_posts(token)
    
    if posts:
        print("\n最新的动态列表:")
        for i, post in enumerate(posts[:3], 1):
            print(f"{i}. {post.get('content', '')[:50]}...")
            print(f"   位置: {post.get('location', '无')}")
            print(f"   图片: {'有' if post.get('imageUrl') else '无'}")
            print(f"   点赞: {post.get('likeCount', 0)}")
            print()
    
    print("=" * 50)
    print("测试完成！")

if __name__ == "__main__":
    main()
