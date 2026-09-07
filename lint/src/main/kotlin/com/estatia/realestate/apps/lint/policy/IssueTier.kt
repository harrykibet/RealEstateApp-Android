package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.detector.api.Severity

/**
 * Defines the enforcement level for a rule based on production risk and organizational policy.
 */
enum class IssueTier(val severity: Severity) {
    /** 
     * Non-negotiable architectural or safety violations. Must never enter main.
     */
    FATAL(Severity.FATAL),
    
    /** 
     * Core production safety rules. Must be fixed before release.
     */
    ERROR(Severity.ERROR),
    
    /** 
     * Strong design smells or Estatia conventions that promote consistency. 
     * Requires justification if suppressed.
     */
    WARNING(Severity.WARNING),

    /**
     * Estatia design patterns or preferred conventions.
     */
    CONVENTION(Severity.WARNING),
    
    /** 
     * Syntactic or organizational style preferences.
     */
    STYLE(Severity.INFORMATIONAL),

    /**
     * General guidance or future-proofing advice.
     */
    INFO(Severity.INFORMATIONAL)
}
