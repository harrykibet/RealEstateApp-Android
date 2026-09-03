package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * Ensures mutable state in Chaos Controllers is synchronized to maintain test determinism.
 */
class ChaosSynchronizationDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            val className = node.name ?: return
            val qualifiedName = node.qualifiedName ?: ""
            
            val isChaosController = className.contains("Chaos") || qualifiedName.contains(".chaos.")
            if (!isChaosController) return

            node.fields.forEach { field ->
                if (!field.isFinal) {
                    context.report(
                        ISSUE,
                        field,
                        context.getLocation(field),
                        "Chaos controller state '${field.name}' is a plain 'var'. Use AtomicReference or MutableStateFlow to ensure determinism."
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "UnsynchronizedChaosState",
            description = "Unsynchronized Chaos Controller State",
            rationale = "Chaos controllers must be thread-safe for deterministic testing.",
            badExample = "var state = false",
            goodExample = "val state = AtomicBoolean(false)",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-012 (State Synchronization)",
            implementation = Implementation(ChaosSynchronizationDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
