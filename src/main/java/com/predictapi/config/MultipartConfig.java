package com.predictapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.MultipartConfigElement;

/**
 * Configures HTTP multipart and max payload size limits.
 *
 * <p>The 5 MB constraint is enforced both here (at the servlet level) and in
 * {@link com.predictapi.dto.PredictionRequest} validation, providing defense
 * in depth against oversized image payloads.</p>
 */
@Configuration
public class MultipartConfig {

    @Value("${predictapi.image.max-bytes:5242880}")
    private long maxImageBytes;

    /**
     * Sets the maximum request and file size for multipart uploads.
     * Also applies to JSON body payloads via the Spring Boot auto-configuration.
     *
     * @return configured {@link MultipartConfigElement}
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        DataSize limit = DataSize.ofBytes(maxImageBytes);
        factory.setMaxFileSize(limit);
        factory.setMaxRequestSize(limit);
        return factory.createMultipartConfig();
    }

    /** Standard multipart resolver required for Spring MVC. */
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
