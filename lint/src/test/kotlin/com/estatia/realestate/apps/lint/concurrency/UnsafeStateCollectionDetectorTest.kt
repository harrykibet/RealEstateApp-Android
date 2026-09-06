package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class UnsafeStateCollectionDetectorTest {

    @Test
    fun `MutableStateFlow inside List reports error`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.FLOW,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.flow.MutableStateFlow
                    
                    class Test {
                        val states: List<MutableStateFlow<String>> = emptyList()
                    }
                    """.trimIndent()
                )
            )
            .issues(UnsafeStateCollectionDetector.ISSUE)
            .run()
            .expectContains("State containers inside collections detected")
    }

    @Test
    fun `MutableState inside Map reports error`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.COMPOSE,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import androidx.compose.runtime.MutableState
                    
                    class Test {
                        val stateMap: Map<String, MutableState<Int>> = emptyMap()
                    }
                    """.trimIndent()
                )
            )
            .issues(UnsafeStateCollectionDetector.ISSUE)
            .run()
            .expectContains("State containers inside collections detected")
    }

    @Test
    fun `plain list is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        val items: List<String> = emptyList()
                    }
                    """.trimIndent()
                )
            )
            .issues(UnsafeStateCollectionDetector.ISSUE)
            .run()
            .expectClean()
    }
}
