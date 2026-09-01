package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULiteralExpression

/**
 * Detector that flags hardcoded colors (#RRGGBB) and DP dimensions in UI code,
 * enforcing Design System consistency.
 */
class HardcodedColorDimensionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(ULiteralExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : com.android.tools.lint.client.api.UElementHandler() {
        override fun visitLiteralExpression(node: ULiteralExpression) {
            val value = node.value ?: return
            
            // 1. Check for Hex Colors
            if (value is String && value.startsWith("#") && (value.length == 7 || value.length == 9)) {
                report(context, node, "Hardcoded color '$value'")
            }
            
            // 2. Check for DP values
            val source = node.sourcePsi?.text ?: ""
            if (source.endsWith(".dp") || source.endsWith(".sp")) {
                report(context, node, "Hardcoded dimension '$source'")
            }
        }
    }

    private fun report(context: JavaContext, node: UElement, message: String) {
        // Skip for values in the DesignSystem module itself or tests
        val path = context.file.path.replace("\\", "/")
        if (path.contains("/design-system/") || path.contains("Test")) return

        context.report(
            ISSUE,
            node,
            context.getLocation(node),
            "$message is forbidden. Use MaterialTheme.colorScheme or DesignSystem tokens instead."
        )
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "HardcodedColorDimension",
            briefDescription = "Hardcoded Color or Dimension",
            explanation = """
                Hardcoding UI values like colors or DP sizes makes the app difficult to theme 
                and maintain. It violates the "Single Source of Truth" for design system tokens.
                
                Always use standard tokens from 'MaterialTheme' or your local 'DesignSystem'.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(
                HardcodedColorDimensionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
