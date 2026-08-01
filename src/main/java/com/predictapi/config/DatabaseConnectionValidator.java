package com.predictapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Validates the database connection at application startup.
 *
 * <p>On {@link ApplicationReadyEvent}, attempts to acquire a JDBC connection
 * from the HikariCP pool and logs the result. A failed connection is logged
 * as an error but does not abort the startup — health endpoint will reflect
 * the degraded state.</p>
 */
@Slf4j
@Component
public class DatabaseConnectionValidator {

    private final DataSource dataSource;

    @Value("${spring.datasource.url:unknown}")
    private String datasourceUrl;

    public DatabaseConnectionValidator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Fires after the application context is fully loaded.
     * Validates that the configured data source is reachable.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void validateConnection() {
        try (Connection conn = dataSource.getConnection()) {
            log.info("Database connection OK — url={} catalog={}",
                    datasourceUrl, conn.getCatalog());
        } catch (SQLException ex) {
            log.error("Database connection FAILED — url={} error={}",
                    datasourceUrl, ex.getMessage());
        }
    }
}
