package com.vishal.aiyoutube.ai_analysis_service.producer;

import com.vishal.aiyoutube.ai_analysis_service.dto.AnalysisCompletedEvent;
import com.vishal.aiyoutube.ai_analysis_service.dto.StatusUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Producer responsible for communicating results back to the Topic Management Service.
 * It handles both final AI insights and intermediary status updates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisResultProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Topic names must match the configuration in Service 1 and Service 2.
     * ANALYSIS_TOPIC: Carries the final synthesized AI report.
     * STATUS_TOPIC: Carries real-time state changes (e.g., ANALYZING).
     */
    private static final String ANALYSIS_TOPIC = "analysis-completed-events";
    private static final String STATUS_TOPIC = "topic-status-updates";

    /**
     * Publishes the final AI-generated insights.
     * This is the terminal event of the entire backend pipeline.
     * * @param event The synthesized report containing summary, consensus, and segments.
     */
    public void sendAnalysisCompleted(AnalysisCompletedEvent event) {
        log.info("Attempting to publish final AI analysis for Topic ID: {}", event.getTopicId());

        /**
         * Partition Key Strategy:
         * Uses Topic ID as the key to ensure strict ordering. In a distributed
         * environment, this guarantees that all events for a specific research
         * topic are handled sequentially.
         */
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(ANALYSIS_TOPIC, event.getTopicId().toString(), event);

        // Asynchronous callback to verify data persistence in Kafka
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent AnalysisCompletedEvent for Topic ID: [{}] at offset: [{}]",
                        event.getTopicId(), result.getRecordMetadata().offset());
            } else {
                log.error("CRITICAL: Failed to send AI analysis for Topic ID: [{}]. Reason: {}",
                        event.getTopicId(), ex.getMessage());
            }
        });
    }

    /**
     * Publishes a status update to keep the Topic Management Service in sync.
     * This powers the real-time progress bar on the Nexus AI dashboard.
     * * @param event The status object (Topic ID, Status, Message).
     */
    public void sendStatusUpdate(StatusUpdateEvent event) {
        log.info("Publishing pipeline status update [{}] for Topic ID: {}", event.getStatus(), event.getTopicId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(STATUS_TOPIC, event.getTopicId().toString(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish status update for Topic ID: {}: {}",
                        event.getTopicId(), ex.getMessage());
            }
        });
    }
}