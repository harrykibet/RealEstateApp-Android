package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UField
import org.jetbrains.uast.getParentOfType

/**
 * Detects usage of non-thread-safe collections or state in multi-threaded environments.
 */
class ThreadSafetyDetector : Detector(), SourceCodeScanner {

    private val unsafeCollections = mapOf(
        "java.util.HashMap" to "ConcurrentHashMap",
        "java.util.ArrayList" to "CopyOnWriteArrayList",
        "java.util.HashSet" to "ConcurrentHashMap.newKeySet()"
    )

    override fun getApplicableUastTypes() = listOf(UField::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val containingClass = node.getParentOfType<UClass>() ?: return
            if (!isSingleton(containingClass)) return

            val type = node.type.canonicalText
            unsafeCollections.forEach { (unsafe, safe) ->
                if (type.startsWith(unsafe)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Unsafe collection '$unsafe' used in a Singleton. Use '$safe' or wrap in a mutex to prevent data races."
                    )
                }
            }
        }
    }

    private fun isSingleton(node: UClass): Boolean {
        return node.annotations.any { it.qualifiedName?.contains("Singleton") == true }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ThreadSafetyViolation",
            description = "Non-thread-safe state in multi-threaded component",
            explanation = """
                Singletons and Platform components are often accessed from multiple threads. 
                Using standard collections (ArrayList, HashMap) without synchronization 
                will lead to ConcurrentModificationException or silent data corruption.
            """,
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            implementation = Implementation(ThreadSafetyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
