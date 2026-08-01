package com.predictapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity mapping to the {@code prediction_log} database table.
 *
 * <p>Every call to {@code POST /api/v1/predict} that completes successfully
 * persists one row here. The table supports audit, analytics, and the
 * paginated history endpoint ({@code GET /api/v1/history}).</p>
 *
 * <p>Schema (PostgreSQL production):
 * <pre>
 * CREATE TABLE prediction_log (
 *   id                BIGSERIAL PRIMARY KEY,
 *   request_id        UUID,
 *   predicted_class   INTEGER              NOT NULL,
 *   confidence        DOUBLE PRECISION     NOT NULL,
 *   inference_time_ms BIGINT               NOT NULL,
 *   model_version     VARCHAR(50)          NOT NULL,
 *   client_ip         VARCHAR(45),
 *   created_at        TIMESTAMPTZ          NOT NULL DEFAULT now()
 * );
 * </pre>
 * </p>
 */
@Entity
@Table(
    name = "prediction_log",
    indexes = {
        // Used by GET /api/v1/history default sort (created_at DESC)
        @Index(name = "idx_prediction_log_created_at", columnList = "created_at"),
        // Used by future analytics queries (filter by digit class)
        @Index(name = "idx_prediction_log_predicted_class", columnList = "predicted_class")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionLog {

    /** Auto-generated surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Client-supplied or server-generated request correlation ID.
     * Nullable — not all clients provide one.
     */
    @Column(name = "request_id")
    private UUID requestId;

    /** The winning digit class returned to the caller (0–9). */
    @Column(name = "predicted_class", nullable = false)
    private Integer predictedClass;

    /** Softmax probability of the winning class (0.0–1.0). */
    @Column(nullable = false)
    private Double confidence;

    /** Wall-clock inference time measured in the service layer (ms). */
    @Column(name = "inference_time_ms", nullable = false)
    private Long inferenceTimeMs;

    /** Version tag of the ONNX model that produced this prediction. */
    @Column(name = "model_version", nullable = false, length = 50)
    private String modelVersion;

    /**
     * IPv4 or IPv6 address of the requesting client.
     * Nullable — may be absent when running behind a proxy without forwarding headers.
     */
    @Column(name = "client_ip", length = 45)
    private String clientIp;

    /**
     * UTC creation timestamp. Set automatically by {@link #onCreate()}.
     * Marked {@code updatable = false} — never modified after insert.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * JPA lifecycle callback — sets {@code createdAt} to the current UTC
     * instant immediately before the first database INSERT.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
