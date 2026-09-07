package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import com.intellij.psi.PsiModifierListOwner
import org.jetbrains.uast.*

/**
 * Detects usage of "Magic Numbers" in business logic.
 * Mandates extracting literals to named constants.
 */
class MagicNumberDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(ULiteralExpression::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitLiteralExpression(node: ULiteralExpression) {
            val value = node.value
            if (value is Number) {
                val allowedNumbers = getAllowedNumbers(context)
                if (!allowedNumbers.contains(value.toInt())) {
                    val parent = node.uastParent
                    // Check if it's an assignment to a constant
                    val isConstant = when (parent) {
                        is UVariable -> {
                            val psi = parent.javaPsi as? PsiModifierListOwner
                            context.evaluator.isStatic(psi) || context.evaluator.isFinal(psi)
                        }
                        else -> false
                    }
                    
                    if (!isConstant) {
                        context.report(
                            ISSUE,
                            node,
                            context.getLocation(node),
                            "Magic number '$value' detected. Extract this to a named constant to provide context."
                        )
                    }
                }
            }
        }
    }

    private fun getAllowedNumbers(context: JavaContext): Set<Int> {
        val default = setOf(0, 1, -1, 100, 10, 2, 24, 60, 1000)
        val override = context.configuration.getOption(ISSUE, "allowedNumbers")
        return override?.split(",")?.mapNotNull { it.trim().toIntOrNull() }?.toSet() ?: default
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MagicNumber",
            description = "Magic number detected in logic",
            rationale = "Extract literal numbers to named constants to improve readability.",
            badExample = "if (age > 21) { ... }",
            goodExample = "const val MIN_AGE = 21\nif (age > MIN_AGE) { ... }",
            category = IssueCategory.CODE_HEALTH,
            tier = IssueTier.CONVENTION,
            owner = RuleOwner.PRODUCT,
            architectureLaw = Law.LAW_001,
            implementation = Implementation(MagicNumberDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
