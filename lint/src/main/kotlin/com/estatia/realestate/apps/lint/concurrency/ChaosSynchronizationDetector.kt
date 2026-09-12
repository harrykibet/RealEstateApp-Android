package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.RuleOwner
import com.intellij.psi.PsiModifierListOwner
import org.jetbrains.uast.*

/**
 * LAW-012: State Synchronization.
 * Ensures mutable state in Chaos Controllers and Fakes is synchronized to maintain test determinism.
 */
class ChaosSynchronizationDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            // 🛡️ SEMANTIC RESOLUTION: Rely on contract inheritance or specific annotations
            val isChaosComponent = context.evaluator.inheritsFrom(node, "com.estatia.realestate.apps.core.testing.chaos.contracts.ChaosContract", false) ||
                                   context.evaluator.getAnnotations(node.javaPsi, false).any { 
                                       val qn = it.qualifiedName ?: ""
                                       qn.contains("Chaos") || qn.contains("Mock") || qn.contains("Fake")
                                   }
            
            if (!isChaosComponent) return

            node.fields.forEach { field ->
                // Ignore final fields or constants
                if (!field.isFinal && !isConstant(context, field)) {
                    context.report(
                        ISSUE,
                        field,
                        context.getLocation(field),
                        "Chaos/Fake component state '${field.name}' is a plain 'var'. " +
                                "Test infrastructure MUST use AtomicReference or MutableStateFlow to ensure thread safety (LAW-012)."
                    )
                }
            }
        }
    }

    private fun isConstant(context: JavaContext, field: UField): Boolean {
        val psi = field.javaPsi as? PsiModifierListOwner
        return context.evaluator.isStatic(psi) && context.evaluator.isFinal(psi)
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "UnsynchronizedChaosState",
            description = "Unsynchronized Test Infrastructure State",
            rationale = "Chaos controllers and Fakes must be thread-safe to ensure tests are deterministic and free of races.",
            badExample = "var state = false",
            goodExample = "val state = AtomicBoolean(false)",
            category = IssueCategory.CONCURRENCY,
            architectureLaw = Law.LAW_012,
            implementation = Implementation(ChaosSynchronizationDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
