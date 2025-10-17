@echo off
chcp 65001
echo ========================================
echo  SocialMeet API 测试脚本
echo ========================================
echo.

echo 【测试1】健康检查
curl -X GET "http://localhost:8080/api/auth/health"
echo.
echo.

timeout /t 2 >nul

echo 【测试2】发送验证码
curl -X POST "http://localhost:8080/api/auth/send-code?phone=19812342076"
echo.
echo.

timeout /t 2 >nul

echo 【测试3】验证码登录
curl -X POST "http://localhost:8080/api/auth/login-with-code?phone=19812342076&code=123456"
echo.
echo.

echo ========================================
echo  测试完成！
echo
echo  如果登录成功，你会看到 token 和 user 信息
echo  复制 token 用于后续需要认证的API调用
echo ========================================
echo.
pause
