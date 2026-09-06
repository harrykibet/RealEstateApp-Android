package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * LAW-024: Long-lived components must not hold direct references to UI Context.
 */
class Law024_ContextLeakDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            val isSingleton = context.evaluator.getAnnotations(node.javaPsi, false)
                .any { it.qualifiedName?.contains("Singleton") == true }
            
            if (isSingleton) {
                node.fields.forEach { field ->
                    val typeClass = context.evaluator.getTypeClass(field.type)
                    if (context.evaluator.inheritsFrom(typeClass, "android.content.Context", false)) {
                        context.report(
                            ISSUE,
                            field,
                            context.getLocation(field),
                            "Context stored in a Singleton object (LAW-024)."
                        )
                    }
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ContextLeak",
            description = "Context stored in long-lived component",
            rationale = "Storing Context references in Singletons causes permanent leaks.",
            badExample = "object MySingleton { var context: Context? = null }",
            goodExample = "class MyRepo(@ApplicationContext val context: Context)",
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = Law.LAW_024,
            implementation = Implementation(Law024_ContextLeakDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
