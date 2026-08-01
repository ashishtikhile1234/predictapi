package com.predictapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Represents a single prediction record in the history response.
 *
 * <p>Used as an element inside {@link HistoryResponse#getContent()}.
 * Contains only the summary fields — the full class probabilities are
 * not stored in the log for storage efficiency.</p>
 */
@Data
@Builder
public class HistoryItem {

    /** Database primary key of the prediction log record. */
    private Long id;

    /** The predicted digit class (0–9). */
    private int predictedClass;

    /** Winning class confidence score (0.0–1.0). */
    private double confidence;

    /** UTC timestamp when this prediction was recorded. */
    private Instant timestamp;

    /** Inference wall-clock time in milliseconds. */
    private long inferenceTimeMs;

    /** Version of the model used for this prediction. */
    private String modelVersion;
}
