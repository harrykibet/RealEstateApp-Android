package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import org.jetbrains.uast.UReferenceExpression

/**
 * Detector that prevents usage of hardcoded Coroutine dispatchers (Dispatchers.IO, etc.) 
 * in production code, enforcing dispatcher injection.
 */
class HardcodedDispatcherDetector : Detector(), SourceCodeScanner {

    override fun getApplicableReferenceNames() = listOf("IO", "Main", "Default", "Unconfined")

    override fun visitReference(context: JavaContext, reference: UReferenceExpression, referenced: PsiElement) {
        val evaluator = context.evaluator
        if (referenced is PsiField && evaluator.isMemberInClass(referenced, "kotlinx.coroutines.Dispatchers")) {
            
            // Allow in tests
            val fileName = context.file.name
            if (fileName.contains("Test") || context.file.path.contains("build-logic")) return

            context.report(
                ISSUE,
                reference,
                context.getLocation(reference),
                "Direct usage of 'Dispatchers.${referenced.name}' is forbidden. " +
                        "Inject a CoroutineDispatcher instead to ensure the class is testable and deterministic."
            )
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "HardcodedDispatcher",
            briefDescription = "Hardcoded Coroutine Dispatcher",
            explanation = """
                Hardcoding dispatchers (like 'Dispatchers.IO') makes classes difficult to test 
                deterministically because the test scheduler cannot intercept the execution. 
                
                Always inject dispatchers via the constructor using Hilt named qualifiers 
                (e.g., @IODispatcher).
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(
                HardcodedDispatcherDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
