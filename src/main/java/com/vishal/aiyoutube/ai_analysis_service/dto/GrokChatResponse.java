package com.vishal.aiyoutube.ai_analysis_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object for parsing responses from AI providers (Groq/Grok).
 * Implements the standard OpenAI response schema to ensure compatibility
 * with multiple Large Language Model (LLM) providers.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrokChatResponse {

    /**
     * The list of generated completions.
     * Usually contains one main choice where the synthesized research
     * summary and consensus results are stored.
     */
    private List<Choice> choices;

    /**
     * Metadata tracking token consumption.
     * Critical for managing API quotas and monitoring the operational costs
     * of processing high-volume YouTube transcripts.
     */
    private Usage usage;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private Message message;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        /**
         * The role of the responder (typically 'assistant').
         */
        private String role;

        /**
         * The core intelligence: This contains the AI's final synthesis,
         * including the consensus summary and common claims for the topic.
         */
        private String content;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        /** Number of tokens in the input (YouTube transcripts + System Prompt). */
        private int prompt_tokens;

        /** Number of tokens generated in the AI's response. */
        private int completion_tokens;

        /** The total token count used for this specific research analysis. */
        private int total_tokens;
    }
}