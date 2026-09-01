package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

/**
 * Detector that flags usage of non-thread-safe collections in Singleton classes 
 * without visible synchronization.
 */
class NonThreadSafeCollectionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (!hasSingletonAnnotation(node)) return

            node.fields.forEach { field ->
                val type = field.type.canonicalText
                if (isUnsafeCollection(type)) {
                    // Look for synchronization primitives in the same class
                    val hasSync = node.fields.any { 
                        val fieldType = it.type.canonicalText
                        fieldType.contains("Mutex") || fieldType.contains("Atomic") || fieldType.contains("Concurrent")
                    }
                    
                    if (!hasSync) {
                        context.report(
                            ISSUE,
                            field,
                            context.getLocation(field),
                            "Field '${field.name}' uses a non-thread-safe collection in a Singleton. " +
                                    "Use 'ConcurrentHashMap', a 'Mutex', or 'synchronized' to prevent data races."
                        )
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

    private fun isUnsafeCollection(type: String): Boolean {
        return (type.contains("ArrayList") || 
               type.contains("HashMap") || 
               type.contains("HashSet") ||
               type.contains("MutableList") ||
               type.contains("MutableMap")) && !type.contains("Concurrent")
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "NonThreadSafeCollection",
            briefDescription = "Unsafe Collection in Singleton",
            explanation = """
                Singletons are accessed from multiple threads. Using non-thread-safe collections 
                like 'ArrayList' or 'HashMap' without synchronization leads to non-deterministic 
                crashes and state corruption.
                
                Always use concurrent collections or protect access with a Mutex.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.ERROR,
            implementation = Implementation(
                NonThreadSafeCollectionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
