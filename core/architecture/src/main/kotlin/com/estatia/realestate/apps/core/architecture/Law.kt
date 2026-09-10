package com.estatia.realestate.apps.core.architecture

/**
 * Functional categories for Estatia laws.
 */
enum class LawCategory {
    ARCHITECTURE,   // Module boundaries and layer isolation
    CONCURRENCY,    // Thread safety and coroutine safety
    API_DESIGN,     // Public contracts and error handling
    UI_GOVERNANCE,  // Compose and View standards
    SECURITY,       // PII and data protection
    PERFORMANCE,    // Main thread safety and memory
    CODE_HEALTH,    // Clean code and complexity
    INFRASTRUCTURE  // Build system and dependencies
}

/**
 * Technical confidence levels for architectural enforcement.
 */
enum class Confidence {
    CERTAIN,
    HIGH,
    HEURISTIC
}

/**
 * Business and Production risk level.
 */
enum class Risk {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}

/**
 * The action the engineering system takes upon detecting a violation.
 */
enum class Enforcement {
    BLOCK,
    WARN,
    INFO
}

/**
 * Formal Registry of Estatia Architectural Laws.
 */
enum class Law(
    val id: String, 
    val description: String, 
    val category: LawCategory,
    val risk: Risk,
    val confidence: Confidence,
    val enforcement: Enforcement,
    val rationale: String,
    val recommendation: String
) {
    // --- UI GOVERNANCE ---
    LAW_001(
        "LAW-001", "Presentation owns UI state.", LawCategory.UI_GOVERNANCE, Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN,
        "Composables should be pure projections of state to ensure testability and prevent business logic leaks.",
        "Move state mutation and complex logic into a ViewModel."
    ),
    
    // --- ARCHITECTURE ---
    LAW_002(
        "LAW-002", "Mutable state never crosses an ownership boundary.", LawCategory.ARCHITECTURE, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Exposing mutable state containers allows external components to break encapsulation and UDF invariants.",
        "Expose as a read-only StateFlow and use lambda callbacks for mutations."
    ),
    LAW_003(
        "LAW-003", "Infrastructure does not leak into domain or presentation.", LawCategory.ARCHITECTURE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Direct dependencies on database or network types in the domain layer prevents infrastructure swapping and complicates testing.",
        "Expose domain models from repositories and use interfaces to hide implementation details."
    ),
    LAW_004(
        "LAW-004", "Feature modules cannot depend on other feature modules.", LawCategory.ARCHITECTURE, Risk.HIGH, Confidence.HIGH, Enforcement.BLOCK,
        "Direct feature-to-feature coupling leads to massive build times and circular dependencies.",
        "Communicate between features using a shared API module or central navigation."
    ),

    // --- CONCURRENCY ---
    LAW_005(
        "LAW-005", "Production code does not create coroutine scopes.", LawCategory.CONCURRENCY, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Manual CoroutineScope instantiation leads to non-deterministic lifecycles and memory leaks.",
        "Use provided scopes (viewModelScope, lifeCycleScope) or inject a managed scope."
    ),
    LAW_006(
        "LAW-006", "Production code does not choose dispatchers directly.", LawCategory.CONCURRENCY, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Hardcoding dispatchers prevents swapping them during testing and violates IO/Main separation policies.",
        "Inject a DispatcherProvider or individual dispatchers via constructor."
    ),

    // --- PERFORMANCE ---
    LAW_007(
        "LAW-007", "Production code does not use wall-clock time directly.", LawCategory.PERFORMANCE, Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "Direct usage of System.currentTimeMillis() makes time-dependent logic impossible to test deterministically.",
        "Inject and use a TimeProvider."
    ),

    // --- API DESIGN ---
    LAW_008(
        "LAW-008", "Public APIs expose abstractions, not implementation types.", LawCategory.API_DESIGN, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Exposing implementation types (like ArrayList or Retrofit) in public interfaces couples consumers to internal choices.",
        "Use interfaces and standard Kotlin collection types in public signatures."
    ),
    LAW_009(
        "LAW-009", "Production functions do not silently discard failures.", LawCategory.API_DESIGN, Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN,
        "Silently catching exceptions or returning empty fallbacks masks critical production failures.",
        "Use AppResult wrappers or rethrow exceptions to the appropriate handler."
    ),

    // --- SECURITY ---
    LAW_010(
        "LAW-010", "Sensitive data never enters application logs.", LawCategory.SECURITY, Risk.CRITICAL, Confidence.HEURISTIC, Enforcement.WARN,
        "Logging PII (passwords, tokens, emails) is a major security breach and compliance violation.",
        "Remove sensitive logs or use a masked logging utility."
    ),

    // --- PERFORMANCE ---
    LAW_011(
        "LAW-011", "Blocking work never executes on the main thread.", LawCategory.PERFORMANCE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Thread.sleep or heavy synchronous IO on the UI thread causes ANRs and jank.",
        "Move blocking work to Dispatchers.IO or use non-blocking suspend functions."
    ),

    // --- CONCURRENCY ---
    LAW_012(
        "LAW-012", "Shared mutable state requires explicit synchronization.", LawCategory.CONCURRENCY, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Using standard collections (HashMap) in shared components like Singletons leads to data races and crashes.",
        "Use ConcurrentHashMap, Mutex, or AtomicReference for shared mutable state."
    ),
    LAW_013(
        "LAW-013", "Lifecycle-owned work must be cancellable.", LawCategory.CONCURRENCY, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Infinite loops or long-running work without cancellation checks prevents scope cleanup and causes leaks.",
        "Add yield() or ensureActive() checks in long-running loops."
    ),
    LAW_014(
        "LAW-014", "Critical infrastructure must enforce thread-confinement.", LawCategory.CONCURRENCY, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Internal engine components often assume a specific thread (e.g. Main) for safety.",
        "Add Confinement.checkMainThread() at the entry point of sensitive methods."
    ),

    // --- PERFORMANCE ---
    LAW_015(
        "LAW-015", "Tests must not depend on real time.", LawCategory.PERFORMANCE, Risk.LOW, Confidence.HEURISTIC, Enforcement.WARN,
        "Tests using system time are non-deterministic and flaky depending on runner speed.",
        "Use TestClock to control time progression in tests."
    ),

    // --- ARCHITECTURE ---
    LAW_016(
        "LAW-016", "Tests must strictly remain in test source sets.", LawCategory.ARCHITECTURE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Testing libraries (MockK, JUnit) must never be packaged in production binaries.",
        "Move test code and dependencies to src/test or src/androidTest."
    ),

    // --- CODE HEALTH ---
    LAW_017(
        "LAW-017", "Mutable state must follow the backing-property convention.", LawCategory.CODE_HEALTH, Risk.LOW, Confidence.HEURISTIC, Enforcement.INFO,
        "Using '_' prefix for mutable backing properties prevents accidental external mutation.",
        "Rename the private property to start with '_' (e.g. _uiState)."
    ),

    // --- UI GOVERNANCE ---
    LAW_018(
        "LAW-018", "ViewModels must expose a single canonical persistent UI-state owner.", LawCategory.UI_GOVERNANCE, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Multiple state flows in a ViewModel create 'Source of Truth' confusion and desynchronized UI.",
        "Consolidate multiple StateFlows into a single UI State data class."
    ),

    // --- CONCURRENCY ---
    LAW_019(
        "LAW-019", "Suspend functions must not secretly launch independent work.", LawCategory.CONCURRENCY, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Lauching fire-and-forget work inside a suspend function violates structured concurrency.",
        "Use coroutineScope { launch { ... } } to ensure the work is waited for."
    ),
    LAW_020(
        "LAW-020", "Async results (Deferred) must be joined or returned.", LawCategory.CONCURRENCY, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Ignoring the result of an async block leads to unhandled exceptions and silent logic failure.",
        "Await the deferred result or return it to the caller."
    ),
    LAW_021(
        "LAW-021", "Exception handlers must be placed on root scopes.", LawCategory.CONCURRENCY, Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "CoroutineExceptionHandler is ignored when passed to withContext or child launchers.",
        "Move the CEH to the root CoroutineScope definition."
    ),

    // --- UI GOVERNANCE ---
    LAW_022(
        "LAW-022", "UI must remain localized and accessible.", LawCategory.UI_GOVERNANCE, Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN,
        "Hardcoded strings and standard Material components violate Estatia's design system and localization policy.",
        "Use stringResource() and Estatia-prefixed design components."
    ),

    // --- PERFORMANCE ---
    LAW_023(
        "LAW-023", "Lifecycle-bound objects (Activity/View) must not be stored in long-lived components.", LawCategory.PERFORMANCE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Storing Activities in ViewModels or Singletons causes permanent memory leaks when the UI is destroyed.",
        "Pass needed data from the Activity or use a safe listener pattern."
    ),
    LAW_024(
        "LAW-024", "Long-lived components must not hold direct references to UI Context.", LawCategory.PERFORMANCE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Holding a non-application Context in a Singleton prevents Activity garbage collection.",
        "Inject @ApplicationContext or extract needed values into a plain data class."
    ),

    // --- UI GOVERNANCE ---
    LAW_025(
        "LAW-025", "Composables must not read from mutable singletons directly.", LawCategory.UI_GOVERNANCE, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Reading non-observable 'var' from objects prevents recomposition and leads to stale data.",
        "Wrap the singleton state in a Flow and collect it as State in the Composable."
    ),

    // --- PERFORMANCE ---
    LAW_026(
        "LAW-026", "Expensive object creation must be cached via remember.", LawCategory.PERFORMANCE, Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "Recreating Regex, Bitmaps, or Formatters on every recomposition causes significant performance jank.",
        "Wrap the instantiation in a remember { ... } block."
    ),

    // --- UI GOVERNANCE ---
    LAW_027(
        "LAW-027", "Composables must not directly call domain or data layer components.", LawCategory.UI_GOVERNANCE, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Direct Repository/UseCase calls in UI code bypasses the ViewModel and breaks the UDF architecture.",
        "Expose a UI state from the ViewModel and trigger actions via lambda events."
    ),

    // --- CODE HEALTH ---
    LAW_028(
        "LAW-028", "Methods must be concise and focused (Complexity Budget).", LawCategory.CODE_HEALTH, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Excessive cyclomatic complexity or method length indicates poor separation of concerns.",
        "Refactor the method by extracting logical blocks into smaller, private helper functions."
    ),
    LAW_029(
        "LAW-029", "Classes must have a single responsibility (Size Limit).", LawCategory.CODE_HEALTH, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Giant 'God Objects' are impossible to test and maintain.",
        "Decompose the class into smaller, specialized components with clear interfaces."
    ),
    LAW_030(
        "LAW-030", "Constructors must have a limited dependency budget.", LawCategory.CODE_HEALTH, Risk.MEDIUM, Confidence.CERTAIN, Enforcement.BLOCK,
        "Classes requiring 9+ dependencies are 'Orchestration Monsters' that are likely doing too much.",
        "Introduce Facades or use a Delegation pattern to reduce direct coordination overhead."
    ),

    // --- ARCHITECTURE ---
    LAW_031(
        "LAW-031", "Components must not mix architectural layers or responsibilities.", LawCategory.ARCHITECTURE, Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Mixing View logic in a Repository or Network calls in a ViewModel breaks layer isolation.",
        "Strictly separate concerns: Data (IO), Domain (Logic), Presentation (UI)."
    ),
    LAW_032(
        "LAW-032", "Domain and Model layers must remain pure Kotlin (No Frameworks).", LawCategory.ARCHITECTURE, Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Importing Android or Firebase types into the Domain layer prevents platform independence.",
        "Keep the Domain layer free of platform-specific frameworks."
    ),

    // --- INFRASTRUCTURE ---
    LAW_033(
        "LAW-033", "Rule suppressions must follow strict organizational policy.", LawCategory.INFRASTRUCTURE, Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Wildcard suppressions (@Suppress(\"all\")) or un-justified skips weaken the entire enforcement system.",
        "Use specific rule IDs and provide a mandatory '// Justification: ...' comment."
    ),
    LAW_034(
        "LAW-034", "Architectural enforcement must be crash-resilient and regression-tested.", LawCategory.INFRASTRUCTURE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Silent failures in the linter lead to a false sense of security.",
        "Ensure every rule has an active verification specimen in the :core:canary-violations module."
    ),
    LAW_035(
        "LAW-035", "FATAL architectural rules must never be baselined.", LawCategory.INFRASTRUCTURE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Baselines are for technical debt; high-risk structural laws must be fixed immediately.",
        "Fix the violation instead of adding it to lint-baseline.xml."
    ),
    LAW_040(
        "LAW-040", "Baseline Monotonicity: Baseline counts must never increase.", LawCategory.INFRASTRUCTURE, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Allowing the baseline to grow makes static analysis meaningless over time.",
        "Fix existing violations or justify why the baseline cannot be reduced. Never add new ones."
    ),

    // --- API DESIGN ---
    LAW_036(
        "LAW-036", "Domain results should express business meaning via types.", LawCategory.API_DESIGN, Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "Returning generic types (Boolean, String) hides domain intent.",
        "Use sealed classes or specialized domain models to represent rich result states."
    ),

    // --- INFRASTRUCTURE ---
    LAW_037(
        "LAW-037", "Dependencies must be managed via the version catalog.", LawCategory.INFRASTRUCTURE, Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Directly hardcoding dependency versions in build scripts leads to version drift and conflicts.",
        "Move all dependency declarations to gradle/libs.versions.toml."
    ),
    
    // --- RELEASE INTEGRITY (Consolidated LAW-038) ---
    LAW_038(
        "LAW-038", "Release artifact symbol obfuscation integrity.", LawCategory.INFRASTRUCTURE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Leaking internal symbol names (InternalImpl, SecretStore) in production mapping files compromises reverse-engineering protections.",
        "Audit R8 mapping files and update Proguard rules to ensure sensitive internal symbols are correctly obfuscated."
    ),

    // --- CODE HEALTH ---
    LAW_039(
        "LAW-039", "Clean Code: Mandatory constant extraction for literals.", LawCategory.CODE_HEALTH, Risk.LOW, Confidence.HEURISTIC, Enforcement.INFO,
        "Using magic numbers or strings directly in logic makes the code difficult to maintain and reason about.",
        "Extract literals to named constants in a Companion object or Top-level file."
    );

    override fun toString(): String = id
}
