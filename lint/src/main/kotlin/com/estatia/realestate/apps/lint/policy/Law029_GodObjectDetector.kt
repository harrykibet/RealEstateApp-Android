package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

/**
 * LAW-029: Classes must have a single responsibility (Size Limit).
 */
class Law029_GodObjectDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            val source = node.asSourceString()
            val lineCount = source.lines().size
            
            val fatalThreshold = context.getOption(ISSUE, "fatalThreshold", 1000)
            val errorThreshold = context.getOption(ISSUE, "errorThreshold", 600)
            val warningThreshold = context.getOption(ISSUE, "warningThreshold", 300)

            when {
                lineCount > fatalThreshold -> {
                    context.report(ISSUE, node, context.getLocation(node as UElement),
                        "God Object detected: '${node.name}' has $lineCount lines. FATAL limit is $fatalThreshold (LAW-029).")
                }
                lineCount > errorThreshold -> {
                    context.report(ISSUE, node, context.getLocation(node as UElement),
                        "Class '${node.name}' exceeds the complexity budget ($lineCount lines). ERROR limit is $errorThreshold (LAW-029).")
                }
                lineCount > warningThreshold -> {
                    context.report(ISSUE, node, context.getLocation(node as UElement),
                        "Class '${node.name}' is becoming large ($lineCount lines). WARNING limit is $warningThreshold (LAW-029).")
                }
            }
        }
    }

    private fun JavaContext.getOption(issue: Issue, name: String, default: Int): Int {
        return configuration.getOption(issue, name)?.toIntOrNull() ?: default
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "GodObjectFatal",
            description = "Class violates size budget",
            rationale = "Large classes usually have too many responsibilities. Decompose into smaller components.",
            badExample = "class EverythingManager { ... 1000 lines ... }",
            goodExample = "class FocusedComponent { ... 200 lines ... }",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_029,
            implementation = Implementation(Law029_GodObjectDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
