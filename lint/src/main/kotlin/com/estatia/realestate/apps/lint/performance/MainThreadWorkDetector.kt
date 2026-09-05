package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*

/**
 * Enforces LAW-011: "Blocking work never executes on the main thread."
 * Detects Thread.sleep, runBlocking, and blocking I/O in UI-sensitive contexts.
 */
class MainThreadWorkDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf(
        "sleep", "runBlocking", "get", "join", "read", "write", "readAllBytes", "openStream"
    )

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val methodName = method.name
        val evaluator = context.evaluator

        val isBlocking = when {
            evaluator.isMemberInClass(method, "java.lang.Thread") && methodName == "sleep" -> true
            methodName == "runBlocking" && isMemberInPackage(method, "kotlinx.coroutines") -> true
            evaluator.inheritsFrom(method.containingClass, "java.util.concurrent.Future", false) && methodName == "get" -> true
            evaluator.inheritsFrom(method.containingClass, "java.util.concurrent.CompletableFuture", false) && (methodName == "get" || methodName == "join") -> true
            evaluator.inheritsFrom(method.containingClass, "java.io.InputStream", false) && methodName == "read" -> true
            evaluator.inheritsFrom(method.containingClass, "java.io.OutputStream", false) && methodName == "write" -> true
            evaluator.isMemberInClass(method, "java.nio.file.Files") && (methodName.startsWith("read") || methodName.startsWith("write")) -> true
            evaluator.isMemberInClass(method, "java.net.URL") && methodName == "openStream" -> true
            else -> false
        }

        if (isBlocking) {
            val containingMethod = node.getParentOfType<UMethod>()
            val containingClass = node.getParentOfType<UClass>()
            
            val isUIContext = isInsideUIContext(context, containingClass, containingMethod)
            val isSuspend = containingMethod?.let { context.evaluator.isSuspend(it) } ?: false

            if (isUIContext || isSuspend) {
                val message = if (isSuspend) {
                    "Blocking call '$methodName' detected inside suspend function. This blocks the coroutine dispatcher and can lead to thread starvation."
                } else {
                    "Blocking call '$methodName' detected in a UI-sensitive context (ViewModel/Composable/Activity). Move this work to a background dispatcher (e.g., Dispatchers.IO)."
                }

                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    message
                )
            }
        }
    }

    private fun isMemberInPackage(method: PsiMethod, packageName: String): Boolean {
        val qualifiedName = method.containingClass?.qualifiedName ?: return false
        return qualifiedName.startsWith("$packageName.") || qualifiedName == packageName
    }

    private fun isInsideUIContext(context: JavaContext, uClass: UClass?, uMethod: UMethod?): Boolean {
        if (uClass == null) return false
        
        val evaluator = context.evaluator
        val isLifecycleComponent = evaluator.inheritsFrom(uClass, "androidx.lifecycle.ViewModel", false) || 
                                   evaluator.inheritsFrom(uClass, "android.app.Activity", false) || 
                                   evaluator.inheritsFrom(uClass, "androidx.fragment.app.Fragment", false)
        
        val isComposable = uMethod?.let { 
            evaluator.getAnnotations(it.javaPsi, false).any { ann -> ann.qualifiedName == "androidx.compose.runtime.Composable" }
        } ?: false

        return isLifecycleComponent || isComposable
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "BlockingMainThreadWork",
            description = "Blocking operation detected on main thread or coroutine",
            rationale = """
                Blocking the main thread leads to ANRs. Blocking a coroutine thread 
                causes starvation. Move blocking work to 'Dispatchers.IO'.
            """,
            badExample = "suspend fun fetch() { Thread.sleep(1000) }",
            goodExample = "suspend fun fetch() = withContext(dispatchers.io) { Thread.sleep(1000) }",
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-011 (Main Thread Isolation)",
            implementation = Implementation(MainThreadWorkDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
