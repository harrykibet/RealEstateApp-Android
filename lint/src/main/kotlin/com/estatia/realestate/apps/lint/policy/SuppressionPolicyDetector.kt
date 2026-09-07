package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import org.jetbrains.uast.*

/**
 * LAW-033: Suppression Policy Enforcement.
 * 
 * This detector ensures that architectural laws are not blindly suppressed.
 * Rules:
 * 1. Blind suppression using "all" is strictly forbidden.
 * 2. FATAL rules (Architectural Laws) cannot be suppressed.
 * 3. ERROR rules require an ADJACENT justification comment with the specific issue ID.
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
        
        // 1. Try to extract from the literal values (e.g. @Suppress("all"))
        node.attributeValues.forEach { attr ->
            extractFromExpression(attr.expression, list)
        }
        
        // 2. Fallback to regex if UAST didn't catch it
        if (list.isEmpty()) {
            val src = node.asSourceString()
            "\"([^\"]+)\"".toRegex().findAll(src).forEach { 
                list.add(it.groupValues[1])
            }
        }
        
        return list.distinct()
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
            is UExpressionList -> {
                expr.expressions.forEach { extractFromExpression(it, list) }
            }
            is UClassLiteralExpression -> {
                expr.type?.canonicalText?.let { list.add(it) }
            }
            is USimpleNameReferenceExpression -> {
                list.add(expr.identifier)
            }
            is UQualifiedReferenceExpression -> {
                list.add(expr.asRenderString().removeSuffix("::class"))
            }
        }
    }

    private fun checkSuppressedIssues(context: JavaContext, node: UAnnotation, suppressed: List<String>) {
        val registry = context.driver.registry
        suppressed.forEach { id ->
            val cleanId = id.substringAfterLast(".").removeSuffix("::class")
            
            if (cleanId.lowercase() == "all") {
                context.report(ISSUE, node, context.getLocation(node), "Blind suppression using 'all' is forbidden in Estatia (LAW-033).")
                return@forEach
            }

            // We handle UnstableApi as a special case for OptIn
            val issue = registry.getIssue(cleanId) ?: 
                        if (cleanId.contains("UnstableApi")) registry.getIssue("UnsafeOptInUsageError") else null
            
            if (issue == null) return@forEach
            
            when (issue.defaultSeverity) {
                Severity.FATAL -> {
                    // SuppressionPolicyViolation itself is FATAL, don't recurse
                    if (cleanId != ISSUE.id) {
                        context.report(
                            ISSUE, 
                            node, 
                            context.getLocation(node), 
                            "Architectural Law violation '$cleanId' (FATAL) cannot be suppressed (LAW-033)."
                        )
                    }
                }
                Severity.ERROR -> {
                    if (!checkJustification(context, node, cleanId)) {
                        context.report(
                            ISSUE, 
                            node, 
                            context.getLocation(node), 
                            "Suppression of ERROR-level rule '$cleanId' requires an immediately preceding justification comment " +
                            "matching: '// Justification: $cleanId - <reason>' (LAW-033)."
                        )
                    }
                }
                else -> { }
            }
        }
    }

    private fun checkJustification(context: JavaContext, node: UAnnotation, issueId: String): Boolean {
        val source = context.getContents() ?: return false
        val startOffset = node.sourcePsi?.textRange?.startOffset ?: return false
        
        // Scan backwards from the annotation to find the preceding line
        val precedingText = source.substring(0, startOffset).trimEnd()
        val lastNewline = precedingText.lastIndexOf('\n')
        val lastLine = if (lastNewline != -1) precedingText.substring(lastNewline + 1) else precedingText
        
        // Pattern: // Justification: IssueId - <reason>
        val pattern = Regex("""//\s*Justification:\s*$issueId\s*-.*""", RegexOption.IGNORE_CASE)
        return pattern.matches(lastLine)
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "SuppressionPolicyViolation",
            description = "Illegal or undocumented rule suppression",
            rationale = "FATAL rules cannot be suppressed, and ERROR rules require a specific, adjacent justification comment.",
            badExample = "@Suppress(\"ExposedMutableState\")",
            goodExample = "// Justification: ExposedMutableState - Required for legacy data mapping\n@Suppress(\"ExposedMutableState\")",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_033,
            implementation = Implementation(SuppressionPolicyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
