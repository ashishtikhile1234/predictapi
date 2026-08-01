package com.predictapi.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request body for the batch prediction endpoint {@code POST /api/v1/predict/batch}.
 *
 * <p>Accepts up to 10 Base64-encoded images in a single request.
 * Each image is processed independently — a failure on one item does
 * not abort the rest of the batch (partial success is supported).</p>
 *
 * <p>Example:
 * <pre>
 * {
 *   "images": ["&lt;base64-1&gt;", "&lt;base64-2&gt;", "&lt;base64-3&gt;"]
 * }
 * </pre>
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchPredictionRequest {

    /**
     * List of Base64-encoded digit images to classify.
     * Must contain 1–10 items; each item follows the same rules as
     * {@link PredictionRequest#getImage()}.
     */
    @NotEmpty(message = "images list must not be empty")
    @Size(max = 10, message = "batch size must not exceed 10 images per request")
    private List<String> images;
}
