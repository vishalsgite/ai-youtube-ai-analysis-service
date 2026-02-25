package com.vishal.aiyoutube.ai_analysis_service.service;

/**
 * Interface for generating LLM instructions.
 * Encapsulates the prompt engineering logic to ensure consistent
 * AI behavior across different research topics.
 */
public interface IPromptEngine {

    /**
     * Builds the prompt for analyzing an individual video transcript.
     */
    String buildAnalysisPrompt(String context);

    /**
     * Builds the prompt for synthesizing multiple partial summaries into a final report.
     */
    String buildGlobalSynthesisPrompt(String partialSummaries);
}