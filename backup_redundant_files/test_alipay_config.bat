@echo off
echo ========================================
echo 测试支付宝API配置
echo ========================================

echo.
echo 1. 检查配置文件...
if exist "SocialMeet\src\main\resources\application-alipay-real.yml" (
    echo ✅ 配置文件存在
) else (
    echo ❌ 配置文件不存在
    pause
    exit /b 1
)

echo.
echo 2. 启动后端服务进行测试...
cd SocialMeet
echo 启动Spring Boot应用...
start "SocialMeet Backend" cmd /k "gradlew.bat bootRun --args='--spring.profiles.active=alipay-real'"

echo.
echo 等待服务启动...
timeout /t 10 /nobreak > nul

echo.
echo 3. 测试支付宝API连接...
curl -X GET "http://localhost:8080/api/test/alipay/connection" -H "Content-Type: application/json"

echo.
echo 4. 测试身份证二要素核验...
echo 请输入测试用的姓名和身份证号：
set /p test_name="姓名: "
set /p test_id="身份证号: "

curl -X POST "http://localhost:8080/api/test/alipay/verify?certName=%test_name%&certNo=%test_id%" -H "Content-Type: application/json"

echo.
echo ========================================
echo 测试完成！
echo ========================================
pause
