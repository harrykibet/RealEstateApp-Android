package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiType
import org.jetbrains.uast.UCatchClause
import org.jetbrains.uast.UTryExpression

/**
 * Detector that checks if broad catch blocks (Exception, Throwable, RuntimeException) 
 * in coroutine-aware contexts correctly handle CancellationException.
 */
class CoroutineCancellationDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UTryExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitTryExpression(node: UTryExpression) {
            val catchClauses = node.catchClauses
            var handlesCancellation = false

            for (catchClause in catchClauses) {
                // In many Lint versions, UCatchClause provides types or type
                val types = catchClause.types
                
                if (types.any { isCancellationException(it) }) {
                    handlesCancellation = true
                } else if (types.any { isBroadException(it) }) {
                    if (!handlesCancellation) {
                        context.report(
                            ISSUE,
                            catchClause,
                            context.getLocation(catchClause),
                            "Broad catch block might swallow Coroutine Cancellation. " +
                                    "Add 'catch (e: CancellationException) { throw e }' before this block."
                        )
                    }
                }
            }
        }
    }

    private fun isBroadException(type: PsiType): Boolean {
        val canonicalText = type.canonicalText
        return canonicalText == "java.lang.Exception" || 
               canonicalText == "java.lang.Throwable" || 
               canonicalText == "java.lang.RuntimeException"
    }

    private fun isCancellationException(type: PsiType): Boolean {
        val canonicalText = type.canonicalText
        return canonicalText == "kotlinx.coroutines.CancellationException" || 
               canonicalText == "java.util.concurrent.CancellationException"
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "SwallowedCancellationException",
            briefDescription = "Swallowed Coroutine Cancellation",
            explanation = """
                Broad catch blocks like 'catch (e: Exception)' or 'catch (e: Throwable)' catch 
                'CancellationException', which is used by Kotlin Coroutines to manage job cancellation. 
                Swallowing this exception prevents coroutines from cancelling correctly, leading to 
                resource leaks and unpredictable behavior.
                
                Always handle 'CancellationException' separately and rethrow it, or ensure your catch 
                block explicitly ignores or rethrows it.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(
                CoroutineCancellationDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
