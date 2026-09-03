package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class CoroutineCancellationDetectorTest {

    @Test
    fun `suspend loop missing cancellation check reports error`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class Test {
                        suspend fun loop() {
                            while(true) {
                                // work
                            }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(CoroutineCancellationDetector.ISSUE)
            .run()
            .expectContains("is missing a cancellation check")
    }

    @Test
    fun `suspend loop with isActive is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import kotlinx.coroutines.isActive
                    class Test {
                        suspend fun loop() {
                            while(isActive) {
                                // work
                            }
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(CoroutineCancellationDetector.ISSUE)
            .run()
            .expectClean()
    }
}
