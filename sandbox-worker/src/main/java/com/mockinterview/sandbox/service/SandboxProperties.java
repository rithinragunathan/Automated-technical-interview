package com.mockinterview.sandbox.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.sandbox")
public class SandboxProperties {
    private String javaImage = "openjdk:21-alpine";
    private int timeoutSeconds = 10;
    private long memoryBytes = 268435456;
    private long cpuPeriod = 100000;
    private long cpuQuota = 50000;
    private String workspaceBase = "/tmp/sandbox";
    private long pidLimit = 50;
}
