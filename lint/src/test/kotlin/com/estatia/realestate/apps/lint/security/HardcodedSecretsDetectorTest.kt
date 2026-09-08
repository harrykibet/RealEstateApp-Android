package com.estatia.realestate.apps.lint.security

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.java
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
                java(
                    """
                    package com.estatia.realestate.apps;
                    public class Config {
                        public String apiKey = "12345-ABCDE";
                    }
                    """.trimIndent()
                )
            )
            .issues(HardcodedSecretsDetector.ISSUE)
            .run()
            .expectContains("Potential hardcoded secret detected")
    }

    @Test
    fun `long string with generic name is missed`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Config {
                        // This matches an AWS key pattern but has a generic name
                        val data = "AKIAIOSFODNN7EXAMPLE" 
                    }
                    """.trimIndent()
                )
            )
            .issues(HardcodedSecretsDetector.ISSUE)
            .run()
            .expectClean() // Current behavior: misses because 'data' is not a keyword
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
