package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import com.estatia.realestate.apps.lint.Stubs
import org.junit.Test

class ConfinementDetectorTest {

    @Test
    fun `critical method missing confinement check reports fatal`() {
        lint()
            .skipTestModes(TestMode.JVM_OVERLOADS)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.DAGGER_HILT,
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.player_engine
                    import javax.inject.Singleton
                    
                    @Singleton
                    class PlayerEngine {
                        fun play() {
                            // Missing checkConfinement()
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(ConfinementDetector.ISSUE)
            .run()
            .expectContains("is missing a thread-confinement check")
    }

    @Test
    fun `critical method with official confinement check is clean`() {
        lint()
            .skipTestModes(TestMode.JVM_OVERLOADS)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.DAGGER_HILT,
                Stubs.CONFINEMENT,
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.player_engine
                    import javax.inject.Singleton
                    import com.estatia.realestate.apps.core.common.concurrency.Confinement
                    
                    @Singleton
                    class PlayerEngine {
                        fun play() {
                            Confinement.checkMainThread()
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(ConfinementDetector.ISSUE)
            .run()
            .expectClean()
    }

    @Test
    fun `critical method with empty local check fails`() {
        lint()
            .skipTestModes(TestMode.JVM_OVERLOADS)
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.DAGGER_HILT,
                kotlin(
                    """
                    package com.estatia.realestate.apps.core.player_engine
                    import javax.inject.Singleton
                    
                    @Singleton
                    class PlayerEngine {
                        fun play() {
                            checkConfinement()
                        }
                        private fun checkConfinement() {
                            // Empty check that used to game the linter
                        }
                    }
                    """.trimIndent()
                )
            )
            .issues(ConfinementDetector.ISSUE)
            .run()
            .expectContains("is missing a thread-confinement check")
    }

    @Test
    fun `non-critical class is clean`() {
        lint()
            .allowCompilationErrors()
            .allowMissingSdk()
            .files(
                Stubs.DAGGER_HILT,
                kotlin(
                    """
                    package com.estatia.realestate.apps.feature.home
                    import javax.inject.Singleton
                    
                    @Singleton
                    class HomeUI {
                        fun show() {}
                    }
                    """.trimIndent()
                )
            )
            .issues(ConfinementDetector.ISSUE)
            .run()
            .expectClean()
    }
}
