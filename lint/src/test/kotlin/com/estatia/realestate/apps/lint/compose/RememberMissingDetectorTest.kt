package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class RememberMissingDetectorTest {

    @Test
    fun `mutableStateOf without remember reports warning`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    import androidx.compose.runtime.mutableStateOf
                    
                    @Composable
                    fun MyUI() {
                        val state = mutableStateOf(0)
                    }
                    """.trimIndent()
                )
            )
            .issues(RememberMissingDetector.ISSUE)
            .run()
            .expectContains("is not wrapped in 'remember'")
    }

    @Test
    fun `mutableStateOf with remember is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    import androidx.compose.runtime.mutableStateOf
                    import androidx.compose.runtime.remember
                    
                    @Composable
                    fun MyUI() {
                        val state = remember { mutableStateOf(0) }
                    }
                    """.trimIndent()
                )
            )
            .issues(RememberMissingDetector.ISSUE)
            .run()
            .expectClean()
    }
}
