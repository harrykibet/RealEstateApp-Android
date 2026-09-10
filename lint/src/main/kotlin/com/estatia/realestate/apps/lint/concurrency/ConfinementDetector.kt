package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * Ensures critical infrastructure classes implement thread-confinement checks.
 */
class ConfinementDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (node is UAnonymousClass) return
            
            // 🛡️ SEMANTIC RESOLUTION: Check for critical modules via package and Singleton status
            val qualifiedName = node.qualifiedName ?: ""
            val isCriticalModule = qualifiedName.contains(".core.player_engine") || 
                                  qualifiedName.contains(".core.security") ||
                                  qualifiedName.contains(".core.data")

            if (isCriticalModule && !node.isInterface && hasSingletonAnnotation(context, node)) {
                node.methods.forEach { method ->
                    if (context.evaluator.isPublic(method) && !method.isConstructor && !isGenerated(method)) {
                        checkConfinement(context, method)
                    }
                }
            }
        }
    }

    private fun isGenerated(method: UMethod): Boolean {
        // Detect synthetic overloads from @JvmOverloads
        return method.sourcePsi == null || method.javaPsi.annotations.any { it.qualifiedName?.contains("JvmOverloads") == true }
    }

    private fun hasSingletonAnnotation(context: JavaContext, node: UClass) = 
        context.evaluator.getAnnotations(node.javaPsi, false)
            .any { it.qualifiedName?.contains("Singleton") == true }

    private fun checkConfinement(context: JavaContext, method: UMethod) {
        val body = method.uastBody ?: return
        
        var foundCheck = false
        body.accept(object : AbstractUastVisitor() {
            override fun visitCallExpression(node: UCallExpression): Boolean {
                val resolved = node.resolve()
                if (resolved != null) {
                    val evaluator = context.evaluator
                    // Official Estatia Confinement Utility
                    if (evaluator.isMemberInClass(resolved, "com.estatia.realestate.apps.core.common.concurrency.Confinement")) {
                        foundCheck = true
                    }
                    // Jetpack Arch Components
                    if (evaluator.isMemberInClass(resolved, "androidx.lifecycle.LiveData") && resolved.name == "assertMainThread") {
                        foundCheck = true
                    }
                } else {
                    // Fallback for cases where resolution fails but the intention is clear
                    val name = node.methodName
                    if (name == "checkMainThread" && node.receiver?.asRenderString()?.contains("Confinement") == true) {
                        foundCheck = true
                    }
                }
                return super.visitCallExpression(node)
            }
        })

        if (!foundCheck) {
            context.report(
                ISSUE,
                method,
                context.getLocation(method as UElement),
                "Critical infrastructure method '${method.name}' is missing a thread-confinement check. " +
                        "Use 'Confinement.checkMainThread()' to satisfy architectural safety (LAW-014)."
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
            architectureLaw = Law.LAW_014,
            implementation = Implementation(ConfinementDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
