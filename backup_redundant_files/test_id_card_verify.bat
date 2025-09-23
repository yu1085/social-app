@echo off
echo ========================================
echo 身份证二要素核验功能测试
echo ========================================

echo.
echo 正在编译测试代码...
cd SocialMeet
javac -cp "src/main/java" src/main/java/com/example/socialmeet/controller/SimpleIdCardController.java

if %errorlevel% neq 0 (
    echo 编译失败！
    pause
    exit /b 1
)

echo 编译成功！
echo.

echo 正在运行测试...
java -cp "src/main/java" com.example.socialmeet.controller.SimpleIdCardController

echo.
echo 测试完成！
pause
