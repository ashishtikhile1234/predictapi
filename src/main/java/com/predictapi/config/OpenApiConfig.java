package com.predictapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SpringDoc OpenAPI configuration.
 *
 * <p>Provides the top-level API metadata shown in Swagger UI at
 * {@code /swagger-ui.html}. Individual endpoint descriptions are
 * added via {@code @Operation} annotations in the controller layer.</p>
 */
@Configuration
public class OpenApiConfig {

    @Value("${predictapi.model.version:mnist-cnn-v1}")
    private String modelVersion;

    /**
     * Configures the global OpenAPI metadata bean used by SpringDoc.
     *
     * @return fully configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI predictApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PredictAPI")
                        .version("1.0.0")
                        .description(
                                "REST API for MNIST digit classification via ONNX Runtime. " +
                                "Model version: **" + modelVersion + "**. " +
                                "Submit a Base64-encoded handwritten digit image and receive " +
                                "the predicted class, confidence score, and per-class probabilities.")
                        .contact(new Contact()
                                .name("PredictAPI")
                                .url("https://github.com/ashishtikhile1234/predictapi"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Dev"),
                        new Server().url("https://api.predictapi.io").description("Production")
                ));
    }
}
