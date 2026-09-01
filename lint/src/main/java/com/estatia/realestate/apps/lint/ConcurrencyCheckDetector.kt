package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

/**
 * Detector that ensures critical infrastructure classes implement thread-confinement 
 * checks in their public APIs.
 */
class ConcurrencyCheckDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            val qualifiedName = node.qualifiedName ?: return
            
            // Focus on critical infrastructure packages
            val isCritical = qualifiedName.contains(".player_engine") || 
                             qualifiedName.contains(".security") ||
                             qualifiedName.contains(".persistence")

            if (isCritical && !node.isInterface && hasSingletonAnnotation(node)) {
                node.methods.forEach { method ->
                    if (context.evaluator.isPublic(method) && !method.isConstructor) {
                        checkConfinement(context, method)
                    }
                }
            }
        }
    }

    private fun hasSingletonAnnotation(node: UClass): Boolean {
        return node.annotations.any { 
            val name = it.qualifiedName
            name == "javax.inject.Singleton" || name == "jakarta.inject.Singleton"
        }
    }

    private fun checkConfinement(context: JavaContext, method: UMethod) {
        val body = method.uastBody ?: return
        val firstStatement = (body as? UBlockExpression)?.expressions?.firstOrNull()?.asRenderString() ?: ""
        
        // Check for common confinement check patterns
        val hasCheck = firstStatement.contains("checkConfinement") || 
                       firstStatement.contains("getMainLooper") ||
                       firstStatement.contains("assertMainThread")

        if (!hasCheck) {
            context.report(
                ISSUE,
                method,
                context.getLocation(method),
                "Critical infrastructure method '${method.name}' is missing a thread-confinement check. " +
                        "Add 'checkConfinement()' at the start of this method to prevent multi-threaded corruption."
            )
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "MissingConcurrencyCheck",
            briefDescription = "Missing Concurrency Check",
            explanation = """
                Critical infrastructure classes that manage hardware or sensitive state (like 
                ExoPlayer or encryption keys) must enforce thread confinement. 
                
                Failing to check if a method is called from the correct thread (usually Main) 
                can lead to non-deterministic data races and state corruption in production.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(
                ConcurrencyCheckDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
