package com.estatia.realestate.apps.core.architecture

/**
 * Technical confidence levels for architectural enforcement.
 * Describes HOW accurately the rule can identify a violation.
 */
enum class Confidence {
    /** 
     * High-fidelity symbol resolution (KSP, Semantic Lint). 
     * Cannot be bypassed by FQN, aliases, or star imports.
     */
    CERTAIN,
    
    /** 
     * Reliable structural analysis (Konsist). 
     * Excellent for topology but theoretically bypassable via FQN.
     */
    HIGH,
    
    /** 
     * Pattern/Keyword matching (Syntactic Lint, Grep). 
     * Best-effort feedback. May have false negatives/positives.
     */
    HEURISTIC
}

/**
 * Business and Production risk level.
 */
enum class Risk {
    /** Immediate production failure, crash, or security breach. */
    CRITICAL,
    
    /** High potential for leaks, data races, or major tech debt. */
    HIGH,
    
    /** Architectural smells or suboptimal patterns. */
    MEDIUM,
    
    /** Cosmetic or minor stylistic deviations. */
    LOW
}

/**
 * The action the engineering system takes upon detecting a violation.
 */
enum class Enforcement {
    /** Blocks the build/merge. Non-negotiable. */
    BLOCK,
    
    /** Emits a warning in IDE/CI. Requires justification to bypass. */
    WARN,
    
    /** Optional advice. No action required for merge. */
    INFO
}

/**
 * Formal Registry of Estatia Architectural Laws.
 * 
 * This enum acts as the Single Source of Truth (SSoT) for law metadata.
 */
