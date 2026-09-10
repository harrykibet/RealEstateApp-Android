package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * LAW-027: UI/Data Decoupling.
 * Prevents direct Repository/Service/UseCase calls inside Composable functions.
 */
class Law027_ComposeArchitectureLeakageDetector : Detector(), SourceCodeScanner {

    private val targetAnnotations = setOf(
        "com.estatia.realestate.apps.core.architecture.annotations.Repository",
        "com.estatia.realestate.apps.core.architecture.annotations.Service",
        "com.estatia.realestate.apps.core.architecture.annotations.UseCase"
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitCallExpression(node: UCallExpression) {
            if (!isInsideComposable(context, node)) return
            
            val method = node.resolve()
            val containingClass = method?.containingClass ?: return
            
            val isArchComponent = context.evaluator.getAnnotations(containingClass, false)
                .any { targetAnnotations.contains(it.qualifiedName) }

            if (isArchComponent) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Direct call to architectural component '${containingClass.name}' inside Composable. " +
                            "This work must be managed by a ViewModel to ensure proper lifecycle handling (LAW-027)."
                )
            }
        }
    }

    private fun isInsideComposable(context: JavaContext, node: UElement): Boolean {
        var current: UElement? = node
        while (current != null) {
            if (current is UMethod) {
                if (context.evaluator.getAnnotations(current.javaPsi, false)
                    .any { it.qualifiedName == "androidx.compose.runtime.Composable" }) {
                    return true
                }
            }
            current = current.uastParent
        }
        return false
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ComposeArchitectureLeakage",
            description = "Architecture component called in Composable",
            rationale = "Directly calling Repositories in Composables breaks UDF and testability.",
            badExample = "@Composable fun List() { repository.load() }",
            goodExample = "@Composable fun List(data: List<Item>) { ... }",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_027,
            implementation = Implementation(Law027_ComposeArchitectureLeakageDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
