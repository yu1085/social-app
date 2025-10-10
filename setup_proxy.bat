@echo off
REM 设置终端代理配置脚本
REM 使用方法：双击运行此批处理文件

echo 正在设置终端代理配置...

REM 设置HTTP代理环境变量
setx http_proxy "http://127.0.0.1:1087"
setx https_proxy "http://127.0.0.1:1087"

REM 设置当前会话的代理
set http_proxy=http://127.0.0.1:1087
set https_proxy=http://127.0.0.1:1087

echo.
echo 代理设置完成！
echo HTTP代理: %http_proxy%
echo HTTPS代理: %https_proxy%
echo.
echo 注意：请确保您的代理软件正在运行在 127.0.0.1:1087
echo.
pause
