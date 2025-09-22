package com.example.socialmeet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.example.socialmeet.repository")
public class SocialMeetApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialMeetApplication.class, args);
	}

}
