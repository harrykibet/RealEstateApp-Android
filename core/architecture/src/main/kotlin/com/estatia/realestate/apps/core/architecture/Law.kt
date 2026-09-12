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
 * The engine responsible for enforcing a specific law.
 */
enum class LawEnforcer {
    LINT,    // Real-time IDE feedback and CI report
    KSP,     // Compile-time signature and structure audits
    KONSIST  // Post-compile structural and dependency tests
}

/**
 * Formal Registry of Estatia Architectural Laws.
 * 
 * 🛡️ THE UNIFIED CONTRACT: This enum is the single source of truth for all 
 * architectural governance metadata. Everything from IDE help text to 
 * documentation tables is derived from this registry.
 */
enum class Law(
    val id: String, 
    val description: String, 
    val category: LawCategory,
    val risk: Risk,
    val confidence: Confidence,
    val enforcement: Enforcement,
    val rationale: String,
    val recommendation: String,
    val enforcers: Set<LawEnforcer>,
    val owner: RuleOwner,
    val fixtures: List<String> = emptyList(),
    val invariants: Map<String, LawInvariant>? = null,
    val targetPrecision: Double = 0.95,
    val targetRecall: Double = 1.0
) {
    // --- UI GOVERNANCE ---
    LAW_001(
        "LAW-001", "Presentation-owned UI state.", LawCategory.UI_GOVERNANCE, Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN,
        "Composables should be pure projections of state to ensure testability and prevent business logic leaks.",
        "Move state mutation and complex logic into a ViewModel.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PRODUCT,
        listOf("Law001_Law002_Law026_Law027_ComposeSpec.kt", "CanarySpecHub.kt"),
        targetPrecision = 0.85, targetRecall = 0.95
    ),
    
    // --- ARCHITECTURE ---
    LAW_002(
        "LAW-002", "Mutable state never crosses an ownership boundary.", LawCategory.ARCHITECTURE, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Exposing mutable state containers allows external components to break encapsulation and UDF invariants.",
        "Expose as a read-only StateFlow and use lambda callbacks for mutations.",
        setOf(LawEnforcer.LINT, LawEnforcer.KSP),
        RuleOwner.ARCHITECTURE,
        listOf("Law001_Law002_Law026_Law027_ComposeSpec.kt", "Law012_Law018_Law023_Law029_StateSpec.kt", "LAW_002_Tier1_Syntactic.kt", "LAW_002_Tier2_Semantic.kt", "LAW_002_Tier3_Dynamic.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_003(
        "LAW-003", "Infrastructure leakage prevention.", LawCategory.ARCHITECTURE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Direct dependencies on database or network types in the domain layer prevents infrastructure swapping and complicates testing.",
        "Expose domain models from repositories and use interfaces to hide implementation details.",
        setOf(LawEnforcer.LINT),
        RuleOwner.ARCHITECTURE,
        listOf("Law008_Law009_ApiDesignSpec.kt", "DomainViolations.kt", "Law001_Law002_Law026_Law027_ComposeSpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_004(
        "LAW-004", "Feature module isolation.", LawCategory.ARCHITECTURE, Risk.HIGH, Confidence.HIGH, Enforcement.BLOCK,
        "Direct feature-to-feature coupling leads to massive build times and circular dependencies.",
        "Communicate between features using a shared API module or central navigation.",
        setOf(LawEnforcer.KONSIST, LawEnforcer.LINT),
        RuleOwner.ARCHITECTURE,
        listOf("HomeViolations.kt", "CanarySpecHub.kt"),
        targetPrecision = 0.98, targetRecall = 1.0
    ),

    // --- CONCURRENCY ---
    LAW_005(
        "LAW-005", "No manual CoroutineScopes.", LawCategory.CONCURRENCY, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Manual CoroutineScope instantiation leads to non-deterministic lifecycles and memory leaks.",
        "Use provided scopes (viewModelScope, lifeCycleScope) or inject a managed scope.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law019_Law006_Law013_Law020_Law021_ConcurrencySpec.kt", "Law001_Law002_Law026_Law027_ComposeSpec.kt", "LAW_005_Tier1_Syntactic.kt", "LAW_005_Tier2_Semantic.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_006(
        "LAW-006", "No hardcoded Dispatchers.", LawCategory.CONCURRENCY, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Hardcoding dispatchers prevents swapping them during testing and violates IO/Main separation policies.",
        "Inject a DispatcherProvider or individual dispatchers via constructor.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law019_Law006_Law013_Law020_Law021_ConcurrencySpec.kt", "Law011_Law024_PerformanceSpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- PERFORMANCE ---
    LAW_007(
        "LAW-007", "Clock-injected wall time.", LawCategory.PERFORMANCE, Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "Direct usage of System.currentTimeMillis() makes time-dependent logic impossible to test deterministically.",
        "Inject and use a TimeProvider.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law007_Law015_Law016_EnvironmentSpec.kt", "CanarySpecHub.kt"),
        targetPrecision = 0.95, targetRecall = 1.0
    ),

    // --- API DESIGN ---
    LAW_008(
        "LAW-008", "Abstraction-only public APIs.", LawCategory.API_DESIGN, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Exposing implementation types (like ArrayList or Retrofit) in public interfaces couples consumers to internal choices.",
        "Use interfaces and standard Kotlin collection types in public signatures.",
        setOf(LawEnforcer.LINT, LawEnforcer.KSP),
        RuleOwner.ARCHITECTURE,
        listOf("Law008_Law009_ApiDesignSpec.kt", "CanaryPlayerEngine.kt", "DomainViolations.kt", "HomeViolations.kt", "FakeChaos.kt", "LAW_008_Tier1_Syntactic.kt", "LAW_008_Tier2_Semantic.kt", "LAW_008_Tier3_Dynamic.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_009(
        "LAW-009", "Explicit failure handling.", LawCategory.API_DESIGN, Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN,
        "Silently catching exceptions or returning empty fallbacks masks critical production failures.",
        "Use AppResult wrappers or rethrow exceptions to the appropriate handler.",
        setOf(LawEnforcer.LINT, LawEnforcer.KSP),
        RuleOwner.ARCHITECTURE,
        listOf("Law008_Law009_ApiDesignSpec.kt"),
        targetPrecision = 0.85, targetRecall = 0.95
    ),

    // --- SECURITY ---
    LAW_010(
        "LAW-010", "Sensitive data protection & Security integrity.", LawCategory.SECURITY, Risk.CRITICAL, Confidence.HEURISTIC, Enforcement.WARN,
        "Hardcoding secrets or logging PII (passwords, tokens, emails) is a major security breach.",
        "Remove sensitive logs, move secrets to secure config, or use build variables.",
        setOf(LawEnforcer.LINT),
        RuleOwner.SECURITY,
        listOf("Law010_SecuritySpec.kt"),
        targetPrecision = 0.85, targetRecall = 0.95
    ),

    // --- PERFORMANCE ---
    LAW_011(
        "LAW-011", "Blocking UI thread work policy.", LawCategory.PERFORMANCE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Blocking UI thread or using unbounded buffers causes ANRs, jank, and OOM.",
        "Move blocking work to Dispatchers.IO and use bounded coroutine buffers.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law011_Law024_PerformanceSpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- CONCURRENCY ---
    LAW_012(
        "LAW-012", "Shared mutable state synchronization.", LawCategory.CONCURRENCY, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Using standard collections (HashMap) in shared components like Singletons leads to data races and crashes.",
        "Use ConcurrentHashMap, Mutex, or AtomicReference for shared mutable state.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law012_Law018_Law023_Law029_StateSpec.kt", "FakeChaos.kt", "LAW_012_Tier1_Syntactic.kt", "LAW_012_Tier2_Semantic.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_013(
        "LAW-013", "Cooperative cancellation.", LawCategory.CONCURRENCY, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Infinite loops or long-running work without cancellation checks prevents scope cleanup and causes leaks.",
        "Add yield() or ensureActive() checks in long-running loops.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law019_Law006_Law013_Law020_Law021_ConcurrencySpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_014(
        "LAW-014", "Thread-confinement enforcement.", LawCategory.CONCURRENCY, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Internal engine components often assume a specific thread (e.g. Main) for safety.",
        "Add Confinement.checkMainThread() at the entry point of sensitive methods.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("CanaryPlayerEngine.kt", "CanarySpecHub.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- PERFORMANCE ---
    LAW_015(
        "LAW-015", "Tests must not depend on real time.", LawCategory.PERFORMANCE, Risk.LOW, Confidence.HEURISTIC, Enforcement.WARN,
        "Tests using system time are non-deterministic and flaky depending on runner speed.",
        "Use TestClock to control time progression in tests.",
        setOf(LawEnforcer.KONSIST, LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law007_Law015_Law016_EnvironmentSpec.kt", "Law015_EnvironmentTestSpec.kt"),
        targetPrecision = 0.85, targetRecall = 0.95
    ),

    // --- ARCHITECTURE ---
    LAW_016(
        "LAW-016", "Tests must strictly remain in test source sets.", LawCategory.ARCHITECTURE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Testing libraries (MockK, JUnit) must never be packaged in production binaries.",
        "Move test code and dependencies to src/test or src/androidTest.",
        setOf(LawEnforcer.KONSIST, LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law007_Law015_Law016_EnvironmentSpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- CODE HEALTH ---
    LAW_017(
        "LAW-017", "Backing-property convention.", LawCategory.CODE_HEALTH, Risk.LOW, Confidence.HEURISTIC, Enforcement.INFO,
        "Using '_' prefix for mutable backing properties prevents accidental external mutation.",
        "Rename the private property to start with '_' (e.g. _uiState).",
        setOf(LawEnforcer.LINT),
        RuleOwner.ARCHITECTURE,
        listOf("Law012_Law018_Law023_Law029_StateSpec.kt"),
        targetPrecision = 0.85, targetRecall = 0.95
    ),

    // --- UI GOVERNANCE ---
    LAW_018(
        "LAW-018", "ViewModel Single Source of Truth.", LawCategory.UI_GOVERNANCE, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Multiple persistent state authorities in a ViewModel create 'Source of Truth' confusion and desynchronized UI.",
        "Consolidate multiple persistent StateFlows into a single UI State data class.",
        setOf(LawEnforcer.KSP),
        RuleOwner.ARCHITECTURE,
        listOf("Law012_Law018_Law023_Law029_StateSpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- CONCURRENCY ---
    LAW_019(
        "LAW-019", "Sequential suspend functions.", LawCategory.CONCURRENCY, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Lauching fire-and-forget work inside a suspend function violates structured concurrency.",
        "Use coroutineScope { launch { ... } } to ensure the work is waited for.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law019_Law006_Law013_Law020_Law021_ConcurrencySpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_020(
        "LAW-020", "Mandatory Deferred joining.", LawCategory.CONCURRENCY, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Ignoring the result of an async block leads to unhandled exceptions and silent logic failure.",
        "Await the deferred result or return it to the caller.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law019_Law006_Law013_Law020_Law021_ConcurrencySpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_021(
        "LAW-021", "Correct CEH placement.", LawCategory.CONCURRENCY, Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "CoroutineExceptionHandler is ignored when passed to withContext or child launchers.",
        "Move the CEH to the root CoroutineScope definition.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law019_Law006_Law013_Law020_Law021_ConcurrencySpec.kt"),
        targetPrecision = 0.95, targetRecall = 1.0
    ),

    // --- UI GOVERNANCE ---
    LAW_022(
        "LAW-022", "Localization & Design consistency.", LawCategory.UI_GOVERNANCE, Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN,
        "Hardcoded strings and standard Material components violate Estatia's design system and localization policy.",
        "Use stringResource() and Estatia-prefixed design components.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PRODUCT,
        listOf("Law022_ComposeSpec.kt"),
        targetPrecision = 0.85, targetRecall = 0.95
    ),

    // --- PERFORMANCE ---
    LAW_023(
        "LAW-023", "Component lifecycle safety.", LawCategory.PERFORMANCE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Storing Activities in ViewModels or Singletons causes permanent memory leaks when the UI is destroyed.",
        "Pass needed data from the Activity or use a safe listener pattern.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law012_Law018_Law023_Law029_StateSpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_024(
        "LAW-024", "Memory leak prevention (Context).", LawCategory.PERFORMANCE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Holding a non-application Context in a Singleton prevents Activity garbage collection.",
        "Inject @ApplicationContext or extract needed values into a plain data class.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PLATFORM,
        listOf("Law011_Law024_PerformanceSpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- UI GOVERNANCE ---
    LAW_025(
        "LAW-025", "No mutable singleton reads in UI.", LawCategory.UI_GOVERNANCE, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Reading non-observable 'var' from objects prevents recomposition and leads to stale data.",
        "Wrap the singleton state in a Flow and collect it as State in the Composable.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PRODUCT,
        listOf("Law025_ComposeSpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- PERFORMANCE ---
    LAW_026(
        "LAW-026", "Efficient recomposition (Caching).", LawCategory.PERFORMANCE, Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "Recreating Regex, Bitmaps, or Formatters on every recomposition causes significant performance jank.",
        "Wrap the instantiation in a remember { ... } block.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PRODUCT,
        listOf("Law001_Law002_Law026_Law027_ComposeSpec.kt"),
        targetPrecision = 0.95, targetRecall = 1.0
    ),

    // --- UI GOVERNANCE ---
    LAW_027(
        "LAW-027", "UDF: No direct data layer calls.", LawCategory.UI_GOVERNANCE, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Direct Repository/UseCase calls in UI code bypasses the ViewModel and breaks the UDF architecture.",
        "Expose a UI state from the ViewModel and trigger actions via lambda events.",
        setOf(LawEnforcer.LINT),
        RuleOwner.ARCHITECTURE,
        listOf("Law001_Law002_Law026_Law027_ComposeSpec.kt", "LAW_027_Tier1_Syntactic.kt", "LAW_027_Tier2_Semantic.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- CODE HEALTH ---
    LAW_028(
        "LAW-028", "Methods must be concise and focused.", LawCategory.CODE_HEALTH, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Excessive cyclomatic complexity or method length indicates poor separation of concerns.",
        "Refactor the method by extracting logical blocks into smaller, private helper functions.",
        setOf(LawEnforcer.LINT),
        RuleOwner.ARCHITECTURE,
        listOf("Law030_ComplexitySpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_029(
        "LAW-029", "Classes must have a single responsibility.", LawCategory.CODE_HEALTH, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Giant 'God Objects' are impossible to test and maintain.",
        "Decompose the class into smaller, specialized components with clear interfaces.",
        setOf(LawEnforcer.LINT),
        RuleOwner.ARCHITECTURE,
        listOf("Law012_Law018_Law023_Law029_StateSpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_030(
        "LAW-030", "Constructors must have a limited dependency budget.", LawCategory.CODE_HEALTH, Risk.MEDIUM, Confidence.CERTAIN, Enforcement.BLOCK,
        "Classes requiring 9+ dependencies are 'Orchestration Monsters' that are likely doing too much.",
        "Introduce Facades or use a Delegation pattern to reduce direct coordination overhead.",
        setOf(LawEnforcer.LINT, LawEnforcer.KSP),
        RuleOwner.ARCHITECTURE,
        listOf("Law030_ComplexitySpec.kt"),
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- ARCHITECTURE ---
    LAW_031(
        "LAW-031", "Components must not mix architectural layers or responsibilities.", LawCategory.ARCHITECTURE, Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Mixing View logic in a Repository or Network calls in a ViewModel breaks layer isolation.",
        "Strictly separate concerns: Data (IO), Domain (Logic), Presentation (UI).",
        setOf(LawEnforcer.KONSIST),
        RuleOwner.ARCHITECTURE,
        targetPrecision = 0.98, targetRecall = 1.0
    ),
    LAW_032(
        "LAW-032", "Domain and Model layers must remain pure Kotlin (No Frameworks).", LawCategory.ARCHITECTURE, Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Importing Android or Firebase types into the Domain layer prevents platform independence.",
        "Keep the Domain layer free of platform-specific frameworks.",
        setOf(LawEnforcer.KONSIST),
        RuleOwner.ARCHITECTURE,
        targetPrecision = 0.98, targetRecall = 1.0
    ),

    // --- INFRASTRUCTURE ---
    LAW_033(
        "LAW-033", "Rule suppressions must follow strict organizational policy.", LawCategory.INFRASTRUCTURE, Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Wildcard suppressions (@Suppress(\"all\")) or un-justified skips weaken the entire enforcement system.",
        "Use specific rule IDs and provide a mandatory '// Justification: ...' comment.",
        setOf(LawEnforcer.LINT),
        RuleOwner.ARCHITECTURE,
        targetPrecision = 0.98, targetRecall = 1.0
    ),
    LAW_034(
        "LAW-034", "Architectural enforcement must be crash-resilient and regression-tested.", LawCategory.INFRASTRUCTURE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Silent failures in the linter lead to a false sense of security.",
        "Ensure every rule has an active verification specimen in the :core:canary-violations module.",
        setOf(LawEnforcer.KONSIST, LawEnforcer.LINT),
        RuleOwner.ARCHITECTURE,
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_035(
        "LAW-035", "FATAL architectural rules must never be baselined.", LawCategory.INFRASTRUCTURE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Baselines are for technical debt; high-risk structural laws must be fixed immediately.",
        "Fix the violation instead of adding it to lint-baseline.xml.",
        setOf(LawEnforcer.KONSIST),
        RuleOwner.ARCHITECTURE,
        targetPrecision = 1.0, targetRecall = 1.0
    ),
    LAW_040(
        "LAW-040", "Baseline monotonicity mandate.", LawCategory.INFRASTRUCTURE, Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK,
        "Allowing the baseline to grow makes static analysis meaningless over time.",
        "Fix existing violations or justify why the baseline cannot be reduced. Never add new ones.",
        setOf(LawEnforcer.KONSIST),
        RuleOwner.ARCHITECTURE,
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- API DESIGN ---
    LAW_036(
        "LAW-036", "Domain results should express business meaning via types.", LawCategory.API_DESIGN, Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN,
        "Returning generic types (Boolean, String) hides domain intent.",
        "Use sealed classes or specialized domain models to represent rich result states.",
        setOf(LawEnforcer.KSP),
        RuleOwner.ARCHITECTURE,
        targetPrecision = 0.95, targetRecall = 1.0
    ),

    // --- INFRASTRUCTURE ---
    LAW_037(
        "LAW-037", "Dependencies must be managed via the version catalog.", LawCategory.INFRASTRUCTURE, Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK,
        "Directly hardcoding dependency versions in build scripts leads to version drift and conflicts.",
        "Move all dependency declarations to gradle/libs.versions.toml.",
        setOf(LawEnforcer.KONSIST),
        RuleOwner.PLATFORM,
        targetPrecision = 0.98, targetRecall = 1.0
    ),
    
    // --- RELEASE INTEGRITY ---
    LAW_038(
        "LAW-038", "Release artifact symbol obfuscation integrity.", LawCategory.INFRASTRUCTURE, Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK,
        "Leaking internal symbol names (InternalImpl, SecretStore) in production mapping files compromises reverse-engineering protections.",
        "Audit R8 mapping files and update Proguard rules to ensure sensitive internal symbols are correctly obfuscated.",
        setOf(LawEnforcer.KONSIST),
        RuleOwner.SECURITY,
        targetPrecision = 1.0, targetRecall = 1.0
    ),

    // --- CODE HEALTH ---
    LAW_039(
        "LAW-039", "Clean Code: Mandatory constant extraction.", LawCategory.CODE_HEALTH, Risk.LOW, Confidence.HEURISTIC, Enforcement.WARN,
        "Using magic numbers or strings directly in logic makes the code difficult to maintain and reason about.",
        "Extract literals to named constants in a Companion object or Top-level file.",
        setOf(LawEnforcer.LINT),
        RuleOwner.PRODUCT,
        listOf("Law030_ComplexitySpec.kt"),
        targetPrecision = 0.85, targetRecall = 0.95
    ),
    LAW_041(
        "LAW-041", "Mandatory Architectural Identity.", LawCategory.ARCHITECTURE, Risk.HIGH, Confidence.HIGH, Enforcement.BLOCK,
        "Classes in governed modules must explicitly declare their role via annotations to enable semantic safety checks.",
        "Add the appropriate architectural annotation (e.g., @Repository, @Service) to the class.",
        setOf(LawEnforcer.KONSIST, LawEnforcer.KSP),
        RuleOwner.ARCHITECTURE,
        emptyList(),
        mapOf(
            "Repository" to LawInvariant(pathContains = "/core/data/|/core/localization/|/app/"),
            "DataSource" to LawInvariant(pathContains = "/core/network/|/core/database/|/core/config/|/core/data/|/core/datastore/|/core/security/|/core/player-engine/|/app/"),
            "UseCase" to LawInvariant(pathContains = "/core/domain/"),
            "DomainModel" to LawInvariant(pathContains = "/core/domain/|/core/model/|/core/analytics/|/core/common/|/core/player-engine/"),
            "EntityModel" to LawInvariant(pathContains = "/core/network/|/core/database/|/core/data/", mustBeData = true),
            "ViewModelMarker" to LawInvariant(mustInheritFrom = "androidx.lifecycle.ViewModel"),
            "Contract" to LawInvariant(mustBeInterface = true),
            "AppEntryPoint" to LawInvariant(pathContains = "/app/|/feature/"),
            "Coordinator" to LawInvariant(pathContains = "/core/|/feature/"),
            "Manager" to LawInvariant(pathContains = "/core/|/app/"),
            "UiState" to LawInvariant(mustBeDataSealedOrValue = true),
            "Mapper" to LawInvariant(pathContains = "/core/data/|/core/network/|/core/database/"),
            "Foundation" to LawInvariant(pathContains = "/core/common/|/core/localization/|/app/|/core/analytics/|/core/datastore/|/core/player-engine/|/core/security/|/core/config/"),
            "Policy" to LawInvariant(pathContains = "/core/network/|/core/player-engine/|/core/common/"),
            "UiPrimitive" to LawInvariant(pathContains = "/core/design-system/|/core/ui/|/core/player-ui/|/app/"),
            "BatteryState" to LawInvariant(mustBeDataSealedOrValue = true, pathContains = "/core/common/"),
            "NetworkState" to LawInvariant(mustBeDataSealedOrValue = true, pathContains = "/core/model/"),
            "EnvironmentState" to LawInvariant(mustBeDataSealedOrValue = true, pathContains = "/core/model/"),
            "AnalyticsState" to LawInvariant(mustBeDataSealedOrValue = true, pathContains = "/core/model/|/core/analytics/"),
            "AuthState" to LawInvariant(mustBeDataSealedOrValue = true, pathContains = "/core/common/|/core/domain/|/core/model/|/feature/auth/"),
            "PlayerState" to LawInvariant(mustBeDataSealedOrValue = true, pathContains = "/core/player-engine/|/core/player-ui/"),
            "UiAction" to LawInvariant(mustBeDataSealedOrValue = true, pathContains = "/feature/"),
            "UiEvent" to LawInvariant(mustBeDataSealedOrValue = true, pathContains = "/feature/"),
            "UiScreen" to LawInvariant(mustBeComposable = true, pathContains = "/feature/|/app/"),
            "UiComponent" to LawInvariant(mustBeComposable = true, pathContains = "/feature/|/core/ui/|/core/player-ui/|/app/"),
            "UiPrimitiveFunction" to LawInvariant(mustBeComposable = true, pathContains = "/core/design-system/|/core/ui/|/app/"),
            "UiRoute" to LawInvariant(mustBeComposable = true, pathContains = "/feature/|/app/")
        ),
        targetPrecision = 0.98, targetRecall = 1.0
    );

    override fun toString(): String = id
}
