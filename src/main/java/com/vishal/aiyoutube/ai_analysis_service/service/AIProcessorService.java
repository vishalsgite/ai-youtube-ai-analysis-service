package com.vishal.aiyoutube.ai_analysis_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishal.aiyoutube.ai_analysis_service.config.GrokClient;
import com.vishal.aiyoutube.ai_analysis_service.dto.InternalAnalysisDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AIProcessorService is the 'Synthesizer' of the analysis service.
 * It manages the conversation with Groq Cloud and implements a resilient retry strategy.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIProcessorService implements IAIProcessorService {

    private final GrokClient grokClient;
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 30000;

    @Override
    public InternalAnalysisDTO analyzeTranscripts(String prompt) {
        log.info("Executing AI Analysis request for Groq Cloud...");

        /**
         * MANUAL SCHEMA DEFINITION:
         * Since we removed Spring AI dependencies, we define the expected JSON structure
         * manually. This tells the LLM exactly what fields to return.
         */
        String jsonSchema = """
                {
                  "summary": "string",
                  "sentiment": 0.0,
                  "consensus": 0.0,
                  "claims": ["string"],
                  "highlights": [
                    {
                      "videoId": "string",
                      "timestamp": "string",
                      "explanation": "string",
                      "shortSummary": "string"
                    }
                  ]
                }
                """;

        String systemPrompt = "You are a professional News and Content Analyst. " +
                "Analyze the provided video data and return a structured JSON report. " +
                "\nRULES: " +
                "\n1. Return ONLY valid JSON. " +
                "\n2. Do not include markdown formatting or backticks. " +
                "\n3. Use this exact JSON structure: \n" + jsonSchema;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String rawResponse = grokClient.chat(systemPrompt, prompt);
                String sanitizedJson = extractJson(rawResponse);
                return objectMapper.readValue(sanitizedJson, InternalAnalysisDTO.class);

            } catch (Exception e) {
                log.warn("Attempt {} failed for AI Synthesis: {}", attempt, e.getMessage());

                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    log.error("AI Analysis failed after {} retries.", MAX_RETRIES);
                    throw new RuntimeException("AI processing failure: " + e.getMessage());
                }
            }
        }
        throw new RuntimeException("Unexpected AI Service Error.");
    }

    private String extractJson(String rawResponse) {
        if (rawResponse == null || rawResponse.isEmpty()) throw new RuntimeException("Empty AI response");
        try {
            int startIndex = rawResponse.indexOf("{");
            int endIndex = rawResponse.lastIndexOf("}");
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                return rawResponse.substring(startIndex, endIndex + 1);
            }
        } catch (Exception e) {
            log.error("JSON Extraction failed.");
        }
        return rawResponse;
    }
}