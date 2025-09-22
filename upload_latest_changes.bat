@echo off
echo 正在上传最新的配置修改到服务器...

echo.
echo 步骤1: 停止服务器上的应用
ssh ubuntu@119.45.174.10 "sudo pkill -f socialmeet"

echo.
echo 步骤2: 上传修改后的SecurityConfig.java
scp src\main\java\com\example\socialmeet\config\SecurityConfig.java ubuntu@119.45.174.10:~/SocialMeet/src/main/java/com/example/socialmeet/config/

echo.
echo 步骤3: 上传OpenApiConfig.java
scp src\main\java\com\example\socialmeet\config\OpenApiConfig.java ubuntu@119.45.174.10:~/SocialMeet/src/main/java/com/example/socialmeet/config/

echo.
echo 步骤4: 上传SimpleSecurityConfig.java
scp src\main\java\com\example\socialmeet\config\SimpleSecurityConfig.java ubuntu@119.45.174.10:~/SocialMeet/src/main/java/com/example/socialmeet/config/

echo.
echo 步骤5: 上传修改后的build.gradle.kts
scp build.gradle.kts ubuntu@119.45.174.10:~/SocialMeet/

echo.
echo 步骤6: 上传修改后的application.properties
scp src\main\resources\application.properties ubuntu@119.45.174.10:~/SocialMeet/src/main/resources/

echo.
echo 步骤7: 在服务器上重新构建
ssh ubuntu@119.45.174.10 "cd ~/SocialMeet && sudo rm -rf build/ && ./gradlew build -x test --offline"

echo.
echo 步骤8: 启动应用
ssh ubuntu@119.45.174.10 "cd ~/SocialMeet && nohup java -jar build/libs/socialmeet-0.0.1-SNAPSHOT.jar > app.log 2>&1 &"

echo.
echo 步骤9: 等待应用启动
timeout /t 15 /nobreak

echo.
echo 步骤10: 检查应用状态
ssh ubuntu@119.45.174.10 "ps aux | grep java | grep socialmeet"

echo.
echo 上传完成！请访问以下链接测试：
echo - Swagger UI: http://119.45.174.10:8080/swagger-ui.html
echo - API文档: http://119.45.174.10:8080/api-docs
echo - 健康检查: http://119.45.174.10:8080/api/health
