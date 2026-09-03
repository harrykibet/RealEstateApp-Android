package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.detector.api.Severity

/**
 * Defines the enforcement level for a rule based on production risk.
 */
enum class IssueTier(val severity: Severity) {
    /** 
     * Architectural or safety violations that must never enter main. 
     * These represent fundamental breakage of the Estatia engineering model.
     */
    FATAL(Severity.FATAL),
    
    /** 
     * Production correctness, security, or concurrency defects. 
     * Must be fixed before release.
     */
    ERROR(Severity.ERROR),
    
    /** 
     * Strong design smells or localized best-practice violations. 
     * Requires justification if suppressed.
     */
    WARNING(Severity.WARNING),
    
    /** 
     * Optimization or maintainability guidance.
     */
    INFO(Severity.INFORMATIONAL)
}
