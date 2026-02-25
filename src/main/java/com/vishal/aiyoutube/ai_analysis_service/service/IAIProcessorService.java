package com.vishal.aiyoutube.ai_analysis_service.service;

import com.vishal.aiyoutube.ai_analysis_service.dto.InternalAnalysisDTO;

/**
 * Interface for AI Processing operations.
 * Defines the contract for transforming raw string prompts into
 * structured InternalAnalysisDTO objects.
 */
public interface IAIProcessorService {

    /**
     * Executes the analysis of transcripts with built-in error handling and retries.
     * @param prompt The combined user prompt containing video transcripts.
     * @return A mapped InternalAnalysisDTO object.
     */
    InternalAnalysisDTO analyzeTranscripts(String prompt);
}