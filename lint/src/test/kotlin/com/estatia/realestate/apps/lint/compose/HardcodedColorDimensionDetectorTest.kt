package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class HardcodedColorDimensionDetectorTest {

    @Test
    fun `hardcoded dp in feature module reports warning`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.home
                    import androidx.compose.ui.unit.dp
                    
                    fun test() {
                        val x = 16.dp
                    }
                    """.trimIndent()
                ).to("src/com/estatia/realestate/apps/feature/home/Test.kt")
            )
            .issues(HardcodedColorDimensionDetector.ISSUE)
            .run()
            .expectContains("Hardcoded color or dimension detected")
    }

    @Test
    fun `dp usage in designsystem module is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.designsystem
                    import androidx.compose.ui.unit.dp
                    
                    fun test() {
                        val x = 16.dp
                    }
                    """.trimIndent()
                ).to("src/com/estatia/realestate/apps/core/designsystem/Test.kt")
            )
            .issues(HardcodedColorDimensionDetector.ISSUE)
            .run()
            .expectClean()
    }
}
