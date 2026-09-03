package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.detector.api.Category

/**
 * Categorizes Estatia engineering rules to help developers understand the impact
 * and goal of each check.
 */
enum class IssueCategory(val lintCategory: Category) {
    /** Architectural boundaries, layering, and dependency direction. */
    ARCHITECTURE(Category.CORRECTNESS),
    
    /** Public API shape, visibility, and mutation safety. */
    API_DESIGN(Category.CORRECTNESS),
    
    /** Thread safety, cancellation, and dispatcher usage. */
    CONCURRENCY(Category.CORRECTNESS),
    
    /** Compose-specific best practices and state management. */
    COMPOSE(Category.CORRECTNESS),
    
    /** Data privacy, secrets, and secure storage. */
    SECURITY(Category.SECURITY),
    
    /** Performance-critical path work and memory allocations. */
    PERFORMANCE(Category.PERFORMANCE),
    
    /** Test-only patterns and infrastructure safety. */
    TESTING(Category.TESTING)
}
