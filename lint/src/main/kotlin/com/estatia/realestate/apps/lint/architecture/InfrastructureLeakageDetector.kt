package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiClass
import org.jetbrains.uast.*

/**
 * Prevents infrastructure-specific classes (Retrofit, Room, Firebase) from leaking
 * into the Domain layer.
 */
class InfrastructureLeakageDetector : Detector(), SourceCodeScanner {

    private val forbiddenPackages = listOf(
        "retrofit2",
        "androidx.room",
        "com.google.firebase",
        "okhttp3",
        "com.amplifyframework"
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = 
        listOf(UCallExpression::class.java, UQualifiedReferenceExpression::class.java, UImportStatement::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitImportStatement(node: UImportStatement) {
            val path = node.importReference?.asRenderString() ?: return
            checkStringLeakage(context, node, path)
        }

        override fun visitCallExpression(node: UCallExpression) {
            val clazz = node.resolve()?.containingClass
            checkLeakage(context, node, clazz)
        }

        override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
            val resolved = node.resolve()
            if (resolved is PsiClass) {
                checkLeakage(context, node, resolved)
            }
        }
    }

    private fun checkLeakage(context: JavaContext, node: UElement, clazz: PsiClass?) {
        val qualifiedName = clazz?.qualifiedName ?: return
        checkStringLeakage(context, node, qualifiedName)
    }

    private fun checkStringLeakage(context: JavaContext, node: UElement, qualifiedName: String) {
        val path = context.file.path.replace("\\", "/")
        if (!path.contains("/domain/")) return

        if (forbiddenPackages.any { qualifiedName.startsWith(it) }) {
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Infrastructure leak: Class '$qualifiedName' is forbidden in the Domain layer (LAW-003)."
            )
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "InfrastructureLeakage",
            description = "Infrastructure-specific classes leaked into Domain layer",
            rationale = """
                The Domain layer must remain pure and agnostic of infrastructure details.
                Leaking framework details into Domain logic makes it impossible to test 
                without heavy mocks and couples business rules to implementation details.
            """,
            badExample = "class SaveUserUseCase(val db: RoomDatabase)",
            goodExample = "class SaveUserUseCase(val repository: UserRepository)",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-003 (Infrastructure Isolation)",
            implementation = Implementation(InfrastructureLeakageDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
