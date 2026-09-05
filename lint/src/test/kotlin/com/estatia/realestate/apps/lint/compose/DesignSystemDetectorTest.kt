package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class DesignSystemDetectorTest {

    @Test
    fun `Material 3 Button usage reports warning`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COMPOSE,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.material3.Button
                    import androidx.compose.runtime.Composable
                    
                    @Composable
                    fun Test() {
                        Button(onClick = {}) { }
                    }
                    """.trimIndent()
                )
            )
            .issues(DesignSystemDetector.ISSUE)
            .run()
            .expectContains("Using standard Material 3 Button. Use EstatiaButton instead")
    }

    @Test
    fun `Estatia design system usage is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COMPOSE,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import com.estatia.realestate.apps.core.designsystem.component.EstatiaButton
                    import androidx.compose.runtime.Composable
                    
                    @Composable
                    fun Test() {
                        EstatiaButton(onClick = {}) { }
                    }
                    """.trimIndent()
                )
            )
            .issues(DesignSystemDetector.ISSUE)
            .run()
            .expectClean()
    }
}
