package com.mockinterview.evaluation.provider;

import com.mockinterview.config.AppProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMProviderFactory {

    private final AppProperties appProperties;
    private final LLMProvider mockProvider;
    private final LLMProvider openAIProvider;

    public LLMProviderFactory(AppProperties appProperties, 
                              @Qualifier("mockLLMProvider") LLMProvider mockProvider, 
                              @Qualifier("openAIProvider") LLMProvider openAIProvider) {
        this.appProperties = appProperties;
        this.mockProvider = mockProvider;
        this.openAIProvider = openAIProvider;
    }

    @Bean
    public LLMProvider llmProvider() {
        if ("openai".equalsIgnoreCase(appProperties.getAi().getProvider())) {
            return openAIProvider;
        }
        return mockProvider;
    }
}
