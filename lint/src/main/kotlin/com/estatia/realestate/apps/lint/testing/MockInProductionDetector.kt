package com.estatia.realestate.apps.lint.testing

import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Prevents usage of testing libraries (MockK, Mockito) in production code.
 */
class MockInProductionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("mockk", "mock", "spy", "every", "verify")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val path = context.file.path.replace("\\", "/")
        if (path.contains("/src/test/") || path.contains("/src/androidTest/")) return

        val className = method.containingClass?.qualifiedName ?: ""
        if (className.startsWith("io.mockk") || className.startsWith("org.mockito")) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Testing library usage detected in production code. Remove all mocks before merging."
            )
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MockInProduction",
            description = "Testing mocks found in production code",
            rationale = "Mocks increase APK size and should strictly remain in test source sets.",
            badExample = "val user = mockk<User>() // In main source set",
            goodExample = "val user = User(id = \"1\")",
            category = IssueCategory.TESTING,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-016 (Source Separation)",
            implementation = Implementation(MockInProductionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
