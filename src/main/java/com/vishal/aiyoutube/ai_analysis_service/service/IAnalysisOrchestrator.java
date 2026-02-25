package com.vishal.aiyoutube.ai_analysis_service.service;

import com.vishal.aiyoutube.ai_analysis_service.dto.VideoDataProcessedEvent;
import java.util.UUID;

/**
 * Interface defining the orchestration logic for AI analysis.
 * Manages the transition from individual video processing to global consensus.
 */
public interface IAnalysisOrchestrator {

    /**
     * Processes an incoming video data event, manages state, and triggers final synthesis.
     * @param event The processed video data from the YouTube service.
     */
    void processAnalysis(VideoDataProcessedEvent event);
}
