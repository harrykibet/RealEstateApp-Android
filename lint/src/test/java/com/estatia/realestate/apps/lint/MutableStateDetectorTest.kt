package com.estatia.realestate.apps.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class MutableStateDetectorTest {

    @Test
    fun `exposed MutableStateFlow should report error`() {
        lint()
            .files(
                kotlin(
                    """
                    package com.estatia.test
                    import kotlinx.coroutines.flow.MutableStateFlow
                    
                    class TestViewModel {
                        val state = MutableStateFlow(0)
                    }
                    """.trimIndent()
                ),
                kotlin(
                    """
                    package kotlinx.coroutines.flow
                    class MutableStateFlow<T>(value: T)
                    """.trimIndent()
                )
            )
            .issues(MutableStateDetector.ISSUE)
            .run()
            .expect(
                """
                src/com/estatia/test/TestViewModel.kt:5: Error: Mutable state 'state' is exposed publicly. This violates Estatia's state ownership standards. Expose as an immutable type (e.g., StateFlow, List) instead. [ExposedMutableState]
                    val state = MutableStateFlow(0)
                    ~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `private MutableStateFlow should not report error`() {
        lint()
            .files(
                kotlin(
                    """
                    package com.estatia.test
                    import kotlinx.coroutines.flow.MutableStateFlow
                    
                    class TestViewModel {
                        private val _state = MutableStateFlow(0)
                    }
                    """.trimIndent()
                ),
                kotlin(
                    """
                    package kotlinx.coroutines.flow
                    class MutableStateFlow<T>(value: T)
                    """.trimIndent()
                )
            )
            .issues(MutableStateDetector.ISSUE)
            .run()
            .expectClean()
    }
}
