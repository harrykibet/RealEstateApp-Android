package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.RuleOwner
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField

/**
 * LAW-002: Mutable state never crosses an ownership boundary.
 * Enforces that ViewModels, Repositories, UseCases, Managers and Services do not expose mutable state containers.
 */
class ExposedMutableStateDetector : Detector(), SourceCodeScanner {

    private val mutableContainers = setOf(
        "kotlinx.coroutines.flow.MutableStateFlow",
        "androidx.compose.runtime.MutableState"
    )

    private val targetAnnotations = setOf(
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.ViewModelMarker",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Repository",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Service",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.UseCase",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Manager"
    )

    private val targetSimpleNames = targetAnnotations.map { it.substringAfterLast(".") }.toSet()

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UField::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val containingClass = node.containingClass ?: return
            
            val annotations = context.evaluator.getAnnotations(containingClass, false)
            val hasTargetAnnotation = annotations.any { ann ->
                val qn = ann.qualifiedName ?: ""
                targetAnnotations.contains(qn) || targetSimpleNames.any { qn.endsWith(".$it") }
            }
            
            val isViewModel = context.evaluator.inheritsFrom(containingClass, "androidx.lifecycle.ViewModel", false)
            
            if (!hasTargetAnnotation && !isViewModel) return

            // 🛡️ REFINEMENT: In Kotlin, properties are fields + accessors. 
            // We want to catch exposed mutable containers regardless of backing field visibility
            // if the property itself is public.
            val isPublic = context.evaluator.isPublic(node) || 
                          (node.sourcePsi is KtProperty &&
                           !(node.sourcePsi as KtProperty).hasModifier(KtTokens.PRIVATE_KEYWORD))

            if (isPublic) {
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
            architectureLaw = Law.LAW_002,
            implementation = Implementation(ExposedMutableStateDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
