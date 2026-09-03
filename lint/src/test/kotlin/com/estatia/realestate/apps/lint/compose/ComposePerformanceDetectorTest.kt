package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import org.junit.Test

class ComposePerformanceDetectorTest {

    private val composableStub = kotlin(
        """
        package androidx.compose.runtime
        annotation class Composable
        """.trimIndent()
    )

    @Test
    fun `expensive object creation in composable reports warning`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                composableStub,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    import kotlin.text.Regex
                    @Composable
                    fun MyUI() {
                        val regex = Regex(".*")
                    }
                    """.trimIndent()
                )
            )
            .issues(ComposePerformanceDetector.EXPENSIVE_RECOMPOSITION_ISSUE)
            .run()
            .expectContains("Expensive object 'kotlin.text.Regex' created on every recomposition")
    }
}
