#!/usr/bin/env python3
"""
创建中国移动应用图标
尺寸: 256x256px
格式: PNG
大小: < 50KB
"""

from PIL import Image, ImageDraw, ImageFont
import os

def create_app_icon():
    # 创建256x256的图片
    size = 256
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # 背景渐变 (中国移动蓝色主题)
    for y in range(size):
        color_value = int(255 * (1 - y / size * 0.3))  # 从深蓝到浅蓝
        color = (0, 120, 215, color_value)
        draw.line([(0, y), (size, y)], fill=color)
    
    # 绘制圆形背景
    margin = 20
    circle_size = size - margin * 2
    draw.ellipse([margin, margin, size-margin, size-margin], 
                fill=(255, 255, 255, 200), outline=(0, 120, 215, 255), width=3)
    
    # 绘制手机图标
    phone_width = 80
    phone_height = 120
    phone_x = (size - phone_width) // 2
    phone_y = (size - phone_height) // 2 - 10
    
    # 手机外框
    draw.rounded_rectangle([phone_x, phone_y, phone_x + phone_width, phone_y + phone_height], 
                         radius=15, fill=(0, 120, 215, 255))
    
    # 手机屏幕
    screen_margin = 8
    draw.rounded_rectangle([phone_x + screen_margin, phone_y + screen_margin, 
                          phone_x + phone_width - screen_margin, phone_y + phone_height - screen_margin], 
                         radius=10, fill=(255, 255, 255, 255))
    
    # 绘制认证图标 (勾选标记)
    check_size = 30
    check_x = phone_x + phone_width // 2 - check_size // 2
    check_y = phone_y + phone_height // 2 - check_size // 2
    
    # 勾选标记
    draw.line([check_x, check_y + check_size//2, 
              check_x + check_size//3, check_y + check_size*2//3], 
             fill=(0, 120, 215, 255), width=4)
    draw.line([check_x + check_size//3, check_y + check_size*2//3,
              check_x + check_size, check_y + check_size//3], 
             fill=(0, 120, 215, 255), width=4)
    
    # 添加文字 "认证"
    try:
        # 尝试使用系统字体
        font = ImageFont.truetype("arial.ttf", 24)
    except:
        # 使用默认字体
        font = ImageFont.load_default()
    
    text = "认证"
    text_bbox = draw.textbbox((0, 0), text, font=font)
    text_width = text_bbox[2] - text_bbox[0]
    text_height = text_bbox[3] - text_bbox[1]
    text_x = (size - text_width) // 2
    text_y = phone_y + phone_height + 15
    
    draw.text((text_x, text_y), text, fill=(0, 120, 215, 255), font=font)
    
    # 保存图片
    output_path = "app_icon_256.png"
    img.save(output_path, "PNG", optimize=True)
    
    # 检查文件大小
    file_size = os.path.getsize(output_path)
    print(f"图标已创建: {output_path}")
    print(f"尺寸: {size}x{size}px")
    print(f"文件大小: {file_size} bytes ({file_size/1024:.1f} KB)")
    
    if file_size > 50 * 1024:
        print("⚠️  文件大小超过50KB，需要优化")
    else:
        print("✅ 文件大小符合要求")
    
    return output_path

if __name__ == "__main__":
    create_app_icon()
