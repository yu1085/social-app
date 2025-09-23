#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成测试数据脚本
在数据库中插入真实的用户、动态、点赞和评论数据
"""

import requests
import json
import random
import time
from datetime import datetime, timedelta

BASE_URL = "http://localhost:8080"

def register_user(username, password, phone, gender="MALE"):
    """注册用户"""
    user_data = {
        "username": username,
        "password": password,
        "phone": phone,
        "gender": gender
    }
    
    response = requests.post(f"{BASE_URL}/api/auth/register", json=user_data)
    if response.status_code == 200:
        return response.json()["data"]["token"]
    else:
        print(f"注册用户失败: {username}, 错误: {response.text}")
        return None

def login_user(phone, password):
    """用户登录"""
    login_data = {
        "phone": phone,
        "password": password
    }
    
    response = requests.post(f"{BASE_URL}/api/auth/login", json=login_data)
    if response.status_code == 200:
        return response.json()["data"]["token"]
    else:
        print(f"登录失败: {phone}, 错误: {response.text}")
        return None

def create_post(token, content, location=None):
    """创建动态"""
    headers = {"Authorization": f"Bearer {token}"}
    post_data = {
        "content": content,
        "location": location
    }
    
    response = requests.post(f"{BASE_URL}/api/posts", json=post_data, headers=headers)
    if response.status_code == 200:
        return response.json()["data"]["id"]
    else:
        print(f"创建动态失败: {response.text}")
        return None

def like_post(token, post_id):
    """点赞动态"""
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.post(f"{BASE_URL}/api/posts/{post_id}/toggle-like", headers=headers)
    return response.status_code == 200

def add_comment(token, post_id, content, parent_id=None):
    """添加评论"""
    headers = {"Authorization": f"Bearer {token}"}
    params = {"content": content}
    if parent_id:
        params["parentId"] = parent_id
    
    response = requests.post(f"{BASE_URL}/api/posts/{post_id}/comments", params=params, headers=headers)
    return response.status_code == 200

def generate_test_data():
    """生成测试数据"""
    print("开始生成测试数据...")
    
    # 生成随机手机号
    def generate_phone():
        return f"1{random.randint(3, 9)}{random.randint(10000000, 99999999)}"
    
    # 测试用户数据
    users = []
    for i in range(8):
        username = f"测试用户{i+1}"
        phone = generate_phone()
        gender = random.choice(["MALE", "FEMALE"])
        users.append({"username": username, "phone": phone, "gender": gender})
    
    # 动态内容模板
    post_contents = [
        "今天天气真好，适合出去走走！",
        "刚看完一部很棒的电影，推荐给大家",
        "周末和朋友聚餐，聊得很开心",
        "新买的衣服到了，很满意！",
        "今天学会了做一道新菜，味道不错",
        "工作虽然累，但很有成就感",
        "和朋友一起爬山，风景太美了",
        "今天读了一本好书，收获很多",
        "刚完成了一个项目，庆祝一下",
        "周末在家休息，看看书听听音乐",
        "今天去了一家新开的咖啡店，环境很好",
        "和朋友一起打游戏，玩得很开心",
        "今天锻炼了身体，感觉很有活力",
        "刚看了一场精彩的演出，很震撼",
        "今天尝试了新的运动，很有趣",
    ]
    
    # 评论内容模板
    comment_contents = [
        "说得对！",
        "我也这么觉得",
        "哈哈，有趣",
        "太棒了！",
        "羡慕你",
        "我也想去",
        "加油！",
        "支持你",
        "很有道理",
        "学到了",
        "确实如此",
        "我也经历过",
        "太厉害了",
        "赞一个",
        "不错不错",
    ]
    
    # 位置数据
    locations = [
        "北京市朝阳区",
        "上海市浦东新区", 
        "广州市天河区",
        "深圳市南山区",
        "杭州市西湖区",
        "成都市锦江区",
        "武汉市江汉区",
        "西安市雁塔区",
        "南京市鼓楼区",
        "重庆市渝中区",
    ]
    
    # 注册用户并获取token
    user_tokens = {}
    for user in users:
        token = register_user(user["username"], "123456", user["phone"], user["gender"])
        if token:
            user_tokens[user["username"]] = token
            print(f"✅ 用户 {user['username']} 注册成功")
        time.sleep(0.5)  # 避免请求过快
    
    print(f"\n成功注册 {len(user_tokens)} 个用户")
    
    # 创建动态
    post_ids = []
    for i, (username, token) in enumerate(user_tokens.items()):
        # 每个用户创建2-4条动态
        num_posts = random.randint(2, 4)
        for j in range(num_posts):
            content = random.choice(post_contents)
            location = random.choice(locations)
            
            post_id = create_post(token, content, location)
            if post_id:
                post_ids.append(post_id)
                print(f"✅ 用户 {username} 创建动态: {content[:20]}...")
            time.sleep(0.3)
    
    print(f"\n成功创建 {len(post_ids)} 条动态")
    
    # 生成点赞数据
    like_count = 0
    for post_id in post_ids:
        # 随机选择一些用户来点赞
        likers = random.sample(list(user_tokens.keys()), random.randint(1, 4))
        for liker in likers:
            if like_post(user_tokens[liker], post_id):
                like_count += 1
        time.sleep(0.2)
    
    print(f"✅ 生成了 {like_count} 个点赞")
    
    # 生成评论数据
    comment_count = 0
    for post_id in post_ids:
        # 随机选择一些用户来评论
        commenters = random.sample(list(user_tokens.keys()), random.randint(1, 3))
        for commenter in commenters:
            content = random.choice(comment_contents)
            if add_comment(user_tokens[commenter], post_id, content):
                comment_count += 1
        time.sleep(0.2)
    
    print(f"✅ 生成了 {comment_count} 条评论")
    
    # 测试获取动态列表
    print("\n测试获取动态列表...")
    test_token = list(user_tokens.values())[0]
    headers = {"Authorization": f"Bearer {test_token}"}
    
    response = requests.get(f"{BASE_URL}/api/posts/enhanced?filter=nearby&page=0&size=10", headers=headers)
    if response.status_code == 200:
        data = response.json()
        if data.get('data') and data['data'].get('content'):
            posts = data['data']['content']
            print(f"✅ 成功获取 {len(posts)} 条动态")
            for i, post in enumerate(posts[:3]):  # 显示前3条
                print(f"  {i+1}. {post.get('userName', '未知')}: {post.get('content', '无内容')[:30]}...")
        else:
            print("❌ 动态列表为空")
    else:
        print(f"❌ 获取动态列表失败: {response.text}")
    
    print("\n🎉 测试数据生成完成！")
    print(f"生成了 {len(user_tokens)} 个用户，{len(post_ids)} 条动态，{like_count} 个点赞，{comment_count} 条评论")

if __name__ == "__main__":
    generate_test_data()
