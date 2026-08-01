# PredictAPI

> A Java Spring Boot application that serves a trained MNIST digit classifier through a production-grade REST API.

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-green)](https://spring.io/projects/spring-boot)
[![ONNX Runtime](https://img.shields.io/badge/ONNX%20Runtime-1.17-blue)](https://onnxruntime.ai/)

---

## Overview

PredictAPI bridges the gap between data science and enterprise backend engineering. It accepts a handwritten digit image (Base64 encoded), runs inference via a pre-trained ONNX model, and returns:

- **Predicted digit** (0–9)
- **Confidence score**
- **Full class probabilities**
- **Inference latency**

All predictions are persisted to a database and queryable via a paginated history endpoint.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3 |
| ML Inference | ONNX Runtime 1.17 |
| Database (dev) | H2 (embedded, zero config) |
| Database (prod) | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5, Mockito |

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run Locally (H2 — zero config)

```bash
git clone https://github.com/ashishtikhile1234/predictapi.git
cd predictapi
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

### Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoints

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/predict` | Classify a digit image |
| `GET` | `/api/v1/history` | Paginated prediction history |
| `GET` | `/api/v1/health` | Service and model health check |

### Example Request
```bash
curl -X POST http://localhost:8080/api/v1/predict \
  -H "Content-Type: application/json" \
  -d '{"image": "<base64-string-here>"}'
```

### Example Response
```json
{
  "requestId": "a1b2c3d4-...",
  "predictedClass": 7,
  "confidence": 0.9823,
  "classProbabilities": { "0": 0.001, "7": 0.9823, ... },
  "timestamp": "2026-08-01T10:15:30Z",
  "inferenceTimeMs": 42,
  "modelVersion": "mnist-cnn-v1"
}
```

---

## Project Structure

```
src/main/java/com/predictapi/
├── controller/      # REST endpoints (HTTP concerns only)
├── service/         # Business logic: preprocessing, inference, logging
├── dto/             # Request/Response objects
├── entity/          # JPA entities (DB schema)
├── repository/      # Spring Data JPA repositories
├── exception/       # Custom exceptions + global handler
├── config/          # ONNX model loading config
└── PredictApiApplication.java
```

---

## License

MIT
