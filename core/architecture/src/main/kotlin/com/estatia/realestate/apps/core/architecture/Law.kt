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
    val enforcement: Enforcement
) {
    LAW_001("LAW-001", "Presentation owns UI state.", Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN),
    LAW_002("LAW-002", "Mutable state never crosses an ownership boundary.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_003("LAW-003", "Infrastructure does not leak into domain or presentation.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_004("LAW-004", "Feature modules cannot depend on other feature modules.", Risk.HIGH, Confidence.HIGH, Enforcement.BLOCK),
    LAW_005("LAW-005", "Production code does not create coroutine scopes.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_006("LAW-006", "Production code does not choose dispatchers directly.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_007("LAW-007", "Production code does not use wall-clock time directly.", Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN),
    LAW_008("LAW-008", "Public APIs expose abstractions, not implementation types.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_009("LAW-009", "Production functions do not silently discard failures.", Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN),
    LAW_010("LAW-010", "Sensitive data never enters application logs.", Risk.CRITICAL, Confidence.HEURISTIC, Enforcement.WARN),
    LAW_011("LAW-011", "Blocking work never executes on the main thread.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_012("LAW-012", "Shared mutable state requires explicit synchronization.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_013("LAW-013", "Lifecycle-owned work must be cancellable.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_014("LAW-014", "Critical infrastructure must enforce thread-confinement.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_015("LAW-015", "Tests must not depend on real time.", Risk.LOW, Confidence.HEURISTIC, Enforcement.WARN),
    LAW_016("LAW-016", "Tests must strictly remain in test source sets.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_017("LAW-017", "Mutable state must follow the backing-property convention.", Risk.LOW, Confidence.HEURISTIC, Enforcement.INFO),
    LAW_018("LAW-018", "ViewModels must expose a single canonical persistent UI-state owner.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_019("LAW-019", "Suspend functions must not secretly launch independent work.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_020("LAW-020", "Async results (Deferred) must be joined or returned.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_021("LAW-021", "Exception handlers must be placed on root scopes.", Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN),
    LAW_022("LAW-022", "UI must remain localized and accessible.", Risk.MEDIUM, Confidence.HEURISTIC, Enforcement.WARN),
    LAW_023("LAW-023", "Lifecycle-bound objects (Activity/View) must not be stored in long-lived components.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_024("LAW-024", "Long-lived components must not hold direct references to UI Context.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_025("LAW-025", "Composables must not read from mutable singletons directly.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_026("LAW-026", "Expensive object creation must be cached via remember.", Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN),
    LAW_027("LAW-027", "Composables must not directly call domain or data layer components.", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_028("LAW-028", "Methods must be concise and focused (Complexity Budget).", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_029("LAW-029", "Classes must have a single responsibility (Size Limit).", Risk.HIGH, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_030("LAW-030", "Constructors must have a limited dependency budget.", Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN),
    LAW_031("LAW-031", "Components must not mix architectural layers or responsibilities.", Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK),
    LAW_032("LAW-032", "Domain and Model layers must remain pure Kotlin (No Frameworks).", Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK),
    LAW_033("LAW-033", "Rule suppressions must follow strict organizational policy.", Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK),
    LAW_034("LAW-034", "Architectural enforcement must be crash-resilient and regression-tested.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_035("LAW-035", "FATAL architectural rules must never be baselined.", Risk.CRITICAL, Confidence.CERTAIN, Enforcement.BLOCK),
    LAW_036("LAW-036", "Domain results should express business meaning via types.", Risk.MEDIUM, Confidence.CERTAIN, Enforcement.WARN),
    LAW_037("LAW-037", "Dependencies must be managed via the version catalog.", Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK),
    LAW_038("LAW-038", "Production binaries must remain pure and obfuscated.", Risk.CRITICAL, Confidence.HIGH, Enforcement.BLOCK);

    override fun toString(): String = id
}
