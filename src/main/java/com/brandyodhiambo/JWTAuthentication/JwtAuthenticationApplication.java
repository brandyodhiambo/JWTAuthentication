package com.brandyodhiambo.JWTAuthentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class JwtAuthenticationApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JwtAuthenticationApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "8089"));
        app.run(args);
    }

}
