package com.predictapi.service;

import com.predictapi.dto.HistoryItem;
import com.predictapi.dto.HistoryResponse;
import com.predictapi.entity.PredictionLog;
import com.predictapi.repository.PredictionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for persisting and querying {@link PredictionLog} records.
 *
 * <p>Acts as the bridge between the controller layer and the repository,
 * applying business rules for pagination limits and sort validation.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionLogService {

    /** Maximum number of results allowed per history page. */
    private static final int MAX_PAGE_SIZE = 100;

    /** Allowed sort fields for the history endpoint. */
    private static final java.util.Set<String> SORTABLE_FIELDS =
            java.util.Set.of("createdAt", "confidence", "predictedClass", "inferenceTimeMs");

    private final PredictionLogRepository repository;

    /**
     * Persists a prediction log record to the database.
     *
     * @param log the prediction log entity to save
     * @return the saved entity with its generated {@code id} and {@code createdAt}
     */
    @Transactional
    public PredictionLog save(PredictionLog log) {
        PredictionLog saved = repository.save(log);
        log.setId(saved.getId());
        log.setCreatedAt(saved.getCreatedAt());
        log.log("Persisted prediction log id={} class={} confidence={}",
                saved.getId(), saved.getPredictedClass(), saved.getConfidence());
        return saved;
    }

    /**
     * Returns a paginated list of prediction history records.
     *
     * @param page   0-indexed page number
     * @param size   results per page (capped at {@value #MAX_PAGE_SIZE})
     * @param sortBy field to sort by (must be in {@link #SORTABLE_FIELDS})
     * @param order  {@code "asc"} or {@code "desc"}
     * @return paginated {@link HistoryResponse}
     */
    @Transactional(readOnly = true)
    public HistoryResponse getHistory(int page, int size, String sortBy, String order) {
        // Clamp page size to the documented maximum
        int clampedSize = Math.min(size, MAX_PAGE_SIZE);

        // Fall back to default sort field if an unsupported field is requested
        String resolvedSortBy = SORTABLE_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(order)
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, clampedSize, Sort.by(direction, resolvedSortBy));
        Page<PredictionLog> resultPage = repository.findAll(pageable);

        java.util.List<HistoryItem> items = resultPage.getContent().stream()
                .map(this::toHistoryItem)
                .toList();

        return HistoryResponse.builder()
                .content(items)
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .totalElements(resultPage.getTotalElements())
                .totalPages(resultPage.getTotalPages())
                .build();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private HistoryItem toHistoryItem(PredictionLog log) {
        return HistoryItem.builder()
                .id(log.getId())
                .predictedClass(log.getPredictedClass())
                .confidence(log.getConfidence())
                .timestamp(log.getCreatedAt())
                .inferenceTimeMs(log.getInferenceTimeMs())
                .modelVersion(log.getModelVersion())
                .build();
    }
}
