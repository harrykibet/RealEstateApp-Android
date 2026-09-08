package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class Law009_FailureHandlingTest {

    @Test
    fun `unwrapped return in repository reports warning`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.RESULT,
                Stubs.FLOW,
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.data
                    class MyRepository {
                        // Non-trivial complex type requires wrapping under Estatia Convention
                        suspend fun getData(): List<String> = emptyList()
                    }
                    """.trimIndent()
                )
            )
            .issues(Law009_ResultWrapperDetector.ISSUE)
            .run()
            .expectContains("should return a wrapped Result type")
    }

    @Test
    fun `failure smuggling in catch block reports error`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.ANDROID_LOG,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun doWork(): List<String> {
                            try { work() } catch (e: Exception) { return emptyList() }
                            return emptyList()
                        }
                        fun work() {}
                        fun <T> emptyList(): List<T> = error("Stub")
                    }
                    """.trimIndent()
                )
            )
            .issues(Law009_FailureSmugglingDetector.ISSUE)
            .run()
            .expectContains("Potential 'failure smuggling' detected in catch block")
    }

    @Test
    fun `dangerous fallback with elvis reports warning`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun check(value: String?, flag: Boolean?, list: List<Int>?) {
                            val s = value ?: ""           // Dangerous: empty string
                            val b = flag ?: false         // Dangerous: false
                            val l = list ?: emptyList()    // Dangerous: empty list
                            val n = value ?: null          // Dangerous: null
                            
                            val safe = value ?: "default" // Safe: non-empty literal
                            val alsoSafe = value ?: compute() // Safe: dynamic computation
                        }
                        fun compute() = "data"
                        fun <T> emptyList(): List<T> = TODO()
                    }
                    """.trimIndent()
                )
            )
            .issues(Law009_DangerousFallbackDetector.ISSUE)
            .run()
            .expectContains("Dangerous fallback detected")
    }
}
