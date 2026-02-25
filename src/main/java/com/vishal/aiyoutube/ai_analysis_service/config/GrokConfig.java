package com.vishal.aiyoutube.ai_analysis_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import java.time.Duration;

/**
 * Configuration class for the AI Analysis Service's networking layer.
 * This class optimizes the WebClient specifically for the high-latency
 * nature of Large Language Model (LLM) processing.
 */
@Configuration
public class GrokConfig {

    /**
     * Creates a specialized WebClient bean for communicating with the Groq/Grok API.
     * * @param baseUrl The endpoint for the AI provider (injected from grok.base-url).
     * @param timeoutSeconds The maximum duration to wait for the AI to synthesize a response.
     * * KEY ARCHITECTURAL FEATURES:
     * 1. Reactive Netty HttpClient: Uses the underlying Netty engine to manage
     * asynchronous connections efficiently.
     * 2. Response Timeout: Crucial for AI services. Because synthesizing a full
     * consensus report can take 10-30 seconds, we override the default
     * short timeouts to prevent "ReadTimeoutExceptions".
     */
    @Bean
    public WebClient grokWebClient(
            @Value("${grok.base-url}") String baseUrl,
            @Value("${grok.timeout-seconds}") int timeoutSeconds) {

        // Build the HTTP client with a custom response timeout duration
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(timeoutSeconds));

        // Assemble the WebClient with the customized Reactor Netty connector
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}