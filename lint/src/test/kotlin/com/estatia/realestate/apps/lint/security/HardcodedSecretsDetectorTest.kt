package com.estatia.realestate.apps.lint.security

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class HardcodedSecretsDetectorTest {

    @Test
    fun `hardcoded api key reports fatal`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.ANDROID_APP,
                Stubs.VIEWMODEL,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Config {
                        private val apiKey = "12345-ABCDE"
                    }
                    """.trimIndent()
                )
            )
            .issues(HardcodedSecretsDetector.ISSUE)
            .run()
            .expectContains("Potential hardcoded secret detected")
    }

    @Test
    fun `non-secret string is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                Stubs.ANDROID_APP,
                Stubs.VIEWMODEL,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Config {
                        val title = "My App"
                    }
                    """.trimIndent()
                )
            )
            .issues(HardcodedSecretsDetector.ISSUE)
            .run()
            .expectClean()
    }
}
