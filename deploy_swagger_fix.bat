@echo off
echo 正在修复Swagger UI访问问题...

echo.
echo 步骤1: 停止服务器上的应用
ssh ubuntu@119.45.174.10 "pkill -f socialmeet"

echo.
echo 步骤2: 更新application.properties文件
ssh ubuntu@119.45.174.10 "cat >> ~/SocialMeet/src/main/resources/application.properties << 'EOF'

# Swagger/OpenAPI 配置
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
EOF"

echo.
echo 步骤3: 更新build.gradle.kts文件
ssh ubuntu@119.45.174.10 "sed -i '/runtimeOnly(\"com.mysql:mysql-connector-j\")/a\\    // Swagger/OpenAPI 文档\n    implementation(\"org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0\")' ~/SocialMeet/build.gradle.kts"

echo.
echo 步骤4: 创建OpenApiConfig.java文件
ssh ubuntu@119.45.174.10 "mkdir -p ~/SocialMeet/src/main/java/com/example/socialmeet/config"

ssh ubuntu@119.45.174.10 "cat > ~/SocialMeet/src/main/java/com/example/socialmeet/config/OpenApiConfig.java << 'EOF'
package com.example.socialmeet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(\"SocialMeet API\")
                        .description(\"社交交友应用后端API文档\")
                        .version(\"1.0.0\")
                        .contact(new Contact()
                                .name(\"SocialMeet Team\")
                                .email(\"support@socialmeet.com\"))
                        .license(new License()
                                .name(\"MIT License\")
                                .url(\"https://opensource.org/licenses/MIT\")))
                .servers(List.of(
                        new Server()
                                .url(\"http://119.45.174.10:8080\")
                                .description(\"生产服务器\"),
                        new Server()
                                .url(\"http://localhost:8080\")
                                .description(\"本地开发服务器\")
                ));
    }
}
EOF"

echo.
echo 步骤5: 重新构建应用
ssh ubuntu@119.45.174.10 "cd ~/SocialMeet && ./gradlew clean build -x test"

echo.
echo 步骤6: 启动应用
ssh ubuntu@119.45.174.10 "cd ~/SocialMeet && nohup java -jar build/libs/socialmeet-0.0.1-SNAPSHOT.jar > app.log 2>&1 &"

echo.
echo 步骤7: 等待应用启动
timeout /t 10 /nobreak

echo.
echo 步骤8: 检查应用状态
ssh ubuntu@119.45.174.10 "ps aux | grep java | grep socialmeet"

echo.
echo 步骤9: 测试Swagger UI访问
echo 正在测试 http://119.45.174.10:8080/swagger-ui.html ...

echo.
echo 部署完成！请访问以下链接：
echo - Swagger UI: http://119.45.174.10:8080/swagger-ui.html
echo - API文档: http://119.45.174.10:8080/api-docs
echo - 健康检查: http://119.45.174.10:8080/api/health
echo.
echo 如果仍有问题，请检查服务器日志：
echo ssh ubuntu@119.45.174.10 "tail -f ~/SocialMeet/app.log"
