package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.*

/**
 * Detector that flags hardcoded string literals in UI calls (e.g., Text("Hello")),
 * enforcing internationalization (I18n) standards.
 */
class HardcodedStringDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UCallExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitCallExpression(node: UCallExpression) {
            val method = node.resolve() ?: return
            
            // Focus on common UI components that accept text
            if (isUiTextComponent(method)) {
                node.valueArguments.forEach { arg ->
                    if (arg is ULiteralExpression && arg.isString) {
                        context.report(
                            ISSUE,
                            arg,
                            context.getLocation(arg),
                            "Hardcoded string literal in UI component. " +
                                    "Use 'stringResource(R.string.id)' to support localization and prevent maintenance toil."
                        )
                    }
                }
            }
        }
    }

    private fun isUiTextComponent(method: PsiMethod): Boolean {
        val name = method.name
        val className = method.containingClass?.qualifiedName ?: ""
        return (name == "Text" || name == "Button" || name == "TextField" || name == "Title") && 
               (className.contains("compose.material") || className.contains("designsystem"))
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "HardcodedString",
            briefDescription = "Hardcoded UI String",
            explanation = """
                Hardcoding strings in UI makes the app impossible to localize and difficult to 
                maintain. FAANG-level apps require all UI text to be sourced from resource 
                files (strings.xml) or dynamic localization services.
                
                Always use 'stringResource()' in Compose.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.ERROR,
            implementation = Implementation(
                HardcodedStringDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
