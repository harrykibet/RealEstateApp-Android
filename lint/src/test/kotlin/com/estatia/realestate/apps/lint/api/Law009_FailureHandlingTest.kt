package com.estatia.realestate.apps.lint.api

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class Law009_FailureHandlingTest {

    @Test
    fun `unwrapped return in repository reports error`() {
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
                        fun getData(): String = ""
                    }
                    """.trimIndent()
                )
            )
            .issues(Law009_ResultWrapperDetector.ISSUE)
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
                        fun <T> emptyList(): List<T> = TODO()
                    }
                    """.trimIndent()
                )
            )
            .issues(Law009_FailureSmugglingDetector.ISSUE)
            .run()
            .expectContains("Potential 'failure smuggling' detected in catch block")
    }

    @Test
    fun `dangerous fallback with elvis reports error`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        fun check(value: Boolean?) {
                            val data = value ?: false
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(Law009_DangerousFallbackDetector.ISSUE)
            .run()
            .expectContains("Dangerous fallback detected")
    }
}
