@echo off
echo ========================================
echo 测试消息通知流程
echo ========================================

echo.
echo 1. 检查后端服务状态...
curl -s http://localhost:8080/api/health > nul
if %errorlevel% neq 0 (
    echo ❌ 后端服务未运行，请先启动后端服务
    echo 运行: cd backend && mvn spring-boot:run
    pause
    exit /b 1
)
echo ✅ 后端服务运行正常

echo.
echo 2. 测试用户登录...
set /p USER1_TOKEN="请输入用户1的token (发起方): "
set /p USER2_TOKEN="请输入用户2的token (接收方): "

echo.
echo 3. 测试发起通话...
curl -X POST "http://localhost:8080/api/call/initiate" ^
  -H "Authorization: Bearer %USER1_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"receiverId\": 2, \"callType\": \"VIDEO\"}" ^
  -w "\nHTTP状态码: %%{http_code}\n" ^
  -s

echo.
echo 4. 等待5秒让通知发送...
timeout /t 5 /nobreak > nul

echo.
echo 5. 测试接受通话...
curl -X POST "http://localhost:8080/api/call/accept" ^
  -H "Authorization: Bearer %USER2_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"callSessionId\": \"CALL_test123\"}" ^
  -w "\nHTTP状态码: %%{http_code}\n" ^
  -s

echo.
echo 6. 等待3秒让状态通知发送...
timeout /t 3 /nobreak > nul

echo.
echo 7. 测试拒绝通话...
curl -X POST "http://localhost:8080/api/call/initiate" ^
  -H "Authorization: Bearer %USER1_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"receiverId\": 2, \"callType\": \"VOICE\"}" ^
  -w "\nHTTP状态码: %%{http_code}\n" ^
  -s

timeout /t 2 /nobreak > nul

curl -X POST "http://localhost:8080/api/call/reject" ^
  -H "Authorization: Bearer %USER2_TOKEN%" ^
  -H "Content-Type: application/json" ^
  -d "{\"callSessionId\": \"CALL_test456\"}" ^
  -w "\nHTTP状态码: %%{http_code}\n" ^
  -s

echo.
echo ========================================
echo 测试完成！
echo 请检查Android设备上的通知：
echo 1. 接收方应该收到来电通知
echo 2. 发起方应该收到接受/拒绝状态通知
echo ========================================
pause
