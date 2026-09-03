package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

/**
 * Enforces LAW-033: "Suppression Policy Enforcement".
 */
class SuppressionPolicyDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitAnnotation(node: UAnnotation) {
            val name = node.qualifiedName ?: ""
            val text = node.sourcePsi?.text ?: node.asRenderString()
            
            if (name.contains("SuppressLint") || name.contains("Suppress") || 
                text.contains("SuppressLint") || text.contains("Suppress")) {
                
                val suppressed = extractSuppressed(node)
                checkSuppressedIssues(context, node, suppressed)
            }
        }
    }

    private fun extractSuppressed(node: UAnnotation): List<String> {
        val list = mutableListOf<String>()
        val value = node.findAttributeValue("value")
        
        fun addValue(expr: UExpression?) {
            if (expr is ULiteralExpression) {
                expr.value?.toString()?.let { list.add(it) }
            } else if (expr is UCallExpression) {
                expr.valueArguments.forEach { addValue(it) }
            }
        }
        
        addValue(value)
        
        // Fallback to text parsing if UAST resolution is weak (common in tests)
        if (list.isEmpty()) {
            val text = node.asRenderString()
            val regex = "\"([^\"]+)\"".toRegex()
            regex.findAll(text).forEach { list.add(it.groupValues[1]) }
        }
        
        return list
    }

    private fun checkSuppressedIssues(context: JavaContext, node: UAnnotation, suppressed: List<String>) {
        val severityMap = mapOf(
            "InfrastructureLeakage" to Severity.FATAL,
            "ExposedMutableState" to Severity.ERROR
        )

        suppressed.forEach { id ->
            if (id == "all") {
                context.report(ISSUE, node, context.getLocation(node), "Blind suppression using 'all' is forbidden in Estatia. Explicitly list the rules you are suppressing (LAW-033).")
            } else {
                val severity = severityMap[id]
                if (severity == Severity.FATAL) {
                    context.report(ISSUE, node, context.getLocation(node), "Architectural Law violation '$id' (FATAL) cannot be suppressed. You must fix the underlying design issue (LAW-033).")
                } else if (severity == Severity.ERROR) {
                    if (!checkJustification(context, node)) {
                        context.report(ISSUE, node, context.getLocation(node), "Suppression of ERROR-level rule '$id' requires a preceding justification comment (e.g., '// Justification: ...') (LAW-033).")
                    }
                }
            }
        }
    }

    private fun checkJustification(context: JavaContext, node: UAnnotation): Boolean {
        val source = context.getContents() ?: return false
        val offset = node.sourcePsi?.textRange?.startOffset ?: return false
        val preceding = source.substring(0, offset)
        return preceding.contains("Justification:", ignoreCase = true)
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "SuppressionPolicyViolation",
            description = "Illegal or undocumented rule suppression",
            rationale = "FATAL rules cannot be suppressed, and ERROR rules require a justification comment.",
            badExample = "@SuppressLint(\"ExposedMutableState\")",
            goodExample = "// Justification: reason\n@SuppressLint(\"ExposedMutableState\")",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-033",
            implementation = Implementation(SuppressionPolicyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