enum class Law(
    val id: String, 
    val description: String, 
    val risk: Risk,
    val confidence: Confidence,
    val enforcement: Enforcement,
    val rationale: String,
    val recommendation: String
) {
    LAW_001(
        "LAW-001", "Presentation owns UI state.", Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN,
        "Composables should be pure projections of state to ensure testability and prevent business logic leaks.",
        "Move state mutation and complex logic into a ViewModel."
    ),
    LAW_002(
        "LAW-002", "Mutable state never crosses an ownership boundary.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Exposing mutable state containers allows external components to break encapsulation and UDF invariants.",
        "Expose as a read-only StateFlow and use lambda callbacks for mutations."
    ),
    LAW_003(
        "LAW-003", "Infrastructure does not leak into domain or presentation.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Direct dependencies on database or network types in the domain layer prevents infrastructure swapping and complicates testing.",
        "Expose domain models from repositories and use interfaces to hide implementation details."
    ),
    LAW_004(
        "LAW-004", "Feature modules cannot depend on other feature modules.", Risk.HIGH, Confidence.HIGH, Enforcement.BLOCK,
        "Direct feature-to-feature coupling leads to massive build times and circular dependencies.",
        "Communicate between features using a shared API module or central navigation."
    ),
    LAW_005(
        "LAW-005", "Production code does not create coroutine scopes.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Manual CoroutineScope instantiation leads to non-deterministic lifecycles and memory leaks.",
        "Use provided scopes (viewModelScope, lifeCycleScope) or inject a managed scope."
    ),
    LAW_006(
        "LAW-006", "Production code does not choose dispatchers directly.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Hardcoding dispatchers prevents swapping them during testing and violates IO/Main separation policies.",
        "Inject a DispatcherProvider or individual dispatchers via constructor."
    ),
    LAW_007(
        "LAW-007", "Production code does not use wall-clock time directly.", Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "Direct usage of System.currentTimeMillis() makes time-dependent logic impossible to test deterministically.",
        "Inject and use a TimeProvider."
    ),
    LAW_008(
        "LAW-008", "Public APIs expose abstractions, not implementation types.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Exposing implementation types (like ArrayList or Retrofit) in public interfaces couples consumers to internal choices.",
        "Use interfaces and standard Kotlin collection types in public signatures."
    ),
    LAW_009(
        "LAW-009", "Production functions do not silently discard failures.", Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN,
        "Silently catching exceptions or returning empty fallbacks masks critical production failures.",
        "Use AppResult wrappers or rethrow exceptions to the appropriate handler."
    ),
    LAW_010(
        "LAW-010", "Sensitive data never enters application logs.", Risk.CRITICAL, Confidence.HEURISTIC, Enforcement.WARN,
        "Logging PII (passwords, tokens, emails) is a major security breach and compliance violation.",
        "Remove sensitive logs or use a masked logging utility."
    ),
    LAW_011(
        "LAW-011", "Blocking work never executes on the main thread.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Thread.sleep or heavy synchronous IO on the UI thread causes ANRs and jank.",
        "Move blocking work to Dispatchers.IO or use non-blocking suspend functions."
    ),
    LAW_012(
        "LAW-012", "Shared mutable state requires explicit synchronization.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Using standard collections (HashMap) in shared components like Singletons leads to data races and crashes.",
        "Use ConcurrentHashMap, Mutex, or AtomicReference for shared mutable state."
    ),
    LAW_013(
        "LAW-013", "Lifecycle-owned work must be cancellable.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Infinite loops or long-running work without cancellation checks prevents scope cleanup and causes leaks.",
        "Add yield() or ensureActive() checks in long-running loops."
    ),
    LAW_014(
        "LAW-014", "Critical infrastructure must enforce thread-confinement.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Internal engine components often assume a specific thread (e.g. Main) for safety.",
        "Add Confinement.checkMainThread() at the entry point of sensitive methods."
    ),
    LAW_015(
        "LAW-015", "Tests must not depend on real time.", Risk.LOW, Confidence.HEURISTIC, Enforcement.WARN,
        "Tests using system time are non-deterministic and flaky depending on runner speed.",
        "Use TestClock to control time progression in tests."
    ),
    LAW_016(
        "LAW-016", "Tests must strictly remain in test source sets.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Testing libraries (MockK, JUnit) must never be packaged in production binaries.",
        "Move test code and dependencies to src/test or src/androidTest."
    ),
    LAW_017(
        "LAW-017", "Mutable state must follow the backing-property convention.", Risk.LOW, Confidence.HEURISTIC, Enforcement.INFO,
        "Using '_' prefix for mutable backing properties prevents accidental external mutation.",
        "Rename the private property to start with '_' (e.g. _uiState)."
    ),
    LAW_018(
        "LAW-018", "ViewModels must expose a single canonical persistent UI-state owner.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Multiple state flows in a ViewModel create 'Source of Truth' confusion and desynchronized UI.",
        "Consolidate multiple StateFlows into a single UI State data class."
    ),
    LAW_019(
        "LAW-019", "Suspend functions must not secretly launch independent work.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Lauching fire-and-forget work inside a suspend function violates structured concurrency.",
        "Use coroutineScope { launch { ... } } to ensure the work is waited for."
    ),
    LAW_020(
        "LAW-020", "Async results (Deferred) must be joined or returned.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Ignoring the result of an async block leads to unhandled exceptions and silent logic failure.",
        "Await the deferred result or return it to the caller."
    ),
    LAW_021(
        "LAW-021", "Exception handlers must be placed on root scopes.", Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "CoroutineExceptionHandler is ignored when passed to withContext or child launchers.",
        "Move the CEH to the root CoroutineScope definition."
    ),
    LAW_022(
        "LAW-022", "UI must remain localized and accessible.", Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN,
        "Hardcoded strings and standard Material components violate Estatia's design system and localization policy.",
        "Use stringResource() and Estatia-prefixed design components."
    ),
    LAW_023(
        "LAW-023", "Lifecycle-bound objects (Activity/View) must not be stored in long-lived components.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Storing Activities in ViewModels or Singletons causes permanent memory leaks when the UI is destroyed.",
        "Pass needed data from the Activity or use a safe listener pattern."
    ),
    LAW_024(
        "LAW-024", "Long-lived components must not hold direct references to UI Context.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Holding a non-application Context in a Singleton prevents Activity garbage collection.",
        "Inject @ApplicationContext or extract needed values into a plain data class."
    ),
    LAW_025(
        "LAW-025", "Composables must not read from mutable singletons directly.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Reading non-observable 'var' from objects prevents recomposition and leads to stale data.",
        "Wrap the singleton state in a Flow and collect it as State in the Composable."
    ),
    LAW_026(
        "LAW-026", "Expensive object creation must be cached via remember.", Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "Recreating Regex, Bitmaps, or Formatters on every recomposition causes significant performance jank.",
        "Wrap the instantiation in a remember { ... } block."
    ),
    LAW_027(
        "LAW-027", "Composables must not directly call domain or data layer components.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Direct Repository/UseCase calls in UI code bypasses the ViewModel and breaks the UDF architecture.",
        "Expose a UI state from the ViewModel and trigger actions via lambda events."
    ),
    LAW_028(
        "LAW-028", "Methods must be concise and focused (Complexity Budget).", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Excessive cyclomatic complexity or method length indicates poor separation of concerns.",
        "Refactor the method by extracting logical blocks into smaller, private helper functions."
    ),
    LAW_029(
        "LAW-029", "Classes must have a single responsibility (Size Limit).", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Giant 'God Objects' are impossible to test and maintain.",
        "Decompose the class into smaller, specialized components with clear interfaces."
    ),
    LAW_030(
        "LAW-030", "Constructors must have a limited dependency budget.", Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "Classes requiring 9+ dependencies are 'Orchestration Monsters' that are likely doing too much.",
        "Introduce Facades or use a Delegation pattern to reduce direct coordination overhead."
    ),
    LAW_031(
        "LAW-031", "Components must not mix architectural layers or responsibilities.", Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Mixing View logic in a Repository or Network calls in a ViewModel breaks layer isolation.",
        "Strictly separate concerns: Data (IO), Domain (Logic), Presentation (UI)."
    ),
    LAW_032(
        "LAW-032", "Domain and Model layers must remain pure Kotlin (No Frameworks).", Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Importing Android or Firebase types into the Domain layer prevents platform independence.",
        "Keep the Domain layer free of platform-specific frameworks."
    ),
    LAW_033(
        "LAW-033", "Rule suppressions must follow strict organizational policy.", Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Wildcard suppressions (@Suppress(\"all\")) or un-justified skips weaken the entire enforcement system.",
        "Use specific rule IDs and provide a mandatory '// Justification: ...' comment."
    ),
    LAW_034(
        "LAW-034", "Architectural enforcement must be crash-resilient and regression-tested.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Silent failures in the linter lead to a false sense of security.",
        "Ensure every rule has an active verification specimen in the :core:canary-violations module."
    ),
    LAW_035(
        "LAW-035", "FATAL architectural rules must never be baselined.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Baselines are for technical debt; high-risk structural laws must be fixed immediately.",
        "Fix the violation instead of adding it to lint-baseline.xml."
    ),
    LAW_036(
        "LAW-036", "Domain results should express business meaning via types.", Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "Returning generic types (Boolean, String) hides domain intent.",
        "Use sealed classes or specialized domain models to represent rich result states."
    ),
    LAW_037(
        "LAW-037", "Dependencies must be managed via the version catalog.", Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Directly hardcoding dependency versions in build scripts leads to version drift and conflicts.",
        "Move all dependency declarations to gradle/libs.versions.toml."
    ),
    LAW_038(
        "LAW-038", "Production binaries must remain pure and obfuscated.", Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Leaking symbols or using non-standard packaging increases APK size and security risk.",
        "Ensure Proguard/R8 rules are strictly applied and audited."
    );

    override fun toString(): String = id
}
