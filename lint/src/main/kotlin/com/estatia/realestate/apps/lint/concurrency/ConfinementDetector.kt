package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * Ensures critical infrastructure classes implement thread-confinement checks.
 */
class ConfinementDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            val qualifiedName = node.qualifiedName ?: return
            val isCritical = qualifiedName.contains(".player_engine") || qualifiedName.contains(".security")

            if (isCritical && !node.isInterface && hasSingletonAnnotation(node)) {
                node.methods.forEach { method ->
                    if (context.evaluator.isPublic(method) && !method.isConstructor) {
                        checkConfinement(context, method)
                    }
                }
            }
        }
    }

    private fun hasSingletonAnnotation(node: UClass) = node.annotations.any { 
        it.qualifiedName?.contains("Singleton") == true 
    }

    private fun checkConfinement(context: JavaContext, method: UMethod) {
        val body = method.uastBody ?: return
        val render = body.asRenderString()
        if (!render.contains("checkConfinement") && !render.contains("assertMainThread")) {
            context.report(
                ISSUE,
                method,
                context.getLocation(method),
                "Critical infrastructure method '${method.name}' is missing a thread-confinement check."
            )
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MissingConcurrencyCheck",
            description = "Missing thread-confinement check in critical component",
            rationale = "Sensitive infrastructure must enforce thread confinement to prevent races.",
            badExample = "fun play() { /* no check */ }",
            goodExample = "fun play() { checkConfinement(); ... }",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-014 (Thread Confinement)",
            implementation = Implementation(ConfinementDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
