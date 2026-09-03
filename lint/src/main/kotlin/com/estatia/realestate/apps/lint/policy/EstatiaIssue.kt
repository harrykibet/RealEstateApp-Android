package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import java.util.EnumSet

/**
 * Factory for creating structured Lint issues following Estatia engineering policy.
 */
object EstatiaIssue {
    fun create(
        id: String,
        description: String,
        explanation: String,
        category: IssueCategory,
        tier: IssueTier,
        owner: RuleOwner,
        implementation: Implementation
    ): Issue {
        val fullExplanation = """
            $explanation
            
            Tier: ${tier.name}
            Category: ${category.name}
            Owner: ${owner.handle}
        """.trimIndent()

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
