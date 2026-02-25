package com.vishal.aiyoutube.ai_analysis_service.service;

import com.vishal.aiyoutube.ai_analysis_service.dto.*;
import com.vishal.aiyoutube.ai_analysis_service.producer.AnalysisResultProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalysisOrchestrator implements IAnalysisOrchestrator {

    private final IAIProcessorService aiProcessor;
    private final AnalysisResultProducer resultProducer;
    private final PromptEngine promptEngine;

    private final Map<UUID, List<InternalAnalysisDTO>> partialResultsMap = new ConcurrentHashMap<>();
    private final Map<UUID, List<AnalysisCompletedEvent.VideoSegmentDTO>> allSegmentsMap = new ConcurrentHashMap<>();

    private static final int MAX_TRANSCRIPT_CHARS = 8000;

    @Override
    public void processAnalysis(VideoDataProcessedEvent event) {
        UUID tid = event.getTopicId();
        log.info("Received Video {}/{} for Topic: {}", event.getCurrentCount(), event.getTotalVideos(), tid);

        try {
            String fullTranscript = event.getVideoData().getSegments().stream()
                    .map(VideoDataProcessedEvent.TranscriptSegmentDTO::getText)
                    .collect(Collectors.joining(" "));

            String safeTranscript = fullTranscript.length() > MAX_TRANSCRIPT_CHARS
                    ? fullTranscript.substring(0, MAX_TRANSCRIPT_CHARS) + "..."
                    : fullTranscript;

            // Individual Video Analysis
            InternalAnalysisDTO partialAiResult = aiProcessor.analyzeTranscripts(
                    promptEngine.buildAnalysisPrompt(safeTranscript)
            );

            // Convert for aggregation
            List<AnalysisCompletedEvent.VideoSegmentDTO> currentSegments = partialAiResult.getHighlights().stream()
                    .map(h -> AnalysisCompletedEvent.VideoSegmentDTO.builder()
                            .videoId(event.getVideoData().getVideoId())
                            .videoTitle(event.getVideoData().getTitle())
                            .videoUrl(event.getVideoData().getVideoUrl())
                            .timestamp(h.getTimestamp())
                            .bestExplanation(h.getExplanation())
                            .segmentSummary(h.getShortSummary())
                            .build())
                    .toList();

            allSegmentsMap.computeIfAbsent(tid, k -> Collections.synchronizedList(new ArrayList<>())).addAll(currentSegments);
            partialResultsMap.computeIfAbsent(tid, k -> Collections.synchronizedList(new ArrayList<>())).add(partialAiResult);

            // Check if we hit the strict target (e.g., 3/3)
            if (event.getCurrentCount() >= event.getTotalVideos()) {
                performFinalSynthesis(tid);
            } else {
                resultProducer.sendStatusUpdate(new StatusUpdateEvent(tid, "ANALYZING",
                        "Analyzed " + event.getCurrentCount() + " of " + event.getTotalVideos() + " sources..."));
            }

        } catch (Exception e) {
            log.error("Analysis failed for topic {}: {}", tid, e.getMessage());
            resultProducer.sendStatusUpdate(new StatusUpdateEvent(tid, "FAILED", "AI logic error"));
        }
    }

    private void performFinalSynthesis(UUID tid) {
        log.info("All sources collected. Calculating Consensus for Topic: {}", tid);
        List<InternalAnalysisDTO> partials = partialResultsMap.get(tid);
        List<AnalysisCompletedEvent.VideoSegmentDTO> allSegments = allSegmentsMap.get(tid);

        if (partials == null || partials.isEmpty()) return;

        String combinedContext = partials.stream()
                .map(p -> "Source Summary: " + p.getSummary() + " | Claims: " + String.join(", ", p.getClaims()))
                .collect(Collectors.joining("\n---\n"));

        try {
            // THE GLOBAL SYNTHESIS: This determines the final Consensus and Summary
            InternalAnalysisDTO finalAi = aiProcessor.analyzeTranscripts(
                    promptEngine.buildGlobalSynthesisPrompt(combinedContext)
            );

            resultProducer.sendAnalysisCompleted(AnalysisCompletedEvent.builder()
                    .topicId(tid)
                    .finalSummary(finalAi.getSummary())
                    .sentimentScore(finalAi.getSentiment())
                    .consensusPercentage(finalAi.getConsensus())
                    .commonClaims(finalAi.getClaims() != null ? String.join(", ", finalAi.getClaims()) : "Diverse perspectives found")
                    .segments(allSegments)
                    .build());

            resultProducer.sendStatusUpdate(new StatusUpdateEvent(tid, "COMPLETED", "Final report generated."));

        } catch (Exception e) {
            log.error("Synthesis failed: {}", e.getMessage());
        } finally {
            partialResultsMap.remove(tid);
            allSegmentsMap.remove(tid);
        }
    }
}