@echo off
echo ========================================
echo 测试支付宝API
echo ========================================

echo.
echo 正在启动应用...
cd SocialMeet
gradlew bootRun --args='--spring.profiles.active=alipay-real'

echo.
echo 应用启动后，请访问以下URL进行测试：
echo.
echo 1. 测试API连接：
echo    http://localhost:8080/api/test/alipay/connection
echo.
echo 2. 测试身份证核验：
echo    http://localhost:8080/api/test/alipay/verify?certName=张三&certNo=110101199001011234
echo.
echo 3. 使用curl测试：
echo    curl -X POST "http://localhost:8080/api/test/alipay/verify?certName=张三&certNo=110101199001011234"
echo.
echo 4. 使用Postman测试：
echo    POST http://localhost:8080/api/test/alipay/verify
echo    参数: certName=张三, certNo=110101199001011234
echo.
pause
