package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class ErrorHandlingDetectorTest {

    @Test
    fun `unwrapped return in repository reports error`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.data
                    class MyRepository {
                        fun getData(): String = ""
                    }
                    """.trimIndent()
                )
            )
            .issues(ErrorHandlingDetector.MISSING_WRAPPER_ISSUE)
            .run()
            .expectContains("must return a wrapped Result type")
    }

    @Test
    fun `failure smuggling in catch block reports error`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun doWork() {
                            try { work() } catch (e: Exception) { return emptyList() }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(ErrorHandlingDetector.FAILURE_SMUGGLING_ISSUE)
            .run()
            .expectContains("Potential 'failure smuggling' detected in catch block")
    }

    @Test
    fun `dangerous fallback with elvis reports error`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class MyRepository { fun load(): List<String>? = null }
                    class Test(val repo: MyRepository) {
                        fun check() {
                            val data = repo.load() ?: emptyList()
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(ErrorHandlingDetector.DANGEROUS_FALLBACK_ISSUE)
            .run()
            .expectContains("Dangerous fallback detected")
    }
}
