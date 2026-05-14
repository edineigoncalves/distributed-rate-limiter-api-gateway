# ADR-0002: Multi-repository structure

## Status
Accepted

## Date
2026-05-12

## Drivers
- Independent release cadence per service
- Per-service semantic versioning with git tags scoped to each repo
- Independent CI/CD pipelines as the default, no path-based filtering needed
- Simulate operational separation typical of multi-team production environments
- Reduce coupling between services at the source-control level

## Context
The platform comprises three services that may evolve at different paces:
api-gateway (edge concerns), rate-limiter-service (decision engine), and
async-counter-processor (eventual consistency). Each has independent
deployment concerns and may require independent versioning.

## Decision
Adopt multi-repository structure. One git repository per service:
- distributed-rate-limiter-api-gateway
- distributed-rate-limiter-service (future)
- distributed-rate-limiter-async-counter-processor (future)
- distributed-rate-limiter-contracts (future, shared models / proto)

## Consequences
+ Each service has its own version, tags, changelog, and release lifecycle
+ Independent CI/CD pipelines without monorepo tooling overhead
+ Smaller, focused repos for reviewers and contributors
+ Permissions and access control can be scoped per repo if needed later
- Cross-service refactors require coordinated PRs across repos
- Shared contracts must be published as an artifact (Maven Local during dev,
  Maven Central or private registry in production scenarios)
- Reviewers must navigate multiple repos to see the full system; mitigated by
  a top-level README in each repo linking to the others
- Initial setup is heavier (multiple repos, possibly multiple CI configs)

## Alternatives considered
- **Monorepo with Gradle multi-module**: simpler reviewer experience, atomic
  cross-service refactors, single CI pipeline. Rejected because per-service
  versioning and tagging require additional tooling (Nx Release, Changesets,
  or custom scripts with path-based tag conventions), which adds complexity
  orthogonal to the project's main goal of demonstrating rate limiter design.

- **Monorepo with single version for all services**: would couple release
  cadence across services unnecessarily. Rejected for the same reason as
  above plus loss of independent versioning.

## Future considerations
Migration to monorepo remains feasible if cross-service refactor friction
becomes significant once multiple services exist. Trigger to revisit:
recurring need to coordinate PRs across 3+ repos for single logical changes.