@echo off
echo 正在更新服务器文件...

REM 停止当前运行的应用
ssh ubuntu@119.45.174.10 "pkill -f socialmeet"

REM 上传修改后的文件
scp SocialMeet\src\main\java\com\example\socialmeet\config\SecurityConfig.java ubuntu@119.45.174.10:~/SocialMeet/src/main/java/com/example/socialmeet/config/
scp SocialMeet\src\main\java\com\example\socialmeet\config\OpenApiConfig.java ubuntu@119.45.174.10:~/SocialMeet/src/main/java/com/example/socialmeet/config/
scp SocialMeet\build.gradle.kts ubuntu@119.45.174.10:~/SocialMeet/
scp SocialMeet\src\main\resources\application.properties ubuntu@119.45.174.10:~/SocialMeet/src/main/resources/

REM 在服务器上重新构建和启动
ssh ubuntu@119.45.174.10 "cd ~/SocialMeet && ./gradlew clean build -x test && java -jar build/libs/socialmeet-0.0.1-SNAPSHOT.jar > app.log 2>&1 &"

echo 更新完成！
echo 请等待几秒钟让应用启动，然后访问: http://119.45.174.10:8080/swagger-ui.html
