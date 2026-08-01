package com.predictapi.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response body for {@code GET /api/v1/health}.
 *
 * <p>Returns {@code "UP"} with model details when the service is healthy,
 * or {@code "DOWN"} with a {@code reason} field if the model failed to load.</p>
 *
 * <p>Example (healthy):
 * <pre>
 * {
 *   "status": "UP",
 *   "modelLoaded": true,
 *   "modelVersion": "mnist-cnn-v1",
 *   "uptimeSeconds": 3600
 * }
 * </pre>
 * </p>
 *
 * <p>Example (degraded):
 * <pre>
 * {
 *   "status": "DOWN",
 *   "modelLoaded": false,
 *   "reason": "Model file not found at startup path"
 * }
 * </pre>
 * </p>
 */
@Data
@Builder
public class HealthResponse {

    /** {@code "UP"} if the service is operational; {@code "DOWN"} otherwise. */
    private String status;

    /** {@code true} if the ONNX model was loaded successfully at startup. */
    private boolean modelLoaded;

    /** Version tag of the currently loaded model (null if not loaded). */
    private String modelVersion;

    /** Seconds since the application started (null when status is DOWN). */
    private Long uptimeSeconds;

    /**
     * Human-readable reason for degraded status.
     * Only populated when {@code modelLoaded} is {@code false}.
     */
    private String reason;
}
