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
        val className = method.containingClass?.qualifiedName ?: return

        val isBlocking = when {
            className == "java.lang.Thread" && methodName == "sleep" -> true
            className.contains("BuildersKt") && methodName == "runBlocking" -> true
            className == "java.util.concurrent.Future" && methodName == "get" -> true
            className == "java.util.concurrent.CompletableFuture" && (methodName == "get" || methodName == "join") -> true
            className.startsWith("java.io.InputStream") && methodName == "read" -> true
            className.startsWith("java.io.OutputStream") && methodName == "write" -> true
            className == "java.nio.file.Files" && (methodName.startsWith("read") || methodName.startsWith("write")) -> true
            className == "java.net.URL" && methodName == "openStream" -> true
            else -> false
        }

        if (isBlocking) {
            val containingMethod = node.getParentOfType<UMethod>()
            val containingClass = node.getParentOfType<UClass>()
            
            val isUIContext = isInsideUIContext(containingClass, containingMethod)
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

    private fun isInsideUIContext(uClass: UClass?, uMethod: UMethod?): Boolean {
        if (uClass == null) return false
        
        val className = uClass.qualifiedName ?: ""
        val isLifecycleComponent = className.contains("ViewModel") || 
                                   className.contains("Activity") || 
                                   className.contains("Fragment")
        
        val isComposable = uMethod?.javaPsi?.annotations?.any { 
            it.qualifiedName?.contains("Composable") == true 
        } == true

        return isLifecycleComponent || isComposable
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "BlockingMainThreadWork",
            description = "Blocking operation detected on main thread or coroutine",
            explanation = """
                Blocking the main thread leads to Application Not Responding (ANR) errors 
                and jank. Blocking a coroutine thread causes thread starvation in your 
                dispatcher. 
                
                Always use non-blocking suspend functions or move blocking I/O / Thread.sleep 
                to 'Dispatchers.IO' using 'withContext'.
            """,
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            implementation = Implementation(MainThreadWorkDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
