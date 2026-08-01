package com.predictapi.repository;

import com.predictapi.entity.PredictionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for {@link PredictionLog} entities.
 *
 * <p>All CRUD and pagination operations are handled by Spring Data.
 * Custom query methods are defined here for analytics and filtering use cases.</p>
 */
@Repository
public interface PredictionLogRepository extends JpaRepository<PredictionLog, Long> {

    /**
     * Returns a paginated list of all prediction logs, supporting dynamic
     * sorting via the {@link Pageable} parameter.
     *
     * <p>This is the primary query backing {@code GET /api/v1/history}.</p>
     *
     * @param pageable page number, size, and sort specification
     * @return a page of prediction log entities
     */
    Page<PredictionLog> findAll(Pageable pageable);

    /**
     * Counts how many predictions were made for a specific digit class.
     *
     * @param predictedClass the digit class (0–9)
     * @return total count of predictions for that class
     */
    long countByPredictedClass(Integer predictedClass);

    /**
     * Retrieves all prediction logs created within the given time window.
     * Useful for time-range analytics and monitoring dashboards.
     *
     * @param start start of the time window (inclusive)
     * @param end   end of the time window (inclusive)
     * @return list of prediction logs within the range
     */
    List<PredictionLog> findByCreatedAtBetween(Instant start, Instant end);

    /**
     * Calculates the average confidence score across all predictions.
     *
     * @return average confidence, or {@code null} if no records exist
     */
    @Query("SELECT AVG(p.confidence) FROM PredictionLog p")
    Double findAverageConfidence();

    /**
     * Calculates the average inference latency in milliseconds.
     *
     * @return average latency in ms, or {@code null} if no records exist
     */
    @Query("SELECT AVG(p.inferenceTimeMs) FROM PredictionLog p")
    Double findAverageInferenceTimeMs();
}
