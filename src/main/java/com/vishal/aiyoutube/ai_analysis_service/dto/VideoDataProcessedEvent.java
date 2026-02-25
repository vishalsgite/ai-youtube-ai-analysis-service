package com.vishal.aiyoutube.ai_analysis_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for ingesting processed YouTube transcripts.
 * This model is optimized for a 'Chunked Streaming' architecture,
 * reducing memory overhead in the Kafka pipeline.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoDataProcessedEvent {

    /**
     * Unique identifier for the research topic (e.g., Budget 2026).
     * Used to aggregate multiple video events into a single AI synthesis.
     */
    private UUID topicId;

    /**
     * Encapsulates the core metadata and transcript for a single video.
     */
    private VideoTranscriptData videoData;

    /**
     * Progress tracker for the current video in the research batch.
     */
    private int currentCount;

    /**
     * Total number of videos expected for this topic.
     * Helps the AI service determine when to finalize the consensus summary.
     */
    private int totalVideos;

    /**
     * Inner class representing the extracted content of a YouTube video.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VideoTranscriptData {
        private String videoId;
        private String title;
        private String videoUrl;

        /**
         * Collection of timestamped text snippets used for AI analysis
         * and 'Source Intelligence' redirection.
         */
        private List<TranscriptSegmentDTO> segments;
    }

    /**
     * Represents a granular slice of the video transcript.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TranscriptSegmentDTO {
        /** Start time in seconds (e.g., 120.5 for 2:00.5). */
        private Double start;
        /** Raw spoken text content. */
        private String text;
    }
}