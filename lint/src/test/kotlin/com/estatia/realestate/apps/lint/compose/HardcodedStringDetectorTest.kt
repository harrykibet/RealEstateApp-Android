package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class HardcodedStringDetectorTest {

    @Test
    fun `hardcoded string in composable reports warning`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COMPOSE,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    
                    @Composable
                    fun Test() {
                        val text = "Hello"
                    }
                    """.trimIndent()
                )
            )
            .issues(HardcodedStringDetector.ISSUE)
            .run()
            .expectContains("Hardcoded string 'Hello' found in Composable")
    }

    @Test
    fun `empty string is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COMPOSE,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    
                    @Composable
                    fun Test() {
                        val text = ""
                    }
                    """.trimIndent()
                )
            )
            .issues(HardcodedStringDetector.ISSUE)
            .run()
            .expectClean()
    }
}
