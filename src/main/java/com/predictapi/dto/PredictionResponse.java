package com.predictapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * Successful response body for {@code POST /api/v1/predict}.
 *
 * <p>Contains the predicted digit class, confidence, full class probability
 * distribution, and metadata such as inference latency and model version.</p>
 */
@Data
@Builder
public class PredictionResponse {

    /** Correlation ID — either from the request or server-generated UUID. */
    private String requestId;

    /** The digit class with the highest probability (0–9). */
    private int predictedClass;

    /** Probability of the winning class (0.0–1.0). */
    private double confidence;

    /**
     * Softmax probability for every class.
     * Keys are string digits "0"–"9", values sum to ≈ 1.0.
     */
    private Map<String, Double> classProbabilities;

    /** UTC timestamp when the prediction was processed. */
    private Instant timestamp;

    /** Wall-clock time from request receipt to response, in milliseconds. */
    private long inferenceTimeMs;

    /** Human-readable version tag of the loaded ONNX model. */
    private String modelVersion;
}
