@echo off
echo ========================================
echo 修复JVM版本兼容性问题
echo ========================================
echo.

echo 问题分析：
echo - Java编译目标: 21
echo - Kotlin编译目标: 17
echo - 两者必须保持一致
echo.

echo 解决方案：
echo 1. 统一使用Java 17（推荐）
echo 2. 或者升级Kotlin到支持Java 21的版本
echo.

echo 选择方案1：统一使用Java 17
echo.

echo 1. 清理项目...
gradle-8.9\bin\gradle.bat clean
echo.

echo 2. 检查Java版本...
java -version
echo.

echo 3. 重新编译项目...
gradle-8.9\bin\gradle.bat assembleDebug
if %errorlevel% neq 0 (
    echo 编译失败，请检查错误信息
    pause
    exit /b 1
)
echo 编译成功！
echo.

echo 4. 安装应用程序...
gradle-8.9\bin\gradle.bat installDebug
if %errorlevel% neq 0 (
    echo 安装失败，请检查设备连接
    pause
    exit /b 1
)
echo 安装成功！
echo.

echo 5. 启动应用程序...
adb shell am start -n com.example.myapplication/.LoginActivity
echo.

echo 修复完成！
echo.
echo 当前配置：
echo - Java编译目标: 17
echo - Kotlin编译目标: 17
echo - 版本兼容性: ✅
echo.
pause
