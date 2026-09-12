package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.RuleOwner
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiNamedElement
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
                val typeClass = context.evaluator.getTypeClass(field.type)
                unsafeCollections.keys.any { unsafe -> 
                    context.evaluator.inheritsFrom(typeClass, unsafe, false)
                }
            }

            if (candidateUnsafeFields.isEmpty()) return

            // 1. var fields are always violations in shared components
            candidateUnsafeFields.filter { !it.hasModifierProperty(PsiModifier.FINAL) }.forEach { field ->
                reportViolation(context, field, field as UElement)
            }

            // 2. val fields are only violations if mutated post-init
            val candidateValFields = candidateUnsafeFields.filter { it.hasModifierProperty(PsiModifier.FINAL) }
            if (candidateValFields.isNotEmpty()) {
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
                        val resolvedReceiver = receiver?.tryResolve()
                        val field = if (resolvedReceiver != null) {
                            candidateValFields.find { 
                                it.javaPsi == resolvedReceiver || 
                                it.name == (resolvedReceiver as? PsiNamedElement)?.name
                            }
                        } else if (receiver != null) {
                            // 🛡️ REFINEMENT: Fallback for unresolved receivers. Strip parentheses for TestMode compatibility.
                            val receiverText = receiver.asRenderString()
                                .replace("(", "").replace(")", "")
                                .removePrefix("this.")
                            candidateValFields.find { it.name == receiverText }
                        } else null

                        if (field != null && !isInsideInitialization(mutationNode)) {
                            reportViolation(context, field, mutationNode)
                        }
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
                        var p = lambda.uastParent
                        if (p is UCallExpression) return p
                        // In some UAST versions, lambda is wrapped in an expression list
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
        }

        private fun reportViolation(context: JavaContext, field: UField, locationNode: UElement) {
            val typeClass = context.evaluator.getTypeClass(field.type)
            val unsafeType = typeClass?.qualifiedName ?: typeClass?.name ?: "Collection"
            val safeType = unsafeCollections[unsafeType] ?: "thread-safe alternative"

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
