# ADR-0002: Repository structure — Monorepo with Gradle multi-module

## Drivers
- Single-developer project (portfolio); no team autonomy requirement
- Services evolve together (contracts, shared models)
- Reviewer experience: a single repo URL surfaces the whole platform
- Atomic changes across services without cross-repo PR coordination

## Decision
Adopt monorepo with Gradle multi-module structure.
Each service (api-gateway, rate-limiter-service, async-counter-processor)
is a Gradle subproject. Shared contracts live in a `contracts/` module.

## Consequences
+ Reviewers see the whole system in one place
+ Atomic refactors across service boundaries
+ Single CI pipeline, single dependency graph
+ Contracts shared via internal Gradle dependency, no Maven publish needed
- Repo grows over time (mitigated: still small, ~5 modules expected)
- All services share Spring Boot baseline (acceptable: ADR-0001 enforces this)

## Alternatives considered
- Multi-repo: would mimic large org separation, but adds friction without
  benefit at single-developer scale. Cross-repo contract coordination
  becomes overhead disproportionate to the team size (n=1). Revisit if
  the project ever grows to multiple maintainers with independent release cadences.