# ADR-008: Platform baseline — Spring Boot 4.0 + Spring Cloud 2025.1.x

## Status
Accepted

## Date
2026-05-12

## Drivers
- Alignment with the current state of the art in the Spring ecosystem (2026)
- Long-term support runway: avoid starting a new portfolio project on a
  release train approaching end of life
- Access to new platform capabilities (JSpecify null-safety, modularization,
  native API versioning, first-class Java 25 support)
- Coherence across all services in the platform (Gateway, Rate Limiter
  Service, Async Counter Processor) — avoid mixed baselines
- Trade-off accepted: reduced availability of community content and
  third-party tutorials in exchange for a modern foundation

## Context
The project was initially started with Spring Boot 3.3 in previous ADRs, but
when generating the Gateway module, Spring Initializr suggested 4.0 as the
default. I evaluated whether to stay on 3.3 or migrate the entire platform.

## Decision
Adopt Spring Boot 4.0.x + Spring Cloud 2025.1.x across the entire platform
(Gateway, Rate Limiter Service, Async Counter Processor).

## Consequences
+ Stack aligned with the state of the art as of 2026
+ JSpecify null-safety, full modularization, native API versioning support
+ Java 25 available as first-class (keeping Java 21 per previous ADRs)
- Less learning material available compared to 3.x
- Servlet 6.1 baseline required; Undertow no longer supported
- ADRs 001–007 must be revisited to reflect the 4.0 baseline

## Alternatives considered
- Spring Boot 3.3: more documentation and community content, fewer new
  features, OSS support ending in 2026