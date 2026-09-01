package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

/**
 * Detector that flags usage of unbounded queues or channels (e.g., Channel.UNLIMITED),
 * enforcing Bounded Resource Behavior.
 */
class UnboundedInternalBufferDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("Channel")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        // Check for Channel(Channel.UNLIMITED) or Channel(Int.MAX_VALUE)
        val args = node.valueArguments
        if (args.isNotEmpty()) {
            val firstArg = args[0].asRenderString()
            if (firstArg.contains("UNLIMITED") || firstArg.contains("MAX_VALUE")) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Usage of unbounded buffers (UNLIMITED) is forbidden. " +
                            "Use a bounded capacity to prevent memory exhaustion under backpressure."
                )
            }
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "UnboundedInternalBuffer",
            briefDescription = "Unbounded Internal Buffer",
            explanation = """
                Using 'Channel.UNLIMITED' or very large buffer sizes can lead to OutOfMemoryErrors 
                if the producer is faster than the consumer. 
                
                Always define a specific capacity for internal queues and channels to ensure 
                bounded resource behavior.
            """.trimIndent(),
            category = Category.PERFORMANCE,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                UnboundedInternalBufferDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
