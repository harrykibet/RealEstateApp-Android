package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class StateOwnershipDetectorTest {

    @Test
    fun `mutable state parameter in composable reports error`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    import androidx.compose.runtime.MutableState
                    
                    @Composable
                    fun MyUI(state: MutableState<String>) { }
                    """.trimIndent()
                )
            )
            .issues(StateOwnershipDetector.ISSUE)
            .run()
            .expectContains("is a mutable container")
    }

    @Test
    fun `immutable state parameter in composable is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    
                    @Composable
                    fun MyUI(text: String) { }
                    """.trimIndent()
                )
            )
            .issues(StateOwnershipDetector.ISSUE)
            .run()
            .expectClean()
    }
}
