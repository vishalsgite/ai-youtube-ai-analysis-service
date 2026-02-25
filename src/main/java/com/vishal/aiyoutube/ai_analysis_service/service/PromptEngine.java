package com.vishal.aiyoutube.ai_analysis_service.service;

import org.springframework.stereotype.Service;

/**
 * The PromptEngine handles the instructional design for the AI Agents.
 * Updated with strict formatting rules to prevent "AI Drift" where
 * dates were being returned instead of video offsets.
 */
@Service
public class PromptEngine implements IPromptEngine {

    /**
     * Stage 1: Granular Video Analysis Prompt.
     * Instructs the AI to extract specific insights and precise video offsets.
     */
    @Override
    public String buildAnalysisPrompt(String context) {
        return """
            SYSTEM INSTRUCTIONS:
            You are a Video Content Analyst. Your task is to extract the top 2 key insights from the provided transcript.
            
            STRICT RULES FOR DATA EXTRACTION:
            1. TIMESTAMP: This MUST be the time offset in the video (e.g., '02:15'). 
            2. NO DATES: Never use calendar dates (like '2022-06-20') in the timestamp field.
            3. FORMAT: If you cannot find a specific second, default to '00:00'.
            4. JSON ONLY: Return a raw JSON object matching the internal schema.

            TRANSCRIPT TO ANALYZE:
            %s
            """.formatted(context);
    }

    /**
     * Stage 2: Multi-Source Global Synthesis Prompt.
     * Instructs the AI to compare findings from 3 independent sources
     * to calculate a consensus score and final intelligence report.
     */
    @Override
    public String buildGlobalSynthesisPrompt(String partialSummaries) {
        /**
         * NOTE: We use double percentage '%%' to escape the literal '%'
         * character required for the String.formatted() method.
         */
        return """
            SYSTEM INSTRUCTIONS:
            You are a Lead Intelligence Editor. You have been provided with summaries from 3 independent video sources.
            
            YOUR TASK:
            1. Analyze the points of agreement and contradiction across all 3 sources.
            2. Write a professional Executive Summary of the findings.
            3. CONSENSUS SCORE: Provide a percentage (0-100) representing how much the sources agree with each other.
            4. SENTIMENT: Provide a score (0.0 to 1.0) where 1.0 is extremely positive.
            5. COMMON CLAIMS: List the factual statements that appeared in multiple sources.

            INPUT SUMMARIES:
            %s

            STRICT OUTPUT JSON FORMAT (No markdown, no backticks):
            {
              "summary": "The executive summary of all research findings...",
              "sentiment": 0.5,
              "consensus": 85.0,
              "claims": ["Fact A found in sources", "Fact B confirmed by multiple agents"],
              "highlights": []
            }
            """.formatted(partialSummaries);
    }
}