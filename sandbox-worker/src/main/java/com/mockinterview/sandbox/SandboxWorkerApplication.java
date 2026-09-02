package com.mockinterview.sandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SandboxWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SandboxWorkerApplication.class, args);
    }
}
