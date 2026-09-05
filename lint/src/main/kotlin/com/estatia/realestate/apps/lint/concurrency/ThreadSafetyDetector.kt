package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * Detects usage of non-thread-safe collections or state in multi-threaded environments.
 */
class ThreadSafetyDetector : Detector(), SourceCodeScanner {

    private val unsafeCollections = mapOf(
        "java.util.HashMap" to "ConcurrentHashMap",
        "java.util.ArrayList" to "CopyOnWriteArrayList",
        "HashMap" to "ConcurrentHashMap"
    )

    override fun getApplicableUastTypes() = listOf(UField::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val containingClass = node.getParentOfType<UClass>() ?: return
            if (!isSingleton(context, containingClass)) return

            val type = node.type
            
            unsafeCollections.forEach { (unsafe, safe) ->
                if (context.evaluator.inheritsFrom(context.evaluator.getTypeClass(type), unsafe, false)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node as UElement),
                        "Unsafe collection '$unsafe' used in a Singleton. Use '$safe' or wrap in a mutex (LAW-012)."
                    )
                }
            }
        }
    }

    private fun isSingleton(context: JavaContext, node: UClass): Boolean {
        return context.evaluator.getAnnotations(node.javaPsi, false)
            .any { it.qualifiedName?.contains("Singleton") == true }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ThreadSafetyViolation",
            description = "Non-thread-safe state in multi-threaded component",
            rationale = "Standard collections used in Singletons lead to data races and crashes.",
            badExample = "val map = HashMap<String, String>()",
            goodExample = "val map = ConcurrentHashMap<String, String>()",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-012",
            implementation = Implementation(ThreadSafetyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
