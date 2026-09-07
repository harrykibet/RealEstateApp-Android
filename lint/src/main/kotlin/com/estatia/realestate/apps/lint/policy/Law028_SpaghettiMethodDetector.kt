package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * LAW-028: Methods must be concise and focused (Complexity Budget).
 * 
 * Enforces two budgets:
 * 1. Length Budget: Max lines of code.
 * 2. Complexity Budget: Cyclomatic Complexity (number of decision branches).
 */
class Law028_SpaghettiMethodDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            if (node.isConstructor) return
            
            val body = node.uastBody ?: return
            
            // 1. Length Check
            val lineCount = body.asSourceString().lines().size
            val maxLinesFatal = context.getOption(ISSUE, "maxLinesFatal", 300)
            val maxLinesError = context.getOption(ISSUE, "maxLinesError", 120)
            val maxLinesWarning = context.getOption(ISSUE, "maxLinesWarning", 60)

            // 2. Complexity Check (Cyclomatic Complexity)
            val complexity = calculateComplexity(body)
            val maxComplexityFatal = context.getOption(ISSUE, "maxComplexityFatal", 30)
            val maxComplexityError = context.getOption(ISSUE, "maxComplexityError", 20)
            val maxComplexityWarning = context.getOption(ISSUE, "maxComplexityWarning", 10)

            when {
                complexity > maxComplexityFatal -> {
                    report(context, node, "Complexity", complexity, "FATAL", maxComplexityFatal)
                }
                lineCount > maxLinesFatal -> {
                    report(context, node, "Length", lineCount, "FATAL", maxLinesFatal)
                }
                complexity > maxComplexityError -> {
                    report(context, node, "Complexity", complexity, "ERROR", maxComplexityError)
                }
                lineCount > maxLinesError -> {
                    report(context, node, "Length", lineCount, "ERROR", maxLinesError)
                }
                complexity > maxComplexityWarning -> {
                    report(context, node, "Complexity", complexity, "WARNING", maxComplexityWarning)
                }
                lineCount > maxLinesWarning -> {
                    report(context, node, "Length", lineCount, "WARNING", maxLinesWarning)
                }
            }
        }
    }

    private fun calculateComplexity(element: UElement): Int {
        var branches = 1
        element.accept(object : AbstractUastVisitor() {
            override fun visitIfExpression(node: UIfExpression): Boolean {
                branches++
                return super.visitIfExpression(node)
            }

            override fun visitWhileExpression(node: UWhileExpression): Boolean {
                branches++
                return super.visitWhileExpression(node)
            }

            override fun visitDoWhileExpression(node: UDoWhileExpression): Boolean {
                branches++
                return super.visitDoWhileExpression(node)
            }

            override fun visitForExpression(node: UForExpression): Boolean {
                branches++
                return super.visitForExpression(node)
            }

            override fun visitForEachExpression(node: UForEachExpression): Boolean {
                branches++
                return super.visitForEachExpression(node)
            }

            override fun visitElement(node: UElement): Boolean {
                // Catch Kotlin 'when' branches and Java 'switch' labels generically
                if (node is USwitchClauseExpression) {
                    branches++
                }
                return super.visitElement(node)
            }

            override fun visitCatchClause(node: UCatchClause): Boolean {
                branches++
                return super.visitCatchClause(node)
            }

            override fun visitBinaryExpression(node: UBinaryExpression): Boolean {
                val operator = node.operator.text
                if (operator == "&&" || operator == "||" || operator == "?:") {
                    branches++
                }
                return super.visitBinaryExpression(node)
            }

            override fun visitPolyadicExpression(node: UPolyadicExpression): Boolean {
                val operator = node.operator.text
                if (operator == "&&" || operator == "||" || operator == "?:") {
                    branches += node.operands.size - 1
                }
                return super.visitPolyadicExpression(node)
            }
        })
        return branches
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
            rationale = "Complex and long methods are hard to test and maintain. " +
                        "Complexity is measured by decision points (if, for, when, etc.). " +
                        "Length is measured by total lines of code.",
            badExample = "fun monster() { if(a) { while(b) { if(c) { ... } } } }",
            goodExample = "fun focused() { decomposeIntoSmallFunctions() }",
            category = IssueCategory.CODE_HEALTH,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_028,
            implementation = Implementation(Law028_SpaghettiMethodDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
