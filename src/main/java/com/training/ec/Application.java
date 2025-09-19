package com.training.ec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("Hello World!");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin"; // 実際のパスワード
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("ハッシュ化されたパスワード: " + encodedPassword);

        String rawPassword2 = "user"; // 実際のパスワード
        String encodedPassword2 = encoder.encode(rawPassword2);
        System.out.println("ハッシュ化されたパスワード一般: " + encodedPassword2);
    }
}
