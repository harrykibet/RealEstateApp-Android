package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

class DesignSystemDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String> = listOf("Text", "Button")

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val className = method.containingClass?.qualifiedName
        
        if (className?.startsWith("androidx.compose.material3") == true) {
            val name = method.name
            val replacement = "Estatia$name"
            
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Using standard Material 3 $name. Use $replacement instead to ensure design system consistency.",
                fix().replace().all().with("com.estatia.realestate.apps.core.designsystem.component.$replacement").build()
            )
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "DesignSystemUsage",
            briefDescription = "Standard Material component usage",
            explanation = "Developers should use Estatia-prefixed design system components instead of raw Material 3 components to maintain visual consistency across the app.",
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.WARNING,
            implementation = Implementation(
                DesignSystemDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
