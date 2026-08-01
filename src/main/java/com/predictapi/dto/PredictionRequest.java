package com.predictapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Incoming request body for {@code POST /api/v1/predict}.
 *
 * <p>The {@code image} field must be a valid Base64-encoded PNG or JPEG.
 * The server will resize it to 28×28 grayscale internally before inference.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequest {

    /**
     * Base64-encoded image (PNG or JPEG). Required.
     * Max raw size ≈ 5 MB before encoding (~6.8 MB base64).
     */
    @NotBlank(message = "image field is required and must not be blank")
    @Size(max = 6_800_000, message = "image payload exceeds the 5 MB limit")
    private String image;

    /**
     * Optional client-supplied UUID for request correlation.
     * If absent, the server generates one automatically.
     */
    private String requestId;
}
