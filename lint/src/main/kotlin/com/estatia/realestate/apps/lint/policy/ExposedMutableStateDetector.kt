package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField

/**
 * LAW-002: Mutable state never crosses an ownership boundary.
 * Enforces that ViewModels, Repositories, and Services do not expose mutable state containers.
 */
class ExposedMutableStateDetector : Detector(), SourceCodeScanner {

    private val mutableContainers = setOf(
        "kotlinx.coroutines.flow.MutableStateFlow",
        "androidx.compose.runtime.MutableState"
    )

    private val targetAnnotations = setOf(
        "com.estatia.realestate.apps.core.common.annotations.ViewModelMarker",
        "com.estatia.realestate.apps.core.common.annotations.Repository",
        "com.estatia.realestate.apps.core.common.annotations.Service"
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UField::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val containingClass = node.containingClass ?: return
            
            val isTargetClass = context.evaluator.getAnnotations(containingClass, false)
                .any { targetAnnotations.contains(it.qualifiedName) }
            
            if (!isTargetClass) return

            if (context.evaluator.isPublic(node)) {
                val type = node.type
                val isMutable = mutableContainers.any { containerFqn ->
                    context.evaluator.inheritsFrom(context.evaluator.getTypeClass(type), containerFqn, false)
                }

                if (isMutable) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Exposing mutable state container '${node.name}' is forbidden (LAW-002). " +
                        "Mutable state must remain private. Expose as a read-only StateFlow instead."
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ExposedMutableState",
            description = "Mutable state container exposed publicly",
            rationale = "Exposing mutable containers allows external components to mutate internal state, breaking encapsulation.",
            badExample = "val uiState = MutableStateFlow(State())",
            goodExample = "private val _uiState = MutableStateFlow(State()); val uiState = _uiState.asStateFlow()",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = Law.LAW_002,
            implementation = Implementation(ExposedMutableStateDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
