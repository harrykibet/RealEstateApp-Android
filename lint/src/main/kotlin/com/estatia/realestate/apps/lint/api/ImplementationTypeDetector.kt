package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.core.architecture.RuleOwner
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiType
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * LAW-008: Abstraction Boundaries.
 * Ensures that public APIs of repositories and services do not expose infrastructure implementation types.
 */
class ImplementationTypeDetector : Detector(), SourceCodeScanner {

    private val targetAnnotations = setOf(
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Repository",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Service",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.UseCase",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.DataSource",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract"
    )

    private val targetSimpleNames = targetAnnotations.map { it.substringAfterLast(".") }.toSet()

    private val concreteCollections = setOf(
        "java.util.ArrayList", "java.util.HashMap", "java.util.HashSet",
        "java.util.LinkedHashMap", "java.util.LinkedHashSet", "java.util.TreeMap", "java.util.TreeSet"
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UMethod::class.java, UField::class.java, UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            // 🛡️ REFINEMENT: Recursively scan nested objects/classes inside governed components
            if (isInsideGovernedComponent(context, node)) {
                node.fields.filter { context.evaluator.isPublic(it) }.forEach { checkType(it.type, it, context) }
                node.methods.filter { context.evaluator.isPublic(it) && !it.isConstructor }.forEach { method ->
                    method.returnType?.let { checkType(it, method, context) }
                    method.uastParameters.forEach { checkType(it.type, it, context) }
                }
            }
        }

        override fun visitField(node: UField) {
            // Logic handled in visitClass for class members, but we need to satisfy the override requirement if registered.
            if (node.uastParent is UFile && context.evaluator.isPublic(node)) {
                // Handle top-level properties (rare in Android but possible)
                if (isInsideGovernedComponent(context, node)) {
                    checkType(node.type, node, context)
                }
            }
        }

        override fun visitMethod(node: UMethod) {
            if (node.isConstructor || !context.evaluator.isPublic(node)) return
            if (!isInsideGovernedComponent(context, node)) return

            // 1. Check Signature
            node.returnType?.let { checkType(it, node, context) }
            node.uastParameters.forEach { checkType(it.type, it, context) }
            
            // 2. 🕵️ ADVERSARIAL HARDENING: Check return expressions for erasure bypass
            node.accept(object : AbstractUastVisitor() {
                override fun visitReturnExpression(node: UReturnExpression): Boolean {
                    node.returnExpression?.getExpressionType()?.let { checkType(it, node, context) }
                    return super.visitReturnExpression(node)
                }
            })
        }

        private fun isInsideGovernedComponent(context: JavaContext, node: UElement): Boolean {
            val psiMember = (node as? UDeclaration)?.javaPsi as? PsiMember ?: return false
            
            // 1. Check if the containing class is governed
            val containingClass = psiMember.containingClass
            if (containingClass != null) {
                val annotations = context.evaluator.getAnnotations(containingClass, false)
                val isGoverned = annotations.any { ann ->
                    val qn = ann.qualifiedName ?: ""
                    targetAnnotations.contains(qn) || targetSimpleNames.any { qn.endsWith(".$it") }
                }
                if (isGoverned) return true
            }

            // 2. 🛡️ REFINEMENT: Check for Extension Functions targeting governed roles
            if (node is UMethod) {
                val receiverType = node.uastParameters.firstOrNull { it.name.startsWith("this") || it.name.startsWith("\$this") }?.type
                
                if (receiverType != null) {
                    val typeName = receiverType.canonicalText
                    val receiverClass = context.evaluator.getTypeClass(receiverType)
                    val annotations = if (receiverClass != null) {
                        context.evaluator.getAnnotations(receiverClass, false)
                    } else emptyList()

                    val isGovernedReceiver = annotations.any { ann ->
                        val qn = ann.qualifiedName ?: ""
                        targetAnnotations.contains(qn) || targetSimpleNames.any { qn.endsWith(".$it") }
                    } || targetSimpleNames.any { typeName.endsWith(".$it") || typeName == it }

                    if (isGovernedReceiver) return true
                }
            }
            
            return false
        }

        private fun checkType(type: PsiType, node: UElement, context: JavaContext) {
            val qualifiedName = type.canonicalText.substringBefore("<")
            
            // 1. Check Infrastructure Packages
            val isInfraLeak = ArchitecturalPolicy.Law003.InfrastructurePackages.any { qualifiedName.startsWith(it) }
            
            // 2. Check Concrete Collections
            val isConcreteCollection = concreteCollections.contains(qualifiedName)

            if (isInfraLeak || isConcreteCollection) {
                val typeCategory = if (isInfraLeak) "infrastructure type" else "implementation type"
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Public API exposes $typeCategory '$qualifiedName' (LAW-008). Use an abstraction instead."
                )
                return // Found at top level, no need to check generics
            }

            // 3. Recursive Check for Generics
            if (type is PsiClassType) {
                type.parameters.forEach { checkType(it, node, context) }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ImplementationTypeInPublicApi",
            description = "Infrastructure type exposed in public API",
            rationale = "Public interfaces should only depend on domain models and abstractions to prevent implementation leakage.",
            badExample = "fun getUser(): FirebaseUser",
            goodExample = "fun getUser(): UserDomainModel",
            category = IssueCategory.API_DESIGN,
            architectureLaw = Law.LAW_008,
            implementation = Implementation(ImplementationTypeDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
