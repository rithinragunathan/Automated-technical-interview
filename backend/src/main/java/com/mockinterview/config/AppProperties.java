package com.mockinterview.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Sandbox sandbox = new Sandbox();
    private Ai ai = new Ai();
    private Scoring scoring = new Scoring();

    public Jwt getJwt() { return jwt; }
    public void setJwt(Jwt jwt) { this.jwt = jwt; }
    public Cors getCors() { return cors; }
    public void setCors(Cors cors) { this.cors = cors; }
    public Sandbox getSandbox() { return sandbox; }
    public void setSandbox(Sandbox sandbox) { this.sandbox = sandbox; }
    public Ai getAi() { return ai; }
    public void setAi(Ai ai) { this.ai = ai; }
    public Scoring getScoring() { return scoring; }
    public void setScoring(Scoring scoring) { this.scoring = scoring; }

    public static class Jwt {
        private String secret;
        private long expirationMs;
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getExpirationMs() { return expirationMs; }
        public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
    }

    public static class Cors {
        private List<String> allowedOrigins;
        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }
    }

    public static class Sandbox {
        private String dockerHost;
        private String javaImage;
        private int timeoutSeconds;
        private long memoryBytes;
        private long cpuPeriod;
        private long cpuQuota;
        private String workspaceBase;
        private String workspaceVolume;
        private String workerUrl;
        // getters and setters omitted for brevity, add if needed by reflection
        public String getDockerHost() { return dockerHost; }
        public void setDockerHost(String dockerHost) { this.dockerHost = dockerHost; }
        public String getJavaImage() { return javaImage; }
        public void setJavaImage(String javaImage) { this.javaImage = javaImage; }
        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
        public long getMemoryBytes() { return memoryBytes; }
        public void setMemoryBytes(long memoryBytes) { this.memoryBytes = memoryBytes; }
        public long getCpuPeriod() { return cpuPeriod; }
        public void setCpuPeriod(long cpuPeriod) { this.cpuPeriod = cpuPeriod; }
        public long getCpuQuota() { return cpuQuota; }
        public void setCpuQuota(long cpuQuota) { this.cpuQuota = cpuQuota; }
        public String getWorkspaceBase() { return workspaceBase; }
        public void setWorkspaceBase(String workspaceBase) { this.workspaceBase = workspaceBase; }
        public String getWorkspaceVolume() { return workspaceVolume; }
        public void setWorkspaceVolume(String workspaceVolume) { this.workspaceVolume = workspaceVolume; }
        public String getWorkerUrl() { return workerUrl; }
        public void setWorkerUrl(String workerUrl) { this.workerUrl = workerUrl; }
    }

    public static class Ai {
        private Gemini gemini = new Gemini();

        public Gemini getGemini() { return gemini; }
        public void setGemini(Gemini gemini) { this.gemini = gemini; }

        public static class Gemini {
            private String apiKey;
            private String baseUrl;
            private String model;

            public String getApiKey() { return apiKey; }
            public void setApiKey(String apiKey) { this.apiKey = apiKey; }
            public String getBaseUrl() { return baseUrl; }
            public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
            public String getModel() { return model; }
            public void setModel(String model) { this.model = model; }
        }
    }

    public static class Scoring {
        private Map<String, Double> weights;
        private Map<String, Double> codingWeights;
        private Map<String, Integer> thresholds;
        public Map<String, Double> getWeights() { return weights; }
        public void setWeights(Map<String, Double> weights) { this.weights = weights; }
        public Map<String, Double> getCodingWeights() { return codingWeights; }
        public void setCodingWeights(Map<String, Double> codingWeights) { this.codingWeights = codingWeights; }
        public Map<String, Integer> getThresholds() { return thresholds; }
        public void setThresholds(Map<String, Integer> thresholds) { this.thresholds = thresholds; }
    }
}
