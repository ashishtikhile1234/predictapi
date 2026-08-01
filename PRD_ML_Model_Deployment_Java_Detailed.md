# Product Requirements Document (PRD)

## Project Title
**PredictAPI** — A Java-Based Machine Learning Model Deployment Platform

**Version:** 2.0 (Detailed)
**Author:** [Your Name]
**Date:** August 2026
**Status:** Draft
**Document Owner:** [Your Name]
**Reviewers:** [Instructor / Mentor / Team, if applicable]

---

## 1. Executive Summary

PredictAPI is a Java Spring Boot application that serves a trained machine learning model — an image classifier for handwritten digits (MNIST dataset) — through a well-documented REST API. It demonstrates end-to-end ML deployment skills in a JVM-native environment: model inference, request validation, persistence of prediction history, observability, and (optionally) a lightweight front-end for live demos.

The project is designed as both a **learning exercise** and a **portfolio artifact**, proving the ability to bridge data science outputs (trained models) with production-style backend engineering (Java/Spring Boot), which is a gap in many ML projects that stay Python-only and never reach a deployable, enterprise-integrable state.

---

## 2. Background & Motivation

### 2.1 Context
Most ML tutorials stop at training a model in a Jupyter notebook. In real companies, models must be served reliably, monitored, versioned, and integrated into existing systems — which in many enterprises (banking, insurance, logistics, government) are Java-based. Very few beginner/intermediate projects show this bridge clearly.

### 2.2 Why This Project
- Combines two in-demand skill areas: **Java backend engineering** and **applied ML**
- Uses a well-understood, low-risk dataset (MNIST) so the focus stays on **engineering quality**, not model tuning
- Naturally scoped to be completed solo in 4–6 weeks, while still being extensible

### 2.3 Related/Competitive Approaches
| Approach | Limitation this project addresses |
|---|---|
| Python Flask/FastAPI model serving | Doesn't demonstrate JVM ecosystem skills |
| TensorFlow Serving / TorchServe | Heavyweight, infra-focused, not a hands-on coding project |
| Cloud ML endpoints (SageMaker, Vertex AI) | Abstracts away the actual serving logic — less educational |

---

## 3. Objectives & Success Criteria

### 3.1 Business/Personal Objectives
1. Build a demonstrable, working artifact for a developer portfolio or resume
2. Gain hands-on experience with Spring Boot, REST API design, and ML inference integration in Java
3. Produce a project that can be explained clearly in a technical interview

### 3.2 Success Criteria (Definition of Done for MVP)
- [ ] API successfully classifies a hand-drawn digit image with ≥ 97% accuracy on held-out test data
- [ ] All P0 endpoints implemented, tested, and documented
- [ ] Prediction history persisted and queryable
- [ ] README allows a new developer to clone, build, and run the project in under 10 minutes
- [ ] Swagger UI available and accurate

---

## 4. Target Users & Personas

### Persona 1: "Dev Recruiter Dana"
- Reviews GitHub portfolios quickly
- Needs: clean README, working demo (ideally live or a GIF), clear architecture diagram
- Pain point: abandons projects that don't run out-of-the-box

### Persona 2: "Integrating Developer Ivan"
- Wants to call the API from another service
- Needs: clear request/response contracts, predictable error codes, Swagger docs
- Pain point: undocumented or inconsistent APIs waste his time

### Persona 3: "You, the Builder"
- Learning Spring Boot + ML integration
- Needs: a scoped project that doesn't spiral into "build an MLOps platform"
- Pain point: scope creep, unclear where MVP ends

---

## 5. User Stories

| ID | As a... | I want to... | So that... | Priority |
|---|---|---|---|---|
| US1 | API consumer | POST an image and get a prediction | I can classify digits programmatically | P0 |
| US2 | API consumer | Receive a confidence score with each prediction | I can decide whether to trust the result | P0 |
| US3 | API consumer | Get a clear error when my image is invalid | I can fix my request quickly | P0 |
| US4 | Developer/operator | Check a health endpoint | I know the service and model are running correctly | P0 |
| US5 | Developer/operator | View a history of past predictions | I can audit and debug model behavior | P1 |
| US6 | Portfolio viewer | Try the model via a simple web UI | I can see it work without technical setup | P2 |
| US7 | Developer/operator | See API documentation | I can integrate without reading source code | P1 |
| US8 | Developer/operator | Swap model versions without downtime | I can update the model safely | P2 |
| US9 | Developer/operator | Submit multiple images in one request | I can classify in bulk efficiently | P2 |
| US10 | Security-conscious operator | Require an API key for predictions | I can control access to the service | P2 |

