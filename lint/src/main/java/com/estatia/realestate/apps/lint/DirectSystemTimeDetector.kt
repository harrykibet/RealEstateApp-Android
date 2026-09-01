package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Detector that prevents direct access to System.currentTimeMillis() or System.nanoTime(),
 * enforcing the use of an injectable Clock/Instant provider for testability.
 */
class DirectSystemTimeDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("currentTimeMillis", "nanoTime")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val evaluator = context.evaluator
        if (evaluator.isMemberInClass(method, "java.lang.System")) {
            
            // Allow in tests and specifically allowed infra (like metrics implementations)
            val fileName = context.file.name
            if (fileName.contains("Test") || fileName.contains("Metrics") || fileName.contains("Analytics")) return

            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Direct usage of 'System.${method.name}()' is forbidden. " +
                        "Inject a Clock or Instant provider instead to ensure time-dependent logic is testable."
            )
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "DirectSystemTimeAccess",
            briefDescription = "Direct System Time Access",
            explanation = """
                Accessing system time directly prevents deterministic testing of time-based logic 
                (like timeouts, cache expiration, or debouncing). 
                
                Always inject a 'Clock' or a custom time provider interface.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                DirectSystemTimeDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
