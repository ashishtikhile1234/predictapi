package com.predictapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PredictAPI — Java-based ML Model Deployment Platform.
 *
 * <p>Serves a trained MNIST digit classifier via a REST API backed by
 * Spring Boot 3.x, ONNX Runtime, and PostgreSQL/H2.</p>
 */
@SpringBootApplication
public class PredictApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictApiApplication.class, args);
    }
}
