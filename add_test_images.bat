@echo off
echo 正在为模拟器添加测试图片...

REM 创建测试图片目录
adb shell mkdir -p /sdcard/Pictures/TestImages

REM 创建一些简单的测试图片（使用base64编码的小图片）
echo 创建测试图片1...
adb shell "echo 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==' | base64 -d > /sdcard/Pictures/TestImages/test1.png"

echo 创建测试图片2...
adb shell "echo 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==' | base64 -d > /sdcard/Pictures/TestImages/test2.png"

echo 创建测试图片3...
adb shell "echo 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==' | base64 -d > /sdcard/Pictures/TestImages/test3.png"

echo 测试图片已添加到模拟器相册！
echo 现在可以在应用中选择"相册"选项来测试照片上传功能
pause
