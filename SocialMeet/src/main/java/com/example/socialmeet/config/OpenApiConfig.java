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
                        .title("SocialMeet API")
                        .description("社交交友应用后端API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SocialMeet Team")
                                .email("support@socialmeet.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://119.45.174.10:8080")
                                .description("生产服务器"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("本地开发服务器")
                ));
    }
}
