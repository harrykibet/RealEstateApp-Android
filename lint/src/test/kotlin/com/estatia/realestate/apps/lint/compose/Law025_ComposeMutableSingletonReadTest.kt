package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class Law025_ComposeMutableSingletonReadTest {

    @Test
    fun `reading var from singleton object in Composable reports error`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COMPOSE,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    
                    object Config {
                        @JvmField
                        var value = 0
                    }
                    
                    @Composable
                    fun MyUI() {
                        val v = Config.value
                    }
                    """.trimIndent()
                ).to("src/com/estatia/realestate/apps/Config.kt")
            )
            .issues(Law025_ComposeMutableSingletonReadDetector.ISSUE)
            .run()
            .expectContains("Reading mutable singleton state")
    }

    @Test
    fun `reading val from singleton object in Composable is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COMPOSE,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    
                    object Config {
                        val value = 0
                    }
                    
                    @Composable
                    fun MyUI() {
                        val v = Config.value
                    }
                    """.trimIndent()
                )
            )
            .issues(Law025_ComposeMutableSingletonReadDetector.ISSUE)
            .run()
            .expectClean()
    }
}
