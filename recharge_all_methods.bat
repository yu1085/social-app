@echo off
echo ========================================
echo 用户充值脚本 - 多种方式
echo 给用户ID 65899032 和 44479883 各充值10000
echo ========================================
echo.

echo 请选择充值方式:
echo 1. 使用Python直接操作数据库
echo 2. 使用Spring Boot API
echo 3. 使用SQL脚本
echo 4. 全部执行
echo.

set /p choice=请输入选择 (1-4): 

if "%choice%"=="1" goto python_db
if "%choice%"=="2" goto api_method
if "%choice%"=="3" goto sql_method
if "%choice%"=="4" goto all_methods
goto invalid_choice

:python_db
echo.
echo ========================================
echo 方法1: Python直接操作数据库
echo ========================================
echo 检查Python环境...
python --version
if %errorlevel% neq 0 (
    echo 错误: 未找到Python环境
    pause
    exit /b 1
)

echo 安装依赖...
pip install -r requirements_recharge.txt

echo 执行充值...
python recharge_users.py
goto end

:api_method
echo.
echo ========================================
echo 方法2: Spring Boot API
echo ========================================
echo 检查Python环境...
python --version
if %errorlevel% neq 0 (
    echo 错误: 未找到Python环境
    pause
    exit /b 1
)

echo 检查Spring Boot服务...
curl -s http://localhost:8080/api/health >nul 2>&1
if %errorlevel% neq 0 (
    echo 警告: Spring Boot服务可能未运行
    echo 请先运行 start_backend.bat 启动服务
    echo.
    set /p continue=是否继续? (y/n): 
    if /i not "%continue%"=="y" goto end
)

echo 执行充值...
python recharge_api.py
goto end

:sql_method
echo.
echo ========================================
echo 方法3: SQL脚本
echo ========================================
echo 请手动执行以下SQL脚本:
echo.
echo 1. 连接到MySQL数据库:
echo    mysql -u root -p123456 socialmeet
echo.
echo 2. 执行SQL脚本:
echo    source recharge_users.sql
echo.
echo 或者使用MySQL客户端工具执行 recharge_users.sql 文件
goto end

:all_methods
echo.
echo ========================================
echo 执行所有充值方法
echo ========================================

echo.
echo --- 方法1: Python直接操作数据库 ---
python recharge_users.py

echo.
echo --- 方法2: Spring Boot API ---
python recharge_api.py

echo.
echo --- 方法3: SQL脚本 ---
echo SQL脚本已生成: recharge_users.sql
echo 请手动执行该脚本

goto end

:invalid_choice
echo 无效选择，请重新运行脚本
goto end

:end
echo.
echo ========================================
echo 操作完成！
echo ========================================
pause
