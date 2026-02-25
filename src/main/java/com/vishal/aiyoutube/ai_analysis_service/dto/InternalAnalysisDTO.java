package com.vishal.aiyoutube.ai_analysis_service.dto;

import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object used for Spring AI Structured Output.
 * This class defines the exact JSON contract that the AI must fulfill
 * to ensure its response can be automatically mapped back to a Java object.
 */
@Data
public class InternalAnalysisDTO {

    /**
     * The consolidated executive summary of the research topic.
     */
    private String summary;

    /**
     * Quantitative tone of the video content (0.0 = Very Negative, 1.0 = Very Positive).
     */
    private Double sentiment;

    /**
     * Percentage (0-100) representing how much the different sources agree.
     */
    private Double consensus;

    /**
     * A list of recurring arguments or facts identified across all transcripts.
     */
    private List<String> claims;

    /**
     * Specific "Aha!" moments from the source videos used for evidence.
     */
    private List<Highlight> highlights;

    /**
     * Inner class representing a timestamped piece of evidence.
     * This maps directly to the 'Source Intelligence' cards on the UI.
     */
    @Data
    public static class Highlight {
        /** The unique identifier of the source YouTube video. */
        private String videoId;

        /** The exact time (e.g., "05:12") where the insight occurs. */
        private String timestamp;

        /** Why the AI thinks this specific clip is important. */
        private String explanation;

        /** A concise 1-sentence description of the segment. */
        private String shortSummary;
    }
}