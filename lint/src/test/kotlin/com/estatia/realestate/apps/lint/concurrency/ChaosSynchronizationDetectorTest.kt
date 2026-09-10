package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class ChaosSynchronizationDetectorTest {

    @Test
    fun `plain var in ChaosController reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                Stubs.CHAOS,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import com.estatia.realestate.apps.core.testing.chaos.contracts.ChaosContract

                    class NetworkChaosController : ChaosContract() {
                        var isEnabled = false
                    }
                    """.trimIndent()
                )
            )
            .issues(ChaosSynchronizationDetector.ISSUE)
            .run()
            .expectContains("is a plain 'var'")
    }

    @Test
    fun `plain var in Fake infrastructure reports fatal`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                Stubs.CHAOS,
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import com.estatia.realestate.apps.core.testing.chaos.contracts.ChaosContract

                    class FakeAuthSource : ChaosContract() {
                        var currentUser = "none"
                    }
                    """.trimIndent()
                )
            )
            .issues(ChaosSynchronizationDetector.ISSUE)
            .run()
            .expectContains("Chaos/Fake component state 'currentUser' is a plain 'var'")
    }

    @Test
    fun `atomic state in ChaosController is clean`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    import java.util.concurrent.atomic.AtomicBoolean
                    
                    class NetworkChaosController {
                        val isEnabled = AtomicBoolean(false)
                    }
                    """.trimIndent()
                )
            )
            .issues(ChaosSynchronizationDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `static final constant is clean`() {
        lint()
            .testModes(TestMode.DEFAULT)
            .allowMissingSdk()
            .files(
                kotlin(
                    """
                    package com.estatia.realestate.apps
                    class FakeDataSource {
                        companion object {
                            const val TAG = "FAKE"
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(ChaosSynchronizationDetector.ISSUE)
            .run()
            .expectClean()
    }
}
