package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * Ensures that long-running coroutine loops check for cancellation using [yield()] or [isActive].
 */
class CoroutineCancellationDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(
        UWhileExpression::class.java,
        UDoWhileExpression::class.java,
        UForExpression::class.java,
        UForEachExpression::class.java
    )

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitWhileExpression(node: UWhileExpression) = checkLoop(node)
        override fun visitDoWhileExpression(node: UDoWhileExpression) = checkLoop(node)
        override fun visitForExpression(node: UForExpression) = checkLoop(node)
        override fun visitForEachExpression(node: UForEachExpression) = checkLoop(node)

        private fun checkLoop(node: ULoopExpression) {
            val method = node.getParentOfType<UMethod>() ?: return
            if (!context.evaluator.isSuspend(method)) return

            var hasCancellationCheck = false
            node.body.accept(object : AbstractUastVisitor() {
                override fun visitCallExpression(node: UCallExpression): Boolean {
                    val name = node.methodName
                    if (name == "yield" || name == "ensureActive") {
                        hasCancellationCheck = true
                    }
                    return super.visitCallExpression(node)
                }

                override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression): Boolean {
                    if (node.identifier == "isActive") {
                        hasCancellationCheck = true
                    }
                    return super.visitSimpleNameReferenceExpression(node)
                }
            })

            if (!hasCancellationCheck) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Suspended loop in '${method.name}' is missing a cancellation check. " +
                            "Use 'yield()' or 'ensureActive()' to prevent 'zombie' coroutines."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MissingCoroutineCancellation",
            description = "Coroutine loop missing cancellation check",
            rationale = "Long-running loops must be cooperative with cancellation to prevent leaks.",
            badExample = "suspend fun loop() { while(true) { ... } }",
            goodExample = "suspend fun loop() { while(isActive) { ... } }",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-013 (Cooperative Cancellation)",
            implementation = Implementation(CoroutineCancellationDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
