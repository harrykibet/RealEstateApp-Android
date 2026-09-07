package com.estatia.realestate.apps.core.architecture

/**
 * Defines the nature and enforcement weight of an architectural rule.
 */
enum class LawType {
    /** Non-negotiable structural rules. Violation blocks the build. */
    FATAL,
    
    /** Core production safety rules. Must be resolved before merge. */
    ERROR,
    
    /** Design smells or best-practice deviations. */
    WARNING,
    
    /** Syntactic or organizational style preferences. */
    STYLE,
    
    /** Estatia-specific design patterns that promote consistency but aren't structural laws. */
    CONVENTION
}

/**
 * Formal Registry of Estatia Architectural Laws.
 * 
 * This enum acts as the Single Source of Truth (SSoT) for law IDs across 
 * all enforcement layers (Lint, KSP, Konsist).
 */
enum class Law(val id: String, val description: String, val type: LawType) {
    LAW_001("LAW-001", "Presentation owns UI state.", LawType.CONVENTION),
    LAW_002("LAW-002", "Mutable state never crosses an ownership boundary.", LawType.ERROR),
    LAW_003("LAW-003", "Infrastructure does not leak into domain or presentation.", LawType.FATAL),
    LAW_004("LAW-004", "Feature modules cannot depend on other feature modules.", LawType.FATAL),
    LAW_005("LAW-005", "Production code does not create coroutine scopes.", LawType.FATAL),
    LAW_006("LAW-006", "Production code does not choose dispatchers directly.", LawType.FATAL),
    LAW_007("LAW-007", "Production code does not use wall-clock time directly.", LawType.ERROR),
    LAW_008("LAW-008", "Public APIs expose abstractions, not implementation types.", LawType.FATAL),
    LAW_009("LAW-009", "Production functions do not silently discard failures.", LawType.CONVENTION),
    LAW_010("LAW-010", "Sensitive data never enters application logs.", LawType.FATAL),
    LAW_011("LAW-011", "Blocking work never executes on the main thread.", LawType.FATAL),
    LAW_012("LAW-012", "Shared mutable state requires explicit synchronization.", LawType.FATAL),
    LAW_013("LAW-013", "Lifecycle-owned work must be cancellable.", LawType.ERROR),
    LAW_014("LAW-014", "Critical infrastructure must enforce thread-confinement.", LawType.FATAL),
    LAW_015("LAW-015", "Tests must not depend on real time.", LawType.WARNING),
    LAW_016("LAW-016", "Tests must strictly remain in test source sets.", LawType.FATAL),
    LAW_017("LAW-017", "Mutable state must follow the backing-property convention.", LawType.STYLE),
    LAW_018("LAW-018", "ViewModels must expose a single canonical persistent UI-state owner.", LawType.ERROR),
    LAW_019("LAW-019", "Suspend functions must not secretly launch independent work.", LawType.FATAL),
    LAW_020("LAW-020", "Async results (Deferred) must be joined or returned.", LawType.ERROR),
    LAW_021("LAW-021", "Exception handlers must be placed on root scopes.", LawType.WARNING),
    LAW_022("LAW-022", "UI must remain localized and accessible.", LawType.CONVENTION),
    LAW_023("LAW-023", "Lifecycle-bound objects (Activity/View) must not be stored in long-lived components.", LawType.FATAL),
    LAW_024("LAW-024", "Long-lived components must not hold direct references to UI Context.", LawType.FATAL),
    LAW_025("LAW-025", "Composables must not read from mutable singletons directly.", LawType.ERROR),
    LAW_026("LAW-026", "Expensive object creation must be cached via remember.", LawType.WARNING),
    LAW_027("LAW-027", "Composables must not directly call domain or data layer components.", LawType.ERROR),
    LAW_028("LAW-028", "Methods must be concise and focused (Complexity Budget).", LawType.FATAL),
    LAW_029("LAW-029", "Classes must have a single responsibility (Size Limit).", LawType.FATAL),
    LAW_030("LAW-030", "Constructors must have a limited dependency budget.", LawType.ERROR),
    LAW_031("LAW-031", "Components must not mix architectural layers or responsibilities.", LawType.FATAL),
    LAW_032("LAW-032", "Domain and Model layers must remain pure Kotlin (No Frameworks).", LawType.FATAL),
    LAW_033("LAW-033", "Rule suppressions must follow strict organizational policy.", LawType.FATAL),
    LAW_034("LAW-034", "Architectural enforcement must be crash-resilient and regression-tested.", LawType.FATAL),
    LAW_035("LAW-035", "FATAL architectural rules must never be baselined.", LawType.FATAL),
    LAW_036("LAW-036", "Domain results should express business meaning via types.", LawType.CONVENTION);

    override fun toString(): String = id
}
