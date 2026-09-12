package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * LAW-028: Methods must be concise and focused (Complexity Budget).
 * 
 * Enforces:
 * 1. Length Budget (Lines)
 * 2. Complexity Budget (Cyclomatic)
 * 3. Nesting Depth
 * 4. Fan-out (Call Sites)
 */
class Law028_SpaghettiMethodDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            if (node.isConstructor) return
            
            val body = node.uastBody ?: return
            
            // 1. Length Check
            val lineCount = body.asSourceString().lines().size
            checkThreshold(context, node, "Length", lineCount, "maxLines", 60, 120, 300)

            // 2. Complexity Check (Cyclomatic Complexity)
            val metrics = calculateMetrics(body)
            checkThreshold(context, node, "Complexity", metrics.complexity, "maxComplexity", 10, 20, 30)

            // 3. Nesting Depth
            checkThreshold(context, node, "Nesting Depth", metrics.maxNesting, "maxNesting", 3, 5, 8)

            // 4. Fan-out (Call Sites / External Dependencies)
            checkThreshold(context, node, "Fan-out", metrics.callSites, "maxFanOut", 10, 20, 40)
        }
    }

    private fun checkThreshold(
        context: JavaContext, 
        node: UMethod, 
        label: String, 
        value: Int, 
        optionPrefix: String,
        warn: Int, 
        err: Int, 
        fatal: Int
    ) {
        val fLimit = context.getOption(ISSUE, "${optionPrefix}Fatal", fatal)
        val eLimit = context.getOption(ISSUE, "${optionPrefix}Error", err)
        val wLimit = context.getOption(ISSUE, "${optionPrefix}Warning", warn)

        when {
            value > fLimit -> report(context, node, label, value, "FATAL", fLimit)
            value > eLimit -> report(context, node, label, value, "ERROR", eLimit)
            value > wLimit -> report(context, node, label, value, "WARNING", wLimit)
        }
    }

    private data class MethodMetrics(val complexity: Int, val maxNesting: Int, val callSites: Int)

    private fun calculateMetrics(element: UElement): MethodMetrics {
        var complexity = 1
        var callSites = 0
        var currentNesting = 0
        var maxNesting = 0

        element.accept(object : AbstractUastVisitor() {
            override fun visitElement(node: UElement): Boolean {
                if (isComplexityPoint(node)) {
                    complexity++
                }
                
                if (isNestingPoint(node)) {
                    currentNesting++
                    if (currentNesting > maxNesting) maxNesting = currentNesting
                }
                
                if (node is UCallExpression) {
                    callSites++
                }
                return super.visitElement(node)
            }

            override fun afterVisitElement(node: UElement) {
                if (isNestingPoint(node)) {
                    currentNesting--
                }
                super.afterVisitElement(node)
            }
            
            private fun isComplexityPoint(node: UElement): Boolean = when (node) {
                is UIfExpression, is UWhileExpression, is UDoWhileExpression, 
                is UForExpression, is UForEachExpression, is USwitchClauseExpression,
                is UCatchClause -> true
                is UBinaryExpression -> {
                    val op = node.operator.text
                    op == "&&" || op == "||" || op == "?:"
                }
                is UPolyadicExpression -> {
                    val op = node.operator.text
                    op == "&&" || op == "||" || op == "?:"
                }
                else -> false
            }

            private fun isNestingPoint(node: UElement): Boolean = when (node) {
                is UIfExpression, is UWhileExpression, is UDoWhileExpression, 
                is UForExpression, is UForEachExpression, is USwitchClauseExpression,
                is UCatchClause -> true
                else -> false
            }
        })

        return MethodMetrics(complexity, maxNesting, callSites)
    }

    private fun report(context: JavaContext, node: UMethod, type: String, value: Int, level: String, limit: Int) {
        context.report(
            ISSUE,
            node,
            context.getLocation(node as UElement),
            "Method '${node.name}' violates $type Budget ($value). $level limit is $limit (LAW-028)."
        )
    }

    private fun JavaContext.getOption(issue: Issue, name: String, default: Int): Int {
        return configuration.getOption(issue, name)?.toIntOrNull() ?: default
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "SpaghettiMethodFatal",
            description = "Method violates complexity or length budget",
            rationale = "Complex methods are hard to test and maintain. Estatia enforces budgets for length, cyclomatic complexity, nesting depth, and fan-out.",
            badExample = "fun monster() { if(a) { while(b) { if(c) { ... } } } }",
            goodExample = "fun focused() { decomposeIntoSmallFunctions() }",
            category = IssueCategory.CODE_HEALTH,
            architectureLaw = Law.LAW_028,
            implementation = Implementation(Law028_SpaghettiMethodDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
