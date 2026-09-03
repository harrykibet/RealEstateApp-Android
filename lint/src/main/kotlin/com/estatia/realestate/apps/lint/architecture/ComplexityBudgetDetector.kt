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
        val CLASS_SIZE_FATAL = EstatiaIssue.create("GodObjectFatal", "Class is too massive", "Refactor now.", IssueCategory.ARCHITECTURE, IssueTier.FATAL, RuleOwner.ARCHITECTURE, IMPLEMENTATION)
        val CLASS_SIZE_ERROR = EstatiaIssue.create("GodObjectError", "Class exceeds line budget", "Extract responsibilities.", IssueCategory.ARCHITECTURE, IssueTier.ERROR, RuleOwner.ARCHITECTURE, IMPLEMENTATION)
        val CLASS_SIZE_WARNING = EstatiaIssue.create("GodObjectWarning", "Class is growing large", "Monitor scope.", IssueCategory.ARCHITECTURE, IssueTier.WARNING, RuleOwner.ARCHITECTURE, IMPLEMENTATION)

        // Method Size
        val METHOD_SIZE_FATAL = EstatiaIssue.create("SpaghettiMethodFatal", "Method is too massive", "Refactor now.", IssueCategory.ARCHITECTURE, IssueTier.FATAL, RuleOwner.ARCHITECTURE, IMPLEMENTATION)
        val METHOD_SIZE_ERROR = EstatiaIssue.create("SpaghettiMethodError", "Method exceeds line budget", "Extract helpers.", IssueCategory.ARCHITECTURE, IssueTier.ERROR, RuleOwner.ARCHITECTURE, IMPLEMENTATION)
        val METHOD_SIZE_WARNING = EstatiaIssue.create("SpaghettiMethodWarning", "Method is growing long", "Improve readability.", IssueCategory.ARCHITECTURE, IssueTier.WARNING, RuleOwner.ARCHITECTURE, IMPLEMENTATION)

        // Parameters
        val PARAMETER_COUNT_ERROR = EstatiaIssue.create("TooManyParametersError", "Too many parameters", "Use data classes.", IssueCategory.API_DESIGN, IssueTier.ERROR, RuleOwner.ARCHITECTURE, IMPLEMENTATION)
        val PARAMETER_COUNT_WARNING = EstatiaIssue.create("TooManyParametersWarning", "High parameter count", "Monitor complexity.", IssueCategory.API_DESIGN, IssueTier.WARNING, RuleOwner.ARCHITECTURE, IMPLEMENTATION)

        // Constructor Dependencies (LAW-030)
        val CONSTRUCTOR_DEPENDENCY_ERROR = EstatiaIssue.create("OrchestrationMonsterError", "Constructor has too many dependencies", "Refactor orchestration.", IssueCategory.ARCHITECTURE, IssueTier.ERROR, RuleOwner.ARCHITECTURE, IMPLEMENTATION)
        val CONSTRUCTOR_DEPENDENCY_WARNING = EstatiaIssue.create("OrchestrationMonsterWarning", "High dependency count in constructor", "Monitor complexity.", IssueCategory.ARCHITECTURE, IssueTier.WARNING, RuleOwner.ARCHITECTURE, IMPLEMENTATION)
    }
}
