package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class SharedMutableStateDetectorTest {

    @Test
    fun `public mutable state flow should report error`() {
        lint()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.home
                    import kotlinx.coroutines.flow.MutableStateFlow
                    
                    class HomeViewModel {
                        val uiState = MutableStateFlow("Initial")
                    }
                    """.trimIndent()
                )
            )
            .issues(SharedMutableStateDetector.ISSUE)
            .run()
            .expect(
                """
                src/com/estatia/realestate/apps/feature/home/HomeViewModel.kt:5: Error: Publicly exposing mutable state ('kotlinx.coroutines.flow.MutableStateFlow') is forbidden. Expose the read-only interface (e.g., StateFlow) instead. [ExposedMutableState]
                    val uiState = MutableStateFlow("Initial")
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `private mutable state flow should not report error`() {
        lint()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.home
                    import kotlinx.coroutines.flow.MutableStateFlow
                    
                    class HomeViewModel {
                        private val _uiState = MutableStateFlow("Initial")
                    }
                    """.trimIndent()
                )
            )
            .issues(SharedMutableStateDetector.ISSUE)
            .run()
            .expectClean()
    }
}
