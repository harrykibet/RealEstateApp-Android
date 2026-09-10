package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import org.jetbrains.uast.*

/**
 * LAW-033: Suppression Policy Enforcement.
 * 
 * authoritatively enforces that architectural laws are not blindly suppressed.
 */
class SuppressionPolicyDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitAnnotation(node: UAnnotation) {
            val name = node.qualifiedName ?: node.asRenderString()
            // 🛡️ POLICY: Only analyze actual suppression mechanisms.
            // @OptIn is an API stability tool, not a debt-hiding mechanism.
            if (name.contains("SuppressLint") || name.contains("Suppress") || 
                name.contains("SuppressWarnings")) {
                val suppressed = extractSuppressed(node)
                checkSuppressedIssues(context, node, suppressed)
            }
        }
    }

    private fun extractSuppressed(node: UAnnotation): List<String> {
        val list = mutableListOf<String>()
        
        // 1. Attribute extraction
        node.attributeValues.forEach { attr ->
            extractFromExpression(attr.expression, list)
        }
        
        // 2. Raw source fallback (Ensures we catch literals in various formats)
        val src = node.asSourceString()
        Regex("\"([^\"]+)\"").findAll(src).forEach { 
            list.add(it.groupValues[1])
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
        }
    }

    private fun checkSuppressedIssues(context: JavaContext, node: UAnnotation, suppressed: List<String>) {
        val registry = context.driver.registry
        suppressed.forEach { id ->
            val cleanId = id.substringAfterLast(".").removeSuffix("::class")
            
            // 💡 Note: Wildcard suppression ("all") is enforced in the Konsist layer 
            // (Law033_WildcardSuppressionTest) because Lint's own engine would 
            // silence reports in this detector if "all" is present in the scope.
            if (cleanId.equals("all", ignoreCase = true)) return@forEach

            val issue = registry.getIssue(cleanId)
            if (issue == null) return@forEach
            
            // Prevent suppression of this detector itself to avoid recursion/hiding
            if (cleanId == ISSUE.id) return@forEach

            when (issue.defaultSeverity) {
                Severity.FATAL -> {
                    context.report(
                        ISSUE, 
                        node, 
                        context.getLocation(node), 
                        "Architectural Law violation '$cleanId' (FATAL) cannot be suppressed (LAW-033)."
                    )
                }
                Severity.ERROR, Severity.WARNING -> {
                    if (!checkJustification(context, node, cleanId)) {
                        context.report(
                            ISSUE, 
                            node, 
                            context.getLocation(node), 
                            "Suppression of ${issue.defaultSeverity.description}-level rule '$cleanId' requires an immediately preceding justification comment " +
                            "matching: '// Justification: $cleanId - <reason>' (LAW-033)."
                        )
                    }
                }
                else -> { }
            }
        }
    }

    private fun checkJustification(context: JavaContext, node: UAnnotation, issueId: String): Boolean {
        val contents = context.getContents() ?: return false
        val startOffset = node.sourcePsi?.textRange?.startOffset ?: return false
        
        // Scan backwards through the contiguous comment block immediately preceding the annotation
        val prefix = contents.substring(0, startOffset).trimEnd()
        val lines = prefix.lines()
        
        val pattern = Regex("""//\s*Justification:\s*$issueId\s*-.*""", RegexOption.IGNORE_CASE)
        
        for (i in lines.indices.reversed()) {
            val line = lines[i].trim()
            if (line.isEmpty()) continue
            if (!line.startsWith("//")) break // End of contiguous comment block
            
            if (pattern.matches(line)) return true
        }
        
        return false
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
