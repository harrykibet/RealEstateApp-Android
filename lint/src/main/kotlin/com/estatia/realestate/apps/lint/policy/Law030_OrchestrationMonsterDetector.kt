package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

/**
 * LAW-030: Constructors must have a limited dependency budget.
 */
class Law030_OrchestrationMonsterDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            if (!node.isConstructor) return
            
            val paramsCount = node.uastParameters.size
            
            val errorThreshold = context.getOption(ISSUE, "errorThreshold", 9)
            val warningThreshold = context.getOption(ISSUE, "warningThreshold", 6)

            when {
                paramsCount >= errorThreshold -> {
                    context.report(ISSUE, node, context.getLocation(node as UElement),
                        "Orchestration Monster detected: Constructor has $paramsCount dependencies. ERROR limit is $errorThreshold (LAW-030).")
                }
                paramsCount >= warningThreshold -> {
                    context.report(ISSUE, node, context.getLocation(node as UElement),
                        "Constructor is accumulating too many dependencies ($paramsCount). WARNING limit is $warningThreshold (LAW-030).")
                }
            }
        }
    }

    private fun JavaContext.getOption(issue: Issue, name: String, default: Int): Int {
        return configuration.getOption(issue, name)?.toIntOrNull() ?: default
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "OrchestrationMonsterError",
            description = "Too many dependencies in constructor",
            rationale = "Classes with many dependencies are hard to test and reason about. Use Facades or decompose responsibilities.",
            badExample = "class Monster(d1: D, d2: D, d3: D, d4: D, d5: D, d6: D, d7: D, d8: D, d9: D)",
            goodExample = "class Focused(service: IService)",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-030",
            implementation = Implementation(Law030_OrchestrationMonsterDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
