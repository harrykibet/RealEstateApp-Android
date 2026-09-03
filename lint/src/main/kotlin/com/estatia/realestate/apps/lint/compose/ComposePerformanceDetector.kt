package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * Enforces LAW-026: "Expensive object creation must be cached via remember."
 * Detects instantiation of expensive types (Regex, Bitmap, etc.) directly in Composables.
 */
class ComposePerformanceDetector : Detector(), SourceCodeScanner {

    private val expensiveTypes = setOf(
        "android.graphics.Bitmap",
        "java.util.regex.Pattern",
        "kotlin.text.Regex",
        "java.text.SimpleDateFormat",
        "android.media.MediaPlayer",
        "java.util.Scanner"
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitCallExpression(node: UCallExpression) {
            if (!isInsideComposable(node)) return

            // Check if it's a constructor call or an expensive factory method
            val method = node.resolve() ?: return
            if (method.isConstructor) {
                val type = method.containingClass?.qualifiedName ?: ""
                if (expensiveTypes.contains(type)) {
                    if (!isInsideRemember(node)) {
                        context.report(
                            EXPENSIVE_RECOMPOSITION_ISSUE,
                            node,
                            context.getLocation(node),
                            "Expensive object '$type' created on every recomposition. Wrap this in 'remember { ... }' to improve performance (LAW-026)."
                        )
                    }
                }
            }
        }
    }

    private fun isInsideComposable(node: UElement): Boolean {
        val method = node.getParentOfType<UMethod>() ?: return false
        return method.javaPsi.annotations.any { it.qualifiedName?.contains("Composable") == true }
    }

    private fun isInsideRemember(node: UElement): Boolean {
        var current = node.uastParent
        while (current != null && current !is UMethod) {
            if (current is UCallExpression) {
                val name = current.methodName ?: ""
                if (name.startsWith("remember")) return true
            }
            current = current.uastParent
        }
        return false
    }

    companion object {
        val EXPENSIVE_RECOMPOSITION_ISSUE = EstatiaIssue.create(
            id = "ExpensiveRecomposition",
            description = "Expensive object created on recomposition",
            rationale = "Creating heavy objects like Bitmaps or Regex in Composables causes jank.",
            badExample = "@Composable fun UI() { val regex = Regex(\"...\") }",
            goodExample = "@Composable fun UI() { val regex = remember { Regex(\"...\") } }",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PRODUCT,
            architectureLaw = "LAW-026 (Recomposition Safety)",
            implementation = Implementation(ComposePerformanceDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
