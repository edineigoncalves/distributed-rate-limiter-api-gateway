# API Gateway — Distributed Rate Limiter

> Edge service for the distributed-rate-limiter platform. Routes inbound
> traffic and delegates rate-limit decisions to the Rate Limiter Service.

## Status
Work in progress — Stage 2 complete (proxy routing). Stage 3: rate-limit
filter integration.

## Architecture context
This service is one of three in the platform:

- **api-gateway** (this repo) — edge routing + rate-limit enforcement
- **rate-limiter-service** — decision engine (link quando existir)
- **async-counter-processor** — eventual consistency counter store (link)

See [`docs/architecture.md`](docs/architecture.md) for the full system diagram.

## Tech stack
- Java 21 (Virtual Threads)
- Spring Boot 4.0
- Spring Cloud Gateway Server WebMVC (Spring Cloud 2025.1.x)
- Micrometer + Prometheus

## Running locally
\```bash
./gradlew bootRun
\```

Test the proxy route:
\```bash
curl http://localhost:8080/test/get
\```

## Architecture decisions
All ADRs live in [`docs/adr/`](docs/adr/). Key ones:

- [ADR-0001: Platform baseline — Spring Boot 4.0](docs/adr/0001-platform-baseline-spring-boot-4.md)
- [ADR-0002: Multi-repository structure](docs/adr/0002-multi-repo-structure.md)

## Known limitations
- `/actuator/gateway/routes` is not available with the WebMVC starter.
  Tracked upstream in [spring-cloud/spring-cloud-gateway#3233](https://github.com/spring-cloud/spring-cloud-gateway/issues/3233).
  Use direct path requests for dev inspection; rely on Prometheus
  metrics and structured logs for production observability.

## License
MIT (ou qualquer outra que escolher)