package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Detector that flags "Service Locator" anti-patterns like Firebase.getInstance() 
 * or Amplify.Auth, enforcing explicit dependency injection via Hilt.
 */
class ServiceLocatorDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames() = listOf("getInstance", "getClient")

    private val restrictedClasses = setOf(
        "com.google.firebase.firestore.FirebaseFirestore",
        "com.google.firebase.auth.FirebaseAuth",
        "com.google.firebase.storage.FirebaseStorage",
        "com.google.firebase.remoteconfig.FirebaseRemoteConfig"
    )

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        val className = method.containingClass?.qualifiedName ?: return
        
        if (restrictedClasses.contains(className)) {
            // Allow in Hilt modules or specialized initializers
            val path = context.file.path.replace("\\", "/")
            if (path.contains("/di/") || path.contains("Initializer")) return

            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Usage of '$className.getInstance()' is forbidden. " +
                        "Inject this dependency via Hilt constructor injection instead."
            )
        }
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "ServiceLocatorUsage",
            briefDescription = "Service Locator Pattern Usage",
            explanation = """
                Using static 'getInstance()' methods creates hidden dependencies and makes 
                unit testing difficult. This violates Estatia's explicit dependency standard.
                
                Always provide these instances via Hilt modules and inject them into constructors.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(
                ServiceLocatorDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
