package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * LAW-012: State Synchronization.
 * Detects usage of non-thread-safe collections or state in multi-threaded environments.
 */
class ThreadSafetyDetector : Detector(), SourceCodeScanner {

    private val unsafeCollections = mapOf(
        "java.util.HashMap" to "ConcurrentHashMap",
        "java.util.ArrayList" to "CopyOnWriteArrayList",
        "java.util.HashSet" to "ConcurrentHashMap.newKeySet()",
        "HashMap" to "ConcurrentHashMap",
        "ArrayList" to "CopyOnWriteArrayList",
        "HashSet" to "ConcurrentHashMap.newKeySet()"
    )

    override fun getApplicableUastTypes() = listOf(UField::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val containingClass = node.getParentOfType<UClass>() ?: return
            
            // 🏎️ Risk Surface: Any class that is shared or manages asynchronous work
            if (!isAtRiskComponent(context, containingClass)) return

            val type = node.type
            
            unsafeCollections.forEach { (unsafe, safe) ->
                if (context.evaluator.inheritsFrom(context.evaluator.getTypeClass(type), unsafe, false)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node as UElement),
                        "Unsafe collection '$unsafe' used in a multi-threaded component. " +
                                "Use '$safe' or wrap in a mutex (LAW-012)."
                    )
                }
            }
        }
    }

    private fun isAtRiskComponent(context: JavaContext, node: UClass): Boolean {
        val name = node.name ?: ""
        
        // 1. Long-lived singletons (Hilt)
        val hasSingletonAnnotation = context.evaluator.getAnnotations(node.javaPsi, false)
            .any { 
                val qn = it.qualifiedName ?: ""
                qn.contains("Singleton") || qn.contains("Service") || qn.contains("Repository") 
            }
            
        // 2. ViewModels (Implicitly multi-threaded via viewModelScope)
        val isViewModel = context.evaluator.inheritsFrom(node, "androidx.lifecycle.ViewModel", false) ||
                          name.endsWith("ViewModel")
                          
        // 3. Explicit architectural markers
        val isArchComponent = name.endsWith("Repository") || 
                              name.endsWith("Service") || 
                              name.endsWith("UseCase") ||
                              name.endsWith("Manager")

        return hasSingletonAnnotation || isViewModel || isArchComponent
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ThreadSafetyViolation",
            description = "Non-thread-safe state in multi-threaded component",
            rationale = "Standard collections used in shared components (Singletons, Repositories, ViewModels) " +
                        "lead to data races and crashes when accessed from multiple coroutines.",
            badExample = "val map = HashMap<String, String>()",
            goodExample = "val map = ConcurrentHashMap<String, String>()",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = Law.LAW_012,
            implementation = Implementation(ThreadSafetyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
