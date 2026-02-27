# ENGINEERING.md

# Estatia — Engineering Standards

## Code Quality

* Kotlin idiomatic practices
* SOLID principles adherence
* Minimal boilerplate; explicit state and dependencies
* Immutability preferred

## Testing Strategy

* **Domain**: Unit tests for use cases
* **Data**: Integration tests for repositories
* **UI / Feature**: Compose UI tests, navigation, interaction tests
* Baseline profiling for feed performance

## Performance & Profiling

* Media caching and prefetch optimized
* ExoPlayer pooling for feed videos
* Firestore query tuning
* Baseline Profiles + Benchmark module enabled

## Security & Trust

* Firebase custom claims for role-based access
* Server-side ownership validation
* Defensive input validation
* Clear client/server responsibility boundaries

## CI/CD & Build

* Gradle convention plugins + version catalogs
* Feature module isolation enforced via CI
* Linting and static analysis on commits
* Separate build variants for debug, staging, production

## Developer Guidelines

* Feature modules depend only on domain interfaces
* Reusable components in core-ui
* State exposed immutably
* Side effects explicit and contained
* PRs require test coverage and architectural review

---

These engineering guidelines ensure Estatia's codebase remains maintainable, scalable, and production-ready across all modules and features.
