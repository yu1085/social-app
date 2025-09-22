@echo off
echo 获取Android应用签名信息
echo.

echo 方法1: 使用keytool获取debug keystore签名
echo 默认debug keystore位置: %USERPROFILE%\.android\debug.keystore
echo.

if exist "%USERPROFILE%\.android\debug.keystore" (
    echo 找到debug keystore，获取签名信息...
    keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android
) else (
    echo 未找到debug keystore
)

echo.
echo 方法2: 使用jarsigner验证APK签名
echo APK文件: app\build\outputs\apk\debug\app-debug.apk
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    jarsigner -verify -verbose -certs app\build\outputs\apk\debug\app-debug.apk
) else (
    echo APK文件不存在
)

echo.
echo 方法3: 手动计算MD5签名
echo 如果你有keystore文件，可以使用以下命令:
echo keytool -exportcert -alias your_alias -keystore your_keystore | openssl dgst -md5
echo.
pause
