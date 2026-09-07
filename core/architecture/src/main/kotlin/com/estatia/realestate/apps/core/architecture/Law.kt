package com.estatia.realestate.apps.core.architecture

/**
 * Formal Registry of Estatia Architectural Laws.
 * 
 * This enum acts as the Single Source of Truth (SSoT) for law IDs across 
 * all enforcement layers (Lint, KSP, Konsist).
 */
enum class Law(val id: String, val description: String) {
    LAW_001("LAW-001", "Presentation owns UI state."),
    LAW_002("LAW-002", "Mutable state never crosses an ownership boundary."),
    LAW_003("LAW-003", "Infrastructure does not leak into domain or presentation."),
    LAW_004("LAW-004", "Feature modules cannot depend on other feature modules."),
    LAW_005("LAW-005", "Production code does not create coroutine scopes."),
    LAW_006("LAW-006", "Production code does not choose dispatchers directly."),
    LAW_007("LAW-007", "Production code does not use wall-clock time directly."),
    LAW_008("LAW-008", "Public APIs expose abstractions, not implementation types."),
    LAW_009("LAW-009", "Production functions do not silently discard failures."),
    LAW_010("LAW-010", "Sensitive data never enters application logs."),
    LAW_011("LAW-011", "Blocking work never executes on the main thread."),
    LAW_012("LAW-012", "Shared mutable state requires explicit synchronization."),
    LAW_013("LAW-013", "Lifecycle-owned work must be cancellable."),
    LAW_014("LAW-014", "Critical infrastructure must enforce thread-confinement."),
    LAW_015("LAW-015", "Tests must not depend on real time."),
    LAW_016("LAW-016", "Tests must strictly remain in test source sets."),
    LAW_017("LAW-017", "Mutable state must follow the backing-property convention."),
    LAW_018("LAW-018", "UI components do not own mutable state sources (UDF)."),
    LAW_019("LAW-019", "Suspend functions must not secretly launch independent work."),
    LAW_020("LAW-020", "Async results (Deferred) must be joined or returned."),
    LAW_021("LAW-021", "Exception handlers must be placed on root scopes."),
    LAW_022("LAW-022", "UI must remain localized and accessible."),
    LAW_023("LAW-023", "Lifecycle-bound objects (Activity/View) must not be stored in long-lived components."),
    LAW_024("LAW-024", "Long-lived components must not hold direct references to UI Context."),
    LAW_025("LAW-025", "Composables must not read from mutable singletons directly."),
    LAW_026("LAW-026", "Expensive object creation must be cached via remember."),
    LAW_027("LAW-027", "Composables must not directly call domain or data layer components."),
    LAW_028("LAW-028", "Methods must be concise and focused (Complexity Budget)."),
    LAW_029("LAW-029", "Classes must have a single responsibility (Size Limit)."),
    LAW_030("LAW-030", "Constructors must have a limited dependency budget."),
    LAW_031("LAW-031", "Components must not mix architectural layers or responsibilities."),
    LAW_032("LAW-032", "Domain and Model layers must remain pure Kotlin (No Frameworks)."),
    LAW_033("LAW-033", "Rule suppressions must follow strict organizational policy."),
    LAW_034("LAW-034", "Architectural enforcement must be crash-resilient and regression-tested."),
    LAW_035("LAW-035", "FATAL architectural rules must never be baselined.");

    override fun toString(): String = id
}
