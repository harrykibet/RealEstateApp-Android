package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.estatia.realestate.apps.core.architecture.Law

/**
 * Factory for creating structured Lint issues following Estatia engineering policy.
 */
object EstatiaIssue {
    fun create(
        id: String,
        description: String,
        rationale: String,
        badExample: String,
        goodExample: String,
        category: IssueCategory,
        tier: IssueTier,
        owner: RuleOwner,
        architectureLaw: Law,
        implementation: Implementation,
        autofixAvailable: Boolean = false
    ): Issue {
        val fullExplanation = """
            |**Rationale**
            |$rationale
            |
            |**Bad Example**
            |```kotlin
            |${badExample.trimIndent()}
            |```
            |
            |**Good Example**
            |```kotlin
            |${goodExample.trimIndent()}
            |```
            |
            |**Architecture Law**: ${architectureLaw.id} (${architectureLaw.description})
            |**Risk**: ${architectureLaw.risk.name}
            |**Confidence**: ${architectureLaw.confidence.name}
            |**Enforcement**: ${architectureLaw.enforcement.name}
            |**Category**: ${category.name}
            |**Owner**: ${owner.handle}
            |**Autofix Available**: $autofixAvailable
        """.trimMargin()

        return Issue.create(
            id = id,
            briefDescription = description,
            explanation = fullExplanation,
            category = category.lintCategory,
            priority = 7,
            severity = tier.severity,
            implementation = implementation
        )
    }
}
