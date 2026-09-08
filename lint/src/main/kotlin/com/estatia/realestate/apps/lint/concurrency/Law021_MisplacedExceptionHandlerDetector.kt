package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * LAW-021: Exception Handling.
 * CoroutineExceptionHandler used in withContext (where it's ignored).
 */
class Law021_MisplacedExceptionHandlerDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("withContext", "launch", "async")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (!isMemberInPackage(method, "kotlinx.coroutines")) return
        
        val methodName = node.methodName ?: return
        val containingMethod = node.getParentOfType<UMethod>() ?: return
        val isInsideSuspend = context.evaluator.isSuspend(containingMethod)

        val shouldCheck = when (methodName) {
            "withContext" -> true
            "launch", "async" -> isInsideSuspend && isChildCoroutine(context, node, method)
            else -> false
        }

        if (shouldCheck) {
            node.valueArguments.forEach { argument ->
                if (containsExceptionHandler(context, argument)) {
                    val message = if (methodName == "withContext") {
                        "CoroutineExceptionHandler used in 'withContext' will be ignored (LAW-021)."
                    } else {
                        "CoroutineExceptionHandler used in a child coroutine ('$methodName') will be ignored. CEH only works on root scopes (LAW-021)."
                    }
                    context.report(ISSUE, node, context.getLocation(node), message)
                }
            }
        }
    }

    private fun isChildCoroutine(context: JavaContext, node: UCallExpression, method: PsiMethod): Boolean {
        val receiverType = node.receiverType
        return if (receiverType != null) {
            context.evaluator.inheritsFrom(context.evaluator.getTypeClass(receiverType), "kotlinx.coroutines.CoroutineScope", false)
        } else {
            context.evaluator.isMemberInClass(method, "kotlinx.coroutines.CoroutineScope") ||
            isExtensionOnScope(context, method)
        }
    }

    private fun isExtensionOnScope(context: JavaContext, method: PsiMethod): Boolean {
        val evaluator = context.evaluator
        val parameters = method.parameterList.parameters
        if (parameters.isEmpty()) return false
        val firstParamType = parameters[0].type
        return evaluator.inheritsFrom(evaluator.getTypeClass(firstParamType), "kotlinx.coroutines.CoroutineScope", false)
    }

    private fun containsExceptionHandler(context: JavaContext, expression: UExpression): Boolean {
        var found = false
        expression.accept(object : AbstractUastVisitor() {
            override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression): Boolean {
                val type = node.getExpressionType()
                if (type != null && context.evaluator.inheritsFrom(context.evaluator.getTypeClass(type), "kotlinx.coroutines.CoroutineExceptionHandler", false)) {
                    found = true
                }
                return super.visitSimpleNameReferenceExpression(node)
            }
            
            override fun visitCallExpression(node: UCallExpression): Boolean {
                val type = node.getExpressionType()
                if (type != null && context.evaluator.inheritsFrom(context.evaluator.getTypeClass(type), "kotlinx.coroutines.CoroutineExceptionHandler", false)) {
                    found = true
                }
                return super.visitCallExpression(node)
            }
        })
        return found
    }

    private fun isMemberInPackage(method: PsiMethod, packageName: String): Boolean {
        val qualifiedName = method.containingClass?.qualifiedName ?: return false
        return qualifiedName.startsWith("$packageName.") || qualifiedName == packageName
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MisplacedCoroutineExceptionHandler",
            description = "CoroutineExceptionHandler used in withContext",
            rationale = "CEH only works on root coroutines and is ignored in withContext.",
            badExample = "withContext(Dispatchers.IO + ceh) { ... }",
            goodExample = "CoroutineScope(Dispatchers.Main + ceh).launch { ... }",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PLATFORM,
            architectureLaw = Law.LAW_021,
            implementation = Implementation(Law021_MisplacedExceptionHandlerDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
