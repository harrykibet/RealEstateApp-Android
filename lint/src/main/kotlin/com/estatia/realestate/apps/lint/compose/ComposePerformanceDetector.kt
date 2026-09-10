package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
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
            if (!isInsideComposable(context, node)) return

            // Check if it's a constructor call or an expensive factory method
            val method = node.resolve() ?: return
            if (method.isConstructor) {
                val type = method.containingClass?.qualifiedName ?: ""
                if (expensiveTypes.contains(type)) {
                    if (!isInsideRemember(context, node)) {
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

    private fun isInsideComposable(context: JavaContext, node: UElement): Boolean {
        val method = node.getParentOfType<UMethod>() ?: return false
        return context.evaluator.getAnnotations(method.javaPsi, false)
            .any { it.qualifiedName == "androidx.compose.runtime.Composable" }
    }

    private fun isInsideRemember(context: JavaContext, node: UElement): Boolean {
        var current = node.uastParent
        while (current != null && current !is UMethod) {
            if (current is UCallExpression) {
                val method = current.resolve()
                if (method != null && isMemberInPackage(method, "androidx.compose.runtime") &&
                    method.name.startsWith("remember")) {
                    return true
                }
            }
            current = current.uastParent
        }
        return false
    }

    private fun isMemberInPackage(method: PsiMethod, packageName: String): Boolean {
        val qualifiedName = method.containingClass?.qualifiedName ?: return false
        return qualifiedName.startsWith("$packageName.") || qualifiedName == packageName
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
            architectureLaw = Law.LAW_026,
            implementation = Implementation(ComposePerformanceDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
