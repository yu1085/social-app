@echo off
chcp 65001 >nul
echo ========================================
echo SocialMeet 统一管理脚本
echo ========================================
echo.

if "%1"=="" (
    echo 使用方法:
    echo   unified_management.bat [命令]
    echo.
    echo 可用命令:
    echo   start-backend    启动后端服务
    echo   start-emulator   启动Android模拟器
    echo   build-app        构建Android应用
    echo   install-app      安装Android应用
    echo   test-api         运行API测试
    echo   test-basic       运行基础测试
    echo   test-auth        运行认证测试
    echo   test-payment     运行支付测试
    echo   fix-device       修复设备连接问题
    echo   clean-build      清理构建文件
    echo   deploy           部署应用
    echo   help             显示帮助信息
    echo.
    pause
    exit /b 0
)

if "%1"=="help" (
    goto :show_help
)
if "%1"=="start-backend" (
    goto :start_backend
)
if "%1"=="start-emulator" (
    goto :start_emulator
)
if "%1"=="build-app" (
    goto :build_app
)
if "%1"=="install-app" (
    goto :install_app
)
if "%1"=="test-api" (
    goto :test_api
)
if "%1"=="test-basic" (
    goto :test_basic
)
if "%1"=="test-auth" (
    goto :test_auth
)
if "%1"=="test-payment" (
    goto :test_payment
)
if "%1"=="fix-device" (
    goto :fix_device
)
if "%1"=="clean-build" (
    goto :clean_build
)
if "%1"=="deploy" (
    goto :deploy
)

echo 未知命令: %1
echo 使用 'unified_management.bat help' 查看帮助
pause
exit /b 1

:show_help
echo ========================================
echo SocialMeet 统一管理脚本 - 帮助
echo ========================================
echo.
echo 后端管理:
echo   start-backend    启动Spring Boot后端服务
echo.
echo 模拟器管理:
echo   start-emulator   启动Android模拟器
echo.
echo 应用构建:
echo   build-app        构建Android应用
echo   install-app      安装Android应用到设备
echo.
echo 测试功能:
echo   test-api         运行完整API测试套件
echo   test-basic       运行基础连接测试
echo   test-auth        运行认证功能测试
echo   test-payment     运行支付功能测试
echo.
echo 维护功能:
echo   fix-device       修复ADB设备连接问题
echo   clean-build      清理所有构建文件
echo   deploy           部署应用到服务器
echo.
pause
exit /b 0

:start_backend
echo ========================================
echo 启动后端服务
echo ========================================
echo.
cd SocialMeet
echo 正在启动Spring Boot服务...
gradlew bootRun
pause
exit /b 0

:start_emulator
echo ========================================
echo 启动Android模拟器
echo ========================================
echo.
echo 检查ADB服务...
adb kill-server
timeout /t 2 /nobreak >nul
adb start-server
echo.
echo 启动模拟器...
emulator -avd Pixel_7_API_34
pause
exit /b 0

:build_app
echo ========================================
echo 构建Android应用
echo ========================================
echo.
echo 清理之前的构建...
gradlew clean
echo.
echo 构建应用...
gradlew assembleDebug
echo.
echo 构建完成！
pause
exit /b 0

:install_app
echo ========================================
echo 安装Android应用
echo ========================================
echo.
echo 检查设备连接...
adb devices
echo.
echo 安装应用...
adb install -r app\build\outputs\apk\debug\app-debug.apk
echo.
echo 启动应用...
adb shell am start -n com.example.myapplication/.MainActivity
echo.
echo 安装完成！
pause
exit /b 0

:test_api
echo ========================================
echo 运行API测试
echo ========================================
echo.
echo 检查Python环境...
python --version
if %errorlevel% neq 0 (
    echo 错误: 未找到Python环境，请先安装Python
    pause
    exit /b 1
)
echo.
echo 安装依赖...
pip install requests
echo.
echo 运行统一测试套件...
python unified_test_suite.py --verbose
echo.
echo 测试完成！
pause
exit /b 0

:test_basic
echo ========================================
echo 运行基础测试
echo ========================================
echo.
python unified_test_suite.py --test basic --verbose
pause
exit /b 0

:test_auth
echo ========================================
echo 运行认证测试
echo ========================================
echo.
python unified_test_suite.py --test auth --verbose
pause
exit /b 0

:test_payment
echo ========================================
echo 运行支付测试
echo ========================================
echo.
python unified_test_suite.py --test payment --verbose
pause
exit /b 0

:fix_device
echo ========================================
echo 修复设备连接问题
echo ========================================
echo.
echo 重启ADB服务...
adb kill-server
timeout /t 2 /nobreak >nul
adb start-server
echo.
echo 检查设备连接...
adb devices
echo.
echo 如果看到设备，强制停止应用...
adb shell am force-stop com.example.myapplication
echo.
echo 重新安装应用...
gradlew installDebug
echo.
echo 修复完成！
pause
exit /b 0

:clean_build
echo ========================================
echo 清理构建文件
echo ========================================
echo.
echo 清理Android构建...
gradlew clean
echo.
echo 清理后端构建...
cd SocialMeet
gradlew clean
cd ..
echo.
echo 删除临时文件...
if exist temp_apk rmdir /s /q temp_apk
if exist temp_debug.keystore del temp_debug.keystore
echo.
echo 清理完成！
pause
exit /b 0

:deploy
echo ========================================
echo 部署应用
echo ========================================
echo.
echo 构建后端...
cd SocialMeet
gradlew build
echo.
echo 构建前端...
cd ..
gradlew assembleRelease
echo.
echo 部署到服务器...
echo 请手动配置服务器部署信息
echo.
echo 部署完成！
pause
exit /b 0
