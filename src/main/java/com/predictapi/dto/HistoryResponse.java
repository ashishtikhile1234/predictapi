package com.predictapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Paginated response wrapper for {@code GET /api/v1/history}.
 *
 * <p>Mirrors the structure of Spring Data's {@code Page<T>} but serialized
 * as a flat JSON object for a clean, predictable API contract.</p>
 *
 * <p>Example:
 * <pre>
 * {
 *   "content": [...],
 *   "page": 0,
 *   "size": 20,
 *   "totalElements": 154,
 *   "totalPages": 8
 * }
 * </pre>
 * </p>
 */
@Data
@Builder
public class HistoryResponse {

    /** List of prediction records for the current page. */
    private List<HistoryItem> content;

    /** Current page number (0-indexed). */
    private int page;

    /** Number of results per page (max 100). */
    private int size;

    /** Total number of prediction records across all pages. */
    private long totalElements;

    /** Total number of pages given the current page size. */
    private int totalPages;
}
