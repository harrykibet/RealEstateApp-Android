package com.estatia.realestate.apps.lint

import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UFile
import org.jetbrains.uast.UImportStatement

/**
 * Detector that prevents test-only libraries like MockK or Mockito from being 
 * used in production source code.
 */
class MockInProductionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UImportStatement::class.java)

    override fun createUastHandler(context: JavaContext) = object : com.android.tools.lint.client.api.UElementHandler() {
        override fun visitImportStatement(node: UImportStatement) {
            val importPath = node.importReference?.asRenderString() ?: return
            
            if (isMockLibrary(importPath)) {
                // Check if this is a production file (not in src/test or src/androidTest)
                val path = context.file.path.replace("\\", "/")
                if (!path.contains("/test/") && !path.contains("/androidTest/") && !path.contains("/testFixtures/")) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Test-only library '$importPath' used in production code. " +
                                "Mocking libraries should only be used in test sources."
                    )
                }
            }
        }
    }

    private fun isMockLibrary(path: String): Boolean {
        return path.startsWith("io.mockk") || 
               path.startsWith("org.mockito") || 
               path.startsWith("com.nhaarman.mockitokotlin2")
    }

    companion object {
        @JvmField
        val ISSUE: Issue = Issue.create(
            id = "MockInProduction",
            briefDescription = "Test-only library in Production",
            explanation = """
                Using mocking libraries (like MockK or Mockito) in production code increases 
                binary size, impacts performance, and is a major architectural smell. 
                These libraries are designed for test execution only.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(
                MockInProductionDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}
