#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import random

BASE_URL = "http://localhost:8080"

def send_verification_code(phone):
    """发送验证码"""
    response = requests.post(f"{BASE_URL}/api/auth/send-code", json={"phone": phone})
    if response.status_code == 200:
        print(f"验证码已发送到 {phone}")
        return True
    else:
        print(f"发送验证码失败: {response.text}")
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
        print(f"登录失败: {response.text}")
        return None

def get_posts(token):
    """获取动态列表"""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(f"{BASE_URL}/api/posts/enhanced?page=0&size=50", headers=headers)
    if response.status_code == 200:
        return response.json()["data"]["content"]
    else:
        print(f"获取动态失败: {response.text}")
        return []

def update_post_image(token, post_id, image_url):
    """更新动态图片"""
    headers = {"Authorization": f"Bearer {token}"}
    update_data = {
        "imageUrl": image_url
    }
    
    response = requests.put(f"{BASE_URL}/api/posts/{post_id}", json=update_data, headers=headers)
    return response.status_code == 200

def main():
    print("开始为现有动态添加图片...")
    
    # 发送验证码
    phone = "13900139000"
    if not send_verification_code(phone):
        return
    
    # 等待用户输入验证码
    code = input("请输入收到的验证码: ")
    
    # 登录
    token = login_with_code(phone, code)
    if not token:
        print("登录失败，无法继续")
        return
    
    print("✅ 登录成功")
    
    # 获取现有动态
    posts = get_posts(token)
    if not posts:
        print("没有找到动态")
        return
    
    print(f"找到 {len(posts)} 条动态")
    
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
    
    # 为动态添加图片
    success_count = 0
    for post in posts:
        if post.get("imageUrl") is None:  # 只更新没有图片的动态
            image_url = random.choice(sample_images)
            if image_url:
                if update_post_image(token, post["id"], image_url):
                    success_count += 1
                    print(f"✅ 动态 {post['id']} 添加图片: {image_url}")
                else:
                    print(f"❌ 动态 {post['id']} 添加图片失败")
    
    print(f"\n成功为 {success_count} 条动态添加了图片")

if __name__ == "__main__":
    main()
