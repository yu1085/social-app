@echo off
echo 测试用户资料API接口
echo ====================

echo 检查Python环境...
python --version
if %errorlevel% neq 0 (
    echo 错误: 未找到Python环境，请先安装Python
    pause
    exit /b 1
)

echo.
echo 安装依赖包...
pip install requests

echo.
echo 运行API测试...
python test_profile_api.py

echo.
echo 测试完成！
pause
