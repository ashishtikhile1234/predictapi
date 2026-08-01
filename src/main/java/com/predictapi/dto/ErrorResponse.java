package com.predictapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Standardized error response body returned for all 4xx and 5xx responses.
 *
 * <p>Error codes match the documented values in PRD §7.1:
 * {@code INVALID_IMAGE}, {@code MISSING_FIELD}, {@code PAYLOAD_TOO_LARGE},
 * {@code MODEL_INFERENCE_ERROR}, {@code MODEL_NOT_LOADED}.</p>
 *
 * <p>Example:
 * <pre>
 * {
 *   "error": "INVALID_IMAGE",
 *   "message": "Image could not be decoded or is empty.",
 *   "timestamp": "2026-08-01T10:15:30Z"
 * }
 * </pre>
 * </p>
 */
@Data
@Builder
public class ErrorResponse {

    /** Machine-readable error code (e.g. {@code INVALID_IMAGE}). */
    private String error;

    /** Human-readable description of what went wrong. */
    private String message;

    /** UTC timestamp when the error occurred. */
    private Instant timestamp;
}
