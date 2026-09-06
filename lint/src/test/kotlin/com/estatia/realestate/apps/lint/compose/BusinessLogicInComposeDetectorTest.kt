package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class BusinessLogicInComposeDetectorTest {

    @Test
    fun `launching coroutine in Composable reports warning`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COMPOSE,
                Stubs.COROUTINES,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    import kotlinx.coroutines.CoroutineScope
                    import kotlinx.coroutines.launch
                    
                    @Composable
                    fun MyUI(scope: CoroutineScope) {
                        scope.launch { 
                            // Perform work
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(BusinessLogicInComposeDetector.ISSUE)
            .run()
            .expectContains("Avoid complex logic or side-effects like 'launch' directly in a Composable")
    }

    @Test
    fun `collecting flow in Composable reports warning`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COMPOSE,
                Stubs.FLOW,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.Composable
                    import kotlinx.coroutines.flow.Flow
                    import kotlinx.coroutines.flow.collect
                    
                    @Composable
                    fun MyUI(flow: Flow<String>) {
                        flow.collect { 
                            // Do something
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(BusinessLogicInComposeDetector.ISSUE)
            .run()
            .expectContains("Avoid complex logic or side-effects like 'collect' directly in a Composable")
    }

    @Test
    fun `pure UI in Composable is clean`() {
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
                    fun MyUI(text: String) {
                        // Text(text)
                    }
                    """.trimIndent()
                )
            )
            .issues(BusinessLogicInComposeDetector.ISSUE)
            .run()
            .expectClean()
    }
}
