# 🧠 Nexus AI: AI Analysis Service (Service 3)

The **AI Analysis Service** is the cognitive heart of the Nexus AI ecosystem. It acts as a stateful aggregator and synthesis engine, responsible for transforming raw video transcripts into structured, high-fidelity research reports using large language models (LLMs).

---

## 🛠️ Service Overview

This service operates at the end of the research pipeline. It leverages a **Stateful Aggregation Pattern** to ensure that final insights are only generated once a sufficient threshold of data (Consensus) is met.

### Core Responsibilities:
* **Transcript Aggregation:** Consumes individual video transcript chunks from Kafka and groups them by Topic ID.
* **Consensus Thresholding:** Waits until exactly 3 high-quality sources are collected before triggering the analysis engine.
* **LLM Synthesis:** Utilizes **Llama 3.3 (via Groq Cloud)** to identify cross-source agreement, contradictory claims, and sentiment.
* **Interactive Data Generation:** Extracts specific interactive timestamps and segment summaries for the frontend dashboard.

---

## 🏗️ Architecture & Logic Flow

The service maintains internal state (via In-Memory Map or Redis) to track incoming data chunks before final processing.



### Tech Stack:
* **Framework:** Spring Boot 3.5.x
* **AI Engine:** Llama 3.3 70B (Grok Cloud API)
* **Messaging:** Apache Kafka (Consumer of processed video data)
* **Java Version:** 21

---

## 🛠️ Kafka Flow (Service Role)

1.  **Consumer Mode:** Listens to the **`video-data-processed-events`** topic.
2.  **Aggregation:** Updates the "Video Count" for a specific Topic ID.
3.  **Synthesis Trigger:** Once `count == 3`, it dispatches the combined transcripts to Groq Cloud.
4.  **Producer Mode (Status):** Publishes `ANALYZING` and `COMPLETED` heartbeats to **`topic-status-updates`**.
5.  **Producer Mode (Final):** Pushes the final JSON research report to **`analysis-completed-events`**.

---

## 🚀 Environment Configuration

Ensure your `.env` file or server environment includes the following:
```env
GROQ_API_KEY=your_llama_3_3_key
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
ANALYSIS_MODEL=llama-3.3-70b-versatile
