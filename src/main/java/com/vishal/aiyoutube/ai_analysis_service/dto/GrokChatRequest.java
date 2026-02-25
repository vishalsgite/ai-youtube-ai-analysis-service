package com.vishal.aiyoutube.ai_analysis_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object for sending prompts to the AI provider.
 * Follows the industry-standard Chat Completions API format, making your
 * system compatible with modern LLM providers like Groq, xAI, and OpenAI.
 */
@Data
@Builder
public class GrokChatRequest {

    /**
     * The unique identifier of the AI model to be used.
     * Examples: 'llama-3.3-70b-versatile' or 'grok-1'.
     */
    private String model;

    /**
     * The conversation history or context for the AI.
     * Contains the System Prompt (instructions) and User Prompt (transcript data).
     */
    private List<Message> messages;

    /**
     * Controls the creativity vs. accuracy of the AI output.
     * Value ranges from 0.0 (strictly factual) to 1.0 (highly creative).
     * For 'Consensus Analysis', a lower temperature is usually preferred.
     */
    private Double temperature;

    /**
     * Inner class representing a single message in the chat context.
     */
    @Data
    @Builder
    public static class Message {
        /**
         * The role of the message sender.
         * Possible values: 'system' (instructions), 'user' (data), 'assistant' (AI response).
         */
        private String role;

        /**
         * The actual text content of the message.
         * This includes the raw transcripts harvested from YouTube for analysis.
         */
        private String content;
    }
}

