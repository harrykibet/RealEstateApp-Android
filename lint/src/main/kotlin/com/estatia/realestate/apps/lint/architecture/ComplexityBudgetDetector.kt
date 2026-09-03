package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * Enforces LAW-028, LAW-029, and LAW-030: Complexity Budgets.
 * Prevents "God Objects", "Spaghetti Methods", and "Orchestration Monsters" 
 * by enforcing size and dependency thresholds.
 */
class ComplexityBudgetDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java, UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        
        override fun visitClass(node: UClass) {
            if (node is UAnonymousClass) return
            
            val lineCount = countLines(node)
            val element = node as UElement
            when {
                lineCount > 1000 -> context.report(CLASS_SIZE_FATAL, element, context.getLocation(element), "Class '${node.name}' is massive ($lineCount lines). It MUST be refactored.")
                lineCount > 600 -> context.report(CLASS_SIZE_ERROR, element, context.getLocation(element), "Class '${node.name}' exceeds the 600-line budget ($lineCount lines).")
                lineCount > 300 -> context.report(CLASS_SIZE_WARNING, element, context.getLocation(element), "Class '${node.name}' is growing large ($lineCount lines).")
            }
        }

        override fun visitMethod(node: UMethod) {
            val element = node as UElement
            val paramCount = node.uastParameters.size

            if (node.isConstructor) {
                // LAW-030: Dependency Budget
                when {
                    paramCount >= 9 -> context.report(CONSTRUCTOR_DEPENDENCY_ERROR, element, context.getLocation(element), "Constructor of '${node.containingClass?.name}' has too many dependencies ($paramCount). Refactor into smaller components.")
                    paramCount >= 6 -> context.report(CONSTRUCTOR_DEPENDENCY_WARNING, element, context.getLocation(element), "Constructor of '${node.containingClass?.name}' is becoming an orchestration monster ($paramCount dependencies).")
                }
                return
            }

            val lineCount = countLines(node)
            
            // LAW-028: Method size
            when {
                lineCount > 300 -> context.report(METHOD_SIZE_FATAL, element, context.getLocation(element), "Method '${node.name}' is too long ($lineCount lines).")
                lineCount > 120 -> context.report(METHOD_SIZE_ERROR, element, context.getLocation(element), "Method '${node.name}' exceeds the 120-line budget ($lineCount lines).")
                lineCount > 60 -> context.report(METHOD_SIZE_WARNING, element, context.getLocation(element), "Method '${node.name}' is reaching 60 lines.")
            }

            // General Parameter Count
            if (paramCount > 12) {
                context.report(PARAMETER_COUNT_ERROR, element, context.getLocation(element), "Method '${node.name}' has too many parameters ($paramCount).")
            } else if (paramCount > 7) {
                context.report(PARAMETER_COUNT_WARNING, element, context.getLocation(element), "Method '${node.name}' has many parameters ($paramCount).")
            }
        }
    }

    private fun countLines(node: UElement): Int {
        val text = node.sourcePsi?.text ?: return 0
        return text.lines().size
    }

    companion object {
        private val IMPLEMENTATION = Implementation(ComplexityBudgetDetector::class.java, Scope.JAVA_FILE_SCOPE)

        // Class Size
        val CLASS_SIZE_FATAL = EstatiaIssue.create(
            "GodObjectFatal", 
            "Class is too massive", 
            "Classes exceeding 1000 lines are unmaintainable God Objects.",
            "class Massive : ViewModel() { ... 1001 lines ... }",
            "Refactor into smaller, focused components.",
            IssueCategory.ARCHITECTURE, IssueTier.FATAL, RuleOwner.ARCHITECTURE, "LAW-029", IMPLEMENTATION
        )
        val CLASS_SIZE_ERROR = EstatiaIssue.create(
            "GodObjectError", 
            "Class exceeds line budget", 
            "Classes exceeding 600 lines must be refactored.",
            "class Large : ViewModel() { ... 601 lines ... }",
            "Extract responsibilities into separate classes.",
            IssueCategory.ARCHITECTURE, IssueTier.ERROR, RuleOwner.ARCHITECTURE, "LAW-029", IMPLEMENTATION
        )
        val CLASS_SIZE_WARNING = EstatiaIssue.create(
            "GodObjectWarning", 
            "Class is growing large", 
            "Classes over 300 lines are nearing the complexity limit.",
            "class Medium : ViewModel() { ... 301 lines ... }",
            "Monitor scope and consider extracting private logic.",
            IssueCategory.ARCHITECTURE, IssueTier.WARNING, RuleOwner.ARCHITECTURE, "LAW-029", IMPLEMENTATION
        )

        // Method Size
        val METHOD_SIZE_FATAL = EstatiaIssue.create(
            "SpaghettiMethodFatal", 
            "Method is too massive", 
            "Methods exceeding 300 lines are impossible to reason about.",
            "fun massive() { ... 301 lines ... }",
            "Extract private helper functions.",
            IssueCategory.ARCHITECTURE, IssueTier.FATAL, RuleOwner.ARCHITECTURE, "LAW-028", IMPLEMENTATION
        )
        val METHOD_SIZE_ERROR = EstatiaIssue.create(
            "SpaghettiMethodError", 
            "Method exceeds line budget", 
            "Methods exceeding 120 lines must be broken down.",
            "fun large() { ... 121 lines ... }",
            "Identify sub-tasks and extract them.",
            IssueCategory.ARCHITECTURE, IssueTier.ERROR, RuleOwner.ARCHITECTURE, "LAW-028", IMPLEMENTATION
        )
        val METHOD_SIZE_WARNING = EstatiaIssue.create(
            "SpaghettiMethodWarning", 
            "Method is growing long", 
            "Methods over 60 lines should be checked for readability.",
            "fun medium() { ... 61 lines ... }",
            "Improve readability by extracting helpers.",
            IssueCategory.ARCHITECTURE, IssueTier.WARNING, RuleOwner.ARCHITECTURE, "LAW-028", IMPLEMENTATION
        )

        // Parameters
        val PARAMETER_COUNT_ERROR = EstatiaIssue.create(
            "TooManyParametersError", 
            "Too many parameters", 
            "Methods with 12+ parameters are highly error-prone.",
            "fun do(p1, p2, ... p13)",
            "Group parameters into a data class.",
            IssueCategory.API_DESIGN, IssueTier.ERROR, RuleOwner.ARCHITECTURE, "LAW-028", IMPLEMENTATION
        )
        val PARAMETER_COUNT_WARNING = EstatiaIssue.create(
            "TooManyParametersWarning", 
            "High parameter count", 
            "Methods with 7+ parameters indicate missing abstractions.",
            "fun do(p1, p2, ... p8)",
            "Consider grouping related parameters.",
            IssueCategory.API_DESIGN, IssueTier.WARNING, RuleOwner.ARCHITECTURE, "LAW-028", IMPLEMENTATION
        )

        // Constructor Dependencies (LAW-030)
        val CONSTRUCTOR_DEPENDENCY_ERROR = EstatiaIssue.create(
            "OrchestrationMonsterError", 
            "Constructor has too many dependencies", 
            "Classes with 9+ dependencies indicate too many responsibilities.",
            "class Monster(d1, d2, ... d9)",
            "Decompose into smaller service components.",
            IssueCategory.ARCHITECTURE, IssueTier.ERROR, RuleOwner.ARCHITECTURE, "LAW-030", IMPLEMENTATION
        )
        val CONSTRUCTOR_DEPENDENCY_WARNING = EstatiaIssue.create(
            "OrchestrationMonsterWarning", 
            "High dependency count in constructor", 
            "Classes with 6+ dependencies are becoming hard to test.",
            "class Orchestrator(d1, d2, ... d6)",
            "Monitor component growth.",
            IssueCategory.ARCHITECTURE, IssueTier.WARNING, RuleOwner.ARCHITECTURE, "LAW-030", IMPLEMENTATION
        )
    }
}
