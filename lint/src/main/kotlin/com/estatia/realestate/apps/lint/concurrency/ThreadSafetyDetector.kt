package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.RuleOwner
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiType
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * LAW-012: State Synchronization.
 * Detects usage of non-thread-safe collections or state in multi-threaded environments.
 */
class ThreadSafetyDetector : Detector(), SourceCodeScanner {

    private val unsafeCollections = mapOf(
        "java.util.HashMap" to "ConcurrentHashMap",
        "java.util.ArrayList" to "CopyOnWriteArrayList",
        "java.util.HashSet" to "ConcurrentHashMap.newKeySet()",
        "kotlin.collections.MutableList" to "CopyOnWriteArrayList",
        "kotlin.collections.MutableMap" to "ConcurrentHashMap",
        "kotlin.collections.MutableSet" to "ConcurrentHashMap.newKeySet()",
        "java.util.List" to "CopyOnWriteArrayList",
        "java.util.Map" to "ConcurrentHashMap",
        "java.util.Set" to "ConcurrentHashMap.newKeySet()"
    )

    private val mutatingMethods = setOf(
        "put", "putAll", "remove", "clear", "replace", "replaceAll", "compute", "merge", // Map
        "add", "addAll", "remove", "removeAll", "retainAll", "set", "plusAssign", "minusAssign" // Collection/List
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (node is UAnonymousClass) return
            if (!isAtRiskComponent(context, node)) return

            val candidateUnsafeFields = node.fields.filter { field ->
                isUnsafeType(field.type)
            }

            // 1. var fields are always violations in shared components
            candidateUnsafeFields.filter { !it.hasModifierProperty(PsiModifier.FINAL) }.forEach { field ->
                reportViolation(context, field.type, field as UElement)
            }

            // 2. 🕵️ DEEP SCAN: Track ALL mutations within the class
            val candidateValFields = candidateUnsafeFields.filter { it.hasModifierProperty(PsiModifier.FINAL) }
            
            node.accept(object : AbstractUastVisitor() {
                override fun visitCallExpression(node: UCallExpression): Boolean {
                    val methodName = node.methodName
                    if (mutatingMethods.contains(methodName)) {
                        checkAndReportMutation(node.receiver, node)
                        
                        // 🛡️ REFINEMENT: Handle implicit receivers (scope functions like apply/also)
                        if (node.receiver == null || node.receiver is UThisExpression) {
                            findScopeReceiver(node)?.let { checkAndReportMutation(it, node) }
                        }
                    }
                    return super.visitCallExpression(node)
                }

                override fun visitBinaryExpression(node: UBinaryExpression): Boolean {
                    val op = node.operator
                    if (op.text == "+=" || op.text == "-=") {
                        checkAndReportMutation(node.leftOperand, node)
                    }
                    return super.visitBinaryExpression(node)
                }

                private fun checkAndReportMutation(receiver: UExpression?, mutationNode: UElement) {
                    val type = receiver?.getExpressionType() ?: return
                    if (isUnsafeType(type)) {
                        val resolved = receiver.tryResolve()
                        
                        // 1. Check if it's a known field
                        val isField = candidateValFields.any { it.javaPsi == resolved } ||
                                     (receiver is UQualifiedReferenceExpression && receiver.receiver is UThisExpression) ||
                                     (resolved == null && candidateValFields.any { it.name == receiver.asRenderString() })

                        // 2. 🛡️ ADVERSARIAL HARDENING: Check local variable origins
                        var isDangerousLocal = false
                        if (resolved is ULocalVariable) {
                            val init = resolved.uastInitializer
                            // If it's initialized from a field, method call, reflection, or a cast from Any, it's dangerous
                            isDangerousLocal = init is UQualifiedReferenceExpression || 
                                              init is UCallExpression || 
                                              init is UBinaryExpression ||
                                              init?.asRenderString()?.contains("field.get") == true
                        }

                        // 3. Fallback for unresolved or complex receivers
                        val isUnresolvedDangerous = resolved == null && !isLocalScope(receiver)

                        if ((isField || isDangerousLocal || isUnresolvedDangerous) && !isInsideInitialization(mutationNode)) {
                            reportViolation(context, type, mutationNode)
                        }
                    }
                }
                
                private fun isLocalScope(node: UElement): Boolean {
                    // Check if the expression is a purely local variable (not a field)
                    val resolved = (node as? UExpression)?.tryResolve()
                    return resolved is ULocalVariable && resolved.uastInitializer is ULiteralExpression
                }

                private fun findScopeReceiver(node: UCallExpression): UExpression? {
                    var current: UElement? = node.uastParent
                    while (current != null && current !is UClass) {
                        if (current is ULambdaExpression) {
                            val call = findCallForLambda(current)
                            if (call != null && isScopeFunction(call.methodName)) {
                                return call.receiver
                            }
                        }
                        current = current.uastParent
                    }
                    return null
                }

                private fun findCallForLambda(lambda: ULambdaExpression): UCallExpression? {
                    val p = lambda.uastParent
                    if (p is UCallExpression) return p
                    return p?.uastParent as? UCallExpression
                }

                private fun isScopeFunction(name: String?): Boolean =
                    name == "apply" || name == "also" || name == "run" || name == "with"

                private fun isInsideInitialization(element: UElement): Boolean {
                    var current: UElement? = element
                    while (current != null) {
                        if (current is UMethod && current.isConstructor) return true
                        if (current is UClassInitializer) return true
                        current = current.uastParent
                    }
                    return false
                }
            })
        }

        private fun isUnsafeType(type: PsiType): Boolean {
            val typeClass = context.evaluator.getTypeClass(type) ?: return false
            return unsafeCollections.keys.any { unsafe -> 
                context.evaluator.inheritsFrom(typeClass, unsafe, false)
            }
        }

        private fun reportViolation(context: JavaContext, type: PsiType, locationNode: UElement) {
            val typeClass = context.evaluator.getTypeClass(type)
            val qualifiedName = typeClass?.qualifiedName ?: ""
            val unsafeType = typeClass?.name ?: "Collection"
            val safeType = unsafeCollections[qualifiedName] ?: unsafeCollections[unsafeType] ?: "thread-safe alternative"

            context.report(
                ISSUE,
                locationNode,
                context.getLocation(locationNode),
                "Unsafe collection '$unsafeType' mutated in a multi-threaded component. " +
                        "Use '$safeType' or wrap in a mutex (LAW-012)."
            )
        }
    }

    private fun isAtRiskComponent(context: JavaContext, node: UClass): Boolean {
        if (context.evaluator.inheritsFrom(node, "androidx.lifecycle.ViewModel", false)) return true

        val annotations = context.evaluator.getAnnotations(node.javaPsi, false)
        return annotations.any {
            val qn = it.qualifiedName ?: ""
            qn.contains("Singleton") || 
            qn.contains("com.estatia.realestate.apps.core.architecture.annotations.")
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ThreadSafetyViolation",
            description = "Non-thread-safe state in multi-threaded component",
            rationale = "Standard collections used in shared components lead to data races.",
            badExample = "val map = HashMap<String, String>()",
            goodExample = "val map = ConcurrentHashMap<String, String>()",
            category = IssueCategory.CONCURRENCY,
            architectureLaw = Law.LAW_012,
            implementation = Implementation(ThreadSafetyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
