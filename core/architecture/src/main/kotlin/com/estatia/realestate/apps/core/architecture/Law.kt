package com.estatia.realestate.apps.core.architecture

/**
 * Defines the nature and enforcement weight of an architectural rule.
 * This is about the "Business Impact" and "Release Block" status.
 */
enum class LawType {
    /** 
     * Non-negotiable structural rules. Violation blocks the build. 
     * MUST be backed by NON_BYPASSABLE fidelity.
     */
    FATAL,
    
    /** 
     * Core production safety rules. Must be resolved before release. 
     */
    ERROR,
    
    /** Design smells or best-practice deviations. */
    WARNING,
    
    /** Syntactic or organizational style preferences. */
    STYLE,
    
    /** Estatia-specific design patterns that promote consistency but aren't structural laws. */
    CONVENTION
}

/**
 * Technical confidence levels for architectural enforcement.
 */
enum class Fidelity {
    /** 
     * High-fidelity symbol resolution (KSP, Semantic Lint). 
     * Cannot be bypassed by FQN, aliases, or star imports.
     */
    NON_BYPASSABLE,
    
    /** 
     * Import and Topology analysis (Konsist). 
     * Reliable for module/package boundaries but susceptible to FQN bypass.
     */
    STRUCTURAL,
    
    /** 
     * Pattern/Keyword matching (Syntactic Lint, Grep). 
     * Heuristic-based. Excellent for fast feedback but can have false negatives.
     */
    HEURISTIC
}

/**
 * Formal Registry of Estatia Architectural Laws.
 * 
 * This enum acts as the Single Source of Truth (SSoT) for law IDs across 
 * all enforcement layers (Lint, KSP, Konsist).
 */
enum class Law(
    val id: String, 
    val description: String, 
    val type: LawType,
    val primaryFidelity: Fidelity
) {
    LAW_001("LAW-001", "Presentation owns UI state.", LawType.CONVENTION, Fidelity.HEURISTIC),
    LAW_002("LAW-002", "Mutable state never crosses an ownership boundary.", LawType.ERROR, Fidelity.NON_BYPASSABLE),
    LAW_003("LAW-003", "Infrastructure does not leak into domain or presentation.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_004("LAW-004", "Feature modules cannot depend on other feature modules.", LawType.FATAL, Fidelity.STRUCTURAL), // Topology based
    LAW_005("LAW-005", "Production code does not create coroutine scopes.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_006("LAW-006", "Production code does not choose dispatchers directly.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_007("LAW-007", "Production code does not use wall-clock time directly.", LawType.ERROR, Fidelity.NON_BYPASSABLE),
    LAW_008("LAW-008", "Public APIs expose abstractions, not implementation types.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_009("LAW-009", "Production functions do not silently discard failures.", LawType.CONVENTION, Fidelity.HEURISTIC),
    LAW_010("LAW-010", "Sensitive data never enters application logs.", LawType.ERROR, Fidelity.HEURISTIC),
    LAW_011("LAW-011", "Blocking work never executes on the main thread.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_012("LAW-012", "Shared mutable state requires explicit synchronization.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_013("LAW-013", "Lifecycle-owned work must be cancellable.", LawType.ERROR, Fidelity.NON_BYPASSABLE),
    LAW_014("LAW-014", "Critical infrastructure must enforce thread-confinement.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_015("LAW-015", "Tests must not depend on real time.", LawType.WARNING, Fidelity.HEURISTIC),
    LAW_016("LAW-016", "Tests must strictly remain in test source sets.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_017("LAW-017", "Mutable state must follow the backing-property convention.", LawType.STYLE, Fidelity.HEURISTIC),
    LAW_018("LAW-018", "ViewModels must expose a single canonical persistent UI-state owner.", LawType.ERROR, Fidelity.NON_BYPASSABLE),
    LAW_019("LAW-019", "Suspend functions must not secretly launch independent work.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_020("LAW-020", "Async results (Deferred) must be joined or returned.", LawType.ERROR, Fidelity.NON_BYPASSABLE),
    LAW_021("LAW-021", "Exception handlers must be placed on root scopes.", LawType.WARNING, Fidelity.NON_BYPASSABLE),
    LAW_022("LAW-022", "UI must remain localized and accessible.", LawType.CONVENTION, Fidelity.HEURISTIC),
    LAW_023("LAW-023", "Lifecycle-bound objects (Activity/View) must not be stored in long-lived components.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_024("LAW-024", "Long-lived components must not hold direct references to UI Context.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_025("LAW-025", "Composables must not read from mutable singletons directly.", LawType.ERROR, Fidelity.NON_BYPASSABLE),
    LAW_026("LAW-026", "Expensive object creation must be cached via remember.", LawType.WARNING, Fidelity.NON_BYPASSABLE),
    LAW_027("LAW-027", "Composables must not directly call domain or data layer components.", LawType.ERROR, Fidelity.NON_BYPASSABLE),
    LAW_028("LAW-028", "Methods must be concise and focused (Complexity Budget).", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_029("LAW-029", "Classes must have a single responsibility (Size Limit).", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_030("LAW-030", "Constructors must have a limited dependency budget.", LawType.ERROR, Fidelity.NON_BYPASSABLE),
    LAW_031("LAW-031", "Components must not mix architectural layers or responsibilities.", LawType.FATAL, Fidelity.STRUCTURAL),
    LAW_032("LAW-032", "Domain and Model layers must remain pure Kotlin (No Frameworks).", LawType.FATAL, Fidelity.STRUCTURAL),
    LAW_033("LAW-033", "Rule suppressions must follow strict organizational policy.", LawType.FATAL, Fidelity.STRUCTURAL),
    LAW_034("LAW-034", "Architectural enforcement must be crash-resilient and regression-tested.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_035("LAW-035", "FATAL architectural rules must never be baselined.", LawType.FATAL, Fidelity.NON_BYPASSABLE),
    LAW_036("LAW-036", "Domain results should express business meaning via types.", LawType.CONVENTION, Fidelity.NON_BYPASSABLE),
    LAW_037("LAW-037", "Dependencies must be managed via the version catalog.", LawType.FATAL, Fidelity.STRUCTURAL),
    LAW_038("LAW-038", "Production binaries must remain pure and obfuscated.", LawType.FATAL, Fidelity.STRUCTURAL);

    override fun toString(): String = id
}
