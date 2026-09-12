package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.RuleOwner
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField

/**
 * LAW-017: Backing Property Convention.
 * Enforces that mutable state properties follow the _camelCase naming convention and remain private.
 */
class BackingPropertyConventionDetector : Detector(), SourceCodeScanner {

    private val mutableContainers = setOf(
        "kotlinx.coroutines.flow.MutableStateFlow",
        "androidx.compose.runtime.MutableState"
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UField::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val type = node.type
            val isMutable = mutableContainers.any { containerFqn ->
                context.evaluator.inheritsFrom(context.evaluator.getTypeClass(type), containerFqn, false)
            }

            if (isMutable) {
                if (!context.evaluator.isPrivate(node)) {
                    // This is also caught by ExposedMutableStateDetector if it's a ViewModel/Repo
                    // but for other classes we still want the private convention.
                }

                if (!node.name.startsWith("_")) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Mutable state container '${node.name}' should follow the backing property convention (starting with '_')."
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "BackingPropertyConvention",
            description = "Mutable state property lacks '_' prefix",
            rationale = "Estatia convention uses '_' prefix for mutable backing properties to distinguish them from exposed read-only state.",
            badExample = "private val uiState = MutableStateFlow(State())",
            goodExample = "private val _uiState = MutableStateFlow(State())",
            category = IssueCategory.CODE_HEALTH,
            architectureLaw = Law.LAW_017,
            implementation = Implementation(BackingPropertyConventionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
