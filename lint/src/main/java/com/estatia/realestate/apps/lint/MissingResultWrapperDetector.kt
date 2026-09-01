package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UMethod

/**
 * Detector that ensures all suspend functions in Repository interfaces 
 * return AppResult, enforcing consistent failure semantics.
 */
class MissingResultWrapperDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (!node.isInterface) return
            val name = node.name ?: ""
            if (!name.endsWith("Repository") && !name.endsWith("DataSource")) return

            node.methods.forEach { method ->
                if (context.evaluator.isSuspend(method)) {
                    val returnType = method.returnType?.canonicalText ?: ""
                    if (!returnType.contains("AppResult")) {
                        context.report(
                            ISSUE,
                            method,
                            context.getLocation(method),
                            "Suspend function '${method.name}' must return 'AppResult'. " +
                                    "Exposing naked throwables from repositories violates Estatia's failure semantics."
                        )
                    }
                }
            }
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "MissingResultWrapper",
            briefDescription = "Naked Suspend Return Type",
            explanation = """
                Suspend functions in data layers should always wrap their results in 'AppResult'. 
                This forces callers to handle failures explicitly and prevents unexpected 
                crashes from unmapped infrastructure exceptions.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(
                MissingResultWrapperDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
