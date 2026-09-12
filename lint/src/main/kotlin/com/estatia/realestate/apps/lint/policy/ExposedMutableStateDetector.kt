package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.RuleOwner
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

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

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UField::class.java, UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val containingClass = node.containingClass ?: return
            if (isGovernedComponent(containingClass)) {
                checkType(node, node.type, node.name)
            }
        }

        override fun visitMethod(node: UMethod) {
            if (node.isConstructor) return
            val containingClass = node.containingClass ?: return
            if (!isGovernedComponent(containingClass)) return

            val returnType = node.returnType ?: return
            val name = if (node.name.startsWith("get")) node.name.removePrefix("get").lowercase() else node.name
            checkType(node, returnType, name)
            
            // 🕵️ ADVERSARIAL HARDENING: Check return expressions for erasure bypass (casting to Any)
            node.accept(object : AbstractUastVisitor() {
                override fun visitReturnExpression(returnNode: UReturnExpression): Boolean {
                    returnNode.returnExpression?.getExpressionType()?.let { 
                        checkType(returnNode, it, "returned value") 
                    }
                    return super.visitReturnExpression(returnNode)
                }
            })
        }

        private fun isGovernedComponent(clazz: PsiClass): Boolean {
            val annotations = context.evaluator.getAnnotations(clazz, false)
            val hasTargetAnnotation = annotations.any { ann ->
                val qn = ann.qualifiedName ?: ""
                targetAnnotations.contains(qn) || targetSimpleNames.any { qn.endsWith(".$it") }
            }
            return hasTargetAnnotation || context.evaluator.inheritsFrom(clazz, "androidx.lifecycle.ViewModel", false)
        }

        private fun checkType(node: UElement, type: PsiType, name: String) {
            // Check visibility
            if (!isEffectivelyPublic(node)) return

            // 🛡️ REFINEMENT: Use canonicalText for strict FQN checking
            val canonicalText = type.canonicalText.substringBefore("<")
            
            val isOfficialMutable = canonicalText == "kotlinx.coroutines.flow.MutableStateFlow" || 
                                   canonicalText == "androidx.compose.runtime.MutableState"

            if (isOfficialMutable) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Exposing mutable state container '$name' is forbidden (LAW-002). " +
                    "Mutable state must remain private. Expose as a read-only StateFlow instead."
                )
            }
        }

        private fun isProjectClass(clazz: PsiClass): Boolean {
            val qn = clazz.qualifiedName ?: return false
            return qn.startsWith("com.estatia.realestate.apps")
        }

        private fun isEffectivelyPublic(node: UElement): Boolean {
            val declaration = node as? UDeclaration ?: return true
            if (context.evaluator.isPrivate(declaration) || context.evaluator.isInternal(declaration)) return false
            
            val source = declaration.sourcePsi
            if (source is KtProperty) {
                if (source.hasModifier(KtTokens.PRIVATE_KEYWORD) || 
                    source.hasModifier(KtTokens.INTERNAL_KEYWORD) ||
                    source.hasModifier(KtTokens.PROTECTED_KEYWORD)) return false
            }
            return context.evaluator.isPublic(declaration)
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
