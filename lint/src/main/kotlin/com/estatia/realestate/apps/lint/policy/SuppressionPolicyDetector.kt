package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

/**
 * Enforces LAW-033: "Suppression Policy Enforcement".
 * 
 * Rules:
 * 1. Blind suppression using "all" is forbidden.
 * 2. FATAL rules cannot be suppressed.
 * 3. ERROR rules require a preceding "Justification:" comment.
 */
class SuppressionPolicyDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitAnnotation(node: UAnnotation) {
            val name = node.qualifiedName ?: node.asRenderString()
            if (name.contains("SuppressLint") || name.contains("Suppress") || name.contains("OptIn")) {
                val suppressed = extractSuppressed(node)
                checkSuppressedIssues(context, node, suppressed)
            }
        }
    }

    private fun extractSuppressed(node: UAnnotation): List<String> {
        val list = mutableListOf<String>()
        
        // Check for OptIn values (class literals)
        val name = node.qualifiedName ?: node.asRenderString()
        if (name.contains("OptIn")) {
            val value = node.findAttributeValue("markerClass") ?: node.findAttributeValue("value")
            extractFromExpression(value ?: return emptyList(), list)
            return list
        }

        // Try value attribute
        node.findAttributeValue("value")?.let { attr ->
            extractFromExpression(attr, list)
        }
        
        // Fallback for names attribute (SuppressLint sometimes uses it)
        node.findAttributeValue("names")?.let { attr ->
            extractFromExpression(attr, list)
        }
        
        // Final fallback: Regex on the source
        if (list.isEmpty()) {
            val src = node.asSourceString()
            "\"([^\"]+)\"".toRegex().findAll(src).forEach { list.add(it.groupValues[1]) }
        }
        
        return list
    }

    private fun extractFromExpression(expr: UExpression, list: MutableList<String>) {
        when (expr) {
            is ULiteralExpression -> {
                expr.value?.toString()?.let { list.add(it) }
            }
            is UCallExpression -> {
                expr.valueArguments.forEach { extractFromExpression(it, list) }
            }
            is UPolyadicExpression -> {
                expr.operands.forEach { extractFromExpression(it, list) }
            }
            // Handle array literal in Kotlin
            is UExpressionList -> {
                expr.expressions.forEach { extractFromExpression(it, list) }
            }
            is UClassLiteralExpression -> {
                expr.type?.canonicalText?.let { list.add(it) }
            }
        }
    }

    private fun checkSuppressedIssues(context: JavaContext, node: UAnnotation, suppressed: List<String>) {
        val registry = context.driver.registry
        suppressed.forEach { id ->
            if (id.lowercase() == "all") {
                context.report(ISSUE, node, context.getLocation(node), "Blind suppression using 'all' is forbidden in Estatia (LAW-033).")
                return@forEach
            }

            val issue = registry.getIssue(id) ?: if (id.contains("UnstableApi")) registry.getIssue("UnsafeOptInUsageError") else null
            if (issue == null) return@forEach
            
            when (issue.defaultSeverity) {
                Severity.FATAL -> {
                    if (id != ISSUE.id) {
                        context.report(
                            ISSUE, 
                            node, 
                            context.getLocation(node), 
                            "Architectural Law violation '$id' (FATAL) cannot be suppressed (LAW-033)."
                        )
                    }
                }
                Severity.ERROR -> {
                    if (!checkJustification(context, node)) {
                        context.report(
                            ISSUE, 
                            node, 
                            context.getLocation(node), 
                            "Suppression of ERROR-level rule '$id' requires a preceding justification comment (LAW-033)."
                        )
                    }
                }
                else -> { }
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
