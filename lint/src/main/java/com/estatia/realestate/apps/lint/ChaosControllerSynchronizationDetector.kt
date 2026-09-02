package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

/**
 * Detector that ensures mutable state in Chaos Controllers is synchronized.
 * Chaos controllers must be deterministic even under concurrency.
 */
class ChaosControllerSynchronizationDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            val className = node.name ?: return
            val qualifiedName = node.qualifiedName ?: ""
            
            val isChaosController = className.contains("Chaos") || 
                                     qualifiedName.contains(".chaos.")

            if (!isChaosController) return

            node.fields.forEach { field ->
                // Check if it's a 'var'
                if (!field.isFinal) {
                    reportIssue(context, field, field.name)
                }
            }
        }
    }

    private fun reportIssue(context: JavaContext, node: UElement, name: String) {
        context.report(
            ISSUE,
            node,
            context.getLocation(node),
            "Chaos controller state '$name' is a plain 'var'. Use AtomicReference, AtomicInteger, or MutableStateFlow to ensure determinism under concurrency."
        )
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "UnsynchronizedChaosState",
            briefDescription = "Unsynchronized Chaos Controller State",
            explanation = """
                Chaos controllers must be thread-safe to ensure "Deterministic Chaos" even when 
                exercised by concurrent tests (e.g., using runConcurrent). 
                
                Plain 'var' properties are not thread-safe and can lead to lost increments or 
                stale reads, causing flaky CI builds.
                
                Fix: Replace 'var' with:
                - AtomicReference<T>
                - AtomicInteger
                - AtomicBoolean
                - MutableStateFlow<T>
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(
                ChaosControllerSynchronizationDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
