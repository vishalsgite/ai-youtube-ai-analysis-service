package com.vishal.aiyoutube.ai_analysis_service.consumer;

import com.vishal.aiyoutube.ai_analysis_service.dto.VideoDataProcessedEvent;
import com.vishal.aiyoutube.ai_analysis_service.service.AnalysisOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Entry point for Service 3 (AI Analysis Service).
 * This consumer acts as the 'Gateway' that receives processed video data from
 * the YouTube Processing Service via Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoDataConsumer {

    private final AnalysisOrchestrator analysisOrchestrator;

    /**
     * Consumes the processed video data and transcripts.
     * * @KafkaListener: Subscribes to the 'video-data-processed-events' topic.
     * * KEY ARCHITECTURAL FEATURES:
     * 1. Streaming Ingestion: Processes one video at a time as they arrive,
     * facilitating real-time "On-the-Spot" synthesis.
     * 2. Deserialization: Uses the custom 'kafkaListenerContainerFactory' to
     * resolve cross-service package mapping issues.
     */
    @KafkaListener(
            topics = "video-data-processed-events",
            groupId = "ai-analysis-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeVideoData(VideoDataProcessedEvent event) {
        // Log the arrival of new intelligence data
        log.info("Received VideoDataProcessedEvent for Topic ID: {} (Video {} of {})",
                event.getTopicId(),
                event.getCurrentCount(),
                event.getTotalVideos());

        try {
            /**
             * Trigger the AI Orchestration logic.
             * This hand-off begins the multi-stage process of partial analysis,
             * aggregation, and final synthesis of the consensus report.
             */
            analysisOrchestrator.processAnalysis(event);

            log.info("Successfully initiated AI Analysis for Topic ID: {}", event.getTopicId());
        } catch (IllegalArgumentException e) {
            /**
             * Business Logic Exception:
             * Handles cases where the event data might be malformed or missing
             * critical fields like Topic ID.
             */
            log.error("Validation Error for Topic ID {}: {}", event.getTopicId(), e.getMessage());
        } catch (RuntimeException e) {
            /**
             * System Exception:
             * Handles runtime issues such as connection failures to the AI provider
             * or internal orchestrator failures.
             */
            log.error("Runtime error during AI Analysis for Topic ID {}: {}",
                    event.getTopicId(), e.getMessage());
        } catch (Exception e) {
            /**
             * Global Exception Safety Net:
             * Prevents the Kafka consumer from crashing and allows it to move
             * to the next message in the partition, maintaining system uptime.
             */
            log.error("CRITICAL: Failed to initiate AI Analysis for Topic ID: {}. Error: {}",
                    event.getTopicId(), e.getMessage(), e);
        }
    }
}