#!/bin/bash

# SocialMeet 快速构建脚本 (Linux/macOS)

echo "================================"
echo "  SocialMeet 快速构建脚本"
echo "================================"
echo

echo "[INFO] 设置Gradle环境变量..."
export GRADLE_OPTS="-Xmx2048m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8"
export GRADLE_USER_HOME="/tmp/gradle"
mkdir -p "/tmp/gradle"

echo "[INFO] 清理之前的构建..."
./gradlew clean

echo "[INFO] 开始快速构建..."
./gradlew build -x test --parallel --daemon --build-cache

if [ $? -eq 0 ]; then
    echo
    echo "================================"
    echo "  ✅ 构建成功！"
    echo "================================"
    echo
    echo "构建产物位置："
    echo "  - JAR文件: build/libs/*.jar"
    echo "  - 构建目录: build/"
    echo
    echo "现在可以部署到服务器了！"
else
    echo
    echo "================================"
    echo "  ❌ 构建失败！"
    echo "================================"
    echo
    echo "请检查错误信息并重试"
fi
