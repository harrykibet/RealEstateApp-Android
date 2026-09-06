package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

/**
 * LAW-028: Methods must be concise and focused (Complexity Budget).
 */
class Law028_SpaghettiMethodDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            if (node.isConstructor) return
            
            val source = node.uastBody?.asSourceString() ?: return
            val lineCount = source.lines().size
            
            val fatalThreshold = context.getOption(ISSUE, "fatalThreshold", 300)
            val errorThreshold = context.getOption(ISSUE, "errorThreshold", 120)
            val warningThreshold = context.getOption(ISSUE, "warningThreshold", 60)

            when {
                lineCount > fatalThreshold -> {
                    context.report(ISSUE, node, context.getLocation(node as UElement),
                        "Method '${node.name}' is too large ($lineCount lines). FATAL limit is $fatalThreshold (LAW-028).")
                }
                lineCount > errorThreshold -> {
                    context.report(ISSUE, node, context.getLocation(node as UElement),
                        "Method '${node.name}' exceeds the complexity budget ($lineCount lines). ERROR limit is $errorThreshold (LAW-028).")
                }
                lineCount > warningThreshold -> {
                    context.report(ISSUE, node, context.getLocation(node as UElement),
                        "Method '${node.name}' is becoming complex ($lineCount lines). WARNING limit is $warningThreshold (LAW-028).")
                }
            }
        }
    }

    private fun JavaContext.getOption(issue: Issue, name: String, default: Int): Int {
        return configuration.getOption(issue, name)?.toIntOrNull() ?: default
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "SpaghettiMethodFatal",
            description = "Method exceeds complexity budget",
            rationale = "Long methods are hard to test and maintain. Refactor into smaller, focused functions.",
            badExample = "fun longMethod() { ... 300 lines ... }",
            goodExample = "fun focusedMethod() { ... 20 lines ... }",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL, // Default is FATAL, but we use thresholds to decide
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_028,
            implementation = Implementation(Law028_SpaghettiMethodDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
