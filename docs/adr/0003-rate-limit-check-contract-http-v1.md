# ADR-0003: Rate-limit check contract — HTTP/REST v1

## Status

Accepted

## Date

2026-05-19

## Drivers

- Need a stable contract between API Gateway and Rate Limiter Service
- Unblock implementation of both sides (Gateway filter and Rate Limiter endpoint)
- Establish versioning convention to allow future protocol migration
- Choose a protocol balancing developer familiarity, debugging ease, and performance acceptable for the sub-5ms p99 decision latency SLO

## Context

The API Gateway delegates rate-limit decisions to the Rate Limiter Service on every inbound request (see Container diagram). This call sits on the hot path of every request, so latency, observability, and failure semantics matter. Two protocol families were considered for v1: HTTP/REST and gRPC. A subsequent migration to gRPC is anticipated and tracked in ADR-0004 (pending), driven by JMH benchmarks once v1 is operational.

## Decision

Adopt **HTTP/REST as v1** of the rate-limit check contract.

### Endpoint

`POST /v1/check`

The path is versioned (`/v1/`) to support coexistence with a future `/v2/check` (gRPC migration).

### Request body

JSON payload. Only `tenant` is required; remaining fields refine the rate-limit key when the use case requires.

```json
{
  "tenant": "acme",
  "user": "user-123",
  "route": "/api/orders",
  "method": "POST",
  "cost": 1
}
```

| Field    | Type   | Required | Default | Purpose                                 |
|----------|--------|----------|---------|-----------------------------------------|
| tenant   | string | yes      | —       | Base identity for the rate-limit bucket |
| user     | string | no       | —       | Refines key by user within tenant       |
| route    | string | no       | —       | Refines key by endpoint                 |
| method   | string | no       | —       | Refines key by HTTP method              |
| cost     | int    | no       | 1       | Tokens to consume from the bucket       |

The Rate Limiter Service is responsible for composing the final key from these components (see Component diagram — Key Extractor). This keeps the Gateway agnostic of rate-limit rule composition; rules live in one place.

### Response — allow

```http
HTTP/1.1 200 OK
RateLimit-Limit: 100
RateLimit-Remaining: 49
RateLimit-Reset: 1747350000
Content-Type: application/json

{
  "remaining": 49,
  "limit": 100,
  "reset_at": 1747350000
}
```

### Response — deny

```http
HTTP/1.1 429 Too Many Requests
RateLimit-Limit: 100
RateLimit-Remaining: 0
RateLimit-Reset: 1747350000
Retry-After: 30
Content-Type: application/json

{
  "remaining": 0,
  "limit": 100,
  "reset_at": 1747350000,
  "retry_after": 30
}
```

Status code 200/429 carries the decision (RFC 6585). Headers follow the IETF RateLimit Header Fields draft, matching conventions used by GitHub, Stripe, and Twitter. The JSON body duplicates the headers for clients that prefer structured payload parsing.

### Client failure policy (Gateway side)

When `/v1/check` fails (timeout, connection refused, 5xx), the Gateway falls open: the inbound request is allowed through, and the failure is logged and metricized as a rate-limit bypass. This is consistent with the Fault tolerance design property: a rate limiter that becomes the bottleneck it was designed to prevent has failed its purpose. The acceptable window of abuse during Rate Limiter outage is bounded by alerting and operational recovery.

### Timeout

The Gateway → Rate Limiter call uses a 50ms timeout. This is approximately 7x the expected p99 (~7ms: Rate Limiter SLO of 5ms plus ~2ms intra-cluster network), leaving headroom for GC pauses and jitter without holding the caller during Rate Limiter degradation.

### Implementation note on extensibility

The Gateway integrates the Rate Limiter through a `RateLimitClient` interface, with `HttpRateLimitClient` as the v1 implementation. This isolates protocol details (HTTP vs gRPC) from the filter logic, enabling future v2 migration to gRPC without refactoring caller code.

## Consequences

+ Familiar stack for the developer; debugging is trivial (`curl`, Postman)
+ Headers comply with the IETF RateLimit Fields draft and RFC 6585; rate-limit-aware HTTP clients (Resilience4j, etc.) handle `Retry-After` automatically
+ Versioned path leaves the door open for `/v2/check` without breaking v1
+ Optional fields keep the contract flexible — clients adopt only the granularity they need
+ Fail-open in the Gateway preserves system availability when the Rate Limiter is down, consistent with platform-wide AP bias
- HTTP/JSON serialization overhead is higher than gRPC's binary protocol; acceptable for sub-5ms p99 but worth measuring
- Two channels for the same information (headers + body) require keeping them in sync; mitigated by generating both from a single response model
- 50ms timeout means worst-case Gateway latency contribution during Rate Limiter degradation; acceptable per fail-open policy
- Internal endpoint security is out of scope for this ADR; tracked separately (planned ADR-0005)

## Alternatives considered

### gRPC v1 (rejected — deferred to v2 via ADR-0004)

Pros: lower serialization overhead, native schema versioning via `.proto`, HTTP/2 multiplexing. Rejected for v1 because the developer team has no prior gRPC experience and v1 prioritizes time-to-implementation. JMH benchmarks of v1 will provide quantitative justification for the v2 migration, avoiding a "rewrite because new" anti-pattern.

### Pre-composed key in request (rejected)

Sending `{"key": "tenant:acme:route:/api/orders"}` instead of separate fields. Rejected because it pushes key composition rules to every caller (Gateway), preventing centralized rule changes in the Rate Limiter.

### All-required fields (rejected)

Forcing all of `tenant`, `user`, `route`, `method` as required. Rejected because it forces fake values for callers that rate-limit at coarser granularity (e.g., only by tenant). Optional fields with `tenant` as the sole required key supports both coarse and fine-grained use cases.

### 200-always with decision in body (rejected)

Returning HTTP 200 for both allow and deny, with `decision: "deny"` in the body. Rejected because it loses semantic alignment with RFC 6585 (429 = rate limited) and complicates observability — deny events would not appear in standard HTTP error metrics.

## Future considerations

- **ADR-0004 (pending):** Migration to gRPC for v2 of the check contract, justified by JMH benchmark data showing where HTTP/JSON overhead impacts the p99 latency budget
- **ADR-0005 (pending):** Authentication of internal calls to `/v1/check` (mTLS, shared token, or network policy)
- Consider adding a `metadata` extensibility field if specific use cases require passing arbitrary request context to the Rate Limiter