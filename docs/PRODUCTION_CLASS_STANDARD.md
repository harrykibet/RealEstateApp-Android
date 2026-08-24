# Estatia Production-Grade Class Standard

> Engineering standard for designing, implementing, reviewing, testing, and maintaining production-grade classes in Estatia.

---

## Table of Contents

1. [Purpose](#purpose)
2. [Why This Standard Exists](#why-this-standard-exists)
3. [What Production-Grade Means](#what-production-grade-means)
4. [Core Engineering Philosophy](#core-engineering-philosophy)
5. [Class Risk Tiers](#class-risk-tiers)
6. [The Production-Grade Model](#the-production-grade-model)
7. [1. Responsibility and API Design](#1-responsibility-and-api-design)
8. [2. State and Invariants](#2-state-and-invariants)
9. [3. Dependency Management and Dependency Injection](#3-dependency-management-and-dependency-injection)
10. [4. Concurrency and Thread Safety](#4-concurrency-and-thread-safety)
11. [5. Lifecycle and Cancellation](#5-lifecycle-and-cancellation)
12. [6. Error and Failure Semantics](#6-error-and-failure-semantics)
13. [7. Resilience and Chaos Engineering](#7-resilience-and-chaos-engineering)
14. [8. Security and Trust Boundaries](#8-security-and-trust-boundaries)
15. [9. Performance and Resource Management](#9-performance-and-resource-management)
16. [10. Observability](#10-observability)
17. [11. Testability and Deterministic Testing](#11-testability-and-deterministic-testing)
18. [12. Documentation and Operational Contracts](#12-documentation-and-operational-contracts)
19. [State Machine Design](#state-machine-design)
20. [Failure Taxonomy](#failure-taxonomy)
21. [Chaos Testing Matrix](#chaos-testing-matrix)
22. [Concurrency Testing](#concurrency-testing)
23. [Lifecycle Testing](#lifecycle-testing)
24. [Idempotency and Duplicate Operations](#idempotency-and-duplicate-operations)
25. [Retry and Backoff](#retry-and-backoff)
26. [Cancellation Semantics](#cancellation-semantics)
27. [Backpressure](#backpressure)
28. [Caching and Stale Data](#caching-and-stale-data)
29. [Security Requirements](#security-requirements)
30. [Performance Requirements](#performance-requirements)
31. [Observability Requirements](#observability-requirements)
32. [Testing Strategy](#testing-strategy)
33. [Using `core:testing`](#using-coretesting)
34. [Production vs Test Dependencies](#production-vs-test-dependencies)
35. [Class Review Workflow](#class-review-workflow)
36. [100-Point Scoring Model](#100-point-scoring-model)
37. [Hard Failure Gates](#hard-failure-gates)
38. [Definition of Done](#definition-of-done)
39. [Production-Grade Checklist](#production-grade-checklist)
40. [Examples](#examples)
41. [Rules of Thumb](#rules-of-thumb)
42. [Final Principle](#final-principle)

---

# Purpose

This document defines the engineering standard that a class must satisfy before it is considered **production-grade** in Estatia.

The objective is not to make every class complicated.

The objective is to make every important class:

- Correct
- Deterministic
- Resilient
- Thread-safe
- Lifecycle-safe
- Testable
- Observable
- Secure
- Performant
- Maintainable
- Explicit about its failure behavior

A class should not be considered production-ready merely because:

```text
it compiles
+
the happy path works
+
the UI appears correct
```

Production systems fail in ways that normal development rarely exercises.

A production-grade class must therefore be designed around both:

```text
expected behavior
```

and:

```text
unexpected behavior
```

---

# Why This Standard Exists

Estatia is designed as a large, modular application rather than a small single-module Android application.

The repository contains separate infrastructure, domain, feature, testing, media, security, persistence, networking, analytics, configuration, and UI modules.

As the system grows, the largest engineering risk is no longer:

> "Can we make this feature work?"

It becomes:

> "Can this feature continue working when dependencies fail, state races occur, users leave the screen, the process dies, the network disappears, the backend behaves unexpectedly, or another engineer modifies the system six months later?"

This standard exists to make those concerns explicit.

It also creates a common engineering language.

An engineer reviewing a class should be able to say:

> "The class is correct on the happy path, but it has no defined concurrency model."

or:

> "The network failures are handled, but retry idempotency has not been established."

or:

> "The implementation is correct, but the lifecycle contract is missing."

That is much more useful than saying:

> "This doesn't feel production-ready."

---

# What Production-Grade Means

A production-grade class maintains its invariants under:

```text
normal execution
+
invalid input
+
dependency failure
+
concurrent execution
+
cancellation
+
lifecycle changes
+
resource pressure
+
unexpected external data
+
recovery
```

and provides enough observability and deterministic testing to understand what happened when something goes wrong.

The standard is therefore not:

> "Does it work?"

The standard is:

> **"Does it remain correct when the environment stops cooperating?"**

---

# Core Engineering Philosophy

## 1. Make ownership explicit

Every mutable piece of state must have an identifiable owner.

For every mutable property, know:

```text
Who owns it?
Who may mutate it?
Which thread/context may mutate it?
What invariants must hold?
How is access synchronized?
When does ownership end?
```

---

## 2. Prefer immutable state

Use immutable state wherever practical.

Prefer:

```kotlin
data class SearchState(
    val query: String,
    val results: List<Property>,
    val isLoading: Boolean
)
```

over exposing mutable shared structures.

Mutable state should exist because the problem requires it, not because it is convenient.

---

## 3. Choose a concurrency model intentionally

Do not use a concurrency mechanism simply because it is familiar.

Possible strategies include:

```text
Immutable state
Thread confinement
Main-thread confinement
Mutex
Atomic state
Concurrent collections
Single coroutine ownership
Actor/message ownership
Database transactions
```

The class must explicitly choose the appropriate model.

---

## 4. Model failure as part of behavior

Failure is not an afterthought.

For each operation determine:

```text
What can fail?
Is it retryable?
Is retry safe?
Is it user-visible?
Can it recover automatically?
What state does the system enter?
What should be logged?
What should be measured?
```

---

## 5. Make dependencies replaceable

Production dependencies must be replaceable in tests where replacement is meaningful.

Examples:

```text
Network client
Database
File system
Clock
Randomness
Analytics
Authentication
Configuration
Dispatcher
External SDK
```

Use dependency injection at meaningful boundaries.

---

## 6. Test failure deliberately

Tests should not only prove that correct dependencies produce correct results.

They should prove that the class remains correct when dependencies fail.

Examples:

```text
network timeout
server unavailable
database failure
authentication expiry
cancellation
concurrent calls
stale responses
corrupted files
resource exhaustion
```

---

# Class Risk Tiers

Not every class deserves the same engineering burden.

## Tier 0 — Data and value classes

Examples:

```text
Data classes
DTOs
Domain models
UI models
Value classes
Enums
Simple sealed hierarchies
```

Primary concerns:

```text
Immutability
Correct representation
Equality
Serialization
Validation
```

Recommended score:

**80+**

---

## Tier 1 — Pure/stateless logic

Examples:

```text
Mappers
Validators
Parsers
Formatters
Reducers
Calculators
Transformers
```

Primary concerns:

```text
Determinism
Input validation
Edge cases
Total behavior
Property-based testing where useful
```

Recommended score:

**85+**

---

## Tier 2 — Stateful application classes

Examples:

```text
ViewModels
State holders
Caches
Controllers
Configuration state
```

Primary concerns:

```text
State ownership
State transitions
Concurrency
Lifecycle
Cancellation
Error handling
```

Recommended score:

**90+**

---

## Tier 3 — Infrastructure classes

Examples:

```text
Network clients
Repositories
Database components
Data sources
Workers
Authentication managers
File systems
Analytics
Media infrastructure
```

Primary concerns:

```text
Everything above
+
Chaos testing
+
Resource management
+
Observability
+
Integration testing
+
Performance
```

Recommended score:

**90+**

All applicable hard failure gates also apply.

---

## Tier 4 — Critical-path classes

Examples:

```text
Authentication
Token management
Payments
Authorization
Security/cryptography
Player engine
Property publishing/upload pipeline
Database migration infrastructure
```

Primary concerns:

```text
Everything above
+
Adversarial testing
+
Concurrency testing
+
Failure-state modeling
+
Observability
+
Security review
+
Integration/contract tests
+
Recovery testing
```

Recommended score:

**95+**

---

# The Production-Grade Model

A complex production class should be viewed as this system:

```text
                   ┌─────────────────────┐
                   │    Class Contract   │
                   └──────────┬──────────┘
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
          ▼                   ▼                   ▼
        State             Dependencies       Lifecycle
          │                   │                   │
          ▼                   ▼                   ▼
      Invariants         Failure modes       Cancellation
          │                   │                   │
          └───────────────────┼───────────────────┘
                              ▼
                         Concurrency
                              │
                              ▼
                          Resilience
                              │
                   ┌──────────┴──────────┐
                   ▼                     ▼
              Observability          Testing
                   │                     │
                   └──────────┬──────────┘
                              ▼
                       Production safety
```

---

# 1. Responsibility and API Design

A class should have one coherent responsibility.

## Requirements

```text
□ Responsibility is explainable in one sentence
□ Business logic lives in the correct layer
□ No unrelated responsibilities
□ Public API is minimal
□ Implementation details are private
□ Inputs have explicit types
□ Nullability is intentional
□ Invalid states are difficult to represent
□ No unnecessary abstraction
```

## Avoid

```kotlin
class PropertyManager {
    fun fetchProperties()
    fun compressImages()
    fun authenticateUser()
    fun trackAnalytics()
    fun navigate()
}
```

This class has become a system rather than a component.

---

# 2. State and Invariants

Every mutable class must define its state ownership.

Ask:

```text
What state exists?
Who owns the state?
Who can mutate it?
What states are valid?
What transitions are valid?
What transitions are invalid?
```

## Example

For a player engine:

```text
Invariant 1:
A player can have at most one active assignment.

Invariant 2:
Released players cannot be active.

Invariant 3:
Player pool size cannot exceed configured limits.

Invariant 4:
ExoPlayer state is accessed only from its required execution context.

Invariant 5:
A stale callback cannot modify a newer player assignment.
```

These invariants should be reflected in the implementation and tests.

---

# 3. Dependency Management and Dependency Injection

Dependencies should be explicit.

Prefer constructor injection:

```kotlin
class PropertyRepository @Inject constructor(
    private val remote: PropertyRemoteDataSource,
    private val local: PropertyLocalDataSource,
    private val clock: Clock
)
```

Avoid hidden dependencies:

```kotlin
class PropertyRepository {
    private val firestore = FirebaseFirestore.getInstance()
}
```

## Requirements

```text
□ Dependencies are explicit
□ Hilt is used where appropriate
□ Dependencies have correct scopes
□ Infrastructure can be replaced in tests
□ No service locator
□ No hidden global state
□ Dependency ownership is clear
```

Hilt is the mechanism, not the goal.

The goal is explicit and controllable dependency ownership.

---

# 4. Concurrency and Thread Safety

Every stateful class must have an explicit concurrency model.

Possible models:

```text
Immutable
Main-thread confined
Specific dispatcher confined
Mutex protected
Atomic
Concurrent collection
Single coroutine owner
Actor/message ownership
Database transaction
```

## Requirements

```text
□ Concurrent invocation considered
□ Shared state protected
□ No data races
□ No unsafe callback mutations
□ Stale responses cannot overwrite current state
□ Out-of-order operations are handled
□ Duplicate operations are handled
□ Resource ownership is thread-safe
```

Do not assume:

```text
@Singleton == thread-safe
```

A singleton only controls instance lifetime.

---

# Thread Confinement vs Actor-Style Ownership

Thread confinement means:

```text
Only Main may access this state.
```

Actor-style ownership means:

```text
One owner controls the state.
Other callers communicate with it through serialized commands.
```

These can overlap.

For example, a player engine can be:

```text
Singleton
+
Main-thread confined
+
single owner
+
serialized state transitions
```

Do not introduce an actor abstraction merely to use the word "actor".

Choose the simplest model that correctly protects the state.

---

# 5. Lifecycle and Cancellation

Every asynchronous operation needs an owner.

For every coroutine ask:

```text
Who created it?
Who owns it?
Who cancels it?
What happens if cancellation occurs halfway through?
What resources need cleanup?
```

## Requirements

```text
□ Structured concurrency
□ No orphan jobs
□ Cancellation propagates
□ Cancellation is not swallowed
□ Resources close on cancellation
□ Callbacks are removed
□ No state mutation after disposal
□ Process death considered where relevant
```

Never casually turn coroutine cancellation into a normal business failure.

---

# 6. Error and Failure Semantics

A production class must distinguish failures.

Typical failure categories include:

```text
Invalid input
Unauthorized
Forbidden
Not found
Conflict
Offline
Timeout
Rate limited
Server unavailable
Malformed response
Persistence failure
Resource exhaustion
Cancellation
Unexpected/programmer failure
```

Each must have explicit semantics.

## Example

```text
401
→ authentication recovery

429
→ retry with backoff

500
→ retry according to policy

404
→ terminal failure

Timeout
→ retryable depending on operation

Cancellation
→ propagate cancellation

Malformed server response
→ terminal failure + observability
```

Do not blindly do:

```kotlin
catch (e: Exception) {
    return Failure
}
```

Exception mapping must preserve meaningful semantics.

---

# 7. Resilience and Chaos Engineering

Every infrastructure boundary should have adversarial tests for all applicable failures.

The testing strategy is:

```text
Normal behavior
+
Boundary behavior
+
Failure behavior
+
Recovery behavior
```

A class is not resilient merely because it catches exceptions.

---

# Chaos Testing Matrix

## Network Chaos

Applicable network-dependent classes should consider:

```text
□ Offline
□ DNS failure
□ Connection refused
□ Connection reset
□ Timeout
□ Slow response
□ HTTP 400
□ HTTP 401
□ HTTP 403
□ HTTP 404
□ HTTP 408
□ HTTP 409
□ HTTP 429
□ HTTP 500
□ HTTP 502
□ HTTP 503
□ HTTP 504
□ Malformed response
□ Empty response
□ Partial response
□ Unexpected schema
□ Oversized response
□ Duplicate response
□ Out-of-order response
□ Server processes request but client times out
```

The last case is particularly important for operations with side effects.

---

## Database Chaos

```text
□ Database unavailable
□ Database locked
□ Constraint violation
□ Migration failure
□ Schema mismatch
□ Corrupted data
□ Duplicate data
□ Concurrent writes
□ Transaction failure
□ Partial transaction
□ Process death during transaction
□ Disk full
□ Very large dataset
□ Empty dataset
```

---

## Filesystem and Media Chaos

```text
□ File missing
□ File disappears during operation
□ Permission denied
□ Disk full
□ Corrupt file
□ Zero-byte file
□ Unsupported format
□ Wrong MIME type
□ Partial file
□ File changes while being read
□ Very large file
□ I/O failure
```

---

## Authentication Chaos

```text
□ Token expired
□ Token revoked
□ Refresh fails
□ Refresh times out
□ Multiple simultaneous refresh requests
□ Logout during refresh
□ Logout during active request
□ Account disabled
□ Permissions revoked
□ Process death during authentication
□ Network lost during refresh
□ Invalid session restoration
```

---

## Concurrency Chaos

```text
□ Duplicate operation
□ Concurrent operation
□ Out-of-order completion
□ Stale result
□ Cancellation race
□ Callback race
□ Double release
□ Double initialization
□ Operation after disposal
□ Concurrent state mutation
□ Multiple refresh operations
```

---

## Lifecycle Chaos

```text
□ Screen destruction during operation
□ Navigation away
□ Navigation back
□ Configuration recreation
□ Background
□ Foreground
□ ViewModel cleared
□ Process death
□ Dependency disposal
```

---

## Resource Chaos

```text
□ Memory pressure
□ CPU pressure
□ Worker exhaustion
□ Thread-pool exhaustion
□ Player-pool exhaustion
□ Queue saturation
□ Huge dataset
□ Huge media
□ Too many simultaneous operations
□ Disk exhaustion
```

---

## Time Chaos

Time must be injectable where time affects behavior.

Test:

```text
□ Timeout
□ Expiration
□ Retry deadline
□ Boundary exactly at expiration
□ Just before expiration
□ Just after expiration
□ Clock moving forward
□ Clock moving backward
□ Clock skew
□ Long-running operation
```

Prefer a `Clock` abstraction over scattered calls to system time.

---

## Input Chaos

```text
□ Null where allowed
□ Empty
□ Blank
□ Malformed
□ Oversized
□ Negative values
□ Zero
□ Maximum values
□ Unexpected enum
□ Unknown server field
□ Invalid identifier
□ Invalid URL
□ Invalid file metadata
□ Unicode/unusual characters where relevant
```

---

# 8. Security and Trust Boundaries

Every class that handles external or sensitive data must identify its trust boundary.

Ask:

```text
What data is untrusted?
Who controls it?
What validation occurs?
What secrets enter?
Where are secrets stored?
Can sensitive information leak through logging?
Can authorization be bypassed?
```

## Requirements

```text
□ Validate untrusted inputs
□ Do not trust client-side authorization
□ Protect secrets
□ Avoid sensitive logging
□ Use established cryptographic primitives
□ Keep authorization server-enforced
□ Minimize sensitive data lifetime
```

Client-side validation improves correctness and UX.

It must never replace server-side authorization.

---

# 9. Performance and Resource Management

Production-grade classes must have bounded resource behavior.

Consider:

```text
CPU
Memory
Allocations
Threads
Coroutines
Network requests
Database operations
Disk I/O
Player instances
Queue sizes
Cache sizes
```

## Requirements

```text
□ Main-thread work is intentional
□ Complexity is understood
□ Memory growth is bounded
□ Queues are bounded
□ Concurrency is bounded
□ Large inputs have defined behavior
□ Resources are released
□ Performance is benchmarked where critical
```

For high-frequency systems such as the player engine, feed, search, and media processing, performance characteristics should be measured rather than guessed.

---

# 10. Observability

A production class should be diagnosable.

For critical infrastructure, capture relevant:

```text
success count
failure count
failure category
latency
retry count
cancellation
queue depth
resource utilization
state transition information
```

## Logging principles

Logs should answer:

```text
What happened?
Which operation?
Which resource?
Why did it fail?
Can it be correlated with another operation?
```

Avoid logs such as:

```text
"something failed"
```

Prefer structured information.

Never log:

```text
passwords
tokens
private credentials
sensitive payment data
raw secrets
```

---

# 11. Testability and Deterministic Testing

A class should be easy to test without booting the entire application.

Useful injectable dependencies include:

```text
Clock
Dispatcher
Random source
Network
Database
Filesystem
Analytics
Authentication
Configuration
External SDK
```

## Determinism

Tests should be able to control:

```text
time
execution
dependencies
failure order
concurrency
```

Avoid tests that rely on real timing:

```kotlin
delay(100)
```

when deterministic scheduling can be used.

Prefer controlled test schedulers and explicit synchronization points.

---

# 12. Documentation and Operational Contracts

Complex classes should document their important contracts.

Example:

```kotlin
/**
 * Ownership:
 * All mutable player state is Main-thread confined.
 *
 * Concurrency:
 * Commands are serialized by the player engine.
 *
 * Lifecycle:
 * close() releases all player resources.
 *
 * Failure:
 * Network failures are classified by RetryPolicy.
 *
 * Invariants:
 * One player may only have one active assignment.
 */
```

Documentation should explain behavior that another engineer could otherwise misunderstand.

Do not document obvious implementation syntax.

Document:

```text
ownership
invariants
concurrency
lifecycle
failure
side effects
operational assumptions
```

---

# State Machine Design

Complex classes should be treated as state machines.

Do not model only:

```text
Success
Failure
```

Consider the actual lifecycle:

```text
Idle
  ↓
Loading
  ↓
Loaded
  ↓
Refreshing
  ↓
Loaded
```

with failure branches:

```text
Loading
 ├── Success
 ├── Empty
 ├── Timeout
 ├── Offline
 ├── Unauthorized
 └── ServerFailure
```

And lifecycle branches:

```text
Loading
   ↓
Cancelled
Disposed
ProcessDeath
```

The class should explicitly define what happens in each relevant transition.

---

# Failure Taxonomy

A useful failure taxonomy is:

```text
                    Failure
                       │
        ┌──────────────┼───────────────┐
        ▼              ▼               ▼
    Expected        Recoverable      Unexpected
        │              │               │
        ▼              ▼               ▼
  Invalid input      Retry          Programmer bug
  Not found          Refresh        Unknown invariant
  Unauthorized       Reconnect
                     Reconcile
```

Do not treat every exception as equivalent.

---

# Idempotency and Duplicate Operations

Any operation that can be retried must answer:

> What happens if it executes twice?

Examples:

```text
Create property
Like property
Send message
Upload media
Create payment
Update profile
Delete resource
```

For each operation determine:

```text
Is it naturally idempotent?
Does it require an idempotency key?
Can duplicates be detected?
Can the server reconcile duplicates?
What happens after client timeout?
```

This is particularly critical for payments.

A timeout does not necessarily mean the server did not process the operation.

---

# Retry and Backoff

Retries must be explicit.

For every retry policy define:

```text
maximum attempts
initial delay
maximum delay
backoff strategy
jitter
retryable errors
non-retryable errors
idempotency requirement
```

Do not retry:

```text
invalid input
authorization failures that cannot recover
non-idempotent operations without protection
```

without a specific reason.

---

# Cancellation Semantics

Cancellation is part of the API contract.

For each suspend operation:

```text
What happens if cancellation occurs:
  before execution?
  during network I/O?
  during database transaction?
  after remote side effect?
  during local persistence?
```

Cancellation must not leave local state inconsistent.

When cancellation is meaningful, test it explicitly.

---

# Backpressure

High-throughput systems must not allow producers to overwhelm consumers.

Examples include:

```text
Feed prefetch
Image loading
Video prefetch
Search queries
Analytics
Uploads
Database synchronization
```

Potential strategies:

```text
Bound the queue
Cancel stale work
Coalesce duplicate work
Prioritize visible work
Limit concurrency
Drop obsolete work
Apply backpressure
```

For a scrolling media feed:

```text
User scrolls:
1 → 2 → 3 → 4 → 5 → 6
```

the system should not necessarily execute every historical prefetch request.

Stale work should be cancelled or deprioritized.

---

# Caching and Stale Data

A cache is a consistency mechanism, not simply a speed optimization.

Define:

```text
cache lifetime
staleness
refresh strategy
invalidation
fallback behavior
maximum size
evalution
corruption handling
```

Test:

```text
fresh cache
stale cache
empty cache
corrupt cache
network unavailable with cache
network success with stale cache
concurrent refresh
```

---

# Testing Strategy

Testing should occur at multiple levels.

## Unit tests

Fast and deterministic.

Use them for:

```text
business logic
state transitions
reducers
mappers
validators
error mapping
retry decisions
```

---

## Integration tests

Use real infrastructure where the infrastructure behavior matters.

Examples:

```text
Room DAO
database migrations
DataStore
network serialization
repository integration
```

---

## UI tests

Validate:

```text
critical user flows
navigation
state rendering
interaction
permissions
```

Do not push all business logic validation into UI tests.

---

## Performance tests

Use benchmarks for:

```text
startup
feed rendering
media startup
scrolling
database access
large transformations
```

---

## Chaos tests

Validate resilience.

Examples:

```text
timeout
offline
server error
authentication expiry
database failure
cancellation
concurrency races
resource exhaustion
```

---

## Contract tests

When an interface has multiple implementations, define shared behavioral expectations.

Example:

```text
PropertyRepository contract:

□ successful retrieval
□ empty response
□ timeout
□ unauthorized
□ server error
□ cancellation
□ retry
□ concurrent access
□ stale cache behavior
```

Each implementation should satisfy the same contract.

---

# Using `core:testing`

`:core:testing` is the shared test infrastructure for Estatia.

Its responsibility is to provide reusable:

```text
fixtures
fakes
chaos implementations
test clocks
test dispatchers
controlled schedulers
generators
assertions
test scenarios
contract tests
```

It should behave as a **test platform**, not a miscellaneous collection of mocks.

---

# Recommended `core:testing` Structure

A conceptual structure:

```text
core/testing/
└── testFixtures/
    └── kotlin/
        com.estatia.testing/
        │
        ├── assertions/
        │   ├── StateAssertions.kt
        │   ├── ResultAssertions.kt
        │   └── FlowAssertions.kt
        │
        ├── coroutine/
        │   ├── TestDispatchers.kt
        │   ├── TestScheduler.kt
        │   └── TestScopeFactory.kt
        │
        ├── clock/
        │   ├── TestClock.kt
        │   └── TestTicker.kt
        │
        ├── fake/
        │   ├── network/
        │   ├── database/
        │   ├── filesystem/
        │   ├── auth/
        │   └── analytics/
        │
        ├── chaos/
        │   ├── network/
        │   ├── database/
        │   ├── filesystem/
        │   ├── authentication/
        │   ├── concurrency/
        │   ├── lifecycle/
        │   ├── resources/
        │   ├── server/
        │   ├── time/
        │   └── input/
        │
        ├── generators/
        │   ├── fixtures/
        │   └── scenarios/
```

The exact package structure can evolve.

The architectural responsibilities should remain stable.

---

# Fake vs Chaos

These are intentionally different.

## Fake

A fake provides predictable behavior:

```text
"Return these properties."
```

## Chaos implementation

A chaos implementation intentionally creates failure:

```text
"Timeout twice, return 503 once, then succeed."
```

Both belong in `core:testing`.

---

# Deterministic Chaos

Normal CI chaos tests must be deterministic.

Prefer:

```kotlin
network.script(
    Timeout,
    Http(503),
    Success(response)
)
```

over:

```kotlin
network.failRandomly(20)
```

The first produces:

```text
Request 1 → Timeout
Request 2 → 503
Request 3 → Success
```

Every run is reproducible.

Probabilistic chaos can be used separately for stress/nightly testing.

---

# Scenario Testing

High-level test scenarios should compose infrastructure failures.

Examples:

```text
networkOffline()

authenticationExpired()

serverUnavailable()

databaseLocked()

processDeathDuringUpload()

concurrentTokenRefresh()

outOfOrderSearchResponses()
```

This keeps feature tests understandable.

---

# Production vs Test Dependencies

Production modules must never depend on testing infrastructure.

Correct:

```text
feature
    ├── main
    └── test
           ↓
       core:testing
```

Incorrect:

```text
production code
       ↓
core:testing
```

Testing infrastructure must not increase the production application's dependency graph.

---

# Class Review Workflow

When creating or modifying a significant class, follow this process.

## Step 1 — Identify responsibility

Write:

```text
This class is responsible for ______.
```

If the blank contains multiple unrelated responsibilities, reconsider the design.

---

## Step 2 — Identify state

List:

```text
mutable state
immutable state
external state
cached state
derived state
```

---

## Step 3 — Define invariants

Write the rules that must always remain true.

---

## Step 4 — Select concurrency model

Choose explicitly:

```text
immutable
Main-confined
thread-confined
Mutex
Atomic
actor-style
transaction
```

---

## Step 5 — Define lifecycle

Determine:

```text
creation
ownership
active lifetime
cancellation
shutdown
resource cleanup
```

---

## Step 6 — Define failure taxonomy

List:

```text
validation
network
authentication
persistence
resource
cancellation
unexpected
```

---

## Step 7 — Define recovery

For every failure determine:

```text
retry
refresh
fallback
cache
reconcile
surface
terminate
```

---

## Step 8 — Define observability

Determine which events require:

```text
logging
metrics
tracing
diagnostics
```

---

## Step 9 — Build the test surface

Inject:

```text
network
database
clock
scheduler
dispatcher
authentication
filesystem
```

where applicable.

---

## Step 10 — Attack the implementation

Test:

```text
happy path
boundary cases
chaos
concurrency
cancellation
lifecycle
resource pressure
recovery
```

---

## Step 11 — Score the class

Use the 100-point rubric.

---

## Step 12 — Verify hard gates

A class cannot qualify if a hard failure exists.

---

# 100-Point Scoring Model

| Category                             |  Points |
| ------------------------------------ | ------: |
| Responsibility and API design        |       8 |
| State and invariants                 |      10 |
| Dependency management / DI           |       7 |
| Concurrency and thread safety        |      12 |
| Lifecycle and cancellation           |       8 |
| Error and failure semantics          |      12 |
| Resilience / chaos behavior          |      10 |
| Security and trust boundaries        |       7 |
| Performance and resource behavior    |       7 |
| Observability                        |       5 |
| Testability / deterministic testing  |       9 |
| Documentation / operational contract |       5 |
| **Total**                            | **100** |

## Classification

```text
90–100  Production-grade
80–89   Strong; improvement required
70–79   Functional; not yet production-grade
<70     Refactor required
```

Critical-path classes should target:

```text
95+
```

---

# Hard Failure Gates

A class cannot be classified as production-grade regardless of numerical score if any of these apply:

```text
□ Uncontrolled shared mutable state
□ Data race
□ Undefined concurrency behavior
□ Swallowed coroutine cancellation
□ Orphaned coroutine
□ Resource leak
□ Hidden lifecycle dependency
□ Unsafe retry of non-idempotent operation
□ Security-sensitive data logged
□ Authorization trusted solely to the client
□ Critical failure paths completely untested
□ Impossible state allowed to propagate
□ Stale asynchronous result can overwrite current state
□ Dependency failure leaves persistent state inconsistent
□ Production behavior depends on test-only assumptions
```

---

# Definition of Done

A significant class is considered complete when:

```text
□ Responsibility is explicit
□ Public API is minimal
□ State ownership is defined
□ Invariants are documented
□ Concurrency model is explicit
□ Lifecycle is explicit
□ Cancellation behavior is defined
□ Failure taxonomy exists
□ Retry semantics are defined
□ Idempotency has been considered
□ Relevant chaos scenarios are implemented
□ Dependencies are replaceable
□ Tests are deterministic
□ Resource behavior is bounded
□ Security boundaries are clear
□ Observability exists where appropriate
□ Performance is understood
□ Applicable integration tests exist
□ Score meets required tier
□ No hard failure gates are violated
```

---

# Production-Grade Checklist

Use this checklist during code review.

## Architecture

```text
□ Responsibility is explicit
□ Correct module ownership
□ Public API is minimal
□ No hidden dependencies
□ No abstraction for abstraction's sake
```

## State

```text
□ Mutable state identified
□ State owner identified
□ Invariants documented
□ Valid transitions identified
□ Invalid transitions rejected
□ State cannot be externally mutated
□ State transitions are deterministic
□ Stale state cannot overwrite current state
```

## Dependencies

```text
□ Constructor injection where appropriate
□ Correct Hilt scope where applicable
□ Dependencies are replaceable
□ No service locator
□ No hidden global dependencies
□ Dependency lifetime is correct
```

## Concurrency

```text
□ Concurrency model explicitly chosen
□ Shared state protected
□ No data races
□ Duplicate operations handled
□ Stale responses handled
□ Out-of-order responses handled
□ Cancellation races handled
□ Callback races handled
□ Resource ownership is safe
```

## Lifecycle

```text
□ Coroutine owner identified
□ Structured concurrency
□ Cancellation propagates
□ No orphan jobs
□ Resources released
□ Callbacks removed
□ No state mutation after disposal
□ Process death considered where relevant
```

## Errors

```text
□ Failure taxonomy defined
□ Errors mapped at boundaries
□ Cancellation preserved
□ Retryability defined
□ Backoff defined
□ Idempotency considered
□ Partial success defined
□ Unknown failures handled safely
```

## Chaos

```text
□ Offline
□ Timeout
□ Server failure
□ Malformed response
□ Empty response
□ Duplicate operation
□ Out-of-order response
□ Cancellation
□ Lifecycle destruction
□ Resource exhaustion
□ Authentication failure
□ Persistence failure
□ Invalid input
□ Time/expiry failure
□ All applicable failure cases tested
```

## Security

```text
□ Untrusted input validated
□ Authorization boundary correct
□ Secrets protected
□ Sensitive data not logged
□ Cryptographic operations reviewed
□ Server remains authoritative
```

## Performance

```text
□ Main-thread work justified
□ Complexity understood
□ Memory bounded
□ Queues bounded
□ Concurrency bounded
□ Large inputs tested
□ Resources released
□ Performance measured where critical
```

## Observability

```text
□ Important failures observable
□ Latency measurable where relevant
□ Retry/failure metrics where relevant
□ Correlation context available
□ Logs contain actionable context
```

## Testability

```text
□ Dependencies replaceable
□ Clock controllable
□ Dispatcher controllable
□ Failure injectable
□ Concurrency deterministic
□ State transitions observable
□ Contract tests considered
□ Chaos tests deterministic
```

## Verification

```text
□ Happy path
□ Boundary cases
□ Failure cases
□ Concurrency cases
□ Lifecycle cases
□ Recovery cases
□ Integration behavior
□ Security cases where relevant
```

## Final

```text
□ No hard failure gates violated
□ Required score achieved
□ Invariants documented
□ Production behavior diagnosable
□ Class is safe under failure, not merely successful input
```

---

# Examples

## Example: Simple Mapper

```kotlin
class PropertyMapper {
    fun map(dto: PropertyDto): Property
}
```

Likely Tier 1.

Primary requirements:

```text
deterministic
null/invalid input handling
correct mapping
edge cases
unit tests
```

This does not need actor-style concurrency merely because the overall application uses concurrency.

---

# Example: Repository

```kotlin
class PropertyRepository @Inject constructor(
    private val remote: PropertyRemoteDataSource,
    private val local: PropertyLocalDataSource,
    private val clock: Clock
)
```

Likely Tier 3.

Consider:

```text
network timeout
offline
server error
cache hit
cache miss
stale cache
database failure
concurrent requests
cancellation
retry
duplicate request
observability
```

---

# Example: ViewModel

```kotlin
class SearchViewModel @Inject constructor(
    private val search: SearchUseCase
) : ViewModel()
```

Likely Tier 2.

Consider:

```text
query changes rapidly
old request finishes after new request
screen disappears
process recreation
network failure
empty results
retry
duplicate events
state restoration
```

The key invariant might be:

```text
Only the current query may update visible search results.
```

---

# Example: Player Engine

```kotlin
@Singleton
class PlayerEngine
```

Likely Tier 4.

This class requires significantly more rigor.

Potential invariants:

```text
A player has only one assignment.
Released players are never active.
Pool size stays within policy.
All player access happens on the correct thread.
Stale callbacks cannot mutate current assignments.
Disposed engine accepts no new work.
```

Potential chaos:

```text
rapid scrolling
player exhaustion
duplicate acquire
double release
cancellation during preparation
callback after release
memory pressure
thermal pressure
network timeout
prefetch cancellation
out-of-order media preparation
```

This class should have dedicated concurrency, lifecycle, performance, and resource tests.

---

# Example: Payment Processor

This is Tier 4.

The critical question is not merely:

```text
"Does payment succeed?"
```

It is:

```text
What happens if the client times out after the server processes the payment?
```

Potential state:

```text
Created
  ↓
Pending
  ↓
Processing
  ├── Succeeded
  ├── Failed
  └── Unknown
```

`Unknown` is important.

A client timeout after a successful server-side transaction is not necessarily a failure.

The system may require reconciliation.

Tests should therefore include:

```text
success
timeout before server processing
timeout after server processing
duplicate request
network loss
server error
process death
authentication expiry
retry
reconciliation
```

---

# Rules of Thumb

## Rule 1

If mutable state exists, define its owner.

---

## Rule 2

If asynchronous work exists, define its lifetime.

---

## Rule 3

If an operation can fail, define its failure semantics.

---

## Rule 4

If an operation can retry, define idempotency.

---

## Rule 5

If two operations can overlap, define concurrency behavior.

---

## Rule 6

If external data enters the system, treat it as untrusted.

---

## Rule 7

If resources are acquired, define how they are released.

---

## Rule 8

If time affects behavior, make time controllable in tests.

---

## Rule 9

If failure matters operationally, measure it.

---

## Rule 10

If a race is theoretically possible, write a deterministic test for it.

---

## Rule 11

Do not add actors, Mutexes, interfaces, abstractions, or frameworks without a concrete ownership or testability problem.

Production-grade does not mean maximum complexity.

It means:

> **The minimum complexity required to provide strong correctness guarantees.**

---

# Anti-Patterns

Avoid these patterns.

## "It is a singleton, so it is thread-safe."

False.

---

## "The UI only calls it from Main."

Not sufficient unless Main-thread ownership is an explicit invariant.

---

## "We catch all exceptions."

Exception swallowing is not resilience.

---

## "We retry everything."

Retries can duplicate side effects.

---

## "The test passed, so it works."

A happy-path test proves only happy-path behavior.

---

## "We have 100% line coverage."

Coverage does not prove concurrency correctness, lifecycle correctness, idempotency, or resilience.

---

## "The fake returned the expected result."

That does not prove production behavior under failure.

---

## "The class has an interface, so it is testable."

An unnecessary abstraction is not automatically good architecture.

---

## "We use actors everywhere."

Concurrency mechanisms should follow ownership requirements.

---

# What "Not Just Works" Means

A functional implementation answers:

```text
"Does this work when everything goes right?"
```

A production implementation answers:

```text
"What happens when the network disappears?"

"What happens when the response is late?"

"What happens when the caller cancels?"

"What happens when two requests race?"

"What happens when the process dies?"

"What happens when the database fails?"

"What happens when authentication expires?"

"What happens when the server succeeds but the client times out?"

"What happens when resources are exhausted?"

"What happens when the same operation executes twice?"

"What happens when the user performs the operation ten times faster than expected?"

"Can we diagnose the failure after it happens?"
```

That is the standard.

---

# Final Principle

The goal of Estatia is not to create classes that are complicated.

The goal is to create classes whose behavior remains predictable when the environment becomes unpredictable.

A production-grade class therefore has:

```text
Explicit responsibility
        +
Explicit state ownership
        +
Explicit invariants
        +
Explicit concurrency model
        +
Explicit lifecycle
        +
Explicit failure semantics
        +
Explicit recovery strategy
        +
Explicit security boundaries
        +
Bounded resource behavior
        +
Useful observability
        +
Deterministic tests
        +
Adversarial/chaos tests
```

The final question for every significant class is:

> **"If this class is deployed to millions of users and everything around it starts behaving badly, can we still predict, constrain, test, observe, and recover from its behavior?"**

If the answer is yes, the class is approaching production-grade engineering.

If the answer is only:

> "It works."

then the class is not finished.
