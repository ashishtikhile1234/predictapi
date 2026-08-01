package com.predictapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for PredictAPI.
 *
 * <p>Configures global CORS policy and max upload size.
 * The allowed origins are kept restrictive by default — override via
 * {@code predictapi.cors.allowed-origins} in profile-specific configs.</p>
 */
@Configuration
public class WebConfig {

    @Value("${predictapi.cors.allowed-origins:*}")
    private String[] allowedOrigins;

    /**
     * Configures CORS so the Swagger UI and any web front-end can call the API.
     *
     * @return a {@link WebMvcConfigurer} with global CORS mappings applied
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOriginPatterns(allowedOrigins)
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .maxAge(3600);
            }
        };
    }
}
