@echo off
echo ========================================
echo 快速测试支付宝API配置
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
echo 2. 启动后端服务...
cd SocialMeet
echo 启动Spring Boot应用（使用alipay-real配置）...
start "SocialMeet Backend" cmd /k "gradlew.bat bootRun --args='--spring.profiles.active=alipay-real'"

echo.
echo 等待服务启动（15秒）...
timeout /t 15 /nobreak > nul

echo.
echo 3. 测试支付宝API连接...
echo 正在测试连接...
curl -X GET "http://localhost:8080/api/test/alipay/connection" -H "Content-Type: application/json"

echo.
echo 4. 测试身份证二要素核验（使用测试数据）...
echo 使用测试数据：张三，110101199001011234
curl -X POST "http://localhost:8080/api/test/alipay/verify?certName=张三&certNo=110101199001011234" -H "Content-Type: application/json"

echo.
echo ========================================
echo 测试完成！请查看上面的结果
echo ========================================
echo.
echo 如果看到错误，请检查：
echo 1. 支付宝公钥是否正确
echo 2. 应用私钥是否匹配
echo 3. API权限是否已申请
echo.
pause
