package com.visitorfastpass.visitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VisitorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisitorServiceApplication.class, args);
    }
}
