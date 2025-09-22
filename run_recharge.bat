@echo off
echo ========================================
echo 用户充值脚本
echo 给用户ID 65899032 和 44479883 各充值10000
echo ========================================
echo.

echo 检查Python环境...
python --version
if %errorlevel% neq 0 (
    echo 错误: 未找到Python环境，请先安装Python 3.6+
    pause
    exit /b 1
)

echo.
echo 安装Python依赖...
pip install -r requirements_recharge.txt

echo.
echo 执行充值操作...
python recharge_users.py

echo.
echo 操作完成！
pause
