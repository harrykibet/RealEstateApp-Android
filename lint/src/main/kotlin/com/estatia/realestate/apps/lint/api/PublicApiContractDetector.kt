package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiType
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField
import org.jetbrains.uast.UMethod

/**
 * Enforces LAW-008 and LAW-016: "Public APIs expose abstractions" and "Explicit state ownership".
 * Prevents leaking infrastructure, implementation-specific types, or mutable state containers 
 * through public members, and enforces the backing property convention.
 */
class PublicApiContractDetector : Detector(), SourceCodeScanner {

    private val mutableStateTypes = setOf(
        "kotlinx.coroutines.flow.MutableStateFlow",
        "kotlinx.coroutines.flow.MutableSharedFlow",
        "androidx.compose.runtime.MutableState"
    )

    private val forbiddenTypes = setOf(
        "kotlin.collections.MutableList",
        "kotlin.collections.MutableMap",
        "kotlin.collections.MutableSet",
        "java.util.ArrayList",
        "java.util.HashMap",
        "java.util.HashSet",
        "android.content.Context",
        "java.util.concurrent.Executor",
        "kotlinx.coroutines.CoroutineScope",
        "kotlinx.coroutines.Job",
        "kotlinx.coroutines.CoroutineDispatcher"
    )

    private val forbiddenPackagePrefixes = listOf(
        "com.google.firebase",
        "com.amplifyframework",
        "com.estatia.realestate.apps.core.network",
        "com.estatia.realestate.apps.core.database"
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UField::class.java, UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val typeText = node.type.canonicalText
            val shortTypeName = typeText.split("<").first()

            // 1. Enforce Backing Property Convention (LAW-016)
            if (mutableStateTypes.contains(shortTypeName)) {
                if (!context.evaluator.isPrivate(node)) {
                    context.report(
                        MUTABLE_STATE_ISSUE,
                        node,
                        context.getLocation(node),
                        "Mutable state field '${node.name}' must be private. Expose it via a read-only backing property instead (LAW-016)."
                    )
                }
                if (!node.name.startsWith("_")) {
                    context.report(
                        BACKING_PROPERTY_CONVENTION_ISSUE,
                        node,
                        context.getLocation(node),
                        "Mutable state field '${node.name}' should follow the backing-property convention and be prefixed with '_'."
                    )
                }
            } else if (context.evaluator.isPublic(node)) {
                // 2. Check general API contract rules for public fields
                checkType(context, node, node.type, "Field '${node.name}'")
            }
        }

        override fun visitMethod(node: UMethod) {
            if (context.evaluator.isPublic(node) && !node.isConstructor) {
                // Check Return Type
                node.returnType?.let { checkType(context, node, it, "Return type of '${node.name}'") }
                
                // Check Parameters
                node.uastParameters.forEach { param ->
                    checkType(context, param, param.type, "Parameter '${param.name}' of '${node.name}'")
                }
            }
        }
    }

    private fun checkType(context: JavaContext, node: UElement, type: PsiType, locationDescription: String) {
        val canonicalText = type.canonicalText
        val shortName = canonicalText.split("<").first()

        // 1. Check for specific forbidden types (Mutable Containers / Framework primitives)
        if (forbiddenTypes.contains(shortName) || mutableStateTypes.contains(shortName)) {
            context.report(
                MUTABLE_STATE_ISSUE,
                node,
                context.getLocation(node),
                "$locationDescription exposes a mutable container or framework primitive '$shortName'. Use a read-only abstraction instead."
            )
            return
        }

        // 2. Check for Infrastructure/Implementation Leakage (Firebase, DTOs, Entities)
        if (forbiddenPackagePrefixes.any { shortName.startsWith(it) }) {
            context.report(
                IMPLEMENTATION_LEAK_ISSUE,
                node,
                context.getLocation(node),
                "$locationDescription exposes an implementation-specific type '$shortName'. Use a Domain model or Result wrapper instead."
            )
        }
    }

    companion object {
        val MUTABLE_STATE_ISSUE = EstatiaIssue.create(
            id = "ExposedMutableState",
            description = "Mutable state or container exposed in public API",
            rationale = """
                Exposing mutable containers allows external components to modify 
                internal state, breaking encapsulation and UDF.
            """,
            badExample = "val state = MutableStateFlow(0)",
            goodExample = "private val _state = MutableStateFlow(0)\nval state: StateFlow<Int> = _state",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-016 (State Ownership)",
            implementation = Implementation(PublicApiContractDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val BACKING_PROPERTY_CONVENTION_ISSUE = EstatiaIssue.create(
            id = "BackingPropertyConvention",
            description = "Mutable state does not follow '_' prefix convention",
            rationale = "Differentiates internal mutable state from public read-only state.",
            badExample = "private val state = MutableStateFlow(0)",
            goodExample = "private val _state = MutableStateFlow(0)",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.WARNING,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-016 (State Ownership)",
            implementation = Implementation(PublicApiContractDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val IMPLEMENTATION_LEAK_ISSUE = EstatiaIssue.create(
            id = "ImplementationTypeInPublicApi",
            description = "Implementation type leaked in public API",
            rationale = """
                Public APIs must remain stable and agnostic of implementation details. 
                Leaking DTOs or SDK-specific types creates tight coupling.
            """,
            badExample = "fun getUser(): FirebaseUser",
            goodExample = "fun getUser(): User",
            category = IssueCategory.API_DESIGN,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-008 (Abstraction Boundaries)",
            implementation = Implementation(PublicApiContractDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
