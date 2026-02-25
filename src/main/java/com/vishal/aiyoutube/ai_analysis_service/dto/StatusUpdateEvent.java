package com.vishal.aiyoutube.ai_analysis_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Data Transfer Object for real-time pipeline status communication.
 * This event is published to the 'topic-status-updates' Kafka topic
 * to keep the user informed during the heavy AI synthesis phase.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateEvent {

    /**
     * Unique identifier for the research topic.
     * Links this update to the specific user request (e.g., "Budget 2026 Analysis").
     */
    private UUID topicId;

    /**
     * The current lifecycle state of the AI Analysis.
     * Common values:
     * - "ANALYZING": AI is currently reading transcripts.
     * - "COMPLETED": Final report generated and sent.
     * - "FAILED": Error during AI synthesis (e.g., API timeout).
     */
    private String status;

    /**
     * A human-readable message displayed on the frontend dashboard.
     * Example: "Llama-3 is comparing 5 video transcripts for consensus..."
     */
    private String message;
}