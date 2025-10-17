package com.socialmeet.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * SocialMeet 后端应用主类
 */
@SpringBootApplication
@EnableJpaAuditing
public class SocialMeetApplication {

    public static void main(String[] args) {

        SpringApplication.run(SocialMeetApplication.class, args);
        System.out.println("\n" +
                "========================================\n" +
                "   SocialMeet Backend Started!         \n" +
                "   服务地址: http://localhost:8080/api  \n" +
                "========================================\n");
    }
}
