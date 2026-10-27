package com.mockinterview.evaluation.provider;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMProviderFactory {

    private final LLMProvider geminiProvider;

    public LLMProviderFactory(@Qualifier("geminiProvider") LLMProvider geminiProvider) {
        this.geminiProvider = geminiProvider;
    }

    @Bean
    public LLMProvider llmProvider() {
        return geminiProvider;
    }
}
