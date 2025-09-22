@echo off
echo ========================================
echo 生成RSA2密钥对
echo ========================================

echo.
echo 正在生成应用私钥...
keytool -genkeypair -alias socialmeet -keyalg RSA -keysize 2048 -keystore socialmeet.jks -storepass 123456 -keypass 123456 -dname "CN=SocialMeet,OU=IT,O=SocialMeet,L=Beijing,S=Beijing,C=CN" -validity 3650

echo.
echo 正在导出应用私钥（PKCS8格式）...
keytool -importkeystore -srckeystore socialmeet.jks -destkeystore socialmeet.p12 -srcstoretype jks -deststoretype pkcs12 -srcstorepass 123456 -deststorepass 123456

echo.
echo 正在生成私钥文件...
openssl pkcs12 -in socialmeet.p12 -nocerts -nodes -out private_key.pem -passin pass:123456
openssl rsa -in private_key.pem -out private_key_pkcs8.pem -outform PEM

echo.
echo 正在生成公钥文件...
openssl rsa -in private_key.pem -pubout -out public_key.pem

echo.
echo ========================================
echo 密钥生成完成！
echo ========================================
echo.
echo 应用私钥文件: private_key_pkcs8.pem
echo 应用公钥文件: public_key.pem
echo.
echo 请将应用公钥上传到支付宝开放平台
echo 然后从开放平台获取支付宝公钥
echo.
pause
