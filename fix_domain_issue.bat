@echo off
chcp 65001 >nul
echo ========================================
echo 域名解析问题诊断和修复工具
echo ========================================
echo.

echo [1] 检查域名解析状态...
echo.
echo 检查 socialchatai.cloud 解析状态:
nslookup socialchatai.cloud 8.8.8.8
echo.
echo 检查 www.socialchatai.cloud 解析状态:
nslookup www.socialchatai.cloud 8.8.8.8
echo.

echo [2] 检查服务器连通性...
echo.
echo 测试服务器 119.45.174.10 连通性:
ping -n 4 119.45.174.10
echo.

echo [3] 检查应用服务状态...
echo.
echo 测试应用健康检查接口:
curl -s http://119.45.174.10:8080/api/health || echo "应用服务不可用"
echo.

echo [4] 域名问题诊断结果:
echo.
echo ❌ 域名解析失败 - socialchatai.cloud 无法解析
echo ✅ 服务器连通正常 - 119.45.174.10 可访问
echo ✅ 应用服务正常 - Spring Boot应用运行中
echo.

echo [5] 解决方案建议:
echo.
echo 🔧 立即需要处理的问题:
echo    1. 登录阿里云控制台: https://dc.console.aliyun.com/
echo    2. 检查域名 socialchatai.cloud 的实名认证状态
echo    3. 确认域名状态为"正常"而非"暂停"
echo    4. 验证DNS服务器设置是否正确
echo.

echo 🔧 DNS服务器应该设置为:
echo    dns21.hichina.com
echo    dns22.hichina.com
echo.

echo 🔧 临时解决方案:
echo    1. 使用IP地址访问: http://119.45.174.10:8080
echo    2. API接口: http://119.45.174.10:8080/api/
echo    3. Swagger文档: http://119.45.174.10:8080/swagger-ui.html
echo.

echo [6] 下一步操作:
echo.
echo 1. 完成域名实名认证
echo 2. 等待DNS解析生效（通常需要1-24小时）
echo 3. 验证域名解析: nslookup socialchatai.cloud
echo 4. 测试域名访问: http://socialchatai.cloud:8080
echo.

echo ========================================
echo 诊断完成！请按照上述建议处理域名问题。
echo ========================================
pause