---

## 6. Scope

### 6.1 In Scope (MVP)
- Single-model image classification (digits 0–9)
- REST API with predict, history, and health endpoints
- Persistent logging of predictions to a relational database
- Auto-generated API documentation
- Unit and integration test coverage for core services

### 6.2 Out of Scope (MVP)
- Multi-model support / model marketplace
- User authentication and multi-tenant access control
- Real-time model retraining or online learning
- GPU-accelerated inference
- Horizontal auto-scaling / Kubernetes orchestration

### 6.3 Future Scope (Post-MVP)
- Model versioning and A/B testing between versions
- Authentication (API key / JWT)
- Web UI with drawable canvas
- Dockerized deployment with CI/CD pipeline
- Metrics dashboard (Grafana/Prometheus)

---

## 7. Functional Requirements (Detailed Specification)

### 7.1 `POST /api/v1/predict`

**Purpose:** Accept an image and return a digit prediction.

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "image": "<base64-encoded image, PNG or JPEG>",
  "requestId": "optional-client-generated-uuid"
}
```

**Validation Rules:**
- Image must decode to a valid raster image
- Image dimensions: any size accepted; server resizes to 28x28 grayscale internally
- Max payload size: 5MB
- `image` field is required; `requestId` is optional

**Success Response — `200 OK`:**
```json
{
  "requestId": "a1b2c3d4-...",
  "predictedClass": 7,
  "confidence": 0.9823,
  "classProbabilities": {
    "0": 0.001, "1": 0.002, "2": 0.004, "3": 0.001,
    "4": 0.006, "5": 0.003, "6": 0.008, "7": 0.9823,
    "8": 0.002, "9": 0.0007
  },
  "timestamp": "2026-08-01T10:15:30Z",
  "inferenceTimeMs": 42,
  "modelVersion": "mnist-cnn-v1"
}
```

**Error Responses:**

| Status Code | Error Code | Condition |
|---|---|---|
| 400 | `INVALID_IMAGE` | Image cannot be decoded |
| 400 | `MISSING_FIELD` | Required field missing from request |
| 413 | `PAYLOAD_TOO_LARGE` | Image exceeds 5MB |
| 500 | `MODEL_INFERENCE_ERROR` | Unexpected failure during inference |
| 503 | `MODEL_NOT_LOADED` | Model failed to load at startup |

**Example Error Response:**
```json
{
  "error": "INVALID_IMAGE",
  "message": "Image could not be decoded or is empty.",
  "timestamp": "2026-08-01T10:15:30Z"
}
```

---

### 7.2 `GET /api/v1/history`

**Purpose:** Retrieve paginated prediction logs.

**Query Parameters:**

| Param | Type | Default | Description |
|---|---|---|---|
| page | int | 0 | Page number (0-indexed) |
| size | int | 20 | Results per page (max 100) |
| sortBy | string | timestamp | Field to sort by |
| order | string | desc | `asc` or `desc` |

**Response — `200 OK`:**
```json
{
  "content": [
    {
      "id": 101,
      "predictedClass": 7,
      "confidence": 0.9823,
      "timestamp": "2026-08-01T10:15:30Z",
      "inferenceTimeMs": 42
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 154,
  "totalPages": 8
}
```

---

### 7.3 `GET /api/v1/health`

**Response — `200 OK`:**
```json
{
  "status": "UP",
  "modelLoaded": true,
  "modelVersion": "mnist-cnn-v1",
  "uptimeSeconds": 3600
}
```

**Response — `503 Service Unavailable`** (if model failed to load):
```json
{
  "status": "DOWN",
  "modelLoaded": false,
  "reason": "Model file not found at startup path"
}
```

---

### 7.4 `POST /api/v1/predict/batch` (Stretch Goal)

**Request:**
```json
{
  "images": ["<base64-1>", "<base64-2>", "<base64-3>"]
}
```

**Response:** Array of individual prediction results, same shape as single predict, plus per-item error handling (partial failure supported — one bad image shouldn't fail the whole batch).

---

## 8. Non-Functional Requirements

### 8.1 Performance
- P95 inference latency: < 200ms (CPU only, no GPU)
- API should handle at least 20 concurrent requests without degradation on local hardware (4 cores / 8GB RAM baseline)

### 8.2 Reliability & Availability
- Application must fail fast at startup with a descriptive log message if the model file is missing or corrupted
- Health endpoint must accurately reflect model load state at all times

### 8.3 Scalability
- Stateless request handling (no in-memory session state) so multiple instances can run behind a load balancer
- Database connection pooling configured (HikariCP defaults via Spring Boot)

### 8.4 Security
- Input sanitization: reject non-image payloads before they reach the model layer
- No execution of uploaded content (images processed purely as pixel data, never as scripts/files on disk in production paths)
- Rate limiting recommended for public deployments (stretch goal, e.g., via Bucket4j)
- If authentication (US10) is implemented: API keys stored hashed, never logged in plaintext

### 8.5 Maintainability
- Layered architecture: Controller → Service → Repository, no business logic in controllers
- Configuration externalized via `application.yml` (model path, DB credentials, size limits)
- Consistent logging format (SLF4J + Logback), correlation ID per request

### 8.6 Observability
- Structured logs for every prediction request (request ID, latency, result)
- Optional: Spring Boot Actuator endpoints (`/actuator/health`, `/actuator/metrics`) for operational monitoring

### 8.7 Portability
- Runs as a single executable JAR (`java -jar predictapi.jar`)
- Dockerfile provided for containerized deployment
- No hard dependency on a specific OS or cloud provider

---

## 9. Technical Architecture

### 9.1 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot 3.x |
| ML Inference | ONNX Runtime for Java (model trained in Python, exported as `.onnx`) |
| Database | PostgreSQL (prod) / H2 (local dev/test) |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5, Mockito, Spring Boot Test, Testcontainers (for DB integration tests) |
| Containerization | Docker (stretch) |
| CI/CD (stretch) | GitHub Actions |

### 9.2 Architecture Diagram (Textual)

```
                ┌─────────────────────┐
                │   Client (curl,     │
                │   Postman, Web UI)  │
                └──────────┬──────────┘
                           │ HTTPS/JSON
                           ▼
                ┌─────────────────────┐
                │  REST Controllers   │  (validation, HTTP concerns)
                └──────────┬──────────┘
                           ▼
                ┌─────────────────────┐
                │   Service Layer     │
                │ ┌─────────────────┐ │
                │ │ Image Preproc.  │ │ resize/normalize/tensor conv.
                │ └─────────────────┘ │
                │ ┌─────────────────┐ │
                │ │ Model Inference │ │ ONNX Runtime session.run()
                │ └─────────────────┘ │
                │ ┌─────────────────┐ │
                │ │ Result Mapper   │ │ tensor -> JSON response
                │ └─────────────────┘ │
                └──────────┬──────────┘
                           ▼
                ┌─────────────────────┐
                │  Repository Layer   │  (Spring Data JPA)
                └──────────┬──────────┘
                           ▼
                ┌─────────────────────┐
                │     PostgreSQL      │  (prediction logs)
                └─────────────────────┘
```

### 9.3 Project Structure

```
predictapi/
├── src/main/java/com/predictapi/
│   ├── controller/
│   │   ├── PredictionController.java
│   │   └── HealthController.java
│   ├── service/
│   │   ├── ModelService.java
│   │   ├── ImagePreprocessingService.java
│   │   └── PredictionLogService.java
│   ├── dto/
│   │   ├── PredictionRequest.java
│   │   ├── PredictionResponse.java
│   │   └── ErrorResponse.java
│   ├── entity/
│   │   └── PredictionLog.java
│   ├── repository/
│   │   └── PredictionLogRepository.java
│   ├── exception/
│   │   ├── InvalidImageException.java
│   │   ├── ModelNotLoadedException.java
│   │   └── GlobalExceptionHandler.java
│   ├── config/
│   │   └── OnnxModelConfig.java
│   └── PredictApiApplication.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── model/mnist-cnn.onnx
├── src/test/java/com/predictapi/
│   ├── controller/PredictionControllerTest.java
│   ├── service/ModelServiceTest.java
│   └── service/ImagePreprocessingServiceTest.java
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

### 9.4 Data Model

**Table: `prediction_log`**

| Column | Type | Constraints |
|---|---|---|
| id | BIGSERIAL | PRIMARY KEY |
| request_id | UUID | NULLABLE |
| predicted_class | INTEGER | NOT NULL |
| confidence | DOUBLE PRECISION | NOT NULL |
| inference_time_ms | BIGINT | NOT NULL |
| model_version | VARCHAR(50) | NOT NULL |
| client_ip | VARCHAR(45) | NULLABLE |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL, DEFAULT now() |

**Indexes:** `created_at` (for sorting/pagination), `predicted_class` (for future analytics)

---

## 10. Testing Strategy

| Test Type | Scope | Tools |
|---|---|---|
| Unit Tests | Image preprocessing logic, response mapping, validation rules | JUnit 5, Mockito |
| Integration Tests | Full request/response cycle for each endpoint | Spring Boot Test (`@SpringBootTest`) |
| Model Consistency Tests | Compare Java preprocessing output against reference Python preprocessing on sample images | JUnit 5, fixture files |
| Database Tests | Repository queries, pagination | Testcontainers (PostgreSQL) |
| Load Testing (stretch) | Latency and throughput under concurrent load | Apache JMeter or k6 |
| Manual/Exploratory | Swagger UI walkthrough, malformed input edge cases | Manual |

**Target coverage:** ≥ 70% line coverage on `service` package; 100% of documented error codes have a corresponding test.

---

## 11. Deployment Plan

### 11.1 Local Development
```bash
mvn clean install
mvn spring-boot:run
```
Runs against embedded H2 database by default (`application-dev.yml`).

### 11.2 Production-style Deployment
```bash
docker-compose up --build
```
- `predictapi` container: Spring Boot app
- `postgres` container: persistent database
- Environment variables for DB credentials and model path injected via `.env`

### 11.3 Rollback Strategy
- Keep previous JAR/image tagged (`predictapi:v1.0`) so a bad deploy can be rolled back by redeploying the prior tag
- Model files versioned separately from application code so a model rollback doesn't require a full redeploy (stretch goal: F10)

---

## 12. Success Metrics (Post-Launch)

| Metric | Target |
|---|---|
| Model accuracy on held-out test set | ≥ 97% |
| P95 API latency | < 200ms |
| API error rate (5xx) | < 1% under normal load |
| Test coverage (service layer) | ≥ 70% |
| Time for new dev to run project locally | < 10 minutes, per README |
| Documentation completeness | 100% of endpoints in Swagger, matching actual behavior |

---

## 13. Risks & Mitigations

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| ONNX Runtime Java bindings poorly documented | Medium | Medium | Budget extra research time; fallback to DL4J native training if blocked |
| Preprocessing mismatch between Python training and Java serving | Medium | High | Write consistency tests against known Python reference outputs |
| Model file too large for version control | Low | Low | Use Git LFS or download model at build/startup time from external storage |
| Scope creep into full MLOps platform | High | Medium | Strict MVP scope; stretch goals clearly separated and time-boxed |
| Database setup friction for reviewers/graders | Medium | Medium | Default to embedded H2 for zero-config local runs |

---

## 14. Open Questions

1. Should the model be trained from scratch within this repo, or is importing a pre-trained ONNX model acceptable? *(Recommendation: import pre-trained; include the Python training script as a reference artifact, not part of the Java runtime.)*
2. Is a web UI a hard requirement for the deliverable, or is API-only (via Swagger/Postman) sufficient for grading/demo purposes?
3. Should authentication be included in MVP, or explicitly deferred to "Future Scope"?
4. Is PostgreSQL required, or is H2 acceptable for the final submission (affects setup complexity)?

---

## 15. Glossary

| Term | Definition |
|---|---|
| ONNX | Open Neural Network Exchange — a portable format for ML models, allowing models trained in one framework (e.g., PyTorch) to run in another runtime (e.g., Java) |
| Inference | The process of running input data through a trained model to get a prediction |
| MNIST | A standard dataset of 70,000 handwritten digit images (0–9), commonly used to benchmark classification models |
| P0/P1/P2 | Priority levels: P0 = must-have for MVP, P1 = important but not blocking, P2 = nice-to-have/stretch |
| DTO | Data Transfer Object — a plain object used to move data between layers (e.g., API request/response bodies) |

---

## 16. Appendix

### 16.1 Key Dependencies (`pom.xml` highlights)
```xml
<dependency>
    <groupId>com.microsoft.onnxruntime</groupId>
    <artifactId>onnxruntime</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
</dependency>
```

### 16.2 Reference Dataset
MNIST Handwritten Digits: 60,000 training images / 10,000 test images, 28x28 grayscale, single-channel.

### 16.3 Sample cURL Request
```bash
curl -X POST http://localhost:8080/api/v1/predict \
  -H "Content-Type: application/json" \
  -d '{"image": "<base64-string-here>"}'
```
