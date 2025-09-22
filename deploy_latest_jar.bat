@echo off
echo 正在部署最新的JAR文件...

echo.
echo 步骤1: 停止服务器上的应用
ssh ubuntu@119.45.174.10 "sudo pkill -f socialmeet"

echo.
echo 步骤2: 上传最新的JAR文件
scp build\libs\socialmeet-0.0.1-SNAPSHOT.jar ubuntu@119.45.174.10:~/SocialMeet/build/libs/

echo.
echo 步骤3: 上传修复后的SecurityConfig.java
scp src\main\java\com\example\socialmeet\config\SecurityConfig.java ubuntu@119.45.174.10:~/SocialMeet/src/main/java/com/example/socialmeet/config/

echo.
echo 步骤4: 在服务器上启动应用
ssh ubuntu@119.45.174.10 "cd ~/SocialMeet && nohup java -jar build/libs/socialmeet-0.0.1-SNAPSHOT.jar > app.log 2>&1 &"

echo.
echo 步骤5: 等待应用启动
timeout /t 10 /nobreak

echo.
echo 步骤6: 检查应用状态
ssh ubuntu@119.45.174.10 "ps aux | grep java | grep socialmeet"

echo.
echo 部署完成！请访问以下链接测试：
echo - Swagger UI: http://119.45.174.10:8080/swagger-ui.html
echo - API文档: http://119.45.174.10:8080/api-docs
echo - 健康检查: http://119.45.174.10:8080/api/health
echo.
echo 如果仍有问题，请检查服务器日志：
echo ssh ubuntu@119.45.174.10 "tail -f ~/SocialMeet/app.log"
