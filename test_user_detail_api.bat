@echo off
echo ================================================================
echo 测试用户详情页API功能
echo ================================================================

echo.
echo 1. 测试获取video_caller用户详情 (ID: 23820512)
curl -X GET "http://localhost:8080/api/users/23820512" ^
     -H "Content-Type: application/json" ^
     -w "\nHTTP状态码: %{http_code}\n" ^
     -s

echo.
echo 2. 测试获取video_receiver用户详情 (ID: 22491729)
curl -X GET "http://localhost:8080/api/users/22491729" ^
     -H "Content-Type: application/json" ^
     -w "\nHTTP状态码: %{http_code}\n" ^
     -s

echo.
echo 3. 测试搜索用户列表
curl -X GET "http://localhost:8080/api/users/search?page=0&size=10" ^
     -H "Content-Type: application/json" ^
     -w "\nHTTP状态码: %{http_code}\n" ^
     -s

echo.
echo ================================================================
echo 测试完成
echo ================================================================
pause
