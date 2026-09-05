package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class ChaosSynchronizationDetectorTest {

    @Test
    fun `chaos controller with plain var should report error`() {
        lint()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.testing.chaos.network
                    
                    class NetworkChaosController {
                        private var currentIndex = 0
                    }
                    """.trimIndent()
                )
            )
            .issues(ChaosSynchronizationDetector.ISSUE)
            .run()
            .expect(
                """
                src/com/estatia/realestate/apps/core/testing/chaos/network/NetworkChaosController.kt:4: Error: Chaos controller state 'currentIndex' is a plain 'var'. Use AtomicReference or MutableStateFlow to ensure determinism. [UnsynchronizedChaosState]
                    private var currentIndex = 0
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent()
            )
    }

    @Test
    fun `chaos controller with atomic val should not report error`() {
        lint()
            .allowMissingSdk()
            .files(
                Stubs.COROUTINES,
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.testing.chaos.network
                    import java.util.concurrent.atomic.AtomicInteger
                    
                    class NetworkChaosController {
                        private val currentIndex = AtomicInteger(0)
                    }
                    """.trimIndent()
                )
            )
            .issues(ChaosSynchronizationDetector.ISSUE)
            .run()
            .expectClean()
    }
}
